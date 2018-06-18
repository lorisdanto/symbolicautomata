/**
 * MAT* (SFA learning algorithm) implementation.
 *  More details about the algorithm can be found in the paper 
 *  	"The Learnability of Symbolic Automata" by George Argyros and Loris D'Antoni, CAV 2018. 
 *  
 * @author George Argyros
 */


package algebralearning.sfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import algebralearning.AlgebraLearner;
import algebralearning.AlgebraLearnerFactory;
import algebralearning.oracles.EquivalenceOracle;
import algebralearning.oracles.MembershipOracle;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;
import utilities.Pair;

/**
 * The MAT* algorithm relies in underlying boolean algebra learning algorithms in order to infer the 
 * predicates of the target SFA. This class provides an interface in order to allow the implementation
 * of a membership oracle for the underlying predicate learners. 
 *
 * @param <D> The domain of the boolean algebra of the target SFA.
 */
class BALearnerSimulatedMembershipOracle <D> extends MembershipOracle <D> {
	
	private DiscriminationTree <D> tree;
	private List<D> src;
	private List<D> target;
	private List<D> missingLeaf;
	private boolean foundMissingLeaf;
	
	public BALearnerSimulatedMembershipOracle(DiscriminationTree <D>t, List <D> s, List<D> trg) {
		tree = t;
		src = new ArrayList <D>(s);
		target = new ArrayList <D>(trg);
		foundMissingLeaf = false;
		missingLeaf = null;		
	}
	
	public boolean query(D symbol) throws TimeoutException {
		List <D> total = new ArrayList <D>(src);
		List <D> leaf;
		total.add(symbol);
		
		leaf = tree.sift(total);		
		if (leaf == null) { 
			tree.fixMissingAccessSequence(total);
			foundMissingLeaf = true;
			missingLeaf = total;
			return false;
		}		
		if (leaf.equals(target)) {
			return true;
		}
		return false;
	}
	
	/**
	 * The discrimination tree when initialized will have only one child from it's root node. When we
	 * discover a string accessing the other leaf we save it and set the foundMissingLeaf to true. See
	 * the paper for more details.
	 * 
	 * @return whether the missing leaf was found during the execution of any membership query.
	 */
	public boolean foundMissingLeaf() { 
		
		return foundMissingLeaf;
	}
	
	/**
	 * See also the foundMissingLeaf() documentation.
	 * 
	 * @return an input accessing the missing leaf in the discrimination tree.
	 */
	public List <D> getMissingLeaf() {
		
		if (!foundMissingLeaf) {
			throw new AssertionError("getMissingLeaf invalid call");
		}		
		return missingLeaf; 
	}
}


public class SFAAlgebraLearner <P,D> extends AlgebraLearner <SFA <P,D>, List <D>> {

    private DiscriminationTree <D> tree;
    private MembershipOracle <List <D>> membOracle;
    private HashMap <Pair<Integer,Integer>, AlgebraLearner <P,D>> algebraLearners;
    private HashMap <Pair<Integer, Integer>, P> modelGuards;
    private SFA <P,D> model;
    private Boolean guardsInitialized;
    private BooleanAlgebra <P,D> ba;
    private AlgebraLearnerFactory <P,D> baLearnerFactory;
	private Hashtable <String, Integer> perfCounters;
	private Boolean repairedMissingLeaf; 

	
    public SFAAlgebraLearner(MembershipOracle <List<D>>m, BooleanAlgebra <P,D>b, 
    		AlgebraLearnerFactory <P,D> balf) {
    		membOracle = m;
    		model = null;
    		algebraLearners = new HashMap <>();
    		modelGuards = new HashMap<>();
    		ba = b;
    		guardsInitialized = false;
    		baLearnerFactory = balf;
    		perfCounters = new Hashtable <>();
    		repairedMissingLeaf = false;
    		
    		// Initialize the performance counters we keep
    		perfCounters.put("CEGuardUpdates", 0);
    		perfCounters.put("CEStateUpdates", 0);
    		perfCounters.put("CEDet", 0);
    		perfCounters.put("CEComp", 0);
    }	
    
    private void incPerfCounter(String key) {
    		if (!perfCounters.containsKey(key)) {
    			throw new AssertionError("Invalid performance counter requested");
    		}
    		perfCounters.put(key, perfCounters.get(key) + 1);
    		return;
    }
    
    /*********************** Model construction    ************************/
    
    private SFA <P,D> copyModelClean() throws TimeoutException {    		    	
    		return SFA.MkSFA(model.getTransitions(), model.getInitialState(), model.getFinalStates(), ba);    	
    }
    
    /**
     * Check that the guard set of outgoing transitions from srcState form a complete partition 
     * of the alphabet otherwise provide the necessary counterexamples to the underlying 
     * predicate learners.
     * 
     * @param srcState the id of the state to be checked.
     * @param srcAs the access string for srcState.
     * @return true if the guards where updated, false otherwise.
     * @throws TimeoutException
     */
    private boolean makeGuardsComplete(Integer srcState, List <D>srcAs) throws TimeoutException {
    		
    		// Create a the OR of the predicates and check if it is satisfiable, 
    		// negate it and check SAT. If SAT sat then we have a transition which is not there.
    		P guardOrPredicate, guard, pnot;
    		D witness;
    		HashSet <P> guardSet = new HashSet <>();
    		boolean updated = false;
    		Pair <Integer, Integer> stPair;
    		Integer ceTargetStateId, totalStates = tree.getLeafs().size();
    		boolean wasFixedBefore = false;

    		AlgebraLearner<P, D> guardLearner;
    		List <D> ceTargetAs;
     		
    		while (true) {
        		List <D> counterexample = new LinkedList <>(srcAs);
    			for (Integer trgState = 0; trgState < totalStates; trgState ++) {
    				stPair = new Pair <>(srcState, trgState);
    				if (modelGuards.get(stPair) == null) {
    					throw new AssertionError("null guard found to " + trgState);
    				}
    				guardSet.add(modelGuards.get(stPair));  					
    			}		    			    			
    			guardOrPredicate = ba.MkOr(guardSet);    			
    			pnot = ba.MkNot(guardOrPredicate);
    			if (!ba.IsSatisfiable(pnot)) {
    				return updated;
    			}
    			witness = ba.generateWitness(pnot);
    			incPerfCounter("CEComp");
    			updated = true;
    			// We have a counterexample: Find the wrong predicate(s) and update them.
    			counterexample.add(witness);
    			ceTargetAs = tree.sift(counterexample);
    			if (ceTargetAs == null) {
    				tree.fixMissingAccessSequence(counterexample);
    				repairedMissingLeaf = true;
    				return false;
    			}
    			ceTargetStateId = tree.getLeafs().indexOf(ceTargetAs);    	
			stPair = new Pair <>(srcState, ceTargetStateId);
			guard = modelGuards.get(stPair);			
			guardLearner = algebraLearners.get(stPair);    						
			wasFixedBefore = tree.isTreeComplete();
			guard = guardLearner.updateModel(witness);			
			modelGuards.put(stPair, guard);			
			if (!wasFixedBefore && tree.isTreeComplete()) {
				repairedMissingLeaf = true;
				return false;
			}			
    		} // End while
    		// Never reached    	
    }
    
    /**
     * Checks whether the set of outgoing transitions from state srcState is deterministic and 
     * in the opposite case provides the appropriate counterexamples to the underlying algebra 
     * learning instances.
     *
     * @param srcState the id of the state to be checked.
     * @param srcAs the access string for the srcState.
     * @return true if any guard was updated, false otherwise.
     * @throws TimeoutException
     */
    private boolean makeGuardsDeterministic(Integer srcState, List <D>srcAs) 
    		throws TimeoutException {      
    		Integer ceTargetState, totalStates = tree.getLeafs().size();
    		D witness;
    		boolean updatedOverall = false, updatedRound;
    		List <D> counterexample = new LinkedList <>(srcAs);
    		AlgebraLearner<P, D> learner;
    		List <Pair<Integer, P>> candidates;
    		P g1, g2, newGuard, pand;
    		
    		while (true) {
    			updatedRound = false;
	    		for (Integer t1 = 0; t1 < totalStates; t1 ++) {
	    			for (Integer t2 = 0; t2 < totalStates; t2 ++) {
	    				if (t1.equals(t2)) {
	    					continue;
	    				}
	    				g1 = modelGuards.get(new Pair <>(srcState, t1));
	    				g2 = modelGuards.get(new Pair <>(srcState, t2));	    				
	    				pand = ba.MkAnd(g1,g2);
	    				if (!ba.IsSatisfiable(pand)) {
	    					continue;
	    				}
	    				witness = ba.generateWitness(pand);
	    				updatedOverall = updatedRound = true;
	    				/* We have a counterexample */	    				
	        			counterexample.add(witness);
	        			if (tree.sift(counterexample) == null) {
	        				tree.fixMissingAccessSequence(counterexample);
	        				repairedMissingLeaf = true;
	        				return false;
	        			}
	        			ceTargetState = tree.getLeafs().indexOf(tree.sift(counterexample));
	        			incPerfCounter("CEDet");
	        			/* Add both on a list so we won't be repeating code blocks */
	        			candidates = new LinkedList <Pair<Integer, P>>();
	        			candidates.add(new Pair<>(t1, g1));
	        			candidates.add(new Pair<>(t2, g2));
	        			for (Pair <Integer, P> gtp : candidates) {	        			
	        				if ((ceTargetState.equals(gtp.getFirst())) != 
	        						ba.HasModel(gtp.getSecond(), witness)) {	        					
	        					learner = algebraLearners.get(new Pair<>(srcState, gtp.getFirst()));
	        					newGuard = learner.updateModel(witness);
	        					modelGuards.put(new Pair<>(srcState, gtp.getFirst()), newGuard);
	        				}
	        			}
	        			counterexample.remove(counterexample.size()-1);	        				
	    			} // Internal For
	    		} // External for
	    		if (!updatedRound) {
	    			return updatedOverall;
	    		}
    		} // End While
    		// Never reached
    }

    /**
     * Repeatedly call the makeGuardsComplete and makeGuardsDeterministic method until convergence.
     * 
     * @param srcStateId the id of the state for which the guard set is to be tested.
     * @param srcAs the access string for srcStateId.
     * @throws TimeoutException
     */
    private void makeCompleteAndDeterministic(Integer srcStateId, List <D> srcAs) 
    		throws TimeoutException {
    		Boolean notComplete, notDeterministic;
    		notComplete = false;
    		notDeterministic = false;
    		do {
    			notComplete = makeGuardsComplete(srcStateId, srcAs);
    			if (repairedMissingLeaf) {
    				return;
    			}
    			notDeterministic = makeGuardsDeterministic(srcStateId, srcAs);    	    			
    		} while (notComplete || notDeterministic);	
    }
    
    /**
     * Construct the guards of the SFA model by spawning one predicate learning algorithm instance
     * for each pair of states and inferring the guard (or the lack of any guard) between this pair. 
     * 
     * @throws TimeoutException
     */
    private void constructGuards() throws TimeoutException {
    		Integer totalStates = tree.getLeafs().size();
		List <D> srcAs = null;
		List <D> trgAs = null;
		boolean changed;
		
    		for (Integer srcState = 0; srcState < totalStates; srcState ++) {    			
    			changed = false;
    			for (Integer trgState = 0; trgState < totalStates; trgState ++) {    	
        			// Skip states for which the guards are constructed and no update is needed        			 
    				if (modelGuards.containsKey(new Pair<Integer, Integer>(srcState, trgState))) {
    					continue;
    				}
    				changed = true;    				
    				Pair <Integer, Integer> stPair = new Pair <>(srcState, trgState);
    				srcAs = tree.getLeafs().get(srcState);
    				trgAs = tree.getLeafs().get(trgState);
    				BALearnerSimulatedMembershipOracle <D> baMembOracle = 
    						new BALearnerSimulatedMembershipOracle <>(tree, srcAs, trgAs);
    				AlgebraLearner <P,D> guardLearner = 
    						baLearnerFactory.getBALearner(baMembOracle); 
    				P guard;
    				
    				// Everything is set up: learn guard predicate 
    				guard = guardLearner.getModel();    				
    				// Save everything 
    				algebraLearners.put(stPair, guardLearner);
    				if (guard == null) {
    					throw new AssertionError("An empty (null) guard was added");
    				}
    				modelGuards.put(stPair, guard);
    				if (baMembOracle.foundMissingLeaf()) {
    					repairedMissingLeaf = true;
    					return;
    				}
    			}	
    			// make this check only if some guard was updated 
    			if (changed) {
    				makeCompleteAndDeterministic(srcState, srcAs);
    			}
    		}    		
        return;
    }

	/**
	 * Construct the SFA model by spawning the learning algorithms for the underlying predicates.
	 * 
	 * @return the SFA model based on the discrimination tree built so far.
	 * @throws TimeoutException
	 */
    private SFA<P,D> constructModel() throws TimeoutException {    	
    		// constructGuards and then use the tree to generate the SFA.
    		if (!guardsInitialized) {
    			/* 
    			 * This may require two calls in case we fix the missing 
    			 * leaf in the discrimination tree. 
    			 */    			
    			constructGuards();
    			if (repairedMissingLeaf) {
    				constructGuards();
    				repairedMissingLeaf = false;
    			}
    		}		
    		// Build Transitions 
    		List <SFAMove <P,D>> transitions = new LinkedList <>();
    		for (HashMap.Entry<Pair<Integer, Integer>, P> entry : modelGuards.entrySet()) {    			
    			Pair <Integer, Integer> stPair = entry.getKey();
    			P guard = entry.getValue();
    			// Do not add empty transitions in the SFA model 
    			if (ba.AreEquivalent(guard, ba.False())) {
    				continue;
    			}
    			transitions.add(new SFAInputMove <P,D>(stPair.getFirst(), stPair.getSecond(), guard));    			    			
    		}
    		// Determine Final States 
    		List <Integer> finalStates = new LinkedList <Integer>();
    		Integer stateId = 0;
    		for (List <D>accessString : tree.getLeafs()) {    			
    			if (membOracle.query(accessString)) {
    				finalStates.add(stateId);
    			}
    			stateId ++;
    		}
    		// Construct the final SFA model and return a fresh copy back to the caller.
    		model = SFA.MkSFA(transitions, 0, finalStates, ba, false, false, true);
        return copyModelClean();
    }

          
    /******************* Counterexample Processing  ********************/
    
    /**
     * Run the provided input in the SFA model and return the identifier of the state in which
     * the input ends up. 
     * 
     * @param inp The input to run in the model.
     * @return The id of the state where the input ends up in the model.
     * @throws TimeoutException
     */
    private Integer modelGetTargetState(List <D> inp) throws TimeoutException {
        Integer currentState = 0;
        boolean found;
        for (D currentSymbol : inp) {
        		found = false;
            for (SFAInputMove<P, D> ct : model.getInputMovesFrom(currentState)) {
                if (ct.hasModel(currentSymbol, ba)) {                	
                    currentState = ct.to;
                    found = true;
                    break;
                }                            
            }
            if (!found) {
            		throw new AssertionError("Incomplete model in counterexample processing");
            }            
        }
        return currentState;
    }
    
    /**
     * Given the breakpoint where the execution of the target and the model diverge, distinguish 
     * between a counterexample due to a hidden state in the target or a counterexample due to
     * an invalid predicate and handle the counterexample accordingly.
     * 
     * @param ce the provided counterexample
     * @param index the index of the breakpoint in the counterexample.
     * @throws TimeoutException
     */    
    private void analyzeBreakpoint(List <D> ce, int index) throws TimeoutException {    		
    		// index here should be the index such that ce.subList(0, index) is accessing the invalid state    		 
    		List <D> srcAs, trgAs, newAs, newDist, trgPrefix;
    		Integer srcStateId, trgStateId, modelTrgStateId;
    		P newGuard, guard; 
    		
    		//srcAs = tree.sift(ce.subList(0, index));    		
    		srcAs = tree.getLeafs().get(modelGetTargetState(new LinkedList<D>(ce.subList(0, index))));    		
    		trgPrefix = new LinkedList <D>(srcAs);
    		trgPrefix.add(ce.get(index));
    		if ((trgAs = tree.sift(trgPrefix)) == null) {
    			/* 
    			 * The undiscovered new state is the missing leaf from the tree:
    			 * We fix the missing leaf and return. This is the only case where no
    			 * state splitting nor guard repairing is taking place.
    			 */
    			newAs = new LinkedList <D>(srcAs);
    			newAs.add(ce.get(index));
    			tree.fixMissingAccessSequence(newAs);
    			return;    			
    		}
    		srcStateId = tree.getLeafs().indexOf(srcAs);
    		trgStateId = tree.getLeafs().indexOf(trgAs);
    		modelTrgStateId = modelGetTargetState(new LinkedList<D>(ce.subList(0, index+1)));    		
    		guard = modelGuards.get(new Pair<Integer, Integer>(srcStateId, trgStateId));
    		if (ba.HasModel(guard, ce.get(index))) {
    			// Guard is correct, we have an undiscovered state 
    			incPerfCounter("CEStateUpdates");
    			newDist = new LinkedList<D>(ce.subList(index+1, ce.size()));
    			newAs = new LinkedList <D>(srcAs);
    			newAs.add(ce.get(index));   
    			tree.splitLeaf(trgAs, newDist, newAs);
    			// Through away all learning instances that were directed to the old state that was split up 
    			for (Integer sid = 0; sid < tree.getLeafs().size(); sid ++) {
    				modelGuards.remove(new Pair<Integer, Integer>(sid, trgStateId));
    			}    			    			
    			return;
    		}     		
    		/* 
    		 * We have an invalid guard predicate:
    		 * Update both guards, the one that should take the transition and didn't and the 
    		 * one that did take it but shouldn't, make the guard set complete and deterministic
    		 * and make a new equivalence query.
    		 */
    		incPerfCounter("CEGuardUpdates");
    		newGuard = algebraLearners.get(new Pair<Integer, Integer>(srcStateId, trgStateId)).
    				updateModel(ce.get(index));
    		modelGuards.put(new Pair<Integer, Integer>(srcStateId, trgStateId), newGuard);
    		newGuard = algebraLearners.get(new Pair<Integer, Integer>(srcStateId, modelTrgStateId)).
    				updateModel(ce.get(index));
    		modelGuards.put(new Pair<Integer, Integer>(srcStateId, modelTrgStateId), newGuard);
    		makeCompleteAndDeterministic(srcStateId, srcAs);
        return;
    }

    /**
     * Process a counterexample to the current model and either add a node in the discrimination tree
     * or provide a counterexample to one of the underlying algebra learners. The counterexample processing
     * algorithm is using binary search in order to find the breakpoint where the model and the target 
     * diverge. 
     * 
     * @param ce the counterexample to the current model
     * @throws TimeoutException
     */
    private void proccessCounterexample(List <D> ce) throws TimeoutException {    		
    		List <D>prefix = new LinkedList <>();
    		boolean ceOut = membOracle.query(ce);
    		Integer index = 0, curState;
    		List <D> curAs;
    		List <D> curQuery;
    		
    		Integer correctIdx, incorrectIdx, high, low;
    		correctIdx = -1;
    		incorrectIdx = ce.size();
    		low = 0;
    		high = ce.size() - 1;
    		while (incorrectIdx != correctIdx+1) {
        		index = (low + high)/2;        		
    			prefix = new LinkedList <D>(ce.subList(0, index+1));    			    		
    			curState = modelGetTargetState(prefix);
    			curAs = tree.getLeafs().get(curState);    			
    			curQuery = new LinkedList <D>(curAs);
    			curQuery.addAll(new LinkedList <D>(ce.subList(index+1, ce.size())));    
    			if (membOracle.query(curQuery) != ceOut) {
    				incorrectIdx = index;
    				high = index;
    			} else {
    				correctIdx = index;
    				low = index + 1;
    			}	
    		} // End While
    		analyzeBreakpoint(ce, high);    		
    }


    /************************** Public Methods  ****************************/

    public Integer getNumCEStateUpdates() { 
    		return perfCounters.get("CEStateUpdates");
    }
    
    public Integer getNumCEGuardUpdates() {
    		return perfCounters.get("CEGuardUpdates");
    }
    
    public Integer getNumDetCE() {
    		return perfCounters.get("CEDet");
    }
    
    public Integer getNumCompCE() {
    		return perfCounters.get("CEComp");
    }
    
    /***********  Learning API  ***********/
    
    public SFA <P,D> getModel() throws TimeoutException {
    		tree = new DiscriminationTree <D> (membOracle);    		
        return constructModel();
    }

    public SFA <P,D> updateModel(List <D> counterexample) throws TimeoutException {
        if (model == null) {
            throw new AssertionError("UpdateModel called without first building a model");
        } else if (model.accepts(counterexample, ba) == membOracle.query(counterexample)) {
        		throw new AssertionError("Counterexample given is not a counterexample");
        }        
        proccessCounterexample(counterexample);
        return constructModel();           		
    }

    public SFA <P,D> getModelFinal(EquivalenceOracle <SFA <P, D>, List <D>> equiv) throws TimeoutException {
        List <D> ce;
        SFA <P,D> cleanModel = getModel();
        while ((ce = equiv.getCounterexample(cleanModel)) != null) {
        		cleanModel = updateModel(ce);
        }
        return cleanModel;
    }
    	
}

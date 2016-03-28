/**
 * SVPAlib
 * automata.sfa
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata.safa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import com.google.common.collect.Lists;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;
import utilities.Pair;
import utilities.UnionFindHopKarp;

/**
 * Symbolic finite automaton
 * 
 * @param
 * 			<P>
 *            set of predicates over the domain S
 * @param <S>
 *            domain of the automaton alphabet
 */
public class SAFA<P, S, E extends BooleanExpression> {

	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

	private Integer initialState;
	private Collection<Integer> states;
	private Collection<Integer> finalStates;

	protected Map<Integer, Collection<SAFAInputMove<P, S, E>>> inputMovesFrom;

	private Integer maxStateId;
	private Integer transitionCount;

	/**
	 * @return the maximum state id
	 */
	public Integer getMaxStateId() {
		return maxStateId;
	}

	/**
	 * @return number of states in the automaton
	 */
	public Integer stateCount() {
		return states.size();
	}

	/**
	 * @return number of transitions in the automaton
	 */
	public Integer getTransitionCount() {
		return transitionCount;
	}

	// ------------------------------------------------------
	// Constructors
	// ------------------------------------------------------

	// Initializes all the fields of the automaton
	private SAFA() {
		super();
		finalStates = new HashSet<>();
		states = new HashSet<Integer>();
		inputMovesFrom = new HashMap<Integer, Collection<SAFAInputMove<P, S, E>>>();
		transitionCount = 0;
		maxStateId = 0;
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	public static <A, B, E extends BooleanExpression> SAFA<A, B, E> MkSAFA(Collection<SAFAInputMove<A, B, E>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba, BooleanExpressionFactory<E> boolexpr) {
		return MkSAFA(transitions, initialState, finalStates, ba, boolexpr, true);
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	public static <A, B, E extends BooleanExpression> SAFA<A, B, E> MkSAFA(Collection<SAFAInputMove<A, B, E>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba, BooleanExpressionFactory<E> boolexpr, boolean normalize) {

		SAFA<A, B, E> aut = new SAFA<A, B, E>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = new HashSet<>(finalStates);

		for (SAFAInputMove<A, B, E> t : transitions)
			aut.addTransition(t, ba, false);

		if (normalize)
			return aut.normalize(ba, boolexpr);
		else
			return aut;
	}

	// Adds a transition to the SFA
	private void addTransition(SAFAInputMove<P, S, E> transition, BooleanAlgebra<P, S> ba, boolean skipSatCheck) {

		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.maxState > maxStateId)
				maxStateId = transition.maxState;

			states.add(transition.from);
			states.addAll(transition.toStates);

			getInputMovesFrom(transition.from).add((SAFAInputMove<P, S, E>) transition);
		}
	}

	// ------------------------------------------------------
	// Constant automata
	// ------------------------------------------------------

	/**
	 * Returns the empty SFA for the Boolean algebra <code>ba</code>
	 */
	public static <A, B, E extends BooleanExpression> SAFA<A, B, E> getEmptySAFA(BooleanAlgebra<A, B> ba, BooleanExpressionFactory<E> bexpr) {
		SAFA<A, B, E> aut = new SAFA<A, B, E>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<>();
		aut.initialState = 0;
		aut.maxStateId = 1;
		aut.addTransition(new SAFAInputMove<A, B, E>(0, bexpr.MkState(0), ba.True()), ba, true);
		return aut;
	}

	// ------------------------------------------------------
	// Runnable operations
	// ------------------------------------------------------

	/**
	 * Returns true if the SAFA accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 */
	public boolean accepts(List<S> input, BooleanAlgebra<P, S> ba) {
		List<S> revInput = Lists.reverse(input);

		Collection<Integer> currConf = finalStates;

		for (S el : revInput) {
			currConf = getPrevState(currConf, el, ba);

			if (currConf.isEmpty())
				return false;
		}

		return currConf.contains(initialState);
	}

	/**
	 * Return a list [<g1, t1>, ..., <gn, tn>] of <guard, transition table>
	 * pairs such that: - For each i and each state s, s transitions to ti[s] on
	 * reading a letter satisfying gi - {g1, ..., gn} is the set of all
	 * satisfiable conjunctions of guards on outgoing transitions leaving the
	 * input set of states
	 * 
	 * @param states
	 *            The states from which to compute the outgoing transitions
	 * @param ba
	 * @param guard
	 *            All transitions in the list must comply with guard
	 * @return
	 */
	private <F extends BooleanExpression> LinkedList<Pair<P, Map<Integer,F>>> getTransitionTablesFrom(Collection<Integer> states,
			BooleanAlgebra<P, S> ba, P guard, BooleanExpressionFactory<F> tgt) {
		LinkedList<Pair<P, Map<Integer,F>>> moves = new LinkedList<>();

		BooleanExpressionMorphism<F> coerce = new BooleanExpressionMorphism<>((x) -> tgt.MkState(x), tgt);
		moves.add(new Pair<P, Map<Integer, F>>(guard, new HashMap<>()));
		for (Integer s : states) {
			LinkedList<Pair<P, Map<Integer, F>>> moves2 = new LinkedList<>();
			for (SAFAInputMove<P, S, E> t : getInputMovesFrom(s)) {
				for (Pair<P, Map<Integer, F>> move : moves) {
					P newGuard = ba.MkAnd(t.guard, move.getFirst());
					if (ba.IsSatisfiable(newGuard)) {
						Map<Integer,F> map = new HashMap<Integer,F>(move.getSecond());
						map.put(s, coerce.apply(t.to));
						moves2.add(new Pair<>(newGuard, map));
					}
				}
			}
			moves = moves2;
		}
		return moves;
	}

	/**
	 * Checks whether the SAFA aut is empty
	 * @throws TimeoutException 
	 */
	public static <P,S,E extends BooleanExpression> boolean isEmpty(SAFA<P,S,E> aut, BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr) throws TimeoutException {
		return isEquivalent(aut,getEmptySAFA(ba, boolexpr),  ba, boolexpr);
	}
	
	/**
	 * Checks whether laut and raut are equivalent using bisimulation up to congruence.
	 */
	public static <P, S, E extends BooleanExpression, F extends BooleanExpression> boolean isEquivalent(SAFA<P, S, F> laut,
			SAFA<P, S, F> raut, BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr)
			throws TimeoutException {
		SAFARelation similar = new SATRelation();
		LinkedList<Pair<E, E>> worklist = new LinkedList<>();

		E leftInitial = boolexpr.MkState(laut.initialState);
		E rightInitial = boolexpr.MkState(raut.initialState);

		similar.add(leftInitial, rightInitial);
		worklist.add(new Pair<>(leftInitial, rightInitial));
		while (!worklist.isEmpty()) {
			Pair<E, E> next = worklist.removeFirst();

			E left = next.getFirst();
			E right = next.getSecond();

			LinkedList<Pair<P, Map<Integer,E>>> leftMoves = laut.getTransitionTablesFrom(left.getStates(), ba, ba.True(), boolexpr);
			for (Pair<P, Map<Integer, E>> leftMove : leftMoves) {
				E leftSucc = boolexpr.substitute((lit) -> leftMove.getSecond().get(lit)).apply(left);

				boolean leftSuccAccept = leftSucc.hasModel(laut.finalStates);

				LinkedList<Pair<P, Map<Integer,E>>> rightMoves = raut.getTransitionTablesFrom(right.getStates(),
						ba, leftMove.getFirst(), boolexpr);
				for (Pair<P, Map<Integer,E>> rightMove : rightMoves) {
					E rightSucc = boolexpr.substitute((lit) -> rightMove.getSecond().get(lit)).apply(right);
					if (leftSuccAccept != rightSucc.hasModel(raut.finalStates)) {
						// leftSucc is accepting and rightSucc is rejecting or
						// vice versa
						return false;
					} else if (!similar.isMember(leftSucc, rightSucc)) {
						similar.add(leftSucc, rightSucc);
						worklist.addFirst(new Pair<>(leftSucc, rightSucc));
					}
				}
			}
		}
		return true;
	}

	protected Collection<Integer> getPrevState(Collection<Integer> currState, S inputElement, BooleanAlgebra<P, S> ba) {
		Collection<Integer> prevState = new HashSet<Integer>();
		for (SAFAInputMove<P, S, E> t : getInputMoves()) {
			BooleanExpression b = t.to;
			if (b.hasModel(currState) && ba.HasModel(t.guard, inputElement))
				prevState.add(t.from);
		}

		return prevState;
	}	
	

	/**
	 * Checks whether laut and raut are equivalent using HopcroftKarp on the SFA accepting the reverse language
	 */
	public static <P,S,E extends BooleanExpression> Pair<Boolean, List<S>> areReverseEquivalent(SAFA<P,S,E> aut1, SAFA<P,S,E> aut2, BooleanAlgebra<P, S> ba) {

		UnionFindHopKarp<S> ds = new UnionFindHopKarp<>();

		HashMap<HashSet<Integer>, Integer> reached1 = new HashMap<HashSet<Integer>, Integer>();
		HashMap<HashSet<Integer>, Integer> reached2 = new HashMap<HashSet<Integer>, Integer>();
		
		LinkedList<Pair<HashSet<Integer>, HashSet<Integer>>> toVisit = new LinkedList<>();				
		
		HashSet<Integer> in1 =new HashSet<Integer>(aut1.finalStates);
		HashSet<Integer> in2 =new HashSet<Integer>(aut2.finalStates);
				
		reached1.put(in1, 0);
		reached2.put(in2, 1);
		toVisit.add(new Pair<HashSet<Integer>, HashSet<Integer>>(in1, in2));
		
		ds.add(0,in1.contains(aut1.initialState), new LinkedList<>());
		ds.add(1,in2.contains(aut2.initialState), new LinkedList<>());
		ds.mergeSets(0,1);
		
		while (!toVisit.isEmpty()) {
			Pair<HashSet<Integer>, HashSet<Integer>> curr = toVisit.removeFirst();
			HashSet<Integer> curr1 =curr.first;
			HashSet<Integer> curr2 =curr.second;			
			
			ArrayList<SAFAInputMove<P, S, E>> movesToCurr1 = new ArrayList<>();
			ArrayList<P> predicatesToCurr1 = new ArrayList<>();
			ArrayList<SAFAInputMove<P, S, E>> movesToCurr2 = new ArrayList<>();
			ArrayList<P> predicatesToCurr2 = new ArrayList<>();								
			
			for (SAFAInputMove<P, S, E> t : aut1.getInputMoves())
				if (t.to.hasModel(curr1)) {
					movesToCurr1.add(t);
					predicatesToCurr1.add(t.guard);
				}
			for (SAFAInputMove<P, S, E> t : aut2.getInputMoves())
				if (t.to.hasModel(curr2)) {
					movesToCurr2.add(t);
					predicatesToCurr2.add(t.guard);
				}

			Collection<Pair<P, ArrayList<Integer>>> minterms1 = ba.GetMinterms(predicatesToCurr1);
			Collection<Pair<P, ArrayList<Integer>>> minterms2 = ba.GetMinterms(predicatesToCurr2);
			
			for (Pair<P, ArrayList<Integer>> minterm1 : minterms1) {
				for (Pair<P, ArrayList<Integer>> minterm2 : minterms2) {
					P conj = ba.MkAnd(minterm1.first, minterm2.first);
					if (ba.IsSatisfiable(conj)) {
						//Take from states						
						HashSet<Integer> from1 = new HashSet<>();
						HashSet<Integer> from2 = new HashSet<>();
						for(int i=0;i<minterm1.second.size();i++)
							if(minterm1.second.get(i)==1)
								from1.add(movesToCurr1.get(i).from);
						
						for(int i=0;i<minterm2.second.size();i++)
							if(minterm2.second.get(i)==1)
								from2.add(movesToCurr2.get(i).from);
						
						List<S> pref = new LinkedList<S>(ds.getWitness(reached1.get(curr1)));
						pref.add(ba.generateWitness(conj));
												
						// If not in union find add them
						Integer r1=null,r2=null;						
						if(!reached1.containsKey(from1)){
							r1= ds.getNumberOfElements();
							reached1.put(from1, r1);
							ds.add(r1,from1.contains(aut1.initialState),pref);
						}
						if(r1==null)
							r1 = reached1.get(from1);
						
						if(!reached2.containsKey(from2)){
							r2= ds.getNumberOfElements();
							reached2.put(from2, r2);
							ds.add(r2,from2.contains(aut2.initialState),pref);
						}
						if(r2==null)
							r2 = reached2.get(from2);
							
						//Check whether are in simulation relation
						if (!ds.areInSameSet(r1, r2)) {
							if(!ds.mergeSets(r1, r2))
								return new Pair<Boolean, List<S>>(false, Lists.reverse(pref));
							
							toVisit.add(new Pair<HashSet<Integer>, HashSet<Integer>>(from1,from2));
						}
					}
				}
			}
		}
		return new Pair<Boolean, List<S>>(true, null);
	}
	
	/**
	 * Returns true if the SAFA accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 */
	public static <P,S,E extends BooleanExpression> SFA<P, S> getReverseSFA(SAFA<P,S,E> aut, BooleanAlgebra<P, S> ba) {

		// components of new SFA
		Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		HashMap<HashSet<Integer>, Integer> reached = new HashMap<HashSet<Integer>, Integer>();
		LinkedList<HashSet<Integer>> toVisit = new LinkedList<HashSet<Integer>>();

		HashSet<Integer> init = new HashSet<>(aut.finalStates);
		reached.put(init, 0);
		toVisit.add(init);

		while (!toVisit.isEmpty()) {
			HashSet<Integer> currentState = toVisit.removeFirst();
			int currentStateID = reached.get(currentState);
			ArrayList<SAFAInputMove<P, S, E>> movesToCurr = new ArrayList<>();
			ArrayList<P> predicatesToCurr = new ArrayList<>();

			if(currentState.contains(aut.initialState))
				finalStates.add(currentStateID);
			
			for (SAFAInputMove<P, S, E> t : aut.getInputMoves())
				if (t.to.hasModel(currentState)) {
					movesToCurr.add(t);
					predicatesToCurr.add(t.guard);
				}

			Collection<Pair<P, ArrayList<Integer>>> minterms = ba.GetMinterms(predicatesToCurr);
			for (Pair<P, ArrayList<Integer>> minterm : minterms) {
				
				ArrayList<Integer> moveBits = minterm.second;
				HashSet<Integer> fromState = new HashSet<Integer>();
				for (int moveIndex = 0; moveIndex < moveBits.size(); moveIndex++)
					if (moveBits.get(moveIndex) == 1)
						fromState.add(movesToCurr.get(moveIndex).from);

				// Add new move if target state is not the empty set
				if (fromState.size() > 0) {
					int fromSt = getStateId(fromState, reached,toVisit);
					transitions.add(new SFAInputMove<P, S>(currentStateID,
							fromSt, minterm.first));
				}				
			}
		}

		SFA<P,S> rev =  SFA.MkSFA(transitions, initialState, finalStates, ba);
		rev.setIsDet(true);
		return rev;
	}

	
	// ------------------------------------------------------
	// Boolean automata operations
	// ------------------------------------------------------

	/**
	 * Computes the intersection with <code>aut</code> as a new SFA
	 */
	public SAFA<P, S, E> intersectionWith(SAFA<P, S, E> aut, BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr) {
		return binaryOp(this, aut, ba, boolexpr, BoolOp.Intersection);
	}

	/**
	 * Computes the intersection with <code>aut</code> as a new SFA
	 */
	public SAFA<P, S, E> unionWith(SAFA<P, S, E> aut, BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr) {
		return binaryOp(this, aut, ba, boolexpr, BoolOp.Union);
	}

	public enum BoolOp {
		Intersection, Union
	}

	/**
	 * Computes the intersection with <code>aut1</code> and <code>aut2</code> as
	 * a new SFA
	 */
	public static <A, B, E extends BooleanExpression> SAFA<A, B, E> binaryOp(SAFA<A, B, E> aut1, SAFA<A, B, E> aut2, BooleanAlgebra<A, B> ba, BooleanExpressionFactory<E> boolexpr, BoolOp op) {

		int offset = aut1.maxStateId + 1;

		Integer initialState = aut1.maxStateId + aut2.maxStateId + 2;

		Collection<Integer> finalStates = new ArrayList<Integer>(aut1.finalStates);
		for (int state : aut2.finalStates)
			finalStates.add(state + offset);

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<A, B, E>> transitions = new ArrayList<SAFAInputMove<A, B, E>>(aut1.getInputMoves());
		for (SAFAInputMove<A, B, E> t : aut2.getInputMoves())
			transitions.add(new SAFAInputMove<>(t.from + offset, boolexpr.offset(offset).apply(t.to), t.guard));

		switch (op) {
		case Union:
			// Add extra moves from new initial state
			for (SAFAInputMove<A, B, E> t : aut1.getInputMovesFrom(aut1.initialState))
				transitions.add(new SAFAInputMove<>(initialState, t.to, t.guard));

			for (SAFAInputMove<A, B, E> t : aut2.getInputMovesFrom(aut2.initialState))
				transitions.add(new SAFAInputMove<>(initialState, boolexpr.offset(offset).apply(t.to), t.guard));
			break;

		case Intersection:
			// Add extra moves from new initial state
			for (SAFAInputMove<A, B, E> t1 : aut1.getInputMovesFrom(aut1.initialState))
				for (SAFAInputMove<A, B, E> t2 : aut2.getInputMovesFrom(aut2.initialState)) {
					A newGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(newGuard)) {
						// Compute intersected output state
						E liftedt2 = boolexpr.offset(offset).apply(t2.to);
						E newTo = boolexpr.MkAnd(t1.to, liftedt2);

						transitions.add(new SAFAInputMove<>(initialState, newTo, newGuard));
					}
				}
			break;

		default:
			break;
		}

		return MkSAFA(transitions, initialState, finalStates, ba, boolexpr, true);
	}

	/**
	 * Normalizes the SAFA by having at most one transition for each symbol out
	 * of each state
	 */
	public SAFA<P, S, E> normalize(BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr) {

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S, E>> transitions = new ArrayList<SAFAInputMove<P, S, E>>();

		boolean addedSink = false;
		int sink=maxStateId+1;
		for (int state : states) {
			ArrayList<SAFAInputMove<P, S, E>> trFromState = new ArrayList<>(getInputMovesFrom(state));
			P leftoverPredicate = ba.True();
			ArrayList<P> predicates = new ArrayList<>();
			for (SAFAInputMove<P, S, E> t : trFromState){
				predicates.add(t.guard);
				ba.MkAnd(leftoverPredicate, ba.MkNot(t.guard));
			}
			
			//Make sure the automaton is complete
			if(ba.IsSatisfiable(leftoverPredicate)){
				transitions.add(new SAFAInputMove<>(state, boolexpr.MkState(sink), leftoverPredicate));
				addedSink = true;
			}

			Collection<Pair<P, ArrayList<Integer>>> minterms = ba.GetMinterms(predicates);
			for (Pair<P, ArrayList<Integer>> minterm : minterms) {
				E newTo = null;
				for (int i = 0; i < minterm.second.size(); i++)
					if (minterm.second.get(i) == 1)
						if (newTo == null)
							newTo = trFromState.get(i).to;
						else
							newTo = boolexpr.MkOr(newTo, trFromState.get(i).to);

				if (newTo != null)
					transitions.add(new SAFAInputMove<>(state, newTo, minterm.first));
			}
		}
		if(addedSink)
			transitions.add(new SAFAInputMove<>(sink, boolexpr.MkState(sink), ba.True()));

		return MkSAFA(transitions, initialState, finalStates, ba, boolexpr, false);
	}

	// ------------------------------------------------------
	// Properties accessing methods
	// ------------------------------------------------------

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SAFAInputMove<P, S, E>> getInputMovesFrom(Integer state) {
		Collection<SAFAInputMove<P, S, E>> trset = inputMovesFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SAFAInputMove<P, S ,E>>();
			inputMovesFrom.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SAFAInputMove<P, S, E>> getInputMovesFrom(Collection<Integer> stateSet) {
		Collection<SAFAInputMove<P, S, E>> transitions = new LinkedList<SAFAInputMove<P, S, E>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}
	
	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SAFAInputMove<P, S, E>> getInputMoves() {
		return getInputMovesFrom(states);
	}

	@Override
	public Object clone() {
		SAFA<P, S, E> cl = new SAFA<P, S, E>();

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<>(finalStates);

		cl.inputMovesFrom = new HashMap<Integer, Collection<SAFAInputMove<P, S, E>>>(inputMovesFrom);

		return cl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s = "Automaton: " + getTransitionCount() + " transitions, " + stateCount() + " states" + "\n";
		s += "Transitions \n";
		for (SAFAInputMove<P, S, E> t : getInputMoves())
			s = s + t + "\n";

		s += "Initial State \n";
		s = s + initialState + "\n";

		s += "Final States \n";
		for (Integer fs : finalStates)
			s = s + fs + "\n";
		return s;
	}
	
	/**
	 * If <code>state<code> belongs to reached returns reached(state)
	 * otherwise add state to reached and to toVisit and return corresponding id
	 */
	public static <A, B> int getStateId(A state, Map<A, Integer> reached,
			LinkedList<A> toVisit) {
		if (!reached.containsKey(state)) {
			int newId = reached.size();
			reached.put(state, newId);
			toVisit.add(state);
			return newId;
		} else
			return reached.get(state);
	}

}

package algebralearning.bdd;

import algebralearning.AlgebraLearner;
import algebralearning.finitealgebra.FiniteAlgebraLearnerFactory;
import algebralearning.oracles.EquivalenceOracle;
import algebralearning.oracles.MembershipOracle;
import algebralearning.sfa.SFAAlgebraLearner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.bdd.BDD;
import theory.bdd.BDDFactory;
import theory.binaryalgebra.BinaryBooleanAlgebra;


class BDDToSFAMembershipOracle extends MembershipOracle <List<Boolean>> {

	private MembershipOracle <BDD> memb;
	private BDDFactory factory;

	BDDToSFAMembershipOracle(MembershipOracle <BDD>m, BDDFactory f) {
		memb = m;
		factory = f;
	}

	@Override
	public boolean query(List <Boolean> input) throws TimeoutException {
		if (input.size() != factory.varNum()) {
			return false;
		}
		return memb.query(convertBooleanListToBDD(input));
	}


	public BDD convertBooleanListToBDD(List <Boolean> inp) {
		BDD res = factory.one();
		Integer index = 0;

		for (Boolean e : inp) {
			if (!e) {
				res = res.and(factory.nithVar(index));			;
			} else {
				res = res.and(factory.ithVar(index));
			}
			index ++;
		}
		return res;
	}
}

public class BDDAlgebraLearner extends AlgebraLearner<BDD, BDD> {

	private MembershipOracle <BDD> memb;
	private Integer numvars;
	BDDFactory factory;

	SFAAlgebraLearner <HashSet <Boolean> ,Boolean> sfaLearner;
	BinaryBooleanAlgebra ba;
	BDD BDDModel;
	SFA <HashSet<Boolean>, Boolean> sfaModel;


	public BDDAlgebraLearner(MembershipOracle <BDD> m,Integer n) {

		HashSet <Boolean> alphabet = new HashSet <>();
		FiniteAlgebraLearnerFactory <HashSet <Boolean>, Boolean> baLearnerFactory;
		numvars = n;
		factory = BDDFactory.init(500000, 150000);
		factory.setVarNum(numvars);
		alphabet.add(false);
		alphabet.add(true);
		ba = new BinaryBooleanAlgebra();
		baLearnerFactory = new FiniteAlgebraLearnerFactory <HashSet <Boolean>, Boolean>(alphabet, ba);
		memb = m;
		BDDToSFAMembershipOracle sfaMemb = new BDDToSFAMembershipOracle(memb, factory);
		sfaLearner = new SFAAlgebraLearner <HashSet <Boolean>, Boolean>(sfaMemb, ba, baLearnerFactory);
	}

	public BDDAlgebraLearner(MembershipOracle <BDD> m, BDDFactory f) {
		HashSet <Boolean> alphabet = new HashSet <>();
		FiniteAlgebraLearnerFactory <HashSet <Boolean>, Boolean> baLearnerFactory;
		numvars = f.varNum();
		factory = f;

		alphabet.add(false);
		alphabet.add(true);
		ba = new BinaryBooleanAlgebra();
		baLearnerFactory = new FiniteAlgebraLearnerFactory <HashSet <Boolean>, Boolean>(alphabet, ba);
		memb = m;
		BDDToSFAMembershipOracle sfaMemb = new BDDToSFAMembershipOracle(memb, factory);
		sfaLearner = new SFAAlgebraLearner <HashSet <Boolean>, Boolean>(sfaMemb, ba, baLearnerFactory);
	}


	/***** AUTOMATA TO BDD REPR  *****/

	private List <Boolean> generateWitness(SFA <HashSet <Boolean>, Boolean> sfa, BinaryBooleanAlgebra ba, Integer errorState) {
		SFA <HashSet<Boolean>, Boolean> newSFA;

		try {
			newSFA = SFA.MkSFA(sfa.getTransitions(), errorState, sfa.getFinalStates(), ba);
			return newSFA.getWitness(ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private List <Boolean> hasSmallAccepting(SFA <HashSet <Boolean>, Boolean> sfa, BinaryBooleanAlgebra ba) throws TimeoutException {
		
		// Create as many states as number of variables + 1 
		int numStates = factory.varNum() ;
		HashSet <Integer> states, finalStates; 
		HashSet <SFAMove<HashSet<Boolean>, Boolean>>transitions = new HashSet<>(); 
		
		states = new HashSet<>();
		finalStates = new HashSet<>();
		for (int i = 0; i < numStates; i ++) {
			states.add(i);
		}
		finalStates = new HashSet<>(states);
		
		for (int i = 0; i < numStates-1; i ++) {
			SFAMove <HashSet<Boolean>, Boolean>move = new SFAInputMove<HashSet<Boolean>, Boolean>(i, i+1, ba.True());
			transitions.add(move);
		}
		
		SFA <HashSet <Boolean>, Boolean> smallSFA = SFA.MkSFA(transitions, 0, finalStates, ba);		
		//smallSFA.createDotFile("smallsfa", "/tmp/");
		
		SFA <HashSet <Boolean>, Boolean> intersection = smallSFA.intersectionWith(sfa, ba);
		if (!intersection.isEmpty()) {
			return intersection.getWitness(ba);
		}
		return null;
	}
	
	private List <Boolean> hasLargeAccepting(SFA <HashSet <Boolean>, Boolean> sfa, BinaryBooleanAlgebra ba) throws TimeoutException {
		// Create as many states as number of variables + 1 
		int numStates = factory.varNum() + 2;
		HashSet <Integer> states, finalStates; 
		HashSet <SFAMove<HashSet<Boolean>, Boolean>>transitions = new HashSet<>(); 
		
		states = new HashSet<>();
		finalStates = new HashSet<>();
		for (int i = 0; i < numStates; i ++) {
			states.add(i);
		}
		finalStates.add(numStates-1);
		
		for (int i = 0; i < numStates-1; i ++) {
			SFAMove <HashSet<Boolean>, Boolean>move = new SFAInputMove<HashSet<Boolean>, Boolean>(i, i+1, ba.True());
			transitions.add(move);
		}
		
		SFA <HashSet <Boolean>, Boolean> smallSFA = SFA.MkSFA(transitions, 0, finalStates, ba);		
		//smallSFA.createDotFile("largesfa", "/tmp/");
		SFA <HashSet <Boolean>, Boolean> intersection = smallSFA.intersectionWith(sfa, ba);
		if (!intersection.isEmpty()) {
			return intersection.getWitness(ba);
		}
		return null;
	}

	public List <Boolean> verifySFAisBDD(SFA <HashSet <Boolean>, Boolean> sfa, BinaryBooleanAlgebra ba) throws TimeoutException {
		/* Verify that SFA computation graph is a tree */

		HashSet <Integer> visited = new HashSet<>();
		List <List<Boolean>> paths = new LinkedList <>();
		List <Boolean> curPath, errorPath = null;
		List <Integer> stack = new LinkedList <>();
		Integer curState, errorState=0;

		// Assumes that unreachable and dead states are removed;
		if (sfa.stateCount() <= 1) {
			return null;
		}

		if (sfa.getFinalStates().size() > 1) {
			throw new AssertionError("More than one final state, this shouldn't happen");
		}

		
		if ((errorPath = hasSmallAccepting(sfa, ba)) != null) {
			return errorPath;
		}
		if ((errorPath = hasLargeAccepting(sfa, ba)) != null) {
			return errorPath;
		}
		
		
		stack.add(sfa.getInitialState());
		paths.add(new LinkedList <Boolean>());
		while (stack.size() > 0) {
			curState = stack.remove(0);
			curPath = paths.remove(0);
			visited.add(curState);
			for (SFAMove <HashSet<Boolean>, Boolean> t : sfa.getInputMovesFrom(curState)) {
				if (visited.contains(t.to) && !sfa.getFinalStates().contains(t.to)) {
					errorState = t.to;
					errorPath = curPath;
					errorPath.add(t.getWitness(ba));
					break;
				}
				stack.add(t.to);
				curPath.add(t.getWitness(ba));
				paths.add(new LinkedList <Boolean>(curPath));
				curPath.remove(curPath.size()-1);
			}
			if (errorPath != null) {
				break;
			}

		}
		if (errorPath != null) {
			 errorPath.addAll(generateWitness(sfa, ba, errorState));
			 return errorPath;
		}
		return null;
	}


	private Boolean hasSelfLoop(SFA <HashSet <Boolean>, Boolean> sfa, BinaryBooleanAlgebra ba, int curState) throws TimeoutException {
		for (SFAMove <HashSet <Boolean>, Boolean> move : sfa.getTransitionsFrom(curState)) {
			if (move.isSatisfiable(ba) && move.to == curState) {
				return true;
			}
		}
		return false;
	}

	private BDD convertSFAtoBDDRec(SFA <HashSet <Boolean>, Boolean> sfa, int numvars,
			Hashtable <Integer, BDD> stateTable, int curLevel, int curState) throws TimeoutException {

		BinaryBooleanAlgebra ba = new BinaryBooleanAlgebra();
		BDD trueBDD, falseBDD, curBDD, root;
		Integer targetFalse, targetTrue;
		Collection <SFAMove <HashSet <Boolean>, Boolean>> transitions = sfa.getTransitionsFrom(curState);

		if (transitions.size() == 0 || hasSelfLoop(sfa, ba, curState)) {
			if (sfa.getFinalStates().contains(curState)) {
				curBDD = factory.one();
			} else {
				curBDD = factory.zero();
			}
			stateTable.put(curState, curBDD);
			return curBDD;
		}

		targetTrue = targetFalse = -1;
		for (SFAMove <HashSet <Boolean>, Boolean> move : transitions) {
			if (move.hasModel(true, ba)) {
				targetTrue = move.to;
			}
			if (move.hasModel(false, ba)) {
				targetFalse = move.to;
			}
		}
		if (targetTrue == -1) {
			trueBDD = factory.zero();
		} else if (stateTable.containsKey(targetTrue)) {
			trueBDD = stateTable.get(targetTrue);
		} else {
			trueBDD = convertSFAtoBDDRec(sfa, numvars, stateTable, curLevel+1, targetTrue);
		}
		if (targetFalse == -1 ) {
			falseBDD = factory.zero();
		} else if (stateTable.containsKey(targetFalse)) {
			falseBDD = stateTable.get(targetFalse);
		} else {
			falseBDD = convertSFAtoBDDRec(sfa, numvars, stateTable, curLevel+1, targetFalse);
		}
		curBDD = factory.ithVar(curLevel);
		root = curBDD.ite(trueBDD, falseBDD);
		stateTable.put(curState, root);
		return root;
	}

	public BDD convertSFAtoBDD(SFA <HashSet <Boolean>, Boolean>sfa) throws TimeoutException {
		Hashtable <Integer, BDD> stateTable = new Hashtable <>();
		BDD result = convertSFAtoBDDRec(sfa, numvars, stateTable, 0, 0);
		return result;
	}

	/*************** PUBLIC METHODS *******************/


	public BDD convertBooleanListToBDD(List <Boolean> inp) {

		BDD res = factory.one();
		Integer index = 0;

		for (Boolean e : inp) {
			if (!e) {
				res = res.and(factory.nithVar(index));			;
			} else {
				res = res.and(factory.ithVar(index));
			}
			index ++;
		}
		return res;
	}

	public List <Boolean> convertBDDSToBooleanList(BDD singleton) {

		LinkedList <Boolean> res =  new LinkedList <Boolean>();

		for (Integer i = 0; i < numvars; i ++) {
			if (singleton.and(factory.ithVar(i)).equals(factory.zero())) {
				res.add(false);
			} else {
				res.add(true);
			}
		}
		return res;
	}


	@Override
	public BDD getModel() throws TimeoutException {

		sfaModel = sfaLearner.getModel();
		List <Boolean> ce;
		while ((ce = verifySFAisBDD(sfaModel, ba)) != null) {
			sfaModel = sfaLearner.updateModel(ce);
		}
		BDDModel = convertSFAtoBDD(sfaModel);
		if (BDDModel == null) {
			throw new AssertionError("BDD IS NULL");
		}

		return BDDModel;
	}

	BDD extractAtom(BDD bdd) throws TimeoutException {

		Boolean result = memb.query(bdd);
		//bdd.printDot();
		//System.out.println(bdd.isOne());
		

		for (int i = 0; i < factory.varNum(); i ++) {
			// we have that both choices would work.
			BDD pos = factory.ithVar(i).and(bdd);
			BDD neg = factory.nithVar(i).and(bdd);
			if (!neg.isZero() && !pos.isZero()) {
				if (memb.query(pos) == result) {
					bdd = pos;
				} else if (memb.query(neg) == result) {
					bdd = neg;
				} else {
					throw new AssertionError("WTF");
				}
			}
		}
		return bdd;
	}

	@Override
	public BDD updateModel(BDD bddCE) throws TimeoutException {
		
		//bddCE = extractAtom(bddCE);
		//System.out.println("=============================");
		//bddCE.printDot();
		//System.out.println("Initially: " + bddCE);
		//System.out.println("After double conversion: " + convertBooleanListToBDD(convertBDDSToBooleanList(bddCE)));
		//System.out.println("Are equal?" + bddCE.equals(convertBooleanListToBDD(convertBDDSToBooleanList(bddCE))));
		//BDD minus = convertBooleanListToBDD(convertBDDSToBooleanList(bddCE));
		
		//System.out.println("Does it belong? " + (minus.and(bddCE) != factory.zero()));
		//System.out.println("Is the negation empty?" + minus.not().and(bddCE).equals(factory.zero())); 
		
		//System.out.println("Can we get more?" );
		List <Boolean> ce;
		//sfaModel.createDotFile("before.dot", "/tmp/");
		sfaModel = sfaLearner.updateModel(convertBDDSToBooleanList(bddCE));
		while ((ce = verifySFAisBDD(sfaModel, ba)) != null) {
			//sfaModel.createDotFile("foo.dot", "/tmp/");
			//System.out.println(ce);
			sfaModel = sfaLearner.updateModel(ce);
		}
		BDDModel = convertSFAtoBDD(sfaModel);
		if (BDDModel == null) {
			throw new AssertionError("BDD IS NULL");
		}
		return BDDModel;
	}

	@Override
	public BDD getModelFinal(EquivalenceOracle <BDD, BDD> equiv) throws TimeoutException {
		BDD model = getModel();
		BDD ce;
		while ((ce = equiv.getCounterexample(model)) != null) {
			model = updateModel(ce);
		}
		return model;
	}

}

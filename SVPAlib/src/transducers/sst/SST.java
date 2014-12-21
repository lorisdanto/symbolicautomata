/**
 * 
 */
package transducers.sst;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theory.BooleanAlgebraSubst;
import utilities.Pair;
import automata.AutomataException;
import automata.Automaton;
import automata.Move;

public class SST<P, F, S> extends Automaton<P, S> {

	// SST properties
	protected Collection<Integer> states;
	protected Collection<Integer> initialStates;
	protected Collection<Integer> finalStates;

	protected Map<String, Integer> variablesToIndices;

	// moves the output to the variable in position 0
	protected Map<Integer, SimpleVariableUpdate<P, F, S>> outputFunction;

	public Integer maxStateId;

	// Moves are inputs or epsilon
	protected Map<Integer, Collection<SSTInputMove<P, F, S>>> transitionsFrom;
	protected Map<Integer, Collection<SSTInputMove<P, F, S>>> transitionsTo;

	protected Map<Integer, Collection<SSTEpsilon<P, F, S>>> epsTransitionsFrom;
	protected Map<Integer, Collection<SSTEpsilon<P, F, S>>> epsTransitionsTo;

	public Integer stateCount() {
		return states.size();
	}

	public Integer transitionCount() {
		return getTransitions().size();
	}

	protected SST() {
		super();
		initialStates = new HashSet<Integer>();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
		variablesToIndices = new HashMap<String, Integer>();
		outputFunction = new HashMap<Integer, SimpleVariableUpdate<P, F, S>>();
		transitionsFrom = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>();
		transitionsTo = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>();
		epsTransitionsFrom = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>();
		epsTransitionsTo = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>();
		maxStateId = 0;
	}

	/*
	 * Create an automaton (removes unreachable states)
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> MkSST(
			Collection<SSTMove<P1, F1, S1>> transitions,
			Collection<Integer> initialStates, String[] variables,
			Map<Integer, SimpleVariableUpdate<P1, F1, S1>> outputFunction,
			BooleanAlgebraSubst<P1, F1, S1> ba) throws AutomataException {

		// Sanity checks
		if (initialStates.size() == 0)
			throw new AutomataException("No initial states");

		SST<P1, F1, S1> aut = new SST<P1, F1, S1>();

		// Initialize state set
		aut.initialStates=new HashSet<Integer>(initialStates);
		aut.states = new HashSet<Integer>(initialStates);
		aut.finalStates = outputFunction.keySet();
		aut.states.addAll(aut.finalStates);

		aut.outputFunction = outputFunction;
		
		int index=0;
		for(String var: variables){
			aut.variablesToIndices.put(var, index);
			index++;
		}

		for (SSTMove<P1, F1, S1> t : transitions)
			aut.addTransition(t, ba, false);

		// cleanup set isEmpty and hasEpsilon
		// TODO
		// aut = removeUnreachableStates(aut, ba);

		return aut;
	}

	/**
	 * Returns the empty SST for the boolean algebra <code>ba</code>
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> getEmptySST(
			BooleanAlgebraSubst<P1, F1, S1> ba) {
		SST<P1, F1, S1> aut = new SST<P1, F1, S1>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>();
		aut.initialStates = new HashSet<Integer>(aut.states);
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.isEpsilonFree = true;
		aut.maxStateId = 1;
		return aut;
	}

	public List<S> outputOn(List<S> input, BooleanAlgebraSubst<P, F, S> ba) {
		return outputOn(this, input, ba);
	}
	
	/**
	 * Computes one of the ouptuts produced when reading input. Input if no such
	 * output exists
	 * 
	 * @param input
	 * @param ba
	 * @return one output sequence, null if undefined
	 */
	public static <P1, F1, S1> List<S1> outputOn(SST<P1, F1, S1> sstWithEps,
			List<S1> input, BooleanAlgebraSubst<P1, F1, S1> ba) {

		SST<P1, F1, S1> sst = sstWithEps.removeEpsilonMoves(ba);
		// Assume that there are no epsilon transitions for now

		Map<Integer, Collection<VariableAssignment<S1>>> currConf = sst
				.initializedConfig();

		for (Integer state : sst.getInitialStates()) {
			currConf.get(state).add(
					VariableAssignment.MkInitialValue(
							sst.variablesToIndices.size(), ba));
		}

		for (S1 el : input)
			currConf = sst.getNextConfig(currConf, el, ba);

		for (int state : sst.getFinalStates()) {
			Collection<VariableAssignment<S1>> varVals = currConf.get(state);
			for (VariableAssignment<S1> assignment : varVals) {
				// apply outputFunction
				SimpleVariableUpdate<P1, F1, S1> outputUpdate = sst.outputFunction
						.get(state);
				VariableAssignment<S1> v1 = outputUpdate
						.applyTo(assignment, sst.variablesToIndices, ba);
				return v1.outputVariableValue();
			}
		}

		return null;
	}

	// creates an empty configuration
	private Map<Integer, Collection<VariableAssignment<S>>> initializedConfig() {
		Map<Integer, Collection<VariableAssignment<S>>> configuration = new HashMap<Integer, Collection<VariableAssignment<S>>>();
		for (Integer state : getStates())
			configuration.put(state, new LinkedList<VariableAssignment<S>>());

		return configuration;
	}

	// Makes one step on the current config and symbol in the sst
	private Map<Integer, Collection<VariableAssignment<S>>> getNextConfig(
			Map<Integer, Collection<VariableAssignment<S>>> currConfig,
			S input, BooleanAlgebraSubst<P, F, S> ba) {

		Map<Integer, Collection<VariableAssignment<S>>> newConfig = initializedConfig();

		for (int state : getStates()) {
			Collection<VariableAssignment<S>> sourceAssignments = currConfig
					.get(state);
			if (!sourceAssignments.isEmpty())
				for (SSTInputMove<P, F, S> move : getInputMovesFrom(state))
					if (move.hasModel(input, ba)) {
						Collection<VariableAssignment<S>> targetAssignments = newConfig
								.get(move.to);
						for (VariableAssignment<S> assig : sourceAssignments)
							targetAssignments.add(
									move.variableUpdate.applyTo(
											assig, variablesToIndices,
											input, ba));
					}
		}
		return newConfig;
	}

	/**
	 * Computes the combination with <code>aut</code> as a new SST
	 * combine(w)=f1(w)f2(w)
	 */
	public SST<P, F, S> combineWith(SST<P, F, S> aut,
			BooleanAlgebraSubst<P, F, S> ba) {
		return combine(this, aut, ba);
	}

	/**
	 * Computes the combination of <code>aut1</code> and <code>aut2</code>
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> combine(SST<P1, F1, S1> sst1,
			SST<P1, F1, S1> sst2, BooleanAlgebraSubst<P1, F1, S1> ba) {

		// TODO assuming they are epsilon free

		SST<P1, F1, S1> combined = new SST<P1, F1, S1>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial states
		int totStates = 0;
		for (Integer st1 : sst1.initialStates)
			for (Integer st2 : sst2.initialStates) {
				Pair<Integer, Integer> p = new Pair<Integer, Integer>(st1, st2);
				combined.initialStates.add(totStates);
				combined.states.add(totStates);

				reached.put(p, totStates);
				toVisit.add(p);

				totStates++;
			}

		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			// If both states are final, combine is final
			if (sst1.isFinalState(currState.first)
					&& sst2.isFinalState(currState.second))
				combined.finalStates.add(currStateId);

			for (SSTInputMove<P1, F1, S1> t1 : sst1
					.getInputMovesFrom(currState.first))
				for (SSTInputMove<P1, F1, S1> t2 : sst2
						.getInputMovesFrom(currState.second)) {
					if (!t1.isEpsilonTransition() && !t2.isEpsilonTransition()) {
						SSTInputMove<P1, F1, S1> ct1 = (SSTInputMove<P1, F1, S1>) t1;
						SSTInputMove<P1, F1, S1> ct2 = (SSTInputMove<P1, F1, S1>) t2;
						P1 intersGuard = ba.MkAnd(ct1.guard, ct2.guard);
						if (ba.IsSatisfiable(intersGuard)) {

							Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
									t1.to, t2.to);
							int nextStateId = 0;

							if (!reached.containsKey(nextState)) {
								combined.transitionsTo
										.put(totStates,
												new HashSet<SSTInputMove<P1, F1, S1>>());
								reached.put(nextState, totStates);
								toVisit.add(nextState);
								combined.states.add(totStates);
								nextStateId = totStates;
								totStates++;
							} else
								nextStateId = reached.get(nextState);

							// TODO shouldn not be null
							FunctionalVariableUpdate<P1, F1, S1> combinedUpdate = null;
							SSTInputMove<P1, F1, S1> newTrans = 
									new SSTInputMove<P1, F1, S1>(
									currStateId, nextStateId, intersGuard,
									combinedUpdate);

							combined.addTransition(newTrans, ba, true);
						}
					}
				}
		}

		// TODO remove unreachable states
		// combined = removeUnreachableStates(combined, ba);
		return combined;
	}

	//
	// /**
	// * Computes <code>this</code> minus <code>sst2</code>
	// */
	// public SST<P, F, S> minus(SST<P, F, S> aut, BooleanAlgebraSubst<P, F, S>
	// ba) {
	// return differnce(this, aut, ba);
	// }
	//
	// /**
	// * Computes <code>sst1</code> minus <code>sst2</code>
	// */
	// public static <P1, F1, S1> SST<P1, F1, S1> differnce(SST<P1, F1, S1>
	// sst1, SST<P1, F1, S1> sst2,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// SST<P1, F1, S1> diff = sst1.intersectionWith(sst2.complement(ba), ba);
	// return removeUnreachableStates(diff, ba);
	// }
	//
	// /**
	// * Computes the union with <code>aut</code> as a new SST
	// */
	// public SST<P, F, S> unionWith(SST<P, F, S> sst1, BooleanAlgebraSubst<P,
	// F, S> ba) {
	// return union(this, sst1, ba);
	// }
	//
	// /**
	// * Computes the union with <code>aut</code> as a new SST
	// */
	// public static <P1, F1, S1> SST<P1, F1, S1> union(SST<P1, F1, S1> sst1,
	// SST<P1, F1, S1> sst2,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// if (sst1.isEmpty && sst2.isEmpty)
	// return getEmptySST(ba);
	//
	// SST<P1, F1, S1> union = new SST<P1, F1, S1>();
	// union.isEmpty = false;
	//
	// int offSet = sst1.maxStateId + 2;
	// union.maxStateId = sst2.maxStateId + offSet+1;
	//
	// for (Integer state : sst1.states)
	// union.states.add(state);
	//
	// for (Integer state : sst2.states)
	// union.states.add(state + offSet);
	//
	// Integer initState = union.maxStateId;
	// union.initialStates.add(initState);
	// union.states.add(initState);
	//
	// for (SSTMove<P1, F1, S1> t : sst1.getTransitions()) {
	// @SuppressWarnings("unchecked")
	// SSTMove<P1, F1, S1> newMove = (SSTMove<P1, F1, S1>) t.clone();
	// union.addTransition(newMove, ba, true);
	// }
	//
	// for (SSTMove<P1, F1, S1> t : sst2.getTransitions()) {
	// @SuppressWarnings("unchecked")
	// SSTMove<P1, F1, S1> newMove = (SSTMove<P1, F1, S1>) t.clone();
	// newMove.from += offSet;
	// newMove.to += offSet;
	// union.addTransition(newMove, ba, true);
	// }
	//
	// for (Integer state : sst1.initialStates)
	// union.addTransition(new Epsilon<P1, F1, S1>(initState, state), ba, true);
	//
	// for (Integer state : sst2.initialStates)
	// union.addTransition(new Epsilon<P1, F1, S1>(initState, state + offSet),
	// ba, true);
	//
	// for (Integer state : sst1.finalStates)
	// union.finalStates.add(state);
	//
	// for (Integer state : sst2.finalStates)
	// union.finalStates.add(state + offSet);
	//
	// return union;
	// }
	//

	/**
	 * return an equivalent copy without epsilon moves
	 */
	public SST<P, F, S> removeEpsilonMoves(BooleanAlgebraSubst<P, F, S> ba) {
		return removeEpsilonMovesFrom(this, ba);
	}

	/**
	 * TODO implement return an equivalent copy without epsilon moves
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> removeEpsilonMovesFrom(
			SST<P1, F1, S1> aut, BooleanAlgebraSubst<P1, F1, S1> ba) {

		return aut;
		// SST<P1, F1, S1> epsFree = new SST<P1, F1, S1>();
		//
		// HashMap<Collection<Integer>, Integer> reachedStates = new
		// HashMap<Collection<Integer>, Integer>();
		// LinkedList<Collection<Integer>> toVisitStates = new
		// LinkedList<Collection<Integer>>();
		//
		// // Add initial states
		// for (Integer st1 : aut.initialStates) {
		// Collection<Integer> p = aut.getEpsClosure(st1, ba);
		// int nextId = reachedStates.size();
		//
		// epsFree.initialStates.add(nextId);
		// epsFree.states.add(nextId);
		//
		// reachedStates.put(p, nextId);
		// toVisitStates.add(p);
		// }
		//
		// while (!toVisitStates.isEmpty()) {
		// Collection<Integer> currState = toVisitStates.removeFirst();
		// int currStateId = reachedStates.get(currState);
		//
		// for (SSTMove<P1, F1, S1> t1 : aut.getTransitionsFrom(currState)) {
		// if (!t1.isEpsilonTransition()) {
		// Collection<Integer> nextState = aut
		// .getEpsClosure(t1.to, ba);
		//
		// int nextStateId = 0;
		//
		// if (!reachedStates.containsKey(nextState)) {
		// int index = reachedStates.size();
		// reachedStates.put(nextState, index);
		// toVisitStates.add(nextState);
		// epsFree.states.add(index);
		// nextStateId = index;
		// } else {
		// nextStateId = reachedStates.get(nextState);
		// }
		//
		// @SuppressWarnings("unchecked")
		// SSTMove<P1, F1, S1> tnew = (SSTMove<P1, F1, S1>) t1.clone();
		// tnew.from = currStateId;
		// tnew.to = nextStateId;
		//
		// epsFree.addTransition(tnew, ba, true);
		// }
		// }
		//
		// }
		//
		// for (Collection<Integer> stSet : reachedStates.keySet())
		// if (aut.isFinalConfiguration(stSet))
		// epsFree.finalStates.add(reachedStates.get(stSet));
		//
		// return removeUnreachableStates(epsFree, ba);
	}

	//
	// /**
	// * return the complement of the current SST
	// */
	// public SST<P, F, S> complement(BooleanAlgebraSubst<P, F, S> ba) {
	// return complementOf(this, ba);
	// }
	//
	// /**
	// * return the complement of the current SST
	// */
	// public static <P1, F1, S1> SST<P1, F1, S1> complementOf(SST<P1, F1, S1>
	// aut,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// SST<P1, F1, S1> comp = aut.mkTotal(ba);
	//
	// Collection<Integer> finStateCopy = new HashSet<Integer>(
	// comp.finalStates);
	// comp.finalStates = new HashSet<Integer>();
	//
	// for (Integer st : comp.states)
	// if (!finStateCopy.contains(st))
	// comp.finalStates.add(st);
	//
	// return comp;
	// }
	//
	// /**
	// * return the complement of the current SST
	// */
	// @SuppressWarnings("unchecked")
	// public static <P1, F1, S1> SST<P1, F1, S1> mkSingleInitState(SST<P1, F1,
	// S1> aut,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// SST<P1, F1, S1> sing = (SST<P1, F1, S1>) aut.clone();
	// if(sing.initialStates.size()==1)
	// return sing;
	//
	// Integer newState = sing.maxStateId+1;
	//
	// for(Integer st: sing.states)
	// sing.addTransition(new Epsilon<P1, F1, S1>(newState, st), ba, true);
	//
	// sing.initialStates = new HashSet<Integer>();
	// sing.initialStates.add(newState);
	//
	// return sing;
	// }
	//
	// /**
	// * return the total version of the current SST
	// */
	// public SST<P, F, S> mkTotal(BooleanAlgebraSubst<P, F, S> ba) {
	// return mkTotal(this, ba);
	// }
	//
	// /**
	// * return the total version of aut
	// */
	// @SuppressWarnings("unchecked")
	// public static <P1, F1, S1> SST<P1, F1, S1> mkTotal(SST<P1, F1, S1> aut,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// if (aut.isTotal) {
	// return (SST<P1, F1, S1>) aut.clone();
	// }
	//
	// SST<P1, F1, S1> SST = aut;
	// if (!aut.isDeterministic(ba))
	// SST = determinize(aut, ba);
	//
	// SST<P1, F1, S1> total = new SST<P1, F1, S1>();
	//
	// total.initialStates = new HashSet<Integer>(SST.initialStates);
	// total.finalStates = new HashSet<Integer>(SST.finalStates);
	// int newState = SST.maxStateId + 1;
	// for (Integer state : SST.states) {
	// A totGuard = null;
	// for (SSTInputMove<P1, F1, S1> move : SST.getInputMovesFrom(state)) {
	// total.addTransition(move, ba, true);
	// if (totGuard == null)
	// totGuard = ba.MkNot(move.guard);
	// else
	// totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
	// }
	// if (totGuard != null)
	// total.addTransition(new InputMove<P1, F1, S1>(state, newState,
	// totGuard), ba, false);
	// }
	// if (total.states.contains(newState))
	// total.addTransition(
	// new InputMove<P1, F1, S1>(newState, newState, ba.True()), ba,
	// true);
	//
	// total.isTotal = true;
	// return total;
	// }
	//
	// /**
	// * return the complement of the current SST
	// */
	// public boolean isEquivalentTo(SST<P, F, S> aut, BooleanAlgebraSubst<P, F,
	// S> ba) {
	// return areEquivalent(this, aut, ba);
	// }
	//
	// /**
	// * checks wheter sst1 is equivalent to sst2
	// */
	// public static <P1, F1, S1> boolean areEquivalent(SST<P1, F1, S1> sst1,
	// SST<P1, F1, S1> sst2,
	// BooleanAlgebra<P1, F1, S1> ba) {
	// if (!differnce(sst1, sst2, ba).isEmpty)
	// return false;
	// return differnce(sst2, sst1, ba).isEmpty;
	// }
	//
	// /**
	// * concatenation
	// */
	// public SST<P, F, S> concatenateWith(SST<P, F, S> aut,
	// BooleanAlgebraSubst<P, F, S> ba) {
	// return concatenate(this, aut, ba);
	// }
	//
	// /**
	// * concatenates sst1 with sst2
	// */
	// public static <P1, F1, S1> SST<P1, F1, S1> concatenate(SST<P1, F1, S1>
	// sst1, SST<P1, F1, S1> sst2, BooleanAlgebra<A,B> ba) {
	//
	// if (sst1.isEmpty && sst2.isEmpty)
	// return getEmptySST(ba);
	//
	// SST<P1, F1, S1> concat = new SST<P1, F1, S1>();
	// concat.isEmpty = false;
	//
	// int offSet = sst1.maxStateId + 1;
	// concat.maxStateId = sst2.maxStateId + offSet;
	//
	// for (Integer state : sst1.states)
	// concat.states.add(state);
	//
	// for (Integer state : sst2.states)
	// concat.states.add(state + offSet);
	//
	// concat.initialStates=new HashSet<Integer>(sst1.initialStates);
	//
	// for (SSTMove<P1, F1, S1> t : sst1.getTransitions()) {
	// @SuppressWarnings("unchecked")
	// SSTMove<P1, F1, S1> newMove = (SSTMove<P1, F1, S1>) t.clone();
	// concat.addTransition(newMove, ba, true);
	// }
	//
	// for (SSTMove<P1, F1, S1> t : sst2.getTransitions()) {
	// @SuppressWarnings("unchecked")
	// SSTMove<P1, F1, S1> newMove = (SSTMove<P1, F1, S1>) t.clone();
	// newMove.from += offSet;
	// newMove.to += offSet;
	// concat.addTransition(newMove, ba, true);
	// }
	//
	// for (Integer state1 : sst1.finalStates)
	// for (Integer state2 : sst2.initialStates)
	// concat.addTransition(new Epsilon<P1, F1, S1>(state1, state2+ offSet), ba,
	// true);
	//
	// concat.finalStates=new HashSet<Integer>();
	//
	// for (Integer state : sst2.finalStates)
	// concat.finalStates.add(state + offSet);
	//
	// return concat;
	// }
	//
	// /**
	// * language star
	// */
	// public static <P1, F1, S1> SST<P1, F1, S1> star(SST<P1, F1, S1> aut,
	// BooleanAlgebra<A,B> ba) {
	//
	// if (aut.isEmpty)
	// return getEmptySST(ba);
	//
	// SST<P1, F1, S1> star = new SST<P1, F1, S1>();
	// star.isEmpty = false;
	//
	// star.states= new HashSet<Integer>(aut.states);
	// Integer initState = aut.maxStateId+1;
	//
	// star.initialStates=new HashSet<Integer>();
	// star.initialStates.add(initState);
	//
	// for (SSTMove<P1, F1, S1> t : aut.getTransitions()) {
	// @SuppressWarnings("unchecked")
	// SSTMove<P1, F1, S1> newMove = (SSTMove<P1, F1, S1>) t.clone();
	// star.addTransition(newMove, ba, true);
	// }
	//
	// for (Integer state : aut.finalStates)
	// star.addTransition(new Epsilon<P1, F1, S1>(state, initState), ba, true);
	//
	// for (Integer state : aut.initialStates)
	// star.addTransition(new Epsilon<P1, F1, S1>(initState, state), ba, true);
	//
	// star.finalStates=new HashSet<Integer>(aut.finalStates);
	// star.finalStates.add(initState);
	//
	// return star;
	// }
	//
	// /**
	// * return the determinization of the current SST
	// */
	// public SST<P, F, S> determinize(BooleanAlgebraSubst<P, F, S> ba) {
	// return determinize(this, ba);
	// }
	//
	// /**
	// * return the determinization of aut
	// */
	// public static <P1, F1, S1> SST<P1, F1, S1> determinize(SST<P1, F1, S1>
	// aut,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// if (aut.isDeterministic(ba))
	// return aut;
	//
	// SST<P1, F1, S1> deter = new SST<P1, F1, S1>();
	//
	// HashMap<Collection<Integer>, Integer> reachedStates = new
	// HashMap<Collection<Integer>, Integer>();
	// LinkedList<Collection<Integer>> toVisitStates = new
	// LinkedList<Collection<Integer>>();
	//
	// // Add initial state
	// Collection<Integer> currState = aut
	// .getEpsClosure(aut.initialStates, ba);
	//
	// deter.initialStates.add(0);
	// deter.states.add(0);
	//
	// reachedStates.put(currState, 0);
	// toVisitStates.add(currState);
	//
	// // Dfs to find states
	// while (!toVisitStates.isEmpty()) {
	// currState = toVisitStates.removeFirst();
	// int currStateId = reachedStates.get(currState);
	//
	// // Check if final state
	// if (aut.isFinalConfiguration(currState))
	// deter.finalStates.add(currStateId);
	//
	// ArrayList<SSTInputMove<P1, F1, S1>> movesFromCurrState = new
	// ArrayList<SSTInputMove<P1, F1, S1>>(
	// aut.getInputMovesFrom(currState));
	//
	// ArrayList<A> internalPredicates = new ArrayList<A>();
	// for (SSTInputMove<P1, F1, S1> inter: movesFromCurrState)
	// internalPredicates.add(inter.guard);
	//
	// for(Pair<A, ArrayList<Integer>> minterm :
	// ba.GetMinterms(internalPredicates)){
	//
	// A guard = minterm.first;
	// Collection<Integer> bitList= minterm.second;
	// Integer index=0;
	//
	// Collection<Integer> toState = new HashSet<Integer>();
	//
	// for(Integer bit: bitList){
	// // use the predicate positively if i-th bit of i is 1
	// if (bit == 1)
	// {
	// // get the indexth call in the list
	// InputMove<P1, F1, S1> bitMove = movesFromCurrState.get(index);
	// toState.add(bitMove.to);
	// }
	// index++;
	// }
	//
	// toState = aut.getEpsClosure(toState, ba);
	// if (toState.size() > 0)
	// {
	// Integer toStateId = reachedStates.get(toState);
	// if (toStateId == null) {
	// toStateId = reachedStates.size();
	// reachedStates.put(toState, toStateId);
	// toVisitStates.add(toState);
	// }
	// deter.addTransition(new InputMove<P1, F1, S1>(currStateId,
	// toStateId, guard), ba, true);
	// }
	// }
	// }
	//
	// deter.stateCount=deter.states.size();
	// deter.isDeterministic = true;
	// return deter;
	// }
	//
	// /**
	// * checks whether the SST is ambiguous
	// */
	// public List<S> getAmbiguousInput(BooleanAlgebraSubst<P, F, S> ba) {
	// return getAmbiguousInput(this, ba);
	// }
	//
	// /**
	// * Checks whether <code>aut</code> is ambiguous
	// */
	// @SuppressWarnings("unchecked")
	// public static <P1, F1, S1> List<B> getAmbiguousInput(SST<P1, F1, S1> aut,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// SST<P1, F1, S1> sst1 = (SST<P1, F1, S1>) mkSingleInitState(aut,
	// ba).clone();
	// SST<P1, F1, S1> sst2 = (SST<P1, F1, S1>) sst1.clone();
	//
	// SST<P1, F1, S1> product = new SST<P1, F1, S1>();
	//
	// HashMap<Pair<Integer, Integer>, Integer> reached = new
	// HashMap<Pair<Integer, Integer>, Integer>();
	// HashMap<Integer, Pair<Integer, Integer>> reachedRev = new
	// HashMap<Integer, Pair<Integer, Integer>>();
	// LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer,
	// Integer>>();
	//
	// // Add initial states
	// int totStates = 0;
	// for (Integer st1 : sst1.initialStates)
	// for (Integer st2 : sst2.initialStates) {
	// Pair<Integer, Integer> p = new Pair<Integer, Integer>(st1, st2);
	// product.initialStates.add(totStates);
	// product.states.add(totStates);
	//
	// reached.put(p, totStates);
	// reachedRev.put(totStates, p);
	// toVisit.add(p);
	//
	// totStates++;
	// }
	//
	// while (!toVisit.isEmpty()) {
	// Pair<Integer, Integer> currState = toVisit.removeFirst();
	// int currStateId = reached.get(currState);
	//
	// Collection<Integer> epsClo1 = sst1.getEpsClosure(currState.first, ba);
	// Collection<Integer> epsClo2 = sst2.getEpsClosure(currState.second, ba);
	//
	// // Set final states
	// boolean isFin = false;
	// for (Integer st : epsClo1)
	// if (sst1.isFinalState(st)) {
	// isFin = true;
	// break;
	// }
	// if (isFin) {
	// isFin = false;
	// for (Integer st : epsClo2)
	// if (sst2.isFinalState(st)) {
	// isFin = true;
	// break;
	// }
	// if (isFin)
	// product.finalStates.add(currStateId);
	// }
	//
	// for (SSTMove<P1, F1, S1> t1 : sst1.getTransitionsFrom(epsClo1))
	// for (SSTMove<P1, F1, S1> t2 : sst2.getTransitionsFrom(epsClo2)) {
	// if (!t1.isEpsilonTransition() && !t2.isEpsilonTransition()) {
	// InputMove<P1, F1, S1> ct1 = (SSTInputMove<P1, F1, S1>) t1;
	// InputMove<P1, F1, S1> ct2 = (SSTInputMove<P1, F1, S1>) t2;
	// A intersGuard = ba.MkAnd(ct1.guard, ct2.guard);
	// if (ba.IsSatisfiable(intersGuard)) {
	//
	// Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
	// t1.to, t2.to);
	// int nextStateId = 0;
	//
	// if (!reached.containsKey(nextState)) {
	// product.transitionsTo.put(totStates,
	// new HashSet<SSTMove<P1, F1, S1>>());
	//
	// reached.put(nextState, totStates);
	// reachedRev.put(totStates, nextState);
	//
	// toVisit.add(nextState);
	// product.states.add(totStates);
	// nextStateId = totStates;
	// totStates++;
	// } else
	// nextStateId = reached.get(nextState);
	//
	// InputMove<P1, F1, S1> newTrans = new InputMove<P1, F1, S1>(
	// currStateId, nextStateId, intersGuard);
	//
	// product.addTransition(newTrans, ba, true);
	// }
	// }
	// }
	// }
	//
	// product = removeUnreachableStates(product, ba);
	// for(Integer aliveSt: product.states){
	// Pair<Integer,Integer> stP = reachedRev.get(aliveSt);
	// if(stP.first!=stP.second){
	// SST<P1, F1, S1> left = (SST<P1, F1, S1>) product.clone();
	// SST<P1, F1, S1> right = (SST<P1, F1, S1>) product.clone();
	// left.finalStates=new HashSet<Integer>();
	// left.finalStates.add(aliveSt);
	// right.initialStates=new HashSet<Integer>();
	// right.initialStates.add(aliveSt);
	//
	// SST<P1, F1, S1> c = left.concatenateWith(right,ba);
	// SST<P1, F1, S1> clean = removeUnreachableStates(c, ba);
	// return clean.getWitness(ba);
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * return the determinization of the current SST
	// */
	// public SST<P, F, S> minimize(BooleanAlgebraSubst<P, F, S> ba) {
	// return getMinimalOf(this, ba);
	// }
	//
	// /**
	// * return the determinization of aut
	// */
	// @SuppressWarnings("unchecked")
	// public static <P1, F1, S1> SST<P1, F1, S1> getMinimalOf(SST<P1, F1, S1>
	// sst1,
	// BooleanAlgebra<P1, F1, S1> ba) {
	//
	// if(sst1.isEmpty)
	// return (SST<P1, F1, S1>)sst1.clone();
	//
	// SST<P1, F1, S1> aut = sst1;
	//
	// if (!sst1.isDeterministic)
	// aut = sst1.determinize(ba);
	//
	// SST<P1, F1, S1> minimal = new SST<P1, F1, S1>();
	//
	// HashSet<Integer> toSeeStates = new HashSet<Integer>(aut.states);
	//
	// HashMap<Integer,Integer> stateToClass = new HashMap<Integer,Integer>();
	// ArrayList<Collection<Integer>> eqClasses = new
	// ArrayList<Collection<Integer>>(aut.stateCount);
	//
	// // Check for equiv classes
	// int classIndex=0;
	// while (!toSeeStates.isEmpty()) {
	// LinkedList<Integer> toSeeCopy = new LinkedList<Integer>(toSeeStates);
	// Integer currState = toSeeCopy.removeFirst();
	// toSeeStates.remove(currState);
	//
	// stateToClass.put(currState, classIndex);
	//
	// HashSet<Integer> eqClass = new HashSet<Integer>();
	// eqClass.add(currState);
	//
	// SST<P1, F1, S1> autCurrState = (SST<P1, F1, S1>)sst1.clone();
	// autCurrState.initialStates = new HashSet<Integer>();
	// autCurrState.initialStates.add(currState);
	//
	// SST<P1, F1, S1> autOtherState = (SST<P1, F1, S1>)sst1.clone();
	//
	// //Start at 1 to avoid case in which they are all false
	// for(Integer otherState: toSeeCopy){
	// autOtherState.initialStates = new HashSet<Integer>();
	// autOtherState.initialStates.add(otherState);
	//
	// if(autCurrState.isEquivalentTo(autOtherState, ba)){
	// toSeeStates.remove(otherState);
	// eqClass.add(otherState);
	// stateToClass.put(otherState, classIndex);
	// }
	// }
	// eqClasses.add(classIndex, eqClass);
	// classIndex++;
	// }
	//
	// Integer initStateAut = (Integer) aut.initialStates.toArray()[0];
	// for(int i =0 ;i<eqClasses.size();i++){
	// Collection<Integer> eqClass = eqClasses.get(i);
	//
	// for(SSTInputMove<P1, F1, S1> t: aut.getInputMovesFrom(eqClass))
	// minimal.addTransition(new InputMove<P1, F1, S1>(i,
	// stateToClass.get(t.to), t.guard), ba, true);
	//
	// if(aut.isFinalConfiguration(eqClass))
	// minimal.finalStates.add(i);
	//
	// if(eqClass.contains(initStateAut))
	// minimal.initialStates.add(0);
	// }
	//
	// minimal.stateCount=eqClasses.size();
	// minimal = minimal.determinize(ba);
	// return minimal;
	// }
	//
	// //
	// ////////////////////////////////////////////////////////////////////////////////
	//
	// // Accessory methods
	// private static <P1, F1, S1> SST<P1, F1, S1>
	// removeUnreachableStates(SST<P1, F1, S1> aut,
	// BooleanAlgebraSubst<P1, F1, S1> ba) {
	//
	// SST<P1, F1, S1> clean = new SST<P1, F1, S1>();
	//
	// Collection<Integer> reachableFromInit = aut
	// .getReachableStatesFrom(aut.initialStates);
	// Collection<Integer> reachingFinal = aut
	// .getReachingStates(aut.finalStates);
	//
	// for (Integer state : reachableFromInit)
	// if (reachingFinal.contains(state)) {
	// clean.stateCount++;
	// clean.states.add(state);
	// if (state > clean.maxStateId)
	// clean.maxStateId = state;
	// }
	//
	// if (clean.stateCount == 0)
	// return getEmptySST(ba);
	//
	// for (Integer state : clean.states)
	// for (SSTMove<P1, F1, S1> t : aut.getTransitionsFrom(state))
	// if (clean.states.contains(t.to))
	// clean.addTransition(t, ba, true);
	//
	// for (Integer state : aut.initialStates)
	// if (clean.states.contains(state))
	// clean.initialStates.add(state);
	//
	// for (Integer state : aut.finalStates)
	// if (clean.states.contains(state))
	// clean.finalStates.add(state);
	//
	//
	//
	// return clean;
	// }
	//
	// /**
	// * Checks whether the automaton is deterministic
	// *
	// * @return true iff the automaton is deterministic
	// */
	// public boolean isDeterministic(BooleanAlgebraSubst<P, F, S> ba) {
	// // Check if we set it before
	// if (isDeterministic)
	// return isDeterministic;
	//
	// // check only one initial state
	// if (initialStates.size() != 1) {
	// isDeterministic = false;
	// return isDeterministic;
	// }
	//
	// // check only one initial state
	// if (!isEpsilonFree) {
	// isDeterministic = false;
	// return isDeterministic;
	// }
	//
	// // Check transitions out of a state are mutually exclusive
	// for (Integer state : states) {
	// List<SSTMove<P, F, S>> movesFromState = new ArrayList<SSTMove<P, F, S>>(
	// getTransitionsFrom(state));
	//
	// for (int i = 0; i < movesFromState.size(); i++) {
	// SSTMove<P, F, S> t1 = movesFromState.get(i);
	// for (int p = i + 1; p < movesFromState.size(); p++) {
	// SSTMove<P, F, S> t2 = movesFromState.get(p);
	// if (!t1.isDisjointFrom(t2, ba)) {
	// isDeterministic = false;
	// return isDeterministic;
	// }
	// }
	// }
	// }
	//
	// isDeterministic = true;
	// return isDeterministic;
	// }
	//
	// public Collection<Integer> getReachableStatesFrom(Collection<Integer>
	// states) {
	// HashSet<Integer> result = new HashSet<Integer>();
	// for (Integer state : states)
	// visitForward(state, result);
	// return result;
	// }
	//
	// public Collection<Integer> getReachingStates(Collection<Integer> states)
	// {
	// HashSet<Integer> result = new HashSet<Integer>();
	// for (Integer state : states)
	// visitBackward(state, result);
	// return result;
	// }
	//
	// private void visitForward(Integer state, HashSet<Integer> reachables) {
	// if (!reachables.contains(state)) {
	// reachables.add(state);
	// for (SSTMove<P, F, S> t : this.getTransitionsFrom(state)) {
	// Integer nextState = t.to;
	// visitForward(nextState, reachables);
	// }
	// }
	// }
	//
	// private void visitBackward(Integer state, HashSet<Integer> reachables) {
	// if (!reachables.contains(state)) {
	// reachables.add(state);
	// for (SSTMove<P, F, S> t : this.getTransitionsTo(state)) {
	// Integer predState = t.from;
	// visitBackward(predState, reachables);
	// }
	// }
	// }

	// ACCESSORS

	/**
	 * Add Transition
	 */
	private void addTransition(SSTMove<P, F, S> transition,
			BooleanAlgebraSubst<P, F, S> ba, boolean skipSatCheck) {

		if (transition.isEpsilonTransition()) {
			if (transition.to == transition.from)
				return;
			isEpsilonFree = false;
		}

		if (skipSatCheck || transition.isSatisfiable(ba)) {

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.to > maxStateId)
				maxStateId = transition.to;

			states.add(transition.from);
			states.add(transition.to);

			if (transition.isEpsilonTransition()) {
				getEpsilonMovesFrom(transition.from).add(
						(SSTEpsilon<P, F, S>) transition);
				getEpsilonMovesTo(transition.to).add(
						(SSTEpsilon<P, F, S>) transition);
			} else {
				getInputMovesFrom(transition.from).add(
						(SSTInputMove<P, F, S>) transition);
				getInputMovesTo(transition.to).add(
						(SSTInputMove<P, F, S>) transition);
			}
		}
	}

	// ACCESORIES METHODS

	// GET INPUT MOVES

	/**
	 * Returns the set of transitions from state <code>s</code>
	 */
	public Collection<SSTInputMove<P, F, S>> getInputMovesFrom(Integer state) {
		Collection<SSTInputMove<P, F, S>> trset = transitionsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SSTInputMove<P, F, S>>();
			transitionsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SSTInputMove<P, F, S>> getInputMovesFrom(
			Collection<Integer> stateSet) {
		Collection<SSTInputMove<P, F, S>> transitions = new LinkedList<SSTInputMove<P, F, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of input transitions to state <code>s</code>
	 */
	public Collection<SSTInputMove<P, F, S>> getInputMovesTo(Integer state) {
		Collection<SSTInputMove<P, F, S>> trset = transitionsTo.get(state);
		if (trset == null) {
			trset = new HashSet<SSTInputMove<P, F, S>>();
			transitionsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions to set of states
	 */
	public Collection<SSTInputMove<P, F, S>> getInputMovesTo(
			Collection<Integer> stateSet) {
		Collection<SSTInputMove<P, F, S>> transitions = new LinkedList<SSTInputMove<P, F, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesTo(state));
		return transitions;
	}

	// GET Epsilon MOVES

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SSTEpsilon<P, F, S>> getEpsilonMovesFrom(Integer state) {
		Collection<SSTEpsilon<P, F, S>> trset = epsTransitionsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SSTEpsilon<P, F, S>>();
			epsTransitionsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SSTEpsilon<P, F, S>> getEpsilonMovesFrom(
			Collection<Integer> stateSet) {
		Collection<SSTEpsilon<P, F, S>> transitions = new LinkedList<SSTEpsilon<P, F, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getEpsilonMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of input transitions to state <code>s</code>
	 */
	public Collection<SSTEpsilon<P, F, S>> getEpsilonMovesTo(Integer state) {
		Collection<SSTEpsilon<P, F, S>> trset = epsTransitionsTo.get(state);
		if (trset == null) {
			trset = new HashSet<SSTEpsilon<P, F, S>>();
			epsTransitionsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SSTEpsilon<P, F, S>> getEpsilonMovesTo(
			Collection<Integer> stateSet) {
		Collection<SSTEpsilon<P, F, S>> transitions = new LinkedList<SSTEpsilon<P, F, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getEpsilonMovesTo(state));
		return transitions;
	}

	// GET ALL MOVES
	/**
	 * Returns the set of transitions starting at state <code>s</code>
	 */
	public Collection<SSTMove<P, F, S>> getTransitionsFrom(Integer state) {
		Collection<SSTMove<P, F, S>> trset = new HashSet<SSTMove<P, F, S>>();
		trset.addAll(getInputMovesFrom(state));
		trset.addAll(getEpsilonMovesFrom(state));
		return trset;
	}

	/**
	 * Returns the set of transitions starting at a set of states
	 */
	public Collection<SSTMove<P, F, S>> getTransitionsFrom(
			Collection<Integer> stateSet) {
		Collection<SSTMove<P, F, S>> trset = new HashSet<SSTMove<P, F, S>>();
		trset.addAll(getInputMovesFrom(stateSet));
		trset.addAll(getEpsilonMovesFrom(stateSet));
		return trset;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SSTMove<P, F, S>> getTransitionsTo(Integer state) {
		Collection<SSTMove<P, F, S>> trset = new HashSet<SSTMove<P, F, S>>();
		trset.addAll(getInputMovesTo(state));
		trset.addAll(getEpsilonMovesTo(state));
		return trset;
	}

	/**
	 * Returns the set of transitions to a set of states
	 */
	public Collection<SSTMove<P, F, S>> getTransitionsTo(
			Collection<Integer> stateSet) {
		Collection<SSTMove<P, F, S>> trset = new HashSet<SSTMove<P, F, S>>();
		trset.addAll(getInputMovesTo(stateSet));
		trset.addAll(getEpsilonMovesTo(stateSet));
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SSTMove<P, F, S>> getTransitions() {
		return getTransitionsFrom(states);
	}

	// Methods for superclass
	@Override
	public Collection<Move<P, S>> getMoves() {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getTransitions());
		return transitions;
	}

	@Override
	public Collection<Move<P, S>> getMovesFrom(Integer state) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	@Override
	public Collection<Move<P, S>> getMovesTo(Integer state) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	@Override
	public Collection<Integer> getFinalStates() {
		return finalStates;
	}

	@Override
	public Collection<Integer> getInitialStates() {
		return initialStates;
	}

	@Override
	public Collection<Integer> getStates() {
		return states;
	}

	@Override
	public Object clone() {
		SST<P, F, S> cl = new SST<P, F, S>();

		cl.isDeterministic = isDeterministic;
		cl.isTotal = isTotal;
		cl.isEmpty = isEmpty;
		cl.isEpsilonFree = isEpsilonFree;

		cl.maxStateId = maxStateId;

		cl.states = new HashSet<Integer>(states);
		cl.initialStates = new HashSet<Integer>(initialStates);
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.transitionsFrom = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>(
				transitionsFrom);
		cl.transitionsTo = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>(
				transitionsTo);

		cl.epsTransitionsFrom = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>(
				epsTransitionsFrom);
		cl.epsTransitionsTo = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>(
				epsTransitionsTo);

		cl.outputFunction = new HashMap<Integer, SimpleVariableUpdate<P, F, S>>(
				outputFunction);

		return cl;
	}

}

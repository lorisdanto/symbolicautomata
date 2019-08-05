/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import theory.BooleanAlgebraSubst;
import utilities.Pair;
import automata.Automaton;
import automata.Move;
import automata.sfa.SFAEpsilon;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;

/**
 * A symbolic streaming string transducer
 * 
 * @param
 * 			<P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SST<P, F, S> extends Automaton<P, S> {

	// SST properties
	protected Collection<Integer> states;
	protected Integer initialState;

	protected int variableCount;
	protected SimpleVariableUpdate<P, F, S> cachedIdentityVarUp;

	// moves the output to the variable in position 0
	protected Map<Integer, OutputUpdate<P, F, S>> outputFunction;

	protected Integer maxStateId;

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

	// public Collection<String> getVariableNames() {
	// return variablesToIndices.keySet();
	// }

	public SimpleVariableUpdate<P, F, S> identityVarUp() {
		if (cachedIdentityVarUp != null)
			return cachedIdentityVarUp;

		ArrayList<List<ConstantToken<P, F, S>>> identityVariableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>();

		for (int i = 0; i < variableCount; i++) {
			List<ConstantToken<P, F, S>> varup = new ArrayList<ConstantToken<P, F, S>>();
			varup.add(new SSTVariable<P, F, S>(i));
			identityVariableUpdate.add(varup);
		}

		cachedIdentityVarUp = new SimpleVariableUpdate<P, F, S>(identityVariableUpdate);
		return cachedIdentityVarUp;
	}

	protected SST() {
		super();
		states = new HashSet<Integer>();
		transitionsFrom = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>();
		transitionsTo = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>();
		epsTransitionsFrom = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>();
		epsTransitionsTo = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>();
		outputFunction = new HashMap<Integer, OutputUpdate<P, F, S>>();
		maxStateId = 0;
		initialState = 0;
	}

	/*
	 * Create an automaton (removes unreachable states)
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> MkSST(Collection<SSTMove<P1, F1, S1>> transitions, Integer initialState,
			int numberOfVariables, Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction,
			BooleanAlgebraSubst<P1, F1, S1> ba) {

		SST<P1, F1, S1> aut = new SST<P1, F1, S1>();

		// Initialize state set
		aut.initialState = initialState;
		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		Collection<Integer> finalStates = outputFunction.keySet();
		aut.states.addAll(finalStates);
		aut.variableCount = numberOfVariables;

		aut.outputFunction = outputFunction;
		try {
			for (SSTMove<P1, F1, S1> t : transitions)
				aut.addTransition(t, ba, false);

			return aut;
		} catch (TimeoutException toe) {
			return null;
		}
	}

	/**
	 * Returns the empty SST
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> getEmptySST(BooleanAlgebraSubst<P1, F1, S1> ba) {
		SST<P1, F1, S1> aut = new SST<P1, F1, S1>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.initialState = 0;
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.variableCount = 0;
		aut.isEpsilonFree = true;
		aut.maxStateId = 1;
		return aut;
	}

	/**
	 * Returns the SST only defined on epsilon that outputs output
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> getEpsilonSST(List<ConstantToken<P1, F1, S1>> output,
			BooleanAlgebraSubst<P1, F1, S1> ba) {
		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		outputFunction.put(0, new OutputUpdate<P1, F1, S1>(output));

		return MkSST(transitions, 0, 1, outputFunction, ba);
	}

	/**
	 * Returns the SST only defined on predicate that outputs output
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> getBaseSST(P1 predicate, List<Token<P1, F1, S1>> output,
			BooleanAlgebraSubst<P1, F1, S1> ba) {
		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		transitions
				.add(new SSTInputMove<P1, F1, S1>(0, 1, predicate, new FunctionalVariableUpdate<P1, F1, S1>(output)));

		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();

		List<ConstantToken<P1, F1, S1>> outputJustX0 = new ArrayList<ConstantToken<P1, F1, S1>>();
		outputJustX0.add(new SSTVariable<P1, F1, S1>(0));
		outputFunction.put(1, new OutputUpdate<P1, F1, S1>(outputJustX0));

		return MkSST(transitions, 0, 1, outputFunction, ba);
	}

	public List<S> outputOn(List<S> input, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		return outputOn(this, input, ba);
	}

	/**
	 * Computes one of the ouptuts produced when reading input. Null if no such
	 * output exists
	 * 
	 * @param input
	 * @param ba
	 * @return one output sequence, null if undefined
	 * @throws TimeoutException 
	 */
	public static <P1, F1, S1> List<S1> outputOn(SST<P1, F1, S1> sstWithEps, List<S1> input,
			BooleanAlgebraSubst<P1, F1, S1> ba) throws TimeoutException {

		// composition
		SST<P1, F1, S1> sst = sstWithEps.removeEpsilonMoves(ba);
		// Assume that there are no epsilon transitions for now

		Map<Integer, Collection<VariableAssignment<S1>>> currConf = new HashMap<Integer, Collection<VariableAssignment<S1>>>();

		List<VariableAssignment<S1>> initialVariableAssignment = new ArrayList<VariableAssignment<S1>>();
		initialVariableAssignment.add(VariableAssignment.MkInitialValue(sst.variableCount, ba));
		currConf.put(sst.initialState, initialVariableAssignment);

		for (S1 el : input)
			currConf = sst.getNextConfig(currConf, el, ba);

		for (int state : currConf.keySet()) {
			if (sst.isFinalState(state)) {
				Collection<VariableAssignment<S1>> varVals = currConf.get(state);
				for (VariableAssignment<S1> assignment : varVals) {
					// apply outputFunction
					OutputUpdate<P1, F1, S1> outputUpdate = sst.outputFunction.get(state);

					return outputUpdate.applyTo(assignment, ba);
				}
			}
		}

		return null;
	}

	// Makes one step on the current config and symbol in the sst
	private Map<Integer, Collection<VariableAssignment<S>>> getNextConfig(
			Map<Integer, Collection<VariableAssignment<S>>> currConfig, S input, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {

		Map<Integer, Collection<VariableAssignment<S>>> newConfig = new HashMap<Integer, Collection<VariableAssignment<S>>>();

		for (int state : currConfig.keySet()) {
			Collection<VariableAssignment<S>> sourceAssignments = currConfig.get(state);
			for (SSTInputMove<P, F, S> move : getInputMovesFrom(state))
				if (move.hasModel(input, ba)) {
					Collection<VariableAssignment<S>> targetAssignments = new ArrayList<VariableAssignment<S>>();
					if (newConfig.containsKey(move.to))
						targetAssignments = newConfig.get(move.to);
					else
						newConfig.put(move.to, targetAssignments);

					for (VariableAssignment<S> assig : sourceAssignments)
						targetAssignments.add(move.variableUpdate.applyTo(assig, input, ba));
				}
		}
		return newConfig;
	}

	/**
	 * Computes the combination with <code>sst</code> as a new SST
	 * combine(w)=f1(w)f2(w)
	 * 
	 * @throws TimeoutException
	 */
	public SST<P, F, S> combineWith(SST<P, F, S> sst, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		return combine(this, sst, ba);
	}

	/**
	 * Computes the combination of <code>sst1</code> and <code>sst2</code>
	 * 
	 * @throws TimeoutException
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> combine(SST<P1, F1, S1> sst1withEps, SST<P1, F1, S1> sst2withEps,
			BooleanAlgebraSubst<P1, F1, S1> ba) throws TimeoutException {

		// Remove epsilons
		SST<P1, F1, S1> sst1 = sst1withEps.removeEpsilonMoves(ba);
		SST<P1, F1, S1> sst2 = sst2withEps.removeEpsilonMoves(ba);

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Integer initialState = 0;
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial state
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(sst1.initialState, sst2.initialState);

		initialState = 0;

		reached.put(p, initialState);
		toVisit.add(p);

		// Combined has set of variables the disjoint union of the two sets
		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			// If both states are final, combine is final
			if (sst1.isFinalState(currState.first) && sst2.isFinalState(currState.second)) {

				// new output function x = x1x2
				OutputUpdate<P1, F1, S1> outputUpdate = OutputUpdate.combineOutputUpdates(0, 1,
						sst1.outputFunction.get(currState.first), sst2.outputFunction.get(currState.second));
				outputFunction.put(currStateId, outputUpdate);
			}

			for (SSTInputMove<P1, F1, S1> t1 : sst1.getInputMovesFrom(currState.first))
				for (SSTInputMove<P1, F1, S1> t2 : sst2.getInputMovesFrom(currState.second)) {
					P1 intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {

						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(t1.to, t2.to);

						int nextStateId = getStateId(nextState, reached, toVisit);

						// combines two updadate by taking the disjoint union
						FunctionalVariableUpdate<P1, F1, S1> combinedUpdate = FunctionalVariableUpdate
								.combineUpdates(t1.variableUpdate, t2.variableUpdate);
						SSTInputMove<P1, F1, S1> newTrans = new SSTInputMove<P1, F1, S1>(currStateId, nextStateId,
								intersGuard, combinedUpdate);

						transitions.add(newTrans);
					}
				}
		}

		int varCount = sst1.variableCount + sst2.variableCount;

		return MkSST(transitions, initialState, varCount, outputFunction, ba);
	}

	/**
	 * Computes the combination with <code>sst</code> as a new SST
	 * combine(w)=f1(w)f2(w)
	 * 
	 * @throws TimeoutException
	 */
	public SST<P, F, S> restrictInput(SFA<P, S> aut, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		return restrictInput(this, aut, ba);
	}

	/**
	 * Computes the combination of <code>sst1</code> and <code>sst2</code>
	 * 
	 * @throws TimeoutException
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> restrictInput(SST<P1, F1, S1> sst1withEps, SFA<P1, S1> inputSfa,
			BooleanAlgebraSubst<P1, F1, S1> ba) throws TimeoutException {

		// Remove epsilons
		SST<P1, F1, S1> sst = sst1withEps.removeEpsilonMoves(ba);
		SFA<P1, S1> aut = inputSfa.removeEpsilonMoves(ba).determinize(ba).minimize(ba);

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Integer initialState = 0;
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		Integer variableCount = sst.variableCount;

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial state
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(sst.initialState, aut.getInitialState());

		initialState = 0;

		reached.put(p, initialState);
		toVisit.add(p);

		// Combined has set of variables the disjoint union of the two sets
		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			// If both states are final, restrict is final
			if (sst.isFinalState(currState.first) && aut.getFinalStates().contains(currState.second)) {
				outputFunction.put(currStateId, sst.outputFunction.get(currState.first));
			}

			for (SSTInputMove<P1, F1, S1> t1 : sst.getInputMovesFrom(currState.first))
				for (SFAInputMove<P1, S1> t2 : aut.getInputMovesFrom(currState.second)) {
					P1 intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {

						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(t1.to, t2.to);

						int nextStateId = getStateId(nextState, reached, toVisit);

						SSTInputMove<P1, F1, S1> newTrans = new SSTInputMove<P1, F1, S1>(currStateId, nextStateId,
								intersGuard, t1.variableUpdate);

						transitions.add(newTrans);
					}
				}
		}

		return MkSST(transitions, initialState, variableCount, outputFunction, ba);
	}

	/**
	 * return an equivalent copy without epsilon moves
	 */
	public SST<P, F, S> removeEpsilonMoves(BooleanAlgebraSubst<P, F, S> ba) {
		return removeEpsilonMovesFrom(this, ba);
	}

	/**
	 * return an equivalent copy without epsilon moves
	 */
	protected static <P1, F1, S1> SST<P1, F1, S1> removeEpsilonMovesFrom(SST<P1, F1, S1> sst,
			BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst.isEpsilonFree)
			return sst;

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables = sst.variableCount;

		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		HashMap<Integer, Map<Integer, SimpleVariableUpdate<P1, F1, S1>>> statesAss = new HashMap<Integer, Map<Integer, SimpleVariableUpdate<P1, F1, S1>>>();
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// Add initial state
		Map<Integer, SimpleVariableUpdate<P1, F1, S1>> epsclInitial = sst.getSSTEpsClosure(sst.initialState, ba);
		Collection<Integer> p = epsclInitial.keySet();
		initialState = 0;
		statesAss.put(initialState, epsclInitial);

		reachedStates.put(p, initialState);
		toVisitStates.add(p);

		while (!toVisitStates.isEmpty()) {
			Collection<Integer> currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);
			Map<Integer, SimpleVariableUpdate<P1, F1, S1>> stateToAss = statesAss.get(currStateId);

			// set final state
			Integer fin = null;
			for (Integer st : currState) {
				if (sst.isFinalState(st))
					if (fin != null) {
						throw new IllegalArgumentException("two different final states are reachable via epsilon;");
					} else {
						fin = st;
					}
			}
			// set output state if one of the esp closure state is final
			if (fin != null) {
				outputFunction.put(currStateId, stateToAss.get(fin).composeWith(sst.outputFunction.get(fin)));
			}

			for (SSTInputMove<P1, F1, S1> t1 : sst.getInputMovesFrom(currState)) {

				Map<Integer, SimpleVariableUpdate<P1, F1, S1>> epsClosure = sst.getSSTEpsClosure(t1.to, ba);
				Collection<Integer> nextState = epsClosure.keySet();

				int nextStateId = 0;
				if (!reachedStates.containsKey(nextState)) {
					int index = reachedStates.size();
					reachedStates.put(nextState, index);
					toVisitStates.add(nextState);
					statesAss.put(index, epsClosure);
					nextStateId = index;
				} else {
					nextStateId = reachedStates.get(nextState);
				}

				@SuppressWarnings("unchecked")
				SSTInputMove<P1, F1, S1> tnew = (SSTInputMove<P1, F1, S1>) t1.clone();
				tnew.from = currStateId;
				tnew.to = nextStateId;

				tnew.variableUpdate = stateToAss.get(t1.from).composeWith(t1.variableUpdate);

				transitions.add(tnew);
			}

		}

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);
	}

	/**
	 * concatenate with sst
	 */
	public SST<P, F, S> concatenateWith(SST<P, F, S> sst, BooleanAlgebraSubst<P, F, S> ba) {
		return concatenate(this, sst, ba);
	}

	/**
	 * concatenates sst1 with sst2
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> concatenate(SST<P1, F1, S1> sst1, SST<P1, F1, S1> sst2,
			BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst1.isEmpty || sst2.isEmpty)
			return getEmptySST(ba);

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables;

		initialState = sst1.initialState;

		int varRenameSst1 = 0;
		int varRenameSst2 = 0;
		int offSet = sst1.maxStateId + 1;

		// xAcc will be the accumulation variable for the first machine
		// whenever we leave from sst1 to sst2 we update x0 to output of sst1
		int accId = Math.max(sst1.variableCount, sst2.variableCount);
		SSTVariable<P1, F1, S1> accVar = new SSTVariable<P1, F1, S1>(accId);

		numberOfVariables = accId + 1;

		// every transition must have maxId variable Updates
		for (SSTInputMove<P1, F1, S1> t : sst1.getInputMovesFrom(sst1.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst1)
					.liftToNVars(numberOfVariables);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(t.from, t.to, t.guard, variableUpdate);
			transitions.add(newMove);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst1.getEpsilonMovesFrom(sst1.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst1)
					.liftToNVars(numberOfVariables);
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from, t.to, variableUpdate);
			transitions.add(newMove);
		}

		// Moreover transitions in sst2 should perform the update xAcc:=xAcc
		// Each state should also take into account the offset
		for (SSTInputMove<P1, F1, S1> t : sst2.getInputMovesFrom(sst2.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst2)
					.liftToNVars(numberOfVariables);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(accVar);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(t.from + offSet, t.to + offSet, t.guard,
					variableUpdate);
			transitions.add(newMove);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst2.getEpsilonMovesFrom(sst2.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst2)
					.liftToNVars(numberOfVariables);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(accVar);

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from + offSet, t.to + offSet, variableUpdate);

			transitions.add(newMove);
		}

		// add a transition from every final state q of sst1 to the initial
		// state of sst2
		// with the update xAcc = F(q), and x=epsilon for everyone else
		for (Integer finStateSst1 : sst1.getFinalStates()) {
			// Create the update xAcc = F(q), and x=epsilon for everyone else
			ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < numberOfVariables; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For last variable set xAcc := F(q)
				if (i == accId)
					updateList = sst1.outputFunction.get(finStateSst1).renameVars(varRenameSst1).update;
				resUpdate.add(updateList);
			}

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(finStateSst1, sst2.initialState + offSet,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate));

			transitions.add(newMove);
		}

		// create output function for sst2 so that it outputs xAcc F(q)
		for (Integer finStateSst2 : sst2.getFinalStates()) {
			// Create the update x0 = xAcc F(q), and x=epsilon for everyone else

			List<ConstantToken<P1, F1, S1>> outUpdateExpr = new ArrayList<ConstantToken<P1, F1, S1>>();
			outUpdateExpr.add(accVar);
			outUpdateExpr.addAll(sst2.outputFunction.get(finStateSst2).renameVars(varRenameSst2).update);

			outputFunction.put(finStateSst2 + offSet, new OutputUpdate<P1, F1, S1>(outUpdateExpr));
		}

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);
	}

	/**
	 * Computes the union with <code>sst1</code> as a new SST
	 */
	public SST<P, F, S> unionWith(SST<P, F, S> sst1, BooleanAlgebraSubst<P, F, S> ba) {
		return union(this, sst1, ba);
	}

	/**
	 * Computes the union of <code>sst1</code> and <code>sst2</code> as a new
	 * SST
	 */
	@SuppressWarnings("unchecked")
	public static <P1, F1, S1> SST<P1, F1, S1> union(SST<P1, F1, S1> sst1, SST<P1, F1, S1> sst2,
			BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst1.isEmpty && sst2.isEmpty)
			return getEmptySST(ba);
		if (sst1.isEmpty)
			return (SST<P1, F1, S1>) sst2.clone();
		if (sst2.isEmpty)
			return (SST<P1, F1, S1>) sst1.clone();

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables;

		int offSet = sst1.maxStateId + 2;

		// Create fresh initial state for the union
		initialState = sst1.maxStateId + 1;

		// set variable renames for the two ssts, they will share names
		Integer varRenameSst1 = 0;
		Integer varRenameSst2 = 0;

		numberOfVariables = Math.max(sst1.variableCount, sst2.variableCount);

		// every transition must have maxId variable Updates
		for (SSTInputMove<P1, F1, S1> t : sst1.getInputMovesFrom(sst1.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst1)
					.liftToNVars(numberOfVariables);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(t.from, t.to, t.guard, variableUpdate);

			transitions.add(newMove);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst1.getEpsilonMovesFrom(sst1.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst1)
					.liftToNVars(numberOfVariables);
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from, t.to, variableUpdate);

			transitions.add(newMove);
		}
		// Moreover transitions in sst2 should perform the update xAcc:=xAcc
		// Each state should also take into account the offset
		for (SSTInputMove<P1, F1, S1> t : sst2.getInputMovesFrom(sst2.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst2)
					.liftToNVars(numberOfVariables);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(t.from + offSet, t.to + offSet, t.guard,
					variableUpdate);

			transitions.add(newMove);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst2.getEpsilonMovesFrom(sst2.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.renameVars(varRenameSst2)
					.liftToNVars(numberOfVariables);
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from + offSet, t.to + offSet, variableUpdate);

			transitions.add(newMove);
		}

		// Add transitions from new initial state to old initial states
		// Create the update x=epsilon for every var
		ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		for (int i = 0; i < numberOfVariables; i++)
			resUpdate.add(new ArrayList<ConstantToken<P1, F1, S1>>());

		SSTEpsilon<P1, F1, S1> newMove1 = new SSTEpsilon<P1, F1, S1>(initialState, sst1.initialState,
				new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
		transitions.add(newMove1);

		SSTEpsilon<P1, F1, S1> newMove2 = new SSTEpsilon<P1, F1, S1>(initialState, sst2.initialState + offSet,
				new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
		transitions.add(newMove2);

		// Make all states of the two machines final
		for (Integer state : sst1.getFinalStates())
			outputFunction.put(state, sst1.outputFunction.get(state).renameVars(varRenameSst1));

		for (Integer state : sst2.getFinalStates())
			outputFunction.put(state + offSet, sst2.outputFunction.get(state).renameVars(varRenameSst2));

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);
	}

	/**
	 * Iterates the SST
	 */
	public SST<P, F, S> star(BooleanAlgebraSubst<P, F, S> ba) {
		return computeStar(this, ba, false);
	}

	/**
	 * Left iterates the SST
	 */
	public SST<P, F, S> leftStar(BooleanAlgebraSubst<P, F, S> ba) {
		return computeStar(this, ba, true);
	}

	/**
	 * iterate of the sst
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> computeStar(SST<P1, F1, S1> sst, BooleanAlgebraSubst<P1, F1, S1> ba,
			boolean isLeftIter) {

		if (sst.isEmpty) {
			return getEpsilonSST(new LinkedList<ConstantToken<P1, F1, S1>>(), ba);
			// return getEmptySST(ba);
		}

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables;

		initialState = sst.maxStateId + 1;

		// xAcc will be the accumulating var
		int accId = sst.variableCount;
		SSTVariable<P1, F1, S1> xAcc = new SSTVariable<P1, F1, S1>(accId);

		numberOfVariables = accId + 1;

		// every transition must have maxId variable Updates
		for (SSTInputMove<P1, F1, S1> t : sst.getInputMovesFrom(sst.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.liftToNVars(numberOfVariables);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(xAcc);

			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(t.from, t.to, t.guard, variableUpdate);
			transitions.add(newMove);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst.getEpsilonMovesFrom(sst.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate.liftToNVars(numberOfVariables);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(xAcc);

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from, t.to, variableUpdate);
			transitions.add(newMove);
		}

		// add a transition from every final state q of sst to the new initial
		// state
		// state of sst2
		// with the update xAcc = xAcc F(q), and x=epsilon for everyone else
		for (Integer finStateSst : sst.getFinalStates()) {
			// Create the update xAcc = xAcc F(q) (reverse if left iter), and
			// x=epsilon for everyone else
			ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < numberOfVariables; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For last variable set xAcc := xAcc F(q) (reverse if left
				// iter)
				if (i == accId)
					if (isLeftIter) {
						updateList.addAll(sst.outputFunction.get(finStateSst).update);
						updateList.add(xAcc);
					} else {
						updateList.add(xAcc);
						updateList.addAll(sst.outputFunction.get(finStateSst).update);
					}
				resUpdate.add(updateList);
			}

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(finStateSst, initialState,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
			transitions.add(newMove);
		}

		// Create the update x=eps for all vars and xAcc = xAcc, for initial
		// state to old initial state
		ArrayList<List<ConstantToken<P1, F1, S1>>> initUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		for (int i = 0; i < numberOfVariables; i++) {
			List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
			if (i == accId)
				updateList.add(xAcc);
			initUpdate.add(updateList);
		}

		SSTEpsilon<P1, F1, S1> initMove = new SSTEpsilon<P1, F1, S1>(initialState, sst.initialState,
				new SimpleVariableUpdate<P1, F1, S1>(initUpdate));
		transitions.add(initMove);

		// Create the update x0=xAcc for output function in initialState
		List<ConstantToken<P1, F1, S1>> outUpdate = new ArrayList<ConstantToken<P1, F1, S1>>();
		outUpdate.add(xAcc);

		outputFunction.put(initialState, new OutputUpdate<P1, F1, S1>(outUpdate));

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);
	}

	/**
	 * shuffles pairs of ssts (li,ri) such that every li (ri) has domain equal
	 * to sfa
	 * 
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public static <P1, F1, S1> SST<P1, F1, S1> computeShuffle(
			Collection<Pair<SST<P1, F1, S1>, SST<P1, F1, S1>>> combinedSstPairsWitEps,
			BooleanAlgebraSubst<P1, F1, S1> ba, boolean isLeftShuffle) throws TimeoutException {

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables;
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();

		// Remove epsilon transitions from all ssts
		List<SST<P1, F1, S1>> combinedSsts = new ArrayList<SST<P1, F1, S1>>();
		for (Pair<SST<P1, F1, S1>, SST<P1, F1, S1>> sstPair : combinedSstPairsWitEps) {
			combinedSsts.add(sstPair.first.removeEpsilonMoves(ba));
			combinedSsts.add(sstPair.second.removeEpsilonMoves(ba));
		}

		// Create first stretch
		HashMap<List<Integer>, Integer> reached = new HashMap<List<Integer>, Integer>();
		LinkedList<List<Integer>> toVisit = new LinkedList<List<Integer>>();

		List<Integer> firstState = new ArrayList<Integer>();
		for (SST<P1, F1, S1> sst : combinedSsts)
			firstState.add(sst.initialState);

		initialState = 0;
		int initialStateMod = 0;
		Collection<Integer> finalStatesMod = new ArrayList<Integer>();
		Map<Integer, List<Integer>> stateToId = new HashMap<Integer, List<Integer>>();

		reached.put(firstState, initialStateMod);
		toVisit.add(firstState);

		// set of variables is the disjoint union of all the ssts
		List<Integer> varRenames = new ArrayList<Integer>();
		int offSet = 0;
		for (SST<P1, F1, S1> sst : combinedSsts) {
			varRenames.add(offSet);
			offSet += sst.variableCount;
		}
		// add two variables 1 for accumulating and 1 for buffering
		int buffId = offSet;
		int accId = buffId + combinedSstPairsWitEps.size();
		SSTVariable<P1, F1, S1> xAcc = new SSTVariable<P1, F1, S1>(accId);

		numberOfVariables = accId + 1;

		Collection<SSTMove<P1, F1, S1>> transitionFirstStretch = new ArrayList<SSTMove<P1, F1, S1>>();
		// Start composing them
		while (!toVisit.isEmpty()) {
			List<Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			// Assume that all ssts have same domain so check only state type of
			// first one
			boolean isFinalState = combinedSsts.get(0).isFinalState(currState.get(0));
			if (isFinalState) {
				finalStatesMod.add(currStateId);
				stateToId.put(currStateId, currState);
			}

			List<Pair<Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>, List<Integer>>> triples = new ArrayList<Pair<Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>, List<Integer>>>();
			accumulateMovesFromMultiSST(currState, combinedSsts, varRenames, 0, ba, ba.True(),
					new FunctionalVariableUpdate<P1, F1, S1>(), new ArrayList<Integer>(), triples);
			for (Pair<Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>, List<Integer>> triple : triples) {

				List<Integer> nextState = triple.second;
				int nextStateId = getStateId(nextState, reached, toVisit);

				// add buff:=buff and acc:=acc to updates
				FunctionalVariableUpdate<P1, F1, S1> finalUpdate = triple.first.second.liftToNVars(numberOfVariables);
				for (int i = 0; i < combinedSstPairsWitEps.size(); i++) {
					finalUpdate.variableUpdate.get(buffId).add(new SSTVariable<P1, F1, S1>(buffId + i));
				}
				finalUpdate.variableUpdate.get(accId).add(xAcc);

				SSTInputMove<P1, F1, S1> newTrans = new SSTInputMove<P1, F1, S1>(currStateId, nextStateId,
						triple.first.first, finalUpdate);

				transitionFirstStretch.add(newTrans);

			}
		}

		// After done with the first one make 3 copies of the states and
		// transitions shifted by stateCount
		int offset = reached.size();
		transitions = new ArrayList<SSTMove<P1, F1, S1>>(transitionFirstStretch);
		for (SSTMove<P1, F1, S1> t : transitionFirstStretch) {
			for (int i = 1; i < 3; i++) {
				SSTMove<P1, F1, S1> newTrans = (SSTMove<P1, F1, S1>) t.clone();
				newTrans.from = newTrans.from + i * offset;
				newTrans.to = newTrans.to + i * offset;
				transitions.add(newTrans);
			}
		}

		// Add eps transitions to tie it all together
		for (Integer finState : finalStatesMod) {
			List<Integer> sstStates = stateToId.get(finState);

			// From 1 to 2 do: xbufi=F(qi.1)
			ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate1 = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < numberOfVariables; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For buffer vars
				if (i >= buffId && i < accId) {
					// only take even position (i.e. the first els of all pairs)
					int sstIndex = (i - buffId) * 2;
					OutputUpdate<P1, F1, S1> out = combinedSsts.get(sstIndex).outputFunction
							.get(sstStates.get(sstIndex));
					updateList.addAll(out.renameVars(varRenames.get(sstIndex)).update);
				}
				resUpdate1.add(updateList);
			}

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(finState, initialState + offset,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate1));
			transitions.add(newMove);

			// From 2 to 3, 3 to 4 and 4 to 3 do: xbufi=F(qi.1) xacc = xacc
			// forall i xbufi F(qi.2)
			ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate2 = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < numberOfVariables; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For buffer vars
				if (i >= buffId && i < accId) {
					// only take even position (i.e. the first els of all pairs)
					int sstIndex = (i - buffId) * 2;
					OutputUpdate<P1, F1, S1> out = combinedSsts.get(sstIndex).outputFunction
							.get(sstStates.get(sstIndex));
					updateList.addAll(out.renameVars(varRenames.get(sstIndex)).update);
				} else {
					if (i == accId) {
						if (!isLeftShuffle)
							updateList.add(xAcc);
						for (int sstIndex = 0; sstIndex < combinedSstPairsWitEps.size(); sstIndex++) {
							int secondEl = sstIndex * 2 + 1;
							updateList.add(new SSTVariable<P1, F1, S1>(buffId + sstIndex));
							OutputUpdate<P1, F1, S1> out = combinedSsts.get(secondEl).outputFunction
									.get(sstStates.get(secondEl));
							updateList.addAll(out.renameVars(varRenames.get(secondEl)).update);
						}
						if (isLeftShuffle)
							updateList.add(xAcc);
					}
				}
				resUpdate2.add(updateList);
			}

			newMove = new SSTEpsilon<P1, F1, S1>(finState + offset, initialState + 2 * offset,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate2));
			transitions.add(newMove);
			newMove = new SSTEpsilon<P1, F1, S1>(finState + 2 * offset, initialState + 2 * offset,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate2));
			transitions.add(newMove);

			// Create output function x0=xacc forall i xbufi F(qi.2)
			List<ConstantToken<P1, F1, S1>> outputExpr = new ArrayList<ConstantToken<P1, F1, S1>>();
			if (!isLeftShuffle)
				outputExpr.add(xAcc);
			for (int sstIndex = 0; sstIndex < combinedSstPairsWitEps.size(); sstIndex++) {
				int secondEl = sstIndex * 2 + 1;
				outputExpr.add(new SSTVariable<P1, F1, S1>(buffId + sstIndex));
				OutputUpdate<P1, F1, S1> out = combinedSsts.get(secondEl).outputFunction.get(sstStates.get(secondEl));
				outputExpr.addAll(out.renameVars(varRenames.get(secondEl)).update);
			}
			if (isLeftShuffle)
				outputExpr.add(xAcc);

			outputFunction.put(finState + offset * 1, new OutputUpdate<P1, F1, S1>(outputExpr));
			outputFunction.put(finState + offset * 2, new OutputUpdate<P1, F1, S1>(outputExpr));
		}

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);

	}

	// Given the states of multiple ssts computes all the combined moves in
	// transitions
	private static <P1, F1, S1> void accumulateMovesFromMultiSST(List<Integer> currState, List<SST<P1, F1, S1>> ssts,
			List<Integer> varRenames, int currIndex, BooleanAlgebraSubst<P1, F1, S1> ba, P1 currPred,
			FunctionalVariableUpdate<P1, F1, S1> currVaribleUpdate, List<Integer> currNextState,
			List<Pair<Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>, List<Integer>>> transitions)
					throws TimeoutException {

		if (!ba.IsSatisfiable(currPred))
			return;

		if (currIndex == currState.size()) {
			Pair<P1, FunctionalVariableUpdate<P1, F1, S1>> pair = new Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>(
					currPred, currVaribleUpdate);
			Pair<Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>, List<Integer>> trip = new Pair<Pair<P1, FunctionalVariableUpdate<P1, F1, S1>>, List<Integer>>(
					pair, currNextState);
			transitions.add(trip);
			return;
		}

		int currVarRen = varRenames.get(currIndex);
		for (SSTInputMove<P1, F1, S1> move : ssts.get(currIndex).getInputMovesFrom(currState.get(currIndex))) {
			P1 newPred = ba.MkAnd(currPred, move.guard);
			FunctionalVariableUpdate<P1, F1, S1> newVaribleUpdate = FunctionalVariableUpdate.addUpdate(currVarRen,
					currVaribleUpdate, move.variableUpdate);

			List<Integer> newNextState = new ArrayList<Integer>(currNextState);
			newNextState.add(move.to);
			accumulateMovesFromMultiSST(currState, ssts, varRenames, currIndex + 1, ba, newPred, newVaribleUpdate,
					newNextState, transitions);
		}
	}

	/**
	 * shuffle
	 * 
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public static <P1, F1, S1> SST<P1, F1, S1> computeShuffle(SST<P1, F1, S1> sstUnchecked, SFA<P1, S1> autUnchecked,
			BooleanAlgebraSubst<P1, F1, S1> ba, boolean isLeftShuffle) throws TimeoutException {

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables;
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();

		// Remove epsilon transitions from all ssts
		SST<P1, F1, S1> sst = sstUnchecked.removeEpsilonMoves(ba);
		SFA<P1, S1> aut = autUnchecked.removeEpsilonMoves(ba);

		// Create first stretch
		HashMap<List<Integer>, Integer> reached = new HashMap<List<Integer>, Integer>();
		LinkedList<List<Integer>> toVisit = new LinkedList<List<Integer>>();

		// state is a triple (sst1 sst2 aut), sst is -1 if it hasn't started yet
		List<Integer> firstState = Arrays.asList(sst.initialState, -1, aut.getInitialState());

		initialState = 0;
		int initialStateMod = 0;

		// States in the product for which the aut entry is final
		Collection<Integer> finalStatesFirstStretch = new ArrayList<Integer>();
		Collection<Integer> finalStatesOtherStretches1 = new ArrayList<Integer>();
		Collection<Integer> finalStatesOtherStretches2 = new ArrayList<Integer>();
		Map<Integer, List<Integer>> idToState = new HashMap<Integer, List<Integer>>();

		reached.put(firstState, initialStateMod);
		toVisit.add(firstState);

		// add two variables 1 for accumulating and 1 for buffering
		int accId = sst.variableCount * 2;
		SSTVariable<P1, F1, S1> xAcc = new SSTVariable<P1, F1, S1>(accId);

		numberOfVariables = accId + 1;

		Collection<SSTMove<P1, F1, S1>> transitionFirstStretch = new ArrayList<SSTMove<P1, F1, S1>>();
		Collection<SSTMove<P1, F1, S1>> transitionOtherStretches = new ArrayList<SSTMove<P1, F1, S1>>();
		// Start composing them
		while (!toVisit.isEmpty()) {
			List<Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);
			idToState.put(currStateId, currState);

			int sst1state = currState.get(0);
			int sst2state = currState.get(1);
			int autstate = currState.get(2);

			// A state is final if aut is final. Keep separately those of the
			// first stretch
			boolean isFinalState = aut.getFinalStates().contains(autstate);
			if (isFinalState)
				if (sst2state == -1) {
					finalStatesFirstStretch.add(currStateId);

					// Create an initial state in the second machine so that I
					// will explore it later
					List<Integer> initStateStart = Arrays.asList(sst1state, sst.initialState, aut.getInitialState());
					getStateId(initStateStart, reached, toVisit);
				} else {
					if (sst.isFinalState(sst1state)) {
						finalStatesOtherStretches1.add(currStateId);
						List<Integer> initStateStart = Arrays.asList(sst.initialState, sst2state,
								aut.getInitialState());
						getStateId(initStateStart, reached, toVisit);
					} else {
						finalStatesOtherStretches2.add(currStateId);
						List<Integer> initStateStart = Arrays.asList(sst1state, sst.initialState,
								aut.getInitialState());
						getStateId(initStateStart, reached, toVisit);
					}
				}

			// Find product moves
			for (SSTInputMove<P1, F1, S1> sst1move : sst.getInputMovesFrom(sst1state))
				for (SFAInputMove<P1, S1> autmove : aut.getInputMovesFrom(autstate)) {

					P1 guard = ba.MkAnd(autmove.guard, sst1move.guard);
					if (ba.IsSatisfiable(guard)) {

						if (sst2state == -1) {
							// continue only with first machine
							List<Integer> nextState = Arrays.asList(sst1move.to, -1, autmove.to);

							int nextStateId = getStateId(nextState, reached, toVisit);

							// only update first vars
							FunctionalVariableUpdate<P1, F1, S1> newUpdate = sst1move.variableUpdate
									.liftToNVars(numberOfVariables);

							SSTInputMove<P1, F1, S1> newTrans = new SSTInputMove<P1, F1, S1>(currStateId, nextStateId,
									guard, newUpdate);

							transitionFirstStretch.add(newTrans);

						} else {
							// continue with both sst1 and sst2
							for (SSTInputMove<P1, F1, S1> sst2move : sst.getInputMovesFrom(sst2state)) {

								List<Integer> nextState = Arrays.asList(sst1move.to, sst2move.to, autmove.to);

								int nextStateId = getStateId(nextState, reached, toVisit);

								// add buff:=buff and acc:=acc to updates
								FunctionalVariableUpdate<P1, F1, S1> newUpdate = FunctionalVariableUpdate
										.combineUpdates(sst1move.variableUpdate, sst2move.variableUpdate)
										.liftToNVars(numberOfVariables);
								newUpdate.variableUpdate.get(accId).add(xAcc);

								SSTInputMove<P1, F1, S1> newTrans = new SSTInputMove<P1, F1, S1>(currStateId,
										nextStateId, guard, newUpdate);

								transitionOtherStretches.add(newTrans);
							}
						}

					}

				}
		}

		// After done with the first one make 4 total of the transitions shifted
		// by stateCount
		// copy 0 only contains transitions from -1 to -1
		int offset = reached.size();
		transitions = new ArrayList<SSTMove<P1, F1, S1>>(transitionFirstStretch);
		// Now copy other transitions 3 times
		for (SSTMove<P1, F1, S1> t : transitionOtherStretches) {
			for (int i = 1; i <= 4; i++) {
				SSTMove<P1, F1, S1> newTrans = (SSTMove<P1, F1, S1>) t.clone();
				newTrans.from = newTrans.from + i * offset;
				newTrans.to = newTrans.to + i * offset;
				transitions.add(newTrans);
			}
		}

		// from 0 to 1 add epsilon from all final states of first machine
		// create identity update
		{
			SimpleVariableUpdate<P1, F1, S1> idUpdate = SimpleVariableUpdate.identity(numberOfVariables);
			for (Integer finState : finalStatesFirstStretch) {
				List<Integer> listOfStates = idToState.get(finState);
				int sst1state = listOfStates.get(0);

				List<Integer> nextState = Arrays.asList(sst1state, sst.initialState, aut.getInitialState());
				int nextStateId = reached.get(nextState);

				SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(finState, nextStateId + offset, idUpdate);
				transitions.add(newMove);
			}
		}

		// case in which sst1 is in a final state
		for (Integer finState : finalStatesOtherStretches1) {
			List<Integer> listOfStates = idToState.get(finState);
			int sst1state = listOfStates.get(0);
			int sst2state = listOfStates.get(1);

			// Output expression on X1
			List<ConstantToken<P1, F1, S1>> outputExprFirst = new ArrayList<ConstantToken<P1, F1, S1>>();
			if (!isLeftShuffle)
				outputExprFirst.add(xAcc);
			outputExprFirst.addAll(sst.outputFunction.get(sst1state).update);
			if (isLeftShuffle)
				outputExprFirst.add(xAcc);

			// from 1 to 2, and 3 to 2: identity on X2, reset X1, xacc= xacc
			// F(X1)
			ArrayList<List<ConstantToken<P1, F1, S1>>> update1to2and3to2 = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < numberOfVariables; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For variables in x2
				if (i >= sst.variableCount && i < accId) {
					updateList.add(new SSTVariable<P1, F1, S1>(i));
				} else {
					// For xAcc:= xacc F(1)
					if (i == accId) {
						updateList = outputExprFirst;
					}
				}
				update1to2and3to2.add(updateList);
			}
			SimpleVariableUpdate<P1, F1, S1> svu1to2and3to2 = new SimpleVariableUpdate<P1, F1, S1>(update1to2and3to2);

			// next state
			List<Integer> nextState = Arrays.asList(sst.initialState, sst2state, aut.getInitialState());
			int nextStateId = reached.get(nextState);

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(finState + offset, nextStateId + 2 * offset,
					svu1to2and3to2);
			transitions.add(newMove);

			newMove = new SSTEpsilon<P1, F1, S1>(finState + 3 * offset, nextStateId + 2 * offset, svu1to2and3to2);
			transitions.add(newMove);

			// Output function on X1
			outputFunction.put(finState + offset, new OutputUpdate<P1, F1, S1>(outputExprFirst));
			outputFunction.put(finState + offset * 3, new OutputUpdate<P1, F1, S1>(outputExprFirst));
		}

		// case in which sst2 is in a final state
		for (Integer finState : finalStatesOtherStretches2) {
			List<Integer> listOfStates = idToState.get(finState);
			int sst1state = listOfStates.get(0);
			int sst2state = listOfStates.get(1);

			// Output expression on X2
			List<ConstantToken<P1, F1, S1>> outputExprSecond = new ArrayList<ConstantToken<P1, F1, S1>>();
			if (!isLeftShuffle)
				outputExprSecond.add(xAcc);
			// Output function on X2 (done via renaming vars)
			outputExprSecond.addAll(sst.outputFunction.get(sst2state).renameVars(sst.variableCount).update);
			if (isLeftShuffle)
				outputExprSecond.add(xAcc);

			// from 2 to 3: identity on X1, reset X2, xacc= xacc F(X2)
			ArrayList<List<ConstantToken<P1, F1, S1>>> update2to3 = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < numberOfVariables; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For variables in X1
				if (i < sst.variableCount) {
					updateList.add(new SSTVariable<P1, F1, S1>(i));
				} else {
					// For xAcc:= xacc F(2)
					if (i == accId) {
						updateList = outputExprSecond;
					}
				}
				update2to3.add(updateList);
			}
			SimpleVariableUpdate<P1, F1, S1> svu2to3 = new SimpleVariableUpdate<P1, F1, S1>(update2to3);

			// next state
			List<Integer> nextState = Arrays.asList(sst1state, sst.initialState, aut.getInitialState());
			int nextStateId = reached.get(nextState);

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(finState + 2 * offset, nextStateId + 3 * offset,
					svu2to3);
			transitions.add(newMove);

			// Output function on X2
			outputFunction.put(finState + offset * 2, new OutputUpdate<P1, F1, S1>(outputExprSecond));
		}

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);

	}

	/**
	 * Normalizes sst state names
	 * 
	 * @param ba
	 * @return
	 */
	public SST<P, F, S> normalize(BooleanAlgebraSubst<P, F, S> ba) {
		return normalize(this, ba);
	}

	/**
	 * Normalizes sst state names
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> normalize(SST<P1, F1, S1> sst, BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst.isEmpty)
			return getEmptySST(ba);

		Collection<SSTMove<P1, F1, S1>> transitions = new ArrayList<SSTMove<P1, F1, S1>>();
		Map<Integer, OutputUpdate<P1, F1, S1>> outputFunction = new HashMap<Integer, OutputUpdate<P1, F1, S1>>();
		Integer initialState;
		Integer numberOfVariables;

		initialState = 0;
		numberOfVariables = sst.variableCount;

		Map<Integer, Integer> stateRen = new HashMap<Integer, Integer>();
		for (int state : sst.states)
			stateRen.put(state, stateRen.size());

		// rename transitions
		for (SSTInputMove<P1, F1, S1> t : sst.getInputMovesFrom(sst.states)) {
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(stateRen.get(t.from), stateRen.get(t.to),
					t.guard, t.variableUpdate);
			transitions.add(newMove);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst.getEpsilonMovesFrom(sst.states)) {
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(stateRen.get(t.from), stateRen.get(t.to),
					t.variableUpdate);
			transitions.add(newMove);
		}

		// Rename output function
		for (int state : sst.getFinalStates())
			outputFunction.put(stateRen.get(state), sst.outputFunction.get(state));

		return MkSST(transitions, initialState, numberOfVariables, outputFunction, ba);
	}

	/**
	 * Computes the domain automaton of the sst
	 * 
	 * @throws TimeoutException
	 */
	public SFA<P, S> getDomain(BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();

		for (SSTInputMove<P, F, S> t : getInputMovesFrom(states))
			transitions.add(new SFAInputMove<P, S>(t.from, t.to, t.guard));

		for (SSTEpsilon<P, F, S> t : getEpsilonMovesFrom(states))
			transitions.add(new SFAEpsilon<P, S>(t.from, t.to));

		Collection<Integer> finalStates = getFinalStates();

		return SFA.MkSFA(transitions, initialState, finalStates, ba);
	}

	/**
	 * Computes the pre-image on the set outputNonMin
	 * 
	 * @throws TimeoutException
	 */
	public boolean typeCheck(SFA<P, S> inputNonMin, SFA<P, S> outputNonMin, BooleanAlgebraSubst<P, F, S> ba)
			throws TimeoutException {
		SFA<P, S> complement = outputNonMin.complement(ba);
		SFA<P, S> preim = SST.preImage(this, complement, ba);
		SFA<P, S> inters = preim.intersectionWith(inputNonMin, ba);

		return inters.isEmpty();
	}

	/**
	 * Computes the pre-image on the set outputNonMin
	 * 
	 * @throws TimeoutException
	 */
	public SFA<P, S> getPreImage(SFA<P, S> outputNonMin, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		return SST.preImage(this, outputNonMin, ba);
	}

	/**
	 * Computes the pre-image of sst on the set outputNonMin
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B, C> SFA<A, C> preImage(SST<A, B, C> sstWithEps, SFA<A, C> outputNonMin,
			BooleanAlgebraSubst<A, B, C> ba) throws TimeoutException {
		SFA<A, C> output = outputNonMin.minimize(ba);
		SST<A, B, C> sst = sstWithEps.removeEpsilonMoves(ba);

		Collection<SFAMove<A, C>> transitions = new ArrayList<SFAMove<A, C>>();
		Collection<Integer> finalStates = new HashSet<Integer>();
		Integer initialState = 0;

		// A state is a pair (q, f) where q is a state of the sst and f: X -> QO
		// -> QO is a function
		// mapping each variable x to a function from QO to QO (the
		// summarization)
		Map<Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>>, Integer> reached = new HashMap<Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>>, Integer>();
		LinkedList<Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>>> toVisit = new LinkedList<Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>>>();

		// The initial state is the identity for every variable
		HashMap<Integer, Integer> identityStateMap = new HashMap<Integer, Integer>();
		HashMap<Integer, HashMap<Integer, Integer>> identityMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		for (int stateId : output.getStates())
			identityStateMap.put(stateId, stateId);
		for (int varId = 0; varId < sst.variableCount; varId++)
			identityMap.put(varId, identityStateMap);

		Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>> initialStatePair = new Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>>(
				sst.initialState, identityMap);
		reached.put(initialStatePair, 0);
		toVisit.add(initialStatePair);

		// do a DFS and look for reachable states
		while (!toVisit.isEmpty()) {
			Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			int sstState = currState.first;
			HashMap<Integer, HashMap<Integer, Integer>> currFun = currState.second;

			// set final states to those for which the output func summarized on
			// initial state gives a final state of O
			if (sst.isFinalState(sstState)) {
				Integer fromqoOnOutputFunction = sst.outputFunction.get(sstState).getInitStateSummary(currFun, output,
						ba);
				if (fromqoOnOutputFunction != null && output.getFinalStates().contains(fromqoOnOutputFunction))
					finalStates.add(currStateId);
			}

			// For each move of the sst compute the next state
			for (SSTInputMove<A, B, C> t : sst.getInputMovesFrom(sstState)) {

				Collection<Pair<HashMap<Integer, HashMap<Integer, Integer>>, A>> nextFuns = t.variableUpdate
						.getNextSummary(currFun, t.guard, output, ba);
				for (Pair<HashMap<Integer, HashMap<Integer, Integer>>, A> pair : nextFuns) {

					Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nextState = new Pair<Integer, HashMap<Integer, HashMap<Integer, Integer>>>(
							t.to, pair.first);

					int nextStateId = getStateId(nextState, reached, toVisit);

					transitions.add(new SFAInputMove<A, C>(currStateId, nextStateId, pair.second));
				}
			}

		}

		return SFA.MkSFA(transitions, initialState, finalStates, ba);
	}

	// non-public methods

	protected Map<Integer, SimpleVariableUpdate<P, F, S>> getSSTEpsClosure(Integer fronteer, BooleanAlgebra<P, S> ba) {

		Map<Integer, SimpleVariableUpdate<P, F, S>> stateToAss = new HashMap<Integer, SimpleVariableUpdate<P, F, S>>();
		Collection<Integer> reached = new HashSet<Integer>(fronteer);
		LinkedList<Integer> toVisit = new LinkedList<Integer>();
		toVisit.add(fronteer);
		stateToAss.put(fronteer, identityVarUp());

		while (toVisit.size() > 0) {
			int fromState = toVisit.removeFirst();
			SimpleVariableUpdate<P, F, S> fromUpdate = stateToAss.get(fromState);
			for (SSTEpsilon<P, F, S> t : getEpsilonMovesFrom(fromState)) {
				if (!reached.contains(t.to)) {
					reached.add(t.to);
					toVisit.add(t.to);
					// this should be compose fromUpdate t.variableUpdate
					stateToAss.put(t.to, fromUpdate.composeWith(t.variableUpdate));
				} else {
					throw new IllegalArgumentException(
							"the epsilon transitions cause ambiguity (" + "their relation not a tree)");
				}
			}
		}
		return stateToAss;
	} // ACCESSORS

	/**
	 * Add Transition
	 * 
	 * @throws TimeoutException
	 */
	private void addTransition(SSTMove<P, F, S> transition, BooleanAlgebraSubst<P, F, S> ba, boolean skipSatCheck)
			throws TimeoutException {

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
				getEpsilonMovesFrom(transition.from).add((SSTEpsilon<P, F, S>) transition);
				getEpsilonMovesTo(transition.to).add((SSTEpsilon<P, F, S>) transition);
			} else {
				getInputMovesFrom(transition.from).add((SSTInputMove<P, F, S>) transition);
				getInputMovesTo(transition.to).add((SSTInputMove<P, F, S>) transition);
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
	public Collection<SSTInputMove<P, F, S>> getInputMovesFrom(Collection<Integer> stateSet) {
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
	public Collection<SSTInputMove<P, F, S>> getInputMovesTo(Collection<Integer> stateSet) {
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
	public Collection<SSTEpsilon<P, F, S>> getEpsilonMovesFrom(Collection<Integer> stateSet) {
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
	public Collection<SSTEpsilon<P, F, S>> getEpsilonMovesTo(Collection<Integer> stateSet) {
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
	public Collection<SSTMove<P, F, S>> getTransitionsFrom(Collection<Integer> stateSet) {
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
	public Collection<SSTMove<P, F, S>> getTransitionsTo(Collection<Integer> stateSet) {
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
		return outputFunction.keySet();
	}

	@Override
	public Integer getInitialState() {
		return initialState;
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
		cl.initialState = initialState;

		cl.transitionsFrom = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>(transitionsFrom);
		cl.transitionsTo = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>(transitionsTo);

		cl.epsTransitionsFrom = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>(epsTransitionsFrom);
		cl.epsTransitionsTo = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>(epsTransitionsTo);

		cl.outputFunction = new HashMap<Integer, OutputUpdate<P, F, S>>(outputFunction);

		return cl;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());

		sb.append("Output Function \n");
		for (int st : outputFunction.keySet()) {
			sb.append("F(" + st + ")=" + outputFunction.get(st));
		}

		return sb.toString();
	}
}

/**
 * 
 */
package transducers.sst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theory.BooleanAlgebra;
import theory.BooleanAlgebraSubst;
import utilities.Pair;
import automata.AutomataException;
import automata.Automaton;
import automata.Move;
import automata.fsa.Epsilon;
import automata.fsa.InputMove;
import automata.fsa.SFA;
import automata.fsa.SFAMove;

public class SST<P, F, S> extends Automaton<P, S> {

	// SST properties
	protected Collection<Integer> states;
	protected Integer initialState;

	protected Map<String, Integer> variablesToIndices;
	protected SimpleVariableUpdate<P, F, S> cachedIdentityVarUp;

	// moves the output to the variable in position 0
	protected Map<Integer, SimpleVariableUpdate<P, F, S>> outputFunction;

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

	public Collection<String> getVariableNames() {
		return variablesToIndices.keySet();
	}

	public SimpleVariableUpdate<P, F, S> identityVarUp() {
		if (cachedIdentityVarUp != null)
			return cachedIdentityVarUp;

		ArrayList<List<ConstantToken<P, F, S>>> identityVariableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>();

		for (int i = 0; i < variablesToIndices.size(); i++) {
			for (String var : getVariableNames()) {
				if (variablesToIndices.get(var) == i)
					identityVariableUpdate.add(getVarForStr(var));
			}
		}

		cachedIdentityVarUp = new SimpleVariableUpdate<P, F, S>(
				identityVariableUpdate);
		return cachedIdentityVarUp;
	}

	protected SST() {
		super();
		states = new HashSet<Integer>();
		variablesToIndices = new HashMap<String, Integer>();
		outputFunction = new HashMap<Integer, SimpleVariableUpdate<P, F, S>>();
		transitionsFrom = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>();
		transitionsTo = new HashMap<Integer, Collection<SSTInputMove<P, F, S>>>();
		epsTransitionsFrom = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>();
		epsTransitionsTo = new HashMap<Integer, Collection<SSTEpsilon<P, F, S>>>();
		outputFunction = new HashMap<Integer, SimpleVariableUpdate<P, F, S>>();
		maxStateId = 0;
		initialState = 0;
	}

	/*
	 * Create an automaton (removes unreachable states)
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> MkSST(
			Collection<SSTMove<P1, F1, S1>> transitions, Integer initialState,
			String[] variables,
			Map<Integer, SimpleVariableUpdate<P1, F1, S1>> outputFunction,
			BooleanAlgebraSubst<P1, F1, S1> ba) throws AutomataException {

		SST<P1, F1, S1> aut = new SST<P1, F1, S1>();

		// Initialize state set
		aut.initialState = initialState;
		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		Collection<Integer> finalStates = outputFunction.keySet();
		aut.states.addAll(finalStates);

		aut.outputFunction = outputFunction;

		int index = 0;
		ArrayList<List<ConstantToken<P1, F1, S1>>> identityVariableUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		for (String var : variables) {
			aut.variablesToIndices.put(var, index);
			identityVariableUpdate.add(aut.getVarForStr(var));
			index++;
		}

		aut.cachedIdentityVarUp = new SimpleVariableUpdate<P1, F1, S1>(
				identityVariableUpdate);

		for (SSTMove<P1, F1, S1> t : transitions)
			aut.addTransition(t, ba, false);

		// cleanup set isEmpty
		// TODO
		// aut = removeUnreachableStates(aut, ba);

		return aut;
	}

	private List<ConstantToken<P, F, S>> getVarForStr(String s) {
		List<ConstantToken<P, F, S>> idVar = new ArrayList<ConstantToken<P, F, S>>();
		idVar.add(new StringVariable<P, F, S>(s));
		return idVar;
	}

	/**
	 * Returns the empty SST
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> getEmptySST(
			BooleanAlgebraSubst<P1, F1, S1> ba) {
		SST<P1, F1, S1> aut = new SST<P1, F1, S1>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.initialState = 0;
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
	 * Computes one of the ouptuts produced when reading input. Null if no such
	 * output exists
	 * 
	 * @param input
	 * @param ba
	 * @return one output sequence, null if undefined
	 */
	public static <P1, F1, S1> List<S1> outputOn(SST<P1, F1, S1> sstWithEps,
			List<S1> input, BooleanAlgebraSubst<P1, F1, S1> ba) {

		// composition
		SST<P1, F1, S1> sst = sstWithEps.removeEpsilonMoves(ba);
		// Assume that there are no epsilon transitions for now

		Map<Integer, Collection<VariableAssignment<S1>>> currConf = new HashMap<Integer, Collection<VariableAssignment<S1>>>();

		List<VariableAssignment<S1>> ini = new ArrayList<VariableAssignment<S1>>();
		ini.add(VariableAssignment.MkInitialValue(
				sst.variablesToIndices.size(), ba));
		currConf.put(sst.initialState, ini);

		for (S1 el : input)
			currConf = sst.getNextConfig(currConf, el, ba);

		for (int state : currConf.keySet()) {
			if (sst.isFinalState(state)) {
				Collection<VariableAssignment<S1>> varVals = currConf
						.get(state);
				for (VariableAssignment<S1> assignment : varVals) {
					// apply outputFunction
					SimpleVariableUpdate<P1, F1, S1> outputUpdate = sst.outputFunction
							.get(state);
					VariableAssignment<S1> v1 = outputUpdate.applyTo(
							assignment, sst.variablesToIndices, ba);
					return v1.outputVariableValue();
				}
			}
		}

		return null;
	}

	// Makes one step on the current config and symbol in the sst
	private Map<Integer, Collection<VariableAssignment<S>>> getNextConfig(
			Map<Integer, Collection<VariableAssignment<S>>> currConfig,
			S input, BooleanAlgebraSubst<P, F, S> ba) {

		Map<Integer, Collection<VariableAssignment<S>>> newConfig = new HashMap<Integer, Collection<VariableAssignment<S>>>();

		for (int state : currConfig.keySet()) {
			Collection<VariableAssignment<S>> sourceAssignments = currConfig
					.get(state);
			for (SSTInputMove<P, F, S> move : getInputMovesFrom(state))
				if (move.hasModel(input, ba)) {
					Collection<VariableAssignment<S>> targetAssignments = new ArrayList<VariableAssignment<S>>();
					if (newConfig.containsKey(move.to))
						targetAssignments = newConfig.get(move.to);
					else
						newConfig.put(move.to, targetAssignments);

					for (VariableAssignment<S> assig : sourceAssignments)
						targetAssignments.add(move.variableUpdate.applyTo(
								assig, variablesToIndices, input, ba));
				}
		}
		return newConfig;
	}

	/**
	 * Computes the combination with <code>sst</code> as a new SST
	 * combine(w)=f1(w)f2(w)
	 */
	public SST<P, F, S> combineWith(SST<P, F, S> sst,
			BooleanAlgebraSubst<P, F, S> ba) {
		return combine(this, sst, ba);
	}

	/**
	 * Computes the combination of <code>sst1</code> and <code>sst2</code>
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> combine(
			SST<P1, F1, S1> sst1withEps, SST<P1, F1, S1> sst2withEps,
			BooleanAlgebraSubst<P1, F1, S1> ba) {

		// Remove epsilons
		SST<P1, F1, S1> sst1 = sst1withEps.removeEpsilonMoves(ba);
		SST<P1, F1, S1> sst2 = sst2withEps.removeEpsilonMoves(ba);

		SST<P1, F1, S1> combined = new SST<P1, F1, S1>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial state
		int totStates = 1;
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(
				sst1.initialState, sst2.initialState);
		combined.initialState = 0;
		combined.states.add(combined.initialState);

		reached.put(p, combined.initialState);
		toVisit.add(p);

		// Combined has set of variables the disjoint union of the two sets
		HashMap<String, String> varRenameSst1 = new HashMap<String, String>();
		HashMap<String, String> varRenameSst2 = new HashMap<String, String>();
		int ind = 0;
		for (String var1 : sst1.variablesToIndices.keySet()) {
			String newVarName = "x" + ind;
			varRenameSst1.put(var1, newVarName);
			combined.variablesToIndices.put(newVarName, ind);
			ind++;
		}
		for (String var2 : sst2.variablesToIndices.keySet()) {
			String newVarName = "x" + ind;
			varRenameSst2.put(var2, newVarName);
			combined.variablesToIndices.put(newVarName, ind);
			ind++;
		}

		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			// If both states are final, combine is final
			if (sst1.isFinalState(currState.first)
					&& sst2.isFinalState(currState.second)) {

				// new output function x = x1x2
				SimpleVariableUpdate<P1, F1, S1> outputUpdate = SimpleVariableUpdate
						.combineOutputUpdates(varRenameSst1, varRenameSst2,
								sst1.outputFunction.get(currState.first),
								sst2.outputFunction.get(currState.second));
				combined.outputFunction.put(currStateId, outputUpdate);
			}

			for (SSTInputMove<P1, F1, S1> t1 : sst1
					.getInputMovesFrom(currState.first))
				for (SSTInputMove<P1, F1, S1> t2 : sst2
						.getInputMovesFrom(currState.second)) {
					P1 intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {

						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
								t1.to, t2.to);
						int nextStateId = 0;

						if (!reached.containsKey(nextState)) {
							combined.transitionsTo.put(totStates,
									new HashSet<SSTInputMove<P1, F1, S1>>());
							reached.put(nextState, totStates);
							toVisit.add(nextState);
							combined.states.add(totStates);
							nextStateId = totStates;
							totStates++;
						} else
							nextStateId = reached.get(nextState);

						// combines two updadate by taking the disjoint union
						FunctionalVariableUpdate<P1, F1, S1> combinedUpdate = FunctionalVariableUpdate
								.combineUpdates(varRenameSst1, varRenameSst2,
										t1.variableUpdate, t2.variableUpdate);
						SSTInputMove<P1, F1, S1> newTrans = new SSTInputMove<P1, F1, S1>(
								currStateId, nextStateId, intersGuard,
								combinedUpdate);

						combined.addTransition(newTrans, ba, true);
					}
				}
		}

		// TODO remove unreachable states
		// combined = removeUnreachableStates(combined, ba);
		return combined;
	}

	/**
	 * return an equivalent copy without epsilon moves
	 */
	public SST<P, F, S> removeEpsilonMoves(BooleanAlgebraSubst<P, F, S> ba) {
		return removeEpsilonMovesFrom(this, ba);
	}

	/**
	 * TODO implement return an equivalent copy without epsilon moves
	 */
	protected static <P1, F1, S1> SST<P1, F1, S1> removeEpsilonMovesFrom(
			SST<P1, F1, S1> sst, BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst.isEpsilonFree)
			return sst;

		SST<P1, F1, S1> epsFree = new SST<P1, F1, S1>();

		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		HashMap<Integer, Map<Integer, SimpleVariableUpdate<P1, F1, S1>>> statesAss = new HashMap<Integer, Map<Integer, SimpleVariableUpdate<P1, F1, S1>>>();
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// Add initial state
		Map<Integer, SimpleVariableUpdate<P1, F1, S1>> epsclInitial = sst
				.getSSTEpsClosure(sst.initialState, ba);
		Collection<Integer> p = epsclInitial.keySet();
		epsFree.initialState = 0;
		epsFree.states.add(epsFree.initialState);
		statesAss.put(epsFree.initialState, epsclInitial);
		epsFree.variablesToIndices = sst.variablesToIndices;

		reachedStates.put(p, epsFree.initialState);
		toVisitStates.add(p);

		while (!toVisitStates.isEmpty()) {
			Collection<Integer> currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);
			Map<Integer, SimpleVariableUpdate<P1, F1, S1>> stateToAss = statesAss
					.get(currStateId);

			// set final state
			Integer fin = null;
			for (Integer st : currState) {
				if (sst.isFinalState(st))
					if (fin != null) {
						throw new IllegalArgumentException(
								"two different final states are reachable via epsilon;");
					} else {
						fin = st;
					}
			}
			// set output state if one of the esp closure state is final
			if (fin != null) {
				epsFree.outputFunction.put(
						currStateId,
						stateToAss.get(fin).composeWith(
								sst.outputFunction.get(fin),
								sst.variablesToIndices));
			}

			for (SSTInputMove<P1, F1, S1> t1 : sst.getInputMovesFrom(currState)) {

				Map<Integer, SimpleVariableUpdate<P1, F1, S1>> epsClosure = sst
						.getSSTEpsClosure(t1.to, ba);
				Collection<Integer> nextState = epsClosure.keySet();

				int nextStateId = 0;
				if (!reachedStates.containsKey(nextState)) {
					int index = reachedStates.size();
					reachedStates.put(nextState, index);
					toVisitStates.add(nextState);
					statesAss.put(index, epsClosure);
					epsFree.states.add(index);
					nextStateId = index;
				} else {
					nextStateId = reachedStates.get(nextState);
				}

				@SuppressWarnings("unchecked")
				SSTInputMove<P1, F1, S1> tnew = (SSTInputMove<P1, F1, S1>) t1
						.clone();
				tnew.from = currStateId;
				tnew.to = nextStateId;
				// TODO this should be compose stateToAss(t1.from)
				// t1.variableUpdate
				tnew.variableUpdate = stateToAss.get(t1.from).composeWith(
						t1.variableUpdate, sst.variablesToIndices);

				epsFree.addTransition(tnew, ba, true);
			}

		}

		epsFree.isEpsilonFree = true;
		return epsFree;
	}

	/**
	 * concatenate with sst
	 */
	public SST<P, F, S> concatenateWith(SST<P, F, S> sst,
			BooleanAlgebraSubst<P, F, S> ba) {
		return concatenate(this, sst, ba);
	}

	/**
	 * concatenates sst1 with sst2
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> concatenate(
			SST<P1, F1, S1> sst1, SST<P1, F1, S1> sst2,
			BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst1.isEmpty || sst2.isEmpty)
			return getEmptySST(ba);

		SST<P1, F1, S1> concat = new SST<P1, F1, S1>();
		concat.isEmpty = false;

		int offSet = sst1.maxStateId + 1;
		concat.maxStateId = sst2.maxStateId + offSet;

		for (Integer state : sst1.states)
			concat.states.add(state);

		for (Integer state : sst2.states)
			concat.states.add(state + offSet);

		concat.initialState = sst1.initialState;

		// set variable renames for the two ssts, they will share names
		HashMap<String, String> varRenameSst1 = new HashMap<String, String>();
		HashMap<String, String> varRenameSst2 = new HashMap<String, String>();
		int ind1 = 0;
		for (String var1 : sst1.variablesToIndices.keySet()) {
			String newVarName = "x" + ind1;
			varRenameSst1.put(var1, newVarName);
			concat.variablesToIndices.put(newVarName, ind1);
			ind1++;
		}
		// Reset indices since we want only Max(X1,X2) variables
		int ind2 = 0;
		for (String var2 : sst2.variablesToIndices.keySet()) {
			String newVarName = "x" + ind2;
			varRenameSst2.put(var2, newVarName);
			concat.variablesToIndices.put(newVarName, ind2);
			ind2++;
		}

		// xAcc will be the accumulation variable for the first machine
		// whenever we leave from sst1 to sst2 we update x0 to output of sst1
		int accId = Math.max(ind1, ind2);
		String xAcc = "x" + accId;
		concat.variablesToIndices.put(xAcc, accId);

		int totVars = accId + 1;

		// every transition must have maxId variable Updates
		for (SSTInputMove<P1, F1, S1> t : sst1.getInputMovesFrom(sst1.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst1).liftToNVars(totVars);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(
					t.from, t.to, t.guard, variableUpdate);
			concat.addTransition(newMove, ba, true);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst1.getEpsilonMovesFrom(sst1.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst1).liftToNVars(totVars);
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from,
					t.to, variableUpdate);
			concat.addTransition(newMove, ba, true);
		}

		// Moreover transitions in sst2 should perform the update xAcc:=xAcc
		// Each state should also take into account the offset
		for (SSTInputMove<P1, F1, S1> t : sst2.getInputMovesFrom(sst2.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst2).liftToNVars(totVars);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(
					new StringVariable<P1, F1, S1>(xAcc));
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(
					t.from + offSet, t.to + offSet, t.guard, variableUpdate);
			concat.addTransition(newMove, ba, true);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst2.getEpsilonMovesFrom(sst2.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst2).liftToNVars(totVars);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(
					new StringVariable<P1, F1, S1>(xAcc));

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from
					+ offSet, t.to + offSet, variableUpdate);
			concat.addTransition(newMove, ba, true);
		}

		// add a transition from every final state q of sst1 to the initial
		// state of sst2
		// with the update xAcc = F(q), and x=epsilon for everyone else
		for (Integer finStateSst1 : sst1.getFinalStates()) {
			// Create the update xAcc = F(q), and x=epsilon for everyone else
			ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < totVars; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For last variable set xAcc := F(q)
				if (i == accId)
					updateList = sst1.outputFunction.get(finStateSst1)
							.renameVars(varRenameSst1)
							.getOutputVariableUpdate();
				resUpdate.add(updateList);
			}

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(
					finStateSst1, sst2.initialState + offSet,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
			concat.addTransition(newMove, ba, true);
		}

		// create output function for sst2 so that it outputs xAcc F(q)
		for (Integer finStateSst2 : sst2.getFinalStates()) {
			// Create the update x0 = xAcc F(q), and x=epsilon for everyone else
			ArrayList<List<ConstantToken<P1, F1, S1>>> outUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < totVars; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For first variable set xAcc := xAcc F(q)
				if (i == 0) {
					updateList.add(new StringVariable<P1, F1, S1>(xAcc));
					updateList.addAll(sst2.outputFunction.get(finStateSst2)
							.renameVars(varRenameSst2)
							.getOutputVariableUpdate());
				}
				outUpdate.add(updateList);
			}

			concat.outputFunction.put(finStateSst2 + offSet,
					new SimpleVariableUpdate<P1, F1, S1>(outUpdate));
		}

		return concat;
	}

	/**
	 * Computes the union with <code>sst1</code> as a new SST
	 */
	public SST<P, F, S> unionWith(SST<P, F, S> sst1,
			BooleanAlgebraSubst<P, F, S> ba) {
		return union(this, sst1, ba);
	}

	/**
	 * Computes the union of <code>sst1</code> and <code>sst2</code> as a new
	 * SST
	 */
	@SuppressWarnings("unchecked")
	public static <P1, F1, S1> SST<P1, F1, S1> union(SST<P1, F1, S1> sst1,
			SST<P1, F1, S1> sst2, BooleanAlgebraSubst<P1, F1, S1> ba) {

		if (sst1.isEmpty && sst2.isEmpty)
			return getEmptySST(ba);
		if (sst1.isEmpty)
			return (SST<P1, F1, S1>) sst2.clone();
		if (sst2.isEmpty)
			return (SST<P1, F1, S1>) sst1.clone();

		SST<P1, F1, S1> union = new SST<P1, F1, S1>();
		union.isEmpty = false;

		int offSet = sst1.maxStateId + 2;
		union.maxStateId = sst2.maxStateId + offSet + 1;

		for (Integer state : sst1.states)
			union.states.add(state);

		for (Integer state : sst2.states)
			union.states.add(state + offSet);

		Integer initState = union.maxStateId;
		union.initialState = initState;
		union.states.add(initState);

		// set variable renames for the two ssts, they will share names
		HashMap<String, String> varRenameSst1 = new HashMap<String, String>();
		HashMap<String, String> varRenameSst2 = new HashMap<String, String>();
		int ind1 = 0;
		for (String var1 : sst1.variablesToIndices.keySet()) {
			String newVarName = "x" + ind1;
			varRenameSst1.put(var1, newVarName);
			union.variablesToIndices.put(newVarName, ind1);
			ind1++;
		}
		// Reset indices since we want only Max(X1,X2) variables
		int ind2 = 0;
		for (String var2 : sst2.variablesToIndices.keySet()) {
			String newVarName = "x" + ind2;
			varRenameSst2.put(var2, newVarName);
			union.variablesToIndices.put(newVarName, ind2);
			ind2++;
		}
		int totVars = Math.max(ind1, ind2);

		// every transition must have maxId variable Updates
		for (SSTInputMove<P1, F1, S1> t : sst1.getInputMovesFrom(sst1.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst1).liftToNVars(totVars);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(
					t.from, t.to, t.guard, variableUpdate);
			union.addTransition(newMove, ba, true);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst1.getEpsilonMovesFrom(sst1.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst1).liftToNVars(totVars);
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from,
					t.to, variableUpdate);
			union.addTransition(newMove, ba, true);
		}
		// Moreover transitions in sst2 should perform the update xAcc:=xAcc
		// Each state should also take into account the offset
		for (SSTInputMove<P1, F1, S1> t : sst2.getInputMovesFrom(sst2.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst2).liftToNVars(totVars);
			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(
					t.from + offSet, t.to + offSet, t.guard, variableUpdate);
			union.addTransition(newMove, ba, true);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst2.getEpsilonMovesFrom(sst2.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRenameSst2).liftToNVars(totVars);
			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from
					+ offSet, t.to + offSet, variableUpdate);
			union.addTransition(newMove, ba, true);
		}

		// Add transitions from new initial state to old initial states
		// Create the update x=epsilon for every var
		ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		for (int i = 0; i < totVars; i++)
			resUpdate.add(new ArrayList<ConstantToken<P1, F1, S1>>());
				
		SSTEpsilon<P1, F1, S1> newMove1 = new SSTEpsilon<P1, F1, S1>(
				initState, sst1.initialState,
				new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
		union.addTransition(newMove1, ba, true);
		SSTEpsilon<P1, F1, S1> newMove2 = new SSTEpsilon<P1, F1, S1>(
				initState, sst2.initialState + offSet,
				new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
		union.addTransition(newMove2, ba, true);

		// Make all states of the two machines final
		for (Integer state : sst1.getFinalStates())
			union.outputFunction.put(state, sst1.outputFunction.get(state).renameVars(varRenameSst1));

		for (Integer state : sst2.getFinalStates())
			union.outputFunction.put(state+offSet, sst2.outputFunction.get(state).renameVars(varRenameSst2));

		return union;
	}

	/**
	 * concatenate with sst
	 */
	public SST<P, F, S> star(BooleanAlgebraSubst<P, F, S> ba) {
		return computeStar(this, ba, false);
	}

	/**
	 * concatenate with sst
	 */
	public SST<P, F, S> leftStar(BooleanAlgebraSubst<P, F, S> ba) {
		return computeStar(this, ba, true);
	}

	/**
	 * iterate of the sst
	 */
	public static <P1, F1, S1> SST<P1, F1, S1> computeStar(SST<P1, F1, S1> sst,
			BooleanAlgebraSubst<P1, F1, S1> ba, boolean isLeftIter) {

		if (sst.isEmpty)
			return getEmptySST(ba);

		SST<P1, F1, S1> star = new SST<P1, F1, S1>();
		star.isEmpty = false;

		star.states = new HashSet<Integer>(sst.states);
		Integer initState = sst.maxStateId + 1;

		star.initialState = initState;

		// set variable renames for the two ssts, they will share names
		HashMap<String, String> varRename = new HashMap<String, String>();
		int ind = 0;
		for (String var1 : sst.variablesToIndices.keySet()) {
			String newVarName = "x" + ind;
			varRename.put(var1, newVarName);
			star.variablesToIndices.put(newVarName, ind);
			ind++;
		}

		// xAcc will be the accumulating var
		int accId = ind;
		String xAcc = "x" + accId;
		star.variablesToIndices.put(xAcc, accId);
		int totVars = accId + 1;

		// every transition must have maxId variable Updates
		for (SSTInputMove<P1, F1, S1> t : sst.getInputMovesFrom(sst.states)) {
			FunctionalVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRename).liftToNVars(totVars);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(
					new StringVariable<P1, F1, S1>(xAcc));

			SSTInputMove<P1, F1, S1> newMove = new SSTInputMove<P1, F1, S1>(
					t.from, t.to, t.guard, variableUpdate);
			star.addTransition(newMove, ba, true);
		}
		for (SSTEpsilon<P1, F1, S1> t : sst.getEpsilonMovesFrom(sst.states)) {
			SimpleVariableUpdate<P1, F1, S1> variableUpdate = t.variableUpdate
					.renameVars(varRename).liftToNVars(totVars);
			// For last variable set xAcc := xAcc
			variableUpdate.variableUpdate.get(accId).add(
					new StringVariable<P1, F1, S1>(xAcc));

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(t.from,
					t.to, variableUpdate);
			star.addTransition(newMove, ba, true);
		}

		// add a transition from every final state q of to the new initial state
		// state of sst2
		// with the update xAcc = F(q), and x=epsilon for everyone else
		for (Integer finStateSst : sst.getFinalStates()) {
			// Create the update xAcc = xAcc F(q) (reverse if left iter), and
			// x=epsilon for everyone else
			ArrayList<List<ConstantToken<P1, F1, S1>>> resUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
			for (int i = 0; i < totVars; i++) {
				List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
				// For last variable set xAcc := xAcc F(q) (reverse if left
				// iter)
				if (i == accId)
					if (isLeftIter) {
						updateList.addAll(sst.outputFunction.get(finStateSst)
								.renameVars(varRename)
								.getOutputVariableUpdate());
						updateList.add(new StringVariable<P1, F1, S1>(xAcc));
					} else {
						updateList.add(new StringVariable<P1, F1, S1>(xAcc));
						updateList.addAll(sst.outputFunction.get(finStateSst)
								.renameVars(varRename)
								.getOutputVariableUpdate());
					}
				resUpdate.add(updateList);
			}

			SSTEpsilon<P1, F1, S1> newMove = new SSTEpsilon<P1, F1, S1>(
					finStateSst, initState,
					new SimpleVariableUpdate<P1, F1, S1>(resUpdate));
			star.addTransition(newMove, ba, true);
		}

		// Create the update x=eps for all vars and xAcc = xAcc, for initial
		// state to old initial state
		ArrayList<List<ConstantToken<P1, F1, S1>>> initUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		for (int i = 0; i < totVars; i++) {
			List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
			if (i == accId)
				updateList.add(new StringVariable<P1, F1, S1>(xAcc));
			initUpdate.add(updateList);
		}

		SSTEpsilon<P1, F1, S1> initMove = new SSTEpsilon<P1, F1, S1>(initState,
				sst.initialState, new SimpleVariableUpdate<P1, F1, S1>(
						initUpdate));
		star.addTransition(initMove, ba, true);

		// Create the update x0=xAcc for output function in initstate
		ArrayList<List<ConstantToken<P1, F1, S1>>> outUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		for (int i = 0; i < totVars; i++) {
			List<ConstantToken<P1, F1, S1>> updateList = new ArrayList<ConstantToken<P1, F1, S1>>();
			if (i == 0)
				updateList.add(new StringVariable<P1, F1, S1>(xAcc));
			outUpdate.add(updateList);
		}
		star.outputFunction.put(initState,
				new SimpleVariableUpdate<P1, F1, S1>(outUpdate));

		return star;
	}

	/**
	 * Computes the domain automaton of the sst
	 * 
	 * @throws AutomataException
	 */
	public SFA<P, S> getDomain(BooleanAlgebraSubst<P, F, S> ba)
			throws AutomataException {
		Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();

		for (SSTInputMove<P, F, S> t : getInputMovesFrom(states))
			transitions.add(new InputMove<P, S>(t.from, t.to, t.guard));

		for (SSTEpsilon<P, F, S> t : getEpsilonMovesFrom(states))
			transitions.add(new Epsilon<P, S>(t.from, t.to));

		Collection<Integer> finalStates = getFinalStates();

		return SFA.MkSFA(transitions, initialState, finalStates, ba);
	}

	protected Map<Integer, SimpleVariableUpdate<P, F, S>> getSSTEpsClosure(
			Integer fronteer, BooleanAlgebra<P, S> ba) {

		Map<Integer, SimpleVariableUpdate<P, F, S>> stateToAss = new HashMap<Integer, SimpleVariableUpdate<P, F, S>>();
		Collection<Integer> reached = new HashSet<Integer>(fronteer);
		LinkedList<Integer> toVisit = new LinkedList<Integer>();
		toVisit.add(fronteer);
		stateToAss.put(fronteer, identityVarUp());

		while (toVisit.size() > 0) {
			int fromState = toVisit.removeFirst();
			SimpleVariableUpdate<P, F, S> fromUpdate = stateToAss
					.get(fromState);
			for (SSTEpsilon<P, F, S> t : getEpsilonMovesFrom(fromState)) {
				if (!reached.contains(t.to)) {
					reached.add(t.to);
					toVisit.add(t.to);
					// this should be compose fromUpdate t.variableUpdate
					stateToAss.put(t.to, fromUpdate.composeWith(
							t.variableUpdate, variablesToIndices));
				} else {
					throw new IllegalArgumentException(
							"the epsilon transitions cause ambiguity ("
									+ "their relation not a tree)");
				}
			}
		}
		return stateToAss;
	} // ACCESSORS

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
		return outputFunction.keySet();
	}

	@Override
	public Collection<Integer> getInitialStates() {
		Collection<Integer> is = new HashSet<Integer>();
		is.add(initialState);
		return is;
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

		cl.variablesToIndices = new HashMap<String, Integer>(variablesToIndices);

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

package automata.svpa;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import automata.svpa.TaggedSymbol.SymbolTag;
import theory.BooleanAlgebra;
import utilities.Pair;
import utilities.Quadruple;

public abstract class VPAutomaton<P, S> {

	public boolean isEmpty;
	public boolean isDeterministic;
	public boolean isEpsilonFree;
	public boolean isTotal;

	public VPAutomaton() {
		isEmpty = false;
		isDeterministic = false;
		isEpsilonFree = true;
		isTotal = false;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<P, S>> getMoves() {
		return getMovesFrom(getStates());
	}

	/**
	 * Set of moves from state
	 */
	public abstract Collection<SVPAMove<P, S>> getMovesFrom(Integer state);

	/**
	 * Set of moves from set of states
	 */
	public Collection<SVPAMove<P, S>> getMovesFrom(Collection<Integer> states) {
		Collection<SVPAMove<P, S>> transitions = new LinkedList<SVPAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getMovesFrom(state));
		return transitions;
	}

	/**
	 * Set of moves to state
	 */
	public abstract Collection<SVPAMove<P, S>> getMovesTo(Integer state);

	/**
	 * Set of moves to set of states
	 */
	public Collection<SVPAMove<P, S>> getMovesTo(Collection<Integer> states) {
		Collection<SVPAMove<P, S>> transitions = new LinkedList<SVPAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(Pair<Integer, Integer> state);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(Pair<Integer, Integer> state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(Integer state, Integer stackState);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(Integer state, Integer stackState);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(Integer state);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions starting in a state in
	 * <code>stateSet</code> with stack state <code>stackState</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(Collection<Integer> stateSet, Integer stackState);

	/**
	 * Returns the set of return transitions to a state in <code>stateSet</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(Collection<Integer> stateSet);

	/**
	 * Returns the set of call transitions starting a state <code>state</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(Integer state);

	/**
	 * Returns the set of call transitions starting in state <code>state</code>
	 * with stack state <code>stackState</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(Integer state, Integer stackState);

	/**
	 * Returns the set of call transitions starting in a state in
	 * <code>stateSet</code> with stack state <code>stackState</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(Collection<Integer> stateSet, Integer stackState);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Call<P, S>> getCallsTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<Call<P, S>> getCallsTo(Collection<Integer> stateSet) {
		Collection<Call<P, S>> returns = new HashSet<Call<P, S>>();
		for (Integer st : stateSet)
			returns.addAll(getCallsTo(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<ReturnBS<P, S>> getReturnBSFrom(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<ReturnBS<P, S>> getReturnBSTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<ReturnBS<P, S>> getReturnBSFrom(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<ReturnBS<P, S>> getReturnBSTo(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<SVPAEpsilon<P, S>> getEpsilonsFrom(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<SVPAEpsilon<P, S>> getEpsilonsTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<SVPAEpsilon<P, S>> getEpsilonsFrom(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<SVPAEpsilon<P, S>> getEpsilonsTo(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Internal<P, S>> getInternalsFrom(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Internal<P, S>> getInternalsTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Internal<P, S>> getInternalsFrom(Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Internal<P, S>> getInternalsTo(Collection<Integer> stateSet);

	/**
	 * Returns the set of states
	 */
	public abstract Collection<Integer> getStates();

	/**
	 * Returns the set of initial states
	 */
	public abstract Collection<Integer> getInitialStates();

	/**
	 * Returns the set of final states
	 */
	public abstract Collection<Integer> getFinalStates();

	/**
	 * Saves in the file <code>name</code> under the path <code>path</code> the
	 * dot representation of the automaton. Adds .dot if necessary
	 */
	public boolean createDotFile(String name, String path) {
		try {
			FileWriter fw = new FileWriter(path + name + (name.endsWith(".dot") ? "" : ".dot"));
			fw.write("digraph " + name + "{\n rankdir=LR;\n");
			for (Integer state : getStates()) {

				fw.write(state + "[label=" + state);
				if (getFinalStates().contains(state))
					fw.write(",peripheries=2");

				fw.write("]\n");
				if (getInitialStates().contains(state))
					fw.write("XX" + state + " [color=white, label=\"\"]");
			}

			for (Integer state : getInitialStates()) {
				fw.write("XX" + state + " -> " + state + "\n");
			}

			for (Integer state : getStates()) {
				for (SVPAMove<P, S> t : getMovesFrom(state))
					fw.write(t.toDotString());
			}

			fw.write("}");
			fw.close();
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s = "Automaton: " + getMoves().size() + " transitions, " + getStates().size() + " states" + "\n";
		s += "Transitions \n";
		for (SVPAMove<P, S> t : getMoves())
			s = s + t + "\n";
		s += "Initial States \n";
		for (Integer is : getInitialStates())
			s = s + is + "\n";
		s += "Final States \n";
		for (Integer fs : getFinalStates())
			s = s + fs + "\n";
		return s;
	}

	/**
	 * Generate a string in the language, null if language is empty
	 * 
	 * @param ba
	 * @return
	 * @throws TimeoutException 
	 */
	public LinkedList<TaggedSymbol<S>> getWitness(BooleanAlgebra<P, S> ba) throws TimeoutException {

		Collection<Integer> states = getStates();
		Map<Integer, Integer> stateToId = new HashMap<Integer, Integer>();
		Map<Integer, Integer> idToState = new HashMap<Integer, Integer>();

		Map<Integer, Collection<Integer>> wmRelList = new HashMap<Integer, Collection<Integer>>();
		boolean[][] wmReachRel = new boolean[states.size()][states.size()];
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> witnesses = new HashMap<>();

		Integer count = 0;
		for (Integer state : getStates()) {
			stateToId.put(state, count);
			idToState.put(count, state);
			wmReachRel[count][count] = true;

			Collection<Integer> reachableFromi = new HashSet<Integer>();
			reachableFromi.add(state);
			wmRelList.put(state, reachableFromi);

			LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
			witnesses.put(new Pair<Integer, Integer>(state, state), witness);

			for (int j = 0; j < wmReachRel.length; j++)
				if (count != j)
					wmReachRel[count][j] = false;

			if (getInitialStates().contains(state) && getFinalStates().contains(state))
				return witness;

			count++;
		}

		// Build one step relation
		for (Integer state1 : states) {
			int id1 = stateToId.get(state1);

			// Epsilon Transition
			for (SVPAEpsilon<P, S> t : getEpsilonsFrom(state1)) {
				wmReachRel[id1][stateToId.get(t.to)] = true;
				wmRelList.get(state1).add(t.to);

				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witnesses.put(new Pair<Integer, Integer>(t.from, t.to), witness);
				if (getInitialStates().contains(t.from) && getFinalStates().contains(t.to))
					return witness;
			}

			// Internal Transition
			for (Internal<P, S> t : getInternalsFrom(state1)) {
				wmReachRel[id1][stateToId.get(t.to)] = true;
				wmRelList.get(state1).add(t.to);

				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witness.add(new TaggedSymbol<S>(ba.generateWitness(t.guard), SymbolTag.Internal));
				witnesses.put(new Pair<Integer, Integer>(t.from, t.to), witness);

				if (getInitialStates().contains(t.from) && getFinalStates().contains(t.to))
					return witness;
			}
		}

		//

		// Compute fixpoint of reachability relation
		boolean changed = true;
		while (changed) {
			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);

				Collection<Integer> fromState1 = wmRelList.get(state1);
				Collection<Integer> newStates = new HashSet<>();

				// Calls and returns
				for (Call<P, S> tCall : getCallsFrom(state1)) {
					for (Integer toState : wmRelList.get(tCall.to))
						for (Return<P, S> tReturn : getReturnsFrom(toState, tCall.stackState)) {
							int stId = stateToId.get(tReturn.to);
							if (!wmReachRel[id1][stId]) {
								P conj = ba.MkAnd(tCall.guard, tReturn.guard);
								if (ba.IsSatisfiable(conj)) {
									changed = true;
									wmReachRel[id1][stId] = true;
									newStates.add(tReturn.to);

									LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
											witnesses.get(new Pair<Integer, Integer>(tCall.to, tReturn.from)));
									Pair<S, S> elements = ba.generateWitnesses(conj);
									witness.addFirst(new TaggedSymbol<S>(elements.first, SymbolTag.Call));
									witness.addLast(new TaggedSymbol<S>(elements.second, SymbolTag.Return));
									witnesses.put(new Pair<Integer, Integer>(state1, tReturn.to), witness);

									if (getInitialStates().contains(state1) && getFinalStates().contains(tReturn.to))
										return witness;
								}
							}
						}
				}

				// Closure
				for (Integer state2 : fromState1) {
					if (state1 != state2) {
						Collection<Integer> fromState2 = wmRelList.get(state2);
						for (int state3 : fromState2) {
							if (state3 != state2 && state3 != state1) {
								int id3 = stateToId.get(state3);
								if (!wmReachRel[id1][id3]) {
									changed = true;
									newStates.add(state3);
									wmReachRel[id1][id3] = true;

									LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
											witnesses.get(new Pair<Integer, Integer>(state1, state2)));
									witness.addAll(witnesses.get(new Pair<Integer, Integer>(state2, state3)));
									witnesses.put(new Pair<Integer, Integer>(state1, state3), witness);

									if (getInitialStates().contains(state1) && getFinalStates().contains(state3))
										return witness;
								}
							}
						}
					}
				}

				fromState1.addAll(newStates);
			}
		}

		// Unmatched calls
		boolean[][] unCallRel = copyBoolMatrix(wmReachRel);
		Map<Integer, Collection<Integer>> uCallRelList = copyMap(wmRelList);
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> ucWitnesses = new HashMap<>(witnesses);

		// Calls
		Collection<Call<P, S>> calls = getCallsFrom(getStates());
		for (Call<P, S> tCall : calls) {
			unCallRel[stateToId.get(tCall.from)][stateToId.get(tCall.to)] = true;
			uCallRelList.get(tCall.from).add(tCall.to);

			LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
			witness.add(new TaggedSymbol<S>(ba.generateWitness(tCall.guard), SymbolTag.Call));
			ucWitnesses.put(new Pair<Integer, Integer>(tCall.from, tCall.to), witness);

			if (getInitialStates().contains(tCall.from) && getFinalStates().contains(tCall.to))
				return witness;
		}

		if (!calls.isEmpty()) {
			changed = true;
			while (changed) {

				changed = false;
				for (Integer state1 : states) {
					int id1 = stateToId.get(state1);

					Collection<Integer> fromState1 = uCallRelList.get(state1);
					Collection<Integer> newStates = new HashSet<>();
					// Closure
					for (Integer state2 : fromState1) {
						Collection<Integer> fromState2 = uCallRelList.get(state2);
						for (int state3 : fromState2) {
							if (state3 != state2 && state3 != state1) {
								int id3 = stateToId.get(state3);
								if (!unCallRel[id1][id3]) {
									changed = true;
									newStates.add(state3);
									unCallRel[id1][id3] = true;

									LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
											witnesses.get(new Pair<Integer, Integer>(state1, state2)));
									witness.addAll(witnesses.get(new Pair<Integer, Integer>(state2, state3)));
									ucWitnesses.put(new Pair<Integer, Integer>(state1, state3), witness);

									if (getInitialStates().contains(state1) && getFinalStates().contains(state3))
										return witness;
								}
							}
						}
					}

					fromState1.addAll(newStates);
				}
			}
		}

		// Unmatched returns
		boolean[][] unRetRel = copyBoolMatrix(wmReachRel);
		Map<Integer, Collection<Integer>> uRetRelList = copyMap(wmRelList);
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> urWitnesses = new HashMap<>(witnesses);

		// Returns
		Collection<ReturnBS<P, S>> returns = getReturnBSFrom(getStates());
		for (ReturnBS<P, S> tRet : returns) {
			int idFrom = stateToId.get(tRet.from);
			int idTo = stateToId.get(tRet.to);
			if (!unRetRel[idFrom][idTo]) {
				unRetRel[idFrom][idTo] = true;
				uRetRelList.get(tRet.from).add(tRet.to);

				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witness.add(new TaggedSymbol<S>(ba.generateWitness(tRet.guard), SymbolTag.Return));
				urWitnesses.put(new Pair<Integer, Integer>(tRet.from, tRet.to), witness);

				if (getInitialStates().contains(tRet.from) && getFinalStates().contains(tRet.to))
					return witness;
			}
		}

		if (!returns.isEmpty()) {
			changed = true;
			while (changed) {

				changed = false;
				for (Integer state1 : states) {
					int id1 = stateToId.get(state1);

					Collection<Integer> fromState1 = uRetRelList.get(state1);
					Collection<Integer> newStates = new HashSet<>();
					// Closure
					for (Integer state2 : fromState1) {
						if (state2 != state1) {
							Collection<Integer> fromState2 = uRetRelList.get(state2);
							for (int state3 : fromState2) {
								if (state3 != state2 && state3 != state1) {
									int id3 = stateToId.get(state3);
									if (!unRetRel[id1][id3]) {
										changed = true;
										newStates.add(state3);
										unRetRel[id1][id3] = true;

										LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
												urWitnesses.get(new Pair<Integer, Integer>(state1, state2)));
										witness.addAll(urWitnesses.get(new Pair<Integer, Integer>(state2, state3)));
										urWitnesses.put(new Pair<Integer, Integer>(state1, state3), witness);

										if (getInitialStates().contains(state1) && getFinalStates().contains(state3))
											return witness;
									}
								}
							}
						}
					}

					fromState1.addAll(newStates);
				}
			}
		}

		// Full reachability relation
		for (Integer state1 : states) {
			for (Integer state2 : uRetRelList.get(state1)) {
				if (state1 != state2) {
					for (Integer state3 : uCallRelList.get(state2)) {
						if (state3 != state2 && state3 != state1) {
							if (getFinalStates().contains(state3)) {

								LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
										urWitnesses.get(new Pair<Integer, Integer>(state1, state2)));
								witness.addAll(ucWitnesses.get(new Pair<Integer, Integer>(state2, state3)));

								return witness;
							}
						}
					}
				}
			}
		}

		throw new IllegalArgumentException("The automaton can't be empty");

	}

	private boolean[][] copyBoolMatrix(boolean[][] matrix) {
		boolean[][] myMatrix = new boolean[matrix.length][];
		for (int i = 0; i < matrix.length; i++) {
			boolean[] aMatrix = matrix[i];
			int aLength = aMatrix.length;
			myMatrix[i] = new boolean[aLength];
			System.arraycopy(aMatrix, 0, myMatrix[i], 0, aLength);
		}
		return myMatrix;
	}

	private Map<Integer, Collection<Integer>> copyMap(Map<Integer, Collection<Integer>> matrix) {
		HashMap<Integer, Collection<Integer>> myMatrix = new HashMap<>();
		for (Integer i : matrix.keySet()) {
			myMatrix.put(i, new HashSet<>(matrix.get(i)));
		}
		return myMatrix;
	}

	// Compute reachability relations between states (wm, ucall, uret, unm)
	protected Quadruple<Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>> getReachRel(
			BooleanAlgebra<P, S> ba) throws TimeoutException {

		Collection<Integer> states = getStates();
		Map<Integer, Integer> stateToId = new HashMap<Integer, Integer>();
		Map<Integer, Integer> idToState = new HashMap<Integer, Integer>();

		Map<Integer, Collection<Integer>> wmRelList = new HashMap<Integer, Collection<Integer>>();
		boolean[][] wmReachRel = new boolean[states.size()][states.size()];

		HashSet<Integer> relevantStates = new HashSet<>();
		relevantStates.addAll(getInitialStates());
		HashSet<Integer> callStates = new HashSet<>();

		Integer count = 0;
		for (Integer state : getStates()) {
			stateToId.put(state, count);
			idToState.put(count, state);
			wmReachRel[count][count] = true;

			Collection<Integer> reachableFromi = new HashSet<Integer>();
			reachableFromi.add(state);
			wmRelList.put(state, reachableFromi);

			for (int j = 0; j < wmReachRel.length; j++)
				if (count != j)
					wmReachRel[count][j] = false;

			if (!getCallsFrom(state).isEmpty()){
				relevantStates.add(state);
				callStates.add(state);
			}else{
				if (!getReturnBSFrom(state).isEmpty())
					relevantStates.add(state);
				else{
					if (!getReturnsFrom(state).isEmpty())
						relevantStates.add(state);
					else{
						if (!getCallsTo(state).isEmpty())
							relevantStates.add(state);
						else{
							if (!getReturnBSTo(state).isEmpty())
								relevantStates.add(state);
							else
								if (!getReturnsTo(state).isEmpty())
									relevantStates.add(state);
						}
					}
				}
			}
				
			count++;
		}

		// Build relation for epsilon and internal transitions
		for (Integer state1 : relevantStates)
			dfsInternal(state1, stateToId, wmReachRel, wmRelList);

		// Compute fixpoint of reachability relation
		boolean changed = true;
		while (changed) {
			changed = false;

			for (Integer state1 : callStates) {
				int id1 = stateToId.get(state1);

				Collection<Integer> fromState1 = wmRelList.get(state1);
				Collection<Integer> newStates = new HashSet<>();

				// Calls and returns
				for (Call<P, S> tCall : getCallsFrom(state1)) {
					for (Integer toState : wmRelList.get(tCall.to))
						for (Return<P, S> tReturn : getReturnsFrom(toState, tCall.stackState)) {
							int stId = stateToId.get(tReturn.to);
							if (!wmReachRel[id1][stId])
								if (ba.IsSatisfiable(ba.MkAnd(tCall.guard, tReturn.guard))) {
									changed = true;
									wmReachRel[id1][stId] = true;
									newStates.add(tReturn.to);
								}
						}
				}
				fromState1.addAll(newStates);
			}

			if (changed)
				for (Integer state1 : relevantStates)
					dfsReachRel(state1, stateToId, wmReachRel, wmRelList);
		}

		// Unmatched calls
		boolean[][] unCallRel = copyBoolMatrix(wmReachRel);
		Map<Integer, Collection<Integer>> uCallRelList = copyMap(wmRelList);

		// Calls
		Collection<Call<P, S>> calls = getCallsFrom(getStates());
		for (Call<P, S> tCall : calls)
		{
			unCallRel[stateToId.get(tCall.from)][stateToId.get(tCall.to)] = true;
			uCallRelList.get(tCall.from).add(tCall.to);
		}

		if (!calls.isEmpty())
			for (Integer state1 : relevantStates)
				dfsReachRel(state1, stateToId, unCallRel, uCallRelList);

		// Unmatched returns
		boolean[][] unRetRel = copyBoolMatrix(wmReachRel);
		Map<Integer, Collection<Integer>> uRetRelList = copyMap(wmRelList);

		// Returns
		Collection<ReturnBS<P, S>> returns = getReturnBSFrom(getStates());
		for (ReturnBS<P, S> tRet : returns) {
			unRetRel[stateToId.get(tRet.from)][stateToId.get(tRet.to)] = true;
			uRetRelList.get(tRet.from).add(tRet.to);
		}

		if (!returns.isEmpty())
			for (Integer state1 : relevantStates)
				dfsReachRel(state1, stateToId, unRetRel, uRetRelList);

		// Full reachability relation
		Map<Integer, Collection<Integer>> relList = copyMap(uRetRelList);

		for (Integer state1 : states)

		{
			Collection<Integer> newStates = new HashSet<>();
			for (Integer state2 : uRetRelList.get(state1)) {
				newStates.addAll(uCallRelList.get(state2));
			}
			relList.put(state1, newStates);
		}

		return new Quadruple<Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>>(
				wmRelList, uCallRelList, uRetRelList, relList);

	}

	private void dfsInternal(int stateFrom, Map<Integer, Integer> stateToId, boolean[][] reachRel,
			Map<Integer, Collection<Integer>> wmRelList) {

		HashSet<Integer> reached = new HashSet<>();
		LinkedList<Integer> toVisit = new LinkedList<>();
		toVisit.add(stateFrom);

		int id = stateToId.get(stateFrom);

		while (!toVisit.isEmpty()) {
			int state = toVisit.removeFirst();

			// Epsilon Transition
			for (SVPAEpsilon<P, S> t : getEpsilonsFrom(state)) {
				if (!reached.contains(t.to)) {
					reachRel[id][stateToId.get(t.to)] = true;
					wmRelList.get(stateFrom).add(t.to);
					toVisit.add(t.to);
					reached.add(t.to);
				}
			}

			// Internal Transition
			for (Internal<P, S> t : getInternalsFrom(state)) {
				if (!reached.contains(t.to)) {
					reachRel[id][stateToId.get(t.to)] = true;
					wmRelList.get(stateFrom).add(t.to);
					toVisit.add(t.to);
					reached.add(t.to);
				}
			}
		}
	}

	private void dfsReachRel(int stateFrom, Map<Integer, Integer> stateToId, boolean[][] reachRel,
			Map<Integer, Collection<Integer>> wmRelList) {

		HashSet<Integer> reached = new HashSet<>();
		LinkedList<Integer> toVisit = new LinkedList<>();
		toVisit.add(stateFrom);

		int id = stateToId.get(stateFrom);

		while (!toVisit.isEmpty()) {
			int state = toVisit.removeFirst();

			// Epsilon Transition
			for (Integer to : wmRelList.get(state)) {
				if (!reached.contains(to)) {
					reachRel[id][stateToId.get(to)] = true;
					wmRelList.get(stateFrom).add(to);
					toVisit.add(to);
					reached.add(to);
				}
			}
		}
	}
}
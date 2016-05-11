package automata.svpa;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

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
	 * @param ba
	 * @return
	 */
	public LinkedList<TaggedSymbol<S>> getWitness(BooleanAlgebra<P, S> ba) {

		Collection<Integer> states = getStates();
		Map<Integer, Integer> stateToId = new HashMap<Integer, Integer>();
		Map<Integer, Integer> idToState = new HashMap<Integer, Integer>();
		boolean[][] wmReachRel = new boolean[states.size()][states.size()];
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> witnesses = new HashMap<>();

		Integer count = 0;
		for (Integer state : getStates()) {
			stateToId.put(state, count);
			idToState.put(count, state);
			wmReachRel[count][count] = true;
			LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
			witnesses.put(new Pair<Integer, Integer>(state, state), witness);

			if (getInitialStates().contains(state) && getFinalStates().contains(state))
				return witness;

			count++;
		}

		// Build reflexive relation
		for (int i = 0; i < wmReachRel.length; i++)
			for (int j = 0; j < wmReachRel.length; j++)
				if (i != j)
					wmReachRel[i][j] = false;

		// Build one step relation
		for (Integer state1 : states) {
			int id1 = stateToId.get(state1);

			// Epsilon Transition
			for (SVPAEpsilon<P, S> t : getEpsilonsFrom(state1)) {
				wmReachRel[id1][stateToId.get(t.to)] = true;
				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witnesses.put(new Pair<Integer, Integer>(t.from, t.to), witness);
				
				if (getInitialStates().contains(t.from) && getFinalStates().contains(t.to))
					return witness;
			}

			// Internal Transition
			for (Internal<P, S> t : getInternalsFrom(state1)) {
				wmReachRel[id1][stateToId.get(t.to)] = true;
				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witness.add(new TaggedSymbol<S>(ba.generateWitness(t.guard), SymbolTag.Internal));
				witnesses.put(new Pair<Integer, Integer>(t.from, t.to), witness);
				
				if (getInitialStates().contains(t.from) && getFinalStates().contains(t.to))
					return witness;
			}
		}

		// Compute fixpoint of reachability relation
		boolean changed = true;
		while (changed) {
			// start with same set
			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);
				for (Integer state2 : states) {
					int id2 = stateToId.get(state2);

					if_check: if (!wmReachRel[id1][id2]) {

						// Calls and returns
						for (Call<P, S> tCall : getCallsFrom(state1))
							for (Return<P, S> tReturn : getReturnsTo(state2, tCall.stackState))
								if (wmReachRel[stateToId.get(tCall.to)][stateToId.get(tReturn.from)]) {
									P conj = ba.MkAnd(tCall.guard, tReturn.guard);
									if (ba.IsSatisfiable(conj)) {
										wmReachRel[id1][id2] = true;

										LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
												witnesses.get(new Pair<Integer, Integer>(tCall.to, tReturn.from)));
										Pair<S, S> elements = ba.generateWitnesses(conj);
										witness.addFirst(new TaggedSymbol<S>(elements.first, SymbolTag.Call));
										witness.addLast(new TaggedSymbol<S>(elements.second, SymbolTag.Return));
										witnesses.put(new Pair<Integer, Integer>(state1, state2), witness);

										if (getInitialStates().contains(state1) && getFinalStates().contains(state2))
											return witness;
										
										changed = true;
										break if_check;
									}
								}

						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (wmReachRel[id1][idMid] && wmReachRel[idMid][id2]) {
								wmReachRel[id1][id2] = true;
								LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
										witnesses.get(new Pair<Integer, Integer>(state1, stateMid)));
								witness.addAll(witnesses.get(new Pair<Integer, Integer>(stateMid, state2)));
								witnesses.put(new Pair<Integer, Integer>(state1, state2), witness);

								if (getInitialStates().contains(state1) && getFinalStates().contains(state2))
									return witness;
								
								changed = true;
								break if_check;
							}
						}
					}
				}
			}
		}

		// Unmatched calls
		boolean[][] unCallRel = wmReachRel.clone();
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> ucWitnesses = new HashMap<>(witnesses);
		// Calls
		for (Call<P, S> tCall : getCallsFrom(getStates())) {

			int idFrom = stateToId.get(tCall.from);
			int idTo = stateToId.get(tCall.to);
			if (!unCallRel[idFrom][idTo]) {
				unCallRel[idFrom][idTo] = true;

				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witness.add(new TaggedSymbol<S>(ba.generateWitness(tCall.guard), SymbolTag.Call));
				ucWitnesses.put(new Pair<Integer, Integer>(tCall.from, tCall.to), witness);
				
				if (getInitialStates().contains(tCall.from) && getFinalStates().contains(tCall.to))
					return witness;
			}
		}

		changed = true;
		while (changed) {
			// start with same set
			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);
				for (Integer state2 : states) {
					int id2 = stateToId.get(state2);

					if_check: if (!unCallRel[id1][id2]) {
						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (unCallRel[id1][idMid] && unCallRel[idMid][id2]) {
								unCallRel[id1][id2] = true;

								LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
										ucWitnesses.get(new Pair<Integer, Integer>(state1, stateMid)));
								witness.addAll(ucWitnesses.get(new Pair<Integer, Integer>(stateMid, state2)));
								ucWitnesses.put(new Pair<Integer, Integer>(state1, state2), witness);

								if (getInitialStates().contains(state1) && getFinalStates().contains(state2))
									return witness;
								
								changed = true;
								break if_check;
							}
						}
					}
				}
			}
		}

		// Unmatched returns
		boolean[][] unRetRel = wmReachRel.clone();		
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> urWitnesses = new HashMap<>(witnesses);
		// Returns
		for (ReturnBS<P, S> tRet : getReturnBSFrom(getStates())) {
			int idFrom = stateToId.get(tRet.from);
			int idTo = stateToId.get(tRet.to);
			if (!unRetRel[idFrom][idTo]) {
				unRetRel[idFrom][idTo] = true;

				LinkedList<TaggedSymbol<S>> witness = new LinkedList<>();
				witness.add(new TaggedSymbol<S>(ba.generateWitness(tRet.guard), SymbolTag.Return));
				urWitnesses.put(new Pair<Integer, Integer>(tRet.from, tRet.to), witness);
				
				if (getInitialStates().contains(tRet.from) && getFinalStates().contains(tRet.to))
					return witness;
			}
		}

		changed = true;
		while (changed) {
			// start with same set
			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);
				for (Integer state2 : states) {
					int id2 = stateToId.get(state2);

					if_check: if (!unRetRel[id1][id2]) {
						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (unRetRel[id1][idMid] && unRetRel[idMid][id2]) {
								unRetRel[id1][id2] = true;

								LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
										urWitnesses.get(new Pair<Integer, Integer>(state1, stateMid)));
								witness.addAll(urWitnesses.get(new Pair<Integer, Integer>(stateMid, state2)));
								urWitnesses.put(new Pair<Integer, Integer>(state1, state2), witness);

								if (getInitialStates().contains(state1) && getFinalStates().contains(state2))
									return witness;
								
								changed = true;
								break if_check;
							}
						}
					}
				}
			}
		}

		// Full reachability relation
		boolean[][] reachRel = wmReachRel.clone();				
		for (Integer state1 : getInitialStates()) {
			int id1 = stateToId.get(state1);
			for (Integer state2 : getFinalStates()) {
				int id2 = stateToId.get(state2);

				// Closure
				if (!reachRel[id1][id2])
					for (Integer stateMid : states) {
						int idMid = stateToId.get(stateMid);
						if (unRetRel[id1][idMid] && unCallRel[idMid][id2]) {
							reachRel[id1][id2] = true;

							LinkedList<TaggedSymbol<S>> witness = new LinkedList<>(
									urWitnesses.get(new Pair<Integer, Integer>(state1, stateMid)));
							witness.addAll(ucWitnesses.get(new Pair<Integer, Integer>(stateMid, state2)));

							return witness;
						}
					}
			}
		}

		throw new IllegalArgumentException("The automaton can't be empty");

	}

	// Compute reachability relations between states (wm, ucall, uret, unm)
	protected Quadruple<Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>> getReachRel(
			BooleanAlgebra<P, S> ba) {

		Collection<Integer> states = getStates();
		Map<Integer, Integer> stateToId = new HashMap<Integer, Integer>();
		Map<Integer, Integer> idToState = new HashMap<Integer, Integer>();
		boolean[][] wmReachRel = new boolean[states.size()][states.size()];

		Integer count = 0;
		for (Integer state : getStates()) {
			stateToId.put(state, count);
			idToState.put(count, state);
			wmReachRel[count][count] = true;
			count++;
		}

		// Build reflexive relation
		for (int i = 0; i < wmReachRel.length; i++)
			for (int j = 0; j < wmReachRel.length; j++)
				if (i != j)
					wmReachRel[i][j] = false;

		// Build one step relation
		for (Integer state1 : states) {
			int id1 = stateToId.get(state1);

			// Epsilon Transition
			for (SVPAEpsilon<P, S> t : getEpsilonsFrom(state1))
				wmReachRel[id1][stateToId.get(t.to)] = true;

			// Internal Transition
			for (Internal<P, S> t : getInternalsFrom(state1))
				wmReachRel[id1][stateToId.get(t.to)] = true;
		}

		// Compute fixpoint of reachability relation
		boolean changed = true;
		while (changed) {
			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);
				for (Integer state2 : states) {
					int id2 = stateToId.get(state2);

					if_check: if (!wmReachRel[id1][id2]) {

						// Calls and returns
						for (Call<P, S> tCall : getCallsFrom(state1))
							for (Return<P, S> tReturn : getReturnsTo(state2, tCall.stackState))
								if (wmReachRel[stateToId.get(tCall.to)][stateToId.get(tReturn.from)])
									if (ba.IsSatisfiable(ba.MkAnd(tCall.guard, tReturn.guard))) {
										wmReachRel[id1][id2] = true;
										changed = true;
										break if_check;
									}

						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (wmReachRel[id1][idMid] && wmReachRel[idMid][id2]) {
								wmReachRel[id1][id2] = true;
								changed = true;
								break if_check;
							}
						}
					}
				}
			}
		}

		// Unmatched calls
		boolean[][] unCallRel = wmReachRel.clone();
		// Calls
		for (Call<P, S> tCall : getCallsFrom(getStates()))
			unCallRel[stateToId.get(tCall.from)][stateToId.get(tCall.to)] = true;

		changed = true;
		while (changed) {

			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);
				for (Integer state2 : states) {
					int id2 = stateToId.get(state2);

					if_check: if (!unCallRel[id1][id2]) {
						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (unCallRel[id1][idMid] && unCallRel[idMid][id2]) {
								unCallRel[id1][id2] = true;
								changed = true;
								break if_check;
							}
						}
					}
				}
			}
		}

		// Unmatched returns
		boolean[][] unRetRel = wmReachRel.clone();
		// Returns
		for (ReturnBS<P, S> tRet : getReturnBSFrom(getStates()))
			unRetRel[stateToId.get(tRet.from)][stateToId.get(tRet.to)] = true;

		changed = true;
		while (changed) {

			changed = false;

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);
				for (Integer state2 : states) {
					int id2 = stateToId.get(state2);

					if_check: if (!unRetRel[id1][id2]) {
						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (unRetRel[id1][idMid] && unRetRel[idMid][id2]) {
								unRetRel[id1][id2] = true;
								changed = true;
								break if_check;
							}
						}
					}
				}
			}
		}

		// Full reachability relation
		boolean[][] reachRel = wmReachRel.clone();

		for (Integer state1 : states) {
			int id1 = stateToId.get(state1);
			for (Integer state2 : states) {
				int id2 = stateToId.get(state2);

				// Closure
				if (!reachRel[id1][id2])
					for (Integer stateMid : states) {
						int idMid = stateToId.get(stateMid);
						if (unRetRel[id1][idMid] && unCallRel[idMid][id2]) {
							reachRel[id1][id2] = true;
						}
					}
			}
		}

		// Put into lists
		Map<Integer, Collection<Integer>> wmRelList = new HashMap<Integer, Collection<Integer>>();

		for (int i = 0; i < wmReachRel.length; i++) {
			Collection<Integer> reachableFromi = new HashSet<Integer>();
			for (int j = 0; j < wmReachRel.length; j++)
				if (wmReachRel[i][j])
					reachableFromi.add(idToState.get(j));
			wmRelList.put(idToState.get(i), reachableFromi);
		}

		Map<Integer, Collection<Integer>> uCallRelList = new HashMap<Integer, Collection<Integer>>();

		for (int i = 0; i < unCallRel.length; i++) {
			Collection<Integer> reachableFromi = new HashSet<Integer>();
			for (int j = 0; j < unCallRel.length; j++)
				if (unCallRel[i][j])
					reachableFromi.add(idToState.get(j));
			uCallRelList.put(idToState.get(i), reachableFromi);
		}

		Map<Integer, Collection<Integer>> uRetRelList = new HashMap<Integer, Collection<Integer>>();
		for (int i = 0; i < unRetRel.length; i++) {
			Collection<Integer> reachableFromi = new HashSet<Integer>();
			for (int j = 0; j < unRetRel.length; j++)
				if (unRetRel[i][j])
					reachableFromi.add(idToState.get(j));
			uRetRelList.put(idToState.get(i), reachableFromi);
		}

		Map<Integer, Collection<Integer>> relList = new HashMap<Integer, Collection<Integer>>();
		for (int i = 0; i < reachRel.length; i++) {
			Collection<Integer> reachableFromi = new HashSet<Integer>();
			for (int j = 0; j < reachRel.length; j++)
				if (reachRel[i][j])
					reachableFromi.add(idToState.get(j));
			relList.put(idToState.get(i), reachableFromi);
		}

		return new Quadruple<Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>, Map<Integer, Collection<Integer>>>(
				wmRelList, uCallRelList, uRetRelList, relList);
	}

}

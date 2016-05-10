package automata.svpa;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

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

	public LinkedList<TaggedSymbol<S>> getWitness(BooleanAlgebra<P, S> ba) {
		if (isEmpty)
			return null;

		Random ran = new Random();
		Integer finState = new ArrayList<Integer>(getFinalStates()).get(ran.nextInt(getFinalStates().size()));

		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> matchedWit = new HashMap<>();
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> uCallWit = new HashMap<>();
		HashMap<Pair<Integer, Integer>, LinkedList<TaggedSymbol<S>>> uRetWit = new HashMap<>();

		// Map<Integer, Collection<Integer>> rel = getReachabilityRelation(ba);
		// for (Integer s : getInitialStates())
		// if (rel.get(s).contains(finState))
		// return getWitness(ba, getWellMatchedReachRel(ba), rel, s, finState,
		// true, tried);

		return null;
	}

	// Generate a string in the language, null if language is empty
	private LinkedList<TaggedSymbol<S>> getWitness(BooleanAlgebra<P, S> ba, Map<Integer, Collection<Integer>> wmrel,
			Map<Integer, Collection<Integer>> unCallRel, Map<Integer, Collection<Integer>> unRetRel, int from, int to,
			boolean canBeNotWM) {

		// LinkedList<TaggedSymbol<S>> output = new
		// LinkedList<TaggedSymbol<S>>();
		// if (from == to)
		// return output;
		//
		// // Internal Transition
		// for (Internal<P, S> t : getInternalsFrom(from))
		// if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
		// if (rel.get(t.to).contains(to)) {
		// output = getWitness(ba, wmrel, rel, t.to, to, canBeNotWM, tried);
		// output.addFirst(new TaggedSymbol<S>(ba.generateWitness(t.guard),
		// SymbolTag.Internal));
		// return output;
		// }
		//
		// // Calls and returns
		// for (Call<P, S> tCall : getCallsFrom(from)) {
		// for (Return<P, S> tReturn : getReturnsTo(to, tCall.stackState))
		// if (!tried.contains(new Pair<Integer, Integer>(tCall.to,
		// tReturn.from)))
		// if (wmrel.get(tCall.to).contains(tReturn.from)) {
		// P pred = ba.MkAnd(tCall.guard, tReturn.guard);
		// if (ba.IsSatisfiable(pred)) {
		//
		// Pair<S, S> a = ba.generateWitnesses(ba.MkAnd(tCall.guard,
		// tReturn.guard));
		// output = getWitness(ba, wmrel, rel, tCall.to, tReturn.from, false,
		// tried);
		//
		// output.addFirst(new TaggedSymbol<S>(a.first, SymbolTag.Call));
		// output.addLast(new TaggedSymbol<S>(a.second, SymbolTag.Return));
		// return output;
		// }
		// }
		// }
		//
		//
		// // Epsilon Transition
		// for (SVPAEpsilon<P, S> t : getEpsilonsFrom(from))
		// if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
		// if (rel.get(t.to).contains(to))
		// return getWitness(ba, wmrel, rel, t.to, to, canBeNotWM, tried);
		//
		// // Closure
		// for (Integer stateMid : getStates()) {
		// if (wmrel.get(from).contains(stateMid) &&
		// wmrel.get(stateMid).contains(to)) {
		// output = getWitness(ba, wmrel, rel, from, stateMid, canBeNotWM,
		// tried);
		// LinkedList<TaggedSymbol<S>> right = getWitness(ba, wmrel, rel,
		// stateMid, to, canBeNotWM, tried);
		// output.addAll(right);
		// return output;
		// }
		// // NotWM
		// if (canBeNotWM) {
		// //TODO
		// if (wmrel.get(from).contains(stateMid) &&
		// wmrel.get(stateMid).contains(to)) {
		// output = getWitness(ba, wmrel, rel, from, stateMid, canBeNotWM,
		// tried);
		// LinkedList<TaggedSymbol<S>> right = getWitness(ba, wmrel, rel,
		// stateMid, to, canBeNotWM, tried);
		// output.addAll(right);
		// return output;
		// }
		// }
		// }

		throw new IllegalArgumentException("this shouldn't happen");
	}

	// Generate a string in the language, null if language is empty
	private LinkedList<TaggedSymbol<S>> getWitnessUnmatchedCalls(BooleanAlgebra<P, S> ba,
			Map<Integer, Collection<Integer>> wmrel, Map<Integer, Collection<Integer>> rel, int from, int to,
			Random ran) {

		// LinkedList<TaggedSymbol<S>> output = new
		// LinkedList<TaggedSymbol<S>>();
		// if (from == to)
		// return output;
		//
		// // Internal Transition
		// for (Internal<P, S> t : getInternalsFrom(from))
		// if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
		// if (rel.get(t.to).contains(to)) {
		// output = getWitness(ba, wmrel, rel, t.to, to, ran, canBeNotWM,
		// tried);
		// output.addFirst(new TaggedSymbol<S>(ba.generateWitness(t.guard),
		// SymbolTag.Internal));
		// return output;
		// }
		//
		// // returnBS
		// if (canBeNotWM) {
		// for (ReturnBS<P, S> t : getReturnBSFrom(from))
		// if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
		// if (rel.get(t.to).contains(to)) {
		// output = getWitness(ba, wmrel, rel, t.to, to, ran, canBeNotWM,
		// tried);
		// output.addFirst(new TaggedSymbol<S>(ba.generateWitness(t.guard),
		// SymbolTag.Return));
		// return output;
		// }
		// }
		//
		// // Calls and returns
		// for (Call<P, S> tCall : getCallsFrom(from)) {
		// for (Return<P, S> tReturn : getReturnsTo(to, tCall.stackState))
		// if (!tried.contains(new Pair<Integer, Integer>(tCall.to,
		// tReturn.from)))
		// if (wmrel.get(tCall.to).contains(tReturn.from)) {
		// P pred = ba.MkAnd(tCall.guard, tReturn.guard);
		// if (ba.IsSatisfiable(pred)) {
		//
		// Pair<S, S> a = ba.generateWitnesses(ba.MkAnd(tCall.guard,
		// tReturn.guard));
		// output = getWitness(ba, wmrel, rel, tCall.to, tReturn.from, ran,
		// false, tried);
		//
		// output.addFirst(new TaggedSymbol<S>(a.first, SymbolTag.Call));
		// output.addLast(new TaggedSymbol<S>(a.second, SymbolTag.Return));
		// return output;
		// }
		// }
		// }
		//
		// if (canBeNotWM)
		// for (Call<P, S> t : getCallsTo(to))
		// if (!tried.contains(new Pair<Integer, Integer>(from, t.from)))
		// if (rel.get(from).contains(t.from)) {
		// output = getWitness(ba, wmrel, rel, from, t.from, ran, canBeNotWM,
		// tried);
		// output.addLast(new TaggedSymbol<S>(ba.generateWitness(t.guard),
		// SymbolTag.Call));
		// return output;
		// }
		//
		// // Epsilon Transition
		// for (SVPAEpsilon<P, S> t : getEpsilonsFrom(from))
		// if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
		// if (rel.get(t.to).contains(to))
		// return getWitness(ba, wmrel, rel, t.to, to, ran, canBeNotWM, tried);
		//
		// // Closure
		// for (Integer stateMid : states) {
		// int idMid = stateToId.get(stateMid);
		// if (rel.get(t.to).con && reachabilityRelation[idMid][id2]) {
		// reachabilityRelation[id1][id2] = true;
		// break if_check;
		// }
		// }

		throw new IllegalArgumentException("this shouldn't happen");

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
		boolean[][] tmpRel = new boolean[states.size()][states.size()];
		while (changed) {
			// start with same set
			tmpRel = wmReachRel.clone();
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
			// start with same set
			tmpRel = unCallRel.clone();
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
			// start with same set
			tmpRel = unRetRel.clone();
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

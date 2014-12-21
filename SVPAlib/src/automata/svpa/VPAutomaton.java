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
import automata.svpa.TaggedSymbol.SymbolTag;

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
	public abstract Collection<Return<P, S>> getReturnsFrom(
			Pair<Integer, Integer> state);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(
			Pair<Integer, Integer> state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(Integer state,
			Integer stackState);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(Integer state,
			Integer stackState);

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
	public abstract Collection<Return<P, S>> getReturnsFrom(
			Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions starting in a state in
	 * <code>stateSet</code> with stack state <code>stackState</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsFrom(
			Collection<Integer> stateSet, Integer stackState);

	/**
	 * Returns the set of return transitions to a state in <code>stateSet</code>
	 */
	public abstract Collection<Return<P, S>> getReturnsTo(
			Collection<Integer> stateSet);

	/**
	 * Returns the set of call transitions starting a state <code>state</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(Integer state);

	/**
	 * Returns the set of call transitions starting in state <code>state</code>
	 * with stack state <code>stackState</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(Integer state,
			Integer stackState);

	/**
	 * Returns the set of call transitions starting in a state in
	 * <code>stateSet</code> with stack state <code>stackState</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(
			Collection<Integer> stateSet, Integer stackState);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Call<P, S>> getCallsTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Call<P, S>> getCallsFrom(
			Collection<Integer> stateSet);

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
	public abstract Collection<ReturnBS<P, S>> getReturnBSFrom(
			Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<ReturnBS<P, S>> getReturnBSTo(
			Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Epsilon<P, S>> getEpsilonsFrom(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Epsilon<P, S>> getEpsilonsTo(Integer state);

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public abstract Collection<Epsilon<P, S>> getEpsilonsFrom(
			Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Epsilon<P, S>> getEpsilonsTo(
			Collection<Integer> stateSet);

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
	public abstract Collection<Internal<P, S>> getInternalsFrom(
			Collection<Integer> stateSet);

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public abstract Collection<Internal<P, S>> getInternalsTo(
			Collection<Integer> stateSet);

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
			FileWriter fw = new FileWriter(path + name
					+ (name.endsWith(".dot") ? "" : ".dot"));
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
		s = "Automaton: " + getMoves().size() + " transitions, "
				+ getStates().size() + " states" + "\n";
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
		Integer finState = new ArrayList<Integer>(getFinalStates()).get(ran
				.nextInt(getFinalStates().size()));

		HashSet<Pair<Integer, Integer>> tried = new HashSet<Pair<Integer, Integer>>();
		Map<Integer, Collection<Integer>> rel = getReachabilityRelation(ba);
		for (Integer s : getInitialStates())
			if (rel.get(s).contains(finState))
				return getWitness(ba, getWellMatchedReachRel(ba), rel, s,
						finState, ran, true, tried);

		return null;
	}

	// Generate a string in the language, null if language is empty
	private LinkedList<TaggedSymbol<S>> getWitness(BooleanAlgebra<P, S> ba,
			Map<Integer, Collection<Integer>> wmrel,
			Map<Integer, Collection<Integer>> rel, int from, int to,
			Random ran, boolean canBeNotWM,
			HashSet<Pair<Integer, Integer>> tried) {

		tried.add(new Pair<Integer, Integer>(from, to));

		LinkedList<TaggedSymbol<S>> output = new LinkedList<TaggedSymbol<S>>();
		if (from == to)
			return output;

		// Internal Transition
		for (Internal<P, S> t : getInternalsFrom(from))
			if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
				if (rel.get(t.to).contains(to)) {
					output = getWitness(ba, wmrel, rel, t.to, to, ran,
							canBeNotWM, tried);
					output.addFirst(new TaggedSymbol<S>(ba
							.generateWitness(t.guard), SymbolTag.Internal));
					return output;
				}

		// returnBS
		if (canBeNotWM) {
			for (ReturnBS<P, S> t : getReturnBSFrom(from))
				if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
					if (rel.get(t.to).contains(to)) {
						output = getWitness(ba, wmrel, rel, t.to, to, ran,
								canBeNotWM, tried);
						output.addFirst(new TaggedSymbol<S>(ba
								.generateWitness(t.guard), SymbolTag.Return));
						return output;
					}
		}

		// Calls and returns
		for (Call<P, S> tCall : getCallsFrom(from)) {
			for (Return<P, S> tReturn : getReturnsTo(to, tCall.stackState))
				if (!tried.contains(new Pair<Integer, Integer>(tCall.to,
						tReturn.from)))
					if (wmrel.get(tCall.to).contains(tReturn.from)) {
						P pred = ba.MkAnd(tCall.guard, tReturn.guard);
						if (ba.IsSatisfiable(pred)) {

							Pair<S, S> a = ba.generateWitnesses(ba.MkAnd(
									tCall.guard, tReturn.guard));
							output = getWitness(ba, wmrel, rel, tCall.to,
									tReturn.from, ran, false, tried);

							output.addFirst(new TaggedSymbol<S>(a.first,
									SymbolTag.Call));
							output.addLast(new TaggedSymbol<S>(a.second,
									SymbolTag.Return));
							return output;
						}
					}
		}

		if (canBeNotWM)
			for (Call<P, S> t : getCallsTo(to))
				if (!tried.contains(new Pair<Integer, Integer>(from, t.from)))
					if (rel.get(from).contains(t.from)) {
						output = getWitness(ba, wmrel, rel, from, t.from, ran,
								canBeNotWM, tried);
						output.addLast(new TaggedSymbol<S>(ba
								.generateWitness(t.guard), SymbolTag.Call));
						return output;
					}

		// Epsilon Transition
		for (Epsilon<P, S> t : getEpsilonsFrom(from))
			if (!tried.contains(new Pair<Integer, Integer>(t.to, to)))
				if (rel.get(t.to).contains(to))
					return getWitness(ba, wmrel, rel, t.to, to, ran,
							canBeNotWM, tried);

		return output;
	}

	// Compute well matched reachability relation between states
	private Map<Integer, Collection<Integer>> getWellMatchedReachRel(
			BooleanAlgebra<P, S> ba) {

		Collection<Integer> states = getStates();
		Map<Integer, Integer> stateToId = new HashMap<Integer, Integer>();
		Map<Integer, Integer> idToState = new HashMap<Integer, Integer>();
		boolean[][] reachabilityRelation = new boolean[states.size()][states
				.size()];

		// Build reflexive relation
		for (int i = 0; i < reachabilityRelation.length; i++)
			for (int j = 0; j < reachabilityRelation.length; j++)
				reachabilityRelation[i][j] = false;

		Integer count = 0;
		for (Integer state : states) {
			stateToId.put(state, count);
			idToState.put(count, state);
			reachabilityRelation[count][count] = true;
			count++;
		}

		// Compute fixpoint of reachability relation
		boolean[][] reachabilityRelationTmp = new boolean[states.size()][states
				.size()];
		while (!Arrays
				.deepEquals(reachabilityRelation, reachabilityRelationTmp)) {
			// start with same set
			reachabilityRelationTmp = reachabilityRelation.clone();

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);

				for (Integer state2 : states) {

					int id2 = stateToId.get(state2);

					if_check: if (!reachabilityRelation[id1][id2]) {

						// Epsilon Transition
						for (Epsilon<P, S> t : getEpsilonsFrom(state1))
							if (reachabilityRelation[stateToId.get(t.to)][id2]) {
								reachabilityRelation[id1][id2] = true;
								break if_check;
							}

						// Internal Transition
						for (Internal<P, S> t : getInternalsFrom(state1))
							if (reachabilityRelation[stateToId.get(t.to)][id2]) {
								reachabilityRelation[id1][id2] = true;
								break if_check;
							}

						// Calls and returns
						for (Call<P, S> tCall : getCallsFrom(state1))
							for (Return<P, S> tReturn : getReturnsTo(state2,
									tCall.stackState))
								if (reachabilityRelation[stateToId
										.get(tCall.to)][stateToId
										.get(tReturn.from)])
									if (ba.IsSatisfiable(ba.MkAnd(tCall.guard,
											tReturn.guard))) {
										reachabilityRelation[id1][id2] = true;
										break if_check;
									}

						// Closure
						try {
							for (Integer stateMid : states) {
								int idMid = stateToId.get(stateMid);
								if (reachabilityRelation[id1][idMid]
										&& reachabilityRelation[idMid][id2]) {
									reachabilityRelation[id1][id2] = true;
									break if_check;
								}
							}
						} catch (Exception e) {
							System.out.print(e);
						}
					}
				}
			}
		}
		// Copy to adjacency list
		Map<Integer, Collection<Integer>> reachRelList = new HashMap<Integer, Collection<Integer>>();

		for (int i = 0; i < reachabilityRelation.length; i++) {
			Collection<Integer> reachableFromi = new HashSet<Integer>();
			for (int j = 0; j < reachabilityRelation.length; j++)
				if (reachabilityRelation[i][j])
					reachableFromi.add(idToState.get(j));
			reachRelList.put(idToState.get(i), reachableFromi);
		}
		return reachRelList;
	}

	// Compute reachability relation between states
	protected Map<Integer, Collection<Integer>> getReachabilityRelation(
			BooleanAlgebra<P, S> ba) {

		Collection<Integer> states = getStates();
		Map<Integer, Integer> stateToId = new HashMap<Integer, Integer>();
		Map<Integer, Integer> idToState = new HashMap<Integer, Integer>();
		boolean[][] reachabilityRelation = new boolean[states.size()][states
				.size()];

		// Build reflexive relation
		for (int i = 0; i < reachabilityRelation.length; i++)
			for (int j = 0; j < reachabilityRelation.length; j++)
				reachabilityRelation[i][j] = false;

		Integer count = 0;
		for (Integer state : getStates()) {
			stateToId.put(state, count);
			idToState.put(count, state);
			reachabilityRelation[count][count] = true;
			count++;
		}

		// Compute fixpoint of reachability relation
		boolean[][] reachabilityRelationTmp = new boolean[states.size()][states
				.size()];
		while (!Arrays
				.deepEquals(reachabilityRelation, reachabilityRelationTmp)) {
			// start with same set
			reachabilityRelationTmp = reachabilityRelation.clone();

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);

				for (Integer state2 : states) {

					int id2 = stateToId.get(state2);

					if_check: if (!reachabilityRelation[id1][id2]) {

						// Epsilon Transition
						for (Epsilon<P, S> t : getEpsilonsFrom(state1))
							if (reachabilityRelation[stateToId.get(t.to)][id2]) {
								reachabilityRelation[id1][id2] = true;
								break if_check;
							}

						// Internal Transition
						for (Internal<P, S> t : getInternalsFrom(state1))
							if (reachabilityRelation[stateToId.get(t.to)][id2]) {
								reachabilityRelation[id1][id2] = true;
								break if_check;
							}

						// Calls and returns
						for (Call<P, S> tCall : getCallsFrom(state1))
							for (Return<P, S> tReturn : getReturnsTo(state2,
									tCall.stackState))
								if (reachabilityRelation[stateToId
										.get(tCall.to)][stateToId
										.get(tReturn.from)])
									if (ba.IsSatisfiable(ba.MkAnd(tCall.guard,
											tReturn.guard))) {
										reachabilityRelation[id1][id2] = true;
										break if_check;
									}

						// Closure
						try {
							for (Integer stateMid : states) {
								int idMid = stateToId.get(stateMid);
								if (reachabilityRelation[id1][idMid]
										&& reachabilityRelation[idMid][id2]) {
									reachabilityRelation[id1][id2] = true;
									break if_check;
								}
							}
						} catch (Exception e) {
							System.out.print(e);
						}
					}
				}
			}
		}

		// Compute call closure
		boolean[][] reachabilityRelationCall = reachabilityRelation.clone();
		reachabilityRelationTmp = new boolean[states.size()][states.size()];
		while (!Arrays.deepEquals(reachabilityRelationCall,
				reachabilityRelationTmp)) {
			// start with same set
			reachabilityRelationTmp = reachabilityRelationCall.clone();

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);

				for (Integer state2 : states) {

					int id2 = stateToId.get(state2);
					if_check2: if (!reachabilityRelationCall[id1][id2]) {

						// Epsilon Transition
						for (Epsilon<P, S> t : getEpsilonsFrom(state1))
							if (reachabilityRelationCall[stateToId.get(t.to)][id2]) {
								reachabilityRelationCall[id1][id2] = true;
								break if_check2;
							}

						// Calls
						for (Call<P, S> tCall : getCallsFrom(state1))
							if (reachabilityRelationCall[stateToId
									.get(tCall.to)][id2]) {
								reachabilityRelationCall[id1][id2] = true;
								break if_check2;
							}

						// Internal Transition
						for (Internal<P, S> t : getInternalsFrom(state1))
							if (reachabilityRelationCall[stateToId.get(t.to)][id2]) {
								reachabilityRelationCall[id1][id2] = true;
								break if_check2;
							}

						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (reachabilityRelationCall[id1][idMid]
									&& reachabilityRelationCall[idMid][id2]) {
								reachabilityRelationCall[id1][id2] = true;
								break if_check2;
							}
						}
					}
				}
			}
		}

		// Compute return closure
		boolean[][] reachabilityRelationReturn = reachabilityRelation.clone();
		reachabilityRelationTmp = new boolean[states.size()][states.size()];
		while (!Arrays.deepEquals(reachabilityRelationReturn,
				reachabilityRelationTmp)) {
			// start with same set
			reachabilityRelationTmp = reachabilityRelationReturn.clone();

			for (Integer state1 : states) {
				int id1 = stateToId.get(state1);

				for (Integer state2 : states) {

					int id2 = stateToId.get(state2);
					if_check3: if (!reachabilityRelationReturn[id1][id2]) {

						// Epsilon Transition
						for (Epsilon<P, S> t : getEpsilonsFrom(state1))
							if (reachabilityRelationReturn[stateToId.get(t.to)][id2]) {
								reachabilityRelationReturn[id1][id2] = true;
								break if_check3;
							}

						// Bottom stack returns
						for (ReturnBS<P, S> tRet : getReturnBSFrom(state1))
							if (reachabilityRelationReturn[stateToId
									.get(tRet.to)][id2]) {
								reachabilityRelationReturn[id1][id2] = true;
								break if_check3;
							}

						// Internal Transition
						for (Internal<P, S> t : getInternalsFrom(state1))
							if (reachabilityRelationReturn[stateToId.get(t.to)][id2]) {
								reachabilityRelationReturn[id1][id2] = true;
								break if_check3;
							}

						// Closure
						for (Integer stateMid : states) {
							int idMid = stateToId.get(stateMid);
							if (reachabilityRelationReturn[id1][idMid]
									&& reachabilityRelationReturn[idMid][id2]) {
								reachabilityRelationReturn[id1][id2] = true;
								break if_check3;
							}
						}

					}
				}
			}
		}

		// calls and returns closure
		for (Integer state1 : states) {
			int id1 = stateToId.get(state1);
			for (Integer state2 : states) {
				int id2 = stateToId.get(state2);
				if (reachabilityRelationCall[id1][id2]
						|| reachabilityRelationReturn[id1][id2])
					reachabilityRelation[id1][id2] = true;
				else
					for (Integer stateMid : states) {
						int idMid = stateToId.get(stateMid);
						if (reachabilityRelationReturn[id1][idMid]
								&& reachabilityRelationCall[idMid][id2]) {
							reachabilityRelationReturn[id1][id2] = true;
							break;
						}
					}
			}
		}

		// Copy to adjacency list
		Map<Integer, Collection<Integer>> reachRelList = new HashMap<Integer, Collection<Integer>>();

		for (int i = 0; i < reachabilityRelation.length; i++) {
			Collection<Integer> reachableFromi = new HashSet<Integer>();
			for (int j = 0; j < reachabilityRelation.length; j++)
				if (reachabilityRelation[i][j])
					reachableFromi.add(idToState.get(j));
			reachRelList.put(idToState.get(i), reachableFromi);
		}
		return reachRelList;
	}

}

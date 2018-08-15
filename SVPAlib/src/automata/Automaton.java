/**
 * SVPAlib
 * automata
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * Automaton abstract class
 * 
 * @param
 * 			<P>
 *            set of predicates over the domain S
 * @param <S>
 *            domain of the automaton alphabet
 */
public abstract class Automaton<P, S> {

	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

	protected boolean isEmpty;
	protected boolean isDeterministic;
	protected boolean isEpsilonFree;
	protected boolean isTotal;

	public Automaton() {
		isEmpty = false;
		isDeterministic = false;
		isEpsilonFree = true;
		isTotal = false;
	}

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
				if (isInitialState(state))
					fw.write("XX" + state + " [color=white, label=\"\"]");
			}

			fw.write("XX" + getInitialState() + " -> " + getInitialState() + "\n");

			for (Integer state : getStates()) {
				for (Move<P, S> t : getMovesFrom(state))
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
		for (Move<P, S> t : getMoves())
			s = s + t + "\n";

		s += "Initial State \n";
		s = s + getInitialState() + "\n";

		s += "Final States \n";
		for (Integer fs : getFinalStates())
			s = s + fs + "\n";
		return s;
	}

	/**
	 * Returns a sequence in the input domain that is accepted by the automaton
	 * 
	 * @return a list in the domain language, null if empty
	 * @throws TimeoutException 
	 */
	public HashSet<List<S>> getWitnesses(BooleanAlgebra<P, S> ba, int howMany) throws TimeoutException {
		if (isEmpty)
			return null;

		Map<Integer, HashSet<List<S>>> witMap = new HashMap<>();
		for (Integer state : getFinalStates()) {
			HashSet<List<S>> s = new HashSet<>();
			s.add(new LinkedList<>());
			witMap.put(state, s);
		}

		boolean somethingChanged = true;
		while (somethingChanged && (witMap.get(getInitialState()) == null
				|| witMap.get(getInitialState()).size() < howMany)) {
			somethingChanged = false;

			for (Move<P, S> move : getMoves()) {
				HashSet<List<S>> prevStrings = new HashSet<>();

				if (!witMap.containsKey(move.from)) {
					witMap.put(move.from, prevStrings);
				} else {
					prevStrings = witMap.get(move.from);
				}

				int size = prevStrings.size();
				if (witMap.containsKey(move.to)) {
					HashSet<List<S>> newStrings = new HashSet<List<S>>();
					for (List<S> str : witMap.get(move.to)) {
						if (!move.isEpsilonTransition()) {
							LinkedList<S> newStr = new LinkedList<S>(str);
							S wit = move.getWitness(ba);
							newStr.addFirst(wit);
							newStrings.add(newStr);
						} else {
							newStrings.add(str);
						}
					}
					prevStrings.addAll(newStrings);
					if (prevStrings.size() > size)
						somethingChanged = true;
				}
			}
		}
		return witMap.get(getInitialState());
	}

	/**
	 * Returns a sequence in the input domain that is accepted by the automaton
	 * 
	 * @return a list in the domain language, null if empty
	 * @throws TimeoutException 
	 */
	public List<S> getWitness(BooleanAlgebra<P, S> ba) throws TimeoutException {
		if (isEmpty)
			return null;

		Map<Integer, LinkedList<S>> witMap = new HashMap<Integer, LinkedList<S>>();
		for (Integer state : getFinalStates())
			witMap.put(state, new LinkedList<S>());

		HashSet<Integer> reachedStates = new HashSet<Integer>(getFinalStates());
		HashSet<Integer> barreer = new HashSet<Integer>(getFinalStates());

		while (!barreer.isEmpty()) {

			ArrayList<Move<P, S>> moves = new ArrayList<Move<P, S>>(getMovesTo(barreer));

			barreer = new HashSet<Integer>();
			for (Move<P, S> move : moves) {
				if (!reachedStates.contains(move.from)) {
					barreer.add(move.from);
					reachedStates.add(move.from);
				}
				LinkedList<S> newWit = new LinkedList<S>(witMap.get(move.to));
				if (!move.isEpsilonTransition()) {
					newWit.addFirst(move.getWitness(ba));
				}
				if (!witMap.containsKey(move.from))
					witMap.put(move.from, newWit);
				else {
					LinkedList<S> oldWit = witMap.get(move.from);
					if (oldWit.size() > newWit.size())
						witMap.put(move.from, newWit);
				}
			}

		}
		return witMap.get(getInitialState());
	}

	/**
	 * Returns true if the machine accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 * @throws TimeoutException 
	 */
	public boolean accepts(List<S> input, BooleanAlgebra<P, S> ba) throws TimeoutException {
		Collection<Integer> currConf = getEpsClosure(getInitialState(), ba);
		for (S el : input) {
			currConf = getNextState(currConf, el, ba);
			currConf = getEpsClosure(currConf, ba);
			if (currConf.isEmpty())
				return false;
		}

		return isFinalConfiguration(currConf);
	}

	// ------------------------------------------------------
	// Accessory functions
	// ------------------------------------------------------

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<Move<P, S>> getMoves() {
		return getMovesFrom(getStates());
	}

	/**
	 * Set of moves from state
	 */
	public abstract Collection<Move<P, S>> getMovesFrom(Integer state);

	/**
	 * Set of moves from set of states
	 */
	public Collection<Move<P, S>> getMovesFrom(Collection<Integer> states) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		for (Integer state : states)
			transitions.addAll(getMovesFrom(state));
		return transitions;
	}

	/**
	 * Set of moves to <code>state</code>
	 */
	public abstract Collection<Move<P, S>> getMovesTo(Integer state);

	/**
	 * Set of moves to a set of states <code>states</code>
	 */
	public Collection<Move<P, S>> getMovesTo(Collection<Integer> states) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		for (Integer state : states)
			transitions.addAll(getMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of states
	 */
	public abstract Collection<Integer> getStates();

	/**
	 * Returns initial state
	 */
	public abstract Integer getInitialState();

	/**
	 * Returns the set of final states
	 */
	public abstract Collection<Integer> getFinalStates();

	/**
	 * @return true if the set <code>conf</code> contains an initial state
	 */
	public boolean isInitialConfiguration(Collection<Integer> conf) {
		for (Integer state : conf)
			if (isInitialState(state))
				return true;
		return false;
	}

	/**
	 * @return true if <code>state</code> is an initial state
	 */
	public boolean isInitialState(Integer state) {
		return getInitialState() == state;
	}

	/**
	 * @return true if <code>conf</code> contains a final state
	 */
	public boolean isFinalConfiguration(Collection<Integer> conf) {
		for (Integer state : conf)
			if (isFinalState(state))
				return true;
		return false;
	}

	/**
	 * @return true if <code>state</code> is a final state
	 */
	public boolean isFinalState(Integer state) {
		return getFinalStates().contains(state);
	}

	// ------------------------------------------------------
	// Auxiliary protected functions
	// ------------------------------------------------------

	protected Collection<Integer> getEpsClosure(Integer state, BooleanAlgebra<P, S> ba) {

		HashSet<Integer> st = new HashSet<Integer>();
		st.add(state);
		return getEpsClosure(st, ba);
	}

	protected Collection<Integer> getEpsClosure(Collection<Integer> fronteer, BooleanAlgebra<P, S> ba) {

		Collection<Integer> reached = new HashSet<Integer>(fronteer);
		LinkedList<Integer> toVisit = new LinkedList<Integer>(fronteer);

		while (toVisit.size() > 0) {
			for (Move<P, S> t : getMovesFrom(toVisit.removeFirst())) {
				if (t.isEpsilonTransition()) {
					if (!reached.contains(t.to)) {
						reached.add(t.to);
						toVisit.add(t.to);
					}
				}
			}
		}
		return reached;
	}

	protected Collection<Integer> getNextState(Collection<Integer> currState, S inputElement, BooleanAlgebra<P, S> ba) throws TimeoutException {
		Collection<Integer> nextState = new HashSet<Integer>();
		for (Move<P, S> t : getMovesFrom(currState)) {
			if (!t.isEpsilonTransition()) {
				if (t.hasModel(inputElement, ba))
					nextState.add(t.to);
			}
		}

		return nextState;
	}

	/**
	 * If <code>state<code> belongs to reached returns reached(state) otherwise
	 * add state to reached and to toVisit and return corresponding id
	 */
	public static <T> int getStateId(T state, Map<T, Integer> reached, LinkedList<T> toVisit) {
		if (!reached.containsKey(state)) {
			int newId = reached.size();
			reached.put(state, newId);
			toVisit.add(state);
			return newId;
		} else
			return reached.get(state);
	}

	// ------------------------------------------------------
	// Getters
	// ------------------------------------------------------

	/**
	 * @return the isEmpty
	 */
	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * @return the isDeterministic
	 */
	public boolean isDeterministic() {
		return isDeterministic;
	}

	/**
	 * @return the isEpsilonFree
	 */
	public boolean isEpsilonFree() {
		return isEpsilonFree;
	}

	/**
	 * @return the isTotal
	 */
	public boolean isTotal() {
		return isTotal;
	}
}
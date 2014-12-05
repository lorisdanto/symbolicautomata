package automata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public abstract class Automaton<U, S> {

	public boolean isEmpty;
	public boolean isDeterministic;
	public boolean isEpsilonFree;
	public boolean isTotal;

	public Automaton() {
		isEmpty = false;
		isDeterministic = false;
		isEpsilonFree = true;
		isTotal = false;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public abstract Collection<Move<U, S>> getMoves();

	/**
	 * Returns the set of transitions starting set of states
	 */
	public abstract Collection<Move<U, S>> getMovesFrom(Integer state);

	/**
	 * Returns the set of transitions starting set of states
	 */
	public abstract Collection<Move<U, S>> getMovesTo(Integer state);

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
				for (Move<U, S> t : getMovesFrom(state))
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
		for (Move<U, S> t : getMoves())
			s = s + t + "\n";
		s += "Initial States \n";
		for (Integer is : getInitialStates())
			s = s + is + "\n";
		s += "Final States \n";
		for (Integer fs : getFinalStates())
			s = s + fs + "\n";
		return s;
	}
}

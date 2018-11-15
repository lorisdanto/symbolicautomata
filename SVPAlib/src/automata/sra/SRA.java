/**
 * SVPAlib
 * automata.sra
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.sat4j.specs.TimeoutException;


import theory.BooleanAlgebra;
import utilities.*;

/**
 * Symbolic Register Automaton
 * 
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRA<P, S> {

	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

    protected boolean isEmpty;
    protected boolean isDeterministic;
    protected boolean isTotal;
	protected boolean isSingleValued;

	private Integer initialState;
    private LinkedList<S> registers;
	private Collection<Integer> states;
	private Collection<Integer> finalStates;

	public Map<Integer, Collection<SRACheckMove<P, S>>> checkMovesFrom;
	public Map<Integer, Collection<SRACheckMove<P, S>>> checkMovesTo;
    public Map<Integer, Collection<SRAFreshMove<P, S>>> freshMovesFrom;
    public Map<Integer, Collection<SRAFreshMove<P, S>>> freshMovesTo;
	public Map<Integer, Collection<SRAStoreMove<P, S>>> storeMovesFrom;
	public Map<Integer, Collection<SRAStoreMove<P, S>>> storeMovesTo;
    public Map<Integer, Collection<SRAMove<P, S>>> SRAMovesFrom;
    public Map<Integer, Collection<SRAMove<P, S>>> SRAMovesTo;
	
    private Integer maxStateId;
	private Integer transitionCount;

	public void setIsDet(boolean b) {
		isDeterministic = b;
	}

	/**
	 * Returns the empty SRA for the Boolean algebra <code>ba</code>
	 * @throws TimeoutException 
	 */
	public static <A, B> SRA<A, B> getEmptySRA(BooleanAlgebra<A, B> ba) throws TimeoutException {
		SRA<A, B> aut = new SRA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>();
		aut.initialState = 0;
        aut.registers = new LinkedList<B>();
        aut.registers.add(null);
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.isTotal = true;
		aut.isSingleValued = true;
		aut.maxStateId = 1;
		aut.addTransition(new SRACheckMove<>(0, 0, ba.True(), 0), ba, false);
		aut.addTransition(new SRAFreshMove<>(0, 0, ba.True(), 0, aut.registers.size()), ba, false);
		return aut;
	}

	/**
	 * Returns the SRA accepting every string in the Boolean algebra
	 * <code>ba</code>
	 * @throws TimeoutException 
	 */
	public static <A, B> SRA<A, B> getFullSRA(BooleanAlgebra<A, B> ba) throws TimeoutException {
		SRA<A, B> aut = new SRA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>(aut.states);
		aut.initialState = 0;
        aut.registers = new LinkedList<B>();
        aut.registers.add(null);
		aut.isDeterministic = true;
		aut.isEmpty = false;
		aut.maxStateId = 1;
		aut.isSingleValued = false;
		aut.addTransition(new SRACheckMove<>(0, 0, ba.True(), 0), ba, false);
		aut.addTransition(new SRAFreshMove<>(0, 0, ba.True(), 0, aut.registers.size()), ba, false);
		return aut;
	}

    /**
	 * @return the maximum state id
	 */
	public Integer getMaxStateId() {
		return maxStateId;
	}

	/**
	 * @return number of states in the automaton
	 */
	public Integer stateCount() {
		return states.size();
	}

	/**
	 * @return number of transitions in the automaton
	 */
	public Integer getTransitionCount() {
		return transitionCount;
	}

    /**
     * @return the register list used by the automaton
     */
    public LinkedList<S> getRegisters() {
        return registers;
    }

	// ------------------------------------------------------
	// Constructors
	// ------------------------------------------------------

	// Initializes all the fields of the automaton
	private SRA() {
        isEmpty = false;
        isDeterministic = false;
        isTotal = false;
        isSingleValued = false;
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
        registers = new LinkedList<S>();
		checkMovesFrom = new HashMap<Integer, Collection<SRACheckMove<P, S>>>();
		checkMovesTo = new HashMap<Integer, Collection<SRACheckMove<P, S>>>();
        freshMovesFrom = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>();
        freshMovesTo = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>();
        storeMovesFrom = new HashMap<Integer, Collection<SRAStoreMove<P, S>>>();
        storeMovesTo = new HashMap<Integer, Collection<SRAStoreMove<P, S>>>();
        SRAMovesFrom = new HashMap<Integer, Collection<SRAMove<P, S>>>();
        SRAMovesTo = new HashMap<Integer, Collection<SRAMove<P, S>>>();
    	transitionCount = 0;
		maxStateId = 0;
	}

	/**
	 * Create an automaton and removes unreachable states
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, LinkedList<B> registers, BooleanAlgebra<A, B> ba) throws TimeoutException {
    
		return MkSRA(transitions, initialState, finalStates, registers, ba, true);
	}
	
	
	/**
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if <code>remUnreachableStates<code> is true
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, LinkedList<B> registers, BooleanAlgebra<A, B> ba, boolean remUnreachableStates)
					throws TimeoutException {

		return MkSRA(transitions, initialState, finalStates, registers, ba, remUnreachableStates, true);
	}

	/**
	 * Create an automaton and only removes unreachable states
	 * if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, LinkedList<B> registers, BooleanAlgebra<A, B> ba, boolean remUnreachableStates, boolean normalize)
					throws TimeoutException {

		SRA<A, B> aut = new SRA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = finalStates;

		if (finalStates.isEmpty())
			return getEmptySRA(ba);

		aut.registers = registers;

		// Check if there are duplicated values
		aut.isSingleValued = true;
		HashSet<B> nonEmptyRegValues = new HashSet<>();
		for (B regValue: registers) {
			if (regValue != null) {
				if (nonEmptyRegValues.contains(regValue)) {
					aut.isSingleValued = false;
					break;
				}
				else nonEmptyRegValues.add(regValue);
			}
		}


		
        for (SRAMove<A, B> t : transitions) {
			aut.addTransition(t, ba, false);
			if (aut.isSingleValued && t instanceof SRAStoreMove<?, ?>)
				aut.isSingleValued = false;
		}

		if (normalize)
			aut = aut.normalize(ba);

		if (remUnreachableStates)
			aut = removeDeadOrUnreachableStates(aut, ba);

		if (aut.finalStates.isEmpty() || aut.registers.isEmpty())
			return getEmptySRA(ba);

		return aut;
	}

	/**
	 * Gives the option to create an automaton exactly as given by the parameters, avoiding all normalizations.
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, LinkedList<B> registers, BooleanAlgebra<A, B> ba, boolean remUnreachableStates, boolean normalize, boolean keepEmpty)  
					throws TimeoutException {
		SRA<A, B> aut = new SRA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = finalStates;

        aut.registers = registers;

		// Check if there are duplicated values
		aut.isSingleValued = false;
		HashSet<B> nonEmptyRegValues = new HashSet<>();
		for (B regValue: registers) {
			if (regValue != null) {
				if (nonEmptyRegValues.contains(regValue)) {
					aut.isSingleValued = false;
					break;
				}
				else nonEmptyRegValues.add(regValue);
			}
		}

		for (SRAMove<A, B> t : transitions) {
			aut.addTransition(t, ba, true);

			if (aut.isSingleValued && t instanceof SRAStoreMove<?, ?>)
				aut.isSingleValued = false;
		}

		if (normalize)
			aut = aut.normalize(ba);

		if (remUnreachableStates)
			aut = removeDeadOrUnreachableStates(aut, ba);

		if (aut.finalStates.isEmpty() && !keepEmpty)
			return getEmptySRA(ba);

		return aut;
	}

	/**
	 * Adds a transition to the SRA.
	 *
	 * @throws TimeoutException
	 */
	private void addTransition(SRAMove<P, S> transition, BooleanAlgebra<P, S> ba, boolean skipSatCheck) throws TimeoutException {
		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.to > maxStateId)
				maxStateId = transition.to;

			states.add(transition.from);
			states.add(transition.to);

            if (transition instanceof SRACheckMove<?, ?>) {
				getCheckMovesFrom(transition.from).add((SRACheckMove<P, S>) transition);
				getCheckMovesTo(transition.to).add((SRACheckMove<P, S>) transition);
			} else if (transition instanceof SRAFreshMove<?, ?>) {
				getFreshMovesFrom(transition.from).add((SRAFreshMove<P, S>) transition);
				getFreshMovesTo(transition.to).add((SRAFreshMove<P, S>) transition);
			} else if (transition instanceof  SRAStoreMove<?, ?>) {
				isSingleValued = false; // Store moves are not allowed for non single-valued SRAs
				getStoreMovesFrom(transition.from).add((SRAStoreMove<P, S>) transition);
				getStoreMovesTo(transition.to).add((SRAStoreMove<P, S>) transition);
			} else {
				if (transition.E.size() == 1 && transition.I.isEmpty() && transition.U.isEmpty()) {
					getCheckMovesFrom(transition.from).add((new SRACheckMove<P, S>(transition.from, transition.to, transition.guard, transition.E.iterator().next())));
					getCheckMovesTo(transition.to).add((new SRACheckMove<P, S>(transition.from, transition.to, transition.guard, transition.E.iterator().next())));
				} else if (transition.E.isEmpty() && transition.I.size() == registers.size() && transition.U.size() == 1) {
					getFreshMovesFrom(transition.from).add((new SRAFreshMove<P, S>(transition.from, transition.to, transition.guard, transition.U.iterator().next(), registers.size())));
					getFreshMovesTo(transition.to).add((new SRAFreshMove<P, S>(transition.from, transition.to, transition.guard, transition.U.iterator().next(), registers.size())));
				} else if (transition.E.isEmpty() && transition.I.isEmpty() && transition.U.size() == 1) {
					getStoreMovesFrom(transition.from).add((new SRAStoreMove<P, S>(transition.from, transition.to, transition.guard, transition.U.iterator().next())));
					getStoreMovesTo(transition.to).add((new SRAStoreMove<P, S>(transition.from, transition.to, transition.guard, transition.U.iterator().next())));
				} else {
					isSingleValued = false;
					getSRAMovesFrom(transition.from).add(transition);
					getSRAMovesTo(transition.to).add(transition);
				}
			}
		}
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
				for (SRAMove<P, S> t : getMovesFrom(state))
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
		for (SRAMove<P, S> t : getMoves())
			s = s + t + "\n";

		s += "Initial State \n";
		s = s + getInitialState() + "\n";

		s += "Final States \n";
		for (Integer fs : getFinalStates())
			s = s + fs + "\n";
		return s;
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
		Collection<Configuration> currConf = new LinkedList<>();
		currConf.add(new Configuration(initialState, new LinkedList<>(registers)));

		for (S el : input) {
			currConf = getNextConfigurations(currConf, el, ba);

			if (currConf.isEmpty()) {
				return false;
			}

			if (currConf.size() > 1) {
				isDeterministic = false;
			}
		}
		return isFinalConfiguration(currConf);
	}

	// ------------------------------------------------------
	// Accessory functions
	// ------------------------------------------------------

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SRAMove<P, S>> getMoves() {
		return getMovesFrom(getStates());
	}

	/**
	 * Set of moves from state
	 */
	public Collection<SRAMove<P, S>> getMovesFrom(Integer state) {
		return new LinkedList<SRAMove<P, S>>(getTransitionsFrom(state));
	}

    /**
	 * Set of moves from set of states
	 */
	public Collection<SRAMove<P, S>> getMovesFrom(Collection<Integer> states) {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getMovesFrom(state));
		return transitions;
	}

	/**
	 * Set of moves to <code>state</code>
	 */
	public Collection<SRAMove<P, S>> getMovesTo(Integer state) {
		return getTransitionsTo(state);
	}

	/**
	 * Set of moves to a set of states <code>states</code>
	 */
	public Collection<SRAMove<P, S>> getMovesTo(Collection<Integer> states) {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of states
	 */
	public Collection<Integer> getStates() {
        return states;
    }

	/**
	 * Returns initial state
	 */
	public Integer getInitialState() {
        return initialState;
    }

	/**
	 * Returns the set of final states
	 */
	public Collection<Integer> getFinalStates() {
        return finalStates;
    }

    /**
     * Returns the set of non final states
     */
	public Collection<Integer> getNonFinalStates() {
		HashSet<Integer> nonFin = new HashSet<Integer>(states);
		nonFin.removeAll(finalStates);
		return nonFin;
	}

	private class Configuration {
		private Integer state;
		private LinkedList<S> regValues;

		public Configuration(Integer state, LinkedList<S> regValues) {
			this.state = state;
			this.regValues = regValues;
		}

		public String toString() {
			return "(" + state + "," + regValues + ")";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Configuration that = (Configuration) o;
			return Objects.equals(state, that.state) &&
					Objects.equals(regValues, that.regValues);
		}

		@Override
		public int hashCode() {
			return Objects.hash(state, regValues);
		}
	}

	/**
	 * @return true if the set <code>conf</code> contains an initial state
	 */
	public boolean isInitialConfiguration(Collection<Configuration> conf) {
		for (Configuration c: conf)
			if (isInitialState(c.state))
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
	public boolean isFinalConfiguration(Collection<Configuration> conf) {
		for (Configuration c: conf)
			if (isFinalState(c.state))
				return true;
		return false;
	}

	/**
	 * @return true if <code>state</code> is a final state
	 */
	public boolean isFinalState(Integer state) {
		return getFinalStates().contains(state);
	}

	protected Collection<Configuration> getNextConfigurations(Collection<Configuration> currConf,
															  S inputElement, BooleanAlgebra<P, S> ba) throws TimeoutException {
		Collection<Configuration> nextConfs = new HashSet<>();

		for (Configuration c: currConf) {
			for (SRAMove<P, S> t : getMovesFrom(c.state)) {
				if (t.hasModel(inputElement, ba, c.regValues)) {
					LinkedList<S> updatedReg = new LinkedList<>(c.regValues);
					for (Integer index : t.U)
						updatedReg.set(index, inputElement);

					nextConfs.add(new Configuration(t.to, updatedReg));
				}
			}
		}

		return nextConfs;
	}

	/**
	 * @return a list of predicates without duplicates
	 */
	private HashSet<P> getAllPredicates(long timeout) {
		HashSet<P> predicatesSet = new HashSet<>();

		HashMap<Integer, Integer> reached = new HashMap<>();
		LinkedList<Integer> toVisit = new LinkedList<>();

		reached.put(initialState, 0);
		toVisit.add(initialState);

		while (!toVisit.isEmpty()) {
			Integer curState = toVisit.removeFirst();

			for (SRAMove<P, S> ct : getMovesFrom(curState)) {
				predicatesSet.add(ct.guard);

				if (!reached.containsKey(ct.to)) {
					toVisit.add(ct.to);
					reached.put(ct.to, reached.size() + 1);
				}

			}

		}

		return predicatesSet;
	}

	// ------------------------------------------------------
	// Utility functions and classes for normalised SRA
	// ------------------------------------------------------

	// TODO: Should all these be static?

	/**
	 * Encapsulates minterm
	 */
	protected static class MinTerm<P> {

		private Pair<P, ArrayList<Integer>> data;

		MinTerm(P pred, ArrayList<Integer> bitVec) {
			data = new Pair<>(pred, bitVec);
		}

		P getPredicate() {
			return data.first;
		}

		ArrayList<Integer> getBitVector() {
			return data.second;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			MinTerm<?> minTerm = (MinTerm<?>) o;
			return getBitVector().equals(minTerm.getBitVector());
		}

		@Override
		public int hashCode() {
			return Objects.hash(data);
		}

		@Override
		public String toString() {
			return getPredicate().toString();
		}

	}

	/**
	 * Encapsulates normal SRA state
	 */
	static class NormSRAState<P> {
		private Pair<Integer, HashMap<Integer, MinTerm<P>>> data;

		NormSRAState(Integer stateID, HashMap<Integer, MinTerm<P>> regAbs) {
			data = new Pair(stateID, regAbs);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			NormSRAState<?> that = (NormSRAState<?>) o;
			return Objects.equals(data, that.data);
		}

		@Override
		public int hashCode() {
			return Objects.hash(data);
		}

		@Override
		public String toString() {
			return "<" +  getStateId() + " , " + getRegAbs() + ">";
		}

		Integer getStateId() {
			return data.first;
		}

		HashMap<Integer, MinTerm<P>> getRegAbs() {
			return data.second;
		}


	}

	protected static class NormSRAMove<P> {
		public NormSRAState<P> from;
		public NormSRAState<P> to;
		public MinTerm<P> guard;
		public Integer register;

		public NormSRAMove(NormSRAState<P> from, NormSRAState<P> to, MinTerm<P> guard, Integer register) {
			this.from = from;
			this.to = to;
			this.guard = guard;
			this.register = register;
		}
	}

	protected static class NormSRACheckMove<P> extends NormSRAMove<P> {

		public NormSRACheckMove(NormSRAState<P> from, NormSRAState<P> to, MinTerm<P> guard, Integer register) {
			super(from, to, guard, register);
		}

	}

	protected static class NormSRAFreshMove<P> extends NormSRAMove<P> {

		public NormSRAFreshMove(NormSRAState<P> from, NormSRAState<P> to, MinTerm<P> guard, Integer register) {
			super(from, to, guard, register);
		}

	}

	/**
	 * Encapsulates a reduced bisimulation triple
	 */
	protected  static class NormSimTriple<P> {
		Triple<NormSRAState<P>, NormSRAState<P>, HashMap<Integer, Integer>> data;
		NormSimTriple<P> previousTriple;
		P predicateLeadingTo;
		String bisimCase;

		NormSimTriple(NormSRAState<P> NormState1,
					  NormSRAState<P> NormState2,
					  HashMap<Integer, Integer> regMap,
					  NormSimTriple<P> previousTriple,
					  P predicateLeadingTo,
					  String bisimCase){
			data = new Triple<>(NormState1, NormState2, regMap);
			this.previousTriple = previousTriple;
			this.predicateLeadingTo = predicateLeadingTo;
			this.bisimCase = bisimCase;
		}

		NormSRAState<P> getState1() {
			return data.first;
		}

		NormSRAState<P> getState2() {
			return data.second;
		}

		HashMap<Integer, Integer> getRegMap(){
			return data.third;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			NormSimTriple<?> that = (NormSimTriple<?>) o;
			return Objects.equals(data, that.data);
		}

		@Override
		public int hashCode() {
			return Objects.hash(data);
		}

		@Override
		public String toString() {
			String result = "";
			if (predicateLeadingTo != null)
				result = "---" + predicateLeadingTo + "--->";

			result = result + "[ " + data.first + ", " + data.second + ", " + data.third + "] (case" + bisimCase +")";
			return result;
		}
	}



	/**
	 * Breaks down a SRA move into minterms.
	 * Only for single-valued SRAs
	 * @return a LinkedList of <code>NormSRAMove<P></></code>
	 */
	private static <P, S> LinkedList<NormSRAMove<P>> toNormSRAMoves(BooleanAlgebra<P, S> ba,
																  HashMap<Integer, MinTerm<P>> regAbs,
																  HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicate,
																  SRAMove<P, S> move,
																  NormSRAState<P> from) {

		LinkedList<NormSRAMove<P>> normMoves = new LinkedList<>();
		LinkedList<MinTerm<P>> minterms = mintermsForPredicate.get(move.guard);

		if (move instanceof SRACheckMove) {
            Integer register = move.E.iterator().next();
			MinTerm<P> registerMintInAbs = regAbs.get(register);

			if (registerMintInAbs != null && minterms.contains(registerMintInAbs)) {
				HashMap<Integer, MinTerm<P>> newRegAbs = new HashMap<>(regAbs);
				NormSRAState<P> targetState = new NormSRAState<>(move.to, newRegAbs);

				normMoves.add(new NormSRACheckMove<>(from, targetState, registerMintInAbs, register));
			}
		}
		else {
			for (MinTerm<P> mint: minterms) {
				Integer neededWitnessesForMint = 1;

				for (Integer r: regAbs.keySet()) {
					MinTerm<P> regMint = regAbs.get(r);

					if (regMint != null && regMint.equals(mint))
						neededWitnessesForMint++;
				}

				if (ba.hasNDistinctWitnesses(mint.getPredicate(), neededWitnessesForMint)) {
					HashMap<Integer, MinTerm<P>> newRegAbs = new HashMap<>(regAbs);
					newRegAbs.put(move.registerIndex, mint);
					NormSRAState<P> targetState = new NormSRAState<>(move.to, newRegAbs);
					normMoves.add(new NormSRAFreshMove<>(from, targetState, mint, move.registerIndex));
				}
			}
		}

		return normMoves;
	}

	/**
	 * Compute minterms where predicates are non-negated.
	 */
	private static <P> HashMap<P, LinkedList<MinTerm<P>>> getMintermsForPredicates(List<P> allPredicates, List<MinTerm<P>> minTerms) {
		HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates = new HashMap<>();

		for (P pred: allPredicates) {
			LinkedList<MinTerm<P>> mintList = new LinkedList<>();

			Integer predicateIndex = allPredicates.indexOf(pred);

			for (MinTerm<P> mint: minTerms) {
				if (mint.getBitVector().get(predicateIndex) == 1) // pred is non-negated in mint
					mintList.add(mint);
			}

			mintermsForPredicates.put(pred, mintList);
		}

		return mintermsForPredicates;
	}

	/**
	 * Create initial register abstraction
	 */
	private HashMap<Integer, MinTerm<P>> getInitialRegAbs(List<P> allPredicates,
														  BooleanAlgebra<P,S> ba,
														  //LinkedList<P> initAssAtoms,
														  HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates) {
		HashMap<Integer, MinTerm<P>> initRegAb = new HashMap<>();

		//Integer notNullInd = 0;

		for (Integer r = 0; r < registers.size(); r++) {
			S regVal = registers.get(r);

			if (regVal == null)
				initRegAb.put(r, null);
			else
			{
				P atom = ba.MkAtom(regVal);
				initRegAb.put(r, mintermsForPredicates.get(atom).get(0));
			}
		}
//		for (Integer r = 0; r < registers.size(); r++) {
//			// P atom = allPredicates.get(initValAtomsIndex + r);
//
//			if (registers.get(r) == null)
//				initRegAb.put(r, null);
//			else {
//				P atom = initAssAtoms.get(notNullInd);
//				initRegAb.put(r, mintermsForPredicates.get(atom).get(0)); // There should be only 1 minterm for atom
//				notNullInd++;
//			}
//		}

		return initRegAb;
	}

	/**
	 * Checks if the language accepted by an SRA is empty.
	 * @return true if empty, false if not empty.
	 */
	public static <P, S> boolean isLanguageEmpty(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
		long startTime = System.currentTimeMillis();

		if (aut.isEmpty)
			return true;

		if (!aut.isSingleValued)
			aut = aut.toSingleValuedSRA(ba, timeout);


		// Compute all minterms
		HashSet<P> allPredicatesSet = aut.getAllPredicates(timeout);
		// LinkedList<P> initAssAtoms = new LinkedList<>();


		for (S regVal : aut.registers) // Add initial register values to predicates
			if (regVal != null) {
				P atom = ba.MkAtom(regVal);

				//if (!allPredicates.contains(atom))
				allPredicatesSet.add(ba.MkAtom(regVal));

				//initAssAtoms.add(atom);
			}

		ArrayList<P> allPredicates = new ArrayList<>(allPredicatesSet);
		LinkedList<MinTerm<P>> minTerms = new LinkedList<>();

		for(Pair<P, ArrayList<Integer>> minBA: ba.GetMinterms(allPredicates))
			minTerms.add(new MinTerm<>(minBA.first, minBA.second));


		HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates = getMintermsForPredicates(allPredicates, minTerms);
		HashMap<Integer, MinTerm<P>> initRegAbs = aut.getInitialRegAbs(allPredicates, ba, mintermsForPredicates);



		// Create initial state of the normalised SRA
		NormSRAState<P> initNormState = new NormSRAState<>(aut.initialState, initRegAbs);

		// reached contains the product states (p,theta) we discovered and maps
		// them to a stateId
		HashMap<NormSRAState<P>, Integer> reached = new HashMap<>();
		// toVisit contains the product states we still have not explored
		LinkedList<NormSRAState<P>> toVisit = new LinkedList<>();

		toVisit.add(initNormState);
		reached.put(initNormState, 0);

		while (!toVisit.isEmpty()) {
			NormSRAState<P> currentState = toVisit.removeFirst();

			if (aut.finalStates.contains(currentState.getStateId()))
				return false;


			for (SRAMove<P, S> move: aut.getMovesFrom(currentState.getStateId())) {
				LinkedList<NormSRAMove<P>> normMoves =
						toNormSRAMoves(ba, currentState.getRegAbs(), mintermsForPredicates, move, null);

				if (System.currentTimeMillis() - startTime > timeout)
					throw new TimeoutException();

				for (NormSRAMove<P> normMove: normMoves) {
					NormSRAState<P> nextState = normMove.to;

					getStateId(nextState, reached, toVisit);
				}

			}
			
		}

		return true;
	}

	private static HashMap<Integer, Integer> getRegMapInv(HashMap<Integer, Integer> regMap) {
		HashMap<Integer, Integer> invRegMap = new HashMap<>();

		for (Integer r: regMap.keySet())
			invRegMap.put(regMap.get(r), r);

		return invRegMap;
	}



	public HashMap<Pair<Integer, Integer>, P> getPredMap(BooleanAlgebra<P, S> ba) throws TimeoutException {
		HashMap<Pair<Integer, Integer>, P> predMap = new HashMap<>();
		Integer regSize = registers.size();

		HashMap<Integer, Integer> reached = new HashMap<>();
		LinkedList<Integer> toVisit = new LinkedList<>();

		reached.put(initialState, 0);
		toVisit.add(initialState);

		while (!toVisit.isEmpty()) {
			Integer curState = toVisit.removeLast();
			Pair<Integer, Integer> newKey;
			boolean[] defReg = new boolean[regSize + 1];
			Arrays.fill(defReg, false);

			for (SRAMove<P, S> ct : getMovesFrom(curState)) {
				Integer moveReg;

				// TODO: Check case for store moves.
				if (ct instanceof SRACheckMove)
					moveReg = ct.registerIndex;
				else
					moveReg = regSize; // Conventionally for fresh moves

				defReg[moveReg] = true;
				newKey = new Pair<>(curState, moveReg);

				if (predMap.containsKey(newKey)) {
					P curPred = predMap.get(newKey);
					curPred = ba.MkOr(curPred, ct.guard);
					predMap.put(newKey, curPred);
				} else {
					predMap.put(newKey, ct.guard);
				}


				if (!reached.containsKey(ct.to)) {
					toVisit.add(ct.to);
					reached.put(ct.to, reached.size() + 1);
				}
			}

			// Put False in all other positions
			for (Integer i = 0; i <= regSize; i++) {
				if (!defReg[i]) {
					newKey = new Pair<>(curState, i);
					predMap.put(newKey, ba.False());
				}
			}
		}

		return predMap;
	}


	public void complete(BooleanAlgebra<P, S> ba) throws TimeoutException {
		// FIXME: something wrong here

		if (isEmpty)
			return; // empty SRA is already complete

		HashMap<Pair<Integer, Integer>, P> predMap = getPredMap(ba);
		Integer sinkState = stateCount();
		Integer regSize = registers.size();
		Integer chosenReg = 0;

		for (Pair<Integer, Integer> key: predMap.keySet()) {
			Integer state = key.first;
			Integer reg = key.second;
			P negPred = ba.MkNot(predMap.get(key));
			SRAMove<P, S> newMove;

			if (reg.equals(regSize))
				newMove = new SRAFreshMove<>(state, sinkState, negPred, chosenReg, regSize);
			else
				newMove = new SRACheckMove<>(state, sinkState, negPred, reg);



			addTransition(newMove, ba, false);
		}

		SRAMove<P,S> freshSinkLoop = new SRAFreshMove<>(sinkState, sinkState, ba.True(), chosenReg, regSize);
		addTransition(freshSinkLoop, ba, false);

		for (Integer r = 0; r < registers.size(); r++) {
			addTransition(new SRACheckMove<>(sinkState, sinkState, ba.True(), r), ba,false);
		}

		isTotal = true;
	}

	/**
	 * Checks if the language of an SRA is equivalent to the language of another SRA.
	 * @return true of it is equivalent, false if not.
	 */
	public boolean isLanguageEquivalent(SRA<P,S> aut, BooleanAlgebra<P,S> ba, long timeout) throws TimeoutException {
		SRA<P,S> aut1 = (SRA<P,S>) this.clone();
		SRA<P,S> aut2 = (SRA<P,S>) aut.clone();

		if (!aut1.isSingleValued)
			aut1 = aut1.toSingleValuedSRA(ba, timeout);

		if (!aut2.isSingleValued)
			aut2 = aut2.toSingleValuedSRA(ba, timeout);


		if (!aut1.isTotal)
			aut1.complete(ba);

		if (!aut2.isTotal)
			aut2.complete(ba);


		return canSimulate(aut1, aut2, ba, true, timeout);
	}

	/**
	 * Checks if the language of an SRA includes the language of another SRA.
	 * @return true of it includes the language, false if not.
	 */
	public boolean languageIncludes(SRA<P,S> aut, BooleanAlgebra<P,S> ba, long timeout) throws TimeoutException {
		SRA<P,S> aut1 = (SRA<P,S>) this.clone();
		SRA<P,S> aut2 = (SRA<P,S>) aut.clone();

		if (!aut1.isSingleValued)
			aut1 = aut1.toSingleValuedSRA(ba, timeout);

		if (!aut2.isSingleValued)
			aut2 = aut2.toSingleValuedSRA(ba, timeout);


		if (!aut1.isTotal)
			aut1.complete(ba);

		if (!aut2.isTotal)
			aut2.complete(ba);


		return canSimulate(aut2, aut1, ba, false, timeout);
	}


	private static <P> void printTriples(NormSimTriple<P> triple) {
		if (triple != null) {
			SRA.printTriples(triple.previousTriple);
			System.out.println(" " + triple);
		}
	}

	/**
	 * Compiles an SRA (multiple assignment or not), into a Normal SRA.
	 * @return a Normal SRA
	 */
	public SRA<P,S> toNormSRA(BooleanAlgebra<P,S> ba, Long timeout) {
		// Initial register map
		HashSet<P> allPredicatesSet = getAllPredicates(timeout);

		// Integer initValPos1 = allPredicates.size();

		for (P predicate: getAllPredicates(timeout))
			allPredicatesSet.add(predicate);

		for (S regVal: registers) // Add initial register values of aut1 to predicates
			if (regVal != null) {
				P atom = ba.MkAtom(regVal);
				allPredicatesSet.add(atom);
			}

		ArrayList<P> allPredicates = new ArrayList<>(allPredicatesSet);
		LinkedList<MinTerm<P>> minTerms = new LinkedList<>();

		for(Pair<P, ArrayList<Integer>> minBA: ba.GetMinterms(allPredicates))
			minTerms.add(new MinTerm<>(minBA.first, minBA.second));


		HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates = getMintermsForPredicates(allPredicates, minTerms);
		HashMap<Integer, MinTerm<P>> initRegAbs = getInitialRegAbs(allPredicates, ba, mintermsForPredicates);

		// reached contains the triples we have already discovered and maps them to a stateId
		HashMap<NormSRAState<P>, Integer> reached = new HashMap<>();
		// toVisit contains the triples we have not explored yet
		LinkedList<NormSRAState<P>> toVisit = new LinkedList<>();
		// LinkedList<NormSimTriple<P>> toVisitInv = new LinkedList<>();

		NormSRAState<P> initState = new NormSRAState<>(initialState, initRegAbs);

		// TODO: build normalised SRA and check if there is something wrong in it
		return null;
	}

	/**
	 * Checks if <code>aut1</code> can simulate <code>aut2</code>, checks for bisimulation if <code>bisimulation</code> is set to true.
	 * @return true if it simulates, false otherwise.
	 */
	public static <P, S> boolean canSimulate(SRA<P,S> aut1, SRA<P,S> aut2, BooleanAlgebra<P, S> ba, boolean bisimulation, long timeout)
			throws TimeoutException {

		if (aut1.isEmpty) {
			if (bisimulation && !aut2.isEmpty)
				return false;

			return true;
		}


		if(!aut1.isSingleValued)
			aut1 = aut1.toSingleValuedSRA(ba, timeout);

		if(!aut2.isSingleValued)
			aut2 = aut2.toSingleValuedSRA(ba, timeout);


		// Implement synchronised visit

		// Initial register map
		HashMap<Integer, Integer> initRegMap = new HashMap<>();
		HashMap<Integer, Integer> initRegMapInv = new HashMap<>();

		for (Integer r1 = 0; r1 < aut1.registers.size(); r1++) {
			for (Integer r2 = 0; r2 < aut2.registers.size(); r2++) {
				S r1Content = aut1.registers.get(r1);
				S r2Content = aut2.registers.get(r2);

				if (r1Content != null && r1Content.equals(r2Content)) {
					initRegMap.put(r1, r2);
					initRegMapInv.put(r2, r1);
				}
			}
		}

		// Get all predicates for both SRA
		HashSet<P> allPredicatesSet = aut1.getAllPredicates(timeout);

		// Integer initValPos1 = allPredicates.size();

		for (P predicate: aut2.getAllPredicates(timeout))
				allPredicatesSet.add(predicate);

		for (S regVal: aut1.registers) // Add initial register values of aut1 to predicates
			if (regVal != null) {
				P atom = ba.MkAtom(regVal);
				allPredicatesSet.add(atom);
			}

		// Integer initValPos2 = allPredicates.size();

		for (S regVal: aut2.registers) // Add initial register values of aut2 to predicates
			if (regVal != null) {
				P atom = ba.MkAtom(regVal);
				allPredicatesSet.add(ba.MkAtom(regVal));
			}


		// Compute minterms
		ArrayList<P> allPredicates = new ArrayList<>(allPredicatesSet);
		LinkedList<MinTerm<P>> minTerms = new LinkedList<>();

		for(Pair<P, ArrayList<Integer>> minBA: ba.GetMinterms(allPredicates))
			minTerms.add(new MinTerm<>(minBA.first, minBA.second));


		HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates = getMintermsForPredicates(allPredicates, minTerms);
		HashMap<Integer, MinTerm<P>> initRegAbs1 = aut1.getInitialRegAbs(allPredicates, ba, mintermsForPredicates);
		HashMap<Integer, MinTerm<P>> initRegAbs2 = aut2.getInitialRegAbs(allPredicates, ba, mintermsForPredicates);



		// Create initial triples
		NormSRAState<P> initNormState1 = new NormSRAState<>(aut1.initialState, initRegAbs1);
		NormSRAState<P> initNormState2 = new NormSRAState<>(aut2.initialState, initRegAbs2);

		NormSimTriple<P> initTriple = new NormSimTriple<>(initNormState1, initNormState2, initRegMap,
				null, null, null);

		// reached contains the triples we have already discovered and maps them to a stateId
		HashMap<NormSimTriple<P>, Integer> reached = new HashMap<>();
		// toVisit contains the triples we have not explored yet
		LinkedList<NormSimTriple<P>> toVisit = new LinkedList<>();
		// LinkedList<NormSimTriple<P>> toVisitInv = new LinkedList<>();

		toVisit.add(initTriple);
		// toVisitInv.add()
		reached.put(initTriple, 0);



		// Keep track of outgoing normalised transitions that have already been generated
		HashMap<NormSRAState<P>, LinkedList<NormSRAMove<P>>> aut1NormOut = new HashMap<>();
		HashMap<NormSRAState<P>, LinkedList<NormSRAMove<P>>> aut2NormOut = new HashMap<>();


		while (!toVisit.isEmpty()) {
			NormSimTriple<P> currentTriple = toVisit.removeLast(); // BFS visit

			NormSRAState<P> aut1NormState = currentTriple.getState1();
			NormSRAState<P> aut2NormState = currentTriple.getState2();
			HashMap<Integer, Integer> regMap = currentTriple.getRegMap();

			if (aut1.finalStates.contains(aut1NormState.getStateId()) &&
					!aut2.finalStates.contains(aut2NormState.getStateId())) {
//				printTriples(currentTriple);
				return false;
			}


			if (bisimulation)
				if (aut2.finalStates.contains(aut2NormState.getStateId()) &&
						!aut1.finalStates.contains(aut1NormState.getStateId())) {

//					printTriples(currentTriple);
					return false;
				}


			// int currentStateID = reached.get(currentTriple);

			HashMap<Integer, MinTerm<P>> currentRegAbs1 = aut1NormState.getRegAbs();
			HashMap<Integer, MinTerm<P>> currentRegAbs2 = aut2NormState.getRegAbs();

			// Compute all the normalised moves from aut1NormState and aut2NormState
			LinkedList<NormSRAMove<P>> normMovesFromCurrent1;
			LinkedList<NormSRAMove<P>> normMovesFromCurrent2;

			if (aut1NormOut.containsKey(aut1NormState))
				normMovesFromCurrent1 = aut1NormOut.get(aut1NormState);
			else {
				normMovesFromCurrent1 = new LinkedList<>();

				for (SRAMove<P, S> move : aut1.getMovesFrom(aut1NormState.getStateId())) {
					LinkedList<NormSRAMove<P>> partialNormMoves = toNormSRAMoves(ba, currentRegAbs1, mintermsForPredicates,
							move, aut1NormState);

					normMovesFromCurrent1.addAll(partialNormMoves);
				}

				aut1NormOut.put(aut1NormState, normMovesFromCurrent1);
			}

			if (!bisimulation && normMovesFromCurrent1.isEmpty()) // we don't need to find matching moves from aut2
				continue;

			if (aut2NormOut.containsKey(aut2NormState))
				normMovesFromCurrent2 = aut2NormOut.get(aut2NormState);
			else {
				normMovesFromCurrent2 = new LinkedList<>();

				for (SRAMove<P, S> move : aut2.getMovesFrom(aut2NormState.getStateId())) {
					LinkedList<NormSRAMove<P>> partialNormMoves = toNormSRAMoves(ba, currentRegAbs2, mintermsForPredicates,
							move, aut2NormState);

					normMovesFromCurrent2.addAll(partialNormMoves);
				}

				aut2NormOut.put(aut2NormState, normMovesFromCurrent2);
			}

			// Get new similarity triples
			LinkedList<NormSimTriple<P>> newTriples = normSimSucc(ba, normMovesFromCurrent1, normMovesFromCurrent2,
					regMap, currentRegAbs1, currentRegAbs2);

			if (newTriples == null) {
//				printTriples(currentTriple);
				return false;
			}

			if (bisimulation) {
				if (normMovesFromCurrent2.isEmpty()) // we don't need to find matching moves from aut1
					continue;

				LinkedList<NormSimTriple<P>> invTriples = normSimSucc(ba, normMovesFromCurrent2, normMovesFromCurrent1,
						getRegMapInv(regMap), currentRegAbs2, currentRegAbs1);

				if (invTriples == null) {
//					printTriples(currentTriple);
					return false;
				}
			}

			for (NormSimTriple<P> triple : newTriples) {
				triple.previousTriple = currentTriple;
				getStateId(triple, reached, toVisit);
			}

		}


		return true;
	}

	private static HashMap<Integer, Integer> updateRegMap(HashMap<Integer, Integer> regMap, Integer r1, Integer r2) {
		HashMap<Integer, Integer> newRegMap = new HashMap<>(regMap);

		// First remove pair (x, move2.register)
		for (Integer r: newRegMap.keySet()) {
			if (newRegMap.get(r).equals(r2)) {
				newRegMap.remove(r);
				break;
			}
		}

		newRegMap.put(r1, r2);
		return newRegMap;

	}

	// Returns all reduced bisimulation triples that need to be checked in subsequent steps
	private static <P, S> LinkedList<NormSimTriple<P>> normSimSucc(BooleanAlgebra<P, S> ba,
															   	   LinkedList<NormSRAMove<P>> normMoves1,
																   LinkedList<NormSRAMove<P>> normMoves2,
																   HashMap<Integer, Integer> regMap,
																   HashMap<Integer, MinTerm<P>> regAbs1,
																   HashMap<Integer, MinTerm<P>> regAbs2) {

		LinkedList<NormSimTriple<P>> nextTriples = new LinkedList<>();
		String bisimCase = "";

		for (NormSRAMove<P> move1: normMoves1) {
			if (move1 instanceof NormSRACheckMove) {
				Integer r1 = move1.register;
				NormSRAMove<P> matchingMove = null;
				HashMap<Integer, Integer> newRegMap = null;

				// Case 1(a) in the paper
				if (regMap.containsKey(r1)){
					Integer r2 = regMap.get(r1);

					for (NormSRAMove<P> move2: normMoves2) {
						if (move2 instanceof NormSRACheckMove && move2.register.equals(r2)) { // Guard is the same by construction
							matchingMove = move2;
							newRegMap = new HashMap<>(regMap);
							bisimCase = "read(" + r1 + ") - read(" + r2 +")";
							break;
						}
					}
				}
				else {
					// Case 1(b) in the paper
					for (NormSRAMove<P> move2: normMoves2) {
						if (move2 instanceof NormSRAFreshMove && move2.guard.equals(move1.guard)) {
							matchingMove = move2;
							newRegMap = updateRegMap(regMap, move1.register, move2.register);
							bisimCase = "read(" + move1.register + ") - fresh(" + move2.register +")";
							break;
						}
					}
				}

				if (matchingMove == null)
					return null;

				nextTriples.add(new NormSimTriple<>(move1.to, matchingMove.to, newRegMap, null,
						move1.guard.getPredicate(), bisimCase));
			}
			else {
				// Case 2(a)

				// regInImg(r) = false iff r not in img(regMap)
				Integer regNum2 = regAbs2.size();
				boolean[] regInImg = new boolean[regNum2];
				Arrays.fill(regInImg, false);

				for (Integer r1: regMap.keySet())
					regInImg[regMap.get(r1)] = true;

				for (int r2 = 0; r2 < regNum2; r2++) {
					MinTerm<P> mintermForReg = regAbs2.get(r2);

					if (mintermForReg != null && !regInImg[r2] && mintermForReg.equals(move1.guard)) {
						NormSRAMove<P> matchingMove = null;
						HashMap<Integer, Integer> newRegMap = null;

						for (NormSRAMove<P> move2: normMoves2) {
							if (move2 instanceof NormSRACheckMove && move2.register.equals(r2)) { // Guard must be the same
								matchingMove = move2;
								newRegMap = updateRegMap(regMap, move1.register, move2.register);
								bisimCase = "fresh(" + move1.register + ") - read(" + move2.register +")";
							}
						}

						if (matchingMove == null)
							return null;

						nextTriples.add(new NormSimTriple<>(move1.to, matchingMove.to, newRegMap, null,
								move1.guard.getPredicate(), bisimCase));
					}
				}

				// Case 2(b)
				Integer howManyEqualToGuard1 = 1;

				for (Integer reg: regAbs1.keySet()) {
					MinTerm<P> mintermForReg = regAbs1.get(reg);

					if (mintermForReg != null && regAbs1.get(reg).equals(move1.guard))
						howManyEqualToGuard1++;
				}

				for (Integer reg: regAbs2.keySet()) {
					MinTerm<P> mintermForReg = regAbs2.get(reg);

					if (mintermForReg != null && regAbs2.get(reg).equals(move1.guard))
						howManyEqualToGuard1++;
				}

				if (ba.hasNDistinctWitnesses(move1.guard.getPredicate(), howManyEqualToGuard1)) {
					NormSRAMove<P> matchingMove = null;
					HashMap<Integer, Integer> newRegMap = null;

					for (NormSRAMove<P> move2: normMoves2) {
						if (move2 instanceof NormSRAFreshMove && move2.guard.equals(move1.guard)) { // Guard must be the same
							matchingMove = move2;
							newRegMap = updateRegMap(regMap, move1.register, move2.register);
							bisimCase = "fresh(" + move1.register + ") - fresh(" + move2.register +")";
							break;
						}
					}

					if (matchingMove == null)
						return null;

					nextTriples.add(new NormSimTriple<>(move1.to, matchingMove.to, newRegMap, null,
							move1.guard.getPredicate(), bisimCase));
				}
			}

		}

		return nextTriples;
	}


	/**
	 * Compiles <code>this</code> down to an equivalent Single-valued SRA
	 *
	 * @throws TimeoutException
	 */
	public SRA<P, S> toSingleValuedSRA(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {

        long startTime = System.currentTimeMillis();

		// FIXME: check that, when isSingleValued == false, moves gets translated properly
        if (isSingleValued)
            return MkSRA(getTransitions(), initialState, finalStates, getRegisters(), ba, false, false);

        // If the automaton is empty return the empty SRA
        if (isEmpty)
            return getEmptySRA(ba);


		// Initialise register indexes
		HashSet<Integer> regIndexes = new HashSet<>();
		Integer regNum = registers.size();

		for (Integer r = 0; r < regNum; r++) // FIXME: no garbage register
			regIndexes.add(r);


		// components of target SRA
        Collection<SRAMove<P, S>> transitions = new ArrayList<SRAMove<P, S>>();
        LinkedList<S> newRegisters = new LinkedList<S>(registers);
        Collection<Integer> newFinalStates = new ArrayList<Integer>();

        // Add garbage collector register
        //newRegisters.add(registers.size(), null);

        HashMap<S, ArrayList<Integer>> valueToRegisters = new HashMap<S, ArrayList<Integer>>();
        for (Integer index = 0; index < regNum; index++) {
            S registerValue = newRegisters.get(index);
            if (valueToRegisters.containsKey(registerValue)) {
                valueToRegisters.get(registerValue).add(index);
            } else {
         //       if (registerValue != null) {
                    ArrayList<Integer> registersForValue = new ArrayList<Integer>();
                    registersForValue.add(index);
                    valueToRegisters.put(registerValue, registersForValue);
          //      }
            }
        }

        HashMap<Integer, Integer> initialMap = new HashMap<Integer, Integer>();
        for (ArrayList<Integer> repeatedRegisters : valueToRegisters.values()) {
            Integer firstElement = repeatedRegisters.get(0);
            initialMap.put(firstElement, firstElement);

            for (int i = 1; i < repeatedRegisters.size(); i++) {
                initialMap.put(repeatedRegisters.get(i), firstElement);
                newRegisters.set(repeatedRegisters.get(i), null);
            }
        }

//
//		newRegisters.add(newRegisters.size(), null);


        // reached contains the states (p,f) we discovered and maps
        // them to a stateId
        HashMap<Pair<Integer, HashMap<Integer,Integer>>, Integer> reached = new HashMap<Pair<Integer, HashMap<Integer,Integer>>, Integer>();
        // toVisit contains the product states we still have not explored
        LinkedList<Pair<Integer, HashMap<Integer,Integer>>> toVisit = new LinkedList<Pair<Integer, HashMap<Integer,Integer>>>();

        // The initial state is the pair consisting of the initial state (q0,f0)
        Pair<Integer, HashMap<Integer,Integer>> initPair = new Pair<Integer, HashMap<Integer,Integer>>(initialState, initialMap);
        reached.put(initPair, 0);
        toVisit.add(initPair);

        // Explore the product automaton until no new states can be reached
        while (!toVisit.isEmpty()) {

            Pair<Integer, HashMap<Integer, Integer>> currentState = toVisit.removeLast(); // BFS visit
            int currentStateID = reached.get(currentState);
            HashMap<Integer, Integer> currentMap = currentState.second;


            for (SRAMove<P, S> ct : getMovesFrom(currentState.first)) {
				LinkedList<SRAMove<P, S>> SRAMoves = new LinkedList<>();

				if (System.currentTimeMillis() - startTime > timeout)
					throw new TimeoutException();

				HashSet<HashSet<Integer>> compatibleSets = ct.getCompatibleSets(regIndexes);

				for (HashSet<Integer> set: compatibleSets) {
					Set<Integer> repeatedRegisters = new HashSet<>();

					// Rule (REG)
					// Check if there is register such that currentMap(S) = r
					for (Integer regSet : set) {
						Integer registerImg = currentMap.get(regSet);
						// if (registerImg != null)
						repeatedRegisters.add(registerImg);
					}

					if (repeatedRegisters.size() == 1)
						SRAMoves.add(new SRACheckMove<P, S>(currentStateID, null, ct.guard, repeatedRegisters.iterator().next()));

					if (set.isEmpty()) {
						// Compute inverse of currentMap
						HashMap<Integer, LinkedList<Integer>> inverseMap = new HashMap<>();

						for (Integer i = 0; i < regNum; i++) {
							Integer registerImg = currentMap.get(i);

							LinkedList<Integer> inverseImg;

							if (inverseMap.get(registerImg) == null) {
								inverseImg = new LinkedList<>();
								inverseMap.put(registerImg, inverseImg);
							} else
								inverseImg = inverseMap.get(registerImg);

							inverseImg.add(i);
						}

						// Rule (REG): check if there is register r such that r is not in img(currentMap)
						for (Integer r = 0; r < regNum; r++)
							if (inverseMap.get(r) == null)
								SRAMoves.add(new SRACheckMove<>(currentStateID, null, ct.guard, r));


						if (!ct.U.isEmpty()) {
							// Rule (FRESH)

							// Check whether inverseMap(r) is included in U, for some r
							// It takes the least r, which guarantees determinism
							for (Integer r = 0; r < regNum; r++) {
								LinkedList<Integer> inverseImg = inverseMap.get(r);

								if (inverseImg == null || ct.U.containsAll(inverseImg)) {
									SRAMoves.add(new SRAFreshMove<P, S>(currentStateID, null, ct.guard, r, newRegisters.size()));
									break;
								}
							}
						} else {
							// Rule (NOP)
							// FIXME: have a look at this
							// Integer garbageReg = newRegisters.size() - 1;
							// SRAMoves.add(new SRACheckMove<P, S>(currentStateID, null, ct.guard, garbageReg));
							// SRAMoves.add(new SRAFreshMove<P, S>(currentStateID, null, ct.guard, garbageReg, newRegisters.size()));
						}
					}
				}

                for (SRAMove<P, S> transition : SRAMoves) {
                    if (transition.isSatisfiable(ba)) {
                        HashMap<Integer, Integer> nextMap = new HashMap<>(currentMap);
                        Integer transitionRegister = transition.registerIndex;

                        for (Integer registersToUpdate : ct.U)
                            nextMap.put(registersToUpdate, transitionRegister);

                        Pair<Integer, HashMap<Integer, Integer>> nextState = new Pair<>(ct.to, nextMap);
                        transition.to = getStateId(nextState, reached, toVisit);

                        if (finalStates.contains(ct.to))
                            newFinalStates.add(transition.to);
                        transitions.add(transition);
                    }
                }

            }
        }
        return MkSRA(transitions, initialState, newFinalStates, newRegisters, ba);//, false, false);
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
	 * @return the isTotal
	 */
	public boolean isTotal() {
		return isTotal;
	}

    // ------------------------------------------------------
    // Boolean automata operations
    // ------------------------------------------------------

    /**
     * Computes the intersection with <code>aut</code> as a new SRA
     *
     * @throws TimeoutException
     */
    public SRA<P, S> intersectionWith(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
        return intersection(this, aut, ba, timeout);
    }

    /**
     * Computes the intersection with <code>aut</code> as a new SRA
     *
     * @throws TimeoutException
     */
    public SRA<P, S> intersectionWith(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
        return intersection(this, aut, ba, Long.MAX_VALUE);
    }

    /**
     * Computes the intersection with <code>aut1</code> and <code>aut2</code> as
     * a new SRA
     *
     * @throws TimeoutException
     */
    public static <A, B> SRA<A, B> intersection(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba, long timeout)
            throws TimeoutException {

        long startTime = System.currentTimeMillis();

        // if one of the automata is empty return the empty SRA
        if (aut1.isEmpty || aut2.isEmpty)
            return getEmptySRA(ba);

        // components of new SRA
        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
        Integer initialState = 0;
        Collection<Integer> finalStates = new ArrayList<Integer>();
        LinkedList<B> registers = new LinkedList<B>();
       
        // intersection registers are the union of register components
        registers.addAll(aut1.getRegisters());
        registers.addAll(aut2.getRegisters());

        // reached contains the product states (p1,p2) we discovered and maps
        // them to a stateId
        HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
        // toVisit contains the product states we still have not explored
        LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

        // The initial state is the pair consisting of the initial
        // states of aut1 and aut2
        Pair<Integer, Integer> initPair = new Pair<Integer, Integer>(aut1.initialState, aut2.initialState);
        reached.put(initPair, 0);
        toVisit.add(initPair);

        // Explore the product automaton until no new states can be reached
        while (!toVisit.isEmpty()) {

            Pair<Integer, Integer> currentState = toVisit.removeFirst();
            int currentStateID = reached.get(currentState);

            // Try to pair transitions out of both automata
            for (SRAMove<A, B> ct1 : aut1.getMovesFrom(currentState.first))
                for (SRAMove<A, B> ct2 : aut2.getMovesFrom(currentState.second)) {

                    if (System.currentTimeMillis() - startTime > timeout)
                        throw new TimeoutException();

                    // create conjunction of the two guards and create
                    // transition only if the conjunction is satisfiable
                    A intersGuard = ba.MkAnd(ct1.guard, ct2.guard);

                    // create union of the two E sets.
                    Set<Integer> intersE = new HashSet<Integer>();
                    intersE.addAll(ct1.E);
                    for (Integer registerE : ct2.E)
                        intersE.add(registerE + ct1.E.size());

                    // create union of the two I sets.
                    Set<Integer> intersI = new HashSet<Integer>();
                    intersI.addAll(ct1.I);
                    for (Integer registerI : ct2.I)
                        intersI.add(registerI + ct1.I.size());

                    // create union fo the two U sets.
                    Set<Integer> intersU = new HashSet<Integer>();
                    intersU.addAll(ct1.U);
                    for (Integer registerU : ct2.U)
                        intersU.add(registerU + ct1.U.size());
                    
                    // construct potential transition.
                    SRAMove<A, B> transition = new SRAMove<A, B>(currentStateID, null, intersGuard, intersE, intersI, intersU);

                    // if it is satisfiable, add nextStateID and update iteration lists.
                    if (transition.isSatisfiable(ba)) {
                        Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(ct1.to, ct2.to);
                        transition.to = getStateId(nextState, reached, toVisit);
                        if (aut1.finalStates.contains(ct1.to) || aut2.finalStates.contains(ct2.to))
                            finalStates.add(transition.to);
                        transitions.add(transition);
                    }
                }
        }

        return MkSRA(transitions, initialState, finalStates, registers, ba);
    }

	// ------------------------------------------------------
	// Other automata operations
	// ------------------------------------------------------

    /**
     * Creates a normalized copy of the SRA where all transitions between states
     * are collapsed taking their union, and states are renamed with 0,1,...     
     *
     * @throws TimeoutException
     */
    public SRA<P, S> normalize(BooleanAlgebra<P, S> ba) throws TimeoutException {
        return getNormalized(this, ba);
    }

    /**
     * Creates a normalized copy of <code>aut<code> where all transitions
     * between states are collapsed taking their union
     *
     * @throws TimeoutException
     */
    public static <A, B> SRA<A, B> getNormalized(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {

        if (aut.isEmpty)
            return getEmptySRA(ba);

        // components of new SRA
        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
        Integer initialState = aut.initialState;
        Collection<Integer> finalStates = new HashSet<Integer>(aut.finalStates);
        LinkedList<B> registers = aut.registers;

        // New moves
        Map<Pair<Integer, Integer>, Map<Integer, A>> checkMoves = new HashMap<>();
        Map<Pair<Integer, Integer>, Map<Integer, A>> freshMoves = new HashMap<>();
        Map<Pair<Integer, Integer>, Map<Integer, A>> storeMoves = new HashMap<>();
        Map<Pair<Integer, Integer>, Map<List<Set<Integer>>, A>> SRAMoves = new HashMap<>();

        // Create disjunction of all rules between same state and with the same operation
        for (SRAMove<A, B> move : aut.getMovesFrom(aut.states)) {
        	Pair<Integer, Integer> fromTo = new Pair(move.from, move.to);
			Map<Integer, A> regPredMap = null;
			Integer reg = null;
			Map<Pair<Integer, Integer>, Map<Integer, A>> moveMap = null;


			boolean isGenericMove = false;

            if (move instanceof SRACheckMove<?, ?>) {
				reg = move.E.iterator().next();
				moveMap = checkMoves;
			}
			else if(move instanceof SRAFreshMove) {
				reg = move.U.iterator().next();
				moveMap = freshMoves;
			}
			else if(move instanceof SRAStoreMove) {
				reg = move.U.iterator().next();
				moveMap = storeMoves;
			}
			else {
				isGenericMove = true;
			}

			if (!isGenericMove) {
				if (moveMap.containsKey(fromTo)) {
					regPredMap = moveMap.get(fromTo);

					if (regPredMap.containsKey(reg)) {
						A guard = regPredMap.get(reg);
						regPredMap.replace(reg, ba.MkOr(guard, move.guard));
					}
				} else {
					regPredMap = new HashMap<>();
					regPredMap.put(reg, move.guard);
					moveMap.put(fromTo, regPredMap);
				}
			}
			else
			{
				LinkedList<Set<Integer>> regConstraints = new LinkedList<>(Arrays.asList(move.E, move.I, move.U));
				Map<List<Set<Integer>>, A> regPredMapGen;

				if (SRAMoves.containsKey(fromTo)) {
					regPredMapGen = SRAMoves.get(fromTo);


					if (regPredMapGen.containsKey(regConstraints)) {
						A guard = regPredMapGen.get(regConstraints);
						regPredMapGen.replace(regConstraints, ba.MkOr(guard, move.guard));
					}
				} else {
					regPredMapGen = new HashMap<>();
					regPredMapGen.put(regConstraints, move.guard);
					SRAMoves.put(fromTo, regPredMapGen);
				}

			}


//                if (checkMoves.containsKey(fromTo))
//                    checkMoves.put(fromTo, new Pair<A, Integer>(ba.MkOr(move.guard, checkMoves.get(fromTo).first), move.E.iterator().next()));
//                else
//                    checkMoves.put(fromTo, new Pair<A, Integer>(move.guard, move.E.iterator().next()));
//            } else if (move instanceof SRAFreshMove<?, ?>) {
//                if (freshMoves.containsKey(fromTo))
//                    freshMoves.put(fromTo, new Pair<A, Integer>(ba.MkOr(move.guard, freshMoves.get(fromTo).first), move.U.iterator().next()));
//                else
//                    freshMoves.put(fromTo, new Pair<A, Integer>(move.guard, move.U.iterator().next()));
//            } else if (move instanceof SRAStoreMove<?, ?>) {
//                if (storeMoves.containsKey(fromTo))
//                    storeMoves.put(fromTo, new Pair<A, Integer>(ba.MkOr(move.guard, storeMoves.get(fromTo).first), move.U.iterator().next()));
//                else
//                    storeMoves.put(fromTo, new Pair<A, Integer>(move.guard, move.U.iterator().next()));
//            } else {
//                if (SRAMoves.containsKey(fromTo))
//                    SRAMoves.put(fromTo, new Pair<A, LinkedList<Set<Integer>>>(ba.MkOr(move.guard, SRAMoves.get(fromTo).first),
//                            new LinkedList<Set<Integer>>(Arrays.asList(move.E, move.I, move.U))));
//                else
//                    SRAMoves.put(fromTo, new Pair<A, LinkedList<Set<Integer>>>(move.guard,
//                            new LinkedList<Set<Integer>>(Arrays.asList(move.E, move.I, move.U))));
//            }
        }

        // Create the new transition function
        for (Pair<Integer, Integer> p : checkMoves.keySet()) {
			Map<Integer, A> regPredMap = checkMoves.get(p);

			for (Integer reg: regPredMap.keySet())
				transitions.add(new SRACheckMove<>(p.first, p.second, regPredMap.get(reg), reg));
		}

        for (Pair<Integer, Integer> p : freshMoves.keySet()) {
			Map<Integer, A> regPredMap = freshMoves.get(p);

			for (Integer reg : regPredMap.keySet())
				transitions.add(new SRAFreshMove<>(p.first, p.second, regPredMap.get(reg), reg, registers.size()));
		}

        for (Pair<Integer, Integer> p : storeMoves.keySet()) {
			Map<Integer, A> regPredMap = storeMoves.get(p);

			for (Integer reg : regPredMap.keySet())
				transitions.add(new SRAStoreMove<>(p.first, p.second, regPredMap.get(reg), reg));
		}


        for (Pair<Integer, Integer> p : SRAMoves.keySet()) {
			Map<List<Set<Integer>>, A> consPredMap = SRAMoves.get(p);

			for (List<Set<Integer>> regCons : consPredMap.keySet()) {
				Iterator<Set<Integer>> regConsIt = regCons.iterator();
				Set<Integer> E = regConsIt.next();
				Set<Integer> I = regConsIt.next();
				Set<Integer> U = regConsIt.next();

				transitions.add(new SRAMove<>(p.first, p.second, consPredMap.get(regCons), E, I, U));
			}
		}

        return MkSRA(transitions, initialState, finalStates, registers, ba, false, false);
    }

	// ------------------------------------------------------
	// Reachability methods
	// ------------------------------------------------------

	/**
	 * Creates a new SRA where all unreachable or dead states have been removed
	 * @return a minimal SRA.
	 */
	private static <A, B> SRA<A, B> removeDeadOrUnreachableStates(SRA<A, B> aut, BooleanAlgebra<A, B> ba)
			throws TimeoutException {

		// components of new SRA
		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();
        LinkedList<B> registers = aut.registers;

		HashSet<Integer> initStates = new HashSet<Integer>();
		initStates.add(aut.initialState);
		Collection<Integer> reachableFromInit = aut.getReachableStatesFrom(initStates);
		Collection<Integer> reachingFinal = aut.getReachingStates(aut.finalStates);

		Collection<Integer> aliveStates = new HashSet<Integer>();

		// Computes states that reachable from initial state and can reach a
		// final state
		for (Integer state : reachableFromInit)
			if (reachingFinal.contains(state)) {
				aliveStates.add(state);
			}

		if (aliveStates.size() == 0)
			return getEmptySRA(ba);

		for (Integer state : aliveStates)
			for (SRAMove<A, B> t : aut.getTransitionsFrom(state))
				if (aliveStates.contains(t.to))
					transitions.add(t);

		initialState = aut.initialState;

		for (Integer state : aut.finalStates)
			if (aliveStates.contains(state))
				finalStates.add(state);

		return MkSRA(transitions, initialState, finalStates, registers, ba, false, false);
	}

	/**
	 * Computes states that are reachable from states
	 * @return a collection of Integers.
	 */
	private Collection<Integer> getReachableStatesFrom(Collection<Integer> states) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (Integer state : states)
			visitForward(state, result);
		return result;
	}

	/**
	 * Computes states that can reach states
	 * @return a collection of Integers.
	 */
	private Collection<Integer> getReachingStates(Collection<Integer> states) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (Integer state : states)
			visitBackward(state, result);
		return result;
	}

	/**
	 * DFS accumulates in reached
	 */
	private void visitForward(Integer state, HashSet<Integer> reached) {
		if (!reached.contains(state)) {
			reached.add(state);
			for (SRAMove<P, S> t : this.getTransitionsFrom(state)) {
				Integer nextState = t.to;
				visitForward(nextState, reached);
			}
		}
	}

	/**
	 * backward DFS accumulates in reached
	 */
	private void visitBackward(Integer state, HashSet<Integer> reached) {
		if (!reached.contains(state)) {
			reached.add(state);
			for (SRAMove<P, S> t : this.getTransitionsTo(state)) {
				Integer pNormState = t.from;
				visitBackward(pNormState, reached);
			}
		}
	}

	// ------------------------------------------------------
	// Properties accessing methods
	// ------------------------------------------------------

	/**
	 * Returns the set of transitions starting at state <code>s</code>
	 */
	public Collection<SRAMove<P, S>> getTransitionsFrom(Integer state) {
		Collection<SRAMove<P, S>> moves = new HashSet<SRAMove<P, S>>();
		moves.addAll(getCheckMovesFrom(state));
        moves.addAll(getFreshMovesFrom(state));
        moves.addAll(getStoreMovesFrom(state));
        moves.addAll(getSRAMovesFrom(state));
		return moves;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SRAMove<P, S>> getTransitionsTo(Integer state) {
		Collection<SRAMove<P, S>> moves = new HashSet<SRAMove<P, S>>();
		moves.addAll(getCheckMovesTo(state));
        moves.addAll(getFreshMovesTo(state));
        moves.addAll(getStoreMovesTo(state));
        moves.addAll(getSRAMovesTo(state));
		return moves;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SRAMove<P, S>> getTransitionsFrom(Collection<Integer> stateSet) {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to a set of states
	 */
	public Collection<SRAMove<P, S>> getTransitionsTo(Collection<Integer> stateSet) {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	/**
	 * Returns the set of check transitions to state <code>state</code>
	 */
	public Collection<SRACheckMove<P, S>> getCheckMovesTo(Integer state) {
		return checkMovesTo.computeIfAbsent(state, k -> new HashSet<SRACheckMove<P, S>>());
	}

	/**
	 * Returns the set of check transitions to states <code>stateSet</code>
	 */
	public Collection<SRACheckMove<P, S>> getCheckMovesTo(Collection<Integer> stateSet) {
		Collection<SRACheckMove<P, S>> transitions = new LinkedList<SRACheckMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getCheckMovesTo(state));
		return transitions;
	}

    /**
	 * Returns the set of check transitions from state <code>state</code>
	 */
    public Collection<SRACheckMove<P, S>> getCheckMovesFrom(Integer state) {
        return checkMovesFrom.computeIfAbsent(state, k -> new HashSet<SRACheckMove<P, S>>());
    }

    /**
     * Returns the set of check transitions from states <code>stateSet</code>
     */
    public Collection<SRACheckMove<P, S>> getCheckMovesFrom(Collection<Integer> stateSet) {
        Collection<SRACheckMove<P, S>> transitions = new LinkedList<SRACheckMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getCheckMovesFrom(state));
        return transitions;
    }

    /**
     * Returns the set of fresh transitions to state <code>state</code>
     */
    public Collection<SRAFreshMove<P, S>> getFreshMovesTo(Integer state) {
        return freshMovesTo.computeIfAbsent(state, k -> new HashSet<SRAFreshMove<P, S>>());
    }

    /**
     * Returns the set of fresh transitions to states <code>stateSet</code>
     */
    public Collection<SRAFreshMove<P, S>> getFreshMovesTo(Collection<Integer> stateSet) {
        Collection<SRAFreshMove<P, S>> transitions = new LinkedList<SRAFreshMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getFreshMovesTo(state));
        return transitions;
    }

	/**
	 * Returns the set of fresh transitions to state <code>s</code>
	 */
	public Collection<SRAFreshMove<P, S>> getFreshMovesFrom(Integer state) {
		return freshMovesFrom.computeIfAbsent(state, k -> new HashSet<SRAFreshMove<P, S>>());
	}

    /**
     * Returns the set of fresh moves from states <code>stateSet</code>
     */
    public Collection<SRAFreshMove<P, S>> getFreshMovesFrom(Collection<Integer> stateSet) {
        Collection<SRAFreshMove<P, S>> transitions = new LinkedList<SRAFreshMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getFreshMovesFrom(state));
        return transitions;
    }

    /**
     * Returns the set of store transitions to state <code>state</code>
     */
    public Collection<SRAStoreMove<P, S>> getStoreMovesTo(Integer state) {
        return storeMovesTo.computeIfAbsent(state, k -> new HashSet<SRAStoreMove<P, S>>());
    }

    /**
     * Returns the set of store transitions to states <code>stateSet</code>
     */
    public Collection<SRAStoreMove<P, S>> getStoreMovesTo(Collection<Integer> stateSet) {
        Collection<SRAStoreMove<P, S>> transitions = new LinkedList<SRAStoreMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getStoreMovesTo(state));
        return transitions;
    }

    /**
     * Returns the set of store transitions to state <code>s</code>
     */
    public Collection<SRAStoreMove<P, S>> getStoreMovesFrom(Integer state) {
        return storeMovesFrom.computeIfAbsent(state, k -> new HashSet<SRAStoreMove<P, S>>());
    }

    /**
     * Returns the set of store moves from states <code>stateSet</code>
     */
    public Collection<SRAStoreMove<P, S>> getStoreMovesFrom(Collection<Integer> stateSet) {
        Collection<SRAStoreMove<P, S>> transitions = new LinkedList<SRAStoreMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getStoreMovesFrom(state));
        return transitions;
    }

    /**
     * Returns the set of multiple assignment transitions to state <code>state</code>
     */
    public Collection<SRAMove<P, S>> getSRAMovesTo(Integer state) {
        return SRAMovesTo.computeIfAbsent(state, k -> new HashSet<SRAMove<P, S>>());
    }

    /**
     * Returns the set of multiple assignment transitions to states <code>stateSet</code>
     */
    public Collection<SRAMove<P, S>> getSRAMovesTo(Collection<Integer> stateSet) {
        Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getSRAMovesTo(state));
        return transitions;
    }

    /**
     * Returns the set of multiple assignment transitions from state <code>state</code>
     */
    public Collection<SRAMove<P, S>> getSRAMovesFrom(Integer state) {
        return SRAMovesFrom.computeIfAbsent(state, k -> new HashSet<SRAMove<P, S>>());
    }

    /**
     * Returns the set of multiple assignment transitions from states <code>stateSet</code>
     */
    public Collection<SRAMove<P, S>> getSRAMovesFrom(Collection<Integer> stateSet) {
        Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getSRAMovesFrom(state));
        return transitions;
    }

	/**
	 * Returns the set of all transitions
	 */
	public Collection<SRAMove<P, S>> getTransitions() {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

    @Override
	public Object clone() {
		SRA<P, S> cl = new SRA<P, S>();

		cl.isDeterministic = isDeterministic;
		cl.isTotal = isTotal;
		cl.isEmpty = isEmpty;
		cl.isSingleValued = isSingleValued;
		cl.registers = new LinkedList<>(registers);

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.checkMovesFrom = new HashMap<Integer, Collection<SRACheckMove<P, S>>>(checkMovesFrom);
		cl.checkMovesTo = new HashMap<Integer, Collection<SRACheckMove<P, S>>>(checkMovesTo);

        cl.freshMovesFrom = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>(freshMovesFrom);
        cl.freshMovesTo = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>(freshMovesTo);

        cl.storeMovesFrom = new HashMap<Integer, Collection<SRAStoreMove<P, S>>>(storeMovesFrom);
        cl.storeMovesTo = new HashMap<Integer, Collection<SRAStoreMove<P, S>>>(storeMovesTo);

        cl.SRAMovesFrom = new HashMap<Integer, Collection<SRAMove<P, S>>>(SRAMovesFrom);
        cl.SRAMovesTo = new HashMap<Integer, Collection<SRAMove<P, S>>>(SRAMovesTo);

		return cl;
	}

}

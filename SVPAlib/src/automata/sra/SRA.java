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


import sun.awt.image.ImageWatched;
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
    protected boolean isMSRA;

	private Integer initialState;
    private LinkedList<S> registers;
	private Collection<Integer> states;
	private Collection<Integer> finalStates;

	public Map<Integer, Collection<SRACheckMove<P, S>>> checkMovesFrom;
	public Map<Integer, Collection<SRACheckMove<P, S>>> checkMovesTo;
    public Map<Integer, Collection<SRAFreshMove<P, S>>> freshMovesFrom;
    public Map<Integer, Collection<SRAFreshMove<P, S>>> freshMovesTo;
    public Map<Integer, Collection<MSRAMove<P, S>>> MAMovesFrom;
    public Map<Integer, Collection<MSRAMove<P, S>>> MAMovesTo;
	
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
		aut.maxStateId = 1;
		aut.addTransition(new SRAFreshMove<A, B>(0, 0, ba.True(), 0), ba, false);
        aut.addTransition(new SRACheckMove<A, B>(0, 0, ba.True(), 0), ba, false);
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
		aut.addTransition(new SRAFreshMove<A, B>(0, 0, ba.True(), 0), ba, false);
        aut.addTransition(new SRACheckMove<A, B>(0, 0, ba.True(), 0), ba, false);
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
        isMSRA = false;
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
        registers = new LinkedList<S>();
		checkMovesFrom = new HashMap<Integer, Collection<SRACheckMove<P, S>>>();
		checkMovesTo = new HashMap<Integer, Collection<SRACheckMove<P, S>>>();
        freshMovesFrom = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>();
        freshMovesTo = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>();
        MAMovesFrom = new HashMap<Integer, Collection<MSRAMove<P, S>>>();
        MAMovesTo = new HashMap<Integer, Collection<MSRAMove<P, S>>>();
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
		
        for (SRAMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

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

		for (SRAMove<A, B> t : transitions)
			aut.addTransition(t, ba, true);

		if (normalize)
			aut = aut.normalize(ba);

		if (remUnreachableStates)
			aut = removeDeadOrUnreachableStates(aut, ba);

		if (aut.finalStates.isEmpty() && !keepEmpty)
			return getEmptySRA(ba);

		return aut;
	}
	
	// Adds a transition to the SRA
	private void addTransition(SRAMove<P, S> transition, BooleanAlgebra<P, S> ba, boolean skipSatCheck) throws TimeoutException {
		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.to > maxStateId)
				maxStateId = transition.to;

			states.add(transition.from);
			states.add(transition.to);

            if (transition.isMultipleAssignment()) {
                MSRAMove<P, S> mTransition = transition.asMultipleAssignment();
                if (mTransition.E.size() == 1 && mTransition.U.isEmpty()) {
                    getCheckMovesFrom(transition.from).add((new SRACheckMove<P, S>(transition.from, transition.to, transition.guard, mTransition.E.iterator().next())));
                    getCheckMovesTo(transition.to).add((new SRACheckMove<P, S>(transition.from, transition.to, transition.guard, mTransition.E.iterator().next())));
                } else if (mTransition.E.isEmpty() && mTransition.U.size() == 1) {
                    getFreshMovesFrom(transition.from).add((new SRAFreshMove<P, S>(transition.from, transition.to, transition.guard, mTransition.U.iterator().next())));
                    getFreshMovesTo(transition.to).add((new SRAFreshMove<P, S>(transition.from, transition.to, transition.guard, mTransition.U.iterator().next())));
                } else {
                	isMSRA = true;
                    getMAMovesFrom(transition.from).add(mTransition);
                    getMAMovesTo(transition.to).add(mTransition);
                }
            } else if (transition.isFresh()) {
                getFreshMovesFrom(transition.from).add((SRAFreshMove<P, S>) transition);
                getFreshMovesTo(transition.to).add((SRAFreshMove<P, S>) transition);
            } else {
                getCheckMovesFrom(transition.from).add((SRACheckMove<P, S>) transition);
                getCheckMovesTo(transition.to).add((SRACheckMove<P, S>) transition);
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

//    /**
//     * Returns a sequence in the input domain that is accepted by the automaton
//     *
//     * @return a list in the domain language, null if empty
//     * @throws TimeoutException
//     */
//    public HashSet<List<S>> getWitnesses(BooleanAlgebra<P, S> ba, int howMany) throws TimeoutException {
//        if (isEmpty)
//            return null;
//
//        Map<Integer, HashSet<List<S>>> witMap = new HashMap<>();
//        for (Integer state : getFinalStates()) {
//            HashSet<List<S>> s = new HashSet<>();
//            s.add(new LinkedList<>());
//            witMap.put(state, s);
//        }
//
//        boolean somethingChanged = true;
//        while (somethingChanged && (witMap.get(getInitialState()) == null
//                || witMap.get(getInitialState()).size() < howMany)) {
//            somethingChanged = false;
//
//            for (SRAMove<P, S> move : getMoves()) {
//                HashSet<List<S>> prevStrings = new HashSet<>();
//
//                if (!witMap.containsKey(move.from)) {
//                    witMap.put(move.from, prevStrings);
//                } else {
//                    prevStrings = witMap.get(move.from);
//                }
//
//                int size = prevStrings.size();
//                if (witMap.containsKey(move.to)) {
//                    HashSet<List<S>> newStrings = new HashSet<List<S>>();
//                    for (List<S> str : witMap.get(move.to)) {
//                        if (!move.isEpsilonTransition()) {
//                            LinkedList<S> newStr = new LinkedList<S>(str);
//                            S wit = move.getWitness(ba, registers);
//                            newStr.addFirst(wit);
//                            newStrings.add(newStr);
//                        } else {
//                            newStrings.add(str);
//                        }
//                    }
//                    prevStrings.addAll(newStrings);
//                    if (prevStrings.size() > size)
//                        somethingChanged = true;
//                }
//            }
//        }
//        return witMap.get(getInitialState());
//    }

//    /**
//     * Returns a sequence in the input domain that is accepted by the automaton
//     *
//     * @return a list in the domain language, null if empty
//     * @throws TimeoutException
//     */
//    public List<S> getWitness(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        if (isEmpty)
//            return null;
//
//        Map<Integer, LinkedList<S>> witMap = new HashMap<Integer, LinkedList<S>>();
//        for (Integer state : getFinalStates())
//            witMap.put(state, new LinkedList<S>());
//
//        HashSet<Integer> reachedStates = new HashSet<Integer>(getFinalStates());
//        HashSet<Integer> barreer = new HashSet<Integer>(getFinalStates());
//
//        while (!barreer.isEmpty()) {
//
//            ArrayList<SRAMove<P, S>> moves = new ArrayList<SRAMove<P, S>>(getMovesTo(barreer));
//
//            barreer = new HashSet<Integer>();
//            for (SRAMove<P, S> move : moves) {
//                if (!reachedStates.contains(move.from)) {
//                    barreer.add(move.from);
//                    reachedStates.add(move.from);
//                }
//                LinkedList<S> newWit = new LinkedList<S>(witMap.get(move.to));
//                if (!move.isEpsilonTransition()) {
//                    newWit.addFirst(move.getWitness(ba, registers));
//                }
//                if (!witMap.containsKey(move.from))
//                    witMap.put(move.from, newWit);
//                else {
//                    LinkedList<S> oldWit = witMap.get(move.from);
//                    if (oldWit.size() > newWit.size())
//                        witMap.put(move.from, newWit);
//                }
//            }
//
//        }
//        return witMap.get(getInitialState());
//    }

	/**
	 * Returns true if the machine accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 * @throws TimeoutException 
	 */
	public boolean accepts(List<S> input, BooleanAlgebra<P, S> ba) throws TimeoutException {
	    LinkedList<S> cleanRegisters = new LinkedList<S>(registers);
		Collection<Integer> currConf = new LinkedList<Integer>();
        currConf.add(getInitialState());
		for (S el : input) {
			currConf = getNextState(currConf, el, ba);
			if (currConf.isEmpty())
				return false;
		}
        registers = cleanRegisters;
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
     * Returns the set of moves from a state as multiple assignment form
     */
    public Collection<MSRAMove<P, S>> getMovesFromAsMA(Integer state) {
        Collection<MSRAMove<P, S>> transitions = new LinkedList<MSRAMove<P, S>>();
        for (SRAMove<P, S> transition : getTransitionsFrom(state))
            transitions.add(transition.asMultipleAssignment());
        return transitions;
    }


    /**
     * Returns the set of moves from a set of states as multiple assignment form
     */
    public Collection<MSRAMove<P, S>> getMovesFromAsMA(Collection<Integer> states) {
        Collection<MSRAMove<P, S>> transitions = new LinkedList<MSRAMove<P, S>>();
        for (Integer state : states)
            transitions.addAll(getMovesFromAsMA(state));
        return transitions;
    }

	/**
	 * Set of moves to <code>state</code>
	 */
	public Collection<SRAMove<P, S>> getMovesTo(Integer state) {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		transitions.addAll(getTransitionsTo(state));
		return transitions;
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

	protected Collection<Integer> getNextState(Collection<Integer> currState, S inputElement, BooleanAlgebra<P, S> ba) throws TimeoutException {
		Collection<Integer> nextState = new HashSet<Integer>();
		for (SRAMove<P, S> t : getMovesFrom(currState)) {
			if (t.hasModel(inputElement, ba, registers)) {
                nextState.add(t.to);
                if (t.isMultipleAssignment())
                    for (Integer index : t.asMultipleAssignment().U)
                        registers.set(index, inputElement);
                if (t.isFresh())
                    registers.set(t.registerIndexes.iterator().next(), inputElement);
            }
		}
		return nextState;
	}



	private ArrayList<P> getAllPredicates(long timeout) {
		ArrayList<P> predicates = new ArrayList<>();

		HashMap<Integer, Integer> reached = new HashMap<>();
		// toVisit contains the product states we still have not explored
		LinkedList<Integer> toVisit = new LinkedList<>();

		reached.put(initialState, 0);
		toVisit.add(initialState);

		while (!toVisit.isEmpty()) {
			Integer curState = toVisit.removeFirst();

			for (SRAMove<P, S> ct : getMovesFrom(curState)) {
				if (!predicates.contains(ct.guard))
					predicates.add(ct.guard);

				if (!reached.containsKey(ct.to)) {
					toVisit.add(ct.to);
					reached.put(ct.to, reached.size() + 1);
				}

			}

		}

		return predicates;
	}

	// ------------------------------------------------------
	// Utility functions and classes for reduced SRA
	// ------------------------------------------------------

	// TODO: Should all these be static?

	// Encapsulates minterm
	protected static class MinTerm<P> extends Pair<P, ArrayList<Integer>> {

		protected MinTerm(P pred, ArrayList<Integer> bitVec) {
			super(pred, bitVec);
		}

		protected boolean equals(MinTerm<P> mt) {
			return this.second.equals(mt.second);
		}

		protected P getPredicate() {
			return this.first;
		}

		protected ArrayList<Integer> getBitVector() {
			return this.second;
		}

	}

	// Encapsulates reduced SRA state
	protected  static class RedSRAState<P> extends Pair<Integer, HashMap<Integer, MinTerm<P>>> {

		protected RedSRAState(Integer stateID, HashMap<Integer, MinTerm<P>> regAbs) {
			super(stateID, regAbs);
		}

		protected boolean equals(RedSRAState<P> rs) {
			return this.first.equals(rs.first) && this.second.equals(rs.second);
		}

		protected Integer getStateId() {
			return this.first;
		}

		protected HashMap<Integer, MinTerm<P>> getRegAbs() {
			return this.second;
		}
	}

	protected static class RedSRAMove<P> {
		public RedSRAState<P> from;
		public RedSRAState<P> to;
		public MinTerm<P> guard;
		public Integer register;

		public RedSRAMove(RedSRAState<P> from, RedSRAState<P> to, MinTerm<P> guard, Integer register) {
			this.from = from;
			this.to = to;
			this.guard = guard;
			this.register = register;
		}
	}

	protected static class RedSRACheckMove<P> extends RedSRAMove<P> {

		public RedSRACheckMove(RedSRAState<P> from, RedSRAState<P> to, MinTerm<P> guard, Integer register) {
			super(from, to, guard, register);
		}

	}

	protected static class RedSRAFreshMove<P> extends RedSRAMove<P> {

		public RedSRAFreshMove(RedSRAState<P> from, RedSRAState<P> to, MinTerm<P> guard, Integer register) {
			super(from, to, guard, register);
		}

	}

	// Encapsulates a reduced bisimulation triple
	protected  static class RedBisimTriple<P> extends Triple<RedSRAState<P>, RedSRAState<P>, HashMap<Integer, Integer>> {

		protected RedBisimTriple(RedSRAState<P> redState1, RedSRAState<P> redState2, HashMap<Integer, Integer> regMap) {
			super(redState1, redState2, regMap);
		}

//		protected boolean equals(RedSRAState<P> rs) {
//			return this.first.equals(rs.first) && this.second.equals(rs.second) &&
//		}

		protected RedSRAState<P> getState1() {
			return this.first;
		}

		protected RedSRAState<P> getState2() {
			return this.second;
		}

		protected HashMap<Integer, Integer> getRegMap(){
			return this.third;
		}
	}


	// Breaks down a SRA move into minterms
	private static <P, S> LinkedList<RedSRAMove<P>> toRedSRAMoves(BooleanAlgebra<P, S> ba,
																  HashMap<Integer, MinTerm<P>> regAbs,
																  HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicate,
																  SRAMove<P, S> move,
																  RedSRAState<P> from) {

		LinkedList<RedSRAMove<P>> redMoves = new LinkedList<>();
		LinkedList<MinTerm<P>> minterms = mintermsForPredicate.get(move.guard);

		Integer register = move.registerIndexes.iterator().next();

		if (move instanceof SRACheckMove) {
			MinTerm<P> registerMintInAbs = regAbs.get(register);

			if (registerMintInAbs != null && minterms.contains(registerMintInAbs)) {
				HashMap<Integer, MinTerm<P>> newRegAbs = new HashMap<>(regAbs);
				RedSRAState<P> targetState = new RedSRAState<>(move.to, newRegAbs);

				redMoves.add(new RedSRACheckMove<>(from, targetState, registerMintInAbs, register));
			}
		}
		else {
			for (MinTerm<P> mint: minterms) {
				Integer neededWitnessesForMint = 1;

				for (Integer r: regAbs.keySet()) {
					Pair<P, ArrayList<Integer>> regMint = regAbs.get(r);

					if (regMint != null && regMint.equals(mint))
						neededWitnessesForMint++;
				}

				if (ba.hasNDistinctWitnesses(mint.getPredicate(), neededWitnessesForMint)) {
					HashMap<Integer, MinTerm<P>> newRegAbs = new HashMap<>(regAbs);
					newRegAbs.put(move.registerIndexes.iterator().next(), mint);
					RedSRAState<P> targetState = new RedSRAState<>(move.to, newRegAbs);

					redMoves.add(new RedSRAFreshMove<>(from, targetState, mint, register));
				}
			}
		}

		return redMoves;
	}


	// Compute minterms where predicates are non-negated
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

	// Create initial register abstraction
	private HashMap<Integer, MinTerm<P>> getInitialRegAbs(List<P> allPredicates,
														  Integer initValAtomsIndex,
														  HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates) {
		HashMap<Integer, MinTerm<P>> initRegAb = new HashMap<>();

		for (Integer r = 0; r < registers.size(); r++)
			if (registers.get(r) != null) {
				P atom = allPredicates.get(initValAtomsIndex + r);
				initRegAb.put(r, mintermsForPredicates.get(atom).get(0)); // There should be only 1 minterm for atom
			}

		return initRegAb;
	}



	// Emptyness check

	public static <P, S> boolean checkEmptiness(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
		long startTime = System.currentTimeMillis();

		if (aut.isEmpty)
			return true;

		if (aut.isMSRA)
			aut = aut.compileToSRA(ba, timeout);

		// Compute all minterms
		ArrayList<P> allPredicates = aut.getAllPredicates(timeout);
		Integer initValPos = allPredicates.size();


		for (S regVal: aut.registers) // Add initial register values to predicates
			if (regVal != null)
				allPredicates.add(ba.MkAtom(regVal));

		LinkedList<MinTerm<P>> minTerms = new LinkedList<>();

		for(Pair<P, ArrayList<Integer>> minBA: ba.GetMinterms(allPredicates))
			minTerms.add(new MinTerm<>(minBA.first, minBA.second));


		HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates = getMintermsForPredicates(allPredicates, minTerms);
		HashMap<Integer, MinTerm<P>> initRegAbs = aut.getInitialRegAbs(allPredicates, initValPos, mintermsForPredicates);



		// Create initial state of the reduced SRA
		RedSRAState<P> initRedState = new RedSRAState<>(aut.initialState, initRegAbs);

		// reached contains the product states (p,theta) we discovered and maps
		// them to a stateId
		HashMap<RedSRAState<P>, Integer> reached = new HashMap<>();
		// toVisit contains the product states we still have not explored
		LinkedList<RedSRAState<P>> toVisit = new LinkedList<>();

		toVisit.add(initRedState);
		reached.put(initRedState, 0);

		while (!toVisit.isEmpty()) {
			RedSRAState<P> currentState = toVisit.removeFirst();

			if (aut.finalStates.contains(currentState.getStateId()))
				return false;

			int currentStateID = reached.get(currentState);


			for (SRAMove<P, S> move: aut.getMovesFrom(currentState.getStateId())) {
				LinkedList<RedSRAMove<P>> redMoves =
						toRedSRAMoves(ba, currentState.getRegAbs(), mintermsForPredicates, move, null);

				if (System.currentTimeMillis() - startTime > timeout)
					throw new TimeoutException();

				for (RedSRAMove<P> redMove: redMoves) {
					RedSRAState<P> nextState = redMove.to;

					aut.getStateId(nextState, reached, toVisit);
				}

			}
			
		}

		return true;
	}


	public boolean areHKEquivalent(SRA<P,S> aut1, SRA<P,S> aut2, BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {

		if(aut1.isMSRA)
			aut1 = aut1.compileToSRA(ba, timeout);

		if(aut2.isMSRA)
			aut2 = aut2.compileToSRA(ba, timeout);


		// Implement naive HK
		// TODO: see if we can use union-find

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

		// Get all predicates for both automata
		ArrayList<P> allPredicates = aut1.getAllPredicates(timeout);
		Integer initValPos1 = allPredicates.size();

		aut2.getAllPredicates(timeout).addAll(allPredicates);
		Integer initValPos2 = allPredicates.size();

		for (S regVal: aut1.registers) // Add initial register values of aut1 to predicates
			if (regVal != null)
				allPredicates.add(ba.MkAtom(regVal));

		for (S regVal: aut2.registers) // Add initial register values of aut2 to predicates
			if (regVal != null)
				allPredicates.add(ba.MkAtom(regVal));


		// Computer minterms
		LinkedList<MinTerm<P>> minTerms = new LinkedList<>();

		for(Pair<P, ArrayList<Integer>> minBA: ba.GetMinterms(allPredicates))
			minTerms.add(new MinTerm<>(minBA.first, minBA.second));


		HashMap<P, LinkedList<MinTerm<P>>> mintermsForPredicates = getMintermsForPredicates(allPredicates, minTerms);
		HashMap<Integer, MinTerm<P>> initRegAbs1 = aut1.getInitialRegAbs(allPredicates, initValPos1, mintermsForPredicates);
		HashMap<Integer, MinTerm<P>> initRegAbs2 = aut2.getInitialRegAbs(allPredicates, initValPos2, mintermsForPredicates);



		// Create initial triples
		RedSRAState<P> initRedState1 = new RedSRAState<>(aut1.initialState, initRegAbs1);
		RedSRAState<P> initRedState2 = new RedSRAState<>(aut2.initialState, initRegAbs2);

		RedBisimTriple<P> initTriple = new RedBisimTriple<>(initRedState1, initRedState2, initRegMap);
		RedBisimTriple<P> initTripleInv = new RedBisimTriple<>(initRedState2, initRedState1, initRegMapInv);

		// reached contains the triples we have already discovered and maps them to a stateId
		HashMap<RedBisimTriple<P>, Integer> reached = new HashMap<>();
		// toVisit contains the triples we have not explored yet
		LinkedList<RedBisimTriple<P>> toVisit = new LinkedList<>();

		toVisit.add(initTriple);
		toVisit.add(initTripleInv);
		reached.put(initTriple, 0);
		reached.put(initTriple, 1);

		// Keep track of outgoing reduced transitions that have been already generated
		HashMap<RedSRAState<P>, LinkedList<RedSRAMove<P>>> aut1RedOut = new HashMap<>();
		HashMap<RedSRAState<P>, LinkedList<RedSRAMove<P>>> aut2RedOut = new HashMap<>();


		while (!toVisit.isEmpty()) {
			RedBisimTriple<P> currentTriple = toVisit.removeFirst();

			RedSRAState<P> aut1RedState = currentTriple.getState1();
			RedSRAState<P> aut2RedState = currentTriple.getState2();
			HashMap<Integer, Integer> regMap = currentTriple.getRegMap();

			if (aut1.finalStates.contains(aut1RedState.getStateId()) && !aut2.finalStates.contains(aut2RedState.getStateId()))
				return false;

			int currentStateID = reached.get(currentTriple);

			HashMap<Integer, MinTerm<P>> currentRegAbs1 = aut1RedState.getRegAbs();
			HashMap<Integer, MinTerm<P>> currentRegAbs2 = aut2RedState.getRegAbs();

			// Compute all the reduced moves from aut1RedState and aut2RedState
			LinkedList<RedSRAMove<P>> redMovesFromCurrent1;
			LinkedList<RedSRAMove<P>> redMovesFromCurrent2;

			if (aut1RedOut.containsKey(aut1RedState))
				redMovesFromCurrent1 = aut1RedOut.get(aut1RedState);
			else {
				redMovesFromCurrent1 = new LinkedList<>();

				for (SRAMove<P, S> move : aut1.getMovesFrom(aut1RedState.getStateId())) {
					LinkedList<RedSRAMove<P>> partialRedMoves = toRedSRAMoves(ba, currentRegAbs1, mintermsForPredicates,
							move, aut1RedState);

					redMovesFromCurrent1.addAll(partialRedMoves);
				}
			}

			if (aut2RedOut.containsKey(aut2RedState))
				redMovesFromCurrent1 = aut2RedOut.get(aut2RedState);
			else {
				redMovesFromCurrent2 = new LinkedList<>();

				for (SRAMove<P, S> move : aut2.getMovesFrom(aut2RedState.getStateId())) {
					LinkedList<RedSRAMove<P>> partialRedMoves = toRedSRAMoves(ba, currentRegAbs2, mintermsForPredicates,
							move, aut2RedState);

					redMovesFromCurrent2.addAll(partialRedMoves);
				}
			}




		}


		return true;
	}


	// Returns all reduced bisimulation triples that need to be checked in subsequent steps
	private static <P, S> LinkedList<RedBisimTriple<P>> redBisimSucc(LinkedList<RedSRAMove<P>> redMoves1,
																	 LinkedList<RedSRAMove<P>> redMoves2,
																	 HashMap<Integer, Integer> regMap,
																	 HashMap<Integer, MinTerm<P>> regAbs2,
																	 Integer regNum1, Integer regNum2) {

		LinkedList<RedBisimTriple<P>> nextTriples = new LinkedList<>();


		for (RedSRAMove<P> move1: redMoves1) {
			if (move1 instanceof RedSRACheckMove) {
				Integer r1 = move1.register;
				RedSRAMove<P> matchingMove = null;
				HashMap<Integer, Integer> newRegMap = null;

				// Case 1(a) in the paper
				if (regMap.containsKey(r1)){
					Integer r2 = regMap.get(r1);

					for (RedSRAMove<P> move2: redMoves2) {
						if (move2 instanceof RedSRACheckMove && move2.register.equals(r2)) { // Guard is the same by construction
							matchingMove = move2;
							newRegMap = new HashMap<>(regMap);
							break;
						}
					}
				}
				else {
					// Case 1(b) in the paper
					for (RedSRAMove<P> move2: redMoves2) {
						if (move2 instanceof RedSRAFreshMove && move2.guard.equals(move1.guard)) {
							matchingMove = move2;
							newRegMap = new HashMap<>(regMap);
							newRegMap.put(move1.register, move2.register);
							break;
						}
					}
				}

				if (matchingMove == null)
					return null;

				nextTriples.add(new RedBisimTriple<>(move1.to, matchingMove.to, newRegMap));
			}
			else {
				// Case 2(a)

				// regInImg(r) = false iff r not in img(regMap)
				boolean[] regInImg = new boolean[regNum2];
				Arrays.fill(regInImg, false);

				for (Integer r1: regMap.keySet())
					regInImg[regMap.get(r1)] = true;

				for (int r2 = 0; r2 < regNum2; r2++) {
					if (!regInImg[r2] && regAbs2.get(r2).equals(move1.guard)) {
						RedSRAMove<P> matchinMove;
						for (RedSRAMove<P> move2: redMoves2) {
							if (move2 instanceof Red SRAFreshMove && move2.guard.equals(move1.guard)) {
								matchingMove = move2;
								newRegMap = new HashMap<>(regMap);
								newRegMap.put(move1.register, move2.register);
								break;
							}
						}
					}

				}
			}

		}

		return null;
	}


	/**
	 * Compiles <code>this</code> down to an equivalent SRA
	 *
	 * @throws TimeoutException
	 */
	public SRA<P, S> compileToSRA(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {

        long startTime = System.currentTimeMillis();

        // If the automaton doesn't contain MA moves, it's already an SRA
        if (!isMSRA)
            return MkSRA(getTransitions(), initialState, finalStates, registers, ba);

        // If the automaton is empty return the empty SRA
        if (isEmpty)
            return getEmptySRA(ba);

        // components of target SRA
        Collection<SRAMove<P, S>> transitions = new ArrayList<SRAMove<P, S>>();
        LinkedList<S> newRegisters = new LinkedList<S>(registers);
        Collection<Integer> newFinalStates = new ArrayList<Integer>();

        HashMap<S, ArrayList<Integer>> valueToRegisters = new HashMap<S, ArrayList<Integer>>();
        for (Integer index = 0; index < registers.size(); index++) {
            S registerValue = registers.get(index);
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

            Pair<Integer, HashMap<Integer, Integer>> currentState = toVisit.removeFirst();
            int currentStateID = reached.get(currentState);
            HashMap<Integer, Integer> currentMap = currentState.second;

            for (MSRAMove<P, S> ct : getMovesFromAsMA(currentState.first)) { // I like the function getMovesFromAsMA, we can have an analogous one for reduced SRA
				LinkedList<SRAMove<P, S>> SRAMoves = new LinkedList<>();

				if (System.currentTimeMillis() - startTime > timeout)
					throw new TimeoutException();

                if (!ct.E.isEmpty()) {
                    // Case 1 in the paper: check whether there is a register r such that currentMap(E) = r
                    Set<Integer> repeatedRegisters = new HashSet<>();
                    for (Integer registerE : ct.E) {
                        Integer registerEImg = currentMap.get(registerE);
                        if (registerEImg != null)
                            repeatedRegisters.add(registerEImg);
                    }

                    if (repeatedRegisters.size() == 1)
                        SRAMoves.add(new SRACheckMove<P, S>(currentStateID, null, ct.guard, repeatedRegisters.iterator().next()));
                } else {

					// Compute inverse
					HashMap<Integer, LinkedList<Integer>> inverseMap = new HashMap<>();

					for (Integer i = 0; i < registers.size(); i++) {
						Integer registerImg = currentMap.get(i);

						LinkedList<Integer> inverseImg;

						if (inverseMap.get(registerImg) == null) {
							inverseImg = new LinkedList<>();
							inverseMap.put(registerImg, inverseImg);
						}
						else
							inverseImg = inverseMap.get(registerImg);

						inverseImg.add(i);
					}

					// Case 2 in the paper: check whether inverseMap(r) is included in U, for some r
					for (Integer i = 0; i < registers.size(); i++) {
						LinkedList<Integer> inverseImg = inverseMap.get(i);

						if (inverseImg == null || ct.U.containsAll(inverseImg)) {
							SRAMoves.add(new SRAFreshMove<P, S>(currentStateID, null, ct.guard, i));
							break;
						}
					}


                    // Case 3 in the paper: check whether inverseMap(r) is empty, for some r
					for (Integer i = 0; i < registers.size(); i++)
						if (inverseMap.get(i) == null)
	                        SRAMoves.add(new SRACheckMove<P, S>(currentStateID, null, ct.guard, i));
                }


                for (SRAMove<P, S> transition : SRAMoves) {
                    if (transition.isSatisfiable(ba)) {
                        HashMap<Integer, Integer> nextMap = new HashMap<>(currentMap);
                        Integer transitionRegister = transition.registerIndexes.iterator().next();

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
        return MkSRA(transitions, initialState, newFinalStates, newRegisters, ba);

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
    // FIXME: At the moment I doubt most of these work.
    //        We need to get basic transition operations working first.

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
            for (MSRAMove<A, B> ct1 : aut1.getMovesFromAsMA(currentState.first))
                for (MSRAMove<A, B> ct2 : aut2.getMovesFromAsMA(currentState.second)) {

                    if (System.currentTimeMillis() - startTime > timeout)
                        throw new TimeoutException();

                    // create conjunction of the two guards and create
                    // transition only if the conjunction is satisfiable
                    A intersGuard = ba.MkAnd(ct1.guard, ct2.guard);

                    // create union of the two E sets.
                    Collection<Integer> intersE = new HashSet<Integer>();
                    intersE.addAll(ct1.E);
                    for (Integer registerE : ct2.E)
                        intersE.add(registerE + ct1.E.size());

                    // create union fo the two U sets.
                    Collection<Integer> intersU = new HashSet<Integer>();
                    intersU.addAll(ct1.U);
                    for (Integer registerU : ct2.U)
                        intersU.add(registerU + ct1.U.size());
                    
                    // construct potential transition.
                    MSRAMove<A, B> transition = new MSRAMove<A, B>(currentStateID, null, intersGuard, intersE, intersU);

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

//    /**
//     * Computes <code>this</code> minus <code>aut</code> as a new SRA
//     *
//     * @throws TimeoutException
//     */
//    public SRA<P, S> minus(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return difference(this, aut, ba, Long.MAX_VALUE);
//    }
//
//    /**
//     * Computes <code>this</code> minus <code>aut</code> as a new SRA
//     *
//     * @throws TimeoutException
//     */
//    public SRA<P, S> minus(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
//        return difference(this, aut, ba, timeout);
//    }
//
//    /**
//     * Computes <code>aut1</code> minus <code>aut2</code> as a new SRA
//     *
//     * @throws TimeoutException
//     */
//    public static <A, B> SRA<A, B> difference(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba, long timeout)
//            throws TimeoutException {
//        long startTime = System.currentTimeMillis();
//        SRA<A, B> compAut2 = aut2.complement(ba, timeout);
//        return aut1.intersectionWith(compAut2, ba, timeout - (System.currentTimeMillis() - startTime));
//    }
//
//    /**
//     * Computes the union with <code>aut</code> as a new SRA
//     *
//     * @throws TimeoutException
//     */
//    public SRA<P, S> unionWith(SRA<P, S> aut1, BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return union(this, aut1, ba);
//    }
//
//    /**
//     * Computes the union of <code>aut1</code> and <code>aut2</code> as a new
//     * SRA
//     *
//     * @throws TimeoutException
//     */
//    public static <A, B> SRA<A, B> union(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
//            throws TimeoutException {
//
//        // if both automata are empty return the empty SRA
//        if (aut1.isEmpty && aut2.isEmpty)
//            return getEmptySRA(ba);
//
//        // components of new SRA
//        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
//        Integer initialState;
//        Collection<Integer> finalStates = new ArrayList<Integer>();
//
//        // Offset will be add to all states of aut2
//        // to ensure that the states of aut1 and aut2 are disjoint
//        int offSet = aut1.maxStateId + 2;
//
//        // Copy the moves of aut1 in transitions
//        for (SRAMove<A, B> t : aut1.getTransitions()) {
//            @SuppressWarnings("unchecked")
//            SRAMove<A, B> newMove = (SRAMove<A, B>) t.clone();
//            transitions.add(newMove);
//        }
//
//        // Copy the moves of aut2 in transitions
//        // and shift the states by offset
//        for (SRAMove<A, B> t : aut2.getTransitions()) {
//            @SuppressWarnings("unchecked")
//            SRAMove<A, B> newMove = (SRAMove<A, B>) t.clone();
//            newMove.from += offSet;
//            newMove.to += offSet;
//            transitions.add(newMove);
//        }
//
//        // the new initial state is the first available id
//        initialState = aut2.maxStateId + offSet + 1;
//
//        // Add transitions from new initial state to
//        // the the initial state of aut1 and
//        // the initial state of aut2 shifted by offset
//        transitions.add(new SRAEpsilon<A, B>(initialState, aut1.initialState));
//        transitions.add(new SRAEpsilon<A, B>(initialState, aut2.initialState + offSet));
//
//        // Make all states of the two machines final
//        finalStates.addAll(aut1.finalStates);
//
//        // make all state of aut2 final after adding the offsett
//        for (Integer state : aut2.finalStates)
//            finalStates.add(state + offSet);
//
//        return MkSRA(transitions, initialState, finalStates, ba, false);
//    }
//
//    /**
//     * @return the complement automaton as a new SRA
//     * @throws TimeoutException
//     */
//    public SRA<P, S> complement(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return complementOf(this, ba, Long.MAX_VALUE);
//    }
//
//    /**
//     * @return the complement automaton as a new SRA
//     * @throws TimeoutException
//     */
//    public SRA<P, S> complement(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
//        return complementOf(this, ba, timeout);
//    }
//
//    /**
//     * @return the complement of <code>aut</code> as a new SRA
//     * @throws TimeoutException
//     */
//    public static <A, B> SRA<A, B> complementOf(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
//            throws TimeoutException {
//
//        // make aut total to make sure it has a sink state
//        SRA<A, B> autTotal = aut.mkTotal(ba, timeout);
//
//        // the final states of the complement are
//        // autTotal.states minus autTotal.finalStates
//        Collection<Integer> newFinalStates = new HashSet<Integer>();
//        for (Integer st : autTotal.states)
//            if (!autTotal.finalStates.contains(st))
//                newFinalStates.add(st);
//
//        return MkSRA(autTotal.getTransitions(), autTotal.initialState, newFinalStates, ba, false);
//    }
//
//    /** Remove epsilon transitions and collapses transitions to same state by taking the union of their predicates
//     * @throws TimeoutException
//     */
//    public static <A, B> SRA<A, B> collapseMultipleTransitions(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
//            throws TimeoutException {
//
//        // make aut total to make sure it has a sink state
//        SRA<A, B> autTotal = aut.mkTotal(ba, timeout);
//
//        Map<Pair<Integer, Integer>, A> newMovesMap = new HashMap<Pair<Integer,Integer>, A>();
//        for(int state1:autTotal.states)
//            for(int state2:autTotal.states)
//                newMovesMap.put(new Pair<Integer, Integer>(state1, state2), ba.False());
//
//        for(SRAMove<A, B> move : autTotal.getMovesFrom(autTotal.states)){
//            Pair<Integer, Integer> key = new Pair<>(move.from,move.to);
//            A currentPred = newMovesMap.get(key);
//            newMovesMap.put(key, ba.MkOr(currentPred, move.guard));
//        }
//
//        Collection<SRAMove<A, B>> newMoves = new HashSet<>();
//        for(Pair<Integer, Integer> key: newMovesMap.keySet())
//            newMoves.add(new SRAMove<A, B>(key.first, key.second, newMovesMap.get(key)));
//
//        return MkSRA(newMoves, autTotal.initialState, autTotal.finalStates, ba, false);
//    }

	// ------------------------------------------------------
	// Other automata operations
	// ------------------------------------------------------

    /**
     * @return a new reduced SRA.
     * @throws TimeoutException
     */
	public SRA<P, S> mkReduced(BooleanAlgebra<P, S> ba) throws TimeoutException {
        // 1: Get all transitions from SRA.
        // 2: Get all possible combinations of minterms (keyword *possible*)
        // 3: Get transitions from q0.
        // 3: Apply AND of phi0 (from q0) on set of possible combinations of predicates and register req.
        //    (==registers[rIndex] for check transitions or registers.contains(Input) == false)
        // 4: If the transition can happen, add transition from q0 to final state in original SRA transition.
        // 5:
		return null;
    }
//       /**
//     * @return a new total equivalent total SRA (with one transition for each
//     *         symbol out of every state)
//     * @throws TimeoutException
//     */
//    public SRA<P, S> mkTotal(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return mkTotal(this, ba, Long.MAX_VALUE);
//    }
//
//    /**
//     * @return a new total equivalent total SRA (with one transition for each
//     *         symbol out of every state)
//     * @throws TimeoutException
//     */
//    public SRA<P, S> mkTotal(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
//        return mkTotal(this, ba, timeout);
//    }
//
//    /**
//     * @return a new total total SRA (with one transition for each symbol out of
//     *         every state) equivalent to <code>aut</code>
//     * @throws TimeoutException
//     */
//    @SuppressWarnings("unchecked")
//    public static <A, B> SRA<A, B> mkTotal(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
//            throws TimeoutException {
//
//        if (aut.isTotal) {
//            return (SRA<A, B>) aut.clone();
//        }
//
//        long startTime = System.currentTimeMillis();
//
//        SRA<A, B> SRA = aut;
//        if (!aut.isDeterministic(ba))
//            SRA = determinize(aut, ba, timeout);
//
//        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
//        Integer initialState = SRA.initialState;
//        Collection<Integer> finalStates = new HashSet<Integer>(SRA.finalStates);
//
//        int sinkState = SRA.maxStateId + 1;
//        boolean addSink = false;
//        for (Integer state : SRA.states) {
//            if (System.currentTimeMillis() - startTime > timeout)
//                throw new TimeoutException();
//
//            A totGuard = null;
//            for (SRAMove<A, B> move : SRA.getMovesFrom(state)) {
//                transitions.add(move);
//                if (totGuard == null)
//                    totGuard = ba.MkNot(move.guard);
//                else
//                    totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
//            }
//            // If there are not transitions out of the state set the guard to
//            // the sink to true
//            if (totGuard == null)
//                totGuard = ba.True();
//            if (ba.IsSatisfiable(totGuard)) {
//                addSink = true;
//                transitions.add(new SRAMove<A, B>(state, sinkState, totGuard));
//            }
//        }
//        if (addSink)
//            transitions.add(new SRAMove<A, B>(sinkState, sinkState, ba.True()));
//
//        // Do not remove unreachable states otherwise the sink will be removed
//        // again
//        return MkSRA(transitions, initialState, finalStates, ba, false);
//    }
//
//    /**
//     * Checks whether the automaton accepts the same language as aut
//     *
//     * @throws TimeoutException
//     */
//    public boolean isEquivalentTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return areEquivalent(this, aut, ba);
//    }
//
//    /**
//     * Checks whether aut1 and aut2 accept the same language
//     *
//     * @throws TimeoutException
//     */
//    public static <A, B> Boolean areEquivalent(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
//            throws TimeoutException {
//        return areEquivalentPlusWitness(aut1, aut2, ba, Long.MAX_VALUE).first;
//    }
//
//    /**
//     * Checks whether the automaton accepts the same language as aut
//     *
//     * @throws TimeoutException
//     */
//    public Pair<Boolean, List<S>> isEquivalentPlusWitnessTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return areEquivalentPlusWitness(this, aut, ba, Long.MAX_VALUE);
//    }
//
//    /**
//     * Checks whether aut1 is equivalent to aut2 and returns a concrete witness if not. Second element is null if equivalent.
//     *
//     * @throws TimeoutException
//     */
//    public static <A, B> Pair<Boolean, List<B>> areEquivalentPlusWitness(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
//            throws TimeoutException {
//        return areEquivalentPlusWitness(aut1, aut2, ba, Long.MAX_VALUE);
//    }
//
//    /**
//     * Checks whether aut1 is equivalent to aut2 and returns a concrete witness if not. Second element is null if equivalent.
//     *
//     * @throws TimeoutException
//     */
//
//    public static <A, B> Pair<Boolean, List<B>> areEquivalentPlusWitness(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba, long timeout)
//            throws TimeoutException {
//
//        SRA<A, B> tmp1 = collapseMultipleTransitions(aut1, ba, timeout);
//        SRA<A, B> tmp2 = collapseMultipleTransitions(aut2, ba, timeout);
//
//        Pair<Boolean, List<A>> result = areEquivalentSymbolicWitness(tmp1, tmp2, ba, timeout);
//        if(result.first)
//            return new Pair<Boolean, List<B>>(true, null);
//
//        List<B> concreteWitness = new LinkedList<>();
//        for(A pred: result.second)
//            concreteWitness.add(ba.generateWitness(pred));
//
//        return new Pair<Boolean, List<B>>(false, concreteWitness);
//    }
//
//    /**
//     * checks whether aut1 is equivalent to aut2, if not returns a symbolic
//     * sequence of predicates as a witness
//     * @assume Inputs to be deterministic SRA
//     * @throws TimeoutException
//     */
//    public Pair<Boolean, List<P>> isEquivalentPlusSymoblicWitnessTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout)
//            throws TimeoutException {
//
//        if(!this.isDeterministic || !aut.isDeterministic)
//            throw new IllegalArgumentException("The SRAs have to be deterministic");
//
//        SRA<P, S> tmp1 = collapseMultipleTransitions(this, ba, timeout);
//        SRA<P, S> tmp2 = collapseMultipleTransitions(aut, ba, timeout);
//
//        return areEquivalentSymbolicWitness(tmp1, tmp2, ba, timeout);
//    }
//
//    private static <A, B> Pair<Boolean, List<A>> areEquivalentSymbolicWitness(SRA<A, B> aut1, SRA<A, B> aut2,
//            BooleanAlgebra<A, B> ba, long timeout) throws TimeoutException {
//
//        long startTime = System.currentTimeMillis();
//
//        boolean isF1=aut1.isFinalState(aut1.initialState);
//        boolean isF2=aut2.isFinalState(aut2.initialState);
//        if(isF1!=isF2)
//            return new Pair<Boolean, List<A>>(false, new LinkedList<>());
//
//        Pair<Integer, Integer> initPair = new Pair<Integer, Integer>(aut1.initialState, aut2.initialState);
//        LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<>();
//        toVisit.add(initPair);
//
//        HashSet<Pair<Integer, Integer>> reached = new HashSet<>();
//        reached.add(initPair);
//
//        HashMap<Pair<Integer, Integer>, List<A>> witnesses = new HashMap<>();
//        witnesses.put(initPair, new LinkedList<>());
//
//        while (!toVisit.isEmpty()) {
//
//            if (System.currentTimeMillis() - startTime > timeout)
//                throw new TimeoutException();
//
//            Pair<Integer, Integer> curr = toVisit.removeFirst();
//            List<A> currWitness = witnesses.get(curr);
//
//            for (SRAMove<A, B> move1 : aut1.getMovesFrom(curr.first))
//                for (SRAMove<A, B> move2 : aut2.getMovesFrom(curr.second)) {
//                    A conj = ba.MkAnd(move1.guard, move2.guard);
//                    if (ba.IsSatisfiable(conj)) {
//
//                        Pair<Integer, Integer> newState = new Pair<Integer, Integer>(move1.to, move2.to);
//                        if(!reached.contains(newState)){
//                            toVisit.add(newState);
//                            reached.add(newState);
//                            List<A> newWitness = new LinkedList<A>(currWitness);
//                            newWitness.add(conj);
//                            witnesses.put(newState, newWitness);
//
//                            if(aut1.isFinalState(move1.to)!= aut2.isFinalState(move2.to))
//                                return new Pair<Boolean, List<A>>(false, newWitness);
//                        }
//                    }
//                }
//        }
//
//        return new Pair<Boolean, List<A>>(true, null);
//    }
//
//    /**
//     * concatenation with aut
//     *
//     * @throws TimeoutException
//     */
//    public SRA<P, S> concatenateWith(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return concatenate(this, aut, ba);
//    }
//
//    /**
//     * concatenates aut1 with aut2
//     *
//     * @throws TimeoutException
//     */
//    @SuppressWarnings("unchecked")
//    public static <A, B> SRA<A, B> concatenate(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
//            throws TimeoutException {
//
//        if (aut1.isEmpty || aut2.isEmpty)
//            return getEmptySRA(ba);
//
//        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
//        Integer initialState = aut1.initialState;
//        Collection<Integer> finalStates = new HashSet<Integer>();
//
//        int offSet = aut1.maxStateId + 1;
//
//        for (SRAMove<A, B> t : aut1.getTransitions())
//            transitions.add((SRAMove<A, B>) t.clone());
//
//        for (SRAMove<A, B> t : aut2.getTransitions()) {
//            SRAMove<A, B> newMove = (SRAMove<A, B>) t.clone();
//            newMove.from += offSet;
//            newMove.to += offSet;
//            transitions.add(newMove);
//        }
//
//        for (Integer state1 : aut1.finalStates)
//            transitions.add(new SRAEpsilon<A, B>(state1, aut2.initialState + offSet));
//
//        for (Integer state : aut2.finalStates)
//            finalStates.add(state + offSet);
//
//        return MkSRA(transitions, initialState, finalStates, ba, false);
//    }
//
//    /**
//     * language star
//     *
//     * @throws TimeoutException
//     */
//    @SuppressWarnings("unchecked")
//    public static <A, B> SRA<A, B> star(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {
//
//        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
//        Integer initialState = 0;
//        Collection<Integer> finalStates = new HashSet<Integer>();
//
//        initialState = aut.maxStateId + 1;
//
//        for (SRAMove<A, B> t : aut.getTransitions())
//            transitions.add((SRAMove<A, B>) t.clone());
//
//        // add eps transition from finalStates to initial state
//        for (Integer finState : aut.finalStates)
//            transitions.add(new SRAEpsilon<A, B>(finState, initialState));
//
//        // add eps transition from new initial state to old initial state
//        transitions.add(new SRAEpsilon<A, B>(initialState, aut.initialState));
//
//        // The only final state is the new initial state
//        finalStates.add(initialState);
//
//        return MkSRA(transitions, initialState, finalStates, ba, false);
//    }
//
//    /**
//     * @return an equivalent deterministic SRA
//     * @throws TimeoutException
//     */
//    public SRA<P, S> determinize(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return determinize(this, ba, Long.MAX_VALUE);
//    }
//
//    /**
//     * @return an equivalent deterministic SRA
//     * @throws TimeoutException
//     */
//    public SRA<P, S> determinize(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
//        return determinize(this, ba, timeout);
//    }
//
//    /**
//     * @return a deterministic SRA that is equivalent to <code>aut</code>
//     * @throws TimeoutException
//     */
//    public static <A, B> SRA<A, B> determinize(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
//            throws TimeoutException {
//
//        long startTime = System.currentTimeMillis();
//
//        if (aut.isDeterministic(ba))
//            return aut;
//
//        // Remove epsilon moves before starting
//        SRA<A, B> autChecked = aut;
//        if (!aut.isEpsilonFree)
//            autChecked = aut.removeEpsilonMoves(ba);
//
//        // components of new SRA
//        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
//        Integer initialState = 0;
//        Collection<Integer> finalStates = new HashSet<Integer>();
//
//        // reached contains the subset states we discovered and maps them to a
//        // stateId
//        HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
//        // toVisit contains the subset states we still have not explored
//        LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();
//
//        // the initial state is the set {aut.initialState}
//        Collection<Integer> detInitialState = new HashSet<Integer>();
//        detInitialState.add(initialState);
//        initialState = 0;
//
//        reachedStates.put(detInitialState, 0);
//        toVisitStates.add(detInitialState);
//
//        long availableMemory = Runtime.getRuntime().totalMemory();
//
//        // Explore the automaton until no new subset states can be reached
//        while (!toVisitStates.isEmpty()) {
//
//            long freeMemory = Runtime.getRuntime().freeMemory();
//            if (freeMemory < 0.1 * availableMemory)
//                throw new TimeoutException("Out of memory");
//
//            if (System.currentTimeMillis() - startTime > timeout)
//                throw new TimeoutException();
//
//            Collection<Integer> currentState = toVisitStates.removeFirst();
//            int currentStateId = reachedStates.get(currentState);
//
//            // check if final
//            if (autChecked.isFinalConfiguration(currentState))
//                finalStates.add(currentStateId);
//
//            // get all the moves out of the states in the current subset
//            ArrayList<SRAMove<A, B>> movesFromCurrState = new ArrayList<SRAMove<A, B>>(
//                    autChecked.getMovesFrom(currentState));
//
//            // put in a separate list all the predicates of the moves and in the
//            // same order. We will use them to build the minterms
//            ArrayList<A> predicatesOfMoves = new ArrayList<A>();
//            for (SRAMove<A, B> inter : movesFromCurrState)
//                predicatesOfMoves.add(inter.guard);
//
//            // build the minterms using the predicates and iterate over them:
//            // each minterm is a predicate together with the the corresponding
//            // set of transition IDs
//            for (Pair<A, ArrayList<Integer>> minterm : ba.GetMinterms(predicatesOfMoves,
//                    timeout - (System.currentTimeMillis() - startTime))) {
//
//                if (System.currentTimeMillis() - startTime > timeout)
//                    throw new TimeoutException();
//
//                A guard = minterm.first;
//
//                // The new state contains all the target states of the moves
//                // with bit 1
//                ArrayList<Integer> moveBits = minterm.second;
//                Collection<Integer> toState = new HashSet<Integer>();
//                for (int moveIndex = 0; moveIndex < moveBits.size(); moveIndex++)
//                    if (moveBits.get(moveIndex) == 1)
//                        // add the target state of the moveIndex-th move in the
//                        // list
//                        toState.add(movesFromCurrState.get(moveIndex).to);
//
//                // Add new move if target state is not the empty set
//                if (toState.size() > 0) {
//                    int toStateId = getStateId(toState, reachedStates, toVisitStates);
//                    transitions.add(new SRAMove<A, B>(currentStateId, toStateId, guard));
//                }
//            }
//        }
//
//        SRA<A, B> determinized = MkSRA(transitions, initialState, finalStates, ba, false);
//        // set isDetermistic to true to avoid future redundancy
//        determinized.isDeterministic = true;
//        return determinized;
//    }
//
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
        Map<Pair<Integer, Integer>, Pair<A, Integer>> checkMoves = new HashMap<Pair<Integer, Integer>, Pair<A, Integer>>();
        Map<Pair<Integer, Integer>, Pair<A, Integer>> freshMoves = new HashMap<Pair<Integer, Integer>, Pair<A, Integer>>();
        Map<Pair<Integer, Integer>, Pair<A, Pair<Collection<Integer>, Collection<Integer>>>> MAMoves = new HashMap<Pair<Integer, Integer>, Pair<A, Pair<Collection<Integer>, Collection<Integer>>>>();

        // Create disjunction of all rules between same state and with the same operation
        for (SRAMove<A, B> move : aut.getMovesFrom(aut.states)) {
            Pair<Integer, Integer> fromTo = new Pair<Integer, Integer>(move.from, move.to);
            if (move.isMultipleAssignment()) {
                if (MAMoves.containsKey(fromTo))
                    MAMoves.put(fromTo, new Pair<A, Pair<Collection<Integer>, Collection<Integer>>>(ba.MkOr(move.guard, MAMoves.get(fromTo).first),
                            new Pair<Collection<Integer>, Collection<Integer>>(move.asMultipleAssignment().E, move.asMultipleAssignment().U)));
                else
                    MAMoves.put(fromTo, new Pair<A, Pair<Collection<Integer>, Collection<Integer>>>(move.guard,
                            new Pair<Collection<Integer>, Collection<Integer>>(move.asMultipleAssignment().E, move.asMultipleAssignment().U)));
            } else if (move.isFresh()) {
                if (freshMoves.containsKey(fromTo))
                    freshMoves.put(fromTo, new Pair<A, Integer>(ba.MkOr(move.guard, freshMoves.get(fromTo).first), move.registerIndexes.iterator().next()));
                else
                    freshMoves.put(fromTo, new Pair<A, Integer>(move.guard, move.registerIndexes.iterator().next()));
            } else {
                if (checkMoves.containsKey(fromTo))
                    checkMoves.put(fromTo, new Pair<A, Integer>(ba.MkOr(move.guard, checkMoves.get(fromTo).first), move.registerIndexes.iterator().next()));
                else
                    checkMoves.put(fromTo, new Pair<A, Integer>(move.guard, move.registerIndexes.iterator().next()));
            }
        }

        // Create the new transition function
        for (Pair<Integer, Integer> p : checkMoves.keySet())
            transitions.add(new SRACheckMove<A, B>(p.first, p.second, checkMoves.get(p).first, checkMoves.get(p).second));
        for (Pair<Integer, Integer> p : freshMoves.keySet())
            transitions.add(new SRAFreshMove<A, B>(p.first, p.second, freshMoves.get(p).first, freshMoves.get(p).second));
        for (Pair<Integer, Integer> p : MAMoves.keySet())
            transitions.add(new MSRAMove<A, B>(p.first, p.second, MAMoves.get(p).first, MAMoves.get(p).second.first, MAMoves.get(p).second.second));

        return MkSRA(transitions, initialState, finalStates, registers, ba, false, false);
    }

//    /**
//     * @return a minimized copy of the SRA
//     * @throws TimeoutException
//     */
//    public SRA<P, S> minimize(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return getMinimalOf(this, ba);
//    }
//
//    /**
//     * @return a minimized copy of <code>aut<code>
//     * @throws TimeoutException
//     */
//    public static <A, B> SRA<A, B> getMinimalOf(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {
//
//        if (aut.isEmpty)
//            return getEmptySRA(ba);
//
//        // This algorithm is presented the POPL14 paper by D'Antoni and Veanes
//        // Minimization of symbolic automata
//        SRA<A, B> totalAut = aut.mkTotal(ba);
//
//        Collection<Integer> finStates = totalAut.getFinalStates();
//        Collection<Integer> nonFinStates = totalAut.getNonFinalStates();
//
//        // Initially split states into final and non-final
//        Block fB = new Block(finStates);
//        Block nfB = new Block(nonFinStates);
//
//        // stateToBlock remembers for each state the containing block
//        Map<Integer, Block> stateToBlock = new HashMap<Integer, Block>();
//        for (int q : finStates)
//            stateToBlock.put(q, fB);
//        for (int q : nonFinStates)
//            stateToBlock.put(q, nfB);
//
//        // blocks that might still be split
//        Stack<Block> toExploreBlocks = new Stack<Block>();
//
//        // Initialize search stack with the smallest block
//        if (nfB.size() < fB.size())
//            toExploreBlocks.push(nfB);
//        else
//            toExploreBlocks.push(fB);
//
//        // Continue until all blocks have been split
//        while (!toExploreBlocks.isEmpty()) {
//            Block currentBlock = toExploreBlocks.pop();
//
//            // stateToPredIntoCurrentBlock(s) contains the predicate for which
//            // a move of s goes into a state in currentBlock
//            Map<Integer, A> stateToPredIntoCurrentBlock = new HashMap<Integer, A>();
//            for (SRAMove<A, B> move : totalAut.getMovesTo(currentBlock.set)) {
//                if (stateToPredIntoCurrentBlock.containsKey(move.from))
//                    stateToPredIntoCurrentBlock.put(move.from,
//                            ba.MkOr(stateToPredIntoCurrentBlock.get(move.from), move.guard));
//                else
//                    stateToPredIntoCurrentBlock.put(move.from, move.guard);
//            }
//
//            // Set of states going into currentBlock with some transition
//            Block preOfCurrentBlock = new Block(stateToPredIntoCurrentBlock.keySet());
//
//            // Blocks intersecting with preOfCurrentBlock
//            HashSet<Block> relevantBlocks = new HashSet<Block>();
//            for (int state : stateToBlock.keySet())
//                if (preOfCurrentBlock.set.contains(state))
//                    relevantBlocks.add(stateToBlock.get(state));
//
//            // split relevant blocks
//            for (Block relevantBlock : relevantBlocks) {
//                Block splitBlock = relevantBlock.intersectWith(preOfCurrentBlock);
//                // Change only if the intersection made the block smaller
//                if (splitBlock.size() < relevantBlock.size()) {
//                    for (int p : splitBlock.set) {
//                        relevantBlock.remove(p);
//                        stateToBlock.put(p, splitBlock);
//                    }
//                    if (toExploreBlocks.contains(relevantBlock))
//                        toExploreBlocks.push(splitBlock);
//                    else if (relevantBlock.size() <= splitBlock.size())
//                        toExploreBlocks.push(relevantBlock);
//                    else
//                        toExploreBlocks.push(splitBlock);
//                }
//            }
//
//            boolean iterate = true;
//            while (iterate) {
//                iterate = false;
//
//                // Blocks intersecting with preOfCurrentBlock
//                relevantBlocks = new HashSet<Block>();
//                for (int state : stateToBlock.keySet())
//                    if (preOfCurrentBlock.set.contains(state))
//                        relevantBlocks.add(stateToBlock.get(state));
//
//                // split relevant blocks
//                for (Block relevantBlock : relevantBlocks) {
//                    Block splitBlock = new Block();
//
//                    int current = relevantBlock.getFirst();
//                    A psi = stateToPredIntoCurrentBlock.get(current);
//
//                    boolean splitterFound = false;
//                    splitBlock.add(current);
//
//                    while (relevantBlock.hasNext()) {
//                        int q = relevantBlock.getNext();
//                        A phi = stateToPredIntoCurrentBlock.get(q);
//                        if (splitterFound) {
//                            A conj = ba.MkAnd(psi, phi);
//                            if (ba.IsSatisfiable(conj)) {
//                                splitBlock.add(q);
//                                psi = conj;
//                            }
//                        } else {
//                            A conj = ba.MkAnd(psi, ba.MkNot(phi));
//                            if (ba.IsSatisfiable(conj)) {
//                                psi = conj; // refine the local minterm
//                                splitterFound = true;
//                            } else { // psi implies phi
//                                conj = ba.MkAnd(phi, ba.MkNot(psi));
//                                if (ba.IsSatisfiable(conj)) {
//                                    splitBlock.clear();
//                                    splitBlock.add(q);
//                                    psi = conj;
//                                    splitterFound = true;
//                                } else {
//                                    splitBlock.add(q);
//                                }
//                            }
//                        }
//                    }
//                    // Change only if the intersection made the block smaller
//                    if (splitBlock.size() < relevantBlock.size()) {
//                        // (a,R)-split of P for some a
//                        iterate = (iterate || (relevantBlock.size() > 2));
//                        for (int p : splitBlock.set) {
//                            relevantBlock.remove(p);
//                            stateToBlock.put(p, splitBlock);
//                        }
//                        if (toExploreBlocks.contains(relevantBlock))
//                            toExploreBlocks.push(splitBlock);
//                        else if (relevantBlock.size() <= splitBlock.size())
//                            toExploreBlocks.push(relevantBlock);
//                        else
//                            toExploreBlocks.push(splitBlock);
//                    }
//                }
//            }
//        }
//
//        // minimal automaton components
//        Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
//        Integer initialState = 0;
//        Map<Block, Integer> blockToIndex = new HashMap<Block, Integer>();
//        Map<Integer, Integer> stateToClass = new HashMap<Integer, Integer>();
//        Collection<Integer> finalStates = new HashSet<Integer>();
//
//        // One state per block
//        for (int state : totalAut.states) {
//            Block b = stateToBlock.get(state);
//            if (!blockToIndex.containsKey(b))
//                blockToIndex.put(b, blockToIndex.size());
//            int eqClass = blockToIndex.get(b);
//            stateToClass.put(state, eqClass);
//            if (totalAut.isFinalState(state))
//                finalStates.add(eqClass);
//        }
//
//        initialState = stateToClass.get(totalAut.initialState);
//
//        // Create minimal automaton from equivalence classes
//        for (Block b : blockToIndex.keySet()) {
//            int st = blockToIndex.get(b);
//            for (SRAMove<A, B> t : totalAut.getMovesFrom(b.set))
//                transitions.add(new SRAMove<A, B>(st, stateToClass.get(t.to), t.guard));
//
//        }
//
//        return MkSRA(transitions, initialState, finalStates, ba, false, true);
//    }

	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

//    /**
//     * Checks whether the SRA is ambiguous
//     *
//     * @return an ambiguous input if the automaton is ambiguous,
//     *         <code>null</code> otherwise
//     * @throws TimeoutException
//     */
//    public List<S> getAmbiguousInput(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        return getAmbiguousInput(this, ba);
//    }
//
//    /**
//     * Checks whether <code>aut</code> is ambiguous
//     *
//     * @return an ambiguous input if the automaton is ambiguous,
//     *         <code>null</code> otherwise
//     * @throws TimeoutException
//     */
//    @SuppressWarnings("unchecked")
//    public static <A, B> List<B> getAmbiguousInput(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {
//
//        SRA<A, B> aut1 = aut;
//        SRA<A, B> aut2 = aut;
//
//        SRA<A, B> product = new SRA<A, B>();
//
//        // maps a product state to its id
//        HashMap<Pair<Pair<Integer, Integer>, Boolean>, Integer> reached = new HashMap<Pair<Pair<Integer, Integer>, Boolean>, Integer>();
//        // maps and id to its product state
//        HashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>> reachedRev = new HashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>>();
//        // list on unexplored product states
//        LinkedList<Pair<Pair<Integer, Integer>, Boolean>> toVisit = new LinkedList<Pair<Pair<Integer, Integer>, Boolean>>();
//
//        // The initial state is the pair consisting of the initial
//        // states of aut1 and aut2, true states whether the state was reached by
//        // input moves (false for epsilon)
//        Pair<Pair<Integer, Integer>, Boolean> initStatePair = new Pair<Pair<Integer, Integer>, Boolean>(
//                new Pair<Integer, Integer>(aut1.initialState, aut2.initialState), true);
//        product.initialState = 0;
//        product.states.add(0);
//
//        reached.put(initStatePair, 0);
//        reachedRev.put(0, initStatePair);
//        toVisit.add(initStatePair);
//
//        int totStates = 1;
//
//        while (!toVisit.isEmpty()) {
//            Pair<Pair<Integer, Integer>, Boolean> currState = toVisit.removeFirst();
//            int st1 = currState.first.first;
//            int st2 = currState.first.second;
//            boolean isInputReached = currState.second;
//            int currStateId = reached.get(currState);
//
//            // Set final states
//            // if both the epsilon closures contain a final state
//            // currentStateID
//            // is final
//            if (aut1.isFinalState(st1) && aut2.isFinalState(st2))
//                product.finalStates.add(currStateId);
//
//            // Try to pair transitions out of both automata
//            for (SRAMove<A, B> t1 : aut1.getMovesFrom(st1))
//                for (SRAMove<A, B> t2 : aut2.getMovesFrom(st2)) {
//
//                    if (t1.to >= t2.to) {
//                        // create conjunction of the two guards and
//                        // create
//                        // transition only if the conjunction is
//                        // satisfiable
//                        A intersGuard = ba.MkAnd(t1.guard, t2.guard);
//                        if (ba.IsSatisfiable(intersGuard)) {
//
//                            // Create new product transition and add it
//                            // to
//                            // transitions
//                            Pair<Pair<Integer, Integer>, Boolean> nextState = new Pair<Pair<Integer, Integer>, Boolean>(
//                                    new Pair<Integer, Integer>(t1.to, t2.to), true);
//                            int nextStateId = 0;
//
//                            if (!reached.containsKey(nextState)) {
//                                product.movesTo.put(totStates, new HashSet<SRAMove<A, B>>());
//
//                                reached.put(nextState, totStates);
//                                reachedRev.put(totStates, nextState);
//
//                                toVisit.add(nextState);
//                                product.states.add(totStates);
//                                nextStateId = totStates;
//                                totStates++;
//                            } else
//                                nextStateId = reached.get(nextState);
//
//                            product.addTransition(new SRAMove<A, B>(currStateId, nextStateId, intersGuard), ba, true);
//                        }
//                    }
//                }
//
//            if (isInputReached) {
//                // get the set of states reachable from currentState via epsilon
//                // moves
//                Collection<Integer> epsilonClosure1 = aut1.getEpsClosure(st1, ba);
//                Collection<Integer> epsilonClosure2 = aut2.getEpsClosure(st2, ba);
//
//                // Add epsilon moves to the closure
//                for (Integer state1 : epsilonClosure1)
//                    for (Integer state2 : epsilonClosure2) {
//                        // Avoid self epsilon loop
//                        if ((state1 != st1 || state2 != st2) && state1>=state2) {
//
//                            Pair<Pair<Integer, Integer>, Boolean> nextState = new Pair<Pair<Integer, Integer>, Boolean>(
//                                    new Pair<Integer, Integer>(state1, state2), false);
//                            int nextStateId = 0;
//
//                            if (!reached.containsKey(nextState)) {
//                                product.movesTo.put(totStates, new HashSet<SRAMove<A, B>>());
//
//                                reached.put(nextState, totStates);
//                                reachedRev.put(totStates, nextState);
//
//                                toVisit.add(nextState);
//                                product.states.add(totStates);
//                                nextStateId = totStates;
//                                totStates++;
//                            } else
//                                nextStateId = reached.get(nextState);
//
//                            product.addTransition(new SRAEpsilon<A, B>(currStateId, nextStateId), ba, true);
//                        }
//                    }
//            }
//        }
//
//        product = removeDeadOrUnreachableStates(product, ba);
//
//        // Check if a state that of the form (s1,s2) such that s1!=s2 is still
//        // alive, if so any string passing to it is ambiguous
//        for (Integer aliveSt : product.states) {
//            Pair<Pair<Integer, Integer>, Boolean> stP = reachedRev.get(aliveSt);
//            if (stP.first.first != stP.first.second) {
//                SRA<A, B> left = (SRA<A, B>) product.clone();
//                SRA<A, B> right = (SRA<A, B>) product.clone();
//                left.finalStates = new HashSet<Integer>();
//                left.finalStates.add(aliveSt);
//                right.initialState = aliveSt;
//
//                SRA<A, B> c = left.concatenateWith(right, ba);
//                SRA<A, B> clean = removeDeadOrUnreachableStates(c, ba);
//                return clean.getWitness(ba);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Checks whether the automaton is deterministic
//     *
//     * @return true iff the automaton is deterministic
//     * @throws TimeoutException
//     */
//    public boolean isDeterministic(BooleanAlgebra<P, S> ba) throws TimeoutException {
//        // Check if we set it before
//        if (isDeterministic)
//            return true;
//
//        // Check transitions out of a state are mutually exclusive
//        for (Integer state : states) {
//            List<SRAMove<P, S>> movesFromState = new ArrayList<SRAMove<P, S>>(getTransitionsFrom(state));
//
//            for (int i = 0; i < movesFromState.size(); i++) {
//                SRAMove<P, S> t1 = movesFromState.get(i);
//                for (int p = i + 1; p < movesFromState.size(); p++) {
//                    SRAMove<P, S> t2 = movesFromState.get(p);
//                    if (!t1.isDisjointFrom(t2, ba)) {
//                        isDeterministic = false;
//                        return false;
//                    }
//                }
//            }
//        }
//
//        isDeterministic = true;
//        return true;
//    }

	// ------------------------------------------------------
	// Reachability methods
	// ------------------------------------------------------

	// creates a new SRA where all unreachable or dead states have been removed
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

	// Computes states that reachable from states
	private Collection<Integer> getReachableStatesFrom(Collection<Integer> states) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (Integer state : states)
			visitForward(state, result);
		return result;
	}

	// Computes states that can reach states
	private Collection<Integer> getReachingStates(Collection<Integer> states) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (Integer state : states)
			visitBackward(state, result);
		return result;
	}

	// DFS accumulates in reached
	private void visitForward(Integer state, HashSet<Integer> reached) {
		if (!reached.contains(state)) {
			reached.add(state);
			for (SRAMove<P, S> t : this.getTransitionsFrom(state)) {
				Integer nextState = t.to;
				visitForward(nextState, reached);
			}
		}
	}

	// backward DFS accumulates in reached
	private void visitBackward(Integer state, HashSet<Integer> reached) {
		if (!reached.contains(state)) {
			reached.add(state);
			for (SRAMove<P, S> t : this.getTransitionsTo(state)) {
				Integer predState = t.from;
				visitBackward(predState, reached);
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
        moves.addAll(getMAMovesFrom(state));
		return moves;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SRAMove<P, S>> getTransitionsTo(Integer state) {
		Collection<SRAMove<P, S>> moves = new HashSet<SRAMove<P, S>>();
		moves.addAll(getCheckMovesTo(state));
        moves.addAll(getFreshMovesTo(state));
        moves.addAll(getMAMovesTo(state));
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
		Collection<SRACheckMove<P, S>> transitions = checkMovesTo.computeIfAbsent(state, k -> new HashSet<SRACheckMove<P, S>>());
		return transitions;
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
        Collection<SRACheckMove<P, S>> transitions = checkMovesFrom.computeIfAbsent(state, k -> new HashSet<SRACheckMove<P, S>>());
        return transitions;
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
        Collection<SRAFreshMove<P, S>> transitions = freshMovesTo.computeIfAbsent(state, k -> new HashSet<SRAFreshMove<P, S>>());
        return transitions;
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
		Collection<SRAFreshMove<P, S>> transitions = freshMovesFrom.computeIfAbsent(state, k -> new HashSet<SRAFreshMove<P, S>>());
		return transitions;
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
     * Returns the set of multiple assignment transitions to state <code>state</code>
     */
    public Collection<MSRAMove<P, S>> getMAMovesTo(Integer state) {
        Collection<MSRAMove<P, S>> transitions = MAMovesTo.computeIfAbsent(state, k -> new HashSet<MSRAMove<P, S>>());
        return transitions;
    }

    /**
     * Returns the set of multiple assignment transitions to states <code>stateSet</code>
     */
    public Collection<MSRAMove<P, S>> getMAMovesTo(Collection<Integer> stateSet) {
        Collection<MSRAMove<P, S>> transitions = new LinkedList<MSRAMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getMAMovesTo(state));
        return transitions;
    }

    /**
     * Returns the set of multiple assignment transitions from state <code>state</code>
     */
    public Collection<MSRAMove<P, S>> getMAMovesFrom(Integer state) {
        Collection<MSRAMove<P, S>> transitions = MAMovesFrom.computeIfAbsent(state, k -> new HashSet<MSRAMove<P, S>>());
        return transitions;
    }

    /**
     * Returns the set of multiple assignment transitions from states <code>stateSet</code>
     */
    public Collection<MSRAMove<P, S>> getMAMovesFrom(Collection<Integer> stateSet) {
        Collection<MSRAMove<P, S>> transitions = new LinkedList<MSRAMove<P, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getMAMovesFrom(state));
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

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.checkMovesFrom = new HashMap<Integer, Collection<SRACheckMove<P, S>>>(checkMovesFrom);
		cl.checkMovesTo = new HashMap<Integer, Collection<SRACheckMove<P, S>>>(checkMovesTo);

        cl.freshMovesFrom = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>(freshMovesFrom);
        cl.freshMovesTo = new HashMap<Integer, Collection<SRAFreshMove<P, S>>>(freshMovesTo);

        cl.MAMovesFrom = new HashMap<Integer, Collection<MSRAMove<P, S>>>(MAMovesFrom);
        cl.MAMovesTo = new HashMap<Integer, Collection<MSRAMove<P, S>>>(MAMovesTo);

		return cl;
	}

}

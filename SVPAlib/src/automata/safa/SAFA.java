/**
 * SVPAlib
 * automata.sfa
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata.safa;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import theory.BooleanAlgebra;

/**
 * Symbolic finite automaton
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SAFA<P, S> {

	// ------------------------------------------------------
	// Constant automata
	// ------------------------------------------------------

	/**
	 * Returns the empty SFA for the Boolean algebra <code>ba</code>
	 */
	public static <A, B> SAFA<A, B> getEmptySFA(BooleanAlgebra<A, B> ba) {
		SAFA<A, B> aut = new SAFA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>();
		aut.initialState = 0;
		aut.maxStateId = 1;
		return aut;
	}
	
	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

	private Integer initialState;
	private Collection<Integer> states;
	private Collection<Integer> finalStates;

	protected Map<Integer, Collection<SAFAInputMove<P, S>>> inputMovesFrom;

	private Integer maxStateId;
	private Integer transitionCount;

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

	// ------------------------------------------------------
	// Constructors
	// ------------------------------------------------------

	// Initializes all the fields of the automaton
	private SAFA() {
		super();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
		inputMovesFrom = new HashMap<Integer, Collection<SAFAInputMove<P, S>>>();
		transitionCount = 0;
		maxStateId = 0;
	}

	/**
	 * Create an automaton and removes unreachable states
	 */
	public static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions,
			Integer initialState, Collection<Integer> finalStates,
			BooleanAlgebra<A, B> ba) {

		return MkSAFA(transitions, initialState, finalStates, ba, true);
	}

	/**
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if <code>remUnreachableStates<code> is true
	 */
	public static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions,
			Integer initialState, Collection<Integer> finalStates,
			BooleanAlgebra<A, B> ba, boolean remUnreachableStates) {

		return MkSAFA(transitions, initialState, finalStates, ba,
				remUnreachableStates, true);
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	private static <A, B> SAFA<A, B> MkSAFA(
			Collection<SAFAInputMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba,
			boolean remUnreachableStates, boolean normalize) {

		SAFA<A, B> aut = new SAFA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = finalStates;
		if(finalStates.isEmpty())
			return getEmptySFA(ba);

		for (SAFAInputMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

//		if (remUnreachableStates)
//			aut = removeDeadOrUnreachableStates(aut, ba);

		if(aut.finalStates.isEmpty())			
			return getEmptySFA(ba);
		
		return aut;
	}

	// Adds a transition to the SFA
	private void addTransition(SAFAInputMove<P, S> transition,
			BooleanAlgebra<P, S> ba, boolean skipSatCheck) {
		
		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.maxState > maxStateId)
				maxStateId = transition.maxState;

			states.add(transition.from);
			states.addAll(transition.toStates);

			getInputMovesFrom(transition.from).add(
						(SAFAInputMove<P, S>) transition);			
		}
	}
	
	// ------------------------------------------------------
	// Boolean automata operations
	// ------------------------------------------------------


	// ------------------------------------------------------
	// Properties accessing methods
	// ------------------------------------------------------

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SAFAInputMove<P, S>> getInputMovesFrom(Integer state) {
		Collection<SAFAInputMove<P, S>> trset = inputMovesFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SAFAInputMove<P, S>>();
			inputMovesFrom.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SAFAInputMove<P, S>> getInputMovesFrom(
			Collection<Integer> stateSet) {
		Collection<SAFAInputMove<P, S>> transitions = new LinkedList<SAFAInputMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	@Override
	public Object clone() {
		SAFA<P, S> cl = new SAFA<P, S>();

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.inputMovesFrom = new HashMap<Integer, Collection<SAFAInputMove<P, S>>>(
				inputMovesFrom);
		
		return cl;
	}

}

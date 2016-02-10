/**
 * SVPAlib
 * automata.sfa
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata.safa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import theory.BooleanAlgebra;
import utilities.Pair;

/**
 * Symbolic finite automaton
 * 
 * @param
 * 			<P>
 *            set of predicates over the domain S
 * @param <S>
 *            domain of the automaton alphabet
 */
public class SAFA<P, S> {

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
		finalStates = new HashSet<>();
		states = new HashSet<Integer>();
		inputMovesFrom = new HashMap<Integer, Collection<SAFAInputMove<P, S>>>();
		transitionCount = 0;
		maxStateId = 0;
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	private static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba) {

		SAFA<A, B> aut = new SAFA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = new HashSet<>(finalStates);

		for (SAFAInputMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

		return aut;
	}

	// Adds a transition to the SFA
	private void addTransition(SAFAInputMove<P, S> transition, BooleanAlgebra<P, S> ba, boolean skipSatCheck) {

		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.maxState > maxStateId)
				maxStateId = transition.maxState;

			states.add(transition.from);
			states.addAll(transition.toStates);

			getInputMovesFrom(transition.from).add((SAFAInputMove<P, S>) transition);
		}
	}

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
		aut.finalStates = new HashSet<>();
		aut.initialState = 0;
		aut.maxStateId = 1;
		return aut;
	}

	// ------------------------------------------------------
	// Runnable operations
	// ------------------------------------------------------

	/**
	 * Returns true if the SAFA accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 */
	public boolean accepts(List<S> input, BooleanAlgebra<P, S> ba) {
		List<S> revInput = Lists.reverse(input);

		Collection<Integer> currConf = finalStates;

		for (S el : revInput) {
			currConf = getPrevState(currConf, el, ba);

			if (currConf.isEmpty())
				return false;
		}

		return currConf.contains(initialState);
	}

	protected Collection<Integer> getPrevState(Collection<Integer> currState, S inputElement, BooleanAlgebra<P, S> ba) {
		Collection<Integer> prevState = new HashSet<Integer>();
		for (SAFAInputMove<P, S> t : getInputMoves()) {
			BooleanExpr b = t.to;
			if (b.hasModel(currState))
				prevState.add(t.from);
		}

		return null;
	}

	// ------------------------------------------------------
	// Boolean automata operations
	// ------------------------------------------------------

	/**
	 * Computes the intersection with <code>aut</code> as a new SFA
	 */
	public SAFA<P, S> intersectionWith(SAFA<P, S> aut, BooleanAlgebra<P, S> ba) {
		return binaryOp(this, aut, ba, BoolOp.Intersection);
	}

	public enum BoolOp {
		Intersection, Union
	}

	/**
	 * Computes the intersection with <code>aut1</code> and <code>aut2</code> as
	 * a new SFA
	 */
	public static <A, B> SAFA<A, B> binaryOp(SAFA<A, B> aut1, SAFA<A, B> aut2, BooleanAlgebra<A, B> ba, BoolOp op) {

		int offset = aut1.maxStateId + 1;

		Integer initialState = aut1.maxStateId + aut2.maxStateId + 2;

		Collection<Integer> finalStates = new ArrayList<Integer>(aut1.finalStates);
		for (int state : aut2.finalStates)
			finalStates.add(state + offset);

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<A, B>> transitions = new ArrayList<SAFAInputMove<A, B>>(aut1.getInputMoves());
		for (SAFAInputMove<A, B> t : aut2.getInputMoves())
			transitions.add(new SAFAInputMove<A, B>(t.from + offset, t.to.offset(offset), t.guard));

		switch (op) {
		case Union:
			// Add extra moves from new initial state
			for (SAFAInputMove<A, B> t : aut1.getInputMovesFrom(aut1.initialState))
				transitions.add(new SAFAInputMove<A, B>(initialState, t.to, t.guard));

			for (SAFAInputMove<A, B> t : aut2.getInputMovesFrom(aut2.initialState))
				transitions.add(new SAFAInputMove<A, B>(initialState, t.to.offset(offset), t.guard));
			break;

		case Intersection:
			// Add extra moves from new initial state
			for (SAFAInputMove<A, B> t1 : aut1.getInputMovesFrom(aut1.initialState))
				for (SAFAInputMove<A, B> t2 : aut2.getInputMovesFrom(aut2.initialState)) {
					A newGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(newGuard)) {
						// Compute intersected output state
						BooleanExpr liftedt2 = t2.to.offset(offset);
						BooleanExpr newTo = t1.to.interesectWith(liftedt2);

						transitions.add(new SAFAInputMove<A, B>(initialState, newTo, newGuard));
					}
				}
			break;

		default:
			break;
		}

		return MkSAFA(transitions, initialState, finalStates, ba);
	}

	/**
	 * Normalizes the SAFA by having at most one transition for each symbol out of each state
	 */
	public SAFA<P, S> normalize(SAFA<P, S> aut, BooleanAlgebra<P, S> ba) {

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>();

		for (int state : states) {
			ArrayList<SAFAInputMove<P, S>> trFromState = new ArrayList<>(getInputMovesFrom(state));
			ArrayList<P> predicates = new ArrayList<>();
			for (SAFAInputMove<P, S> t : trFromState)
				predicates.add(t.guard);

			Collection<Pair<P, ArrayList<Integer>>> minterms = ba.GetMinterms(predicates);
			for (Pair<P, ArrayList<Integer>> minterm : minterms) {
				BooleanExpr newTo = SumOfProducts.empty();
				for (int i : minterm.second)
					newTo = newTo.unionWith(trFromState.get(i).to);			

				transitions.add(new SAFAInputMove<P, S>(state, newTo, minterm.first));
			}
		}

		return MkSAFA(transitions, initialState, finalStates, ba);
	}

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
	public Collection<SAFAInputMove<P, S>> getInputMovesFrom(Collection<Integer> stateSet) {
		Collection<SAFAInputMove<P, S>> transitions = new LinkedList<SAFAInputMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SAFAInputMove<P, S>> getInputMoves() {
		return getInputMovesFrom(states);
	}

	@Override
	public Object clone() {
		SAFA<P, S> cl = new SAFA<P, S>();

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<>(finalStates);

		cl.inputMovesFrom = new HashMap<Integer, Collection<SAFAInputMove<P, S>>>(inputMovesFrom);

		return cl;
	}

}

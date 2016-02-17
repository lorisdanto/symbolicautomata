/**
 * SVPAlib
 * automata.esfa
 * Jan 30, 2016
 * @author Qinheping Hu
 */
package automata.esfa;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import automata.ExtendedAutomaton;
import automata.ExtendedMove;
import theory.BooleanAlgebra;

/**
 * Symbolic finite automaton
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class CartesianESFA<P,S> extends ExtendedAutomaton<P, S> {

	
	
	private Integer initialState;
	private Collection<Integer> states;
	private Collection<Integer> finalStates;

	protected Map<Integer, Collection<CartesianESFAInputMove<P, S>>> inputMovesFrom;
	protected Map<Integer, Collection<CartesianESFAInputMove<P, S>>> inputMovesTo;
	protected Map<Integer, Collection<ESFAEpsilon<P, S>>> epsilonFrom;
	protected Map<Integer, Collection<ESFAEpsilon<P, S>>> epsilonTo;

	private Integer maxStateId;
	private Integer transitionCount;
	
	
	/**
	 * Returns true if the machine accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 */
	public boolean accepts(List<S> input, BooleanAlgebra<P, S> ba,Collection<Integer> departConf) {
		if(input.size()==0&& isFinalConfiguration(departConf)) return true;
		for(ExtendedMove<P, S> t : getMovesFrom(departConf)){
			Integer lh = t.lookahead;
			if(!t.isEpsilonTransition()){
				List<S> temp = input.subList(0, lh-1);
				if(t.hasModel(temp, ba)){
					Collection<Integer> nextState = new HashSet<Integer>();
					nextState.add(t.to);
					nextState = getEpsClosure(nextState,ba);
					if(nextState.isEmpty()) return false;
					boolean sig = accepts(input.subList(lh, input.size()),ba,nextState);
					if(sig) return sig;
				}
			}
		}
		return false;
	}
	
	public boolean accepts(List<S> input, BooleanAlgebra<P, S>ba){
		Collection<Integer> currConf = getEpsClosure(getInitialState(), ba);
		return accepts(input,ba,currConf);
	}
	
	private CartesianESFA() {
		super();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
		inputMovesFrom = new HashMap<Integer, Collection<CartesianESFAInputMove<P, S>>>();
		inputMovesTo = new HashMap<Integer, Collection<CartesianESFAInputMove<P, S>>>();
		epsilonFrom = new HashMap<Integer, Collection<ESFAEpsilon<P, S>>>();
		epsilonTo = new HashMap<Integer, Collection<ESFAEpsilon<P, S>>>();
		transitionCount = 0;
		maxStateId = 0;
	}

	/**
	 * Create an automaton and removes unreachable states
	 */
	public static <A, B> CartesianESFA<A, B> MkESFA(Collection<ESFAMove<A, B>> transitions,
			Integer initialState, Collection<Integer> finalStates,
			BooleanAlgebra<A, B> ba) {

		return MkESFA(transitions, initialState, finalStates, ba, true);
	}

	/**
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if <code>remUnreachableStates<code> is true
	 */
	public static <A, B> CartesianESFA<A, B> MkESFA(Collection<ESFAMove<A, B>> transitions,
			Integer initialState, Collection<Integer> finalStates,
			BooleanAlgebra<A, B> ba, boolean remUnreachableStates) {
		return MkESFA(transitions, initialState, finalStates, ba,
				remUnreachableStates, true);
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	private static <A, B> CartesianESFA<A, B> MkESFA(
			Collection<ESFAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba,
			boolean remUnreachableStates, boolean normalize) {

		CartesianESFA<A, B> aut = new CartesianESFA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = finalStates;
		if(finalStates.isEmpty())
			return getEmptyESFA(ba);

		for (ESFAMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

		//if (normalize)
		//	aut = aut.normalize(ba);

		//if (remUnreachableStates)
		//	aut = removeDeadOrUnreachableStates(aut, ba);

		if(aut.finalStates.isEmpty())			
			return getEmptyESFA(ba);
		
		return aut;
	}
	
	
	public static <A, B> CartesianESFA<A, B> getEmptyESFA(BooleanAlgebra<A, B> ba) {
		CartesianESFA<A, B> aut = new CartesianESFA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>();
		aut.initialState = 0;
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.isEpsilonFree = true;
		aut.maxStateId = 1;
		List<A> guard = new ArrayList<A>();
		guard.add(ba.True());
        aut.addTransition(new CartesianESFAInputMove<A, B>(0, 0, guard), ba, true);
		return aut;
	}
	
	private void addTransition(ESFAMove<P, S> transition,
			BooleanAlgebra<P, S> ba, boolean skipSatCheck) {
		if (transition.isEpsilonTransition()) {
			if (transition.to == transition.from)
				return;
			isEpsilonFree = false;
		}

		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.to > maxStateId)
				maxStateId = transition.to;

			states.add(transition.from);
			states.add(transition.to);

			if (!transition.isEpsilonTransition()) {
				getInputMovesFrom(transition.from).add(
						(CartesianESFAInputMove<P, S>) transition);
				getInputMovesTo(transition.to).add(
						(CartesianESFAInputMove<P, S>) transition);
			} else {
				getEpsilonFrom(transition.from).add((ESFAEpsilon<P, S>) transition);
				getEpsilonTo(transition.to).add((ESFAEpsilon<P, S>) transition);
			}
		}
	}
	
	/**
	 * Returns the set of transitions starting at state <code>s</code>
	 */
	public Collection<ESFAMove<P, S>> getTransitionsFrom(Integer state) {
		Collection<ESFAMove<P, S>> moves = new HashSet<ESFAMove<P, S>>();
		moves.addAll(getInputMovesFrom(state));
		moves.addAll(getEpsilonFrom(state));
		return moves;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<ESFAMove<P, S>> getTransitionsTo(Integer state) {
		Collection<ESFAMove<P, S>> moves = new HashSet<ESFAMove<P, S>>();
		moves.addAll(getInputMovesTo(state));
		moves.addAll(getEpsilonTo(state));
		return moves;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<ESFAMove<P, S>> getTransitionsFrom(
			Collection<Integer> stateSet) {
		Collection<ESFAMove<P, S>> transitions = new LinkedList<ESFAMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to a set of states
	 */
	public Collection<ESFAMove<P, S>> getTransitionsTo(
			Collection<Integer> stateSet) {
		Collection<ESFAMove<P, S>> transitions = new LinkedList<ESFAMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<ESFAEpsilon<P, S>> getEpsilonTo(Integer state) {
		Collection<ESFAEpsilon<P, S>> trset = epsilonTo.get(state);
		if (trset == null) {
			trset = new HashSet<ESFAEpsilon<P, S>>();
			epsilonTo.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<ESFAEpsilon<P, S>> getEpsilonTo(Collection<Integer> stateSet) {
		Collection<ESFAEpsilon<P, S>> transitions = new LinkedList<ESFAEpsilon<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getEpsilonTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<ESFAEpsilon<P, S>> getEpsilonFrom(Integer state) {
		Collection<ESFAEpsilon<P, S>> trset = epsilonFrom.get(state);
		if (trset == null) {
			trset = new HashSet<ESFAEpsilon<P, S>>();
			epsilonFrom.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<ESFAEpsilon<P, S>> getEpsilonFrom(Collection<Integer> stateSet) {
		Collection<ESFAEpsilon<P, S>> transitions = new LinkedList<ESFAEpsilon<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getEpsilonFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<CartesianESFAInputMove<P, S>> getInputMovesTo(Integer state) {
		Collection<CartesianESFAInputMove<P, S>> trset = inputMovesTo.get(state);
		if (trset == null) {
			trset = new HashSet<CartesianESFAInputMove<P, S>>();
			inputMovesTo.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<CartesianESFAInputMove<P, S>> getInputMovesTo(
			Collection<Integer> stateSet) {
		Collection<CartesianESFAInputMove<P, S>> transitions = new LinkedList<CartesianESFAInputMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<CartesianESFAInputMove<P, S>> getInputMovesFrom(Integer state) {
		Collection<CartesianESFAInputMove<P, S>> trset = inputMovesFrom.get(state);
		if (trset == null) {
			trset = new HashSet<CartesianESFAInputMove<P, S>>();
			inputMovesFrom.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<CartesianESFAInputMove<P, S>> getInputMovesFrom(
			Collection<Integer> stateSet) {
		Collection<CartesianESFAInputMove<P, S>> transitions = new LinkedList<CartesianESFAInputMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<ESFAMove<P, S>> getTransitions() {
		Collection<ESFAMove<P, S>> transitions = new LinkedList<ESFAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}
	

	
	@Override
	public Collection<ExtendedMove<P, S>> getMovesFrom(Integer state) {
		Collection<ExtendedMove<P, S>> transitions = new LinkedList<ExtendedMove<P, S>>();
		transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}


	
	@Override
	public Collection<ExtendedMove<P, S>> getMovesTo(Integer state) {
		Collection<ExtendedMove<P, S>> transitions = new LinkedList<ExtendedMove<P, S>>();
		transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	@Override
	public Collection<Integer> getStates() {
		return this.states;
	}

	@Override
	public Integer getInitialState() {
		return this.initialState;
	}

	@Override
	public Collection<Integer> getFinalStates() {
		return this.finalStates;
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

}

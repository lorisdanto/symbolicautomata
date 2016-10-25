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

import org.sat4j.specs.TimeoutException;

import automata.ExtendedAutomaton;
import automata.ExtendedMove;
import theory.BooleanAlgebra;
import utilities.Pair;

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
	 * @throws TimeoutException 
	 */
	public boolean accepts(List<S> input, BooleanAlgebra<P, S> ba,Collection<Integer> departConf) throws TimeoutException {
		if(input.size()==0&& isFinalConfiguration(departConf)) return true;


		for(ExtendedMove<P, S> t : getMovesFrom(departConf)){
			Integer lh = t.lookahead;
			if(!t.isEpsilonTransition()){
				List<S> temp = input.subList(0, lh);
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
	
	public boolean accepts(List<S> input, BooleanAlgebra<P, S>ba) throws TimeoutException{
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
	 * Checks whether the SFA is ambiguous
	 * 
	 * @return an ambiguous input if the automaton is ambiguous,
	 *         <code>null</code> otherwise
	 * @throws TimeoutException 
	 */
	public List<S> getAmbiguousInput(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return getAmbiguousInput(this, ba);
	}

	
	@SuppressWarnings("unchecked")
	public static <A, B> List<B> getAmbiguousInput(CartesianESFA<A, B> aut,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		CartesianESFA<A, B> aut1 = (CartesianESFA<A, B>) aut.clone();
		CartesianESFA<A, B> aut2 = (CartesianESFA<A, B>) aut.clone();

		CartesianESFA<A, B> product = new CartesianESFA<A, B>();

		// maps a product state to its id
		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		// maps and id to its product state
		HashMap<Integer, Pair<Integer, Integer>> reachedRev = new HashMap<Integer, Pair<Integer, Integer>>();
		// list on unexplored product states
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// The initial state is the pair consisting of the initial
		// states of aut1 and aut2
		Pair<Integer, Integer> initStatePair = new Pair<Integer, Integer>(
				aut1.initialState, aut2.initialState);
		product.initialState = 0;
		product.states.add(0);

		reached.put(initStatePair, 0);
		reachedRev.put(0, initStatePair);
		toVisit.add(initStatePair);

		int totStates = 1;

		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();

			
			int currStateId = reached.get(currState);
			// get the set of states reachable from currentState via epsilon
			// moves
			Collection<Integer> epsilonClosure1 = aut1.getEpsClosure(
					currState.first, ba);
			Collection<Integer> epsilonClosure2 = aut2.getEpsClosure(
					currState.second, ba);

			// Set final states
			// if both the epsilon closures contain a final state currentStateID
			// is final

			if (aut1.isFinalConfiguration(epsilonClosure1)
					&& aut2.isFinalConfiguration(epsilonClosure2))
				product.finalStates.add(currStateId);
			
			// Try to pair transitions out of both automata
			for (CartesianESFAInputMove<A, B> t1 : aut1
					.getInputMovesFrom(epsilonClosure1))
				for (CartesianESFAInputMove<A, B> t2 : aut2
						.getInputMovesFrom(epsilonClosure2)) {
					if(t1.lookahead != t2.lookahead) continue;
					// create conjunction of the two guards and create
					// transition only if the conjunction is satisfiable
					List<A> intersGuard = new ArrayList<A>();
					boolean satofinter = true;
					for(int i=0;i<t1.lookahead;i++){
						intersGuard.add(ba.MkAnd(t1.guard.get(i), t2.guard.get(i)));
						if(!ba.IsSatisfiable(ba.MkAnd(t1.guard.get(i), t2.guard.get(i)))) satofinter = false;
					}
					if (satofinter) {

						// Create new product transition and add it to
						// transitions
						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
								t1.to, t2.to);
						int nextStateId = 0;

						if (!reached.containsKey(nextState)) {
							product.inputMovesTo.put(totStates,
									new HashSet<CartesianESFAInputMove<A, B>>());

							reached.put(nextState, totStates);
							reachedRev.put(totStates, nextState);

							toVisit.add(nextState);
							product.states.add(totStates);
							nextStateId = totStates;
							totStates++;
						} else
							nextStateId = reached.get(nextState);

						CartesianESFAInputMove<A, B> newTrans = new CartesianESFAInputMove<A, B>(
								currStateId, nextStateId, intersGuard);

						product.addTransition(newTrans, ba, true);
						
					}

				}
		}
		System.out.println(product);
		product = removeDeadOrUnreachableStates(product, ba);
		// Check if a state that of the form (s1,s2) such that s1!=s2 is still
		// alive, if so any string passing to it is ambiguous
		for (Integer aliveSt : product.states) {
			Pair<Integer, Integer> stP = reachedRev.get(aliveSt);
			if (stP.first != stP.second) {
				CartesianESFA<A, B> left = (CartesianESFA<A, B>) product.clone();
				CartesianESFA<A, B> right = (CartesianESFA<A, B>) product.clone();
				left.finalStates = new HashSet<Integer>();
				left.finalStates.add(aliveSt);
				right.initialState = aliveSt;

				CartesianESFA<A, B> c = left.concatenateWith(right, ba);
				CartesianESFA<A, B> clean = removeDeadOrUnreachableStates(c, ba);
				return clean.getWitness(ba);
			}
		}
		return null;
	}
	

	/**
	 * concatenation with aut
	 */
	public CartesianESFA<P, S> concatenateWith(CartesianESFA<P, S> aut, BooleanAlgebra<P, S> ba) {
		return concatenate(this, aut, ba);
	}

	/**
	 * concatenates aut1 with aut2
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> CartesianESFA<A, B> concatenate(CartesianESFA<A, B> aut1, CartesianESFA<A, B> aut2,
			BooleanAlgebra<A, B> ba) {

		if (aut1.isEmpty || aut2.isEmpty)
			return getEmptyESFA(ba);

		Collection<ESFAMove<A, B>> transitions = new ArrayList<ESFAMove<A, B>>();
		Integer initialState = aut1.initialState;
		Collection<Integer> finalStates = new HashSet<Integer>();

		int offSet = aut1.maxStateId + 1;

		for (ESFAMove<A, B> t : aut1.getTransitions())
			transitions.add((ESFAMove<A, B>) t.clone());

		for (ESFAMove<A, B> t : aut2.getTransitions()) {
			ESFAMove<A, B> newMove = (ESFAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			transitions.add(newMove);
		}

		for (Integer state1 : aut1.finalStates)
			transitions.add(new ESFAEpsilon<A, B>(state1, aut2.initialState
					+ offSet));

		for (Integer state : aut2.finalStates)
			finalStates.add(state + offSet);

		return MkESFA(transitions, initialState, finalStates, ba, false);
	}
	// ------------------------------------------------------
		// Reachability methods
		// ------------------------------------------------------

		// creates a new SFA where all unreachable or dead states have been removed
		private static <A, B> CartesianESFA<A, B> removeDeadOrUnreachableStates(
				CartesianESFA<A, B> aut, BooleanAlgebra<A, B> ba) {

			// components of new SFA
			Collection<ESFAMove<A, B>> transitions = new ArrayList<ESFAMove<A, B>>();
			Integer initialState = 0;
			Collection<Integer> finalStates = new HashSet<Integer>();

			HashSet<Integer> initStates = new HashSet<Integer>();
			initStates.add(aut.initialState);
			Collection<Integer> reachableFromInit = aut
					.getReachableStatesFrom(initStates);
			Collection<Integer> reachingFinal = aut
					.getReachingStates(aut.finalStates);

			Collection<Integer> aliveStates = new HashSet<Integer>();

			// Computes states that reachable from initial state and can reach a
			// final state
			for (Integer state : reachableFromInit)
				if (reachingFinal.contains(state)) {
					aliveStates.add(state);
				}

			if (aliveStates.size() == 0)
				return getEmptyESFA(ba);

			for (Integer state : aliveStates)
				for (ESFAMove<A, B> t : aut.getTransitionsFrom(state))
					if (aliveStates.contains(t.to))
						transitions.add(t);

			initialState = aut.initialState;

			for (Integer state : aut.finalStates)
				if (aliveStates.contains(state))
					finalStates.add(state);

			return MkESFA(transitions, initialState, finalStates, ba, false, false);
		}
		
		// Computes states that reachable from states
		private Collection<Integer> getReachableStatesFrom(
				Collection<Integer> states) {
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
				for (ESFAMove<P, S> t : this.getTransitionsFrom(state)) {
					Integer nextState = t.to;
					visitForward(nextState, reached);
				}
			}
		}	
		// backward DFS accumulates in reached
		private void visitBackward(Integer state, HashSet<Integer> reached) {
			if (!reached.contains(state)) {
				reached.add(state);
				for (ESFAMove<P, S> t : this.getTransitionsTo(state)) {
					Integer predState = t.from;
					visitBackward(predState, reached);
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
	
	@Override
	public Object clone() {
		CartesianESFA<P, S> cl = new CartesianESFA<P, S>();

		cl.isDeterministic = isDeterministic;
		cl.isTotal = isTotal;
		cl.isEmpty = isEmpty;
		cl.isEpsilonFree = isEpsilonFree;

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.inputMovesFrom = new HashMap<Integer, Collection<CartesianESFAInputMove<P, S>>>(
				inputMovesFrom);
		cl.inputMovesTo = new HashMap<Integer, Collection<CartesianESFAInputMove<P, S>>>(
				inputMovesTo);

		cl.epsilonFrom = new HashMap<Integer, Collection<ESFAEpsilon<P, S>>>(
				epsilonFrom);
		cl.epsilonTo = new HashMap<Integer, Collection<ESFAEpsilon<P, S>>>(
				epsilonTo);

		return cl;
	}

}

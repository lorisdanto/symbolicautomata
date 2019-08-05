/**
 * SVPAlib
 * transducers.sft
 * Mar 20, 2018
 * @author Loris D'Antoni
 */
package transducers.sft;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import org.sat4j.specs.TimeoutException;

import automata.Automaton;
import automata.Move;
import automata.sfa.SFA;
import automata.sfa.SFAMove;
import automata.sfa.SFAInputMove;
import theory.BooleanAlgebraSubst;
import utilities.Pair;

/**
 * the product of two symbolic finite state transducers, which is discussed on the paper named after Symbolic Finite
 * State Transducers: Algorithms And Applications
 * Because SFTs are not closed under product, I have to create a new type to represent the product of two SFTs.
 *
 *
 * @param <P>
 *			The type of predicates forming the Boolean algebra
 * @param <F>
 *			The type of functions S->S in the Boolean Algebra
 * @param <S>
 *			The domain of the Boolean algebra
 */
public class SFTProduct<P, F, S> extends Automaton<P, S> {

	// SFT properties
	protected Collection<Integer> states;
	protected Integer initialState;
	protected Map<Integer, Pair<Set<List<S>>, Set<List<S>>>> finalStatesAndTails; // it stores tails for 2 SFTs separately
	protected Integer maxStateId;

	// Moves are inputs
	protected Map<Integer, Collection<SFTProductInputMove<P, F, S>>> transitionsFrom;
	protected Map<Integer, Collection<SFTProductInputMove<P, F, S>>> transitionsTo;

	// Every time we construct a product of 2 SFTs, we always remove the epsilon transitions in 2 SFTs so that there is
	// no epsilon transition in SFTProduct

	public Integer stateCount() {
		return states.size();
	}

	public Integer transitionCount() {
		return getTransitions().size();
	}

	private SFTProduct() {
		super();
		states = new HashSet<Integer>();
		transitionsFrom = new HashMap<Integer, Collection<SFTProductInputMove<P, F, S>>>();
		transitionsTo = new HashMap<Integer, Collection<SFTProductInputMove<P, F, S>>>();
		finalStatesAndTails = new HashMap<Integer, Pair<Set<List<S>>, Set<List<S>>>>();
		maxStateId = 0;
		initialState = 0;
	}

	/*
	* Create a product of two SFTs (removes unreachable states and all tails of final states)
	* Page 3, in the 8-th line of left column, definition 7
	*/
	public static <P, F, S> SFTProduct<P, F, S> MkSFTProduct(SFT<P, F, S> sft1withEps, SFT<P, F, S> sft2withEps, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		// Remove epsilons
		SFT<P, F, S> sft1 = sft1withEps.removeEpsilonMoves(ba);
		SFT<P, F, S> sft2 = sft2withEps.removeEpsilonMoves(ba);

		Collection<SFTMove<P, F, S>> transitions = new ArrayList<SFTMove<P, F, S>>();
		Integer initialState;

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial state
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(sft1.getInitialState(), sft2.getInitialState());

		initialState = 0;
		Map<Integer, Pair<Set<List<S>>, Set<List<S>>>> finalStatesAndTails = new HashMap<Integer, Pair<Set<List<S>>, Set<List<S>>>>();

		reached.put(p, initialState);
		toVisit.add(p);

		// Combined has set of variables the disjoint union of the two sets
		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			// If both states are final, combine is final
			if (sft1.isFinalState(currState.first) && sft2.isFinalState(currState.second)) {
				finalStatesAndTails.put(currStateId, new Pair<Set<List<S>>, Set<List<S>>>(
						sft1.getFinalStatesAndTails().get(currState.first), 
						sft2.getFinalStatesAndTails().get(currState.second)));
			}

			for (SFTInputMove<P, F, S> t1 : sft1.getInputMovesFrom(currState.first))
				for (SFTInputMove<P, F, S> t2 : sft2.getInputMovesFrom(currState.second)) {
					P intersGuard = null;
					try {
						intersGuard = ba.MkAnd(t1.guard, t2.guard);
					} catch (TimeoutException te) {
						te.printStackTrace();
					}
					try {
						if (ba.IsSatisfiable(intersGuard)) {
							Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(t1.to, t2.to);
							int nextStateId = getStateId(nextState, reached, toVisit); // update reached and toVisit
							SFTProductInputMove<P, F, S> newTrans = new SFTProductInputMove<P, F, S>(currStateId, nextStateId,
									intersGuard, t1.outputFunctions, t2.outputFunctions);
							transitions.add(newTrans);
						}
					} catch (TimeoutException te) {
						te.printStackTrace();
					}
				}
		}
		SFTProduct sftProduct = MkSFTProduct(transitions, initialState, finalStatesAndTails, ba);
		return sftProduct.removeDeadend(ba);
	}

	/*
	* Create a product of two SFTs (removes unreachable states)
	*/
	private SFTProduct<P, F, S> removeDeadend(BooleanAlgebraSubst<P, F, S> ba) {
		Collection<SFTMove<P, F, S>> transitions = new ArrayList<SFTMove<P, F, S>>();

		HashSet<Integer> reached = new HashSet<Integer>();
		LinkedList<Integer> toVisit = new LinkedList<Integer>();
		toVisit.addAll(this.finalStatesAndTails.keySet());

		while (!toVisit.isEmpty()) {
			Integer currState = toVisit.removeFirst();
			reached.add(currState);

			for (SFTProductInputMove transition: this.getInputMovesTo(currState)) {
				transitions.add(transition);
				if (!reached.contains(transition.from))
					toVisit.add(transition.from);
			}
		}

		return MkSFTProduct(transitions, this.initialState, this.finalStatesAndTails, ba);
	}

	/*
	* Create a product of two SFTs (removes unreachable states)
	*/
	private static <P, F, S> SFTProduct<P, F, S> MkSFTProduct(Collection<SFTMove<P, F, S>> transitions, Integer initialState,
															  Map<Integer, Pair<Set<List<S>>, Set<List<S>>>> finalStatesAndTails,
													 BooleanAlgebraSubst<P, F, S> ba) {
		SFTProduct<P, F, S> aut = new SFTProduct<P, F, S>();

		// Initialize state set
		aut.initialState = initialState;

		aut.finalStatesAndTails = finalStatesAndTails;
		for (Integer state: finalStatesAndTails.keySet()) {
			Set<List<S>> tails1 = finalStatesAndTails.get(state).first;
			Set<List<S>> tails2 = finalStatesAndTails.get(state).second;

			Set<List<S>> nonEmptyTails1 = new HashSet<List<S>>();
			for (List<S> tail: tails1) {
				if (tail.size() != 0) // remove the empty tail which is just an empty List<S> for brevity
					nonEmptyTails1.add(tail);
			}
			Set<List<S>> nonEmptyTails2 = new HashSet<List<S>>();
			for (List<S> tail: tails2) {
				if (tail.size() != 0)
					nonEmptyTails2.add(tail);
			}

			aut.finalStatesAndTails.put(state, new Pair<Set<List<S>>, Set<List<S>>>(nonEmptyTails1, nonEmptyTails2));
		}
		// Now all tails are not empty.
		// MkSFT in SFT.java has removed all non-empty tails and SFTProduct can only be made by two SFTs so there could
		// not be any empty tails in SFTProduct and the codes above is unnecessary. But I still want to ensure that
		// there is no empty tails in SFTProduct.

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStatesAndTails.keySet());
		
		try {
			for (SFTMove<P, F, S> t : transitions)
				aut.addTransition(t, ba, false);
			return aut;
		} catch (TimeoutException toe) {
			return null;
		}
	}

	/**
	 * Returns the empty SFTProduct
	 */
	public static <P, F, S> SFTProduct<P, F, S> getEmptySFTProduct(BooleanAlgebraSubst<P, F, S> ba) {
		SFTProduct<P, F, S> aut = new SFTProduct<P, F, S>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.initialState = 0;
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.isEpsilonFree = true;
		aut.maxStateId = 1;
		return aut;
	}

	private List<List<SFTProductInputMove<P, F, S>>> getPossibleTransitionChains(Integer startState, int steps) {
		return possibleTransitionChains(this, startState, steps);
	}

	/**
	 * Given a symbolic finite transducer and the start state, return all possible transition chains in
	 * <code>steps</code> steps
	 * @param sft symbolic finite transducer
	 * @param startState the start state which could be any state, including but not limited to the initial state
	 * @param steps the number of steps, which should be a natural number
	 * @return
	 */
	private static <P, F, S> List<List<SFTProductInputMove<P, F, S>>> possibleTransitionChains(SFTProduct<P, F, S> sft, Integer startState, int steps) {
		List<List<SFTProductInputMove<P, F, S>>> chains = new ArrayList<List<SFTProductInputMove<P, F, S>>>();
		for (SFTProductInputMove<P, F, S> initialTransition: sft.getInputMovesFrom(startState)) {
			List<SFTProductInputMove<P, F, S>> tempList = new LinkedList<SFTProductInputMove<P, F, S>>();
			tempList.add(initialTransition); // so the size of tempList is greater than 1
			backtrack(chains, tempList, sft, steps - 1);
		}
		return chains;
	}

	// use backtrack method to get all possible transition chains
	private static <P, F, S> void backtrack(List<List<SFTProductInputMove<P, F, S>>> chains, List<SFTProductInputMove<P, F, S>> tempList, SFTProduct<P, F, S> sft, int remainSteps) {
		if (remainSteps < 0)
			return; // no solution
		else if (remainSteps == 0)
			chains.add(new ArrayList<SFTProductInputMove<P, F, S>>(tempList));
		else {
			Integer currentState = tempList.get(tempList.size() - 1).to;
			for (SFTProductInputMove<P, F, S> transition: sft.getInputMovesFrom(currentState)) {
				tempList.add(transition);
				backtrack(chains, tempList, sft, remainSteps - 1);
				tempList.remove(tempList.size() - 1);
			}
		}
	}

	/**
	 * Computes the domain automaton of the sft
	 *
	 * @throws TimeoutException
	 */
	public SFA<P, S> getDomain(BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();

		for (SFTProductInputMove<P, F, S> t : getInputMovesFrom(states))
			transitions.add(new SFAInputMove<P, S>(t.from, t.to, t.guard));

		Collection<Integer> finalStates = getFinalStates();

		return SFA.MkSFA(transitions, initialState, finalStates, ba);
	}

	/**
	 * use DFS to find a witness from the <code>startState</code> to a final state
	 * if there the <code>startState</code> is in a dead end, return null
	 * @param startState
	 * @param ba
	 * @return
	 */
	public List<S> getWitness(Integer startState, BooleanAlgebraSubst<P, F, S> ba) {
		HashMap<Integer, List<S>> reached = new HashMap<Integer, List<S>>();
		LinkedList<Integer> toVisit = new LinkedList<Integer>();

		reached.put(startState, new ArrayList<S>());
		toVisit.add(startState);

		while (!toVisit.isEmpty()) {
			Integer currState = toVisit.pop();
			if (this.isFinalState(currState)) {
				return reached.get(currState);
			}
			for (SFTProductInputMove transition: this.getInputMovesFrom(currState)) {
				List<S> previousPath = new ArrayList<S>(reached.get(transition.from));
				try {
					previousPath.add((S) transition.getWitness(ba));
				} catch (TimeoutException te) {
					te.printStackTrace();
				}
				if (this.isFinalState(transition.to)) {
					return previousPath;
				}
				reached.put(transition.to, previousPath);
			}
		}
		return null; // there is no such a path from the <code>startState</code> to any final state
	}

	/**
	 * Add Transition
	 *
	 * @throws TimeoutException
	 */
	private void addTransition(SFTMove<P, F, S> transition, BooleanAlgebraSubst<P, F, S> ba, boolean skipSatCheck)
			throws TimeoutException {

		if (transition.isEpsilonTransition()) {
			if (transition.to == transition.from)
				return;
			isEpsilonFree = false;
		}

		if (skipSatCheck || transition.isSatisfiable(ba)) {

			if (transition.from > maxStateId)
				maxStateId = transition.from;
			if (transition.to > maxStateId)
				maxStateId = transition.to;

			states.add(transition.from);
			states.add(transition.to);

			getInputMovesFrom(transition.from).add((SFTProductInputMove<P, F, S>) transition);
			getInputMovesTo(transition.to).add((SFTProductInputMove<P, F, S>) transition);
		}
	}

	// ACCESORIES METHODS

	// GET INPUT MOVES

	/**
	 * Returns the set of transitions from state <code>s</code>
	 */
	public Collection<SFTProductInputMove<P, F, S>> getInputMovesFrom(Integer state) {
		Collection<SFTProductInputMove<P, F, S>> trset = transitionsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SFTProductInputMove<P, F, S>>();
			transitionsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SFTProductInputMove<P, F, S>> getInputMovesFrom(Collection<Integer> stateSet) {
		Collection<SFTProductInputMove<P, F, S>> transitions = new LinkedList<SFTProductInputMove<P, F, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of input transitions to state <code>s</code>
	 */
	public Collection<SFTProductInputMove<P, F, S>> getInputMovesTo(Integer state) {
		Collection<SFTProductInputMove<P, F, S>> trset = transitionsTo.get(state);
		if (trset == null) {
			trset = new HashSet<SFTProductInputMove<P, F, S>>();
			transitionsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions to set of states
	 */
	public Collection<SFTProductInputMove<P, F, S>> getInputMovesTo(Collection<Integer> stateSet) {
		Collection<SFTProductInputMove<P, F, S>> transitions = new LinkedList<SFTProductInputMove<P, F, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SFTProductInputMove<P, F, S>> getTransitions() {
		return getInputMovesFrom(states);
	}

	// Methods for superclass
	@Override
	public Collection<Move<P, S>> getMoves() {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getTransitions());
		return transitions;
	}

	@Override
	public Collection<Move<P, S>> getMovesFrom(Integer state) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	@Override
	public Collection<Move<P, S>> getMovesTo(Integer state) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getInputMovesTo(state));
		return transitions;
	}

	@Override
	public Collection<Integer> getFinalStates() {
		return finalStatesAndTails.keySet();
	}

	public Map<Integer, Pair<Set<List<S>>, Set<List<S>>>> getFinalStatesAndTails() {
		return finalStatesAndTails;
	}

	@Override
	public Integer getInitialState() {
		return initialState;
	}

	@Override
	public Collection<Integer> getStates() {
		return states;
	}
	
	@Override
	public Object clone() {
		SFTProduct<P, F, S> cl = new SFTProduct<P, F, S>();

		cl.isDeterministic = isDeterministic;
		cl.isTotal = isTotal;
		cl.isEmpty = isEmpty;
		cl.isEpsilonFree = isEpsilonFree;

		cl.maxStateId = maxStateId;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;

		cl.transitionsFrom = new HashMap<Integer, Collection<SFTProductInputMove<P, F, S>>>(transitionsFrom);
		cl.transitionsTo = new HashMap<Integer, Collection<SFTProductInputMove<P, F, S>>>(transitionsTo);

		cl.finalStatesAndTails = new HashMap<Integer, Pair<Set<List<S>>, Set<List<S>>>>(finalStatesAndTails);

		return cl;
	}

	@Override
	public String toString() {
		String s = "";
		s = "SFT product: " + getMoves().size() + " transitions, " + getStates().size() + " states" + "\n";
		s += "Transitions \n";
		for (Move<P, S> t : getMoves())
			s = s + t + "\n";

		s += "Initial State \n";
		s = s + getInitialState() + "\n";

		s += "Final States \n";
		for (Integer fs : getFinalStates())
			if (getFinalStatesAndTails().get(fs) == null)
				s = s + fs + "\n";
			else
				s = s + fs + " " + getFinalStatesAndTails().get(fs) + "\n";
		return s;
	}

}

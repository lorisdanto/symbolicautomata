/**
 * 
 */
package automata.fsa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theory.BooleanAlgebra;
import utilities.Pair;
import automata.Automaton;
import automata.Move;

public class SFA<U, S> extends Automaton<U, S> {

	// Constants
	/**
	 * Returns the empty SFA for the boolean algebra <code>ba</code>
	 */
	public static <A, B> SFA<A, B> getEmptySFA(BooleanAlgebra<A, B> ba) {
		SFA<A, B> aut = new SFA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>();
		aut.initialState = 0;
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.isEpsilonFree = true;
		aut.maxStateId = 1;
		return aut;
	}

	/**
	 * Returns the SFA accepting every list for the boolean algebra
	 * <code>ba</code>
	 */
	public static <A, B> SFA<A, B> getFullSFA(BooleanAlgebra<A, B> ba) {
		SFA<A, B> aut = new SFA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>(aut.states);
		aut.initialState = 0;
		aut.isDeterministic = true;
		aut.isEmpty = false;
		aut.isEpsilonFree = true;
		aut.maxStateId = 1;
		aut.addTransition(new InputMove<A, B>(0, 0, ba.True()), ba, true);
		return aut;
	}

	protected Collection<Integer> states;
	protected Integer initialState;
	protected Collection<Integer> finalStates;

	public Integer stateCount(){
		return states.size();
	}
	public Integer maxStateId;

	protected Map<Integer, Collection<SFAMove<U, S>>> transitionsFrom;
	protected Map<Integer, Collection<SFAMove<U, S>>> transitionsTo;
	public Integer transitionCount;

	protected SFA() {
		super();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
		transitionsFrom = new HashMap<Integer, Collection<SFAMove<U, S>>>();
		transitionsTo = new HashMap<Integer, Collection<SFAMove<U, S>>>();
		transitionCount = 0;
		maxStateId = 0;
	}

	/*
	 * Create an automaton and removes unreachable states
	 */
	public static <A, B> SFA<A, B> MkSFA(Collection<SFAMove<A, B>> transitions,
			Integer initialState, Collection<Integer> finalStates,
			BooleanAlgebra<A, B> ba) {

		return MkSFA(transitions, initialState, finalStates, ba, true);
	}

	/*
	 * Create an automaton and removes unreachable states
	 */
	private static <A, B> SFA<A, B> MkSFA(
			Collection<SFAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba,
			boolean remUnreachableStates) {

		// Sanity checks

		SFA<A, B> aut = new SFA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = finalStates;

		for (SFAMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

		// cleanup set isEmpty and hasEpsilon
		if (remUnreachableStates)
			aut = removeDeadOrUnreachableStates(aut, ba);

		return aut;
	}

	/**
	 * Computes the intersection with <code>aut</code> as a new SFA
	 */
	public SFA<U, S> intersectionWith(SFA<U, S> aut, BooleanAlgebra<U, S> ba) {
		return intersection(this, aut, ba);
	}

	/**
	 * Computes the intersection with <code>aut</code> as a new SFA
	 */
	public static <A, B> SFA<A, B> intersection(SFA<A, B> aut1, SFA<A, B> aut2,
			BooleanAlgebra<A, B> ba) {

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial states
		Pair<Integer, Integer> initPair = new Pair<Integer, Integer>(
				aut1.initialState, aut2.initialState);

		reached.put(initPair, 0);
		toVisit.add(initPair);

		int totStates = 1;

		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			Collection<Integer> epsClo1 = aut1.getEpsClosure(currState.first,
					ba);
			Collection<Integer> epsClo2 = aut2.getEpsClosure(currState.second,
					ba);

			// Set final states
			boolean isFin = false;
			for (Integer st : epsClo1)
				if (aut1.isFinalState(st)) {
					isFin = true;
					break;
				}
			if (isFin) {
				isFin = false;
				for (Integer st : epsClo2)
					if (aut2.isFinalState(st)) {
						isFin = true;
						break;
					}
				if (isFin)
					finalStates.add(currStateId);
			}

			for (SFAMove<A, B> t1 : aut1.getTransitionsFrom(epsClo1))
				for (SFAMove<A, B> t2 : aut2.getTransitionsFrom(epsClo2)) {
					if (!t1.isEpsilonTransition() && !t2.isEpsilonTransition()) {
						InputMove<A, B> ct1 = (InputMove<A, B>) t1;
						InputMove<A, B> ct2 = (InputMove<A, B>) t2;
						A intersGuard = ba.MkAnd(ct1.guard, ct2.guard);
						if (ba.IsSatisfiable(intersGuard)) {

							Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
									t1.to, t2.to);
							int nextStateId = 0;

							if (!reached.containsKey(nextState)) {
								reached.put(nextState, totStates);
								toVisit.add(nextState);
								nextStateId = totStates;
								totStates++;
							} else
								nextStateId = reached.get(nextState);

							InputMove<A, B> newTrans = new InputMove<A, B>(
									currStateId, nextStateId, intersGuard);

							transitions.add(newTrans);
						}
					}
				}
		}

		return MkSFA(transitions, initialState, finalStates, ba);
	}

	/**
	 * Computes <code>this</code> minus <code>aut2</code>
	 */
	public SFA<U, S> minus(SFA<U, S> aut, BooleanAlgebra<U, S> ba) {
		return differnce(this, aut, ba);
	}

	/**
	 * Computes <code>aut1</code> minus <code>aut2</code>
	 */
	public static <A, B> SFA<A, B> differnce(SFA<A, B> aut1, SFA<A, B> aut2,
			BooleanAlgebra<A, B> ba) {
		return aut1.intersectionWith(aut2.complement(ba), ba);
	}

	/**
	 * Computes the union with <code>aut</code> as a new SFA
	 */
	public SFA<U, S> unionWith(SFA<U, S> aut1, BooleanAlgebra<U, S> ba) {
		return union(this, aut1, ba);
	}

	/**
	 * Computes the union with <code>aut</code> as a new SFA
	 */
	public static <A, B> SFA<A, B> union(SFA<A, B> aut1, SFA<A, B> aut2,
			BooleanAlgebra<A, B> ba) {

		if (aut1.isEmpty && aut2.isEmpty)
			return getEmptySFA(ba);

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		int offSet = aut1.maxStateId + 2;
		int maxStateId = aut2.maxStateId + offSet + 1;

		initialState = maxStateId;

		for (SFAMove<A, B> t : aut1.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			transitions.add(newMove);
		}

		for (SFAMove<A, B> t : aut2.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			transitions.add(newMove);
		}

		// Add transitions from new initial state to old initial states
		transitions.add(new Epsilon<A, B>(initialState, aut1.initialState));
		transitions.add(new Epsilon<A, B>(initialState, aut2.initialState
				+ offSet));

		// Make all states of the two machines final
		finalStates.addAll(aut1.finalStates);

		for (Integer state : aut2.finalStates)
			finalStates.add(state + offSet);

		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * return an equivalent copy without epsilon moves
	 */
	public SFA<U, S> removeEpsilonMoves(BooleanAlgebra<U, S> ba) {
		return removeEpsilonMovesFrom(this, ba);
	}

	/**
	 * return an equivalent copy without epsilon moves
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SFA<A, B> removeEpsilonMovesFrom(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		if (aut.isEpsilonFree)
			return (SFA<A, B>) aut.clone();

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// Add initial state
		Collection<Integer> reachableFromInit = aut.getEpsClosure(
				aut.initialState, ba);

		reachedStates.put(reachableFromInit, 0);
		toVisitStates.add(reachableFromInit);

		while (!toVisitStates.isEmpty()) {
			Collection<Integer> currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);

			for (SFAMove<A, B> t1 : aut.getTransitionsFrom(currState)) {
				if (!t1.isEpsilonTransition()) {
					Collection<Integer> nextState = aut
							.getEpsClosure(t1.to, ba);

					int nextStateId = 0;

					if (!reachedStates.containsKey(nextState)) {
						int index = reachedStates.size();
						reachedStates.put(nextState, index);
						toVisitStates.add(nextState);
						nextStateId = index;
					} else {
						nextStateId = reachedStates.get(nextState);
					}

					SFAMove<A, B> tnew = (SFAMove<A, B>) t1.clone();
					tnew.from = currStateId;
					tnew.to = nextStateId;

					transitions.add(tnew);
				}
			}

		}

		for (Collection<Integer> stSet : reachedStates.keySet())
			if (aut.isFinalConfiguration(stSet))
				finalStates.add(reachedStates.get(stSet));

		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * return the complement of the current SFA
	 */
	public SFA<U, S> complement(BooleanAlgebra<U, S> ba) {
		return complementOf(this, ba);
	}

	/**
	 * return the complement of the current SFA
	 */
	public static <A, B> SFA<A, B> complementOf(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		SFA<A, B> comp = aut.mkTotal(ba);

		Collection<Integer> newFinalStates = new HashSet<Integer>();

		for (Integer st : comp.states)
			if (!comp.finalStates.contains(st))
				newFinalStates.add(st);

		return MkSFA(comp.getTransitions(), comp.initialState, newFinalStates,
				ba, false);
	}

	/**
	 * return the total version of the current SFA
	 */
	public SFA<U, S> mkTotal(BooleanAlgebra<U, S> ba) {
		return mkTotal(this, ba);
	}

	/**
	 * return the total version of aut
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SFA<A, B> mkTotal(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		if (aut.isTotal) {
			return (SFA<A, B>) aut.clone();
		}

		SFA<A, B> sfa = aut;
		if (!aut.isDeterministic(ba))
			sfa = determinize(aut, ba);

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = sfa.initialState;
		Collection<Integer> finalStates = new HashSet<Integer>(sfa.finalStates);

		int sinkState = sfa.maxStateId + 1;
		boolean addSink = false;
		for (Integer state : sfa.states) {
			A totGuard = null;
			for (InputMove<A, B> move : sfa.getInputMovesFrom(state)) {
				transitions.add(move);
				if (totGuard == null)
					totGuard = ba.MkNot(move.guard);
				else
					totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
			}
			if (totGuard != null && ba.IsSatisfiable(totGuard)) {
				addSink = true;
				transitions
						.add(new InputMove<A, B>(state, sinkState, totGuard));
			}
		}
		if (addSink)
			transitions
					.add(new InputMove<A, B>(sinkState, sinkState, ba.True()));

		// Do not remove unreachable states otherwise the sink will be removed
		// again
		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * checks whether the aut accepts the same language
	 */
	public boolean isEquivalentTo(SFA<U, S> aut, BooleanAlgebra<U, S> ba) {
		return areEquivalent(this, aut, ba);
	}

	/**
	 * checks wheter aut1 is equivalent to aut2
	 */
	public static <A, B> boolean areEquivalent(SFA<A, B> aut1, SFA<A, B> aut2,
			BooleanAlgebra<A, B> ba) {
		if (!differnce(aut1, aut2, ba).isEmpty)
			return false;
		return differnce(aut2, aut1, ba).isEmpty;
	}

	/**
	 * concatenation with aut
	 */
	public SFA<U, S> concatenateWith(SFA<U, S> aut, BooleanAlgebra<U, S> ba) {
		return concatenate(this, aut, ba);
	}

	/**
	 * concatenates aut1 with aut2
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SFA<A, B> concatenate(SFA<A, B> aut1, SFA<A, B> aut2,
			BooleanAlgebra<A, B> ba) {

		if (aut1.isEmpty || aut2.isEmpty)
			return getEmptySFA(ba);

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = aut1.initialState;
		Collection<Integer> finalStates = new HashSet<Integer>();

		int offSet = aut1.maxStateId + 1;

		for (SFAMove<A, B> t : aut1.getTransitions())
			transitions.add((SFAMove<A, B>) t.clone());

		for (SFAMove<A, B> t : aut2.getTransitions()) {
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			transitions.add(newMove);
		}

		for (Integer state1 : aut1.finalStates)
			transitions.add(new Epsilon<A, B>(state1, aut2.initialState
					+ offSet));

		for (Integer state : aut2.finalStates)
			finalStates.add(state + offSet);

		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * language star
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SFA<A, B> star(SFA<A, B> aut, BooleanAlgebra<A, B> ba) {

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();

		initialState = aut.maxStateId + 1;

		for (SFAMove<A, B> t : aut.getTransitions())
			transitions.add((SFAMove<A, B>) t.clone());

		// add eps transition from finalStates to initial state
		for (Integer finState : aut.finalStates)
			transitions.add(new Epsilon<A, B>(finState, initialState));

		// add eps transition from new initial state to old initial state
		transitions.add(new Epsilon<A, B>(initialState, aut.initialState));

		// The only final state is the new initial state
		finalStates.add(initialState);

		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * return the determinization of the current SFA
	 */
	public SFA<U, S> determinize(BooleanAlgebra<U, S> ba) {
		return determinize(this, ba);
	}

	/**
	 * return the determinization of aut
	 */
	public static <A, B> SFA<A, B> determinize(SFA<A, B> autUnchecked,
			BooleanAlgebra<A, B> ba) {

		if (autUnchecked.isDeterministic(ba))
			return autUnchecked;

		SFA<A, B> aut = autUnchecked;
		if (!autUnchecked.isEpsilonFree)
			aut = autUnchecked.removeEpsilonMoves(ba);

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();

		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// Add initial state
		Collection<Integer> currState = aut.getEpsClosure(aut.initialState, ba);

		initialState = 0;

		reachedStates.put(currState, 0);
		toVisitStates.add(currState);

		// Dfs to find states
		while (!toVisitStates.isEmpty()) {
			currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);

			// Check if final state
			if (aut.isFinalConfiguration(currState))
				finalStates.add(currStateId);

			ArrayList<InputMove<A, B>> movesFromCurrState = new ArrayList<InputMove<A, B>>(
					aut.getInputMovesFrom(currState));

			ArrayList<A> internalPredicates = new ArrayList<A>();
			for (InputMove<A, B> inter : movesFromCurrState)
				internalPredicates.add(inter.guard);

			for (Pair<A, ArrayList<Integer>> minterm : ba
					.GetMinterms(internalPredicates)) {

				A guard = minterm.first;
				Collection<Integer> bitList = minterm.second;
				Integer index = 0;

				Collection<Integer> toState = new HashSet<Integer>();

				for (Integer bit : bitList) {
					// use the predicate positively if i-th bit of i is 1
					if (bit == 1) {
						// get the index-th call in the list
						InputMove<A, B> bitMove = movesFromCurrState.get(index);
						toState.add(bitMove.to);
					}
					index++;
				}

				toState = aut.getEpsClosure(toState, ba);
				if (toState.size() > 0) {
					Integer toStateId = reachedStates.get(toState);
					if (toStateId == null) {
						toStateId = reachedStates.size();
						reachedStates.put(toState, toStateId);
						toVisitStates.add(toState);
					}
					transitions.add(new InputMove<A, B>(currStateId, toStateId,
							guard));
				}
			}
		}

		SFA<A, B> determinized = MkSFA(transitions, initialState, finalStates, ba, false);
		determinized.isDeterministic = true;
		return determinized;
	}

	/**
	 * checks whether the SFA is ambiguous
	 */
	public List<S> getAmbiguousInput(BooleanAlgebra<U, S> ba) {
		return getAmbiguousInput(this, ba);
	}

	/**
	 * Checks whether <code>aut</code> is ambiguous
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> List<B> getAmbiguousInput(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		SFA<A, B> aut1 = (SFA<A, B>) aut.clone();
		SFA<A, B> aut2 = (SFA<A, B>) aut.clone();

		SFA<A, B> product = new SFA<A, B>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		HashMap<Integer, Pair<Integer, Integer>> reachedRev = new HashMap<Integer, Pair<Integer, Integer>>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial states
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

			Collection<Integer> epsClo1 = aut1.getEpsClosure(currState.first,
					ba);
			Collection<Integer> epsClo2 = aut2.getEpsClosure(currState.second,
					ba);

			// Set final states
			boolean isFin = false;
			for (Integer st : epsClo1)
				if (aut1.isFinalState(st)) {
					isFin = true;
					break;
				}
			if (isFin) {
				isFin = false;
				for (Integer st : epsClo2)
					if (aut2.isFinalState(st)) {
						isFin = true;
						break;
					}
				if (isFin)
					product.finalStates.add(currStateId);
			}

			for (SFAMove<A, B> t1 : aut1.getTransitionsFrom(epsClo1))
				for (SFAMove<A, B> t2 : aut2.getTransitionsFrom(epsClo2)) {
					if (!t1.isEpsilonTransition() && !t2.isEpsilonTransition()) {
						InputMove<A, B> ct1 = (InputMove<A, B>) t1;
						InputMove<A, B> ct2 = (InputMove<A, B>) t2;
						A intersGuard = ba.MkAnd(ct1.guard, ct2.guard);
						if (ba.IsSatisfiable(intersGuard)) {

							Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
									t1.to, t2.to);
							int nextStateId = 0;

							if (!reached.containsKey(nextState)) {
								product.transitionsTo.put(totStates,
										new HashSet<SFAMove<A, B>>());

								reached.put(nextState, totStates);
								reachedRev.put(totStates, nextState);

								toVisit.add(nextState);
								product.states.add(totStates);
								nextStateId = totStates;
								totStates++;
							} else
								nextStateId = reached.get(nextState);

							InputMove<A, B> newTrans = new InputMove<A, B>(
									currStateId, nextStateId, intersGuard);

							product.addTransition(newTrans, ba, true);
						}
					}
				}
		}

		product = removeDeadOrUnreachableStates(product, ba);
		for (Integer aliveSt : product.states) {
			Pair<Integer, Integer> stP = reachedRev.get(aliveSt);
			if (stP.first != stP.second) {
				SFA<A, B> left = (SFA<A, B>) product.clone();
				SFA<A, B> right = (SFA<A, B>) product.clone();
				left.finalStates = new HashSet<Integer>();
				left.finalStates.add(aliveSt);
				right.initialState = aliveSt;

				SFA<A, B> c = left.concatenateWith(right, ba);
				SFA<A, B> clean = removeDeadOrUnreachableStates(c, ba);
				return clean.getWitness(ba);
			}
		}
		return null;
	}

	/**
	 * minimizes the automaton
	 */
	public SFA<U, S> minimize(BooleanAlgebra<U, S> ba) {
		return getMinimalOf(this, ba);
	}

	/**
	 * return the determinization of aut
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SFA<A, B> getMinimalOf(SFA<A, B> aut1,
			BooleanAlgebra<A, B> ba) {

		if (aut1.isEmpty)
			return (SFA<A, B>) aut1.clone();

		SFA<A, B> aut = aut1;

		if (!aut1.isDeterministic)
			aut = aut1.determinize(ba);

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();		

		HashSet<Integer> toSeeStates = new HashSet<Integer>(aut.states);

		HashMap<Integer, Integer> stateToClass = new HashMap<Integer, Integer>();
		ArrayList<Collection<Integer>> eqClasses = new ArrayList<Collection<Integer>>(
				aut.stateCount());

		// TODO implement Moore's algo
		// Check for equiv classes
		int classIndex = 0;
		while (!toSeeStates.isEmpty()) {
			LinkedList<Integer> toSeeCopy = new LinkedList<Integer>(toSeeStates);
			Integer currState = toSeeCopy.removeFirst();
			toSeeStates.remove(currState);

			stateToClass.put(currState, classIndex);

			HashSet<Integer> eqClass = new HashSet<Integer>();
			eqClass.add(currState);

			SFA<A, B> autCurrState = (SFA<A, B>) aut1.clone();
			autCurrState.initialState = currState;

			SFA<A, B> autOtherState = (SFA<A, B>) aut1.clone();

			// Start at 1 to avoid case in which they are all false
			for (Integer otherState : toSeeCopy) {
				autOtherState.initialState = otherState;

				if (autCurrState.isEquivalentTo(autOtherState, ba)) {
					toSeeStates.remove(otherState);
					eqClass.add(otherState);
					stateToClass.put(otherState, classIndex);
				}
			}
			eqClasses.add(classIndex, eqClass);
			classIndex++;
		}

		//Create minimal automaton from equivalence classes
		for (int i = 0; i < eqClasses.size(); i++) {
			Collection<Integer> eqClass = eqClasses.get(i);

			for (InputMove<A, B> t : aut.getInputMovesFrom(eqClass))
				transitions.add(new InputMove<A, B>(i, stateToClass.get(t.to), t.guard));				

			if (aut.isFinalConfiguration(eqClass))
				finalStates.add(i);

			if (eqClass.contains(aut.initialState))
				initialState = i;
		}

		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	// ////////////////////////////////////////////////////////////////////////////////

	// Accessory methods
	private static <A, B> SFA<A, B> removeDeadOrUnreachableStates(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		Collection<SFAMove<A, B>> transitions = new ArrayList<SFAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();	

		HashSet<Integer> initStates = new HashSet<Integer>();
		initStates.add(aut.initialState);
		Collection<Integer> reachableFromInit = aut
				.getReachableStatesFrom(initStates);
		Collection<Integer> reachingFinal = aut
				.getReachingStates(aut.finalStates);

		Collection<Integer> aliveStates = new HashSet<Integer>();
		
		for (Integer state : reachableFromInit)
			if (reachingFinal.contains(state)) {
				aliveStates.add(state);
			}

		if (aliveStates.size() == 0)
			return getEmptySFA(ba);

		for (Integer state : aliveStates)
			for (SFAMove<A, B> t : aut.getTransitionsFrom(state))
				if (aliveStates.contains(t.to))
					transitions.add(t);

		initialState = aut.initialState;

		for (Integer state : aut.finalStates)
			if (aliveStates.contains(state))
				finalStates.add(state);

		return MkSFA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * Checks whether the automaton is deterministic
	 * 
	 * @return true iff the automaton is deterministic
	 */
	public boolean isDeterministic(BooleanAlgebra<U, S> ba) {
		// Check if we set it before
		if (isDeterministic)
			return isDeterministic;

		// check only one initial state
		if (!isEpsilonFree) {
			isDeterministic = false;
			return isDeterministic;
		}

		// Check transitions out of a state are mutually exclusive
		for (Integer state : states) {
			List<SFAMove<U, S>> movesFromState = new ArrayList<SFAMove<U, S>>(
					getTransitionsFrom(state));

			for (int i = 0; i < movesFromState.size(); i++) {
				SFAMove<U, S> t1 = movesFromState.get(i);
				for (int p = i + 1; p < movesFromState.size(); p++) {
					SFAMove<U, S> t2 = movesFromState.get(p);
					if (!t1.isDisjointFrom(t2, ba)) {
						isDeterministic = false;
						return isDeterministic;
					}
				}
			}
		}

		isDeterministic = true;
		return isDeterministic;
	}

	public Collection<Integer> getReachableStatesFrom(Collection<Integer> states) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (Integer state : states)
			visitForward(state, result);
		return result;
	}

	public Collection<Integer> getReachingStates(Collection<Integer> states) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (Integer state : states)
			visitBackward(state, result);
		return result;
	}

	private void visitForward(Integer state, HashSet<Integer> reachables) {
		if (!reachables.contains(state)) {
			reachables.add(state);
			for (SFAMove<U, S> t : this.getTransitionsFrom(state)) {
				Integer nextState = t.to;
				visitForward(nextState, reachables);
			}
		}
	}

	private void visitBackward(Integer state, HashSet<Integer> reachables) {
		if (!reachables.contains(state)) {
			reachables.add(state);
			for (SFAMove<U, S> t : this.getTransitionsTo(state)) {
				Integer predState = t.from;
				visitBackward(predState, reachables);
			}
		}
	}

	// ACCESSORS

	/**
	 * Add Transition
	 */
	private void addTransition(SFAMove<U, S> transition,
			BooleanAlgebra<U, S> ba, boolean skipSatCheck) {

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

			getTransitionsFrom(transition.from).add(transition);
			getTransitionsTo(transition.to).add(transition);
		}
	}

	/**
	 * Returns the set of transitions starting at state <code>s</code>
	 */
	public Collection<SFAMove<U, S>> getTransitionsFrom(Integer state) {
		Collection<SFAMove<U, S>> trset = transitionsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SFAMove<U, S>>();
			transitionsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SFAMove<U, S>> getTransitionsTo(Integer state) {
		Collection<SFAMove<U, S>> trset = transitionsTo.get(state);
		if (trset == null) {
			trset = new HashSet<SFAMove<U, S>>();
			transitionsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SFAMove<U, S>> getTransitionsFrom(
			Collection<Integer> stateSet) {
		Collection<SFAMove<U, S>> transitions = new LinkedList<SFAMove<U, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<InputMove<U, S>> getInputMovesTo(Integer state) {
		Collection<SFAMove<U, S>> trset = transitionsTo.get(state);
		if (trset == null) {
			trset = new HashSet<SFAMove<U, S>>();
			transitionsTo.put(state, trset);
			return new LinkedList<InputMove<U, S>>();
		}
		Collection<InputMove<U, S>> out = new LinkedList<InputMove<U, S>>();
		for (SFAMove<U, S> move : trset)
			if (!move.isEpsilonTransition())
				out.add((InputMove<U, S>) move);
		return out;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<InputMove<U, S>> getInputMovesTo(
			Collection<Integer> stateSet) {
		Collection<InputMove<U, S>> transitions = new LinkedList<InputMove<U, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<InputMove<U, S>> getInputMovesFrom(Integer state) {
		Collection<SFAMove<U, S>> trset = transitionsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SFAMove<U, S>>();
			transitionsFrom.put(state, trset);
			return new LinkedList<InputMove<U, S>>();
		}
		Collection<InputMove<U, S>> out = new LinkedList<InputMove<U, S>>();
		for (SFAMove<U, S> move : trset)
			if (!move.isEpsilonTransition())
				out.add((InputMove<U, S>) move);
		return out;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<InputMove<U, S>> getInputMovesFrom(
			Collection<Integer> stateSet) {
		Collection<InputMove<U, S>> transitions = new LinkedList<InputMove<U, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getInputMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to a set of states
	 */
	public Collection<SFAMove<U, S>> getTransitionsTo(
			Collection<Integer> stateSet) {
		Collection<SFAMove<U, S>> transitions = new LinkedList<SFAMove<U, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SFAMove<U, S>> getTransitions() {
		Collection<SFAMove<U, S>> transitions = new LinkedList<SFAMove<U, S>>();
		for (Integer state : states)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	@Override
	public Collection<Move<U, S>> getMovesFrom(Integer state) {
		Collection<Move<U, S>> transitions = new LinkedList<Move<U, S>>();
		transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	@Override
	public Collection<Move<U, S>> getMovesTo(Integer state) {
		Collection<Move<U, S>> transitions = new LinkedList<Move<U, S>>();
		transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	@Override
	public Collection<Integer> getFinalStates() {
		return finalStates;
	}

	@Override
	public Collection<Integer> getInitialStates() {
		HashSet<Integer> initialStates = new HashSet<Integer>();
		initialStates.add(initialState);
		return initialStates;
	}

	@Override
	public Collection<Integer> getStates() {
		return states;
	}

	@Override
	public Object clone() {
		SFA<U, S> cl = new SFA<U, S>();

		cl.isDeterministic = isDeterministic;
		cl.isTotal = isTotal;
		cl.isEmpty = isEmpty;
		cl.isEpsilonFree = isEpsilonFree;

		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialState = initialState;
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.transitionsFrom = new HashMap<Integer, Collection<SFAMove<U, S>>>(
				transitionsFrom);
		cl.transitionsTo = new HashMap<Integer, Collection<SFAMove<U, S>>>(
				transitionsTo);

		return cl;
	}

}

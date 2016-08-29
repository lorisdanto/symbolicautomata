/**
 * 
 */
package automata.svpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.sat4j.specs.TimeoutException;

import automata.AutomataException;
import theory.BooleanAlgebra;
import utilities.BitVecUtil;
import utilities.Pair;

public class SVPA<U, S> extends VPAutomaton<U, S> {

	// Constants
	/**
	 * Returns the empty SVPA for the boolean algebra <code>ba</code>
	 */
	public static <A, B> SVPA<A, B> getEmptySVPA(BooleanAlgebra<A, B> ba) {
		SVPA<A, B> aut = new SVPA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>();
		aut.initialStates = new HashSet<Integer>(aut.states);
		aut.isDeterministic = true;
		aut.isEpsilonFree = true;
		aut.isEmpty = true;
		aut.isTotal = true;
		aut.stateCount = 1;
		aut.maxStateId = 0;
		aut.maxStackStateId = 0;
		return aut;
	}

	/**
	 * Returns the SVPA accepting every list for the boolean algebra
	 * <code>ba</code>
	 */
	public static <A, B> SVPA<A, B> getFullSVPA(BooleanAlgebra<A, B> ba) {
		SVPA<A, B> aut = new SVPA<A, B>();
		aut.states = new HashSet<Integer>();
		aut.states.add(0);
		aut.finalStates = new HashSet<Integer>(aut.states);
		aut.initialStates = new HashSet<Integer>(aut.states);
		aut.isDeterministic = true;
		aut.isEmpty = false;
		aut.isEpsilonFree = true;
		aut.stateCount = 1;
		aut.maxStateId = 0;
		aut.maxStackStateId = 0;
		aut.isTotal = true;
		try {
			aut.addTransition(new Internal<A, B>(0, 0, ba.True()), ba, true);	
			aut.addTransition(new Call<A, B>(0, 0, 0, ba.True()), ba, true);
			aut.addTransition(new Return<A, B>(0, 0, 0, ba.True()), ba, true);
			aut.addTransition(new ReturnBS<A, B>(0, 0, ba.True()), ba, true);		
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aut;
	}

	protected Collection<Integer> states;
	protected Collection<Integer> stackStates;

	protected Collection<Integer> initialStates;
	protected Collection<Integer> finalStates;

	protected Map<Integer, Collection<SVPAEpsilon<U, S>>> epsilonsFrom;
	protected Map<Integer, Collection<Internal<U, S>>> internalsFrom;
	protected Map<Integer, Collection<Call<U, S>>> callsFrom;
	protected Map<Pair<Integer, Integer>, Collection<Return<U, S>>> returnsFrom;
	protected Map<Integer, Collection<ReturnBS<U, S>>> returnBSFrom;

	protected Map<Integer, Collection<SVPAEpsilon<U, S>>> epsilonsTo;
	protected Map<Integer, Collection<Internal<U, S>>> internalsTo;
	protected Map<Integer, Collection<Call<U, S>>> callsTo;
	protected Map<Pair<Integer, Integer>, Collection<Return<U, S>>> returnsTo;
	protected Map<Integer, Collection<ReturnBS<U, S>>> returnBSTo;

	public Integer stateCount;
	public Integer maxStateId;
	public Integer maxStackStateId;
	public Integer transitionCount;

	protected SVPA() {
		super();

		initialStates = new HashSet<Integer>();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
		stackStates = new HashSet<Integer>();

		epsilonsFrom = new HashMap<Integer, Collection<SVPAEpsilon<U, S>>>();
		internalsFrom = new HashMap<Integer, Collection<Internal<U, S>>>();
		callsFrom = new HashMap<Integer, Collection<Call<U, S>>>();
		returnsFrom = new HashMap<Pair<Integer, Integer>, Collection<Return<U, S>>>();
		returnBSFrom = new HashMap<Integer, Collection<ReturnBS<U, S>>>();

		epsilonsTo = new HashMap<Integer, Collection<SVPAEpsilon<U, S>>>();
		internalsTo = new HashMap<Integer, Collection<Internal<U, S>>>();
		callsTo = new HashMap<Integer, Collection<Call<U, S>>>();
		returnsTo = new HashMap<Pair<Integer, Integer>, Collection<Return<U, S>>>();
		returnBSTo = new HashMap<Integer, Collection<ReturnBS<U, S>>>();

		stateCount = 0;
		maxStateId = 0;
		maxStackStateId = 0;
		transitionCount = 0;
	}

	/*
	 * Create an automaton (removes unreachable states)
	 */
	public static <T, S1> SVPA<T, S1> MkSVPA(
			Collection<SVPAMove<T, S1>> transitions,
			Collection<Integer> initialStates, Collection<Integer> finalStates,
			BooleanAlgebra<T, S1> ba) throws AutomataException, TimeoutException {

		// Sanity checks
		if (initialStates.size() == 0)
			throw new AutomataException("No initial states");

		SVPA<T, S1> aut = new SVPA<T, S1>();

		aut.states = new HashSet<Integer>(initialStates);
		aut.states.addAll(finalStates);

		aut.initialStates = initialStates;
		aut.finalStates = finalStates;

		for (SVPAMove<T, S1> t : transitions)
			aut.addTransition(t, ba, false);

		// remove unreachable states
		return removeUnreachableStates(aut, ba);
	}

	public boolean accepts(List<TaggedSymbol<S>> input, BooleanAlgebra<U, S> ba) throws TimeoutException {

		Collection<Pair<Integer, Stack<Pair<Integer, S>>>> currConf = new HashSet<Pair<Integer, Stack<Pair<Integer, S>>>>();

		for (Integer state : initialStates)
			currConf.add(new Pair<Integer, Stack<Pair<Integer, S>>>(state,
					new Stack<Pair<Integer, S>>()));

		currConf = getConfigurationEpsClosure(currConf, ba);
		for (TaggedSymbol<S> el : input) {
			currConf = getNextState(currConf, el, ba);
			currConf = getConfigurationEpsClosure(currConf, ba);
			if (currConf.isEmpty())
				return false;
		}

		for (Pair<Integer, Stack<Pair<Integer, S>>> state : currConf)
			if (isFinalState(state.first))
				return true;

		return false;
	}

	private Collection<Pair<Integer, Stack<Pair<Integer, S>>>> getConfigurationEpsClosure(
			Collection<Pair<Integer, Stack<Pair<Integer, S>>>> currConf,
			BooleanAlgebra<U, S> ba) {

		Collection<Pair<Integer, Stack<Pair<Integer, S>>>> currConfEps = currConf;
		LinkedList<Pair<Integer, Stack<Pair<Integer, S>>>> toVisit = new LinkedList<Pair<Integer, Stack<Pair<Integer, S>>>>(
				currConf);

		while (toVisit.size() > 0) {
			Pair<Integer, Stack<Pair<Integer, S>>> visState = toVisit.remove();

			for (SVPAEpsilon<U, S> t : getEpsilonsFrom(visState.first)) {
				Pair<Integer, Stack<Pair<Integer, S>>> newEl = new Pair<Integer, Stack<Pair<Integer, S>>>(
						t.to, visState.second);
				if (!currConfEps.contains(newEl)) {
					currConfEps.add(newEl);
					toVisit.add(newEl);
				}
			}
		}
		return currConfEps;
	}

	private Collection<Integer> getEpsClosure(Integer state,
			BooleanAlgebra<U, S> ba) {

		HashSet<Integer> st = new HashSet<Integer>();
		st.add(state);
		return getEpsClosure(st, ba);
	}

	private Collection<Integer> getEpsClosure(Collection<Integer> fronteer,
			BooleanAlgebra<U, S> ba) {

		Collection<Integer> reached = new HashSet<Integer>(fronteer);
		LinkedList<Integer> toVisit = new LinkedList<Integer>(fronteer);

		while (toVisit.size() > 0) {
			for (SVPAEpsilon<U, S> t : getEpsilonsFrom(toVisit.removeFirst())) {
				if (!reached.contains(t.to)) {
					reached.add(t.to);
					toVisit.add(t.to);
				}
			}
		}
		return reached;
	}

	public Collection<Pair<Integer, Stack<Pair<Integer, S>>>> getNextState(
			Collection<Pair<Integer, Stack<Pair<Integer, S>>>> currConf,
			TaggedSymbol<S> input, BooleanAlgebra<U, S> ba) throws TimeoutException {

		Collection<Pair<Integer, Stack<Pair<Integer, S>>>> nextState = new HashSet<Pair<Integer, Stack<Pair<Integer, S>>>>();

		for (Pair<Integer, Stack<Pair<Integer, S>>> conf : currConf) {

			Pair<Integer, Stack<Pair<Integer, S>>> newState = null;
			for (SVPAMove<U, S> t : getTransitionsFrom(conf)) {
				newState = t.getNextState(conf, input, ba);
				if (newState != null)
					nextState.add(newState);
			}
		}
		return nextState;
	}

	/**
	 * return an equivalent copy without epsilon moves
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> removeEpsilonMoves(BooleanAlgebra<U, S> ba) throws TimeoutException {
		return removeEpsilonMovesFrom(this, ba);
	}

	/**
	 * return an equivalent copy without epsilon moves
	 * @throws TimeoutException 
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SVPA<A, B> removeEpsilonMovesFrom(SVPA<A, B> aut,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		if (aut.isEpsilonFree)
			return (SVPA<A, B>) aut.clone();

		SVPA<A, B> epsFree = new SVPA<A, B>();

		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// Add initial states
		for (Integer st1 : aut.initialStates) {
			Collection<Integer> p = aut.getEpsClosure(st1, ba);
			int nextId = reachedStates.size();

			epsFree.initialStates.add(nextId);
			epsFree.states.add(nextId);

			reachedStates.put(p, nextId);
			toVisitStates.add(p);
		}

		while (!toVisitStates.isEmpty()) {
			Collection<Integer> currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);

			if (aut.isFinalConfiguration(currState))
				epsFree.finalStates.add(currStateId);

			for (SVPAMove<A, B> t1 : aut.getTransitionsFrom(currState)) {
				if (t1.type != Tag.Epsilon) {

					Collection<Integer> nextState = aut
							.getEpsClosure(t1.to, ba);

					Integer nextStateId = reachedStates.get(nextState);

					if (nextStateId == null) {
						int index = reachedStates.size();
						reachedStates.put(nextState, index);
						toVisitStates.add(nextState);
						nextStateId = index;
					}

					SVPAMove<A, B> tnew = (SVPAMove<A, B>) t1.clone();
					tnew.from = currStateId;
					tnew.to = nextStateId;

					epsFree.addTransition(tnew, ba, true);
				}
			}
		}

		return removeUnreachableStates(epsFree, ba);
	}

	/**
	 * Computes the intersection with <code>aut</code> as a new SVPA
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> intersectionWith(SVPA<U, S> aut, BooleanAlgebra<U, S> ba) throws TimeoutException {
		return intersection(this, aut, ba);
	}

	/**
	 * Computes the intersection with <code>aut</code> as a new SVPA
	 * @throws TimeoutException 
	 */
	public static <A, B> SVPA<A, B> intersection(SVPA<A, B> aut1,
			SVPA<A, B> aut2, BooleanAlgebra<A, B> ba) throws TimeoutException {

		SVPA<A, B> inters = new SVPA<A, B>();

		Map<Pair<Integer, Integer>, Integer> reachedStackStates = new HashMap<Pair<Integer, Integer>, Integer>();

		Map<Pair<Integer, Integer>, Integer> reachedStates = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisitStates = new LinkedList<Pair<Integer, Integer>>();

		// Add initial states
		for (Integer st1 : aut1.initialStates)
			for (Integer st2 : aut2.initialStates) {
				Pair<Integer, Integer> p = new Pair<Integer, Integer>(st1, st2);
				int nextId = reachedStates.size();

				inters.initialStates.add(nextId);
				inters.states.add(nextId);

				reachedStates.put(p, nextId);
				toVisitStates.add(p);
			}

		// DFS to discover next states and transitions
		while (!toVisitStates.isEmpty()) {
			Pair<Integer, Integer> currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);

			// Consider epsilon closure
			Collection<Integer> epsClose1 = aut1.getEpsClosure(currState.first,
					ba);
			Collection<Integer> epsClose2 = aut2.getEpsClosure(
					currState.second, ba);

			boolean isFin = false;
			for (Integer st : epsClose1)
				if (aut1.isFinalState(st)) {
					isFin = true;
					break;
				}
			if (isFin) {
				isFin = false;
				for (Integer st : epsClose2)
					if (aut2.isFinalState(st)) {
						isFin = true;
						break;
					}
				if (isFin)
					inters.finalStates.add(currStateId);
			}

			for (Return<A, B> t1 : aut1.getReturnsFrom(epsClose1))
				for (Return<A, B> t2 : aut2.getReturnsFrom(epsClose2)) {
					A intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {

						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
								t1.to, t2.to);
						Pair<Integer, Integer> nextStackState = new Pair<Integer, Integer>(
								t1.stackState, t2.stackState);

						int nextStateId = addStateBinOpHelper(inters,
								reachedStates, toVisitStates, nextState, 0);
						int nextStackStateId = addStackStateBinOpHelper(inters,
								reachedStackStates, nextStackState, 0);

						Return<A, B> newTrans = new Return<A, B>(currStateId,
								nextStateId, nextStackStateId, intersGuard);

						inters.addTransition(newTrans, ba, true);
					}
				}

			for (Call<A, B> t1 : aut1.getCallsFrom(epsClose1))
				for (Call<A, B> t2 : aut2.getCallsFrom(epsClose2)) {
					A intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {

						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
								t1.to, t2.to);
						Pair<Integer, Integer> nextStackState = new Pair<Integer, Integer>(
								t1.stackState, t2.stackState);

						int nextStateId = addStateBinOpHelper(inters,
								reachedStates, toVisitStates, nextState, 0);
						int nextStackStateId = addStackStateBinOpHelper(inters,
								reachedStackStates, nextStackState, 0);

						Call<A, B> newTrans = new Call<A, B>(currStateId,
								nextStateId, nextStackStateId, intersGuard);

						inters.addTransition(newTrans, ba, true);
					}
				}

			for (ReturnBS<A, B> t1 : aut1.getReturnBSFrom(epsClose1))
				for (ReturnBS<A, B> t2 : aut2.getReturnBSFrom(epsClose2)) {
					A intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {
						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
								t1.to, t2.to);
						int nextStateId = addStateBinOpHelper(inters,
								reachedStates, toVisitStates, nextState, 0);
						ReturnBS<A, B> newTrans = new ReturnBS<A, B>(
								currStateId, nextStateId, intersGuard);

						inters.addTransition(newTrans, ba, true);
					}
				}

			for (Internal<A, B> t1 : aut1.getInternalsFrom(epsClose1))
				for (Internal<A, B> t2 : aut2.getInternalsFrom(epsClose2)) {
					A intersGuard = ba.MkAnd(t1.guard, t2.guard);
					if (ba.IsSatisfiable(intersGuard)) {
						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(
								t1.to, t2.to);
						int nextStateId = addStateBinOpHelper(inters,
								reachedStates, toVisitStates, nextState, 0);
						Internal<A, B> newTrans = new Internal<A, B>(
								currStateId, nextStateId, intersGuard);
						inters.addTransition(newTrans, ba, true);
					}
				}

		}

		return removeUnreachableStates(inters, ba);
	}

	private static <A, B> int addStateBinOpHelper(SVPA<A, B> binAut,
			Map<Pair<Integer, Integer>, Integer> reachedStates,
			LinkedList<Pair<Integer, Integer>> toVisitStates,
			Pair<Integer, Integer> state, int offSet) {
		if (!reachedStates.containsKey(state)) {
			int index = reachedStates.size() + offSet;
			reachedStates.put(state, index);
			toVisitStates.add(state);
			binAut.states.add(index);
			return index;
		} else {
			return reachedStates.get(state);
		}
	}

	private static <A, B> int addStackStateBinOpHelper(SVPA<A, B> binAut,
			Map<Pair<Integer, Integer>, Integer> reachedStates,
			Pair<Integer, Integer> stackState, int offSet) {
		if (!reachedStates.containsKey(stackState)) {
			int index = reachedStates.size() + offSet;
			reachedStates.put(stackState, index);
			binAut.stackStates.add(index);
			for (Integer state : binAut.states) {
				binAut.returnsFrom.put(
						new Pair<Integer, Integer>(state, index),
						new HashSet<Return<A, B>>());
				binAut.returnsTo.put(new Pair<Integer, Integer>(state, index),
						new HashSet<Return<A, B>>());
			}
			return index;
		} else {
			return reachedStates.get(stackState);
		}
	}

	/**
	 * Computes the union with <code>aut</code> as a new SVPA
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> unionWith(SVPA<U, S> aut1, BooleanAlgebra<U, S> ba) throws TimeoutException {
		return union(this, aut1, ba);
	}

	/**
	 * Computes the union with <code>aut</code> as a new SVPA
	 * @throws TimeoutException 
	 */
	public static <A, B> SVPA<A, B> union(SVPA<A, B> aut1, SVPA<A, B> aut2,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		if (aut1.isEmpty && aut2.isEmpty)
			return getEmptySVPA(ba);

		SVPA<A, B> union = new SVPA<A, B>();
		union.isEmpty = false;

		int offSet = aut1.maxStateId + 2;
		union.maxStateId = aut2.maxStateId + offSet;

		for (Integer state : aut1.states)
			union.states.add(state + 1);

		for (Integer state : aut2.states)
			union.states.add(state + offSet);

		union.initialStates.add(0);

		for (SVPAMove<A, B> t : aut1.getTransitions()) {
			@SuppressWarnings("unchecked")
			SVPAMove<A, B> newMove = (SVPAMove<A, B>) t.clone();
			newMove.from++;
			newMove.to++;
			union.addTransition(newMove, ba, true);
		}

		for (SVPAMove<A, B> t : aut2.getTransitions()) {
			@SuppressWarnings("unchecked")
			SVPAMove<A, B> newMove = (SVPAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			union.addTransition(newMove, ba, true);
		}

		for (Integer state : aut1.initialStates)
			union.addTransition(new SVPAEpsilon<A, B>(0, state + 1), ba, true);

		for (Integer state : aut2.initialStates)
			union.addTransition(new SVPAEpsilon<A, B>(0, state + offSet), ba, true);

		for (Integer state : aut1.finalStates)
			union.finalStates.add(state + 1);

		for (Integer state : aut2.finalStates)
			union.finalStates.add(state + offSet);

		return union;
	}

	/**
	 * Computes <code>this</code> minus <code>aut2</code>
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> minus(SVPA<U, S> aut, BooleanAlgebra<U, S> ba) throws TimeoutException {
		return differnce(this, aut, ba);
	}

	/**
	 * Computes <code>aut1</code> minus <code>aut2</code>
	 * @throws TimeoutException 
	 */
	public static <A, B> SVPA<A, B> differnce(SVPA<A, B> aut1, SVPA<A, B> aut2,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		SVPA<A, B> diff = aut1.intersectionWith(aut2.complement(ba), ba);
		return removeUnreachableStates(diff, ba);
	}

	/**
	 * return the complement of the current SVPA
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> complement(BooleanAlgebra<U, S> ba) throws TimeoutException {
		return complementOf(this, ba);
	}

	/**
	 * return the complement of the current SVPA
	 * @throws TimeoutException 
	 */
	public static <A, B> SVPA<A, B> complementOf(SVPA<A, B> aut,
			BooleanAlgebra<A, B> ba) throws TimeoutException {
		SVPA<A, B> comp = aut.mkTotal(ba);

		Collection<Integer> finStateCopy = new HashSet<Integer>(
				comp.finalStates);
		comp.finalStates = new HashSet<Integer>();

		for (Integer st : comp.states)
			if (!finStateCopy.contains(st))
				comp.finalStates.add(st);

		return comp;
	}

	/**
	 * return the total version of the current SVPA
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> mkTotal(BooleanAlgebra<U, S> ba) throws TimeoutException {
		return mkTotal(this, ba);
	}

	/**
	 * return the total version of aut
	 * @throws TimeoutException 
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SVPA<A, B> mkTotal(SVPA<A, B> aut,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		if (aut.isTotal)
			return (SVPA<A, B>) aut.clone();

		SVPA<A, B> svpa = aut;
		if (!aut.isDeterministic(ba))
			svpa = determinize(aut, ba);

		SVPA<A, B> total = new SVPA<A, B>();
		total.initialStates = new HashSet<Integer>(svpa.initialStates);
		total.finalStates = new HashSet<Integer>(svpa.finalStates);

		int newState = svpa.maxStateId + 1;
		for (Integer state : svpa.states) {
			// INTERNAL MOVES
			A totGuard = null;
			for (Internal<A, B> move : svpa.getInternalsFrom(state)) {
				total.addTransition(move, ba, true);
				if (totGuard == null)
					totGuard = ba.MkNot(move.guard);
				else
					totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
			}
			if (totGuard != null)
				total.addTransition(new Internal<A, B>(state, newState,
						totGuard), ba, false);
			else
				total.addTransition(
						new Internal<A, B>(state, newState, ba.True()), ba,
						false);

			// RETURNBS MOVES
			totGuard = null;
			for (ReturnBS<A, B> move : svpa.getReturnBSFrom(state)) {
				total.addTransition(move, ba, true);
				if (totGuard == null)
					totGuard = ba.MkNot(move.guard);
				else
					totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
			}
			if (totGuard != null)
				total.addTransition(new ReturnBS<A, B>(state, newState,
						totGuard), ba, false);
			else
				total.addTransition(
						new ReturnBS<A, B>(state, newState, ba.True()), ba,
						false);

			// CALL MOVES
			totGuard = null;
			for (Call<A, B> move : svpa.getCallsFrom(state)) {
				total.addTransition(move, ba, true);
				if (totGuard == null)
					totGuard = ba.MkNot(move.guard);
				else
					totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
			}
			if (totGuard != null)
				total.addTransition(
						new Call<A, B>(state, newState, 0, totGuard), ba, false);
			else
				total.addTransition(
						new Call<A, B>(state, newState, 0, ba.True()), ba,
						false);

			// RETURNS MOVES
			for (Integer stackState : svpa.stackStates) {
				totGuard = null;
				for (Return<A, B> move : svpa.getReturnsFrom(state, stackState)) {
					total.addTransition(move, ba, true);
					if (totGuard == null)
						totGuard = ba.MkNot(move.guard);
					else
						totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
				}
				if (totGuard != null)
					total.addTransition(new Return<A, B>(state, newState,
							stackState, totGuard), ba, false);
				else
					total.addTransition(new Return<A, B>(state, newState,
							stackState, ba.True()), ba, false);
			}
		}
		if (total.states.contains(newState)) {
			total.addTransition(
					new Internal<A, B>(newState, newState, ba.True()), ba, true);
			total.addTransition(
					new ReturnBS<A, B>(newState, newState, ba.True()), ba, true);
			total.addTransition(
					new Call<A, B>(newState, newState, 0, ba.True()), ba, true);
			for (Integer stSt : total.stackStates) {
				total.addTransition(new Return<A, B>(newState, newState, stSt,
						ba.True()), ba, true);
			}
		}

		total.isTotal = true;
		total.stateCount = total.states.size();
		return total;
	}

	/**
	 * return the complement of the current SVPA
	 * @throws TimeoutException 
	 */
	public boolean isEquivalentTo(SVPA<U, S> aut, BooleanAlgebra<U, S> ba) throws TimeoutException {
		return areEquivalent(this, aut, ba);
	}

	/**
	 * return the complement of the current SVPA
	 * @throws TimeoutException 
	 */
	public static <A, B> boolean areEquivalent(SVPA<A, B> aut1,
			SVPA<A, B> aut2, BooleanAlgebra<A, B> ba) throws TimeoutException {
		if (!differnce(aut1, aut2, ba).isEmpty)
			return false;
		return differnce(aut2, aut1, ba).isEmpty;
	}

	/**
	 * return the determinization of the current SVPA
	 * @throws TimeoutException 
	 */
	public SVPA<U, S> determinize(BooleanAlgebra<U, S> ba) throws TimeoutException {
		return determinize(this, ba);
	}

	/**
	 * return the determinization of aut
	 * @throws TimeoutException 
	 */
	public static <A, B> SVPA<A, B> determinize(SVPA<A, B> aut1,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		// Remove epsilon before starting		
		SVPA<A, B> aut = aut1;
		if (!aut1.isEpsilonFree)
			aut = removeEpsilonMovesFrom(aut1, ba);
		
		if (aut1.isDeterministic(ba))
			return aut1;
		
		SVPA<A, B> deter = new SVPA<A, B>();

		HashMap<Collection<Pair<Integer, Integer>>, Integer> reachedStates = new HashMap<Collection<Pair<Integer, Integer>>, Integer>();
		LinkedList<Collection<Pair<Integer, Integer>>> toVisitStates = new LinkedList<Collection<Pair<Integer, Integer>>>();

		HashMap<Pair<Collection<Pair<Integer, Integer>>, A>, Integer> reachedStackStates = new HashMap<Pair<Collection<Pair<Integer, Integer>>, A>, Integer>();

		// Add initial state
		Collection<Pair<Integer, Integer>> currState = new HashSet<Pair<Integer, Integer>>();
		for (Integer st : aut.initialStates)
			currState.add(new Pair<Integer, Integer>(st, st));
		deter.initialStates.add(0);
		deter.states.add(0);

		reachedStates.put(currState, 0);
		toVisitStates.add(currState);

		Collection<Collection<Pair<Integer, Integer>>> previouslyVisited = new HashSet<Collection<Pair<Integer, Integer>>>();

		while (toVisitStates.size() > 0) {

			// pop first state from tVisit and add to visited
			currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);
			previouslyVisited.add(currState);

			// for every stackState discovered so far, add (currState,
			// stackState) to the those to be visited for creating the returns
			Collection<Pair<Collection<Pair<Integer, Integer>>, Pair<Collection<Pair<Integer, Integer>>, A>>> toVisitPairStateStackStates = new HashSet<Pair<Collection<Pair<Integer, Integer>>, Pair<Collection<Pair<Integer, Integer>>, A>>>();
			for (Pair<Collection<Pair<Integer, Integer>>, A> stackState : reachedStackStates
					.keySet())
				toVisitPairStateStackStates
						.add(new Pair<Collection<Pair<Integer, Integer>>, Pair<Collection<Pair<Integer, Integer>>, A>>(
								currState, stackState));

			// take all the second components of the pairs in currState
			Collection<Integer> secondComp = projectSecondComponents(currState);

			// if any pair has a final state as second component the current
			// state is final
			if (aut.isFinalConfiguration(secondComp))
				deter.finalStates.add(currStateId);

			// INTERNAL out of currState
			ArrayList<Internal<A, B>> internalMovesFromCS = new ArrayList<Internal<A, B>>(
					aut.getInternalsFrom(secondComp));

			// Start at 1 to avoid case in which they are all false (we are not
			// building a total SVPA
			for (int i = 1; i < Math.pow(2, internalMovesFromCS.size()); i++) {
				A guard = null;

				Collection<Pair<Integer, Integer>> toState = new HashSet<Pair<Integer, Integer>>();

				for (int bit = 0; bit < internalMovesFromCS.size(); bit++) {
					Internal<A, B> currMove = internalMovesFromCS.get(bit);

					if (BitVecUtil.get_nth_bit(i, bit) == 1) {
						guard = intersectGuards(guard, currMove.guard, ba);

						for (Pair<Integer, Integer> pair : secondIs(currState,
								currMove.from))
							toState.add(new Pair<Integer, Integer>(pair.first,
									currMove.to));

					} else {
						guard = intersectGuards(guard,
								ba.MkNot(currMove.guard), ba);
					}
				}

				// If guard satisfiable add move to deterministic version
				if (ba.IsSatisfiable(guard)) {
					Integer toStateId = reachedStates.get(toState);
					if (toStateId == null) {
						toStateId = reachedStates.size();
						reachedStates.put(toState, toStateId);
						toVisitStates.add(toState);
					}
					deter.addTransition(new Internal<A, B>(currStateId,
							toStateId, guard), ba, true);
				}
			}

			// RETURNBS out of currState
			ArrayList<ReturnBS<A, B>> returnBSMovesFromCS = new ArrayList<ReturnBS<A, B>>(
					aut.getReturnBSFrom(secondComp));

			// Start at 1 to avoid case in which they are all false
			for (int i = 1; i < Math.pow(2, returnBSMovesFromCS.size()); i++) {
				A guard = null;

				Collection<Pair<Integer, Integer>> toState = new HashSet<Pair<Integer, Integer>>();

				for (int bit = 0; bit < returnBSMovesFromCS.size(); bit++) {
					ReturnBS<A, B> currMove = returnBSMovesFromCS.get(bit);

					if (BitVecUtil.get_nth_bit(i, bit) == 1) {
						guard = intersectGuards(guard, currMove.guard, ba);

						for (Pair<Integer, Integer> pair : secondIs(currState,
								currMove.from))
							toState.add(new Pair<Integer, Integer>(pair.first,
									currMove.to));

					} else {
						guard = intersectGuards(guard,
								ba.MkNot(currMove.guard), ba);
					}
				}

				// If guard satisfiable add move to deterministic version
				if (ba.IsSatisfiable(guard)) {
					Integer toStateId = reachedStates.get(toState);
					if (toStateId == null) {
						toStateId = reachedStates.size();
						reachedStates.put(toState, toStateId);
						toVisitStates.add(toState);
					}
					deter.addTransition(new ReturnBS<A, B>(currStateId,
							toStateId, guard), ba, true);
				}
			}

			// CALLS out of currState
			ArrayList<Call<A, B>> callsFromCurrState = new ArrayList<Call<A, B>>(
					aut.getCallsFrom(secondComp));

			// Start at 1 to avoid case in which they are all false
			for (int i = 1; i < Math.pow(2, callsFromCurrState.size()); i++) {
				A a = null;

				// delta_C(S,a)=(S',(S,a)) where
				// S'={(q'',q'') | (q,q') in S and delta_c(q',a)=q'',p}

				// Compute the guard of new move and target state
				Collection<Pair<Integer, Integer>> sPrime = new HashSet<Pair<Integer, Integer>>();
				for (int bit = 0; bit < callsFromCurrState.size(); bit++) {
					// get the ith call in the list
					Call<A, B> currMove = callsFromCurrState.get(bit);

					// use the predicate positively if i-th bit of i is 1
					if (BitVecUtil.get_nth_bit(i, bit) == 1) {
						a = intersectGuards(a, currMove.guard, ba);

						sPrime.add(new Pair<Integer, Integer>(currMove.to,
								currMove.to));
					} else
						a = intersectGuards(a, ba.MkNot(currMove.guard), ba);
				}

				// If guard satisfiable add move to deterministic version
				if (ba.IsSatisfiable(a)) {

					// Pick the state id
					Integer toStateId = reachedStates.get(sPrime);
					if (toStateId == null) {
						toStateId = reachedStates.size();
						reachedStates.put(sPrime, toStateId);
						toVisitStates.add(sPrime);
					}

					// Add stack states to be visited by returns
					Pair<Collection<Pair<Integer, Integer>>, A> currStackState = new Pair<Collection<Pair<Integer, Integer>>, A>(
							currState, a);

					Integer stackStateId = reachedStackStates
							.get(currStackState);
					if (stackStateId == null) {
						stackStateId = reachedStackStates.size();
						reachedStackStates.put(currStackState, stackStateId);

						// Add new discovered stack states to those to be
						// visited by returns
						for (Collection<Pair<Integer, Integer>> visitedState : previouslyVisited)
							toVisitPairStateStackStates
									.add(new Pair<Collection<Pair<Integer, Integer>>, Pair<Collection<Pair<Integer, Integer>>, A>>(
											visitedState, currStackState));
					}

					deter.addTransition(new Call<A, B>(currStateId, toStateId,
							stackStateId, a), ba, true);
				}
			}

			// RETURNS out of every pair in to visit PairStateStackStates
			for (Pair<Collection<Pair<Integer, Integer>>, Pair<Collection<Pair<Integer, Integer>>, A>> stPair : toVisitPairStateStackStates) {				
				
				// adding delta_r(S,(S',a),b)
				Collection<Pair<Integer, Integer>> S = stPair.first;
				Pair<Collection<Pair<Integer, Integer>>, A> stackState = stPair.second;
				Collection<Pair<Integer, Integer>> Sprime = stackState.first;
				A a = stackState.second;
				
				//These transitions are independent from currStateId
				currStateId = reachedStates.get(S);

				// Calls and returns causing the match
				// (q,q'') such that,
				// 1) (q,q') in S',
				// 2) delta_c(q',a)=(q1,p)
				// 3) (q1,q2) in S,
				// 4) delta_r(q2,b,p)=q''

				HashSet<Pair<A, Pair<Call<A, B>, Return<A, B>>>> callRetGuardTripletHS = new HashSet<Pair<A, Pair<Call<A, B>, Return<A, B>>>>();
				for (Call<A, B> call : aut
						.getCallsFrom(projectSecondComponents(Sprime))) {
					A callRetGuard = ba.MkAnd(a, call.guard);
					//if I store all the calls in the stack triplet i can avoid this satisfiability check
					if (ba.IsSatisfiable(callRetGuard))
						for (Return<A, B> ret : aut.getReturnsFrom(
								projectSecondComponents(firstIs(S, call.to)),
								call.stackState))
							if (ba.IsSatisfiable(ba.MkAnd(callRetGuard,
									ret.guard)))
								callRetGuardTripletHS
										.add(new Pair<A, Pair<Call<A, B>, Return<A, B>>>(
												ba.MkAnd(call.guard, ret.guard),
												new Pair<Call<A, B>, Return<A, B>>(
														call, ret)));
				}

				ArrayList<Pair<A, Pair<Call<A, B>, Return<A, B>>>> callRetGuardTriplet = new ArrayList<Pair<A, Pair<Call<A, B>, Return<A, B>>>>(callRetGuardTripletHS);
				
				// Start at 1 to avoid case in which they are all false
				for (int i = 1; i < Math.pow(2, callRetGuardTriplet.size()); i++) {
					A b = a;

					Collection<Pair<Integer, Integer>> toState = new HashSet<Pair<Integer, Integer>>();

					for (int bit = 0; bit < callRetGuardTriplet.size(); bit++) {
						Pair<A, Pair<Call<A, B>, Return<A, B>>> currTriplet = callRetGuardTriplet
								.get(bit);

						A callRetGuard = currTriplet.first;
						Call<A, B> currCall = currTriplet.second.first;
						Return<A, B> currRet = currTriplet.second.second;

						if (BitVecUtil.get_nth_bit(i, bit) == 1) {

							b = intersectGuards(b, callRetGuard, ba);

							for (Pair<Integer, Integer> fst : secondIs(Sprime,
									currCall.from))
								if (S.contains(new Pair<Integer, Integer>(
										currCall.to, currRet.from)))
									toState.add(new Pair<Integer, Integer>(
											fst.first, currRet.to));
						} else {
							b = intersectGuards(b, ba.MkNot(callRetGuard), ba);
						}
					}

					// If guard satisfiable add move to deterministic
					// version
					if (ba.IsSatisfiable(b)) {
						
						Integer toStateId = reachedStates.get(toState);
						if (toStateId == null) {
							toStateId = reachedStates.size();
							reachedStates.put(toState, toStateId);
							toVisitStates.add(toState);
						}

						Integer toStackStateId = reachedStackStates
								.get(stackState);

						if (toStackStateId == null)
							System.out.println("shouldn't be null");

						deter.addTransition(new Return<A, B>(currStateId,
								toStateId, toStackStateId, b), ba, true);
					}
				}

			}

		}
		
		deter.isDeterministic = true;
		return deter;
	}

	private static <A, B> A intersectGuards(A guard, A conjunct,
			BooleanAlgebra<A, B> ba) throws TimeoutException {
		if (guard == null)
			return conjunct;
		else
			return ba.MkAnd(guard, conjunct);
	}

	@SuppressWarnings("unused")
	private static Collection<Integer> projectFirstComponents(
			Collection<Pair<Integer, Integer>> state) {
		HashSet<Integer> fc = new HashSet<Integer>();
		for (Pair<Integer, Integer> st : state)
			fc.add(st.first);
		return fc;
	}

	private static Collection<Integer> projectSecondComponents(
			Collection<Pair<Integer, Integer>> state) {
		HashSet<Integer> sc = new HashSet<Integer>();
		for (Pair<Integer, Integer> st : state)
			sc.add(st.second);
		return sc;
	}

	private static Collection<Pair<Integer, Integer>> firstIs(
			Collection<Pair<Integer, Integer>> state, Integer fst) {
		HashSet<Pair<Integer, Integer>> sc = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> st : state)
			if (st.first == fst)
				sc.add(st);
		return sc;
	}

	private static Collection<Pair<Integer, Integer>> secondIs(
			Collection<Pair<Integer, Integer>> state, Integer sec) {
		HashSet<Pair<Integer, Integer>> sc = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> st : state)
			if (st.second == sec)
				sc.add(st);
		return sc;
	}

	// /////////////////////////////////////////////////////////////////////////////

	// Accessory methods

	@SuppressWarnings("unchecked")
	private static <A, B> SVPA<A, B> removeUnreachableStates(SVPA<A, B> aut,
			BooleanAlgebra<A, B> ba) throws TimeoutException {

		Map<Integer, Collection<Integer>> reachRel = aut.getReachRel(ba).fourth;

		SVPA<A, B> clean = new SVPA<A, B>();

		HashSet<Integer> fromInitial = new HashSet<Integer>();
		for (Integer state : aut.initialStates)
			fromInitial.addAll(reachRel.get(state));

		if (!aut.isFinalConfiguration(fromInitial))
			return getEmptySVPA(ba);

		// compute states
		for (Integer state : fromInitial) {
			if (clean.maxStateId < state)
				clean.maxStateId = state;

			clean.states.add(state);
			if (aut.isFinalState(state))
				clean.finalStates.add(state);
			if (aut.initialStates.contains(state))
				clean.initialStates.add(state);

		}

		// add transitions
		for (Integer state : clean.states)
			for (SVPAMove<A, B> t : aut.getTransitionsFrom(state))
				if (clean.states.contains(t.to))
					clean.addTransition((SVPAMove<A, B>) t.clone(), ba, true);
		
		clean.stateCount = clean.states.size();
		
		return clean;
	}



	



	/**
	 * Checks whether the automaton is deterministic
	 * 
	 * @return true iff the automaton is deterministic
	 * @throws TimeoutException 
	 */
	public boolean isDeterministic(BooleanAlgebra<U, S> ba) throws TimeoutException {

		// Check if we set it before
		if (isDeterministic)
			return isDeterministic;

		// check only one initial state
		if (initialStates.size() != 1)
			return false;		

		// check only one initial state
		if (!isEpsilonFree) 
			return false;		

		// Check transitions out of a state are mutually exclusive
		for (Integer state : states) {
			List<SVPAMove<U, S>> movesFromState = new ArrayList<SVPAMove<U, S>>(
					getTransitionsFrom(state));

			for (int i = 0; i < movesFromState.size(); i++) {
				SVPAMove<U, S> t1 = movesFromState.get(i);
				for (int j = i + 1; j < movesFromState.size(); j++)
					if (!t1.isDisjointFrom(movesFromState.get(j), ba))
						return false;					
			}
		}

		isDeterministic = true;
		return isDeterministic;
	}

	// Transitions accessors

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsFrom(Pair<Integer, Integer> state) {
		Collection<Return<U, S>> trset = returnsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<Return<U, S>>();
			returnsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsTo(Pair<Integer, Integer> state) {
		Collection<Return<U, S>> trset = returnsTo.get(state);
		if (trset == null) {
			trset = new HashSet<Return<U, S>>();
			returnsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsFrom(Integer state,
			Integer stackState) {
		return getReturnsFrom(new Pair<Integer, Integer>(state, stackState));
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsTo(Integer state,
			Integer stackState) {
		return getReturnsTo(new Pair<Integer, Integer>(state, stackState));
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsFrom(Integer state) {
		Collection<Return<U, S>> returns = new HashSet<Return<U, S>>();
		for (Integer ss : stackStates)
			returns.addAll(getReturnsFrom(state, ss));
		return returns;
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsTo(Integer state) {
		Collection<Return<U, S>> returns = new HashSet<Return<U, S>>();
		for (Integer ss : stackStates)
			returns.addAll(getReturnsTo(state, ss));
		return returns;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Return<U, S>> getReturnsFrom(Collection<Integer> stateSet) {
		Collection<Return<U, S>> returns = new HashSet<Return<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getReturnsFrom(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions starting in a state in
	 * <code>stateSet</code> with stack state <code>stackState</code>
	 */
	public Collection<Return<U, S>> getReturnsFrom(
			Collection<Integer> stateSet, Integer stackState) {
		Collection<Return<U, S>> output = new HashSet<Return<U, S>>();
		for (Integer st : stateSet)
			output.addAll(getReturnsFrom(st, stackState));

		return output;
	}

	/**
	 * Returns the set of return transitions to a state in <code>stateSet</code>
	 */
	public Collection<Return<U, S>> getReturnsTo(Collection<Integer> stateSet) {
		Collection<Return<U, S>> returns = new HashSet<Return<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getReturnsTo(st));
		return returns;
	}

	/**
	 * Returns the set of call transitions starting a state <code>state</code>
	 */
	public Collection<Call<U, S>> getCallsFrom(Integer state) {
		Collection<Call<U, S>> trset = callsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<Call<U, S>>();
			callsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of call transitions starting in state <code>state</code>
	 * with stack state <code>stackState</code>
	 */
	public Collection<Call<U, S>> getCallsFrom(Integer state, Integer stackState) {
		Collection<Call<U, S>> trset = callsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<Call<U, S>>();
			callsFrom.put(state, trset);
		}

		Collection<Call<U, S>> output = new HashSet<Call<U, S>>();
		for (Call<U, S> call : trset)
			if (call.stackState == stackState)
				output.add(call);
		return output;
	}

	/**
	 * Returns the set of call transitions starting in a state in
	 * <code>stateSet</code> with stack state <code>stackState</code>
	 */
	public Collection<Call<U, S>> getCallsFrom(Collection<Integer> stateSet,
			Integer stackState) {
		Collection<Call<U, S>> output = new HashSet<Call<U, S>>();
		for (Integer st : stateSet)
			output.addAll(getCallsFrom(st, stackState));

		return output;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Call<U, S>> getCallsTo(Integer state) {
		Collection<Call<U, S>> trset = callsTo.get(state);
		if (trset == null) {
			trset = new HashSet<Call<U, S>>();
			callsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Call<U, S>> getCallsFrom(Collection<Integer> stateSet) {
		Collection<Call<U, S>> returns = new HashSet<Call<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getCallsFrom(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<Call<U, S>> getCallsTo(Collection<Integer> stateSet) {
		Collection<Call<U, S>> returns = new HashSet<Call<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getCallsTo(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<ReturnBS<U, S>> getReturnBSFrom(Integer state) {
		Collection<ReturnBS<U, S>> trset = returnBSFrom.get(state);
		if (trset == null) {
			trset = new HashSet<ReturnBS<U, S>>();
			returnBSFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<ReturnBS<U, S>> getReturnBSTo(Integer state) {
		Collection<ReturnBS<U, S>> trset = returnBSTo.get(state);
		if (trset == null) {
			trset = new HashSet<ReturnBS<U, S>>();
			returnBSTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<ReturnBS<U, S>> getReturnBSFrom(
			Collection<Integer> stateSet) {
		Collection<ReturnBS<U, S>> returns = new HashSet<ReturnBS<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getReturnBSFrom(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<ReturnBS<U, S>> getReturnBSTo(Collection<Integer> stateSet) {
		Collection<ReturnBS<U, S>> returns = new HashSet<ReturnBS<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getReturnBSTo(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<SVPAEpsilon<U, S>> getEpsilonsFrom(Integer state) {
		Collection<SVPAEpsilon<U, S>> trset = epsilonsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SVPAEpsilon<U, S>>();
			epsilonsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<SVPAEpsilon<U, S>> getEpsilonsTo(Integer state) {
		Collection<SVPAEpsilon<U, S>> trset = epsilonsTo.get(state);
		if (trset == null) {
			trset = new HashSet<SVPAEpsilon<U, S>>();
			epsilonsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<SVPAEpsilon<U, S>> getEpsilonsFrom(
			Collection<Integer> stateSet) {
		Collection<SVPAEpsilon<U, S>> returns = new HashSet<SVPAEpsilon<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getEpsilonsFrom(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<SVPAEpsilon<U, S>> getEpsilonsTo(Collection<Integer> stateSet) {
		Collection<SVPAEpsilon<U, S>> returns = new HashSet<SVPAEpsilon<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getEpsilonsTo(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Internal<U, S>> getInternalsFrom(Integer state) {
		Collection<Internal<U, S>> trset = internalsFrom.get(state);
		if (trset == null) {
			trset = new HashSet<Internal<U, S>>();
			internalsFrom.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Internal<U, S>> getInternalsTo(Integer state) {
		Collection<Internal<U, S>> trset = internalsTo.get(state);
		if (trset == null) {
			trset = new HashSet<Internal<U, S>>();
			internalsTo.put(state, trset);
		}
		return trset;
	}

	/**
	 * Returns the set of return transitions starting a state <code>s</code>
	 */
	public Collection<Internal<U, S>> getInternalsFrom(
			Collection<Integer> stateSet) {
		Collection<Internal<U, S>> returns = new HashSet<Internal<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getInternalsFrom(st));
		return returns;
	}

	/**
	 * Returns the set of return transitions to state <code>s</code>
	 */
	public Collection<Internal<U, S>> getInternalsTo(
			Collection<Integer> stateSet) {
		Collection<Internal<U, S>> returns = new HashSet<Internal<U, S>>();
		for (Integer st : stateSet)
			returns.addAll(getInternalsTo(st));
		return returns;
	}

	/**
	 * Returns the set of transitions starting a state <code>s</code>
	 */
	public Collection<SVPAMove<U, S>> getTransitionsFrom(
			Pair<Integer, Stack<Pair<Integer, S>>> configuration) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();

		Integer state = configuration.first;
		Stack<Pair<Integer, S>> stack = configuration.second;

		transitions.addAll(getCallsFrom(state));
		transitions.addAll(getEpsilonsFrom(state));
		transitions.addAll(getInternalsFrom(state));
		transitions.addAll(getReturnBSFrom(state));

		if (stack.size() > 0) {
			Pair<Integer, S> stackTop = stack.peek();
			transitions.addAll(getReturnsFrom(state, stackTop.first));
		}
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitionsFrom(
			HashSet<Pair<Integer, Stack<Pair<Integer, S>>>> configurationSet) {
		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();

		for (Pair<Integer, Stack<Pair<Integer, S>>> configuration : configurationSet)
			transitions.addAll(getTransitionsFrom(configuration));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting a state <code>s</code>
	 */
	public Collection<SVPAMove<U, S>> getTransitionsTo(
			Pair<Integer, Stack<Pair<Integer, S>>> configuration) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();

		Integer state = configuration.first;
		Stack<Pair<Integer, S>> stack = configuration.second;

		transitions.addAll(getCallsTo(state));
		transitions.addAll(getEpsilonsTo(state));
		transitions.addAll(getInternalsTo(state));

		if (stack.size() > 0) {
			Pair<Integer, S> stackTop = stack.peek();
			transitions.addAll(getReturnsTo(state, stackTop.first));
		} else {
			transitions.addAll(getReturnBSTo(state));
		}
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitionsTo(
			Collection<Integer> stateSet) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsTo(state));

		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitionsTo(Integer state) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();

		transitions.addAll(getEpsilonsTo(state));
		transitions.addAll(getInternalsTo(state));
		transitions.addAll(getCallsTo(state));
		transitions.addAll(getReturnBSTo(state));
		transitions.addAll(getReturnsTo(state));

		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitionsTo(
			HashSet<Pair<Integer, Stack<Pair<Integer, S>>>> configurationSet) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();
		for (Pair<Integer, Stack<Pair<Integer, S>>> configuration : configurationSet)
			transitions.addAll(getTransitionsTo(configuration));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitionsFrom(
			Collection<Integer> stateSet) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getTransitionsFrom(state));

		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitions() {
		return getTransitionsFrom(states);
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SVPAMove<U, S>> getTransitionsFrom(Integer state) {

		Collection<SVPAMove<U, S>> transitions = new LinkedList<SVPAMove<U, S>>();

		transitions.addAll(getEpsilonsFrom(state));
		transitions.addAll(getInternalsFrom(state));
		transitions.addAll(getCallsFrom(state));
		transitions.addAll(getReturnBSFrom(state));
		transitions.addAll(getReturnsFrom(state));

		return transitions;
	}

	private boolean isFinalConfiguration(Collection<Integer> conf) {
		for (Integer state : conf)
			if (isFinalState(state))
				return true;
		return false;
	}

	private boolean isFinalState(Integer state) {
		return finalStates.contains(state);
	}

	/**
	 * Add Transition
	 * @throws TimeoutException 
	 */
	private void addTransition(SVPAMove<U, S> transition,
			BooleanAlgebra<U, S> ba, boolean skipSatCheck) throws TimeoutException {

		if (transition.isEpsilonTransition()) {
			if (transition.to == transition.from)
				return;
			isEpsilonFree = false;
		}

		if (skipSatCheck || transition.isSatisfiable(ba)) {

			transitionCount++;

			states.add(transition.from);
			if (maxStateId < transition.from)
				maxStateId = transition.from;
			states.add(transition.to);
			if (maxStateId < transition.to)
				maxStateId = transition.to;

			switch (transition.type) {
			case Call: {

				Call<U, S> ct = (Call<U, S>) transition;
				stackStates.add(ct.stackState);
				if (maxStackStateId < ct.stackState)
					maxStackStateId = ct.stackState;

				getCallsFrom(transition.from).add(ct);
				getCallsTo(transition.to).add(ct);
				break;
			}

			case Return: {
				Return<U, S> ct = (Return<U, S>) transition;
				stackStates.add(ct.stackState);
				if (maxStackStateId < ct.stackState)
					maxStackStateId = ct.stackState;

				getReturnsFrom(ct.from, ct.stackState).add(ct);
				getReturnsTo(ct.to, ct.stackState).add(ct);
				break;
			}

			case Internal: {
				Internal<U, S> ct = (Internal<U, S>) transition;
				getInternalsFrom(transition.from).add(ct);
				getInternalsTo(transition.to).add(ct);
				break;
			}
			case Epsilon: {
				SVPAEpsilon<U, S> ct = (SVPAEpsilon<U, S>) transition;
				getEpsilonsFrom(transition.from).add(ct);
				getEpsilonsTo(transition.to).add(ct);
				break;
			}
			case ReturnBS: {
				ReturnBS<U, S> ct = (ReturnBS<U, S>) transition;
				getReturnBSFrom(transition.from).add(ct);
				getReturnBSTo(transition.to).add(ct);
				break;
			}
			}
		}
	}

	@Override
	public Collection<SVPAMove<U, S>> getMovesFrom(Integer state) {
		Collection<SVPAMove<U, S>> moves = new LinkedList<SVPAMove<U, S>>();
		moves.addAll(getTransitionsFrom(state));
		return moves;
	}

	@Override
	public Collection<SVPAMove<U, S>> getMovesTo(Integer state) {
		Collection<SVPAMove<U, S>> moves = new LinkedList<SVPAMove<U, S>>();
		moves.addAll(getTransitionsTo(state));
		return moves;
	}

	@Override
	public Collection<Integer> getStates() {
		return states;
	}

	@Override
	public Collection<Integer> getInitialStates() {
		return initialStates;
	}

	@Override
	public Collection<Integer> getFinalStates() {
		return finalStates;
	}

	@Override
	public Object clone() {
		SVPA<U, S> cl = new SVPA<U, S>();

		cl.isDeterministic = isDeterministic;
		cl.isTotal = isTotal;
		cl.isEmpty = isEmpty;
		cl.isEpsilonFree = isEpsilonFree;

		cl.stateCount = stateCount;
		cl.maxStateId = maxStateId;
		cl.maxStackStateId = maxStackStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.stackStates = new HashSet<Integer>(stackStates);
		cl.initialStates = new HashSet<Integer>(initialStates);
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.internalsFrom = new HashMap<Integer, Collection<Internal<U, S>>>(
				internalsFrom);
		cl.internalsTo = new HashMap<Integer, Collection<Internal<U, S>>>(
				internalsTo);
		cl.epsilonsFrom = new HashMap<Integer, Collection<SVPAEpsilon<U, S>>>(
				epsilonsFrom);
		cl.epsilonsTo = new HashMap<Integer, Collection<SVPAEpsilon<U, S>>>(
				epsilonsTo);
		cl.callsFrom = new HashMap<Integer, Collection<Call<U, S>>>(callsFrom);
		cl.callsTo = new HashMap<Integer, Collection<Call<U, S>>>(callsTo);
		cl.returnsFrom = new HashMap<Pair<Integer, Integer>, Collection<Return<U, S>>>(
				returnsFrom);
		cl.returnsTo = new HashMap<Pair<Integer, Integer>, Collection<Return<U, S>>>(
				returnsTo);
		cl.returnBSFrom = new HashMap<Integer, Collection<ReturnBS<U, S>>>(
				returnBSFrom);
		cl.returnBSTo = new HashMap<Integer, Collection<ReturnBS<U, S>>>(
				returnBSTo);

		return cl;
	}


}

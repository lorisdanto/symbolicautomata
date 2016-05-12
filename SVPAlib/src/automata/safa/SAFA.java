/**
 * SVPAlib
 * automata.sfa
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata.safa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.sat4j.specs.TimeoutException;

import com.google.common.collect.Lists;

import automata.safa.booleanexpression.PositiveBooleanExpression;
import automata.safa.booleanexpression.PositiveBooleanExpressionFactory;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;
import utilities.Pair;
import utilities.Timers;
import utilities.UnionFindHopKarp;

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

	private static BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = null;

	private PositiveBooleanExpression initialState;
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

	public PositiveBooleanExpression getInitialState() {
		return initialState;
	}

	public Collection<Integer> getStates() {
		return states;
	}
	
	public Collection<Integer> getFinalStates() {
		return finalStates;
	}

	public static BooleanExpressionFactory<PositiveBooleanExpression> getBooleanExpressionFactory() {
		if (boolexpr == null) {
			boolexpr = new PositiveBooleanExpressionFactory();
		}
		return boolexpr;
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
	public static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions,
			PositiveBooleanExpression initialState, Collection<Integer> finalStates, BooleanAlgebra<A, B> ba) {
		return MkSAFA(transitions, initialState, finalStates, ba, true, true);
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	public static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions,
			PositiveBooleanExpression initialState, Collection<Integer> finalStates, BooleanAlgebra<A, B> ba,
			boolean normalize, boolean complete) {

		SAFA<A, B> aut = new SAFA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.addAll(initialState.getStates());
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = new HashSet<>(finalStates);

		for (SAFAInputMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

		if (complete && !normalize)
			aut = aut.complete(ba);

		if (normalize)
			return aut.normalize(ba);
		else
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

			getInputMovesFrom(transition.from).add(transition);
		}
	}

	// ------------------------------------------------------
	// Constant automata
	// ------------------------------------------------------

	/**
	 * Returns the empty SFA for the Boolean algebra <code>ba</code>
	 */
	public static <A, B> SAFA<A, B> getEmptySAFA(BooleanAlgebra<A, B> ba) {
		SAFA<A, B> aut = new SAFA<A, B>();
		BooleanExpressionFactory<PositiveBooleanExpression> bexpr = getBooleanExpressionFactory();
		aut.initialState = bexpr.False();
		return aut;
	}

	/**
	 * Returns the true SFA for the Boolean algebra <code>ba</code>
	 */
	public static <A, B> SAFA<A, B> getFullSAFA(BooleanAlgebra<A, B> ba) {
		SAFA<A, B> aut = new SAFA<A, B>();
		BooleanExpressionFactory<PositiveBooleanExpression> bexpr = getBooleanExpressionFactory();
		aut.initialState = bexpr.True();
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
		}

		return initialState.hasModel(currConf);
	}

	// /**
	// * Return a list [<g1, t1>, ..., <gn, tn>] of <guard, transition table>
	// * pairs such that: - For each i and each state s, s transitions to ti[s]
	// on
	// * reading a letter satisfying gi - {g1, ..., gn} is the set of all
	// * satisfiable conjunctions of guards on outgoing transitions leaving the
	// * input set of states
	// *
	// * @param states
	// * The states from which to compute the outgoing transitions
	// * @param ba
	// * @param guard
	// * All transitions in the list must comply with guard
	// * @return
	// */
	// private <E extends BooleanExpression> LinkedList<Pair<P, Map<Integer,
	// E>>> getTransitionTablesFrom(
	// Collection<Integer> states, BooleanAlgebra<P, S> ba, P guard,
	// BooleanExpressionFactory<E> tgt) {
	// LinkedList<Pair<P, Map<Integer, E>>> moves = new LinkedList<>();
	//
	// BooleanExpressionMorphism<E> coerce = new BooleanExpressionMorphism<>((x)
	// -> tgt.MkState(x), tgt);
	// moves.add(new Pair<P, Map<Integer, E>>(guard, new HashMap<>()));
	// for (Integer s : states) {
	// LinkedList<Pair<P, Map<Integer, E>>> moves2 = new LinkedList<>();
	// for (SAFAInputMove<P, S> t : getInputMovesFrom(s)) {
	// for (Pair<P, Map<Integer, E>> move : moves) {
	// P newGuard = ba.MkAnd(t.guard, move.getFirst());
	// if (ba.IsSatisfiable(newGuard)) {
	// Map<Integer, E> map = new HashMap<Integer, E>(move.getSecond());
	// map.put(s, coerce.apply(t.to));
	// moves2.add(new Pair<>(newGuard, map));
	// }
	// }
	// }
	// moves = moves2;
	// }
	// return moves;
	// }

	/**
	 * Checks whether the SAFA aut is empty
	 * 
	 * @throws TimeoutException
	 */
	public static <P, S, E extends BooleanExpression> boolean isEmpty(SAFA<P, S> aut, BooleanAlgebra<P, S> ba)
			throws TimeoutException {
		// TODO: the default boolean expression factory should *not* be
		// boolexpr.
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = getBooleanExpressionFactory();
		return isEquivalent(aut, getEmptySAFA(ba), ba, boolexpr).getFirst();
	}

	/**
	 * Checks whether the SAFA aut is empty
	 * 
	 * @throws TimeoutException
	 */
	public static <P, S, E extends BooleanExpression> boolean isEmpty(SAFA<P, S> aut, BooleanAlgebra<P, S> ba,
			long timeout) throws TimeoutException {
		// TODO: the default boolean expression factory should *not* be
		// boolexpr.
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = getBooleanExpressionFactory();
		return isEquivalent(aut, getEmptySAFA(ba), ba, boolexpr, timeout).getFirst();
	}

	/**
	 * Checks whether laut and raut are equivalent using bisimulation up to
	 * congruence.
	 * 
	 * @throws TimeoutException
	 */
	public static <P, S, E extends BooleanExpression> Pair<Boolean, List<S>> isEquivalent(SAFA<P, S> laut,
			SAFA<P, S> raut, BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr) throws TimeoutException {
		return isEquivalent(laut, raut, ba, boolexpr, Long.MAX_VALUE);
	}

	/**
	 * Checks whether laut and raut are equivalent using bisimulation up to
	 * congruence.
	 */
	public static <P, S, E extends BooleanExpression> Pair<Boolean, List<S>> isEquivalent(SAFA<P, S> laut,
			SAFA<P, S> raut, BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr, long timeout)
					throws TimeoutException {

		Timers.setForCongruence();
		Timers.startFull();

		SAFARelation similar = new SATRelation();

		PriorityQueue<Pair<Pair<E, E>, List<S>>> worklist = new PriorityQueue<>(new RelationComparator<>());

		BooleanExpressionMorphism<E> coerce = new BooleanExpressionMorphism<>((x) -> boolexpr.MkState(x), boolexpr);
		E leftInitial = coerce.apply(laut.initialState);
		E rightInitial = coerce.apply(raut.initialState);

		similar.add(leftInitial, rightInitial);
		worklist.add(new Pair<>(new Pair<>(leftInitial, rightInitial), new LinkedList<>()));
		while (!worklist.isEmpty()) {
			Timers.assertFullTO(timeout);

			Pair<Pair<E, E>, List<S>> next = worklist.remove();

			E left = next.getFirst().getFirst();
			E right = next.getFirst().getSecond();
			List<S> witness = next.getSecond();

			P guard = ba.True();
			boolean isSat = true;
			do {
				Timers.assertFullTO(timeout);

				Timers.startSolver();
				S model = ba.generateWitness(guard);
				Timers.stopSolver();

				P implicant = ba.True();
				Map<Integer, E> leftMove = new HashMap<>();
				Map<Integer, E> rightMove = new HashMap<>();

				for (Integer s : left.getStates()) {
					E succ = boolexpr.False();
					for (SAFAInputMove<P, S> tr : laut.getInputMovesFrom(s)) {
						Timers.assertFullTO(timeout);

						Timers.startSolver();
						boolean hm = ba.HasModel(tr.guard, model);
						Timers.stopSolver();

						if (hm) {
							Timers.startSubsumption();
							succ = boolexpr.MkOr(succ, coerce.apply(tr.to));
							Timers.stopSubsumption();

							Timers.startSolver();
							implicant = ba.MkAnd(implicant, tr.guard);
							Timers.stopSolver();
						} else {
							Timers.startSolver();
							implicant = ba.MkAnd(implicant, ba.MkNot(tr.guard));
							Timers.stopSolver();
						}
					}
					leftMove.put(s, succ);
				}

				for (Integer s : right.getStates()) {
					E succ = boolexpr.False();
					for (SAFAInputMove<P, S> tr : raut.getInputMovesFrom(s)) {
						Timers.assertFullTO(timeout);

						Timers.startSolver();
						boolean hm = ba.HasModel(tr.guard, model);
						Timers.stopSolver();

						if (hm) {
							Timers.startSubsumption();
							succ = boolexpr.MkOr(succ, coerce.apply(tr.to));
							Timers.stopSubsumption();

							Timers.startSolver();
							implicant = ba.MkAnd(implicant, tr.guard);
							Timers.stopSolver();
						} else {
							Timers.startSolver();
							implicant = ba.MkAnd(implicant, ba.MkNot(tr.guard));
							Timers.stopSolver();
						}
					}
					rightMove.put(s, succ);
				}

				Timers.startSubsumption();
				E leftSucc = boolexpr.substitute((lit) -> leftMove.get(lit)).apply(left);
				E rightSucc = boolexpr.substitute((lit) -> rightMove.get(lit)).apply(right);
				List<S> succWitness = new LinkedList<>();
				succWitness.addAll(witness);
				succWitness.add(model);
				
				boolean checkIfDiff = leftSucc.hasModel(laut.finalStates) != rightSucc.hasModel(raut.finalStates);
				Timers.stopSubsumption();

				if (checkIfDiff) {
					// leftSucc is accepting and rightSucc is rejecting or
					// vice versa
					Timers.stopFull();
					return new Pair<>(false, succWitness);
				} else{ 
					Timers.startSubsumption();
					if (!similar.isMember(leftSucc, rightSucc)) {
						if (!similar.add(leftSucc, rightSucc)) {
							Timers.stopSubsumption();
							Timers.stopFull();
							return new Pair<>(false, succWitness);
						}
						worklist.add(new Pair<>(new Pair<>(leftSucc, rightSucc), succWitness));
					}
					Timers.stopSubsumption();
				}
				Timers.startSolver();
				guard = ba.MkAnd(guard, ba.MkNot(implicant));
				
				isSat =  ba.IsSatisfiable(guard);
				Timers.stopSolver();
			} while (isSat);
		}
		Timers.stopFull();
		return new Pair<>(true, null);
	}

	static class RelationComparator<E extends BooleanExpression, A> implements Comparator<Pair<Pair<E, E>, List<A>>> {
		@Override
		public int compare(Pair<Pair<E, E>, List<A>> x, Pair<Pair<E, E>, List<A>> y) {
			Pair<E, E> xRel = x.first;
			List<A> xWitness = x.second;
			Pair<E, E> yRel = y.first;
			List<A> yWitness = y.second;

			int lsize = xRel.first.getSize() + xRel.second.getSize();
			int rsize = yRel.first.getSize() + yRel.second.getSize();
			if (lsize < rsize)
				return -1;
			if (lsize < rsize)
				return 1;
			return xWitness.size() - yWitness.size();
		}
	}

	protected Collection<Integer> getPrevState(Collection<Integer> currState, S inputElement, BooleanAlgebra<P, S> ba) {
		Collection<Integer> prevState = new HashSet<Integer>();
		for (SAFAInputMove<P, S> t : getInputMoves()) {
			BooleanExpression b = t.to;
			if (b.hasModel(currState) && ba.HasModel(t.guard, inputElement))
				prevState.add(t.from);
		}

		return prevState;
	}

	/**
	 * Checks whether laut and raut are equivalent using HopcroftKarp on the SFA
	 * accepting the reverse language
	 */
	public static <P, S> Pair<Boolean, List<S>> areReverseEquivalent(SAFA<P, S> aut1, SAFA<P, S> aut2,
			BooleanAlgebra<P, S> ba) throws TimeoutException {
		return areReverseEquivalent(aut1, aut2, ba, Long.MAX_VALUE);
	}

	/**
	 * Checks whether laut and raut are equivalent using HopcroftKarp on the SFA
	 * accepting the reverse language
	 */
	public static <P, S> Pair<Boolean, List<S>> areReverseEquivalent(SAFA<P, S> aut1, SAFA<P, S> aut2,
			BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {

		long startTime = System.currentTimeMillis();

		UnionFindHopKarp<S> ds = new UnionFindHopKarp<>();

		HashMap<HashSet<Integer>, Integer> reached1 = new HashMap<HashSet<Integer>, Integer>();
		HashMap<HashSet<Integer>, Integer> reached2 = new HashMap<HashSet<Integer>, Integer>();

		LinkedList<Pair<HashSet<Integer>, HashSet<Integer>>> toVisit = new LinkedList<>();

		HashSet<Integer> in1 = new HashSet<Integer>(aut1.finalStates);
		HashSet<Integer> in2 = new HashSet<Integer>(aut2.finalStates);

		reached1.put(in1, 0);
		reached2.put(in2, 1);
		toVisit.add(new Pair<HashSet<Integer>, HashSet<Integer>>(in1, in2));

		ds.add(0, in1.contains(aut1.initialState), new LinkedList<>());
		ds.add(1, in2.contains(aut2.initialState), new LinkedList<>());
		ds.mergeSets(0, 1);

		while (!toVisit.isEmpty()) {
			if (System.currentTimeMillis() - startTime > timeout)
				throw new TimeoutException("Timeout in the equivalence check");

			Pair<HashSet<Integer>, HashSet<Integer>> curr = toVisit.removeFirst();
			HashSet<Integer> curr1 = curr.first;
			HashSet<Integer> curr2 = curr.second;

			ArrayList<SAFAInputMove<P, S>> movesToCurr1 = new ArrayList<>();
			ArrayList<P> predicatesToCurr1 = new ArrayList<>();
			ArrayList<SAFAInputMove<P, S>> movesToCurr2 = new ArrayList<>();
			ArrayList<P> predicatesToCurr2 = new ArrayList<>();

			for (SAFAInputMove<P, S> t : aut1.getInputMoves())
				if (t.to.hasModel(curr1)) {
					movesToCurr1.add(t);
					predicatesToCurr1.add(t.guard);
				}
			for (SAFAInputMove<P, S> t : aut2.getInputMoves())
				if (t.to.hasModel(curr2)) {
					movesToCurr2.add(t);
					predicatesToCurr2.add(t.guard);
				}

			Collection<Pair<P, ArrayList<Integer>>> minterms1 = ba.GetMinterms(predicatesToCurr1, timeout);
			Collection<Pair<P, ArrayList<Integer>>> minterms2 = ba.GetMinterms(predicatesToCurr2, timeout);

			for (Pair<P, ArrayList<Integer>> minterm1 : minterms1) {
				for (Pair<P, ArrayList<Integer>> minterm2 : minterms2) {
					if (System.currentTimeMillis() - startTime > timeout)
						throw new TimeoutException("Timeout in the equivalence check");

					P conj = ba.MkAnd(minterm1.first, minterm2.first);
					if (ba.IsSatisfiable(conj)) {
						// Take from states
						HashSet<Integer> from1 = new HashSet<>();
						HashSet<Integer> from2 = new HashSet<>();
						for (int i = 0; i < minterm1.second.size(); i++)
							if (minterm1.second.get(i) == 1)
								from1.add(movesToCurr1.get(i).from);

						for (int i = 0; i < minterm2.second.size(); i++)
							if (minterm2.second.get(i) == 1)
								from2.add(movesToCurr2.get(i).from);

						List<S> pref = new LinkedList<S>(ds.getWitness(reached1.get(curr1)));
						pref.add(ba.generateWitness(conj));

						// If not in union find add them
						Integer r1 = null, r2 = null;
						if (!reached1.containsKey(from1)) {
							r1 = ds.getNumberOfElements();
							reached1.put(from1, r1);
							ds.add(r1, aut1.initialState.hasModel(from1), pref);
						}
						if (r1 == null)
							r1 = reached1.get(from1);

						if (!reached2.containsKey(from2)) {
							r2 = ds.getNumberOfElements();
							reached2.put(from2, r2);
							ds.add(r2, aut2.initialState.hasModel(from2), pref);
						}
						if (r2 == null)
							r2 = reached2.get(from2);

						// Check whether are in simulation relation
						if (!ds.areInSameSet(r1, r2)) {
							if (!ds.mergeSets(r1, r2))
								return new Pair<Boolean, List<S>>(false, Lists.reverse(pref));

							toVisit.add(new Pair<HashSet<Integer>, HashSet<Integer>>(from1, from2));
						}
					}
				}
			}
		}
		return new Pair<Boolean, List<S>>(true, null);
	}

	/**
	 * Returns true if the SAFA accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 * @throws TimeoutException
	 */
	public static <P, S> SFA<P, S> getReverseSFA(SAFA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {

		// components of new SFA
		Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		HashMap<HashSet<Integer>, Integer> reached = new HashMap<HashSet<Integer>, Integer>();
		LinkedList<HashSet<Integer>> toVisit = new LinkedList<HashSet<Integer>>();

		HashSet<Integer> init = new HashSet<>(aut.finalStates);
		reached.put(init, 0);
		toVisit.add(init);

		while (!toVisit.isEmpty()) {
			HashSet<Integer> currentState = toVisit.removeFirst();
			int currentStateID = reached.get(currentState);
			ArrayList<SAFAInputMove<P, S>> movesToCurr = new ArrayList<>();
			ArrayList<P> predicatesToCurr = new ArrayList<>();

			if (currentState.contains(aut.initialState))
				finalStates.add(currentStateID);

			for (SAFAInputMove<P, S> t : aut.getInputMoves())
				if (t.to.hasModel(currentState)) {
					movesToCurr.add(t);
					predicatesToCurr.add(t.guard);
				}

			Collection<Pair<P, ArrayList<Integer>>> minterms = ba.GetMinterms(predicatesToCurr);
			for (Pair<P, ArrayList<Integer>> minterm : minterms) {

				ArrayList<Integer> moveBits = minterm.second;
				HashSet<Integer> fromState = new HashSet<Integer>();
				for (int moveIndex = 0; moveIndex < moveBits.size(); moveIndex++)
					if (moveBits.get(moveIndex) == 1)
						fromState.add(movesToCurr.get(moveIndex).from);

				// Add new move if target state is not the empty set
				if (fromState.size() > 0) {
					int fromSt = getStateId(fromState, reached, toVisit);
					transitions.add(new SFAInputMove<P, S>(currentStateID, fromSt, minterm.first));
				}
			}
		}

		SFA<P, S> rev = SFA.MkSFA(transitions, initialState, finalStates, ba);
		rev.setIsDet(true);
		return rev;
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

	/**
	 * Computes the intersection with <code>aut</code> as a new SFA
	 */
	public SAFA<P, S> unionWith(SAFA<P, S> aut, BooleanAlgebra<P, S> ba) {
		return binaryOp(this, aut, ba, BoolOp.Union);
	}

	class DeMorgan extends BooleanExpressionFactory<PositiveBooleanExpression> {
		private BooleanExpressionFactory<PositiveBooleanExpression> boolexpr;

		public DeMorgan() {
			boolexpr = getBooleanExpressionFactory();
		}

		public PositiveBooleanExpression MkAnd(PositiveBooleanExpression p, PositiveBooleanExpression q) {
			return boolexpr.MkOr(p, q);
		}

		public PositiveBooleanExpression MkOr(PositiveBooleanExpression p, PositiveBooleanExpression q) {
			return boolexpr.MkAnd(p, q);
		}

		public PositiveBooleanExpression True() {
			return boolexpr.False();
		}

		public PositiveBooleanExpression False() {
			return boolexpr.True();
		}

		public PositiveBooleanExpression MkState(int i) {
			return boolexpr.MkState(i);
		}
	}

	/**
	 * Computes the complement of the automaton as a new SAFA. The input
	 * automaton need not be normal.
	 */
	public SAFA<P, S> negate(BooleanAlgebra<P, S> ba) {
		// DeMorganize all transitions

		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>();

		BooleanExpressionMorphism<PositiveBooleanExpression> demorganize = new BooleanExpressionMorphism<PositiveBooleanExpression>(
				(x) -> boolexpr.MkState(x), new DeMorgan());
		boolean addAccept = false; // do we need to create an accept state?
		for (int state = 0; state <= maxStateId; state++) {
			P residual = ba.True();
			if (inputMovesFrom.containsKey(state)) {
				for (SAFAInputMove<P, S> transition : inputMovesFrom.get(state)) {
					transitions.add(new SAFAInputMove<>(state, demorganize.apply(transition.to), transition.guard));
					residual = ba.MkAnd(ba.MkNot(transition.guard), residual);
				}
			}
			if (ba.IsSatisfiable(residual)) {
				transitions.add(new SAFAInputMove<>(state, boolexpr.MkState(maxStateId + 1), residual));
				addAccept = true;
			}
		}

		// Negate the set of final states
		Set<Integer> nonFinal = new HashSet<>();
		for (int state = 0; state <= maxStateId; state++) {
			if (!finalStates.contains(state)) {
				nonFinal.add(state);
			}
		}

		if (addAccept) {
			nonFinal.add(maxStateId + 1);
			transitions.add(new SAFAInputMove<>(maxStateId + 1, boolexpr.MkState(maxStateId + 1), ba.True()));
		}

		PositiveBooleanExpression notInitial = demorganize.apply(initialState);
		return MkSAFA(transitions, notInitial, nonFinal, ba, false, false);
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
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = getBooleanExpressionFactory();

		// Integer initialState = aut1.maxStateId + aut2.maxStateId + 2;
		PositiveBooleanExpression initialState = null;

		Collection<Integer> finalStates = new ArrayList<Integer>(aut1.finalStates);
		for (int state : aut2.finalStates)
			finalStates.add(state + offset);

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<A, B>> transitions = new ArrayList<SAFAInputMove<A, B>>(aut1.getInputMoves());
		for (SAFAInputMove<A, B> t : aut2.getInputMoves())
			transitions.add(new SAFAInputMove<>(t.from + offset, boolexpr.offset(offset).apply(t.to), t.guard));

		switch (op) {
		case Union:
			initialState = boolexpr.MkOr(aut1.initialState, boolexpr.offset(offset).apply(aut2.initialState));
			break;

		case Intersection:
			// Add extra moves from new initial state
			initialState = boolexpr.MkAnd(aut1.initialState, boolexpr.offset(offset).apply(aut2.initialState));
			break;

		default:
			throw new NotImplementedException("Operation " + op + " not implemented");
		}

		return MkSAFA(transitions, initialState, finalStates, ba, false, false);
	}

	/**
	 * Normalizes the SAFA by having at most one transition for each symbol out
	 * of each state
	 * 
	 * @throws TimeoutException
	 */
	public SAFA<P, S> normalize(BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = getBooleanExpressionFactory();

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>();

		boolean addedSink = false;
		int sink = maxStateId + 1;
		for (int state : states) {
			ArrayList<SAFAInputMove<P, S>> trFromState = new ArrayList<>(getInputMovesFrom(state));
			ArrayList<P> predicates = new ArrayList<>();
			for (SAFAInputMove<P, S> t : trFromState) {
				predicates.add(t.guard);
			}

			Collection<Pair<P, ArrayList<Integer>>> minterms = ba.GetMinterms(predicates);
			for (Pair<P, ArrayList<Integer>> minterm : minterms) {
				PositiveBooleanExpression newTo = null;

				for (int i = 0; i < minterm.second.size(); i++)
					if (minterm.second.get(i) == 1)
						if (newTo == null)
							newTo = trFromState.get(i).to;
						else
							newTo = boolexpr.MkOr(newTo, trFromState.get(i).to);

				if (newTo != null) {
					transitions.add(new SAFAInputMove<>(state, newTo, minterm.first));
				} else {
					transitions.add(new SAFAInputMove<>(state, boolexpr.MkState(sink), minterm.first));
					addedSink = true;
				}
			}
		}
		if (addedSink)
			transitions.add(new SAFAInputMove<>(sink, boolexpr.MkState(sink), ba.True()));

		return MkSAFA(transitions, initialState, finalStates, ba, false, false);
	}

	/**
	 * Normalizes the SAFA by having at most one transition for each symbol out
	 * of each state
	 */
	public SAFA<P, S> complete(BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = getBooleanExpressionFactory();

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>(this.getInputMoves());

		boolean addedSink = false;
		int sink = maxStateId + 1;
		for (int state : states) {
			ArrayList<SAFAInputMove<P, S>> trFromState = new ArrayList<>(getInputMovesFrom(state));
			P not = ba.True();
			for (SAFAInputMove<P, S> t : trFromState) {
				not = ba.MkAnd(not, ba.MkNot(t.guard));
			}

			if (ba.IsSatisfiable(not)) {
				transitions.add(new SAFAInputMove<>(state, boolexpr.MkState(sink), not));
				addedSink = true;
			}
		}
		if (addedSink)
			transitions.add(new SAFAInputMove<>(sink, boolexpr.MkState(sink), ba.True()));

		return MkSAFA(transitions, initialState, finalStates, ba, false, false);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s = "Automaton: " + getTransitionCount() + " transitions, " + stateCount() + " states" + "\n";
		s += "Transitions \n";
		for (SAFAInputMove<P, S> t : getInputMoves())
			s = s + t + "\n";

		s += "Initial State \n";
		s = s + initialState + "\n";

		s += "Final States \n";
		for (Integer fs : finalStates)
			s = s + fs + "\n";
		return s;
	}

	/**
	 * If <code>state<code> belongs to reached returns reached(state) otherwise
	 * add state to reached and to toVisit and return corresponding id
	 */
	public static <A, B> int getStateId(A state, Map<A, Integer> reached, LinkedList<A> toVisit) {
		if (!reached.containsKey(state)) {
			int newId = reached.size();
			reached.put(state, newId);
			toVisit.add(state);
			return newId;
		} else
			return reached.get(state);
	}

}

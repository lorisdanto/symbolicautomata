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

import org.sat4j.specs.TimeoutException;

import com.google.common.collect.Lists;

import automata.safa.booleanexpression.SumOfProducts;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
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
	public static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba) {
		return MkSAFA(transitions, initialState, finalStates, ba, true);
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	public static <A, B> SAFA<A, B> MkSAFA(Collection<SAFAInputMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, BooleanAlgebra<A, B> ba, boolean normalize) {

		SAFA<A, B> aut = new SAFA<A, B>();

		aut.states = new HashSet<Integer>();
		aut.states.add(initialState);
		aut.states.addAll(finalStates);

		aut.initialState = initialState;
		aut.finalStates = new HashSet<>(finalStates);

		for (SAFAInputMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

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

	/**
	 * Return a list [<g1, t1>, ..., <gn, tn>] of <guard, transition table>
	 * pairs such that: - For each i and each state s, s transitions to ti[s] on
	 * reading a letter satisfying gi - {g1, ..., gn} is the set of all
	 * satisfiable conjunctions of guards on outgoing transitions leaving the
	 * input set of states
	 * 
	 * @param states
	 *            The states from which to compute the outgoing transitions
	 * @param ba
	 * @param guard
	 *            All transitions in the list must comply with guard
	 * @return
	 */
	private LinkedList<Pair<P, BooleanExpression[]>> getTransitionTablesFrom(Collection<Integer> states,
			BooleanAlgebra<P, S> ba, P guard) {
		LinkedList<Pair<P, BooleanExpression[]>> moves = new LinkedList<>();
		moves.add(new Pair<>(guard, new BooleanExpression[maxStateId + 1]));
		for (Integer s : states) {
			LinkedList<Pair<P, BooleanExpression[]>> moves2 = new LinkedList<>();
			for (SAFAInputMove<P, S> t : getInputMovesFrom(s)) {
				for (Pair<P, BooleanExpression[]> move : moves) {
					P newGuard = ba.MkAnd(t.guard, move.getFirst());
					if (ba.IsSatisfiable(newGuard)) {
						BooleanExpression[] map = move.getSecond().clone();
						map[s] = t.to;
						moves2.add(new Pair<>(newGuard, map));
					}
				}
			}
			moves = moves2;
		}
		return moves;
	}

	public static <P, S> boolean isEquivalent(SAFA<P, S> laut, SAFA<P, S> raut, BooleanAlgebra<P, S> ba)
			throws TimeoutException {
		SAFARelation similar = new SATRelation();
		LinkedList<Pair<BooleanExpression, BooleanExpression>> worklist = new LinkedList<>();

		BooleanExpression leftInitial = new SumOfProducts(laut.initialState);
		BooleanExpression rightInitial = new SumOfProducts(raut.initialState);
		similar.add(leftInitial, rightInitial);
		worklist.add(new Pair<>(leftInitial, rightInitial));
		while (!worklist.isEmpty()) {
			Pair<BooleanExpression, BooleanExpression> next = worklist.removeFirst();

			BooleanExpression left = next.getFirst();
			BooleanExpression right = next.getSecond();

			LinkedList<Pair<P, BooleanExpression[]>> leftMoves = laut.getTransitionTablesFrom(left.getStates(), ba,
					ba.True());
			for (Pair<P, BooleanExpression[]> leftMove : leftMoves) {
				BooleanExpression leftSucc = left.substitute((lit) -> leftMove.getSecond()[lit]);
				boolean leftSuccAccept = leftSucc.hasModel(laut.finalStates);

				LinkedList<Pair<P, BooleanExpression[]>> rightMoves = raut.getTransitionTablesFrom(right.getStates(),
						ba, leftMove.getFirst());
				for (Pair<P, BooleanExpression[]> rightMove : rightMoves) {
					BooleanExpression rightSucc = right.substitute((lit) -> rightMove.getSecond()[lit]);
					if (leftSuccAccept != rightSucc.hasModel(raut.finalStates)) {
						// leftSucc is accepting and rightSucc is rejecting or
						// vice versa
						return false;
					} else if (!similar.isMember(leftSucc, rightSucc)) {
						similar.add(leftSucc, rightSucc);
						worklist.add(new Pair<>(leftSucc, rightSucc));
					}
				}
			}
		}
		return true;
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

	public static <P, S> boolean isReverseEquivalent(SAFA<P, S> laut, SAFA<P, S> raut, BooleanAlgebra<P, S> ba) {
		return getReverseSFA(laut,ba).isHopcroftKarpEquivalentTo(getReverseSFA(raut, ba), ba);
	}
	
	/**
	 * Returns true if the SAFA accepts the input list
	 * 
	 * @param input
	 * @param ba
	 * @return true if accepted false otherwise
	 */
	public static <P,S> SFA<P, S> getReverseSFA(SAFA<P,S> aut, BooleanAlgebra<P, S> ba) {

		// components of new SFA
		Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		HashMap<HashSet<Integer>, Integer> reached = new HashMap<HashSet<Integer>, Integer>();
		LinkedList<HashSet<Integer>> toVisit = new LinkedList<HashSet<Integer>>();

		HashSet<Integer> init = new HashSet<>(aut.finalStates);
		reached.put(init, 0);
		toVisit.add(init);

		// Explore the product automaton until no new states can be reached
		while (!toVisit.isEmpty()) {
			HashSet<Integer> currentState = toVisit.removeFirst();
			int currentStateID = reached.get(currentState);
			ArrayList<SAFAInputMove<P, S>> movesToCurr = new ArrayList<>();
			ArrayList<P> predicatesToCurr = new ArrayList<>();

			if(currentState.contains(aut.initialState))
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
					int fromSt = getStateId(fromState, reached,toVisit);
					transitions.add(new SFAInputMove<P, S>(currentStateID,
							fromSt, minterm.first));
				}				
			}
		}

		SFA<P,S> rev =  SFA.MkSFA(transitions, initialState, finalStates, ba);
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
						BooleanExpression liftedt2 = t2.to.offset(offset);
						BooleanExpression newTo = t1.to.and(liftedt2);

						transitions.add(new SAFAInputMove<A, B>(initialState, newTo, newGuard));
					}
				}
			break;

		default:
			break;
		}

		return MkSAFA(transitions, initialState, finalStates, ba, true);
	}

	/**
	 * Normalizes the SAFA by having at most one transition for each symbol out
	 * of each state
	 */
	public SAFA<P, S> normalize(BooleanAlgebra<P, S> ba) {

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>();

		for (int state : states) {
			ArrayList<SAFAInputMove<P, S>> trFromState = new ArrayList<>(getInputMovesFrom(state));
			ArrayList<P> predicates = new ArrayList<>();
			for (SAFAInputMove<P, S> t : trFromState)
				predicates.add(t.guard);

			Collection<Pair<P, ArrayList<Integer>>> minterms = ba.GetMinterms(predicates);
			for (Pair<P, ArrayList<Integer>> minterm : minterms) {
				BooleanExpression newTo = null;
				for (int i = 0; i < minterm.second.size(); i++)
					if (minterm.second.get(i) == 1)
						if (newTo == null)
							newTo = trFromState.get(i).to;
						else
							newTo = newTo.or(trFromState.get(i).to);

				if (newTo != null)
					transitions.add(new SAFAInputMove<P, S>(state, newTo, minterm.first));
			}
		}

		return MkSAFA(transitions, initialState, finalStates, ba, false);
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
	 * If <code>state<code> belongs to reached returns reached(state)
	 * otherwise add state to reached and to toVisit and return corresponding id
	 */
	public static <A, B> int getStateId(A state, Map<A, Integer> reached,
			LinkedList<A> toVisit) {
		if (!reached.containsKey(state)) {
			int newId = reached.size();
			reached.put(state, newId);
			toVisit.add(state);
			return newId;
		} else
			return reached.get(state);
	}

}

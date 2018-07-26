/**
 * SVPAlib
 * automata.sra
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sat4j.specs.TimeoutException;

import automata.Automaton;
import automata.Move;
import theory.BooleanAlgebra;
import utilities.Block;
import utilities.Pair;
import utilities.Timers;
import utilities.UnionFindHopKarp;

/**
 * Symbolic Register Automaton
 * 
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRA<P, S> extends Automaton<P, S> {
	// ------------------------------------------------------
	// Constant automata
	// ------------------------------------------------------

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
        aut.registers = new HashSet<A>();
        aut.registers.add(ba.False());
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.maxStateId = 1;
        // FIXME: We require a register operation on every transition.
        //        How are we handling an emptySRA? 
		aut.addTransition(new SRACheckMove<A, B>(0, 0, ba.True(), 0), ba, true);
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
        aut.registers = new HashSet<A>();
        aut.registers.add(ba.True());
		aut.isDeterministic = true;
		aut.isEmpty = false;
		aut.maxStateId = 1;
        // FIXME: We require a register operation on every transition.
        //        How are we handling a fullSRA?
		aut.addTransition(new SRACheckMove<A, B>(0, 0, ba.True(), 0), ba, true);
		return aut;
	}

	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

	private Integer initialState;
    private Collection<P> registers;
	private Collection<Integer> states;
	private Collection<Integer> finalStates;

	protected Map<Integer, Collection<SRAMove<P, S>>> movesFrom;
	protected Map<Integer, Collection<SRAMove<P, S>>> movesTo;
	
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
	private SRA() {
		super();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
        registers = new HashSet<P>();
		movesFrom = new HashMap<Integer, Collection<SRAMove<P, S>>>();
		movesTo = new HashMap<Integer, Collection<SRAMove<P, S>>>();
        // SRA does not accept epsilon moves.
        isEpsilonFree = true;
    	transitionCount = 0;
		maxStateId = 0;
	}

	/**
	 * Create an automaton and removes unreachable states
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, Collection<A> registers, BooleanAlgebra<A, B> ba) throws TimeoutException {
    
		return MkSRA(transitions, initialState, finalStates, registers, ba, true);
	}
	
	
	/**
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if <code>remUnreachableStates<code> is true
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, Collection<A> registers, BooleanAlgebra<A, B> ba, boolean remUnreachableStates)
					throws TimeoutException {

		return MkSRA(transitions, initialState, finalStates, registers, ba, remUnreachableStates, true);
	}

	/*
	 * Create an automaton and removes unreachable states and only removes
	 * unreachable states if remUnreachableStates is true and normalizes the
	 * automaton if normalize is true
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, Collection<A> registers, BooleanAlgebra<A, B> ba, boolean remUnreachableStates, boolean normalize)
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

		if (aut.finalStates.isEmpty())
			return getEmptySRA(ba);

		return aut;
	}

	/**
	 * Gives the option to create an automaton exactly as given by the parameters, avoiding all normalizations.
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> MkSRA(Collection<SRAMove<A, B>> transitions, Integer initialState,
			Collection<Integer> finalStates, Collection<A> registers, BooleanAlgebra<A, B> ba, boolean remUnreachableStates, boolean normalize, boolean keepEmpty)  
					throws TimeoutException{
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

			getMovesFrom(transition.from).add((SRAMove<P, S>) transition);
			getMovesTo(transition.to).add((SRAMove<P, S>) transition);
		}
	}

	// ------------------------------------------------------
	// Boolean automata operations
	// ------------------------------------------------------
    // FIXME: At the moment I doubt most if these work.
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

			// get the set of states reachable from currentState via epsilon
			// moves
			Collection<Integer> epsilonClosure1 = aut1.getEpsClosure(currentState.first, ba);
			Collection<Integer> epsilonClosure2 = aut2.getEpsClosure(currentState.second, ba);

			// if both the epsilon closures contain a final state currentStateID
			// is final
			if (aut1.isFinalConfiguration(epsilonClosure1) && aut2.isFinalConfiguration(epsilonClosure2))
				finalStates.add(currentStateID);

			// Try to pair transitions out of both automata
			for (SRAMove<A, B> ct1 : aut1.getMovesFrom(epsilonClosure1))
				for (SRAMove<A, B> ct2 : aut2.getMovesFrom(epsilonClosure2)) {

					if (System.currentTimeMillis() - startTime > timeout)
						throw new TimeoutException();

					// create conjunction of the two guards and create
					// transition only if the conjunction is satisfiable
					A intersGuard = ba.MkAnd(ct1.guard, ct2.guard);
					if (ba.IsSatisfiable(intersGuard)) {

						// Create new product transition and add it to
						// transitions
						Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(ct1.to, ct2.to);

						int nextStateId = getStateId(nextState, reached, toVisit);

						SRAMove<A, B> newTrans = new SRAMove<A, B>(currentStateID, nextStateId, intersGuard);

						transitions.add(newTrans);
					}

				}
		}

		return MkSRA(transitions, initialState, finalStates, ba);
	}

	/**
	 * Computes <code>this</code> minus <code>aut</code> as a new SRA
	 * 
	 * @throws TimeoutException
	 */
	public SRA<P, S> minus(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return difference(this, aut, ba, Long.MAX_VALUE);
	}

	/**
	 * Computes <code>this</code> minus <code>aut</code> as a new SRA
	 * 
	 * @throws TimeoutException
	 */
	public SRA<P, S> minus(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
		return difference(this, aut, ba, timeout);
	}

	/**
	 * Computes <code>aut1</code> minus <code>aut2</code> as a new SRA
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> difference(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba, long timeout)
			throws TimeoutException {
		long startTime = System.currentTimeMillis();
		SRA<A, B> compAut2 = aut2.complement(ba, timeout);
		return aut1.intersectionWith(compAut2, ba, timeout - (System.currentTimeMillis() - startTime));
	}

	/**
	 * Computes the union with <code>aut</code> as a new SRA
	 * 
	 * @throws TimeoutException
	 */
	public SRA<P, S> unionWith(SRA<P, S> aut1, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return union(this, aut1, ba);
	}

	/**
	 * Computes the union of <code>aut1</code> and <code>aut2</code> as a new
	 * SRA
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> union(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
			throws TimeoutException {

		// if both automata are empty return the empty SRA
		if (aut1.isEmpty && aut2.isEmpty)
			return getEmptySRA(ba);

		// components of new SRA
		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState;
		Collection<Integer> finalStates = new ArrayList<Integer>();

		// Offset will be add to all states of aut2
		// to ensure that the states of aut1 and aut2 are disjoint
		int offSet = aut1.maxStateId + 2;

		// Copy the moves of aut1 in transitions
		for (SRAMove<A, B> t : aut1.getTransitions()) {
			@SuppressWarnings("unchecked")
			SRAMove<A, B> newMove = (SRAMove<A, B>) t.clone();
			transitions.add(newMove);
		}

		// Copy the moves of aut2 in transitions
		// and shift the states by offset
		for (SRAMove<A, B> t : aut2.getTransitions()) {
			@SuppressWarnings("unchecked")
			SRAMove<A, B> newMove = (SRAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			transitions.add(newMove);
		}

		// the new initial state is the first available id
		initialState = aut2.maxStateId + offSet + 1;

		// Add transitions from new initial state to
		// the the initial state of aut1 and
		// the initial state of aut2 shifted by offset
		transitions.add(new SRAEpsilon<A, B>(initialState, aut1.initialState));
		transitions.add(new SRAEpsilon<A, B>(initialState, aut2.initialState + offSet));

		// Make all states of the two machines final
		finalStates.addAll(aut1.finalStates);

		// make all state of aut2 final after adding the offsett
		for (Integer state : aut2.finalStates)
			finalStates.add(state + offSet);

		return MkSRA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * @return the complement automaton as a new SRA
	 * @throws TimeoutException
	 */
	public SRA<P, S> complement(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return complementOf(this, ba, Long.MAX_VALUE);
	}

	/**
	 * @return the complement automaton as a new SRA
	 * @throws TimeoutException
	 */
	public SRA<P, S> complement(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
		return complementOf(this, ba, timeout);
	}

	/**
	 * @return the complement of <code>aut</code> as a new SRA
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> complementOf(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
			throws TimeoutException {

		// make aut total to make sure it has a sink state
		SRA<A, B> autTotal = aut.mkTotal(ba, timeout);

		// the final states of the complement are
		// autTotal.states minus autTotal.finalStates
		Collection<Integer> newFinalStates = new HashSet<Integer>();
		for (Integer st : autTotal.states)
			if (!autTotal.finalStates.contains(st))
				newFinalStates.add(st);

		return MkSRA(autTotal.getTransitions(), autTotal.initialState, newFinalStates, ba, false);
	}
	
	/** Remove epsilon transitions and collapses transitions to same state by taking the union of their predicates
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> collapseMultipleTransitions(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
			throws TimeoutException {

		// make aut total to make sure it has a sink state
		SRA<A, B> autTotal = aut.mkTotal(ba, timeout);

		Map<Pair<Integer, Integer>, A> newMovesMap = new HashMap<Pair<Integer,Integer>, A>();
		for(int state1:autTotal.states)
			for(int state2:autTotal.states)
				newMovesMap.put(new Pair<Integer, Integer>(state1, state2), ba.False());
		
		for(SRAMove<A, B> move : autTotal.getMovesFrom(autTotal.states)){
			Pair<Integer, Integer> key = new Pair<>(move.from,move.to);
			A currentPred = newMovesMap.get(key);
			newMovesMap.put(key, ba.MkOr(currentPred, move.guard));
		}
		
		Collection<SRAMove<A, B>> newMoves = new HashSet<>();
		for(Pair<Integer, Integer> key: newMovesMap.keySet())
			newMoves.add(new SRAMove<A, B>(key.first, key.second, newMovesMap.get(key)));
		
		return MkSRA(newMoves, autTotal.initialState, autTotal.finalStates, ba, false);
	}

	// ------------------------------------------------------
	// Other automata operations
	// ------------------------------------------------------
    
   	/**
	 * @return a new total equivalent total SRA (with one transition for each
	 *         symbol out of every state)
	 * @throws TimeoutException
	 */
	public SRA<P, S> mkTotal(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return mkTotal(this, ba, Long.MAX_VALUE);
	}

	/**
	 * @return a new total equivalent total SRA (with one transition for each
	 *         symbol out of every state)
	 * @throws TimeoutException
	 */
	public SRA<P, S> mkTotal(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
		return mkTotal(this, ba, timeout);
	}

	/**
	 * @return a new total total SRA (with one transition for each symbol out of
	 *         every state) equivalent to <code>aut</code>
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SRA<A, B> mkTotal(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
			throws TimeoutException {

		if (aut.isTotal) {
			return (SRA<A, B>) aut.clone();
		}

		long startTime = System.currentTimeMillis();

		SRA<A, B> SRA = aut;
		if (!aut.isDeterministic(ba))
			SRA = determinize(aut, ba, timeout);

		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState = SRA.initialState;
		Collection<Integer> finalStates = new HashSet<Integer>(SRA.finalStates);

		int sinkState = SRA.maxStateId + 1;
		boolean addSink = false;
		for (Integer state : SRA.states) {
			if (System.currentTimeMillis() - startTime > timeout)
				throw new TimeoutException();

			A totGuard = null;
			for (SRAMove<A, B> move : SRA.getMovesFrom(state)) {
				transitions.add(move);
				if (totGuard == null)
					totGuard = ba.MkNot(move.guard);
				else
					totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
			}
			// If there are not transitions out of the state set the guard to
			// the sink to true
			if (totGuard == null)
				totGuard = ba.True();
			if (ba.IsSatisfiable(totGuard)) {
				addSink = true;
				transitions.add(new SRAMove<A, B>(state, sinkState, totGuard));
			}
		}
		if (addSink)
			transitions.add(new SRAMove<A, B>(sinkState, sinkState, ba.True()));

		// Do not remove unreachable states otherwise the sink will be removed
		// again
		return MkSRA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * Checks whether the automaton accepts the same language as aut
	 * 
	 * @throws TimeoutException
	 */
	public boolean isEquivalentTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return areEquivalent(this, aut, ba);
	}
	
	/**
	 * Checks whether aut1 and aut2 accept the same language
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> Boolean areEquivalent(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
			throws TimeoutException {
		return areEquivalentPlusWitness(aut1, aut2, ba, Long.MAX_VALUE).first;
	}
	
	/**
	 * Checks whether the automaton accepts the same language as aut
	 * 
	 * @throws TimeoutException
	 */
	public Pair<Boolean, List<S>> isEquivalentPlusWitnessTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return areEquivalentPlusWitness(this, aut, ba, Long.MAX_VALUE);
	}

	/**
	 * Checks whether aut1 is equivalent to aut2 and returns a concrete witness if not. Second element is null if equivalent.
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> Pair<Boolean, List<B>> areEquivalentPlusWitness(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
			throws TimeoutException {
		return areEquivalentPlusWitness(aut1, aut2, ba, Long.MAX_VALUE);
	}

	/**
	 * Checks whether aut1 is equivalent to aut2 and returns a concrete witness if not. Second element is null if equivalent.
	 * 
	 * @throws TimeoutException
	 */
	public static <A, B> Pair<Boolean, List<B>> areEquivalentPlusWitness(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba, long timeout)
			throws TimeoutException {
		if(!aut1.isDeterministic)
			aut1 = aut1.determinize(ba);
		if(!aut2.isDeterministic)
			aut2 = aut2.determinize(ba);
		
		
		SRA<A, B> tmp1 = collapseMultipleTransitions(aut1, ba, timeout);
		SRA<A, B> tmp2 = collapseMultipleTransitions(aut2, ba, timeout);

		Pair<Boolean, List<A>> result = areEquivalentSymbolicWitness(tmp1, tmp2, ba, timeout);
		if(result.first)
			return new Pair<Boolean, List<B>>(true, null);
		
		List<B> concreteWitness = new LinkedList<>();
		for(A pred: result.second)
			concreteWitness.add(ba.generateWitness(pred));
		
		return new Pair<Boolean, List<B>>(false, concreteWitness);
	}	
	
	/**
	 * checks whether aut1 is equivalent to aut2, if not returns a symbolic
	 * sequence of predicates as a witness
	 * @assume Inputs to be deterministic SRA
	 * @throws TimeoutException
	 */
	public Pair<Boolean, List<P>> isEquivalentPlusSymoblicWitnessTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout)
			throws TimeoutException {		
		
		if(!this.isDeterministic || !aut.isDeterministic)
			throw new IllegalArgumentException("The SRAs have to be deterministic");
		
		SRA<P, S> tmp1 = collapseMultipleTransitions(this, ba, timeout);
		SRA<P, S> tmp2 = collapseMultipleTransitions(aut, ba, timeout);

		return areEquivalentSymbolicWitness(tmp1, tmp2, ba, timeout);
	}
	
	private static <A, B> Pair<Boolean, List<A>> areEquivalentSymbolicWitness(SRA<A, B> aut1, SRA<A, B> aut2,
			BooleanAlgebra<A, B> ba, long timeout) throws TimeoutException {

		long startTime = System.currentTimeMillis();

		boolean isF1=aut1.isFinalState(aut1.initialState);
		boolean isF2=aut2.isFinalState(aut2.initialState);
		if(isF1!=isF2)
			return new Pair<Boolean, List<A>>(false, new LinkedList<>());			

		Pair<Integer, Integer> initPair = new Pair<Integer, Integer>(aut1.initialState, aut2.initialState);
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<>();
		toVisit.add(initPair);
		
		HashSet<Pair<Integer, Integer>> reached = new HashSet<>();
		reached.add(initPair);
		
		HashMap<Pair<Integer, Integer>, List<A>> witnesses = new HashMap<>();
		witnesses.put(initPair, new LinkedList<>());
		
		while (!toVisit.isEmpty()) {

			if (System.currentTimeMillis() - startTime > timeout)
				throw new TimeoutException();

			Pair<Integer, Integer> curr = toVisit.removeFirst();
			List<A> currWitness = witnesses.get(curr);
			
			for (SRAMove<A, B> move1 : aut1.getMovesFrom(curr.first))
				for (SRAMove<A, B> move2 : aut2.getMovesFrom(curr.second)) {
					A conj = ba.MkAnd(move1.guard, move2.guard);
					if (ba.IsSatisfiable(conj)) {
						
						Pair<Integer, Integer> newState = new Pair<Integer, Integer>(move1.to, move2.to);
						if(!reached.contains(newState)){
							toVisit.add(newState);
							reached.add(newState);
							List<A> newWitness = new LinkedList<A>(currWitness);
							newWitness.add(conj);
							witnesses.put(newState, newWitness);	
							
							if(aut1.isFinalState(move1.to)!= aut2.isFinalState(move2.to))
								return new Pair<Boolean, List<A>>(false, newWitness);
						}
					}
				}
		}

		return new Pair<Boolean, List<A>>(true, null);
	}

	/**
	 * Checks whether the automaton accepts the same language as aut using Hopcroft-Karp algorithm
	 * @assume the two automata are deterministic
	 * @throws TimeoutException
	 */
	public boolean isHopcroftKarpEquivalentTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba)
			throws TimeoutException {
		
		return areHKEquivalentNondet(this.mkTotal(ba).normalize(ba),
				aut.mkTotal(ba).normalize(ba), ba, Long.MAX_VALUE);
	}

	/**
	 * Checks whether the automaton accepts the same language as aut using Hopcroft-Karp algorithm
	 * @assume the two automata are deterministic
	 * @throws TimeoutException
	 */
	public boolean isHopcroftKarpEquivalentTo(SRA<P, S> aut, BooleanAlgebra<P, S> ba, long timeout)
			throws TimeoutException {

		long startTime = System.currentTimeMillis();
		SRA<P, S> tmp1 = this.mkTotal(ba);
		long leftover = System.currentTimeMillis() - startTime;
		
        startTime = System.currentTimeMillis();
		tmp1 = tmp1.normalize(ba);
		leftover = leftover - (System.currentTimeMillis() - startTime);
		
        startTime = System.currentTimeMillis();
		SRA<P, S> tmp2 = aut.mkTotal(ba);
        leftover = leftover - (System.currentTimeMillis() - startTime);
		
        startTime = System.currentTimeMillis();
		tmp2 = tmp2.normalize(ba);
		leftover = leftover - (System.currentTimeMillis() - startTime);

		return areHKEquivalentNondet(tmp1, tmp2, ba, leftover);
	}
			

	/**
	 * checks whether aut1 is equivalent to aut2 using Hopcroft Karp's algorithm
	 * @assume automata are deterministic
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unused")
	private static <A, B> boolean areHopcroftKarpEquivalent(SRA<A, B> aut1, SRA<A, B> aut2,
			BooleanAlgebra<A, B> ba, long timeout) throws TimeoutException {

		Timers.setForCongruence();

		long startTime = System.currentTimeMillis();
		UnionFindHopKarp<B> ds = new UnionFindHopKarp<>();
		int offset = aut1.stateCount();

		boolean isF1=aut1.isFinalState(aut1.initialState);
		boolean isF2=aut2.isFinalState(aut2.initialState);
		if(isF1!=isF2)
			return false;
		
		ds.add(aut1.initialState, isF1);
		ds.add(aut2.initialState + offset, isF2);
		ds.mergeSets(aut1.initialState, aut2.initialState + offset);

		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<>();
		toVisit.add(new Pair<Integer, Integer>(aut1.initialState, aut2.initialState));
		while (!toVisit.isEmpty()) {
			Timers.oneMoreState();

			if (System.currentTimeMillis() - startTime > timeout)
				throw new TimeoutException();

			Pair<Integer, Integer> curr = toVisit.removeFirst();
			for (SRAMove<A, B> move1 : aut1.getMovesFrom(curr.first))
				for (SRAMove<A, B> move2 : aut2.getMovesFrom(curr.second)) {
					A conj = ba.MkAnd(move1.guard, move2.guard);
					if (ba.IsSatisfiable(conj)) {
						int r1 = move1.to;
						int r2 = move2.to + offset;

						if (!ds.contains(r1))
							ds.add(r1, aut1.isFinalState(move1.to));
						if (!ds.contains(r2))
							ds.add(r2, aut2.isFinalState(move2.to));

						if (!ds.areInSameSet(r1, r2)) {
							if (!ds.mergeSets(r1, r2))
								return false;
							toVisit.add(new Pair<Integer, Integer>(move1.to, move2.to));
						}
					}
				}
		}

		return true;
	}
	
	 /**
     * Lazy Hopcroft-Karp plus determinization 
	 * @throws TimeoutException 
    */
    public static <A, B> boolean areHKEquivalentNondet(SRA<A,B> aut1, SRA<A,B> aut2,
    		BooleanAlgebra<A, B> ba, long timeout) throws TimeoutException
    {
    	Timers.setForCongruence();
    	long startTime = System.currentTimeMillis();
    	
    	UnionFindHopKarp<B> ds = new UnionFindHopKarp<>();

    	HashMap<Integer, Integer> reached1 = new HashMap<Integer, Integer>();
    	HashMap<Integer, Integer> reached2 = new HashMap<Integer, Integer>();

    	LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

    	LinkedList<Integer> aut1States = new LinkedList<Integer>();
    	aut1States.addAll(aut1.getStates());
        //PowerSetStateBuilder dfaStateBuilderForAut1 = PowerSetStateBuilder.Create(aut1States.ToArray());
        
        LinkedList<Integer> aut2States = new LinkedList<Integer>();
    	aut2States.addAll(aut2.getStates());
        //PowerSetStateBuilder dfaStateBuilderForAut2 = PowerSetStateBuilder.Create(aut2States.ToArray());
        

    	HashMap<HashSet<Integer>, Integer> reachedStates1 = new HashMap<HashSet<Integer>, Integer>();
    	HashMap<Integer, HashSet<Integer>> idToStates1 = new HashMap<Integer, HashSet<Integer>>();
    	
    	HashMap<HashSet<Integer>, Integer> reachedStates2 = new HashMap<HashSet<Integer>, Integer>();
    	HashMap<Integer, HashSet<Integer>> idToStates2 = new HashMap<Integer, HashSet<Integer>>();
    	
    	HashSet<Integer> detInitialState1 = new HashSet<Integer>();
		detInitialState1.add(aut1.getInitialState());
		reachedStates1.put(detInitialState1, 0);		
		idToStates1.put(0, detInitialState1);
		
		HashSet<Integer> detInitialState2 = new HashSet<Integer>();
		detInitialState2.add(aut2.getInitialState());
		reachedStates2.put(detInitialState2, 0);		
		idToStates2.put(0, detInitialState2);
		
        int st1 = 0;
        int st2 = 0;

        reached1.put(st1, 0);
        reached2.put(st2, 1);

        toVisit.add(new Pair<Integer, Integer>(st1, st2));

        boolean isIn1Final = aut1.isFinalConfiguration(detInitialState1);
        boolean isIn2Final = aut2.isFinalConfiguration(detInitialState2);
        
        if (isIn1Final != isIn2Final)
            return false;

        ds.add(0, isIn1Final);
        ds.add(1, isIn2Final);
        ds.mergeSets(0, 1);

        while (toVisit.size() > 0)
        {
        	Timers.oneMoreState();

			if (System.currentTimeMillis() - startTime > timeout)
				throw new TimeoutException();

            Pair<Integer,Integer> curr = toVisit.get(0);
            toVisit.removeFirst();

            HashSet<Integer> curr1 = idToStates1.get(curr.first);
            HashSet<Integer> curr2 = idToStates2.get(curr.second);

            ArrayList<SRAMove<A, B>> movesFromCurr1 = new ArrayList<>(); 
            movesFromCurr1.addAll(aut1.getMovesFrom(curr1));
            ArrayList<SRAMove<A, B>> movesFromCurr2 = new ArrayList<>(); 
            movesFromCurr2.addAll(aut2.getMovesFrom(curr2));
            
            
            
            ArrayList<A> predicates1 = new ArrayList<>(); 
            for(SRAMove<A, B> m: movesFromCurr1)
            	predicates1.add(m.guard);

            ArrayList<A> predicates2 = new ArrayList<>(); 
            for(SRAMove<A, B> m: movesFromCurr2)
            	predicates2.add(m.guard);

            Collection<Pair<A, ArrayList<Integer>>> minterms1 = ba.GetMinterms(predicates1);
            Collection<Pair<A, ArrayList<Integer>>> minterms2 = ba.GetMinterms(predicates2);


            for (Pair<A, ArrayList<Integer>> minterm1: minterms1)
            {                    
            	for (Pair<A, ArrayList<Integer>> minterm2: minterms2)
                {
                    A conj = ba.MkAnd(minterm1.first, minterm2.first);
                    if (ba.IsSatisfiable(conj))
                    {
                        HashSet<Integer> to1 = new HashSet<Integer>();
                        for (int i = 0; i < minterm1.second.size(); i++)
                            if (minterm1.second.get(i)==1 && ba.IsSatisfiable(ba.MkAnd(movesFromCurr1.get(i).guard, conj)))
                                to1.add(movesFromCurr1.get(i).to);
                        
                        LinkedList<HashSet<Integer>> l1 = new LinkedList<HashSet<Integer>>();
                        int to1st = getStateId(to1, reachedStates1, l1);  
                        if(!l1.isEmpty())
                        	idToStates1.put(reachedStates1.size()-1,to1);
                        
                        HashSet<Integer> to2 = new HashSet<Integer>();
                        for (int i = 0; i < minterm2.second.size(); i++)
                            if (minterm2.second.get(i)==1 && ba.IsSatisfiable(ba.MkAnd(movesFromCurr2.get(i).guard, conj)))
                                to2.add(movesFromCurr2.get(i).to);
                                                
                        LinkedList<HashSet<Integer>> l2 = new LinkedList<HashSet<Integer>>();
                        int to2st = getStateId(to2, reachedStates2, l2);
                        if(!l2.isEmpty())
                        	idToStates2.put(reachedStates2.size()-1,to2);
                        
                        // If not in union find add them
                        int r1 = 0, r2 = 0;
                        if (!reached1.containsKey(to1st))
                        {
                            r1 = ds.getNumberOfElements();
                            reached1.put(to1st, r1);
                            ds.add(r1, aut1.isFinalConfiguration(to1));
                        }
                        else
                            r1 = reached1.get(to1st);

                        if (!reached2.containsKey(to2st))
                        {
                            r2 = ds.getNumberOfElements();
                            reached2.put(to2st, r2);
                            ds.add(r2, aut2.isFinalConfiguration(to2));
                        }
                        else
                            r2 = reached2.get(to2st);                        

                        // Check whether are in simulation relation
                        if (!ds.areInSameSet(r1, r2))
                        {
                            if (!ds.mergeSets(r1, r2))
                                return false;

                            toVisit.add(new Pair<Integer, Integer>(to1st, to2st));
                        }
                    }
                }
            }
        }
        return true;
    }


	/**
	 * concatenation with aut
	 * 
	 * @throws TimeoutException
	 */
	public SRA<P, S> concatenateWith(SRA<P, S> aut, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return concatenate(this, aut, ba);
	}

	/**
	 * concatenates aut1 with aut2
	 * 
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SRA<A, B> concatenate(SRA<A, B> aut1, SRA<A, B> aut2, BooleanAlgebra<A, B> ba)
			throws TimeoutException {

		if (aut1.isEmpty || aut2.isEmpty)
			return getEmptySRA(ba);

		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState = aut1.initialState;
		Collection<Integer> finalStates = new HashSet<Integer>();

		int offSet = aut1.maxStateId + 1;

		for (SRAMove<A, B> t : aut1.getTransitions())
			transitions.add((SRAMove<A, B>) t.clone());

		for (SRAMove<A, B> t : aut2.getTransitions()) {
			SRAMove<A, B> newMove = (SRAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			transitions.add(newMove);
		}

		for (Integer state1 : aut1.finalStates)
			transitions.add(new SRAEpsilon<A, B>(state1, aut2.initialState + offSet));

		for (Integer state : aut2.finalStates)
			finalStates.add(state + offSet);

		return MkSRA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * language star
	 * 
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SRA<A, B> star(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {

		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();

		initialState = aut.maxStateId + 1;

		for (SRAMove<A, B> t : aut.getTransitions())
			transitions.add((SRAMove<A, B>) t.clone());

		// add eps transition from finalStates to initial state
		for (Integer finState : aut.finalStates)
			transitions.add(new SRAEpsilon<A, B>(finState, initialState));

		// add eps transition from new initial state to old initial state
		transitions.add(new SRAEpsilon<A, B>(initialState, aut.initialState));

		// The only final state is the new initial state
		finalStates.add(initialState);

		return MkSRA(transitions, initialState, finalStates, ba, false);
	}

	/**
	 * @return an equivalent deterministic SRA
	 * @throws TimeoutException
	 */
	public SRA<P, S> determinize(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return determinize(this, ba, Long.MAX_VALUE);
	}

	/**
	 * @return an equivalent deterministic SRA
	 * @throws TimeoutException
	 */
	public SRA<P, S> determinize(BooleanAlgebra<P, S> ba, long timeout) throws TimeoutException {
		return determinize(this, ba, timeout);
	}

	/**
	 * @return a deterministic SRA that is equivalent to <code>aut</code>
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> determinize(SRA<A, B> aut, BooleanAlgebra<A, B> ba, long timeout)
			throws TimeoutException {

		long startTime = System.currentTimeMillis();

		if (aut.isDeterministic(ba))
			return aut;

		// Remove epsilon moves before starting
		SRA<A, B> autChecked = aut;
		if (!aut.isEpsilonFree)
			autChecked = aut.removeEpsilonMoves(ba);

		// components of new SRA
		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState = 0;
		Collection<Integer> finalStates = new HashSet<Integer>();

		// reached contains the subset states we discovered and maps them to a
		// stateId
		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		// toVisit contains the subset states we still have not explored
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// the initial state is the set {aut.initialState}
		Collection<Integer> detInitialState = new HashSet<Integer>();
		detInitialState.add(initialState);
		initialState = 0;

		reachedStates.put(detInitialState, 0);
		toVisitStates.add(detInitialState);

		long availableMemory = Runtime.getRuntime().totalMemory();

		// Explore the automaton until no new subset states can be reached
		while (!toVisitStates.isEmpty()) {

			long freeMemory = Runtime.getRuntime().freeMemory();
			if (freeMemory < 0.1 * availableMemory)
				throw new TimeoutException("Out of memory");

			if (System.currentTimeMillis() - startTime > timeout)
				throw new TimeoutException();

			Collection<Integer> currentState = toVisitStates.removeFirst();
			int currentStateId = reachedStates.get(currentState);

			// check if final
			if (autChecked.isFinalConfiguration(currentState))
				finalStates.add(currentStateId);

			// get all the moves out of the states in the current subset
			ArrayList<SRAMove<A, B>> movesFromCurrState = new ArrayList<SRAMove<A, B>>(
					autChecked.getMovesFrom(currentState));

			// put in a separate list all the predicates of the moves and in the
			// same order. We will use them to build the minterms
			ArrayList<A> predicatesOfMoves = new ArrayList<A>();
			for (SRAMove<A, B> inter : movesFromCurrState)
				predicatesOfMoves.add(inter.guard);

			// build the minterms using the predicates and iterate over them:
			// each minterm is a predicate together with the the corresponding
			// set of transition IDs
			for (Pair<A, ArrayList<Integer>> minterm : ba.GetMinterms(predicatesOfMoves,
					timeout - (System.currentTimeMillis() - startTime))) {

				if (System.currentTimeMillis() - startTime > timeout)
					throw new TimeoutException();

				A guard = minterm.first;

				// The new state contains all the target states of the moves
				// with bit 1
				ArrayList<Integer> moveBits = minterm.second;
				Collection<Integer> toState = new HashSet<Integer>();
				for (int moveIndex = 0; moveIndex < moveBits.size(); moveIndex++)
					if (moveBits.get(moveIndex) == 1)
						// add the target state of the moveIndex-th move in the
						// list
						toState.add(movesFromCurrState.get(moveIndex).to);

				// Add new move if target state is not the empty set
				if (toState.size() > 0) {
					int toStateId = getStateId(toState, reachedStates, toVisitStates);
					transitions.add(new SRAMove<A, B>(currentStateId, toStateId, guard));
				}
			}
		}

		SRA<A, B> determinized = MkSRA(transitions, initialState, finalStates, ba, false);
		// set isDetermistic to true to avoid future redundancy
		determinized.isDeterministic = true;
		return determinized;
	}

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

		// New moves
		Map<Pair<Integer, Integer>, A> inputMoves = new HashMap<Pair<Integer, Integer>, A>();
		Set<Pair<Integer, Integer>> epsMoves = new HashSet<Pair<Integer, Integer>>();

		// Create disjunction of all rules between same state
		for (SRAMove<A, B> move : aut.getMovesFrom(aut.states)) {
			Pair<Integer, Integer> fromTo = new Pair<Integer, Integer>(move.from, move.to);
			if (inputMoves.containsKey(fromTo))
				inputMoves.put(fromTo, ba.MkOr(move.guard, inputMoves.get(fromTo)));
			else
				inputMoves.put(fromTo, move.guard);
		}

		// Create the new transition function
		for (Pair<Integer, Integer> p : inputMoves.keySet())
			transitions.add(new SRAMove<A, B>(p.first, p.second, inputMoves.get(p)));

        return MkSRA(transitions, initialState, finalStates, ba, false, false);
	}

	/**
	 * @return a minimized copy of the SRA
	 * @throws TimeoutException
	 */
	public SRA<P, S> minimize(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return getMinimalOf(this, ba);
	}

	/**
	 * @return a minimized copy of <code>aut<code>
	 * @throws TimeoutException
	 */
	public static <A, B> SRA<A, B> getMinimalOf(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {

		if (aut.isEmpty)
			return getEmptySRA(ba);

		// This algorithm is presented the POPL14 paper by D'Antoni and Veanes
		// Minimization of symbolic automata
        SRA<A, B> totalAut = aut.mkTotal(ba);

		Collection<Integer> finStates = totalAut.getFinalStates();
		Collection<Integer> nonFinStates = totalAut.getNonFinalStates();

		// Initially split states into final and non-final
		Block fB = new Block(finStates);
		Block nfB = new Block(nonFinStates);

		// stateToBlock remembers for each state the containing block
		Map<Integer, Block> stateToBlock = new HashMap<Integer, Block>();
		for (int q : finStates)
			stateToBlock.put(q, fB);
		for (int q : nonFinStates)
			stateToBlock.put(q, nfB);

		// blocks that might still be split
		Stack<Block> toExploreBlocks = new Stack<Block>();

		// Initialize search stack with the smallest block
		if (nfB.size() < fB.size())
			toExploreBlocks.push(nfB);
		else
			toExploreBlocks.push(fB);

		// Continue until all blocks have been split
		while (!toExploreBlocks.isEmpty()) {
			Block currentBlock = toExploreBlocks.pop();

			// stateToPredIntoCurrentBlock(s) contains the predicate for which
			// a move of s goes into a state in currentBlock
			Map<Integer, A> stateToPredIntoCurrentBlock = new HashMap<Integer, A>();
			for (SRAMove<A, B> move : totalAut.getMovesTo(currentBlock.set)) {
				if (stateToPredIntoCurrentBlock.containsKey(move.from))
					stateToPredIntoCurrentBlock.put(move.from,
							ba.MkOr(stateToPredIntoCurrentBlock.get(move.from), move.guard));
				else
					stateToPredIntoCurrentBlock.put(move.from, move.guard);
			}

			// Set of states going into currentBlock with some transition
			Block preOfCurrentBlock = new Block(stateToPredIntoCurrentBlock.keySet());

			// Blocks intersecting with preOfCurrentBlock
			HashSet<Block> relevantBlocks = new HashSet<Block>();
			for (int state : stateToBlock.keySet())
				if (preOfCurrentBlock.set.contains(state))
					relevantBlocks.add(stateToBlock.get(state));

			// split relevant blocks
			for (Block relevantBlock : relevantBlocks) {
				Block splitBlock = relevantBlock.intersectWith(preOfCurrentBlock);
				// Change only if the intersection made the block smaller
				if (splitBlock.size() < relevantBlock.size()) {
					for (int p : splitBlock.set) {
						relevantBlock.remove(p);
						stateToBlock.put(p, splitBlock);
					}
					if (toExploreBlocks.contains(relevantBlock))
						toExploreBlocks.push(splitBlock);
					else if (relevantBlock.size() <= splitBlock.size())
						toExploreBlocks.push(relevantBlock);
					else
						toExploreBlocks.push(splitBlock);
				}
			}

			boolean iterate = true;
			while (iterate) {
				iterate = false;

				// Blocks intersecting with preOfCurrentBlock
				relevantBlocks = new HashSet<Block>();
				for (int state : stateToBlock.keySet())
					if (preOfCurrentBlock.set.contains(state))
						relevantBlocks.add(stateToBlock.get(state));

				// split relevant blocks
				for (Block relevantBlock : relevantBlocks) {
					Block splitBlock = new Block();

					int current = relevantBlock.getFirst();
					A psi = stateToPredIntoCurrentBlock.get(current);

					boolean splitterFound = false;
					splitBlock.add(current);

					while (relevantBlock.hasNext()) {
						int q = relevantBlock.getNext();
						A phi = stateToPredIntoCurrentBlock.get(q);
						if (splitterFound) {
							A conj = ba.MkAnd(psi, phi);
							if (ba.IsSatisfiable(conj)) {
								splitBlock.add(q);
								psi = conj;
							}
						} else {
							A conj = ba.MkAnd(psi, ba.MkNot(phi));
							if (ba.IsSatisfiable(conj)) {
								psi = conj; // refine the local minterm
								splitterFound = true;
							} else { // psi implies phi
								conj = ba.MkAnd(phi, ba.MkNot(psi));
								if (ba.IsSatisfiable(conj)) {
									splitBlock.clear();
									splitBlock.add(q);
									psi = conj;
									splitterFound = true;
								} else {
									splitBlock.add(q);
								}
							}
						}
					}
					// Change only if the intersection made the block smaller
					if (splitBlock.size() < relevantBlock.size()) {
						// (a,R)-split of P for some a
						iterate = (iterate || (relevantBlock.size() > 2));
						for (int p : splitBlock.set) {
							relevantBlock.remove(p);
							stateToBlock.put(p, splitBlock);
						}
						if (toExploreBlocks.contains(relevantBlock))
							toExploreBlocks.push(splitBlock);
						else if (relevantBlock.size() <= splitBlock.size())
							toExploreBlocks.push(relevantBlock);
						else
							toExploreBlocks.push(splitBlock);
					}
				}
			}
		}

		// minimal automaton components
		Collection<SRAMove<A, B>> transitions = new ArrayList<SRAMove<A, B>>();
		Integer initialState = 0;
		Map<Block, Integer> blockToIndex = new HashMap<Block, Integer>();
		Map<Integer, Integer> stateToClass = new HashMap<Integer, Integer>();
		Collection<Integer> finalStates = new HashSet<Integer>();

		// One state per block
		for (int state : totalAut.states) {
			Block b = stateToBlock.get(state);
			if (!blockToIndex.containsKey(b))
				blockToIndex.put(b, blockToIndex.size());
			int eqClass = blockToIndex.get(b);
			stateToClass.put(state, eqClass);
			if (totalAut.isFinalState(state))
				finalStates.add(eqClass);
		}

		initialState = stateToClass.get(totalAut.initialState);

		// Create minimal automaton from equivalence classes
		for (Block b : blockToIndex.keySet()) {
			int st = blockToIndex.get(b);
			for (SRAMove<A, B> t : totalAut.getMovesFrom(b.set))
				transitions.add(new SRAMove<A, B>(st, stateToClass.get(t.to), t.guard));

		}

		return MkSRA(transitions, initialState, finalStates, ba, false, true);
	}

	// ------------------------------------------------------
	// Automata properties
	// ------------------------------------------------------

	/**
	 * Checks whether the SRA is ambiguous
	 * 
	 * @return an ambiguous input if the automaton is ambiguous,
	 *         <code>null</code> otherwise
	 * @throws TimeoutException
	 */
	public List<S> getAmbiguousInput(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return getAmbiguousInput(this, ba);
	}

	/**
	 * Checks whether <code>aut</code> is ambiguous
	 * 
	 * @return an ambiguous input if the automaton is ambiguous,
	 *         <code>null</code> otherwise
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> List<B> getAmbiguousInput(SRA<A, B> aut, BooleanAlgebra<A, B> ba) throws TimeoutException {

		SRA<A, B> aut1 = aut;
		SRA<A, B> aut2 = aut;

		SRA<A, B> product = new SRA<A, B>();

		// maps a product state to its id
		HashMap<Pair<Pair<Integer, Integer>, Boolean>, Integer> reached = new HashMap<Pair<Pair<Integer, Integer>, Boolean>, Integer>();
		// maps and id to its product state
		HashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>> reachedRev = new HashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>>();
		// list on unexplored product states
		LinkedList<Pair<Pair<Integer, Integer>, Boolean>> toVisit = new LinkedList<Pair<Pair<Integer, Integer>, Boolean>>();

		// The initial state is the pair consisting of the initial
		// states of aut1 and aut2, true states whether the state was reached by
		// input moves (false for epsilon)
		Pair<Pair<Integer, Integer>, Boolean> initStatePair = new Pair<Pair<Integer, Integer>, Boolean>(
				new Pair<Integer, Integer>(aut1.initialState, aut2.initialState), true);
		product.initialState = 0;
		product.states.add(0);

		reached.put(initStatePair, 0);
		reachedRev.put(0, initStatePair);
		toVisit.add(initStatePair);

		int totStates = 1;

		while (!toVisit.isEmpty()) {
			Pair<Pair<Integer, Integer>, Boolean> currState = toVisit.removeFirst();
			int st1 = currState.first.first;
			int st2 = currState.first.second;
			boolean isInputReached = currState.second;
			int currStateId = reached.get(currState);

			// Set final states
			// if both the epsilon closures contain a final state
			// currentStateID
			// is final
			if (aut1.isFinalState(st1) && aut2.isFinalState(st2))
				product.finalStates.add(currStateId);

			// Try to pair transitions out of both automata
			for (SRAMove<A, B> t1 : aut1.getMovesFrom(st1))
				for (SRAMove<A, B> t2 : aut2.getMovesFrom(st2)) {

					if (t1.to >= t2.to) {
						// create conjunction of the two guards and
						// create
						// transition only if the conjunction is
						// satisfiable
						A intersGuard = ba.MkAnd(t1.guard, t2.guard);
						if (ba.IsSatisfiable(intersGuard)) {

							// Create new product transition and add it
							// to
							// transitions
							Pair<Pair<Integer, Integer>, Boolean> nextState = new Pair<Pair<Integer, Integer>, Boolean>(
									new Pair<Integer, Integer>(t1.to, t2.to), true);
							int nextStateId = 0;

							if (!reached.containsKey(nextState)) {
								product.movesTo.put(totStates, new HashSet<SRAMove<A, B>>());

								reached.put(nextState, totStates);
								reachedRev.put(totStates, nextState);

								toVisit.add(nextState);
								product.states.add(totStates);
								nextStateId = totStates;
								totStates++;
							} else
								nextStateId = reached.get(nextState);

							product.addTransition(new SRAMove<A, B>(currStateId, nextStateId, intersGuard), ba, true);
						}
					}
				}

			if (isInputReached) {
				// get the set of states reachable from currentState via epsilon
				// moves
				Collection<Integer> epsilonClosure1 = aut1.getEpsClosure(st1, ba);
				Collection<Integer> epsilonClosure2 = aut2.getEpsClosure(st2, ba);

				// Add epsilon moves to the closure
				for (Integer state1 : epsilonClosure1)
					for (Integer state2 : epsilonClosure2) {
						// Avoid self epsilon loop
						if ((state1 != st1 || state2 != st2) && state1>=state2) {

							Pair<Pair<Integer, Integer>, Boolean> nextState = new Pair<Pair<Integer, Integer>, Boolean>(
									new Pair<Integer, Integer>(state1, state2), false);
							int nextStateId = 0;

							if (!reached.containsKey(nextState)) {
								product.movesTo.put(totStates, new HashSet<SRAMove<A, B>>());

								reached.put(nextState, totStates);
								reachedRev.put(totStates, nextState);

								toVisit.add(nextState);
								product.states.add(totStates);
								nextStateId = totStates;
								totStates++;
							} else
								nextStateId = reached.get(nextState);

							product.addTransition(new SRAEpsilon<A, B>(currStateId, nextStateId), ba, true);
						}
					}
			}
		}

		product = removeDeadOrUnreachableStates(product, ba);

		// Check if a state that of the form (s1,s2) such that s1!=s2 is still
		// alive, if so any string passing to it is ambiguous
		for (Integer aliveSt : product.states) {
			Pair<Pair<Integer, Integer>, Boolean> stP = reachedRev.get(aliveSt);
			if (stP.first.first != stP.first.second) {
				SRA<A, B> left = (SRA<A, B>) product.clone();
				SRA<A, B> right = (SRA<A, B>) product.clone();
				left.finalStates = new HashSet<Integer>();
				left.finalStates.add(aliveSt);
				right.initialState = aliveSt;

				SRA<A, B> c = left.concatenateWith(right, ba);
				SRA<A, B> clean = removeDeadOrUnreachableStates(c, ba);
				return clean.getWitness(ba);
			}
		}
		return null;
	}

	/**
	 * Checks whether the automaton is deterministic
	 * 
	 * @return true iff the automaton is deterministic
	 * @throws TimeoutException
	 */
	public boolean isDeterministic(BooleanAlgebra<P, S> ba) throws TimeoutException {
		// Check if we set it before
		if (isDeterministic)
			return true;

        // Check transitions out of a state are mutually exclusive
		for (Integer state : states) {
			List<SRAMove<P, S>> movesFromState = new ArrayList<SRAMove<P, S>>(getTransitionsFrom(state));

			for (int i = 0; i < movesFromState.size(); i++) {
				SRAMove<P, S> t1 = movesFromState.get(i);
				for (int p = i + 1; p < movesFromState.size(); p++) {
					SRAMove<P, S> t2 = movesFromState.get(p);
					if (!t1.isDisjointFrom(t2, ba)) {
						isDeterministic = false;
						return false;
					}
				}
			}
		}

		isDeterministic = true;
		return true;
	}

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

		return MkSRA(transitions, initialState, finalStates, ba, false, false);
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
		moves.addAll(getMovesFrom(state));
		return moves;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SRAMove<P, S>> getTransitionsTo(Integer state) {
		Collection<SRAMove<P, S>> moves = new HashSet<SRAMove<P, S>>();
		moves.addAll(getMovesTo(state));
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
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SRAMove<P, S>> getMovesTo(Integer state) {
		Collection<SRAMove<P, S>> trset = movesTo.get(state);
		if (trset == null) {
			trset = new HashSet<SRAMove<P, S>>();
			movesTo.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SRAMove<P, S>> getMovesTo(Collection<Integer> stateSet) {
		Collection<SRAInputMove<P, S>> transitions = new LinkedList<SRAInputMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getMovesTo(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions to state <code>s</code>
	 */
	public Collection<SRAMove<P, S>> getMovesFrom(Integer state) {
		Collection<SRAInputMove<P, S>> trset = inputMovesFrom.get(state);
		if (trset == null) {
			trset = new HashSet<SRAInputMove<P, S>>();
			inputMovesFrom.put(state, trset);
			return trset;
		}
		return trset;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SRAMove<P, S>> getMovesFrom(Collection<Integer> stateSet) {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : stateSet)
			transitions.addAll(getMovesFrom(state));
		return transitions;
	}

	/**
	 * Returns the set of transitions starting set of states
	 */
	public Collection<SRAMove<P, S>> getTransitions() {
		Collection<SRAMove<P, S>> transitions = new LinkedList<SRAMove<P, S>>();
		for (Integer state : states)
			transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	// ----------------------------------------------------
	// Overridden methods
	// ----------------------------------------------------

	@Override
	public Collection<Move<P, S>> getMovesFrom(Integer state) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getTransitionsFrom(state));
		return transitions;
	}

	@Override
	public Collection<Move<P, S>> getMovesTo(Integer state) {
		Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
		transitions.addAll(getTransitionsTo(state));
		return transitions;
	}

	@Override
	public Integer getInitialState() {
		return initialState;
	}

	@Override
	public Collection<Integer> getFinalStates() {
		return finalStates;
	}

	public Collection<Integer> getNonFinalStates() {
		HashSet<Integer> nonFin = new HashSet<Integer>(states);
		nonFin.removeAll(finalStates);
		return nonFin;
	}

	@Override
	public Collection<Integer> getStates() {
		return states;
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

		cl.movesFrom = new HashMap<Integer, Collection<SRAMove<P, S>>>(movesFrom);
		cl.movesTo = new HashMap<Integer, Collection<SRAMove<P, S>>>(movesTo);
		return cl;
	}

}

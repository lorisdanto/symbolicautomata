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
import automata.AutomataException;
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
		aut.initialStates = new HashSet<Integer>(aut.states);
		aut.isDeterministic = true;
		aut.isEmpty = true;
		aut.isEpsilonFree = true;
		aut.stateCount = aut.maxStateId = 1;
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
		aut.initialStates = new HashSet<Integer>(aut.states);
		aut.isDeterministic = true;
		aut.isEmpty = false;
		aut.isEpsilonFree = true;
		aut.stateCount = aut.maxStateId = 1;
		aut.addTransition(new InputMove<A, B>(0, 0, ba.True()), ba, true);
		return aut;
	}

	protected Collection<Integer> states;
	protected Collection<Integer> initialStates;
	protected Collection<Integer> finalStates;

	public Integer stateCount;
	public Integer maxStateId;

	protected Map<Integer, Collection<SFAMove<U, S>>> transitionsFrom;
	protected Map<Integer, Collection<SFAMove<U, S>>> transitionsTo;
	public Integer transitionCount;

	protected SFA() {
		super();
		initialStates = new HashSet<Integer>();
		finalStates = new HashSet<Integer>();
		states = new HashSet<Integer>();
		transitionsFrom = new HashMap<Integer, Collection<SFAMove<U, S>>>();
		transitionsTo = new HashMap<Integer, Collection<SFAMove<U, S>>>();
		transitionCount = 0;
		stateCount = maxStateId = 0;
	}

	/*
	 * Create an automaton (removes unreachable states)
	 */
	public static <A, B> SFA<A, B> MkSFA(Collection<SFAMove<A, B>> transitions,
			Collection<Integer> initialStates, Collection<Integer> finalStates,
			BooleanAlgebra<A, B> ba) throws AutomataException {

		// Sanity checks
		if (initialStates.size() == 0)
			throw new AutomataException("No initial states");

		SFA<A, B> aut = new SFA<A, B>();

		aut.states = new HashSet<Integer>(initialStates);
		aut.states.addAll(finalStates);

		aut.initialStates = initialStates;
		aut.finalStates = finalStates;

		for (SFAMove<A, B> t : transitions)
			aut.addTransition(t, ba, false);

		// cleanup set isEmpty and hasEpsilon
		aut = removeUnreachableStates(aut, ba);

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

		SFA<A, B> inters = new SFA<A, B>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial states
		int totStates = 0;
		for (Integer st1 : aut1.initialStates)
			for (Integer st2 : aut2.initialStates) {
				Pair<Integer, Integer> p = new Pair<Integer, Integer>(st1, st2);
				inters.initialStates.add(totStates);
				inters.states.add(totStates);

				reached.put(p, totStates);
				toVisit.add(p);

				totStates++;
			}

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
					inters.finalStates.add(currStateId);
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
								inters.transitionsTo.put(totStates,
										new HashSet<SFAMove<A, B>>());
								reached.put(nextState, totStates);
								toVisit.add(nextState);
								inters.states.add(totStates);
								nextStateId = totStates;
								totStates++;
							} else
								nextStateId = reached.get(nextState);

							InputMove<A, B> newTrans = new InputMove<A, B>(
									currStateId, nextStateId, intersGuard);

							inters.addTransition(newTrans, ba, true);
						}
					}
				}
		}

		inters = removeUnreachableStates(inters, ba);
		return inters;
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

		SFA<A, B> diff = aut1.intersectionWith(aut2.complement(ba), ba);
		return removeUnreachableStates(diff, ba);
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

		SFA<A, B> union = new SFA<A, B>();
		union.isEmpty = false;

		int offSet = aut1.maxStateId + 2;
		union.maxStateId = aut2.maxStateId + offSet+1;

		for (Integer state : aut1.states)
			union.states.add(state);

		for (Integer state : aut2.states)
			union.states.add(state + offSet);

		Integer initState = union.maxStateId;
		union.initialStates.add(initState);
		union.states.add(initState);

		for (SFAMove<A, B> t : aut1.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			union.addTransition(newMove, ba, true);
		}

		for (SFAMove<A, B> t : aut2.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			union.addTransition(newMove, ba, true);
		}

		for (Integer state : aut1.initialStates)
			union.addTransition(new Epsilon<A, B>(initState, state), ba, true);

		for (Integer state : aut2.initialStates)
			union.addTransition(new Epsilon<A, B>(initState, state + offSet), ba, true);

		for (Integer state : aut1.finalStates)
			union.finalStates.add(state);

		for (Integer state : aut2.finalStates)
			union.finalStates.add(state + offSet);

		return union;
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
	public static <A, B> SFA<A, B> removeEpsilonMovesFrom(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		SFA<A, B> epsFree = new SFA<A, B>();

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

			for (SFAMove<A, B> t1 : aut.getTransitionsFrom(currState)) {
				if (!t1.isEpsilonTransition()) {
					Collection<Integer> nextState = aut
							.getEpsClosure(t1.to, ba);

					int nextStateId = 0;

					if (!reachedStates.containsKey(nextState)) {
						int index = reachedStates.size();
						reachedStates.put(nextState, index);
						toVisitStates.add(nextState);
						epsFree.states.add(index);
						nextStateId = index;
					} else {
						nextStateId = reachedStates.get(nextState);
					}

					@SuppressWarnings("unchecked")
					SFAMove<A, B> tnew = (SFAMove<A, B>) t1.clone();
					tnew.from = currStateId;
					tnew.to = nextStateId;

					epsFree.addTransition(tnew, ba, true);
				}
			}

		}

		for (Collection<Integer> stSet : reachedStates.keySet())
			if (aut.isFinalConfiguration(stSet))
				epsFree.finalStates.add(reachedStates.get(stSet));

		return removeUnreachableStates(epsFree, ba);
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

		Collection<Integer> finStateCopy = new HashSet<Integer>(
				comp.finalStates);
		comp.finalStates = new HashSet<Integer>();

		for (Integer st : comp.states)
			if (!finStateCopy.contains(st))
				comp.finalStates.add(st);

		return comp;
	}

	/**
	 * return the complement of the current SFA
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> SFA<A, B> mkSingleInitState(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {
		
		SFA<A, B> sing = (SFA<A, B>) aut.clone();
		if(sing.initialStates.size()==1)
			return sing;
		
		Integer newState = sing.maxStateId+1;
		
		for(Integer st: sing.states)
			sing.addTransition(new Epsilon<A, B>(newState, st), ba, true);
		
		sing.initialStates = new HashSet<Integer>(); 
		sing.initialStates.add(newState);
		
		return sing;
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

		SFA<A, B> total = new SFA<A, B>();

		total.initialStates = new HashSet<Integer>(sfa.initialStates);
		total.finalStates = new HashSet<Integer>(sfa.finalStates);
		int newState = sfa.maxStateId + 1;
		for (Integer state : sfa.states) {
			A totGuard = null;
			for (InputMove<A, B> move : sfa.getInputMovesFrom(state)) {
				total.addTransition(move, ba, true);
				if (totGuard == null)
					totGuard = ba.MkNot(move.guard);
				else
					totGuard = ba.MkAnd(totGuard, ba.MkNot(move.guard));
			}
			if (totGuard != null)
				total.addTransition(new InputMove<A, B>(state, newState,
						totGuard), ba, false);
		}
		if (total.states.contains(newState))
			total.addTransition(
					new InputMove<A, B>(newState, newState, ba.True()), ba,
					true);

		total.isTotal = true;
		return total;
	}

	/**
	 * return the complement of the current SFA
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
	 * concatenation
	 */
	public SFA<U, S> concatenateWith(SFA<U, S> aut, BooleanAlgebra<U, S> ba) {
		return concatenate(this, aut, ba);
	}

	/**
	 * concatenates aut1 with aut2
	 */
	public static <A, B> SFA<A, B> concatenate(SFA<A, B> aut1, SFA<A, B> aut2, BooleanAlgebra<A,B> ba) {
		
		if (aut1.isEmpty && aut2.isEmpty)
			return getEmptySFA(ba);

		SFA<A, B> concat = new SFA<A, B>();
		concat.isEmpty = false;

		int offSet = aut1.maxStateId + 1;
		concat.maxStateId = aut2.maxStateId + offSet;

		for (Integer state : aut1.states)
			concat.states.add(state);

		for (Integer state : aut2.states)
			concat.states.add(state + offSet);

		concat.initialStates=new HashSet<Integer>(aut1.initialStates);		

		for (SFAMove<A, B> t : aut1.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			concat.addTransition(newMove, ba, true);
		}

		for (SFAMove<A, B> t : aut2.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			newMove.from += offSet;
			newMove.to += offSet;
			concat.addTransition(newMove, ba, true);
		}

		for (Integer state1 : aut1.finalStates)
			for (Integer state2 : aut2.initialStates)
				concat.addTransition(new Epsilon<A, B>(state1, state2+ offSet), ba, true);

		concat.finalStates=new HashSet<Integer>();

		for (Integer state : aut2.finalStates)
			concat.finalStates.add(state + offSet);

		return concat;
	}
	
	/**
	 * language star
	 */
	public static <A, B> SFA<A, B> star(SFA<A, B> aut, BooleanAlgebra<A,B> ba) {
		
		if (aut.isEmpty)
			return getEmptySFA(ba);

		SFA<A, B> star = new SFA<A, B>();
		star.isEmpty = false;

		star.states= new HashSet<Integer>(aut.states);
		Integer initState = aut.maxStateId+1;
			
		star.initialStates=new HashSet<Integer>();		
		star.initialStates.add(initState);

		for (SFAMove<A, B> t : aut.getTransitions()) {
			@SuppressWarnings("unchecked")
			SFAMove<A, B> newMove = (SFAMove<A, B>) t.clone();
			star.addTransition(newMove, ba, true);
		}

		for (Integer state : aut.finalStates)
			star.addTransition(new Epsilon<A, B>(state, initState), ba, true);
		
		for (Integer state : aut.initialStates)
			star.addTransition(new Epsilon<A, B>(initState, state), ba, true);

		star.finalStates=new HashSet<Integer>(aut.finalStates);
		star.finalStates.add(initState);

		return star;
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
	public static <A, B> SFA<A, B> determinize(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		if (aut.isDeterministic(ba))
			return aut;

		SFA<A, B> deter = new SFA<A, B>();

		HashMap<Collection<Integer>, Integer> reachedStates = new HashMap<Collection<Integer>, Integer>();
		LinkedList<Collection<Integer>> toVisitStates = new LinkedList<Collection<Integer>>();

		// Add initial state
		Collection<Integer> currState = aut
				.getEpsClosure(aut.initialStates, ba);

		deter.initialStates.add(0);
		deter.states.add(0);

		reachedStates.put(currState, 0);
		toVisitStates.add(currState);

		// Dfs to find states
		while (!toVisitStates.isEmpty()) {
			currState = toVisitStates.removeFirst();
			int currStateId = reachedStates.get(currState);

			// Check if final state
			if (aut.isFinalConfiguration(currState))
				deter.finalStates.add(currStateId);

			ArrayList<InputMove<A, B>> movesFromCurrState = new ArrayList<InputMove<A, B>>(
					aut.getInputMovesFrom(currState));

			ArrayList<A> internalPredicates = new ArrayList<A>();
            for (InputMove<A, B> inter: movesFromCurrState)
                internalPredicates.add(inter.guard);
			
            for(Pair<A, ArrayList<Integer>> minterm : ba.GetMinterms(internalPredicates)){

				A guard = minterm.first;
				Collection<Integer> bitList= minterm.second;
				Integer index=0;
				
				Collection<Integer> toState = new HashSet<Integer>();								

				for(Integer bit: bitList){
					// use the predicate positively if i-th bit of i is 1
                    if (bit == 1)
                    {
                        // get the indexth call in the list
                    	InputMove<A, B> bitMove = movesFromCurrState.get(index);
                    	toState.add(bitMove.to);
                    }
                    index++;
				}
					
				toState = aut.getEpsClosure(toState, ba);
				if (toState.size() > 0)
                {
					Integer toStateId = reachedStates.get(toState);
					if (toStateId == null) {
						toStateId = reachedStates.size();
						reachedStates.put(toState, toStateId);
						toVisitStates.add(toState);
					}
					deter.addTransition(new InputMove<A, B>(currStateId,
							toStateId, guard), ba, true);
                }				
			}
		}

		deter.stateCount=deter.states.size();
		deter.isDeterministic = true;
		return deter;
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
		
		SFA<A, B> aut1 = (SFA<A, B>) mkSingleInitState(aut, ba).clone();
		SFA<A, B> aut2 = (SFA<A, B>) aut1.clone();

		SFA<A, B> product = new SFA<A, B>();

		HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
		HashMap<Integer, Pair<Integer, Integer>> reachedRev = new HashMap<Integer, Pair<Integer, Integer>>();
		LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

		// Add initial states
		int totStates = 0;
		for (Integer st1 : aut1.initialStates)
			for (Integer st2 : aut2.initialStates) {
				Pair<Integer, Integer> p = new Pair<Integer, Integer>(st1, st2);
				product.initialStates.add(totStates);
				product.states.add(totStates);

				reached.put(p, totStates);
				reachedRev.put(totStates, p);
				toVisit.add(p);

				totStates++;
			}

		while (!toVisit.isEmpty()) {
			Pair<Integer, Integer> currState = toVisit.removeFirst();
			int currStateId = reached.get(currState);

			Collection<Integer> epsClo1 = aut1.getEpsClosure(currState.first, ba);
			Collection<Integer> epsClo2 = aut2.getEpsClosure(currState.second, ba);

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
		
		product = removeUnreachableStates(product, ba);
		for(Integer aliveSt: product.states){
			Pair<Integer,Integer> stP = reachedRev.get(aliveSt);
			if(stP.first!=stP.second){
				SFA<A, B> left = (SFA<A, B>) product.clone();
				SFA<A, B> right = (SFA<A, B>) product.clone();
				left.finalStates=new HashSet<Integer>();
				left.finalStates.add(aliveSt);
				right.initialStates=new HashSet<Integer>();
				right.initialStates.add(aliveSt);
				
				SFA<A, B> c = left.concatenateWith(right,ba);
				SFA<A, B> clean = removeUnreachableStates(c, ba);
				return clean.getWitness(ba);
			}
		}
		return null;
	}
	
	/**
	 * return the determinization of the current SFA
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

		if(aut1.isEmpty)
			return (SFA<A, B>)aut1.clone();
		
		SFA<A, B> aut = aut1;		
		
		if (!aut1.isDeterministic)
			aut = aut1.determinize(ba);

		SFA<A, B> minimal = new SFA<A, B>();
		
		HashSet<Integer> toSeeStates = new HashSet<Integer>(aut.states);
		
		HashMap<Integer,Integer> stateToClass = new HashMap<Integer,Integer>();
		ArrayList<Collection<Integer>> eqClasses = new ArrayList<Collection<Integer>>(aut.stateCount);

		// Check for equiv classes
		int classIndex=0;
		while (!toSeeStates.isEmpty()) {
			LinkedList<Integer> toSeeCopy = new LinkedList<Integer>(toSeeStates);			
			Integer currState = toSeeCopy.removeFirst();
			toSeeStates.remove(currState);
			
			stateToClass.put(currState, classIndex);
			
			HashSet<Integer> eqClass = new HashSet<Integer>();
			eqClass.add(currState);

			SFA<A, B> autCurrState = (SFA<A, B>)aut1.clone();
			autCurrState.initialStates = new HashSet<Integer>();
			autCurrState.initialStates.add(currState);

			SFA<A, B> autOtherState = (SFA<A, B>)aut1.clone();
			
			//Start at 1 to avoid case in which they are all false
			for(Integer otherState: toSeeCopy){
				autOtherState.initialStates = new HashSet<Integer>();
				autOtherState.initialStates.add(otherState);
				
				if(autCurrState.isEquivalentTo(autOtherState, ba)){
					toSeeStates.remove(otherState);
					eqClass.add(otherState);
					stateToClass.put(otherState, classIndex);
				}
			}
			eqClasses.add(classIndex, eqClass);
			classIndex++;
		}
		
		Integer initStateAut = (Integer) aut.initialStates.toArray()[0];
		for(int i =0 ;i<eqClasses.size();i++){
			Collection<Integer> eqClass = eqClasses.get(i);			
			
			for(InputMove<A, B> t: aut.getInputMovesFrom(eqClass))
				minimal.addTransition(new InputMove<A, B>(i, stateToClass.get(t.to), t.guard), ba, true);
			
			if(aut.isFinalConfiguration(eqClass))
				minimal.finalStates.add(i);
			
			if(eqClass.contains(initStateAut))
				minimal.initialStates.add(0);
		}
		
		minimal.stateCount=eqClasses.size();				
		minimal = minimal.determinize(ba);
		return minimal;
	}

	// ////////////////////////////////////////////////////////////////////////////////

	// Accessory methods
	private static <A, B> SFA<A, B> removeUnreachableStates(SFA<A, B> aut,
			BooleanAlgebra<A, B> ba) {

		SFA<A, B> clean = new SFA<A, B>();

		Collection<Integer> reachableFromInit = aut
				.getReachableStatesFrom(aut.initialStates);
		Collection<Integer> reachingFinal = aut
				.getReachingStates(aut.finalStates);

		for (Integer state : reachableFromInit)
			if (reachingFinal.contains(state)) {
				clean.stateCount++;
				clean.states.add(state);
				if (state > clean.maxStateId)
					clean.maxStateId = state;
			}

		if (clean.stateCount == 0)
			return getEmptySFA(ba);

		for (Integer state : clean.states)
			for (SFAMove<A, B> t : aut.getTransitionsFrom(state))
				if (clean.states.contains(t.to))
					clean.addTransition(t, ba, true);

		for (Integer state : aut.initialStates)
			if (clean.states.contains(state))
				clean.initialStates.add(state);

		for (Integer state : aut.finalStates)
			if (clean.states.contains(state))
				clean.finalStates.add(state);


		
		return clean;
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
		if (initialStates.size() != 1) {
			isDeterministic = false;
			return isDeterministic;
		}

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

		cl.stateCount = stateCount;
		cl.maxStateId = maxStateId;
		cl.transitionCount = transitionCount;

		cl.states = new HashSet<Integer>(states);
		cl.initialStates = new HashSet<Integer>(initialStates);
		cl.finalStates = new HashSet<Integer>(finalStates);

		cl.transitionsFrom = new HashMap<Integer, Collection<SFAMove<U, S>>>(
				transitionsFrom);
		cl.transitionsTo = new HashMap<Integer, Collection<SFAMove<U, S>>>(
				transitionsTo);

		return cl;
	}	




}

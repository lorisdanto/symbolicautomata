package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import theory.BooleanAlgebra;

public abstract class LTLFormula<P,S> {

	public SAFA<P,S> getSAFA(BooleanAlgebra<P, S> ba){
		
		Integer initialState = 0;
		HashMap<LTLFormula<P, S>, Integer> formulaToStateId = new HashMap<>();
		
		Collection<Integer> finalStates = new HashSet<>();
		HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves = new HashMap<>();
		
		this.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		Collection<SAFAInputMove<P, S>> transitions = new LinkedList<>();
		for(Collection<SAFAInputMove<P, S>> c: moves.values())
			transitions.addAll(c);		
		
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
	}
	
	// Checks whether a formula should be a final state in the automaton
	public LTLFormula<P,S> pushNegations(BooleanAlgebra<P, S> ba){
		return pushNegations(true,ba);
	}
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract LTLFormula<P,S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba);	
	
	// returns set of disjoint predicates that are the triggers of transitions out of this state
	protected abstract void accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves, 
			Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba
			);
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract boolean isFinalState();	
	
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}

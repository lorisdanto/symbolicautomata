package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import theory.BooleanAlgebra;

public abstract class LTLFormula<P,S> {

	public <E extends BooleanExpression> SAFA<P,S,E> getSAFA(BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr){
		
		E initialState = boolexpr.MkState(0);
		HashMap<LTLFormula<P, S>, Integer> formulaToStateId = new HashMap<>();
		
		Collection<Integer> finalStates = new HashSet<>();
		HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves = new HashMap<>();
		
		this.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, boolexpr);

		Collection<SAFAInputMove<P, S, E>> transitions = new LinkedList<>();
		for(Collection<SAFAInputMove<P, S, E>> c: moves.values())
			transitions.addAll(c);		
		
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba, boolexpr, false, true);
	}
	
	// Checks whether a formula should be a final state in the automaton
	public LTLFormula<P,S> pushNegations(BooleanAlgebra<P, S> ba){
		return pushNegations(true,ba);
	}
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract LTLFormula<P,S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba);	
	
	// returns set of disjoint predicates that are the triggers of transitions out of this state
	protected abstract <E extends BooleanExpression> void accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves,
			Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr
			);
	
	// returns set of disjoint predicates that are the triggers of transitions out of this state
	public abstract <E extends BooleanExpression> SAFA<P,S,E> getSAFANew(
			BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr
			);
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract boolean isFinalState();	
	
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		this.toString(sb);
		return sb.toString();
	}
	
	public abstract void toString(StringBuilder sb);
	
}

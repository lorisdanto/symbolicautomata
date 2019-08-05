package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpression;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public abstract class LTLFormula<P,S> {
	
	public <E extends BooleanExpression> SAFA<P,S> getSAFA(BooleanAlgebra<P, S> ba) throws TimeoutException{
				
		HashMap<LTLFormula<P, S>, PositiveBooleanExpression> formulaToStateId = new HashMap<>();
		
		Collection<Integer> finalStates = new HashSet<>();
		Collection<SAFAInputMove<P, S>> moves = new LinkedList<>();
		
		int emptyId = 0;
		HashSet<Integer> states = new HashSet<>();
		states.add(emptyId);
		
		//This is the state for the empty string		
		PositiveBooleanExpression initialState = this.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, states);
		

		return SAFA.MkSAFA(moves, initialState, finalStates, ba, false, true, false);
	}
	
	// Checks whether a formula should be a final state in the automaton
	public LTLFormula<P,S> pushNegations(BooleanAlgebra<P, S> ba) throws TimeoutException{
		return pushNegations(true,ba, new HashMap<>(), new HashMap<>());
	}
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract LTLFormula<P,S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba, HashMap<String, LTLFormula<P,S>> posHash, HashMap<String, LTLFormula<P,S>> negHash) throws TimeoutException;	
	
	// returns set of disjoint predicates that are the triggers of transitions out of this state
	protected abstract PositiveBooleanExpression accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, PositiveBooleanExpression> formulaToInitState,
			Collection<SAFAInputMove<P, S>> moves,
			Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba, HashSet<Integer> states);
	
	
	public abstract int getSize();	
	
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

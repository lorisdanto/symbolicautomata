package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import theory.BooleanAlgebra;

public abstract class LTLFormula<P,S> {

//	public SAFA<P,S> getSAFA(BooleanAlgebra<P, S> ba){
//		
//		Integer initialState = 0;
//		HashMap<LTLFormula<P, S>, Integer> formulaToStateId = new HashMap<>();
//		HashMap<Integer, LTLFormula<P, S>> idToFormula = new HashMap<>();
//		
//		Collection<Integer> finalStates = new HashSet<>();
//		HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves = new HashMap<>();
//		
//		this.accumulateSAFAStatesTransitions(formulaToStateId, idToFormula, moves, finalStates);
//
//		Collection<SAFAInputMove<P, S>> transitions = new LinkedList<>();
//		for(Collection<SAFAInputMove<P, S>> c: moves.values())
//			transitions.addAll(c);		
//		
//		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
//	}
//	
//	// TODO
//	protected abstract Collection<SAFAInputMove<P, S>> accumulateSAFAStatesTransitions(
//			HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
//			HashMap<Integer, LTLFormula<P, S>> idToFormula,
//			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves, 
//			Collection<Integer> finalStates);
	
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}

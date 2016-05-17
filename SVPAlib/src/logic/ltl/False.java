package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public class False<P, S> extends LTLFormula<P, S> {

	public False() {
		super();
	}

	@Override
	public int hashCode() {
		return 11;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof False))
			return false;
		return true;
	}		
	
	@Override
	protected PositiveBooleanExpression accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, PositiveBooleanExpression> formulaToState, Collection<SAFAInputMove<P, S>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba, HashSet<Integer> states) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToState.containsKey(this))
			return formulaToState.get(this);

		// Update hash tables
		int id =states.size();
		states.add(id);
		PositiveBooleanExpression initialState = boolexpr.MkState(id);
		formulaToState.put(this, initialState);		
		
		return initialState;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba, HashMap<String, LTLFormula<P,S>> posHash, HashMap<String, LTLFormula<P,S>> negHash){
		if(isPositive){
			return this;
		}
		else{ 
			String key = this.toString();
			if (negHash.containsKey(key)) {
				return negHash.get(key);
			} else {
				LTLFormula<P, S> out = new True<>();
				negHash.put(key, out);
				return out;
			}			
		}
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("false");
	}
	
//	@Override
//	public SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba) {
//		return SAFA.getEmptySAFA(ba);
//	}
	
	@Override
	public int getSize() {
		return 1;
	}
}

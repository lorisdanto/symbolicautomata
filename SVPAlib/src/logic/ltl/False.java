package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFAInputMove;
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
	protected <E extends BooleanExpression> void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);
		
		// delta(False, _) = nothing		
		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
		
		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		if(isPositive)
			return this;
		else 
			return new True<>();
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("false");
	}
}

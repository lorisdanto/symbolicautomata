package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.SumOfProducts;
import theory.BooleanAlgebra;

public class True<P, S> extends LTLFormula<P, S> {

	public True() {
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
		if (!(obj instanceof True))
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
		
		// delta(True, true) = True		
		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
		newMoves.add(new SAFAInputMove<>(id, boolexpr.MkState(id), ba.True()));

		// True is a final state
		finalStates.add(id);
		
		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return true;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		if(isPositive)
			return this;
		else 
			return new False<>();
	}
}

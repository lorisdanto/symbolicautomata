package logic.ltl;

import java.util.Collection;
import java.util.HashMap;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
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
	protected PositiveBooleanExpression accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, PositiveBooleanExpression> formulaToState, Collection<SAFAInputMove<P, S>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba, int emptyId) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToState.containsKey(this))
			return formulaToState.get(this);

		// Update hash tables
		int id = formulaToState.size();
		PositiveBooleanExpression initialState = boolexpr.MkState(id);
		formulaToState.put(this, initialState);

		// delta(True, true) = True
		moves.add(new SAFAInputMove<>(id, boolexpr.MkState(id), ba.True()));

		// True is a final state
		finalStates.add(id);

		return initialState;
	}

	@Override
	protected boolean isFinalState() {
		return true;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba,
			HashMap<String, LTLFormula<P, S>> posHash, HashMap<String, LTLFormula<P, S>> negHash) {
		if (isPositive) {
			return this;
		} else {
			String key = this.toString();
			if (negHash.containsKey(key)) {
				return negHash.get(key);
			} else {
				LTLFormula<P, S> out = new False<>();
				negHash.put(key, out);
				return out;
			}
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("true");
	}

	@Override
	public int getSize() {
		return 1;
	}
}

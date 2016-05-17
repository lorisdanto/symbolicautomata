package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public class Last<P, S> extends LTLFormula<P, S> {

	public Last() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime *prime;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Last))
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
		int id = states.size();
		states.add(id);
		PositiveBooleanExpression initialState = boolexpr.MkState(id);
		formulaToState.put(this, initialState);

		int id2 = states.size();
		states.add(id2);
		PositiveBooleanExpression toState = boolexpr.MkState(id2);

		// delta(last, true) = next
		moves.add(new SAFAInputMove<>(id, toState, ba.True()));

		// True is a final state
		finalStates.add(id2);

		return initialState;
	}


	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba,
			HashMap<String, LTLFormula<P, S>> posHash, HashMap<String, LTLFormula<P, S>> negHash)
					throws TimeoutException {
		throw new IllegalArgumentException("This shouldn't happen");
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("last");
	}

	@Override
	public int getSize() {
		return 1;
	}
}

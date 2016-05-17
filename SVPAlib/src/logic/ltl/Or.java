package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public class Or<P, S> extends LTLFormula<P, S> {

	protected List<LTLFormula<P, S>> disjuncts;

	public Or(LTLFormula<P, S> left, LTLFormula<P, S> right) {
		super();
		disjuncts = new ArrayList<>();
		disjuncts.add(left);
		disjuncts.add(right);
	}

	public Or(List<LTLFormula<P, S>> c) {
		super();
		disjuncts = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disjuncts == null) ? 0 : disjuncts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Or))
			return false;
		Or<?, ?> other = (Or<?, ?>) obj;
		if (disjuncts == null) {
			if (other.disjuncts != null)
				return false;
		} else if (!disjuncts.equals(other.disjuncts))
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

		PositiveBooleanExpression initialState = boolexpr.False();

		// Compute transitions for children
		for (LTLFormula<P, S> phi : disjuncts) {
			PositiveBooleanExpression conjInit = phi.accumulateSAFAStatesTransitions(formulaToState, moves, finalStates,
					ba, states);
			initialState = boolexpr.MkOr(initialState, conjInit);
		}

		// Update hash tables
		formulaToState.put(this, initialState);

		return initialState;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba,
			HashMap<String, LTLFormula<P, S>> posHash, HashMap<String, LTLFormula<P, S>> negHash) throws TimeoutException {
		String key = this.toString();

		LTLFormula<P, S> out = new False<>();

		if (isPositive) {
			if (posHash.containsKey(key)) {
				return posHash.get(key);
			}
			List<LTLFormula<P, S>> newPhis = new ArrayList<>();
			for (LTLFormula<P, S> phi : disjuncts)
				newPhis.add(phi.pushNegations(isPositive, ba, posHash, negHash));
			out = new Or<>(newPhis);
			posHash.put(key, out);
			return out;
		} else {
			if (negHash.containsKey(key))
				return negHash.get(key);
			List<LTLFormula<P, S>> newPhis = new ArrayList<>();
			for (LTLFormula<P, S> phi : disjuncts)
				newPhis.add(phi.pushNegations(isPositive, ba, posHash, negHash));
			out = new And<>(newPhis);
			negHash.put(key, out);
			return out;
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		boolean isFirst = true;
		for (LTLFormula<P, S> phi : disjuncts) {
			if (!isFirst)
				sb.append(" | ");

			phi.toString(sb);
			isFirst = false;
		}
		sb.append(")");
	}

	@Override
	public int getSize() {
		int size = 1;
		for (LTLFormula<P, S> c : disjuncts)
			size += c.getSize();
		return size;
	}
}

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

public class And<P, S> extends LTLFormula<P, S> {

	protected List<LTLFormula<P, S>> conjuncts;

	public And(LTLFormula<P, S> left, LTLFormula<P, S> right) {
		super();
		conjuncts = new ArrayList<>();
		conjuncts.add(left);
		conjuncts.add(right);
	}

	public And(List<LTLFormula<P, S>> c) {
		super();
		conjuncts = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conjuncts == null) ? 0 : conjuncts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof And))
			return false;
		And<?, ?> other = (And<?, ?>) obj;
		if (conjuncts == null) {
			if (other.conjuncts != null)
				return false;
		} else if (!conjuncts.equals(other.conjuncts))
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

		PositiveBooleanExpression initialState = boolexpr.True();

		// Compute transitions for children
		for (LTLFormula<P, S> phi : conjuncts) {
			PositiveBooleanExpression conjInit = phi.accumulateSAFAStatesTransitions(formulaToState, moves, finalStates,
					ba, states);
			initialState = boolexpr.MkAnd(initialState, conjInit);
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
			for (LTLFormula<P, S> phi : conjuncts)
				newPhis.add(phi.pushNegations(isPositive, ba, posHash, negHash));
			out = new And<>(newPhis);
			posHash.put(key, out);
			return out;
		} else {
			if (negHash.containsKey(key))
				return negHash.get(key);
			List<LTLFormula<P, S>> newPhis = new ArrayList<>();
			for (LTLFormula<P, S> phi : conjuncts)
				newPhis.add(phi.pushNegations(isPositive, ba, posHash, negHash));
			out = new Or<>(newPhis);
			negHash.put(key, out);
			return out;
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		boolean isFirst = true;
		for (LTLFormula<P, S> phi : conjuncts) {
			if (!isFirst)
				sb.append(" && ");

			phi.toString(sb);
			isFirst = false;
		}
		sb.append(")");
	}

	// @Override
	// public SAFA<P, S> getSAFANew(BooleanAlgebra<P, S> ba) {
	// ArrayList<LTLFormula<P, S>> c = new ArrayList<>(conjuncts);
	// SAFA<P, S> safa = c.get(0).getSAFANew(ba);
	// for (int i = 1; i < c.size(); i++)
	// safa = safa.intersectionWith(c.get(i).getSAFANew(ba), ba);
	//
	// return safa;
	// }

	@Override
	public int getSize() {
		int size = 1;
		for (LTLFormula<P, S> c : conjuncts)
			size += c.getSize();
		return size;
	}

}

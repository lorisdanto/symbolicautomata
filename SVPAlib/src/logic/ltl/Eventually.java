package logic.ltl;

import java.util.Collection;
import java.util.HashMap;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public class Eventually<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> phi;

	public Eventually(LTLFormula<P, S> phi) {
		super();
		this.phi = phi;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phi == null) ? 0 : phi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Eventually))
			return false;
		@SuppressWarnings("unchecked")
		Eventually<P, S> other = (Eventually<P, S>) obj;
		if (phi == null) {
			if (other.phi != null)
				return false;
		} else if (!phi.equals(other.phi))
			return false;
		return true;
	}

	@Override
	protected PositiveBooleanExpression accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, PositiveBooleanExpression> formulaToState, Collection<SAFAInputMove<P, S>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToState.containsKey(this))
			return formulaToState.get(this);

		// Compute transitions for children
		PositiveBooleanExpression phiState = phi.accumulateSAFAStatesTransitions(formulaToState, moves, finalStates,
				ba);

		// Update hash tables
		int id = formulaToState.size();
		PositiveBooleanExpression initialState = boolexpr.MkOr(boolexpr.MkState(id), phiState);
		formulaToState.put(this, initialState);

		// delta(F phi, true) = phi \/ F phi
		moves.add(new SAFAInputMove<P, S>(id, initialState, ba.True()));

		if (this.isFinalState())
			finalStates.add(id);

		return initialState;
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba,
			HashMap<String, LTLFormula<P, S>> posHash, HashMap<String, LTLFormula<P, S>> negHash) {
		String key = this.toString();

		LTLFormula<P, S> out = new False<>();

		if (isPositive) {
			if (posHash.containsKey(key)) {
				return posHash.get(key);
			}
			out = new Eventually<>(phi.pushNegations(isPositive, ba, posHash, negHash));
			posHash.put(key, out);
			return out;
		} else {
			if (negHash.containsKey(key))
				return negHash.get(key);
			out = new Globally<>(phi.pushNegations(isPositive, ba, posHash, negHash));
			negHash.put(key, out);
			return out;
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(F ");
		phi.toString(sb);
		sb.append(")");
	}

	// @Override
	// public SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba) {
	// BooleanExpressionFactory<PositiveBooleanExpression> boolexpr =
	// SAFA.getBooleanExpressionFactory();
	//
	// SAFA<P,S> phiSafa = phi.getSAFANew(ba);
	// int formulaId = phiSafa.getMaxStateId()+1;
	//
	// PositiveBooleanExpression initialState =
	// boolexpr.MkOr(boolexpr.MkState(formulaId), phiSafa.getInitialState());
	// Collection<Integer> finalStates = phiSafa.getFinalStates();
	//
	// // Copy all transitions (with proper renaming for aut2)
	// Collection<SAFAInputMove<P, S>> transitions = new
	// ArrayList<SAFAInputMove<P, S>>(phiSafa.getInputMoves());
	// transitions.add(new SAFAInputMove<>(formulaId, initialState, ba.True()));
	//
	// return SAFA.MkSAFA(transitions, initialState, finalStates, ba, false,
	// true);
	// }

	@Override
	public int getSize() {
		return 1 + phi.getSize();
	}
}

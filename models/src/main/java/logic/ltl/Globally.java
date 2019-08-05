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

public class Globally<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> phi;

	public Globally(LTLFormula<P, S> phi) {
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
		if (!(obj instanceof Globally))
			return false;
		@SuppressWarnings("unchecked")
		Globally<P, S> other = (Globally<P, S>) obj;
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
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba, HashSet<Integer> states) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToState.containsKey(this))
			return formulaToState.get(this);

		// Compute transitions for children
		PositiveBooleanExpression phiState = phi.accumulateSAFAStatesTransitions(formulaToState, moves, finalStates,
				ba, states);

		// Update hash tables
		// New state for G X phi		
		
		int idXGphi = states.size();
		states.add(idXGphi);
		PositiveBooleanExpression initialState = boolexpr.MkAnd(boolexpr.MkState(idXGphi), phiState);		
		formulaToState.put(this, initialState);
		
		PositiveBooleanExpression nextState = boolexpr.MkOr( boolexpr.MkState(0), boolexpr.MkAnd(boolexpr.MkState(idXGphi),phiState));
		finalStates.add(0);
								
		// delta(G phi, p) = phi /\ G phi
		moves.add(new SAFAInputMove<P, S>(idXGphi, nextState, ba.True()));

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
			out = new Globally<>(phi.pushNegations(isPositive, ba, posHash, negHash));
			posHash.put(key, out);
			return out;
		} else {
			if (negHash.containsKey(key))
				return negHash.get(key);
			out = new Eventually<>(phi.pushNegations(isPositive, ba, posHash, negHash));
			negHash.put(key, out);
			return out;
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(G ");
		phi.toString(sb);
		sb.append(")");
	}

	@Override
	public int getSize() {
		return 1 + phi.getSize();
	}
}

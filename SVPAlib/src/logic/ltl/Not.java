package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.sat4j.specs.TimeoutException;

import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public class Not<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> phi;

	public Not(LTLFormula<P, S> phi) {
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
		if (!(obj instanceof Not))
			return false;
		@SuppressWarnings("unchecked")
		Not<P, S> other = (Not<P, S>) obj;
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

		// If I already visited avoid recomputing
		if (formulaToState.containsKey(this))
			return formulaToState.get(this);
		
		throw new UnsupportedOperationException("At this point the formula should be in negation normal form.");
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba,
			HashMap<String, LTLFormula<P, S>> posHash, HashMap<String, LTLFormula<P, S>> negHash) throws TimeoutException {
		return phi.pushNegations(!isPositive, ba, posHash, negHash);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("!");
		phi.toString(sb);
	}
	
	@Override
	public int getSize() {
		return 1+phi.getSize();
	}
}

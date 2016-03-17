package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFAInputMove;
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
	protected <E extends BooleanExpression>void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, boolexpr);

		// delta(G phi, p) = delta(phi, p) /\ G phi
		Collection<SAFAInputMove<P, S, E>> phiMoves = moves.get(formulaToStateId.get(phi));
		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
		for (SAFAInputMove<P, S, E> move : phiMoves)
			newMoves.add(new SAFAInputMove<>(id, boolexpr.MkAnd(move.to, boolexpr.MkState(id)), move.guard));

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
			return new Globally<>(phi.pushNegations(isPositive,ba));
		else
			return new Eventually<>(phi.pushNegations(isPositive,ba));
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("(G ");
		phi.toString(sb);	
		sb.append(")");
	}
}

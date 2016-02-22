package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveId;
import theory.BooleanAlgebra;

public class Next<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> phi;

	public Next(LTLFormula<P, S> phi) {
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
		if (!(obj instanceof Next))
			return false;
		@SuppressWarnings("unchecked")
		Next<P, S> other = (Next<P, S>) obj;
		if (phi == null) {
			if (other.phi != null)
				return false;
		} else if (!phi.equals(other.phi))
			return false;
		return true;
	}

	@Override
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		// delta(X phi, true) = phi
		int phiId = formulaToStateId.get(phi);
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		newMoves.add(new SAFAInputMove<P, S>(id, new PositiveId(phiId), ba.True()));

		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}
}

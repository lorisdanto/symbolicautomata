package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.SumOfProducts;
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
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, LTLFormula<P, S>> idToFormula, HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);
		idToFormula.put(id, this);

		// Compute transitions for children
		phi.accumulateSAFAStatesTransitions(formulaToStateId, idToFormula, moves, finalStates, ba);

		// delta(G phi, p) = delta(phi, p) /\ G phi
		Collection<SAFAInputMove<P, S>> phiMoves = moves.get(phi);
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		for (SAFAInputMove<P, S> move : phiMoves)
			newMoves.add(new SAFAInputMove<P, S>(id, move.to.and(new SumOfProducts(id)), move.guard));

		finalStates.add(id);
		
		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return true;
	}
}

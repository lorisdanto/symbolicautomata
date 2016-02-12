package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.SumOfProducts;
import theory.BooleanAlgebra;

public class Predicate<P, S> extends LTLFormula<P, S> {

	protected P predicate;

	public Predicate(P predicate) {
		super();
		this.predicate = predicate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Predicate))
			return false;
		@SuppressWarnings("unchecked")
		Predicate<P,S> other = (Predicate<P,S>) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (predicate != other.predicate)
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
		
		// Create true state
		True<P,S> t = new True<>();
		t.accumulateSAFAStatesTransitions(formulaToStateId, idToFormula, moves, finalStates, ba);
		
		// delta([p], p) = true
		int trueId = formulaToStateId.get(t);		
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		newMoves.add(new SAFAInputMove<P, S>(id, new SumOfProducts(trueId), predicate));		

		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}
	
}

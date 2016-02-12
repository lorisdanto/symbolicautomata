package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.SumOfProducts;
import theory.BooleanAlgebra;

public class True<P, S> extends LTLFormula<P, S> {

	public True() {
		super();
	}

	@Override
	public int hashCode() {
		return 11;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof True))
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
		
		// delta(True, true) = True		
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		newMoves.add(new SAFAInputMove<P, S>(id, new SumOfProducts(id), ba.True()));		

		// True is a final state
		finalStates.add(id);
		
		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return true;
	}
}

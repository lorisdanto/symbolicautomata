package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
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
		Predicate<P, S> other = (Predicate<P, S>) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (predicate != other.predicate)
			return false;
		return true;
	}

	@Override
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves, Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba, boolean normalize) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Create true state
		True<P, S> t = new True<>();
		t.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, normalize);

		// delta([p], p) = true
		int trueId = formulaToStateId.get(t);
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		newMoves.add(new SAFAInputMove<>(id, boolexpr.MkState(trueId), predicate));

		moves.put(id, newMoves);
		
		if(this.isFinalState())
			finalStates.add(id);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba,
			HashMap<String, LTLFormula<P, S>> posHash, HashMap<String, LTLFormula<P, S>> negHash) {
		if (isPositive)
			return this;
		else
			return new Predicate<>(ba.MkNot(this.predicate));
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(predicate.toString());
	}

	@Override
	public SAFA<P, S> getSAFANew(BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		int formulaId = 1;

		PositiveBooleanExpression initialState = boolexpr.MkState(1);
		Collection<Integer> finalStates = new HashSet<>();
		finalStates.add(2);

		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>();
		transitions.add(new SAFAInputMove<>(formulaId, boolexpr.MkState(2), this.predicate));

		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
	}
	
	@Override
	public int getSize() {
		return 1;
	}
}

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
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves, Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba, boolean normalize) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, normalize);

		// delta(G phi, p) = delta(phi, p) /\ G phi
		Collection<SAFAInputMove<P, S>> phiMoves = moves.get(formulaToStateId.get(phi));
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		P not = ba.True();
		for (SAFAInputMove<P, S> move : phiMoves) {
			newMoves.add(new SAFAInputMove<P, S>(id, boolexpr.MkAnd(move.to, boolexpr.MkState(id)), move.guard));
			not = ba.MkAnd(not, ba.MkNot(move.guard));
		}

		if (!normalize && ba.IsSatisfiable(not))
			newMoves.add(new SAFAInputMove<P, S>(id, boolexpr.False(), not));

		moves.put(id, newMoves);
		
		if(this.isFinalState())
			finalStates.add(id);
	}

	@Override
	protected boolean isFinalState() {
		return true;
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
	public SAFA<P, S> getSAFANew(BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		SAFA<P, S> phiSafa = phi.getSAFANew(ba);
		int formulaId = phiSafa.getMaxStateId() + 1;

		PositiveBooleanExpression initialState = boolexpr.MkAnd(boolexpr.MkState(formulaId), phiSafa.getInitialState());
		Collection<Integer> finalStates = new HashSet<>(phiSafa.getFinalStates());
		finalStates.add(formulaId);

		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>(phiSafa.getInputMoves());
		transitions.add(new SAFAInputMove<>(formulaId, initialState, ba.True()));

		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
	}
	
	@Override
	public int getSize() {
		return 1+phi.getSize();
	}
}

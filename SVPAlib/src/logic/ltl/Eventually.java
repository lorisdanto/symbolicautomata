package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
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
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		// delta(F phi, p) = delta(phi, p) 
		// delta(F phi, true) = F phi
		Collection<SAFAInputMove<P, S>> phiMoves = moves.get(formulaToStateId.get(phi));
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		P not =ba.True();
		for (SAFAInputMove<P, S> move : phiMoves){
			newMoves.add(new SAFAInputMove<P, S>(id, boolexpr.MkOr(move.to, boolexpr.MkState(id)), move.guard));
			not = ba.MkAnd(not, ba.MkNot(move.guard));
		}

		if (ba.IsSatisfiable(not))
			newMoves.add(new SAFAInputMove<P, S>(id, boolexpr.MkState(id), not));
		
		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		if(isPositive)
			return new Eventually<>(phi.pushNegations(isPositive,ba));
		else
			return new Globally<>(phi.pushNegations(isPositive,ba));
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("(F ");
		phi.toString(sb);	
		sb.append(")");
	}
	
	@Override
	public SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		SAFA<P,S> phiSafa = phi.getSAFANew(ba);
		int formulaId = phiSafa.getMaxStateId()+1;
						
		PositiveBooleanExpression initialState = boolexpr.MkOr(boolexpr.MkState(formulaId), phiSafa.getInitialState());
		Collection<Integer> finalStates = phiSafa.getFinalStates();
		
		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>(phiSafa.getInputMoves());
		transitions.add(new SAFAInputMove<>(formulaId, initialState, ba.True()));
		
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
	}
}

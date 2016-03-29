package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
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
	protected <E extends BooleanExpression> void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
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

		// delta(F phi, p) = delta(phi, p) 
		// delta(F phi, true) = F phi
		Collection<SAFAInputMove<P, S, E>> phiMoves = moves.get(formulaToStateId.get(phi));
		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
		for (SAFAInputMove<P, S, E> move : phiMoves)
			newMoves.add(new SAFAInputMove<P, S, E>(id, move.to, move.guard));
		
		newMoves.add(new SAFAInputMove<P, S, E>(id, boolexpr.MkState(id), ba.True()));
		
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
	public <E extends BooleanExpression> SAFA<P,S,E> getSAFANew(BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr) {
		
		SAFA<P,S,E> phiSafa = phi.getSAFANew(ba, boolexpr);
		int formulaId = phiSafa.getMaxStateId()+1;
						
		E initialState = boolexpr.MkOr(boolexpr.MkState(formulaId), phiSafa.getInitialState());		
		Collection<Integer> finalStates = phiSafa.getFinalStates();
		
		// Copy all transitions (with proper renaming for aut2)
		Collection<SAFAInputMove<P, S, E>> transitions = new ArrayList<SAFAInputMove<P, S, E>>(phiSafa.getInputMoves());
		transitions.add(new SAFAInputMove<>(formulaId, initialState, ba.True()));
		
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba, boolexpr);
	}
}

package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
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
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba, boolean normalize) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba,normalize);

		// delta(X phi, true) = phi
		int phiId = formulaToStateId.get(phi);
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		newMoves.add(new SAFAInputMove<P, S>(id, boolexpr.MkState(phiId), ba.True()));

		moves.put(id, newMoves);
		
		if(this.isFinalState())
			finalStates.add(id);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba, HashMap<String, LTLFormula<P,S>> posHash, HashMap<String, LTLFormula<P,S>> negHash){
		String key = this.toString();

		LTLFormula<P, S> out = new False<>();

		if (isPositive) {
			if (posHash.containsKey(key)) {
				return posHash.get(key);
			}
			out = new Next<>(phi.pushNegations(isPositive,ba,posHash,negHash));
			posHash.put(key, out);
			return out;
		} else {
			if(negHash.containsKey(key))
				return negHash.get(key);
			out = new Next<>(phi.pushNegations(isPositive,ba,posHash,negHash));
			negHash.put(key, out);
			return out;
		}
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("X");
		phi.toString(sb);	
	}
	
	@Override
	public SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		SAFA<P,S> phiSafa = phi.getSAFANew(ba);
		int formulaId = phiSafa.getMaxStateId()+1;
						
		PositiveBooleanExpression initialState = boolexpr.MkState(formulaId);
		Collection<Integer> finalStates = phiSafa.getFinalStates();
		
		Collection<SAFAInputMove<P, S>> transitions = new ArrayList<SAFAInputMove<P, S>>(phiSafa.getInputMoves());
		transitions.add(new SAFAInputMove<>(formulaId, phiSafa.getInitialState(), ba.True()));
		
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
	}
	
	@Override
	public int getSize() {
		return 1+phi.getSize();
	}
}

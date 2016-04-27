package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import theory.BooleanAlgebra;

public class False<P, S> extends LTLFormula<P, S> {

	public False() {
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
		if (!(obj instanceof False))
			return false;
		return true;
	}		
	
	@Override
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba, boolean normalize) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);
		
		// delta(False, _) = nothing		
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		
		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba, HashMap<String, LTLFormula<P,S>> posHash, HashMap<String, LTLFormula<P,S>> negHash){
		if(isPositive){
			return this;
		}
		else{ 
			String key = this.toString();
			if (negHash.containsKey(key)) {
				return negHash.get(key);
			} else {
				LTLFormula<P, S> out = new True<>();
				negHash.put(key, out);
				return out;
			}			
		}
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("false");
	}
	
	@Override
	public SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba) {
		return SAFA.getEmptySAFA(ba);
	}
	
	@Override
	public int getSize() {
		return 1;
	}
}

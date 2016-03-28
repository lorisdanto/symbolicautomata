package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFAInputMove;
import theory.BooleanAlgebra;

public class Or<P, S> extends LTLFormula<P, S> {

	protected List<LTLFormula<P, S>> disjuncts;

	public Or(LTLFormula<P, S> left, LTLFormula<P, S> right) {
		super();
		disjuncts = new ArrayList<>();
		disjuncts.add(left);
		disjuncts.add(right);
	}

	public Or(List<LTLFormula<P, S>> c) {
		super();
		disjuncts = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disjuncts == null) ? 0 : disjuncts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Or))
			return false;
		Or<?, ?> other = (Or<?, ?>) obj;
		if (disjuncts == null) {
			if (other.disjuncts != null)
				return false;
		} else if (!disjuncts.equals(other.disjuncts))
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
		ArrayList<Integer> ids = new ArrayList<>();
		ArrayList<Collection<SAFAInputMove<P, S, E>>> disjMoves = new ArrayList<>();
		// Compute transitions for children
		for (LTLFormula<P, S> phi : disjuncts) {
			phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, boolexpr);
			int phiId=formulaToStateId.get(phi);
			ids.add(phiId);
			disjMoves.add(moves.get(phiId));
		}

		// delta(l or r, p) = delta(l, p) 
		// delta(l or r, p) = delta(r, p) 
		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
		
		for(Collection<SAFAInputMove<P, S, E>> phiMoves : disjMoves)
			for (SAFAInputMove<P, S, E> phiMove : phiMoves)
				newMoves.add(new SAFAInputMove<>(id, phiMove.to, phiMove.guard));

		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		boolean isF = true;
		for (LTLFormula<P, S> phi : disjuncts)
			isF = isF || phi.isFinalState();
		return isF;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		List<LTLFormula<P, S>> newPhis =new ArrayList<>();
		for(LTLFormula<P, S> phi:disjuncts)
			newPhis.add(phi.pushNegations(isPositive,ba));
		
		if(isPositive)
			return new Or<>(newPhis);
		else
			return new And<>(newPhis);
	}
	
	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		boolean isFirst = true;
		for (LTLFormula<P, S> phi : disjuncts){
			if(!isFirst)
				sb.append(" | ");
				
			phi.toString(sb);							
			isFirst=false;
		}
		sb.append(")");
	}
}

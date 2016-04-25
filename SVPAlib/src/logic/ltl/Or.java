package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
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

//	@Override
//	protected <E extends BooleanExpression> void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
//			HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves,
//			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba,
//			BooleanExpressionFactory<E> boolexpr) {
//
//		// If I already visited avoid recomputing
//		if (formulaToStateId.containsKey(this))
//			return;
//
//		// Update hash tables
//		int id = formulaToStateId.size();
//		formulaToStateId.put(this, id);
//
//		// Compute transitions for children
//		ArrayList<Integer> ids = new ArrayList<>();
//		ArrayList<Collection<SAFAInputMove<P, S, E>>> disjMoves = new ArrayList<>();
//		// Compute transitions for children
//		for (LTLFormula<P, S> phi : disjuncts) {
//			phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, boolexpr);
//			int phiId=formulaToStateId.get(phi);
//			ids.add(phiId);
//			disjMoves.add(moves.get(phiId));
//		}
//
//		// delta(l or r, p) = delta(l, p) 
//		// delta(l or r, p) = delta(r, p) 
//		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
//		
//		for(Collection<SAFAInputMove<P, S, E>> phiMoves : disjMoves)
//			for (SAFAInputMove<P, S, E> phiMove : phiMoves)
//				newMoves.add(new SAFAInputMove<>(id, phiMove.to, phiMove.guard));
//
//		moves.put(id, newMoves);
//	}
	
	@Override
	protected void accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves, Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		ArrayList<Integer> ids = new ArrayList<>();
		ArrayList<Collection<SAFAInputMove<P, S>>> conjMoves = new ArrayList<>();
		// Compute transitions for children
		for (LTLFormula<P, S> phi : disjuncts) {
			phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);
			int phiId=formulaToStateId.get(phi);
			ids.add(phiId);
			conjMoves.add(moves.get(phiId));
		}

		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		accumulateMovesOr(ba.True(), boolexpr.False(), newMoves, conjMoves, ba, id, 0);

		moves.put(id, newMoves);
	}

	protected void accumulateMovesOr(P currPred, PositiveBooleanExpression currToExpr,
			Collection<SAFAInputMove<P, S>> newMoves, ArrayList<Collection<SAFAInputMove<P, S>>> conjMoves,
			BooleanAlgebra<P, S> ba, int idFrom, int n) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		if (n == conjMoves.size())
			newMoves.add(new SAFAInputMove<P, S>(idFrom, currToExpr, currPred));
		else
			for (SAFAInputMove<P, S> m : conjMoves.get(n)) {
				P pred = ba.MkAnd(currPred, m.guard);
				if (ba.IsSatisfiable(pred))
					accumulateMovesOr(pred, boolexpr.MkOr(currToExpr, m.to), newMoves, conjMoves, ba, idFrom,
							n + 1);
			}
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
	
	@Override
	public SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba) {
		ArrayList<LTLFormula<P, S>> c = new ArrayList<>(disjuncts);
		SAFA<P,S> safa = c.get(0).getSAFANew(ba);
		for(int i = 1; i<c.size();i++)
			safa = safa.unionWith(c.get(i).getSAFANew(ba),ba);

		return safa;
	}
}

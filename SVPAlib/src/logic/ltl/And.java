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

public class And<P, S> extends LTLFormula<P, S> {


	protected List<LTLFormula<P, S>> conjuncts;

	public And(LTLFormula<P, S> left, LTLFormula<P, S> right) {
		super();
		conjuncts = new ArrayList<>();
		conjuncts.add(left);
		conjuncts.add(right);
	}

	public And(List<LTLFormula<P, S>> c) {
		super();
		conjuncts = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conjuncts == null) ? 0 : conjuncts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof And))
			return false;
		And<?, ?> other = (And<?, ?>) obj;
		if (conjuncts == null) {
			if (other.conjuncts != null)
				return false;
		} else if (!conjuncts.equals(other.conjuncts))
			return false;
		return true;
	}

	@Override
	protected <E extends BooleanExpression> void accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves, Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		ArrayList<Integer> ids = new ArrayList<>();
		ArrayList<Collection<SAFAInputMove<P, S, E>>> conjMoves = new ArrayList<>();
		// Compute transitions for children
		for (LTLFormula<P, S> phi : conjuncts) {
			phi.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, boolexpr);
			int phiId=formulaToStateId.get(phi);
			ids.add(phiId);
			conjMoves.add(moves.get(phiId));
		}

		Collection<SAFAInputMove<P, S, E>> newMoves = new LinkedList<>();
		accumulateMoves(ba.True(), boolexpr.True(), newMoves, conjMoves, ba, boolexpr, id, 0);

		moves.put(id, newMoves);
	}

	private <E extends BooleanExpression> void accumulateMoves(P currPred, E currToExpr,
			Collection<SAFAInputMove<P, S, E>> newMoves, ArrayList<Collection<SAFAInputMove<P, S, E>>> conjMoves,
			BooleanAlgebra<P, S> ba, BooleanExpressionFactory<E> boolexpr, int idFrom, int n) {
		if (n == conjMoves.size())
			newMoves.add(new SAFAInputMove<P, S, E>(idFrom, currToExpr, currPred));
		else
			for (SAFAInputMove<P, S, E> m : conjMoves.get(n)) {
				P pred = ba.MkAnd(currPred, m.guard);
				if (ba.IsSatisfiable(pred))
					accumulateMoves(pred, boolexpr.MkAnd(currToExpr, m.to), newMoves, conjMoves, ba, boolexpr, idFrom,
							n + 1);
			}
	}

	@Override
	protected boolean isFinalState() {
		boolean isF = true;
		for (LTLFormula<P, S> phi : conjuncts)
			isF = isF && phi.isFinalState();
		return isF;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		List<LTLFormula<P, S>> newPhis =new ArrayList<>();
		for(LTLFormula<P, S> phi:conjuncts)
			newPhis.add(phi.pushNegations(isPositive,ba));
		
		if(isPositive)
			return new And<>(newPhis);
		else
			return new Or<>(newPhis);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		boolean isFirst = true;
		for (LTLFormula<P, S> phi : conjuncts){
			if(!isFirst)
				sb.append(" && ");
				
			phi.toString(sb);							
			isFirst=false;
		}
		sb.append(")");
	}

}

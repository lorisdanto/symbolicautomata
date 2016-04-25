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

public class Until<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> left, right;

	public Until(LTLFormula<P, S> left, LTLFormula<P, S> right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Until))
			return false;
		@SuppressWarnings("unchecked")
		Until<P, S> other = (Until<P, S>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves, Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		left.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);
		right.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		// delta(l U r, p) = delta(l, p) and lUr
		// delta(l U r, p) = delta(r, p)
		int leftId = formulaToStateId.get(left);
		int rightId = formulaToStateId.get(right);
		Collection<SAFAInputMove<P, S>> leftMoves = moves.get(leftId);
		Collection<SAFAInputMove<P, S>> rightMoves = moves.get(rightId);

		Collection<SAFAInputMove<P, S>> untMovesL = new ArrayList<>();
		Collection<SAFAInputMove<P, S>> untMovesR = new ArrayList<>();
		P leftoverL = ba.True();
		P leftoverR = ba.True();

		Collection<SAFAInputMove<P, S>> untMoves = new ArrayList<>();

		for (SAFAInputMove<P, S> leftMove : leftMoves) {
			untMovesL.add(new SAFAInputMove<>(id, boolexpr.MkAnd(leftMove.to, boolexpr.MkState(id)), leftMove.guard));
			leftoverL = ba.MkAnd(leftoverL, ba.MkNot(leftMove.guard));
		}

		for (SAFAInputMove<P, S> rightMove : rightMoves) {
			untMovesR.add(new SAFAInputMove<>(id, rightMove.to, rightMove.guard));
			leftoverR = ba.MkAnd(leftoverR, ba.MkNot(rightMove.guard));
			P conj = ba.MkAnd(leftoverL, rightMove.guard);
			if (ba.IsSatisfiable(conj))
				untMoves.add(new SAFAInputMove<P, S>(id, rightMove.to, conj));
		}

		for (SAFAInputMove<P, S> lMove : leftMoves){
			for (SAFAInputMove<P, S> rMove : rightMoves) {
				P conj = ba.MkAnd(lMove.guard, rMove.guard);
				if (ba.IsSatisfiable(conj))
					untMoves.add(new SAFAInputMove<P, S>(id, boolexpr.MkOr(lMove.to, rMove.to), conj));
			}
			P conj = ba.MkAnd(lMove.guard, leftoverR);
			if (ba.IsSatisfiable(conj))
				untMoves.add(new SAFAInputMove<P, S>(id, lMove.to, conj));
		}
		
		moves.put(id, untMoves);
		// throw new IllegalArgumentException("Not finished this yet");
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}

	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		if (isPositive)
			return new Until<>(left.pushNegations(isPositive, ba), right.pushNegations(isPositive, ba));
		else {
			LTLFormula<P, S> rightNeg = right.pushNegations(isPositive, ba);
			return new WeakUntil<>(rightNeg, new And<>(left.pushNegations(isPositive, ba), rightNeg));
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		left.toString(sb);
		sb.append(" U ");
		right.toString(sb);
		sb.append(")");
	}

	@Override
	public SAFA<P, S> getSAFANew(BooleanAlgebra<P, S> ba) {
		// TODO Auto-generated method stub
		return null;
	}
}

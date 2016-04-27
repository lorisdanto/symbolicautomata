package logic.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public class WeakUntil<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> left, right;

	public WeakUntil(LTLFormula<P, S> left, LTLFormula<P, S> right) {
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
		if (!(obj instanceof WeakUntil))
			return false;
		@SuppressWarnings("unchecked")
		WeakUntil<P, S> other = (WeakUntil<P, S>) obj;
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
			BooleanAlgebra<P, S> ba, boolean normalize) {
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		left.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, normalize);
		right.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba, normalize);

		// delta(l W r, p) = delta(l, p) and lWr
		// delta(l W r, p) = delta(r, p)
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
		if (!normalize) {
			untMoves.addAll(untMovesL);
			untMoves.addAll(untMovesR);
		} else {
			for (SAFAInputMove<P, S> lMove : leftMoves) {
				for (SAFAInputMove<P, S> rMove : rightMoves) {
					P conj = ba.MkAnd(lMove.guard, rMove.guard);
					if (ba.IsSatisfiable(conj))
						untMoves.add(new SAFAInputMove<P, S>(id, boolexpr.MkOr(lMove.to, rMove.to), conj));
				}
				P conj = ba.MkAnd(lMove.guard, leftoverR);
				if (ba.IsSatisfiable(conj))
					untMoves.add(new SAFAInputMove<P, S>(id, lMove.to, conj));
			}
		}

		moves.put(id, untMoves);

		// Weak until are final states (unlike regular until)
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
			out = new WeakUntil<>(left.pushNegations(isPositive, ba, posHash, negHash),
					right.pushNegations(isPositive, ba, posHash, negHash));
			posHash.put(key, out);
			return out;
		} else {
			if (negHash.containsKey(key))
				return negHash.get(key);
			LTLFormula<P, S> rightNeg = right.pushNegations(isPositive, ba, posHash, negHash);
			out = new Until<>(rightNeg, new And<>(left.pushNegations(isPositive, ba, posHash, negHash), rightNeg));
			negHash.put(key, out);
			return out;
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		left.toString(sb);
		sb.append(" W ");
		right.toString(sb);
		sb.append(")");
	}

	@Override
	public SAFA<P, S> getSAFANew(BooleanAlgebra<P, S> ba) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getSize() {
		return 1 + left.getSize() + right.getSize();
	}
}

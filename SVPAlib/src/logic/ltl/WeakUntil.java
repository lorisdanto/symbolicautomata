package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
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
	protected  void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
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
		left.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);
		right.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		// delta(l W r, p) = delta(l, p) and lWr
		// delta(l W r, p) = delta(r, p)
		int leftId = formulaToStateId.get(left);
		int rightId = formulaToStateId.get(right);
		Collection<SAFAInputMove<P, S>> leftMoves = moves.get(leftId);
		Collection<SAFAInputMove<P, S>> rightMoves = moves.get(rightId);
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		for (SAFAInputMove<P, S> leftMove : leftMoves)
			newMoves.add(new SAFAInputMove<>(id, boolexpr.MkAnd(leftMove.to, boolexpr.MkState(id)), leftMove.guard));

		for (SAFAInputMove<P, S> rightMove : rightMoves)
			newMoves.add(new SAFAInputMove<>(id, rightMove.to, rightMove.guard));
		// Weak until are final states (unlike regular until)
		finalStates.add(id);

		moves.put(id, newMoves);
		throw new IllegalArgumentException("Not finished this yet");
	}

	@Override
	protected boolean isFinalState() {
		return true;
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		if(isPositive)
			return new WeakUntil<>(left.pushNegations(isPositive,ba), right.pushNegations(isPositive,ba));
		else{
			LTLFormula<P, S> rightNeg =right.pushNegations(isPositive,ba); 
			return new Until<>(rightNeg, 
					new And<>(left.pushNegations(isPositive,ba), rightNeg));
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
}

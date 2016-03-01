package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import automata.safa.BooleanExpression;

public class PositiveOr extends PositiveBooleanExpression {

	public PositiveBooleanExpression left, right;

	public PositiveOr(PositiveBooleanExpression left, PositiveBooleanExpression right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return left.hasModel(elements) || right.hasModel(elements);
	}

	@Override
	public BooleanExpression offset(int offset) {
		return new PositiveOr((PositiveBooleanExpression)left.offset(offset), (PositiveBooleanExpression)right.offset(offset));
	}

	@Override
	public Set<Integer> getStates() {
		Set<Integer> states = left.getStates();
		states.addAll(right.getStates());
		return states;
	}

	@Override
	public Object clone() {
		PositiveBooleanExpression cl = (PositiveBooleanExpression) left.clone();
		PositiveBooleanExpression cr = (PositiveBooleanExpression) right.clone();
		return new PositiveOr(cl, cr);
	}

	@Override
	public BooleanExpression substitute(Function<Integer, BooleanExpression> sigma) {
		return left.substitute(sigma).or(right.substitute(sigma));
	}
}

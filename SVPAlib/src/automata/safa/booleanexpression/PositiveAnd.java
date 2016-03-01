package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import automata.safa.BooleanExpression;

public class PositiveAnd extends PositiveBooleanExpression {

	public PositiveBooleanExpression left, right;

	public PositiveAnd(PositiveBooleanExpression left, PositiveBooleanExpression right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return left.hasModel(elements) && right.hasModel(elements);
	}

	@Override
	public BooleanExpression offset(int offset) {
		return new PositiveAnd((PositiveBooleanExpression)left.offset(offset), (PositiveBooleanExpression)right.offset(offset));
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
		return new PositiveAnd(cl, cr);
	}

	public BooleanExpression substitute(Function<Integer, BooleanExpression> sigma) {
		return left.substitute(sigma).and(right.substitute(sigma));
	}
}

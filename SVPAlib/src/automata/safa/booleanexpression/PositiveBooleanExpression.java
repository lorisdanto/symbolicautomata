package automata.safa.booleanexpression;

import java.util.Collection;

import automata.safa.BooleanExpression;

public abstract class PositiveBooleanExpression extends BooleanExpression {


	@Override
	public abstract boolean hasModel(Collection<Integer> elements);

	@Override
	public BooleanExpression or(BooleanExpression p1) {
		return null;
	}

	@Override
	public BooleanExpression and(BooleanExpression p1) {
		return null;
	}

	@Override
	public abstract BooleanExpression offset(int offset);

	// TODO equals clone...
}

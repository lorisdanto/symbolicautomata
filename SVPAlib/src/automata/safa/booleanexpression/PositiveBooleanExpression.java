package automata.safa.booleanexpression;

import java.util.Collection;

import automata.safa.BooleanExpression;

public abstract class PositiveBooleanExpression extends BooleanExpression {


	@Override
	public abstract boolean hasModel(Collection<Integer> elements);

	// TODO equals clone...
}

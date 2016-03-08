package automata.safa;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public abstract class BooleanExpression {	
	
	public abstract Set<Integer> getStates();
	
	public abstract BooleanExpression substitute(Function<Integer, BooleanExpression> sigma);

	@Override
	public abstract Object clone();
	
	public abstract boolean hasModel(Collection<Integer> elements);

	public abstract BooleanExpression or(BooleanExpression p1);

	public abstract BooleanExpression and(BooleanExpression p1);
	
	public abstract BooleanExpression offset(int offset);

}

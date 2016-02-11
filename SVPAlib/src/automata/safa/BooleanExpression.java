package automata.safa;

import java.util.Collection;
import java.util.Set;

public abstract class BooleanExpression {	
	
	public abstract Set<Integer> getStates();
	
	@Override
	public abstract Object clone();
	
	public abstract boolean hasModel(Collection<Integer> elements);

	public abstract BooleanExpression or(BooleanExpression p1);

	public abstract BooleanExpression and(BooleanExpression p1);
	
	public abstract BooleanExpression offset(int offset);
}

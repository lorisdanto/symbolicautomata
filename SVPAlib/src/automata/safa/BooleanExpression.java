package automata.safa;

import java.util.Collection;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

public abstract class BooleanExpression {	
	
	public abstract Set<Integer> getStates();
	
	public abstract <R> R apply(LatticeMorphism<BooleanExpression, R> f);

	@Override
	public abstract Object clone();
	
	public abstract boolean hasModel(Collection<Integer> elements);
	
	public abstract int getSize();
}

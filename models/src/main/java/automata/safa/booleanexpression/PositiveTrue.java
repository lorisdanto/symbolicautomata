package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;

public class PositiveTrue extends PositiveBooleanExpression {
	private static PositiveTrue instance = null;

	protected PositiveTrue() {
		super();
	}

	public static PositiveBooleanExpression getInstance() {
		if (instance == null) {
			instance = new PositiveTrue();
		}
		return instance;
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return true;
	}

	@Override
	public Set<Integer> getStates() {
		return new HashSet<>();
	}

	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		return f.True();
	}
	
	public String toString() {
		return "true";
	}

	@Override
	public Object clone() {
		return getInstance();
	}
	
	@Override
	public int getSize() {
		return 1;
	}
}

package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;

public class PositiveFalse extends PositiveBooleanExpression {
	private static PositiveFalse instance = null;

	protected PositiveFalse() {
		super();
	}

	public static PositiveBooleanExpression getInstance() {
		if (instance == null) {
			instance = new PositiveFalse();
		}
		return instance;
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return false;
	}

	@Override
	public Set<Integer> getStates() {
		return new HashSet<>();
	}

	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		return f.False();
	}
	
	public String toString() {
		return "false";
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

package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;

public class PositiveId extends PositiveBooleanExpression {

	public Integer state;

	public PositiveId(Integer state) {
		super();
		this.state = state;
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return elements.contains(state);
	}

	@Override
	public Set<Integer> getStates() {
		Set<Integer> states = new HashSet<>();
		states.add(state);
		return states;
	}

	@Override
	public Object clone() {
		return new PositiveId(state);
	}

	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		return f.apply(state);
	}

	public String toString() {
		return state.toString();
	}
	
	@Override
	public int getSize() {
		return 1;
	}
}

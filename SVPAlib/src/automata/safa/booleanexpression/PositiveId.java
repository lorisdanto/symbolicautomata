package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import automata.safa.BooleanExpression;

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
	public BooleanExpression offset(int offset) {
		return new PositiveId(state+offset);
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
	public BooleanExpression substitute(Function<Integer, BooleanExpression> sigma) {
		return sigma.apply(state);
	}
}

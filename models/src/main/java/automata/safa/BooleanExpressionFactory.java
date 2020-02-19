package automata.safa;

import java.util.function.Function;

public abstract class BooleanExpressionFactory<E> {
	public abstract E MkAnd(E left, E right);
	public abstract E MkOr(E left, E right);
	public abstract E True();
	public abstract E False();
	public abstract E MkState(int state);
	public BooleanExpressionMorphism<E> substitute(Function<Integer, E> sigma) {
		return new BooleanExpressionMorphism<E>(sigma, this);
	}
	public BooleanExpressionMorphism<E> offset(int offset) {
		return substitute((state) -> MkState(state + offset));
	}
}

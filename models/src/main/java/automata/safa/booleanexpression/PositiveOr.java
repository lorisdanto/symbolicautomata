package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;

public class PositiveOr extends PositiveBooleanExpression {

	public PositiveBooleanExpression left, right;

	public PositiveOr(PositiveBooleanExpression left, PositiveBooleanExpression right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return left.hasModel(elements) || right.hasModel(elements);
	}

	@Override
	public Set<Integer> getStates() {
		Set<Integer> states = left.getStates();
		states.addAll(right.getStates());
		return states;
	}

	@Override
	public Object clone() {
		PositiveBooleanExpression cl = (PositiveBooleanExpression) left.clone();
		PositiveBooleanExpression cr = (PositiveBooleanExpression) right.clone();
		return new PositiveOr(cl, cr);
	}

	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		return f.MkOr(f.apply(left), f.apply(right));
	}

	public String toString() {
		return left.toString() + " + " + right.toString();
	}
	
	@Override
	public int getSize() {
		return 1+left.getSize()+right.getSize();
	}
}

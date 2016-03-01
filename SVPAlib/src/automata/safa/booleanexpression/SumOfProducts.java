package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import automata.safa.BooleanExpression;

public class SumOfProducts extends BooleanExpression {

	protected List<List<Integer>> dnf;

	public SumOfProducts(List<List<Integer>> dnf) {
		super();
		this.dnf = dnf;
	}
	
	public SumOfProducts(Integer state) {
		super();
		this.dnf = new LinkedList<>();
		List<Integer> l = new LinkedList<>();
		l.add(state);
		this.dnf.add(l);
	}
	
	public SumOfProducts(boolean b) {
		super();
		this.dnf = new LinkedList<>();
		if (b) {
			this.dnf.add(new LinkedList<>());
		}
	}
	
	public List<List<Integer>> getCubes() {
		return this.dnf;
	}

	public Set<Integer> getStates() {
		HashSet<Integer> acc = new HashSet<Integer>();
		dnf.stream().forEach(acc::addAll);
		return acc;
	}
	
	@Override
	public Object clone() {
		List<List<Integer>> newDnf = new LinkedList<>();
		for (List<Integer> l : dnf)
			newDnf.add(new LinkedList<>(l));
		return new SumOfProducts(newDnf);
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		for (List<Integer> l : dnf)
			if (elements.containsAll(l))
				return true;

		return false;
	}

	@Override
	public BooleanExpression or(BooleanExpression p1) {
		if (!(p1 instanceof SumOfProducts))
			throw new IllegalArgumentException("can only interesect SumOfProducts with SumOfProducts");

		SumOfProducts p1c = (SumOfProducts) p1;
		List<List<Integer>> newDnf = new LinkedList<>(dnf);
		newDnf.addAll(p1c.dnf);

		return new SumOfProducts(newDnf);
	}

	@Override
	public BooleanExpression and(BooleanExpression p1) {
		if (!(p1 instanceof SumOfProducts))
			throw new IllegalArgumentException("can only interesect SumOfProducts with SumOfProducts");

		SumOfProducts p1c = (SumOfProducts) p1;

		List<List<Integer>> newDnf = new LinkedList<>();
		for (List<Integer> l1 : dnf)
			for (List<Integer> l2 : p1c.dnf) {
				List<Integer> l1concl2 = new LinkedList<>(l1);
				l1concl2.addAll(l2);
				newDnf.add(l1concl2);
			}

		return new SumOfProducts(newDnf);
	}

	@Override
	public BooleanExpression offset(int offset) {
		List<List<Integer>> newDnf = new LinkedList<>();
		for (List<Integer> l1 : dnf) {
			List<Integer> newl1 = new LinkedList<>();
			for(Integer s: l1){
				newl1.add(s+offset);
			}
			newDnf.add(newl1);
		}
		return new SumOfProducts(newDnf);
	}

	@Override
	public BooleanExpression substitute(Function<Integer, BooleanExpression> sigma) {
		BooleanExpression result = new SumOfProducts(false);
		for (List<Integer> cube : dnf) {
			BooleanExpression sigmaCube = new SumOfProducts(true);
			for(Integer literal : cube) {
				sigmaCube = sigmaCube.and(sigma.apply(literal));
			}
			result = result.or(sigmaCube);
		}
		return result;
	}

	// TODO equals clone...
}

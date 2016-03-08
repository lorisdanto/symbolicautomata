package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

import automata.safa.BooleanExpression;

public class SumOfProducts extends BooleanExpression {

	protected Collection<Collection<Integer>> dnf;

	public SumOfProducts(Collection<Collection<Integer>> dnf) {
		super();
		Collection<Collection<Integer>> antichain = new HashSet<>();
		for (Collection<Integer> cube1 : dnf) {
			boolean subsumed = false;
			for (Collection<Integer> cube2 : dnf) {
				if (cube1.size() < cube2.size()) {
					if (cube2.containsAll(cube1)) {
						subsumed = true;
						break;
					}
				}
			}
			if(!subsumed)
				antichain.add(cube1);
		}
		this.dnf = antichain;
	}

	public SumOfProducts(Integer state) {
		super();
		this.dnf = new HashSet<>();
		Collection<Integer> l = new HashSet<>();
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

	public Collection<Collection<Integer>> getCubes() {
		return this.dnf;
	}

	public Set<Integer> getStates() {
		HashSet<Integer> acc = new HashSet<Integer>();
		dnf.stream().forEach(acc::addAll);
		return acc;
	}

	@Override
	public Object clone() {
		Collection<Collection<Integer>> newDnf = new HashSet<>();
		for (Collection<Integer> l : dnf)
			newDnf.add(new HashSet<>(l));
		return new SumOfProducts(newDnf);
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		for (Collection<Integer> l : dnf)
			if (elements.containsAll(l))
				return true;

		return false;
	}

	@Override
	public BooleanExpression or(BooleanExpression p1) {
		if (!(p1 instanceof SumOfProducts))
			throw new IllegalArgumentException("can only interesect SumOfProducts with SumOfProducts");

		SumOfProducts p1c = (SumOfProducts) p1;
		Collection<Collection<Integer>> newDnf = new HashSet<>(dnf);
		newDnf.addAll(p1c.dnf);

		return new SumOfProducts(newDnf);
	}

	@Override
	public BooleanExpression and(BooleanExpression p1) {
		if (!(p1 instanceof SumOfProducts))
			throw new IllegalArgumentException("can only interesect SumOfProducts with SumOfProducts");

		SumOfProducts p1c = (SumOfProducts) p1;

		Collection<Collection<Integer>> newDnf = new HashSet<>();
		for (Collection<Integer> l1 : dnf)
			for (Collection<Integer> l2 : p1c.dnf) {
				Collection<Integer> l1concl2 = new HashSet<>(l1);
				l1concl2.addAll(l2);
				newDnf.add(l1concl2);
			}

		return new SumOfProducts(newDnf);
	}

	@Override
	public BooleanExpression offset(int offset) {
		Collection<Collection<Integer>> newDnf = new HashSet<>();
		for (Collection<Integer> l1 : dnf) {
			Collection<Integer> newl1 = new HashSet<>();
			for (Integer s : l1) {
				newl1.add(s + offset);
			}
			newDnf.add(newl1);
		}
		return new SumOfProducts(newDnf);
	}

	@Override
	public BooleanExpression substitute(Function<Integer, BooleanExpression> sigma) {
		BooleanExpression result = new SumOfProducts(false);
		for (Collection<Integer> cube : dnf) {
			BooleanExpression sigmaCube = new SumOfProducts(true);
			for (Integer literal : cube) {
				sigmaCube = sigmaCube.and(sigma.apply(literal));
			}
			result = result.or(sigmaCube);
		}
		return result;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean firstel = true;
		for (Collection<Integer> l : dnf) {
			if (!firstel)
				sb.append("+");
			firstel = false;
			sb.append("(");
			boolean first = true;
			for (Integer i : l) {
				if (!first)
					sb.append(",");
				sb.append(i);
				first = false;
			}
			sb.append(")");
		}
		return sb.toString();
	}
	// TODO equals clone...
}

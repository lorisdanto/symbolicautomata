package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;

public class SumOfProducts extends BooleanExpression {

	protected Collection<Collection<Integer>> dnf;

	public SumOfProducts(Collection<Collection<Integer>> dnf) {
		super();
		Collection<Collection<Integer>> antichain = new HashSet<>();
		for (Collection<Integer> cube1 : dnf) {
			boolean subsumed = false;
			for (Collection<Integer> cube2 : dnf) {
				if (cube1.size() > cube2.size()) {
					if (cube1.containsAll(cube2)) {
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

	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		R result = f.False();
		for (Collection<Integer> cube : dnf) {
			R cubeImage = f.True();
			for (Integer state : cube) {
				cubeImage = f.MkAnd(cubeImage, f.apply(state));
			}
			result = f.MkOr(result, cubeImage);
		}
		return result;
	}
	
	@Override
	public int getSize() {
		// TODO maybe should keep it more complex
		return dnf.size();
	}
}

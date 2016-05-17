package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;
import theory.bdd.BDD;
import utilities.Timers;

public class BDDExpression extends BooleanExpression {

	public BDD bdd;
	
	public BDDExpression(BDD me) {
		bdd = me;
	}
		
	@Override
	public Set<Integer> getStates() {
		Set<Integer> states = new TreeSet<Integer>();
		BDD support = bdd.support();
		while (!support.isOne() && !support.isZero()) {
			states.add(support.var());
			support = support.high();
		}
		return states;
	}
	
	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		if (bdd.isOne()) {
			return f.True();
		} else if (bdd.isZero()) {
			return f.False();
		} else {
			return f.MkOr(f.MkAnd(f.apply(bdd.var()), f.apply(new BDDExpression(bdd.high()))),
						f.apply(new BDDExpression(bdd.low())));
		}
	}

	@Override
	public Object clone() {
		return new BDDExpression(bdd);
	}

	private static boolean hasModel(Collection<Integer> elements, BDD bdd) {
		if (bdd.isOne()) {
			return true;
		} else if (bdd.isZero()) {
			return false;
		} else {
			if (elements.contains(bdd.var())) {
				return hasModel(elements, bdd.high());
			} else {
				return hasModel(elements, bdd.low());
			}
		}
	}

	@Override
	public boolean hasModel(Collection<Integer> elements) {
		return hasModel(elements, bdd);
	}

	@Override
	public int getSize() {
		return bdd.nodeCount();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BDDExpression) {
			return this.bdd.equals(((BDDExpression) o).bdd);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.bdd.hashCode();
	}
}

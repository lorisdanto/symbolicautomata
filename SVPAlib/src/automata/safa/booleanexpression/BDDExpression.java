package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import automata.safa.BooleanExpression;
import automata.safa.LatticeMorphism;
import net.sf.javabdd.BDD;

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

	private static <R> R apply(LatticeMorphism<BooleanExpression, R> f, BDD bdd) {
		if (bdd.isOne()) {
			return f.True();
		} else if (bdd.isZero()) {
			return f.False();
		} else {
			return f.MkOr(f.MkAnd(f.apply(bdd.var()), apply(f, bdd.high())),
						apply(f, bdd.low()));
		}
	}
	
	@Override
	public <R> R apply(LatticeMorphism<BooleanExpression, R> f) {
		return apply(f, this.bdd);
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

}

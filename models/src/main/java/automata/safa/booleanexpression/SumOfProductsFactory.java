package automata.safa.booleanexpression;

import java.util.Collection;
import java.util.HashSet;

import automata.safa.BooleanExpressionFactory;

public class SumOfProductsFactory extends BooleanExpressionFactory<SumOfProducts> {
	
	private static SumOfProductsFactory instance = null;
	
	protected SumOfProductsFactory() { }

	public static SumOfProductsFactory getInstance() {
		if (instance == null) {
			instance = new SumOfProductsFactory();
		}
		return instance;
	}

	@Override
	public SumOfProducts MkAnd(SumOfProducts left, SumOfProducts right) {
		Collection<Collection<Integer>> newDnf = new HashSet<>();
		for (Collection<Integer> l1 : left.getCubes())
			for (Collection<Integer> l2 : right.getCubes()) {
				Collection<Integer> l1concl2 = new HashSet<>(l1);
				l1concl2.addAll(l2);
				newDnf.add(l1concl2);
			}

		return new SumOfProducts(newDnf);
	}

	@Override
	public SumOfProducts MkOr(SumOfProducts left, SumOfProducts right) {
		Collection<Collection<Integer>> newDnf = new HashSet<>(left.getCubes());
		newDnf.addAll(right.getCubes());

		return new SumOfProducts(newDnf);
	}

	@Override
	public SumOfProducts True() {
		return new SumOfProducts(true);
	}

	@Override
	public SumOfProducts False() {
		return new SumOfProducts(false);
	}

	@Override
	public SumOfProducts MkState(int state) {
		return new SumOfProducts(state);
	}

}

package automata.safa;

import org.sat4j.specs.TimeoutException;

import automata.safa.booleanexpression.BDDExpression;
import automata.safa.booleanexpression.BDDExpressionFactory;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;

public class BDDRelation extends SAFARelation {
	int size;
	public BDD similar;
	public BDDExpressionFactory factory;
	BooleanExpressionMorphism<BDDExpression> coerce;
	public BDDRelation(int size) {
		this.size = size;
		this.factory = new BDDExpressionFactory(size);
		similar = factory.True().bdd;
		coerce = new BooleanExpressionMorphism<>((x) -> factory.MkState(x), factory);
	}

	@Override
	public boolean isMember(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		BDD pair = coerce.apply(p).bdd.biimp(coerce.apply(q).bdd);
		return similar.and(pair.not()).isZero();
	}

	@Override
	public boolean add(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		BDD pair = coerce.apply(p).bdd.biimp(coerce.apply(q).bdd);
		similar = similar.and(pair);
		return !similar.isZero();
	}
}

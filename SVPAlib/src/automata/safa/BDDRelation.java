package automata.safa;

import org.sat4j.specs.TimeoutException;

import automata.safa.booleanexpression.BDDExpression;
import automata.safa.booleanexpression.BDDExpressionFactory;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;

public class BDDRelation extends SAFARelation {
	int left;
	int right;
	public BDD similar;
	public BDDExpressionFactory factory;
	BooleanExpressionMorphism<BDDExpression> leftCoerce;
	BooleanExpressionMorphism<BDDExpression> rightCoerce;
	public BDDRelation(int left, int right) {
		this.left = left;
		this.right = right;
		this.factory = new BDDExpressionFactory(left + right);
		similar = factory.True().bdd;
		leftCoerce = new BooleanExpressionMorphism<>((x) -> factory.MkState(x), factory);
		rightCoerce = new BooleanExpressionMorphism<>((x) -> factory.MkState(x + left + 1), factory);
	}

	@Override
	public boolean isMember(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		BDD pair = leftCoerce.apply(p).bdd.biimp(rightCoerce.apply(q).bdd);
		return similar.and(pair.not()).isZero();
	}

	@Override
	public boolean add(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		BDD pair = leftCoerce.apply(p).bdd.biimp(rightCoerce.apply(q).bdd);
		similar = similar.and(pair);
		return !similar.isZero();
	}
}

package automata.safa.booleanexpression;

import automata.safa.BooleanExpressionFactory;
import theory.bddalgebra.BDDSolver;

public class BDDExpressionFactory extends BooleanExpressionFactory<BDDExpression> {

	private BDDSolver solver;
	
	public BDDExpressionFactory(int num) {
		super();
		this.solver = new BDDSolver(num);
	}

	@Override
	public BDDExpression MkAnd(BDDExpression left, BDDExpression right) {
		return new BDDExpression (solver.MkAnd(left.bdd, right.bdd));
	}

	@Override
	public BDDExpression MkOr(BDDExpression left, BDDExpression right) {
		return new BDDExpression (solver.MkOr(left.bdd, right.bdd));
	}

	@Override
	public BDDExpression True() {
		return new BDDExpression (solver.True());
	}

	@Override
	public BDDExpression False() {
		return new BDDExpression (solver.False());
	}

	@Override
	public BDDExpression MkState(int state) {
		if (solver.factory.varNum() <= state) {
			solver.factory.setVarNum(state + 1);
		}
		return new BDDExpression (solver.factory.ithVar(state));
	}

}

package automata.safa.booleanexpression;

import automata.safa.BooleanExpressionFactory;
import utilities.Memo;
import utilities.Pair;

public class PositiveBooleanExpressionFactory extends BooleanExpressionFactory<PositiveBooleanExpression> {
	private Memo<Pair<PositiveBooleanExpression, PositiveBooleanExpression>,PositiveBooleanExpression> mkAnd;
	private Memo<Pair<PositiveBooleanExpression, PositiveBooleanExpression>,PositiveBooleanExpression> mkOr;
	private Memo<Integer,PositiveBooleanExpression> mkState;

	public PositiveBooleanExpressionFactory() {
		mkAnd = new Memo<Pair<PositiveBooleanExpression, PositiveBooleanExpression>,PositiveBooleanExpression>((x) -> new PositiveAnd(x.getFirst(), x.getSecond()));
		mkOr = new Memo<Pair<PositiveBooleanExpression, PositiveBooleanExpression>,PositiveBooleanExpression>((x) -> new PositiveOr(x.getFirst(), x.getSecond()));
		mkState = new Memo<Integer,PositiveBooleanExpression>((state) -> new PositiveId(state));
	}
	
	@Override
	public PositiveBooleanExpression MkAnd(PositiveBooleanExpression phi, PositiveBooleanExpression psi) {
		if (phi instanceof PositiveFalse || psi instanceof PositiveFalse) {
			return False();
		} else if (phi instanceof PositiveTrue) {
			return psi;
		} else if (psi instanceof PositiveTrue) {
			return phi;
		} else {
			return mkAnd.apply(new Pair<>(phi, psi));
		}
	}
	
	@Override
	public PositiveBooleanExpression MkOr(PositiveBooleanExpression phi, PositiveBooleanExpression psi) {
		if (phi instanceof PositiveTrue || psi instanceof PositiveTrue) {
			return True();
		} else if (phi instanceof PositiveFalse) {
			return psi;
		} else if (psi instanceof PositiveFalse) {
			return phi;
		} else {
			return mkOr.apply(new Pair<>(phi, psi));
		}
	}

	@Override
	public PositiveBooleanExpression MkState(int state) {
		return mkState.apply(state);
	}

	@Override
	public PositiveBooleanExpression True() {
		return PositiveTrue.getInstance();
	}

	@Override
	public PositiveBooleanExpression False() {
		return PositiveFalse.getInstance();
	}
}
package transducers.sst;

import java.util.LinkedList;
import java.util.List;

import theory.BooleanAlgebraSubst;

public class CharFunction<U, F, S> implements Token<U, F, S> {

	// This has to be made symbolic
	public F unaryFunction;

	public CharFunction(F unaryFunction) {
		super();
		this.unaryFunction = unaryFunction;
	}

	@Override
	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<U, F, S> ba) {
		List<S> out = new LinkedList<S>();
		out.add(ba.MkSubstFuncConst(unaryFunction, input));
		return out;
	}

	@Override
	public Token<U, F, S> rename(int offset) {
		return this;
	}

	@Override
	public String toString() {
		return "{" + unaryFunction.toString() + "}";
	}
}

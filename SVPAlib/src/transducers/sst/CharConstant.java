package transducers.sst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import theory.BooleanAlgebraSubst;

public class CharConstant<U, F, S> implements ConstantToken<U, F, S> {

	// This has to be made symbolic
	public S constant;

	public CharConstant(S constant) {
		super();
		this.constant = constant;
	}

	@Override
	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<U, F, S> ba) {

		List<S> out = new LinkedList<S>();
		out.add(constant);
		return out;
	}

	@Override
	public Token<U, F, S> rename(HashMap<Integer, Integer> varRename) {
		return this;
	}

	@Override
	public Token<U, F, S> rename(int offset) {
		return this;
	}

	@Override
	public String toString() {
		return constant.toString();
	}

}

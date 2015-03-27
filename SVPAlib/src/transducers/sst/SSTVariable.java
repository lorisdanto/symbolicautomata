package transducers.sst;

import java.util.HashMap;
import java.util.List;

import theory.BooleanAlgebraSubst;

public class SSTVariable<P, F, S> implements ConstantToken<P, F, S> {

	public Integer id;

	public SSTVariable(Integer id) {
		super();
		this.id = id;
	}

	@Override
	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba) {
		return assignment.variableValue(id);
	}

	@Override
	public Token<P, F, S> rename(HashMap<Integer, Integer> varRename) {
		// TODO Auto-generated method stub
		return new SSTVariable<P, F, S>(varRename.get(id));
	}

	@Override
	public Token<P, F, S> rename(int offset) {
		// TODO Auto-generated method stub
		return new SSTVariable<P, F, S>(id + offset);
	}

	@Override
	public String toString() {
		return "x" + id;
	}
}

package transducers.sst;

import java.util.HashMap;
import java.util.List;

import automata.fsa.SFA;

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
	public Token<P, F, S> rename(int offset) {
		return new SSTVariable<P, F, S>(id + offset);
	}

	@Override
	public String toString() {
		return "x" + id;
	}

	@Override
	public HashMap<Integer, P> getNextState(HashMap<Integer, HashMap<Integer, Integer>> f,
			P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) {
		HashMap<Integer, P> res = new HashMap<Integer, P>();
		res.put(f.get(id).get(currState), guard);
		return res;
	}
	
}

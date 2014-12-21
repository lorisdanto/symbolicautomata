package transducers.sst;

import java.util.List;
import java.util.Map;

import theory.BooleanAlgebraSubst;

public class StringVariable<U, F, S> implements ConstantToken<U, F, S>{

	public String name;

	public StringVariable(String name) {
		super();
		this.name = name;
	}

	@Override
	public List<S> applyTo(
			VariableAssignment<S> assignment, 
			Map<String, Integer> variablesToIndices,
			S input,
			BooleanAlgebraSubst<U, F, S> ba) {
		int index = variablesToIndices.get(name);
		return assignment.variableValue(index);
	}
	
	
}

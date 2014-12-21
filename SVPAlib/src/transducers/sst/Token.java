package transducers.sst;

import java.util.List;
import java.util.Map;

import theory.BooleanAlgebraSubst;

public interface Token<P, F, S> {

	List<S> applyTo(
			VariableAssignment<S> assignment, 
			Map<String, Integer> variablesToIndices,
			S input, 
			BooleanAlgebraSubst<P, F, S> ba);

}

package transducers.sst;

import java.util.HashMap;
import java.util.List;

import theory.BooleanAlgebraSubst;

public interface Token<P, F, S> {

	List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba);

	Token<P, F, S> rename(HashMap<Integer, Integer> varRename);

	Token<P, F, S> rename(int offset);
}

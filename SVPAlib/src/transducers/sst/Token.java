package transducers.sst;

import java.util.List;

import theory.BooleanAlgebraSubst;

public interface Token<P, F, S> {

	List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba);

	Token<P, F, S> rename(int offset);
}
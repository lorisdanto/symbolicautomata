package transducers.sst;

import java.util.HashMap;
import java.util.List;

import automata.sfa.SFA;

import theory.BooleanAlgebraSubst;

public interface Token<P, F, S> {

	List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba);
	
	HashMap<Integer, P> getNextState(HashMap<Integer, HashMap<Integer, Integer>> f, 
			P guard,
			SFA<P,S> aut,
			Integer currState,
			BooleanAlgebraSubst<P, F, S> ba);

	Token<P, F, S> rename(int offset);
}

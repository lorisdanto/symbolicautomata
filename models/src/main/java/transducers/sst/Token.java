package transducers.sst;

import java.util.HashMap;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;

import theory.BooleanAlgebraSubst;

/**
 * Token that can appear in a variable assignment
 * 
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public interface Token<P, F, S> {

	/**
	 * Applies the token to the input <code>input</code>
	 */
	List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba);

	/**
	 * @return a set of state, predicate pairs (q,p) resulting from applying the
	 *         automaton aut starting in state currstate to the current token
	 * @throws TimeoutException 
	 */
	HashMap<Integer, P> getNextState(
			HashMap<Integer, HashMap<Integer, Integer>> f, P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException;

	/**
	 * Renames the token by applying the offset
	 */
	Token<P, F, S> rename(int offset);
}
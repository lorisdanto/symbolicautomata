/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import java.util.HashMap;
import java.util.List;

import automata.sfa.SFA;

import theory.BooleanAlgebraSubst;

/**
 * SSTVariable to be used in a variable update function
 * 
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SSTVariable<P, F, S> implements ConstantToken<P, F, S> {

	protected Integer id;

	/**
	 * variable with ID <code>id</code>
	 */
	public SSTVariable(Integer id) {
		super();
		this.id = id;
	}

	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba) {
		return assignment.variableValue(id);
	}

	public Token<P, F, S> rename(int offset) {
		return new SSTVariable<P, F, S>(id + offset);
	}

	public HashMap<Integer, P> getNextState(
			HashMap<Integer, HashMap<Integer, Integer>> f, P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) {
		HashMap<Integer, P> res = new HashMap<Integer, P>();
		HashMap<Integer, Integer> hm = f.get(id);
		Integer st = hm.get(currState);
		res.put(st, guard);
		return res;
	}
	
	@Override
	public String toString() {
		return "x" + id;
	}

}

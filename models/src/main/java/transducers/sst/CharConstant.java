/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;

import theory.BooleanAlgebraSubst;


/**
 * A char constant that can appear in a variable update
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class CharConstant<P, F, S> implements ConstantToken<P, F, S> {

	public S constant;

	/**
	 * The constant <code>constant<code>
	 */
	public CharConstant(S constant) {
		super();
		this.constant = constant;
	}

	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba) {

		List<S> out = new LinkedList<S>();
		out.add(constant);
		return out;
	}

	public Token<P, F, S> rename(int offset) {
		return this;
	}

	public HashMap<Integer, P> getNextState(
			HashMap<Integer, HashMap<Integer, Integer>> f, P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		HashMap<Integer, P> res = new HashMap<Integer, P>();

		for (SFAInputMove<P, S> move : aut.getInputMovesFrom(currState)) {
			if (ba.HasModel(move.guard, constant)) {
				res.put(move.to, guard);
				break;
			}
		}

		return res;
	}
	
	@Override
	public String toString() {
		return constant.toString();
	}

}


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
 * CharFunction to be used in a variable update
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class CharFunction<P, F, S> implements Token<P, F, S> {

	protected F unaryFunction;

	public CharFunction(F unaryFunction) {
		super();
		this.unaryFunction = unaryFunction;
	}

	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba) {
		List<S> out = new LinkedList<S>();
		out.add(ba.MkSubstFuncConst(unaryFunction, input));
		return out;
	}

	public Token<P, F, S> rename(int offset) {
		return this;
	}

	public HashMap<Integer, P> getNextState(HashMap<Integer, HashMap<Integer, Integer>> f,
			P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		HashMap<Integer, P> res = new HashMap<Integer, P>();
		
		for(SFAInputMove<P, S> move: aut.getInputMovesFrom(currState)){
			P poff = ba.MkSubstFuncPred(unaryFunction, move.guard);
			P conj = ba.MkAnd(guard,poff);
			if(ba.IsSatisfiable(conj))
				res.put(move.to, conj);
		}
		
		return res;
	}
	
	@Override
	public String toString() {
		return "{" + unaryFunction.toString() + "}";
	}
	
}
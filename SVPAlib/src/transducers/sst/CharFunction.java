package transducers.sst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import automata.fsa.InputMove;
import automata.fsa.SFA;

import theory.BooleanAlgebraSubst;

public class CharFunction<P, F, S> implements Token<P, F, S> {

	// This has to be made symbolic
	public F unaryFunction;

	public CharFunction(F unaryFunction) {
		super();
		this.unaryFunction = unaryFunction;
	}

	@Override
	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba) {
		List<S> out = new LinkedList<S>();
		out.add(ba.MkSubstFuncConst(unaryFunction, input));
		return out;
	}

	@Override
	public Token<P, F, S> rename(int offset) {
		return this;
	}

	@Override
	public String toString() {
		return "{" + unaryFunction.toString() + "}";
	}

	@Override
	public HashMap<Integer, P> getNextState(HashMap<Integer, HashMap<Integer, Integer>> f,
			P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) {
		HashMap<Integer, P> res = new HashMap<Integer, P>();
		
		for(InputMove<P, S> move: aut.getInputMovesFrom(currState)){
			P poff = ba.MkSubstFuncPred(unaryFunction, move.guard);
			P conj = ba.MkAnd(guard,poff);
			if(ba.IsSatisfiable(conj))
				res.put(move.to, conj);
		}
		
		return res;
	}
	
}

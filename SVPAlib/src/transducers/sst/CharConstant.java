package transducers.sst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;

import theory.BooleanAlgebraSubst;

public class CharConstant<P, F, S> implements ConstantToken<P, F, S> {

	// This has to be made symbolic
	public S constant;

	public CharConstant(S constant) {
		super();
		this.constant = constant;
	}

	@Override
	public List<S> applyTo(VariableAssignment<S> assignment, S input,
			BooleanAlgebraSubst<P, F, S> ba) {

		List<S> out = new LinkedList<S>();
		out.add(constant);
		return out;
	}

	@Override
	public Token<P, F, S> rename(int offset) {
		return this;
	}

	@Override
	public String toString() {
		return constant.toString();
	}

	@Override
	public HashMap<Integer, P> getNextState(HashMap<Integer, HashMap<Integer, Integer>> f,
			P guard,
			SFA<P, S> aut, Integer currState, BooleanAlgebraSubst<P, F, S> ba) {
		HashMap<Integer, P> res = new HashMap<Integer, P>();
		
		for(SFAInputMove<P, S> move: aut.getInputMovesFrom(currState)){
			if(ba.HasModel(move.guard, constant)){
				res.put(move.to, guard);
				break;
			}
		}
		
		return res;
	}
}

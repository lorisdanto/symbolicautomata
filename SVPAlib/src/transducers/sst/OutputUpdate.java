package transducers.sst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import automata.fsa.SFA;

import theory.BooleanAlgebraSubst;

public class OutputUpdate<P, F, S> extends VariableUpdate<P, F, S> {

	public List<ConstantToken<P, F, S>> update;

	public OutputUpdate(List<ConstantToken<P, F, S>> update) {
		this.update = update;
	}

	/**
	 * applies the output update to the current variable configuration
	 * 
	 * @param assignment
	 * @param ba
	 * @return
	 */
	public List<S> applyTo(VariableAssignment<S> assignment,
			BooleanAlgebraSubst<P, F, S> ba) {
		List<S> value = new ArrayList<S>();
		for (ConstantToken<P, F, S> token : update) {
			List<S> tokenApp = token.applyTo(assignment, null, ba);
			value.addAll(tokenApp);
		}

		return value;
	}

	public OutputUpdate<P, F, S> renameVars(Integer varRename) {
		if (varRename == 0)
			return this;

		return new OutputUpdate<P, F, S>(renameTokens(varRename, this.update));
	}

	private static <P1, F1, S1> List<ConstantToken<P1, F1, S1>> renameTokens(
			Integer varRename, List<ConstantToken<P1, F1, S1>> singleVarUp) {
		List<ConstantToken<P1, F1, S1>> renamed = new ArrayList<ConstantToken<P1, F1, S1>>();
		for (ConstantToken<P1, F1, S1> t : singleVarUp)
			renamed.add((ConstantToken<P1, F1, S1>) t.rename(varRename));

		return renamed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ConstantToken<P, F, S> tok : update) {
			sb.append(tok.toString());
		}
		sb.append(';');

		return sb.toString();
	}

	public Integer getInitStateSummary(
			HashMap<Integer, HashMap<Integer, Integer>> f, SFA<P, S> aut,
			BooleanAlgebraSubst<P, F, S> ba) {
		HashMap<Integer, P> currState = new HashMap<Integer, P>();
		currState.put(aut.initialState, ba.True());

		for (ConstantToken<P, F, S> token : update){
			HashMap<Integer, P> newState = new HashMap<Integer, P>();
			for (int state : currState.keySet())			
				newState.putAll(token.getNextState(f, ba.True(), aut, state, ba));
			currState = newState;
		}

		for (int state : currState.keySet())
			return state;
		
		return null;
	}

	// STATIC METHODS

	/**
	 * Combines two output updates o=o1o2
	 */
	public static <P1, F1, S1> OutputUpdate<P1, F1, S1> combineOutputUpdates(
			Integer varRename1, Integer varRename2,
			OutputUpdate<P1, F1, S1> update1, OutputUpdate<P1, F1, S1> update2) {

		// final output is concat of the two outputs in first variable
		OutputUpdate<P1, F1, S1> ren1 = (OutputUpdate<P1, F1, S1>) update1
				.renameVars(varRename1);
		OutputUpdate<P1, F1, S1> ren2 = (OutputUpdate<P1, F1, S1>) update2
				.renameVars(varRename2);

		List<ConstantToken<P1, F1, S1>> newOut = new ArrayList<ConstantToken<P1, F1, S1>>(
				ren1.update);
		newOut.addAll(ren2.update);

		return new OutputUpdate<P1, F1, S1>(newOut);
	}
}

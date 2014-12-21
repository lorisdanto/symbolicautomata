package transducers.sst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import theory.BooleanAlgebraSubst;

public class SimpleVariableUpdate<P, F, S> extends VariableUpdate<P, F, S> {

	public ArrayList<List<ConstantToken<P, F, S>>> variableUpdate;

	public SimpleVariableUpdate(
			ArrayList<List<ConstantToken<P, F, S>>> variableUpdate) {
		this.variableUpdate = variableUpdate;
	}

	public SimpleVariableUpdate(List<ConstantToken<P, F, S>> singleUpdate) {
		this.variableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>();
		this.variableUpdate.add(singleUpdate);
	}

	/**
	 * applies the current update to the current variable configuration
	 * 
	 * @param assignment
	 * @param ba
	 * @return
	 */
	public VariableAssignment<S> applyTo(VariableAssignment<S> assignment,
			Map<String, Integer> variablesToIndices,
			BooleanAlgebraSubst<P, F, S> ba) {

		int numVars = assignment.numVars();
		ArrayList<List<S>> variableValues = new ArrayList<List<S>>(numVars);
		for (int variable = 0; variable < numVars; variable++) {
			List<S> value = new ArrayList<S>();

			for (ConstantToken<P, F, S> token : variableUpdate.get(variable)) {
				List<S> tokenApp = token.applyTo(assignment,
						variablesToIndices, null, ba);
				value.addAll(tokenApp);
			}

			variableValues.add(value);
		}

		return new VariableAssignment<S>(variableValues);
	}
}

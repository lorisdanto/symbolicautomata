package transducers.sst;

import java.util.ArrayList;
import java.util.HashMap;
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

	@Override
	public VariableUpdate<P, F, S> renameVars(HashMap<String, String> varRename) {
		ArrayList<List<ConstantToken<P, F, S>>> newVariableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>();
		for (List<ConstantToken<P, F, S>> singleVarUp : variableUpdate) {
			newVariableUpdate.add(renameTokens(varRename, singleVarUp));
		}

		return new SimpleVariableUpdate<P, F, S>(newVariableUpdate);
	}
	
	protected SimpleVariableUpdate<P, F, S> composeWith(SimpleVariableUpdate<P, F, S> varUp, Map<String, Integer> varToIndices){
		ArrayList<List<ConstantToken<P, F, S>>> newVarUp = new ArrayList<List<ConstantToken<P,F,S>>>();
		
		ArrayList<List<ConstantToken<P, F, S>>> variableUpdate2 = varUp.variableUpdate;
		for(List<ConstantToken<P, F, S>> update: variableUpdate2){
			List<ConstantToken<P, F, S>> newUpdate = new ArrayList<ConstantToken<P,F,S>>();
			for(ConstantToken<P, F, S> t: update){
				if(t instanceof StringVariable<?,?,?>){
					StringVariable<P,F,S> ct = (StringVariable<P,F,S>) t;
					newUpdate.addAll(this.variableUpdate.get(varToIndices.get(ct.name)));
				}else
					newUpdate.add(t);
			}
			newVarUp.add(newUpdate);
		}
		return new SimpleVariableUpdate<P, F, S>(newVarUp);
	}
	
	protected FunctionalVariableUpdate<P, F, S> composeWith(FunctionalVariableUpdate<P, F, S> varUp, Map<String, Integer> varToIndices){
		ArrayList<List<Token<P, F, S>>> newVarUp = new ArrayList<List<Token<P,F,S>>>();
		
		ArrayList<List<Token<P, F, S>>> variableUpdate2 = varUp.variableUpdate;
		for(List<Token<P, F, S>> update: variableUpdate2){
			List<Token<P, F, S>> newUpdate = new ArrayList<Token<P,F,S>>();
			for(Token<P, F, S> t: update){
				if(t instanceof StringVariable<?,?,?>){
					StringVariable<P,F,S> ct = (StringVariable<P,F,S>) t;
					newUpdate.addAll(this.variableUpdate.get(varToIndices.get(ct.name)));
				}else
					newUpdate.add(t);
			}
			newVarUp.add(newUpdate);
		}
		return new FunctionalVariableUpdate<P, F, S>(newVarUp);
	}

	private List<ConstantToken<P, F, S>> renameTokens(
			HashMap<String, String> varRename,
			List<ConstantToken<P, F, S>> singleVarUp) {
		List<ConstantToken<P, F, S>> renamed = new ArrayList<ConstantToken<P, F, S>>();
		for (ConstantToken<P, F, S> t : singleVarUp)
			renamed.add((ConstantToken<P, F, S>) t.rename(varRename));

		return renamed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (List<ConstantToken<P, F, S>> ass : variableUpdate) {
			for (ConstantToken<P, F, S> tok : ass) {
				sb.append(tok.toString());
			}
			sb.append(';');
		}
		return sb.toString();
	}
	


	// STATIC METHODS
	/**
	 * Combines two updates into a single one by renaming variables accordingly
	 * using the disjoint rename functions
	 */
	public static <P1, F1, S1> SimpleVariableUpdate<P1, F1, S1> combineUpdates(
			HashMap<String, String> varRename1,
			HashMap<String, String> varRename2,
			SimpleVariableUpdate<P1, F1, S1> update1,
			SimpleVariableUpdate<P1, F1, S1> update2) {

		ArrayList<List<ConstantToken<P1, F1, S1>>> combinedVariableUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();
		SimpleVariableUpdate<P1, F1, S1> ren1 = (SimpleVariableUpdate<P1, F1, S1>) update1
				.renameVars(varRename1);
		SimpleVariableUpdate<P1, F1, S1> ren2 = (SimpleVariableUpdate<P1, F1, S1>) update2
				.renameVars(varRename2);
		combinedVariableUpdate.addAll(ren1.variableUpdate);
		combinedVariableUpdate.addAll(ren2.variableUpdate);
		return new SimpleVariableUpdate<P1, F1, S1>(combinedVariableUpdate);

	}

	/**
	 * Combines two output updates o=o1o2
	 */
	public static <P1, F1, S1> SimpleVariableUpdate<P1, F1, S1> combineOutputUpdates(
			HashMap<String, String> varRename1,
			HashMap<String, String> varRename2,
			SimpleVariableUpdate<P1, F1, S1> update1,
			SimpleVariableUpdate<P1, F1, S1> update2) {

		ArrayList<List<ConstantToken<P1, F1, S1>>> combinedVariableUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>();

		// final output is concat of the two outputs in first variable
		SimpleVariableUpdate<P1, F1, S1> ren1 = (SimpleVariableUpdate<P1, F1, S1>) update1
				.renameVars(varRename1);
		SimpleVariableUpdate<P1, F1, S1> ren2 = (SimpleVariableUpdate<P1, F1, S1>) update2
				.renameVars(varRename2);
		List<ConstantToken<P1, F1, S1>> out2 = ren2.variableUpdate.get(0);
		ren2.variableUpdate.set(0, new ArrayList<ConstantToken<P1, F1, S1>>());
		combinedVariableUpdate.addAll(ren1.variableUpdate);
		combinedVariableUpdate.addAll(ren2.variableUpdate);
		combinedVariableUpdate.get(0).addAll(out2);

		return new SimpleVariableUpdate<P1, F1, S1>(combinedVariableUpdate);
	}

	/**
	 * returns the identity assignment
	 * */
	public static <P1, F1, S1> SimpleVariableUpdate<P1, F1, S1> identity(
			Map<String,Integer> varsToIndices) {

		ArrayList<List<ConstantToken<P1, F1, S1>>> variableUpdate = new ArrayList<List<ConstantToken<P1,F1,S1>>>(varsToIndices.size());
		for(String s: varsToIndices.keySet()){
			List<ConstantToken<P1, F1, S1>> idVar = new ArrayList<ConstantToken<P1,F1,S1>>();
			idVar.add(new StringVariable<P1, F1, S1>(s));
			variableUpdate.set(varsToIndices.get(s), idVar);
		}
		
		return new SimpleVariableUpdate<P1, F1, S1>(variableUpdate);

	}
}

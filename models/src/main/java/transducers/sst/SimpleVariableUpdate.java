/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import java.util.ArrayList;
import java.util.List;

import theory.BooleanAlgebraSubst;

/**
 * A variable update that cannot contain tokens which depend on the input being
 * read
 * 
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SimpleVariableUpdate<P, F, S> extends VariableUpdate<P, F, S> {

	public ArrayList<List<ConstantToken<P, F, S>>> variableUpdate;

	/**
	 * The update <code>variableUpdate</code>. The i-th element of the list is
	 * the value being assigned to the i-th variable
	 */
	public SimpleVariableUpdate(
			ArrayList<List<ConstantToken<P, F, S>>> variableUpdate) {
		this.variableUpdate = variableUpdate;
	}

	/**
	 * A one variable update (if the only variable is 0)
	 */
	public SimpleVariableUpdate(List<ConstantToken<P, F, S>> singleUpdate) {
		this.variableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>();
		this.variableUpdate.add(singleUpdate);
	}

	// applies the current update to the current variable configuration
	protected VariableAssignment<S> applyTo(VariableAssignment<S> assignment,
			BooleanAlgebraSubst<P, F, S> ba) {

		int numVars = assignment.numVars();
		ArrayList<List<S>> variableValues = new ArrayList<List<S>>(numVars);
		for (int variable = 0; variable < numVars; variable++) {
			List<S> value = new ArrayList<S>();

			for (ConstantToken<P, F, S> token : variableUpdate.get(variable)) {
				List<S> tokenApp = token.applyTo(assignment, null, ba);
				value.addAll(tokenApp);
			}

			variableValues.add(value);
		}

		return new VariableAssignment<S>(variableValues);
	}

	// rename all vars shifting them by offset varRename
	protected SimpleVariableUpdate<P, F, S> renameVars(Integer varRename) {
		if (varRename == 0)
			return this;

		ArrayList<List<ConstantToken<P, F, S>>> newVariableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>();
		for (List<ConstantToken<P, F, S>> singleVarUp : variableUpdate) {
			newVariableUpdate.add(renameTokens(varRename, singleVarUp));
		}

		return new SimpleVariableUpdate<P, F, S>(newVariableUpdate);
	}

	/**
	 * A one variable update (if the only variable is 0)
	 */
	protected SimpleVariableUpdate<P, F, S> liftToNVars(int n) {
		ArrayList<List<ConstantToken<P, F, S>>> newVariableUpdate = new ArrayList<List<ConstantToken<P, F, S>>>(
				variableUpdate);
		for (int i = variableUpdate.size(); i < n; i++) {
			newVariableUpdate.add(new ArrayList<ConstantToken<P, F, S>>());
		}

		return new SimpleVariableUpdate<P, F, S>(newVariableUpdate);
	}

	// composes two variable update by replacing each variable x in varUp update
	// with the variable update for x in this
	protected SimpleVariableUpdate<P, F, S> composeWith(
			SimpleVariableUpdate<P, F, S> varUp) {
		ArrayList<List<ConstantToken<P, F, S>>> newVarUp = new ArrayList<List<ConstantToken<P, F, S>>>();

		ArrayList<List<ConstantToken<P, F, S>>> variableUpdate2 = varUp.variableUpdate;
		for (List<ConstantToken<P, F, S>> update : variableUpdate2) {
			List<ConstantToken<P, F, S>> newUpdate = new ArrayList<ConstantToken<P, F, S>>();
			for (ConstantToken<P, F, S> t : update) {
				if (t instanceof SSTVariable<?, ?, ?>) {
					SSTVariable<P, F, S> ct = (SSTVariable<P, F, S>) t;
					newUpdate.addAll(this.variableUpdate.get(ct.id));
				} else
					newUpdate.add(t);
			}
			newVarUp.add(newUpdate);
		}
		return new SimpleVariableUpdate<P, F, S>(newVarUp);
	}

	// replacing each variable x in outputExpression
	// with the variable update for x in this
	protected OutputUpdate<P, F, S> composeWith(
			OutputUpdate<P, F, S> outputExpression) {
		List<ConstantToken<P, F, S>> newUpdate = new ArrayList<ConstantToken<P, F, S>>();
		for (ConstantToken<P, F, S> t : outputExpression.update) {
			if (t instanceof SSTVariable<?, ?, ?>) {
				SSTVariable<P, F, S> ct = (SSTVariable<P, F, S>) t;
				newUpdate.addAll(this.variableUpdate.get(ct.id));
			} else
				newUpdate.add(t);
		}

		return new OutputUpdate<P, F, S>(newUpdate);
	}

	// composes two variable update by replacing each variable x in varUp update
	// with the variable update for x in this
	protected FunctionalVariableUpdate<P, F, S> composeWith(
			FunctionalVariableUpdate<P, F, S> varUp) {
		ArrayList<List<Token<P, F, S>>> newVarUp = new ArrayList<List<Token<P, F, S>>>();

		ArrayList<List<Token<P, F, S>>> variableUpdate2 = varUp.variableUpdate;
		for (List<Token<P, F, S>> update : variableUpdate2) {
			List<Token<P, F, S>> newUpdate = new ArrayList<Token<P, F, S>>();
			for (Token<P, F, S> t : update) {
				if (t instanceof SSTVariable<?, ?, ?>) {
					SSTVariable<P, F, S> ct = (SSTVariable<P, F, S>) t;
					newUpdate.addAll(this.variableUpdate.get(ct.id));
				} else
					newUpdate.add(t);
			}
			newVarUp.add(newUpdate);
		}
		return new FunctionalVariableUpdate<P, F, S>(newVarUp);
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
	
	// Combines two updates into a single one by renaming variables accordingly
	// using the disjoint rename functions
	protected static <P1, F1, S1> SimpleVariableUpdate<P1, F1, S1> combineUpdates(
			Integer varRename1, Integer varRename2,
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

	// returns the identity assignment with varCount variables
	protected static <P1, F1, S1> SimpleVariableUpdate<P1, F1, S1> identity(
			int varCount) {

		ArrayList<List<ConstantToken<P1, F1, S1>>> variableUpdate = new ArrayList<List<ConstantToken<P1, F1, S1>>>(
				varCount);
		for (int i = 0; i < varCount; i++) {
			List<ConstantToken<P1, F1, S1>> idVar = new ArrayList<ConstantToken<P1, F1, S1>>();
			idVar.add(new SSTVariable<P1, F1, S1>(i));
			variableUpdate.add(idVar);
		}

		return new SimpleVariableUpdate<P1, F1, S1>(variableUpdate);
	}

	// renames the tokens using the offset varRename
	private static <P1, F1, S1> List<ConstantToken<P1, F1, S1>> renameTokens(
			Integer varRename, List<ConstantToken<P1, F1, S1>> singleVarUp) {
		List<ConstantToken<P1, F1, S1>> renamed = new ArrayList<ConstantToken<P1, F1, S1>>();
		for (ConstantToken<P1, F1, S1> t : singleVarUp)
			renamed.add((ConstantToken<P1, F1, S1>) t.rename(varRename));

		return renamed;
	}	
}

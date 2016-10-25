/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebraSubst;
import utilities.Pair;
import automata.sfa.SFA;

/**
 * A variable update that can contain tokens which depend on the input being
 * read
 * 
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class FunctionalVariableUpdate<P, F, S> extends VariableUpdate<P, F, S> {

	public ArrayList<List<Token<P, F, S>>> variableUpdate;

	/**
	 * the empty update
	 */
	public FunctionalVariableUpdate() {
		super();
		this.variableUpdate = new ArrayList<List<Token<P, F, S>>>();
	}

	/**
	 * The update <code>variableUpdate</code>. The i-th element of the list is
	 * the value being assigned to the i-th variable
	 */
	public FunctionalVariableUpdate(
			ArrayList<List<Token<P, F, S>>> variableUpdate) {
		super();
		this.variableUpdate = variableUpdate;
	}

	/**
	 * A one variable update (if the only variable is 0)
	 */
	public FunctionalVariableUpdate(List<Token<P, F, S>> singleUpdate) {
		super();
		this.variableUpdate = new ArrayList<List<Token<P, F, S>>>();
		this.variableUpdate.add(singleUpdate);
	}

	// applies the current update to the variable configuration
	// <code>assignment</code>
	protected VariableAssignment<S> applyTo(VariableAssignment<S> assignment,
			S input, BooleanAlgebraSubst<P, F, S> ba) {

		int numVars = assignment.numVars();
		ArrayList<List<S>> variableValues = new ArrayList<List<S>>(numVars);
		for (int variable = 0; variable < numVars; variable++) {
			List<S> value = new ArrayList<S>();

			for (Token<P, F, S> token : variableUpdate.get(variable)) {
				List<S> tokenApp = token.applyTo(assignment, input, ba);
				value.addAll(tokenApp);
			}

			variableValues.add(value);
		}

		return new VariableAssignment<S>(variableValues);
	}

	// Renames all variable by an offset varRename
	protected FunctionalVariableUpdate<P, F, S> renameVars(Integer varRename) {
		if (varRename == 0)
			return this;

		ArrayList<List<Token<P, F, S>>> newVariableUpdate = new ArrayList<List<Token<P, F, S>>>();
		for (List<Token<P, F, S>> singleVarUp : variableUpdate) {
			newVariableUpdate.add(renameTokens(varRename, singleVarUp));
		}

		return new FunctionalVariableUpdate<P, F, S>(newVariableUpdate);
	}

	// Adds spare variables to have a list of length n
	protected FunctionalVariableUpdate<P, F, S> liftToNVars(int n) {
		ArrayList<List<Token<P, F, S>>> newVariableUpdate = new ArrayList<List<Token<P, F, S>>>(
				variableUpdate);
		for (int i = variableUpdate.size(); i < n; i++) {
			newVariableUpdate.add(new ArrayList<Token<P, F, S>>());
		}

		return new FunctionalVariableUpdate<P, F, S>(newVariableUpdate);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (List<Token<P, F, S>> ass : variableUpdate) {
			for (Token<P, F, S> tok : ass) {
				sb.append(tok.toString());
			}
			sb.append(';');
		}
		return sb.toString();
	}

	// STATIC METHODS

	// Combines two updates into a single one
	protected static <P1, F1, S1> FunctionalVariableUpdate<P1, F1, S1> combineUpdates(
			FunctionalVariableUpdate<P1, F1, S1> update1,
			FunctionalVariableUpdate<P1, F1, S1> update2) {

		ArrayList<List<Token<P1, F1, S1>>> combinedVariableUpdate = new ArrayList<List<Token<P1, F1, S1>>>();
		FunctionalVariableUpdate<P1, F1, S1> ren2 = (FunctionalVariableUpdate<P1, F1, S1>) update2
				.renameVars(update1.variableUpdate.size());
		combinedVariableUpdate.addAll(update1.variableUpdate);
		combinedVariableUpdate.addAll(ren2.variableUpdate);
		return new FunctionalVariableUpdate<P1, F1, S1>(combinedVariableUpdate);

	}

	// Combines two updates into a single one by renaming variables accordingly
	// using the disjoint rename functions
	// the second update variables are shifted by varRename
	protected static <P1, F1, S1> FunctionalVariableUpdate<P1, F1, S1> addUpdate(
			Integer varRename, FunctionalVariableUpdate<P1, F1, S1> update1,
			FunctionalVariableUpdate<P1, F1, S1> update2) {

		ArrayList<List<Token<P1, F1, S1>>> combinedVariableUpdate = new ArrayList<List<Token<P1, F1, S1>>>();
		FunctionalVariableUpdate<P1, F1, S1> ren2 = (FunctionalVariableUpdate<P1, F1, S1>) update2
				.renameVars(varRename);
		combinedVariableUpdate.addAll(update1.variableUpdate);
		combinedVariableUpdate.addAll(ren2.variableUpdate);
		return new FunctionalVariableUpdate<P1, F1, S1>(combinedVariableUpdate);

	}

	// Used for type-checking
	// Given a summary, a guard, and the output automaton
	// compute all possible summaries by all possible guards of output automaton
	protected Collection<Pair<HashMap<Integer, HashMap<Integer, Integer>>, P>> getNextSummary(
			HashMap<Integer, HashMap<Integer, Integer>> f, P guard,
			SFA<P, S> aut, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {

		// Ordered of collections of tuples (phi, s->s).
		// The i-th elem contains all possible summaries matching the i-th variable
		ArrayList<Collection<Pair<P, HashMap<Integer, Integer>>>> variableToFunsCrossPred = new ArrayList<Collection<Pair<P, HashMap<Integer, Integer>>>>();

		// Update each variable
		for (int curVar = 0; curVar < this.variableUpdate.size(); curVar++) {

			HashMap<Integer, HashMap<Integer, P>> stateToSetOfPairs = new HashMap<Integer, HashMap<Integer, P>>();
			for (int state : aut.getStates()) {
				HashMap<Integer, P> currState = new HashMap<Integer, P>();
				currState.put(state, guard);

				for (Token<P, F, S> token : variableUpdate.get(curVar)) {
					HashMap<Integer, P> newState = new HashMap<Integer, P>();
					for (int state1 : currState.keySet())
						newState.putAll(token.getNextState(f,
								currState.get(state1), aut, state1, ba));
					currState = newState;
				}

				stateToSetOfPairs.put(state, currState);
			}
			Collection<Pair<P, HashMap<Integer, Integer>>> funcs = new LinkedList<Pair<P, HashMap<Integer, Integer>>>();
			accumulatePerVar(funcs, stateToSetOfPairs, 0, ba.True(),
					new HashMap<Integer, Integer>(),
					new ArrayList<Integer>(aut.getStates()), ba);

			variableToFunsCrossPred.add(funcs);
		}

		Collection<Pair<HashMap<Integer, HashMap<Integer, Integer>>, P>> output = new LinkedList<Pair<HashMap<Integer, HashMap<Integer, Integer>>, P>>();

		accumulate(output, variableToFunsCrossPred, 0, ba.True(),
				new HashMap<Integer, HashMap<Integer, Integer>>(), ba);

		return output;

	}

	// Auxiliary accumulation functions used to perform type-checking and
	// compute
	// possible predicates on summarization
	// explore the variable update of a var and try to execute the output
	// automaton on it
	private void accumulatePerVar(
			Collection<Pair<P, HashMap<Integer, Integer>>> funcs,
			HashMap<Integer, HashMap<Integer, P>> stateToSetOfPairs,
			int currStateId, P p, HashMap<Integer, Integer> currFunc,
			ArrayList<Integer> states, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {

		if (!ba.IsSatisfiable(p))
			return;

		if (currStateId == states.size()) {
			funcs.add(new Pair<P, HashMap<Integer, Integer>>(p, currFunc));
		} else {
			// need to recurse over other states
			Integer currState = states.get(currStateId);
			HashMap<Integer, P> outsOfState = stateToSetOfPairs.get(currState);
			for (Integer st : outsOfState.keySet()) {
				P inters = ba.MkAnd(p, outsOfState.get(st));
				if (ba.IsSatisfiable(p)) {
					HashMap<Integer, Integer> newFunc = new HashMap<Integer, Integer>(
							currFunc);
					newFunc.put(currState, st);
					accumulatePerVar(funcs, stateToSetOfPairs, currStateId + 1,
							inters, newFunc, states, ba);
				}
			}
		}
	}

	// Auxiliary accumulation functions used to perform type-checking and
	// compute
	// possible predicates on summarization
	// explore the variable update func and acumulate all possible predicates of
	// output automaton
	private static <P, F, S> void accumulate(
			Collection<Pair<HashMap<Integer, HashMap<Integer, Integer>>, P>> output,
			ArrayList<Collection<Pair<P, HashMap<Integer, Integer>>>> variableToFunsCrossPred,
			int varId, P p, HashMap<Integer, HashMap<Integer, Integer>> fun,
			BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
		if (!ba.IsSatisfiable(p))
			return;

		if (varId == variableToFunsCrossPred.size()) {
			output.add(new Pair<HashMap<Integer, HashMap<Integer, Integer>>, P>(
					fun, p));
		} else {
			// need to recurse over other variables
			Collection<Pair<P, HashMap<Integer, Integer>>> currVarOptions = variableToFunsCrossPred
					.get(varId);
			for (Pair<P, HashMap<Integer, Integer>> pair : currVarOptions) {
				P inters = ba.MkAnd(p, pair.first);
				if (ba.IsSatisfiable(p)) {
					HashMap<Integer, HashMap<Integer, Integer>> newFun = new HashMap<Integer, HashMap<Integer, Integer>>(fun);
					newFun.put(varId, pair.second);

					accumulate(output, variableToFunsCrossPred, varId + 1,
							inters, newFun, ba);
				}
			}

		}
	}

	// renames the tokens using the offset varRename
	private static <P1, F1, S1> List<Token<P1, F1, S1>> renameTokens(
			Integer varRename, List<Token<P1, F1, S1>> singleVarUp) {
		List<Token<P1, F1, S1>> renamed = new ArrayList<Token<P1, F1, S1>>();
		for (Token<P1, F1, S1> t : singleVarUp)
			renamed.add(t.rename(varRename));

		return renamed;
	}
}
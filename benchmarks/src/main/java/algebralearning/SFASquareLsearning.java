package algebralearning;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import algebralearning.equality.EqualityAlgebraLearnerFactory;
import algebralearning.sfa.SFAAlgebraLearner;
import algebralearning.sfa.SFAAlgebraLearnerFactory;
import algebralearning.sfa.SFAEquivalenceOracle;
import algebralearning.sfa.SFAMembershipOracle;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import utilities.SFAprovider;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import theory.sfa.SFABooleanAlgebra;

/**
 * This class implements the third experiment (section 6.3) from the paper
 * "The Learnability of Symbolic Automata" by George Argyros and Loris D'Antoni.
 *
 * @author George Argyros
 *
 */
public class SFASquareLsearning {


	public static SFA <SFA<CharPred, Character>, List <Character>> constructSFA(int sfaIdx, int predIdx) throws TimeoutException {

		String regexSFA =  String.join("", Collections.nCopies(sfaIdx, "a"));
		String regexPred = String.join("", Collections.nCopies(predIdx, "a"));

		// First parse the Regex and create an SFA over characters
		UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
		SFABooleanAlgebra <CharPred, Character> sfaBA = new SFABooleanAlgebra<>(solver);
		SFA <CharPred, Character> mainsfa = new SFAprovider(regexSFA, solver).getSFA();
		SFA <CharPred, Character> predsfa = new SFAprovider(regexPred, solver).getSFA();
		mainsfa = mainsfa.determinize(solver).minimize(solver);
		predsfa = predsfa.determinize(solver).minimize(solver);
		// Use the previously made SFAs as guards in the final SFA
		SFA <SFA<CharPred, Character>, List <Character>> resultSFA = null;
		Collection <SFAMove<SFA<CharPred, Character>, List <Character>>> transitions = new LinkedList<>();
		for (int s = 0; s < mainsfa.stateCount(); s++) {
			for (SFAMove<CharPred, Character> t : mainsfa.getTransitionsFrom(s)) {
				if (t.hasModel('a', solver) && !t.hasModel('b', solver)) {
					transitions.add(new SFAInputMove<SFA<CharPred, Character>, List<Character>>(t.from, t.to, predsfa));
				}
			}
		}
		resultSFA = SFA.MkSFA(transitions, mainsfa.getInitialState(), mainsfa.getFinalStates(), sfaBA);
		return resultSFA;
	}


	public static Integer[][] runSFAofSFAExperiments() throws TimeoutException {
		// everything below is for the sfa algebra learning
		UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
		SFABooleanAlgebra <CharPred, Character> sfaBa = new SFABooleanAlgebra<>(solver);
		EqualityAlgebraLearnerFactory <CharPred, Character> eqFactory = new EqualityAlgebraLearnerFactory <>(solver);
		SFAAlgebraLearnerFactory <CharPred, Character> sfaBalf = new SFAAlgebraLearnerFactory<>(eqFactory, solver);
		int cnt = 0;
		Integer [][]results = new Integer[15][8];
		// everything below is for the sfa over sfa learning
		for (int sfaSize=5; sfaSize <= 15; sfaSize += 5) {
			for (int predSize=2; predSize < 17; predSize += 3) {
				SFA <SFA<CharPred, Character>, List<Character>> model, target = constructSFA(sfaSize,  predSize);
				SFAMembershipOracle <SFA<CharPred, Character>, List<Character>> memb = new SFAMembershipOracle <>(target, sfaBa);
				SFAEquivalenceOracle <SFA<CharPred, Character>, List<Character>> equiv = new SFAEquivalenceOracle<>(target, sfaBa);
				SFAAlgebraLearner <SFA<CharPred, Character>, List<Character>> learner;
				learner = new SFAAlgebraLearner<>(memb, sfaBa, sfaBalf);
				model = learner.getModelFinal(equiv);
				// Save the results
				results[cnt][0] = model.stateCount();
				results[cnt][1] = model.getTransitionCount();
				results[cnt][2] = memb.getDistinctQueries();
				results[cnt][3] = equiv.getDistinctCeNum();
				results[cnt][4] = equiv.getCachedCeNum();
				results[cnt][5] = learner.getNumCEGuardUpdates();
				results[cnt][6] = learner.getNumDetCE();
				results[cnt][7] = learner.getNumCompCE();
				cnt ++;
			}
		}
		return results;
	}

}

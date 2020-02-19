
package algebralearning.sfa;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import algebralearning.equality.EqualityAlgebraLearnerFactory;
import algebralearning.sfa.SFAAlgebraLearner;
import algebralearning.sfa.SFAEquivalenceOracle;
import algebralearning.sfa.SFAMembershipOracle;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class SFALearnerUnitTest {
	static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();

	private SFA<CharPred, Character> getTestSFA() throws TimeoutException {
		List <SFAMove<CharPred, Character>> transitions = new LinkedList<>();
		HashSet <Integer> finalStates = new HashSet<>();

		transitions.add(new SFAInputMove<>(0, 1, solver.True()));
		transitions.add(new SFAInputMove<>(1, 1, new CharPred('a')));
		transitions.add(new SFAInputMove<>(1, 2, new CharPred('b')));
		CharPred p = new CharPred('c');
		p = solver.MkOr(p, new CharPred('d'));
		transitions.add(new SFAInputMove<>(2, 0, p));
		finalStates.add(2);

		return SFA.MkSFA(transitions, 0, finalStates, solver);
	}

	@Test
	public void testSFALearning1() throws TimeoutException {
		SFA<CharPred, Character> model, sfa = getTestSFA().determinize(solver).minimize(solver);
		SFAMembershipOracle<CharPred, Character>memb = new SFAMembershipOracle<>(sfa, solver);
		SFAEquivalenceOracle<CharPred, Character>equiv = new SFAEquivalenceOracle<>(sfa, solver);
		EqualityAlgebraLearnerFactory<CharPred, Character> balf = new EqualityAlgebraLearnerFactory<>(solver);
		SFAAlgebraLearner<CharPred, Character> learner = new SFAAlgebraLearner<>(memb, solver, balf);

		model = learner.getModelFinal(equiv);
		assertTrue(model.isEquivalentTo(sfa, solver));
	}


}

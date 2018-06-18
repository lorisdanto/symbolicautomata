
package test.algebralearning.sfa;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import algebralearning.equality.EqualityAlgebraLearnerFactory;
import algebralearning.sfa.SFAAlgebraLearner;
import algebralearning.sfa.SFAEquivalenceOracle;
import algebralearning.sfa.SFAMembershipOracle;
import automata.sfa.SFA;
import benchmark.SFAprovider;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

import benchmark.algebralearning.RELearning;

public class SFALearnerUnitTest {

	@Test
	public void testSFALearning1() throws TimeoutException {
		UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
		SFAprovider provider = new SFAprovider("aaa", solver);			
		SFA<CharPred, Character> model, sfa = provider.getSFA().determinize(solver).minimize(solver);
		SFAMembershipOracle<CharPred, Character>memb = new SFAMembershipOracle<>(sfa, solver);
		SFAEquivalenceOracle<CharPred, Character>equiv = new SFAEquivalenceOracle<>(sfa, solver);
		EqualityAlgebraLearnerFactory<CharPred, Character> balf = new EqualityAlgebraLearnerFactory<>(solver);
		SFAAlgebraLearner<CharPred, Character> learner = new SFAAlgebraLearner<>(memb, solver, balf);
		
		model = learner.getModelFinal(equiv);
		assertTrue(model.isEquivalentTo(sfa, solver));					
	}
	
	@Test
	public void testSFALearning2() throws TimeoutException {
		UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
		SFAprovider provider = new SFAprovider("<scrip(t|l)>s(.*)</script>", solver);			
		SFA<CharPred, Character> model, sfa = provider.getSFA().determinize(solver).minimize(solver);
		SFAMembershipOracle<CharPred, Character>memb = new SFAMembershipOracle<>(sfa, solver);
		SFAEquivalenceOracle<CharPred, Character>equiv = new SFAEquivalenceOracle<>(sfa, solver);
		EqualityAlgebraLearnerFactory<CharPred, Character> balf = new EqualityAlgebraLearnerFactory<>(solver);
		SFAAlgebraLearner<CharPred, Character> learner = new SFAAlgebraLearner<>(memb, solver, balf);
		
		model = learner.getModelFinal(equiv);
		assertTrue(model.isEquivalentTo(sfa, solver));					
	}
	
	@Test
	public void testRELearning() throws TimeoutException {
		RELearning rel = new RELearning();
		assertTrue(rel.learnREBenchmark(2) != null);		
	}
}

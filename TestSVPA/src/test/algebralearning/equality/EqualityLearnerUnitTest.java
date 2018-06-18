package test.algebralearning.equality;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.sat4j.specs.TimeoutException;

import algebralearning.equality.EqualityAlgebraLearner;
import algebralearning.equality.EqualityEquivalenceOracle;
import algebralearning.equality.EqualityMembershipOracle;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;


public class EqualityLearnerUnitTest {

	
	@Test
	public void EqualityLearnerTest1() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		CharPred model, target = ba.MkAtom('a');
		target = ba.MkOr(target, ba.MkAtom('b'));		
		EqualityMembershipOracle<CharPred, Character> memb = new EqualityMembershipOracle<>(target, ba);
		EqualityEquivalenceOracle<CharPred, Character> equiv = new EqualityEquivalenceOracle<>(target, ba);		
		EqualityAlgebraLearner <CharPred, Character> learner = new EqualityAlgebraLearner<>(memb, ba);
		model = learner.getModelFinal(equiv);
		assertTrue(model.equals(target));				
	}
	
	@Test
	public void EqualityLearnerTest2() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		CharPred model, target = ba.MkAtom('a');
		target = ba.MkOr(target, ba.MkAtom('b'));
		target = ba.MkNot(target);		
		EqualityMembershipOracle<CharPred, Character> memb = new EqualityMembershipOracle<>(target, ba);
		EqualityEquivalenceOracle<CharPred, Character> equiv = new EqualityEquivalenceOracle<>(target, ba);		
		EqualityAlgebraLearner <CharPred, Character> learner = new EqualityAlgebraLearner<>(memb, ba);
		model = learner.getModelFinal(equiv);
		assertTrue(model.equals(target));				
	}
	
}

package test.algebralearning.bdd;

import org.sat4j.specs.TimeoutException;

import algebralearning.bdd.BDDAlgebraLearner;
import algebralearning.bdd.BDDEquivalenceOracle;
import algebralearning.bdd.BDDMembershipOracle;

import theory.bdd.BDD;
import theory.bdd.BDDFactory;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import java.util.Random;

public class BDDLearnerUnitTest {
	
	private static BDDFactory factory = BDDFactory.init(500000 , 125000);;
	private static Random rand = new Random();
	
	public BDDLearnerUnitTest() {
		
		// set the number of variables for the bdds
		factory.setVarNum(5);
	}
	
	private BDD convertBooleanListToBDD(List <Boolean> inp) {
		BDD res = factory.one();
		Integer index = 0;

		for (Boolean e : inp) {
			if (!e) {
				res = res.and(factory.nithVar(index));			;
			} else {
				res = res.and(factory.ithVar(index));
			}
			index ++;
		}
		return res;
	}
	
	
	@Test
	public void testBDDLearningBasic() throws TimeoutException {
		
		BDD bdd = factory.one();
		BDDMembershipOracle memb = new BDDMembershipOracle(bdd);
		BDDEquivalenceOracle equiv = new BDDEquivalenceOracle(bdd);
		BDDAlgebraLearner learner = new BDDAlgebraLearner(memb, factory);
		
		assertTrue(learner.getModelFinal(equiv) != null);
	}

	
	@Test
	public void testBDDLearningBasic2() throws TimeoutException {
		
		BDD bdd = factory.zero();
		BDDMembershipOracle memb = new BDDMembershipOracle(bdd);
		BDDEquivalenceOracle equiv = new BDDEquivalenceOracle(bdd);
		BDDAlgebraLearner learner = new BDDAlgebraLearner(memb, factory);
		
		assertTrue(learner.getModelFinal(equiv) != null);
	}

	
	private BDD getRandomBDD() { 
		int acceptedNumber = 10;
		BDD ret = factory.zero();
		for (int i = 0; i < acceptedNumber; i++) {
			LinkedList <Boolean> l = new LinkedList<>();			
			for (int j = 0; j < factory.varNum(); j ++) {
				l.add(rand.nextInt(2) == 0? false : true);
			}
			ret = ret.or(convertBooleanListToBDD(l));
		}
		return ret;
	}
	
	@Test
	public void fuzzTest() throws TimeoutException { 
		
		int sampleNumber = 500;
		
		for (int i = 0; i < sampleNumber; i++ ) {			
			BDD sample = getRandomBDD();
			BDDMembershipOracle memb = new BDDMembershipOracle(sample);
			BDDEquivalenceOracle equiv = new BDDEquivalenceOracle(sample);
			BDDAlgebraLearner learner = new BDDAlgebraLearner(memb, factory);
					
			assertTrue(learner.getModelFinal(equiv) != null);			
		}
		
		
		
	}
	
}

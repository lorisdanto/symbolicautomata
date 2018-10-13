package algebralearning.bdd;

import algebralearning.AlgebraLearnerFactory;
import algebralearning.oracles.MembershipOracle;
import theory.bdd.BDD;
import theory.bdd.BDDFactory;

public class BDDAlgebraLearnerFactory extends AlgebraLearnerFactory <BDD,BDD> {

	private BDDFactory factory;

	public BDDAlgebraLearnerFactory(BDDFactory f) {
		factory = f;
	}

	public BDDAlgebraLearner getBALearner(MembershipOracle <BDD> m) {
		return new BDDAlgebraLearner(m, factory);
	}

}
package algebralearning;

import algebralearning.oracles.MembershipOracle;

public abstract class AlgebraLearnerFactory <P,D> {

	public abstract AlgebraLearner <P,D> getBALearner(MembershipOracle <D> m);
	
}

package algebralearning.equality;

import algebralearning.AlgebraLearner;
import algebralearning.AlgebraLearnerFactory;
import algebralearning.oracles.MembershipOracle;
import theory.BooleanAlgebra;

public class EqualityAlgebraLearnerFactory <P,D> extends AlgebraLearnerFactory <P,D> {

	private BooleanAlgebra <P,D> ba;
	
	public EqualityAlgebraLearnerFactory(BooleanAlgebra <P,D> b) {
		ba = b;
	}
		
	public AlgebraLearner <P,D> getBALearner(MembershipOracle <D> m) {
		return new EqualityAlgebraLearner <P,D> (m, ba);
	}

}

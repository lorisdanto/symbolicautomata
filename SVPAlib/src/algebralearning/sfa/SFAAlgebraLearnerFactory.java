package algebralearning.sfa;

import java.util.List;

import algebralearning.AlgebraLearner;
import algebralearning.AlgebraLearnerFactory;
import algebralearning.oracles.MembershipOracle;
import automata.sfa.SFA;
import theory.BooleanAlgebra;

public class SFAAlgebraLearnerFactory <P,D> extends AlgebraLearnerFactory <SFA <P,D>, List <D>> {

	private AlgebraLearnerFactory <P,D> baLearnerFactory;
	private BooleanAlgebra <P,D> ba;
	
	public SFAAlgebraLearnerFactory(AlgebraLearnerFactory <P,D>balf, BooleanAlgebra <P,D>b) {
		baLearnerFactory = balf;
		ba = b;
	}
	
	@Override
	public AlgebraLearner <SFA <P,D>, List <D>> getBALearner(MembershipOracle <List<D>> m) {
			//return new SFAAlgebraLearner(m, ba, baLearnerFactory);
		return new SFAAlgebraLearner <P,D> (m, ba, baLearnerFactory);
	}

}

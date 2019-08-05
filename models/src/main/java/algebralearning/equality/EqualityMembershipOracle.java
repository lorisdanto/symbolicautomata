package algebralearning.equality;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.MembershipOracle;
import theory.BooleanAlgebra;

public class EqualityMembershipOracle <P, D> extends MembershipOracle <D> {

	private P target;
	private BooleanAlgebra <P,D>ba;
	
	public EqualityMembershipOracle(P trg, BooleanAlgebra <P,D> b) {
		target = trg;
		ba = b;
	}
	
	public boolean query(D input) throws TimeoutException {
		return ba.HasModel(target, input); 
	}
	
}

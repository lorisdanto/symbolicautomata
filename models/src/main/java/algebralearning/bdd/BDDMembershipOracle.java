package algebralearning.bdd;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.MembershipOracle;
import theory.bdd.BDD;

public class BDDMembershipOracle extends MembershipOracle <BDD> {

	private BDD target;

	public BDDMembershipOracle(BDD t) {
		target = t;
	}


	public boolean query(BDD input) throws TimeoutException {
		return (!target.and(input).isZero());
	}

}
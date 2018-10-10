package algebralearning.bdd;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.EquivalenceOracle;
import theory.bdd.BDD;

public class BDDEquivalenceOracle extends EquivalenceOracle <BDD, BDD> {

	private BDD target;

	public BDDEquivalenceOracle(BDD t) {
		target = t;
	}

	public BDD getCounterexample(BDD model) throws TimeoutException {
		if (target.equals(model)) {
			return null;
		}

		BDD ce = model.and(target.not()).satOne();
		if (ce.isZero()) {
			ce = target.and(model.not()).satOne();
		}
		if (ce.isZero()) {
			return null;
		}
		return ce;
	}

}
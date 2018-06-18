package algebralearning.equality;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.EquivalenceOracle;
import theory.BooleanAlgebra;

public class EqualityEquivalenceOracle <P, D> extends EquivalenceOracle <P,D> {

	BooleanAlgebra <P,D> ba;
	P target;
	
	public EqualityEquivalenceOracle(P trg, BooleanAlgebra <P,D> b) {
		target = trg;
		ba = b;
	}
	
	@Override
	public D getCounterexample(P model) throws TimeoutException {
		
		if (ba.AreEquivalent(target, model)) {
			return null;
		}
		// Negate the model and intersect 
		P negInt = ba.MkAnd(ba.MkNot(model), target);
		if (ba.IsSatisfiable(negInt)) {
			return ba.generateWitness(negInt);
		}
		// Negate the target and intersect
		negInt = ba.MkAnd(model, ba.MkNot(target));		
		return ba.generateWitness(negInt);				
	}

}

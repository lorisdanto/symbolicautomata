package algebralearning.finitealgebra;

import algebralearning.AlgebraLearnerFactory;
import algebralearning.oracles.MembershipOracle;

import java.util.HashSet;

import theory.BooleanAlgebra;

public class FiniteAlgebraLearnerFactory <P,D> extends AlgebraLearnerFactory <P,D> {

	private HashSet <D> alphabet;
	BooleanAlgebra <P,D> ba;

	public FiniteAlgebraLearnerFactory(HashSet <D>a, BooleanAlgebra <P,D> b) {
		alphabet = new HashSet <D>(a);
		ba = b;
	}

	public FiniteAlgebraLearner <P,D> getBALearner(MembershipOracle <D> m) {
		return new FiniteAlgebraLearner <P,D> (alphabet, m, ba);
	}

}
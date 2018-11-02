package algebralearning.finitealgebra;

import java.util.HashSet;

import org.sat4j.specs.TimeoutException;

import algebralearning.AlgebraLearner;
import algebralearning.oracles.EquivalenceOracle;
import algebralearning.oracles.MembershipOracle;

import theory.BooleanAlgebra;


public class FiniteAlgebraLearner <P,D> extends AlgebraLearner <P,D> {

	HashSet <D> alphabet;
	MembershipOracle <D>membOracle;
	BooleanAlgebra <P,D> ba;

	public FiniteAlgebraLearner(HashSet <D> a, MembershipOracle <D> m, BooleanAlgebra <P,D> b) {
		alphabet = new HashSet <D>(a);
		membOracle = m;
		ba = b;
		//System.out.println(alphabet);
	}

	public P getModel() throws TimeoutException{
		P pred = ba.False();
		for (D element : alphabet) {
			if (membOracle.query(element)) {
				//System.out.println(element);
				pred = ba.MkOr(pred, ba.MkAtom(element));
			}
		}
		return pred;
	}

    public P updateModel(D counterexample) {
    		/* This should never happen */
        throw new AssertionError("FiniteBooleanAlgebraLearner instances never produce invalid results");
    }

    public P getModelFinal(EquivalenceOracle <P, D> equiv) throws TimeoutException {
    		/* Since we never fail on the first model, there is no need for iteration */
    		return getModel();
    }

}
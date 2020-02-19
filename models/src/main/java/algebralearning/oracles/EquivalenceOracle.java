/**
 * Equivalence oracle abstract class implementation
 * @author George Argyros
 */
package algebralearning.oracles;

import org.sat4j.specs.TimeoutException;

public abstract class EquivalenceOracle <P,D> {

	/**
	 * Return a counterexample between the model and the target
	 * or null if the two models are equivalent.
	 * 
	 * @param model The model to compare the target function against
	 * @return A counterexample of type D or null if the two models are equivalent
	 */
    public abstract D getCounterexample(P model) throws TimeoutException;
}

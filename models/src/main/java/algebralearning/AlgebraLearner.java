/**
 * Abstract class for algebra learning algorithms.
 * 
 * @author George Argyros
 */
package algebralearning;


import org.sat4j.specs.TimeoutException;
import algebralearning.oracles.EquivalenceOracle;
/**	
 * Abstract class for implementing learning algorithms for different boolean algebras.
 *
 * @param <P> The type of predicates in the boolean algebra.
 * @param <D> The domain of the underlying boolean algebra. 
 */
public abstract class AlgebraLearner <P, D> {
	
	/* Return an initial model */
    public abstract P getModel() throws TimeoutException;

    /* Update the previous model given a counterexample */
    public abstract P updateModel(D counterexample) throws TimeoutException;
    
    /* Learn iteratively a model using the provided equivalence oracle */
    public abstract P getModelFinal(EquivalenceOracle <P, D> equiv) throws TimeoutException;

}
/**
 * Membership oracle abstract class implementation
 * @author George Argyros
 */
package algebralearning.oracles;



import org.sat4j.specs.TimeoutException;

/**
 * Membership oracle class used by BooleanAlgebraLearner instances
 * 
 * @param <D> Domain of the underlying Boolean algebra
 */
public abstract class MembershipOracle <D> {
	
	/** 
	 * Return the result of a running on the input parameter to the target function. 
	 * 
	 * @param input The input to the membership query.
	 * @return true/false depending on the value of the target function on the provided input.
	 */
	abstract public boolean query(D input) throws TimeoutException;
	
}

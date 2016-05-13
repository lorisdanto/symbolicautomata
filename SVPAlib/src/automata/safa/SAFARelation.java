package automata.safa;

import org.sat4j.specs.TimeoutException;

/**
 * Represents a congruence relation on configurations
 */
public abstract class SAFARelation {
	/**
	 * Check if a pair belongs to the relation
	 * @param p
	 * @param q
	 * @return true if (p,q) was already in the relation, false otherwise
	 * @throws TimeoutException 
	 */
	public abstract boolean isMember(BooleanExpression p, BooleanExpression q) throws TimeoutException;
	
	/**
	 * Add a pair to the relation
	 * @param p
	 * @param q
	 * @throws TimeoutException 
	 */
	public abstract boolean add(BooleanExpression p, BooleanExpression q) throws TimeoutException;
}

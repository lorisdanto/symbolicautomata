/**
 * 
 */
package automata;

import theory.BooleanAlgebra;

public abstract class Move<U, S> {

	public Integer from;

	public Integer to;

	/**
	 * Transition from state <code>from</code> to state <code>to</code>
	 */
	public Move(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * @return whether the transition can ever be enabled
	 */
	public abstract boolean isSatisfiable(BooleanAlgebra<U, S> ba);

	/**
	 * @return an input triggering the transition. Null if it's an epsilon
	 *         transition
	 */
	public abstract S getWitness(BooleanAlgebra<U, S> ba);

	/**
	 * @return true iff <code>input</code> can trigger the transition
	 */
	public abstract boolean hasModel(S input, BooleanAlgebra<U, S> ba);

	/**
	 * @return true iff it is an epsilon transition
	 */
	public abstract boolean isEpsilonTransition();

	/**
	 * Create the dot representation of the move
	 */
	public abstract String toDotString();

}

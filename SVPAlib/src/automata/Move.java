/**
 * 
 */
package automata;

import theory.BooleanAlgebra;

public abstract class Move<U,S>{

	public Integer from;

	public Integer to;
	
	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	public Move(Integer from, Integer to) {
		this.from=from;
		this.to=to;
	}
	
	public abstract boolean isSatisfiable(BooleanAlgebra<U,S> boolal);
	
	/**
	 * Checks if the transition is an epsilon transition
	 * <code>to</code> with input <code>input</code>
	 */
	public abstract boolean isEpsilonTransition();
	
	/**
	 * Create the dot representation of the move
	 */
	public abstract String toDotString();

}

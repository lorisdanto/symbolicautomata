/**
 * 
 */
package automata.sfa;

import theory.BooleanAlgebra;
import automata.Move;

public abstract class SFAMove<U,S> extends Move<U, S>{
	
	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	public SFAMove(Integer from, Integer to) {
		super(from, to);
	}
	
	public abstract boolean isSatisfiable(BooleanAlgebra<U,S> boolal);
	
	public abstract boolean isDisjointFrom(SFAMove<U,S> t, BooleanAlgebra<U,S> ba);
	
	/**
	 * Checks if the transition is an epsilon transition
	 * <code>to</code> with input <code>input</code>
	 */
	public abstract boolean isEpsilonTransition();
	
	@Override
	public abstract Object clone();

}

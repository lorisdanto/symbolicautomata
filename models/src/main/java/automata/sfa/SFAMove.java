/**
 * SVPAlib
 * automata
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata.sfa;

import theory.BooleanAlgebra;

import org.sat4j.specs.TimeoutException;

import automata.Move;

/**
 * Abstract SFA Move
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public abstract class SFAMove<P, S> extends Move<P, S> {

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public SFAMove(Integer from, Integer to) {
		super(from, to);
	}

	/**
	 * Checks if the move is disjoint from the move <code>t</code> (they are not from same state on same predicate)
	 * @throws TimeoutException 
	 */
	public abstract boolean isDisjointFrom(SFAMove<P, S> t,
			BooleanAlgebra<P, S> ba) throws TimeoutException;

	@Override
	public abstract Object clone();

}

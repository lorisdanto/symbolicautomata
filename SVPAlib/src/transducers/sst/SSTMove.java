/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import theory.BooleanAlgebra;

import org.sat4j.specs.TimeoutException;

import automata.Move;

/**
 * An SST move.
 * 
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public abstract class SSTMove<P, F, S> extends Move<P, S> {

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public SSTMove(Integer from, Integer to) {
		super(from, to);
	}

	/**
	 * Checks if the move is disjoint from the move <code>t</code> (they are not
	 * from same state on same predicate)
	 * @throws TimeoutException 
	 */
	public abstract boolean isDisjointFrom(SSTMove<P, F, S> t,
			BooleanAlgebra<P, S> ba) throws TimeoutException;

	@Override
	public abstract Object clone();

}

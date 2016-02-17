package automata.esfa;

import automata.ExtendedMove;
import theory.BooleanAlgebra;

/**
 * Abstract ESFA Move
 * @param <P> set of predicates over the domain S*
 * @param <S> domain of the automaton alphabet
 */
public abstract class ESFAMove<P, S> extends ExtendedMove<P, S> {

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public ESFAMove(Integer from, Integer to) {
		super(from, to);
	}

	/**
	 * Checks if the move is disjoint from the move <code>t</code> (they are not from same state on same predicate)
	 */
	public abstract boolean isDisjointFrom(ESFAMove<P, S> t,
			BooleanAlgebra<P, S> ba);
	@Override
	public abstract Object clone();

}

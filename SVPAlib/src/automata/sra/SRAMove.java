/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import theory.BooleanAlgebra;

import org.sat4j.specs.TimeoutException;

import automata.Move;

/**
 * Abstract SRA Move
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public abstract class SRAMove<P, S> extends Move<P, S> {

    public P guard;
    public Integer register;

	/**
	 * Constructs an SRA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public SRAMove(Integer from, Integer to, P guard, Integer register) {
		super(from, to);
        this.guard = guard;
        this.register = register;
	}

	/**
	 * Checks if the move is disjoint from the move <code>t</code> (they are not from same state on same predicate)
	 * @throws TimeoutException 
	 */
	public abstract boolean isDisjointFrom(SRAMove<P, S> t,
			BooleanAlgebra<P, S> ba) throws TimeoutException;

	@Override
	public abstract Object clone();

    @Override
	public boolean isEpsilonTransition() {
		return false;
	}
}

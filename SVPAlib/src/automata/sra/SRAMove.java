/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.LinkedList;
import java.util.Collection;

import theory.BooleanAlgebra;

import org.sat4j.specs.TimeoutException;

/**
 * Abstract SRA Move
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public abstract class SRAMove<P, S> {

    public Integer from;
    public Integer to;
    public P guard;
    public Collection<Integer> registerIndexes;

	/**
	 * Constructs an SRA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public SRAMove(Integer from, Integer to, P guard, Collection<Integer> registerIndexes) {
		this.from = from;
        this.to = to;
        this.guard = guard;
        this.registerIndexes = registerIndexes;
	}

	/**
	 * @return whether the transition can ever be enabled
	 * @throws TimeoutException 
	 */
	public abstract boolean isSatisfiable(BooleanAlgebra<P, S> ba, LinkedList<S> registers) throws TimeoutException;

	/**
	 * @return an input triggering the transition
	 * @throws TimeoutException 
	 */
	public abstract S getWitness(BooleanAlgebra<P, S> ba, LinkedList<S> registers) throws TimeoutException;

	/**
	 * @return true iff <code>input</code> can trigger the transition
	 * @throws TimeoutException 
	 */
	public abstract boolean hasModel(S input, BooleanAlgebra<P, S> ba, LinkedList<S> registers) throws TimeoutException;

	/**
	 * Create the dot representation of the move
	 */
	public abstract String toDotString();

	/**
	 * Checks if the move is disjoint from the move <code>t</code> (they are not from same state on same predicate)
	 * @throws TimeoutException 
	 */
	public abstract boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException;

	public abstract Object clone();

    public abstract MSRAMove<P, S> asMultipleAssignment();

    public abstract boolean isFresh();

	public abstract boolean isMultipleAssignment();
}

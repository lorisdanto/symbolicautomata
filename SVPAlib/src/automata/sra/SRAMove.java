/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
    public Integer registerIndex = null;
    public Set<Integer> E;
	public Set<Integer> I;
	public Set<Integer> U;

	/**
	 * Constructs an SRA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with predicate <code>guard</code> and registers <code>E, I, U</code>
	 */
	public SRAMove(Integer from, Integer to, P guard, Set<Integer> E, Set<Integer> I, Set<Integer> U) {
		this.from = from;
        this.to = to;
        this.guard = guard;
        this.E = E;
		this.I = I;
		this.U = U;
	}

	/**
	 * @return whether the transition can ever be enabled
	 * @throws TimeoutException 
	 */
	public abstract boolean isSatisfiable(BooleanAlgebra<P, S> ba) throws TimeoutException;

	/**
	 * @return an input triggering the transition
	 * @throws TimeoutException 
	 */
	public abstract S getWitness(BooleanAlgebra<P, S> ba, LinkedList<S> registerValues) throws TimeoutException;

	/**
	 * @return true iff <code>input</code> can trigger the transition
	 * @throws TimeoutException 
	 */
	public abstract boolean hasModel(S input, BooleanAlgebra<P, S> ba, LinkedList<S> registerValues) throws TimeoutException;

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

	/**
	 * Returns the powerset of <code>originalSet</code> - O(n^2)
	 */
	static <S> HashSet<HashSet<Integer>> getPowerset(HashSet<Integer> originalSet) {
		HashSet<HashSet<Integer>> sets = new HashSet<HashSet<Integer>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<Integer>());
			return sets;
		}
		LinkedList<Integer> list = new LinkedList<Integer>(originalSet);
		Integer head = list.get(0);
		HashSet<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
		for (HashSet<Integer> set : getPowerset(rest)) {
			HashSet<Integer> newSet = new HashSet<Integer>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}

	static <S> HashSet<HashSet<Integer>> getPowersetIncluding(HashSet<Integer> originalSet,
                                                              Set<Integer> toInclude ) {
		HashSet<Integer> setWithoutReg = new HashSet<>(originalSet);
		setWithoutReg.removeAll(toInclude);

        HashSet<HashSet<Integer>> powSet = getPowerset(setWithoutReg);
        for (HashSet<Integer> set: powSet)
            set.addAll(toInclude);

        return powSet;
	}

    public abstract LinkedList<MSRAMove<P, S>> asMultipleAssignment(LinkedList<S> registerValues);

    public abstract boolean isFresh();

	public abstract boolean isStore();

	public abstract boolean isMultipleAssignment();
}

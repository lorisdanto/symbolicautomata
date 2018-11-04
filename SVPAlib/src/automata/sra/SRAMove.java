/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.*;

import theory.BooleanAlgebra;

import org.sat4j.specs.TimeoutException;

/**
 * Abstract SRA Move
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRAMove<P, S> {

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
	public boolean isSatisfiable(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return ba.IsSatisfiable(guard);
	}

	/**
	 * @return an input triggering the transition
	 * @throws TimeoutException 
	 */
	public S getWitness(BooleanAlgebra<P, S> ba, LinkedList<S> registerValues) throws TimeoutException {
		P predicates = ba.True();
		// Must be "in" all E registers, i.e. must be equal to the values in all E registers.
		for (Integer ERegister : E)
			if (registerValues.get(ERegister) != null)
				predicates = ba.MkAnd(predicates, ba.MkAtom(registerValues.get(ERegister)));
			else
				predicates = ba.MkAnd(predicates, ba.False());
		// Must not be "in" any I registers, i.e. must not be equal to any value in I registers.
		for (Integer IRegister : I)
			if (registerValues.get(IRegister) != null)
				predicates = ba.MkAnd(predicates, ba.MkNot(ba.MkAtom(registerValues.get(IRegister))));

		return ba.generateWitness(ba.MkAnd(guard, predicates));
	}

	/**
	 * @return true iff <code>input</code> can trigger the transition
	 * @throws TimeoutException 
	 */
	public boolean hasModel(S input, BooleanAlgebra<P, S> ba, LinkedList<S> registerValues) throws TimeoutException {
		Set<Integer> registersWithInput = new HashSet<Integer>();

		for (Integer registerE : E)
		{
			boolean regIsNull = registerValues.get(registerE) == null;
			boolean regNotNullAndNotInput = registerValues.get(registerE) != null &&
					!registerValues.get(registerE).equals(input);

			if (regIsNull || regNotNullAndNotInput)
				return false;
		}

		for (Integer registerI : I)
			if (registerValues.get(registerI) != null && registerValues.get(registerI).equals(input))
				return false;

		return ba.HasModel(guard, input);
	}

	/**
	 * Create the dot representation of the move
	 */
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/{%s},{%s},{%s}\"]\n", from, to, guard, E, I, U);
	}

	public String toString() {
		return String.format("S: %s -%s/{%s},{%s},{%s}-> %s", from, guard, E, I, U, to);
	}

	/**
	 * Checks if the move is disjoint from the move <code>t</code> (they are not from same state on same predicate)
	 * @throws TimeoutException 
	 */
	public boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException {
		if (from.equals(t.from)) {
			if (E != t.E || I != t.I || U != t.U) {
				return true;
			}
			if(ba.IsSatisfiable(ba.MkAnd(guard, t.guard)) || (E == t.E && I == t.I && U == t.U))
				return false;
		}
		return true;
	}

	public Object clone(){
		return new SRAMove<P, S>(from, to, guard, E, I, U);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SRAMove<?, ?>) {
			SRAMove<?, ?> otherCasted = (SRAMove<?, ?>) other;
			return otherCasted.from.equals(from) && otherCasted.to.equals(to) &&
					otherCasted.guard.equals(guard) &&
					otherCasted.E.equals(E) &&
					otherCasted.I.equals(I) &&
					otherCasted.U.equals(U);
		}
		return false;
	}

	/**
	 * Returns the powerset of <code>originalSet</code> - O(n^2)
	 */
	private HashSet<HashSet<Integer>> getPowerset(HashSet<Integer> originalSet) {
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

	public <S> HashSet<HashSet<Integer>> getCompatibleSets(HashSet<Integer> regSet) {
		HashSet<Integer> setWithoutReg = new HashSet<>(regSet);
		setWithoutReg.removeAll(E);
		setWithoutReg.removeAll(I);

        HashSet<HashSet<Integer>> powSet = getPowerset(setWithoutReg);
        for (HashSet<Integer> set: powSet)
            set.addAll(E);

        return powSet;
	}
}

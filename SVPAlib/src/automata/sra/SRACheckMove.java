/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * SRACheckMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRACheckMove<P, S> extends SRAMove<P, S> {

	/**
	 * Constructs an SRA Check Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
     * Check transitions happen iff the predicate is true and the register mentioned matches the input symbol.
	 */
	public SRACheckMove(Integer from, Integer to, P guard, Integer registerIndex) {
		super(from, to, guard, Collections.singleton(registerIndex), Collections.emptySet(), Collections.emptySet());
    }	

	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal) throws TimeoutException {
		return boolal.IsSatisfiable(guard);
	}

    @Override
    public S getWitness(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
		if (registerValues.get(E.iterator().next()) != null)
        	return boolal.generateWitness(boolal.MkAnd(guard, boolal.MkAtom(registerValues.get(E.iterator().next()))));
		return null;
    }

    @Override
    public boolean hasModel(S input, BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
		if (registerValues.get(E.iterator().next()) != null)
        	return boolal.HasModel(boolal.MkAnd(guard, boolal.MkAtom(registerValues.get(E.iterator().next()))), input);
		return false;
    }

	@Override
	public boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException {
		if (from.equals(t.from)) {
            if (E != t.E) {
                return true;
            }
			SRACheckMove<P, S> ct = (SRACheckMove<P, S>) t;
			return !ba.IsSatisfiable(ba.MkAnd(guard,ct.guard));
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("S: %s -%s/%s-> %s", from, guard, E.iterator().next(), to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s\"]\n", from, to, guard, E.iterator().next());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SRACheckMove<?, ?>) {
			SRACheckMove<?, ?> otherCasted = (SRACheckMove<?, ?>) other;
			return otherCasted.from.equals(from) &&
				   otherCasted.to.equals(to) &&
				   otherCasted.guard.equals(guard) &&
				   otherCasted.E.equals(E) &&
				   otherCasted.I.equals(I) &&
				   otherCasted.U.equals(U);
		}

		return false;
	}

	@Override
	public Object clone() {
		  return new SRACheckMove<P, S>(from, to, guard, E.iterator().next());
	}

    @Override
    public LinkedList<MSRAMove<P, S>> asMultipleAssignment(LinkedList<S> registerValues) {
		// FIXME: Inaccurate translation. Should be fixed now?
		// LinkedList<MSRAMove<P, S>> maTransitions = new LinkedList<MSRAMove<P, S>>();

		// maTransitions.add(new MSRAMove<P, S>(from, to, guard, registerIndexes, new LinkedList<Integer>()));
		// return maTransitions;
		HashSet<Integer> indexesSet = new HashSet<Integer>();
		for (Integer index = 0; index < registerValues.size(); index++)
			indexesSet.add(index);

		LinkedList<MSRAMove<P, S>> maTransitions = new LinkedList<MSRAMove<P, S>>();
		for (HashSet<Integer> set : getPowersetIncluding(indexesSet, E))
			maTransitions.add(new MSRAMove<P, S>(from, to, guard, set, new HashSet<>()));
		return maTransitions;
    }

    public boolean isFresh() {
        return false;
    }

	public boolean isStore() {
		return false;
	}

	public boolean isMultipleAssignment() {
		return false;
	}
}

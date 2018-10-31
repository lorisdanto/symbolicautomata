/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.Collections;
import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * SRAFreshMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRAFreshMove<P, S> extends SRAMove<P, S> {

    /**
	 * Constructs an SRA Fresh Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
     * Fresh transitions happen iff the predicate is true and the input symbol is different from all the registers.
     * If this is true, then the input symbol is stored in the register mentioned
	 */
	public SRAFreshMove(Integer from, Integer to, P guard, Integer registerIndex) {
		super(from, to, guard, Collections.singleton(registerIndex));
	}
	
	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal) throws TimeoutException {
		return boolal.IsSatisfiable(guard);
	}

    @Override
    public S getWitness(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        P registerPredicates = boolal.True();
        for (S registerData : registerValues)
			if (registerData != null)
            	registerPredicates = boolal.MkAnd(registerPredicates, boolal.MkNot(boolal.MkAtom(registerData)));
        S witness = boolal.generateWitness(boolal.MkAnd(guard, registerPredicates));
        if (witness != null)
			registerValues.set(registerIndexes.iterator().next(), witness);
        return boolal.generateWitness(boolal.MkAnd(guard, registerPredicates));
    }

    @Override
    public boolean hasModel(S input, BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        P registerPredicates = boolal.True();
        for (S registerData : registerValues)
			if (registerData != null)
            	registerPredicates = boolal.MkAnd(registerPredicates, boolal.MkNot(boolal.MkAtom(registerData)));
        return boolal.HasModel(boolal.MkAnd(guard, registerPredicates), input);
    }

	@Override
	public boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException {
		if (from.equals(t.from)) {
            if (registerIndexes != t.registerIndexes) {
                return true;
            }
			SRAFreshMove<P, S> ct = (SRAFreshMove<P, S>) t;
			return !ba.IsSatisfiable(ba.MkAnd(guard,ct.guard));
		}
		return true;
	}

	@Override
	public String toString() {
		// TODO: Change fresh * to dot.
		return String.format("S: %s -%s/%s*-> %s", from, guard, registerIndexes.iterator().next(), to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s*\"]\n", from, to, guard, registerIndexes.iterator().next());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SRAFreshMove<?, ?>) {
			SRAFreshMove<?, ?> otherCasted = (SRAFreshMove<?, ?>) other;
			return otherCasted.from.equals(from) &&
				   otherCasted.to.equals(to) &&
				   otherCasted.guard.equals(guard) &&
				   otherCasted.registerIndexes.equals(registerIndexes);
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new SRAFreshMove<P, S>(from, to, guard, registerIndexes.iterator().next());
	}

    @Override
	public LinkedList<MSRAMove<P, S>> asMultipleAssignment(LinkedList<S> registerValues) {
		// FIXME: Inaccurate translation.
		LinkedList<MSRAMove<P, S>> maTransitions = new LinkedList<MSRAMove<P, S>>();
		maTransitions.add(new MSRAMove<P, S>(from, to, guard, new LinkedList<Integer>(), registerIndexes));
		return maTransitions;
    }

    public boolean isFresh() {
        return true;
    }

	public boolean isStore() {
		return false;
	}

	public boolean isMultipleAssignment() {
		return false;
	}
}

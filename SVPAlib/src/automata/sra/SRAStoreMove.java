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
 * SRAStoreMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRAStoreMove<P, S> extends SRAMove<P, S> {

    /**
     * Constructs an SRA Store Transition that starts from state <code>from</code> and ends at state
     * <code>to</code> with input <code>input</code>
     * Store transitions happen iff the predicate is true.
     * If this is true, then the input symbol is stored in the register mentioned.
     */
    public SRAStoreMove(Integer from, Integer to, P guard, Integer registerIndex) {
        super(from, to, guard, Collections.emptySet(), Collections.emptySet(), Collections.singleton(registerIndex));
    }

    @Override
    public boolean isSatisfiable(BooleanAlgebra<P, S> boolal) throws TimeoutException {
        return boolal.IsSatisfiable(guard);
    }

    @Override
    public S getWitness(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        return boolal.generateWitness(guard);
    }

    @Override
    public boolean hasModel(S input, BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        return boolal.HasModel(guard, input);
    }

    @Override
    public boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException {
        if (from.equals(t.from)) {
            if (U != t.U) {
                return true;
            }
            SRAStoreMove<P, S> ct = (SRAStoreMove<P, S>) t;
            return !ba.IsSatisfiable(ba.MkAnd(guard,ct.guard));
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("S: %s -%s/%s=-> %s", from, guard, U.iterator().next(), to);
    }

    @Override
    public String toDotString() {
        return String.format("%s -> %s [label=\"%s/%s=\"]\n", from, to, guard, U.iterator().next());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SRAStoreMove<?, ?>) {
            SRAStoreMove<?, ?> otherCasted = (SRAStoreMove<?, ?>) other;
            return otherCasted.from.equals(from) &&
                   otherCasted.to.equals(to) &&
                   otherCasted.guard.equals(guard) &&
                   otherCasted.U.equals(U);
        }

        return false;
    }

    @Override
    public Object clone(){
        return new SRAStoreMove<P, S>(from, to, guard, U.iterator().next());
    }

    @Override
    public LinkedList<MSRAMove<P, S>> asMultipleAssignment(LinkedList<S> registerValues) {
        HashSet<Integer> indexesSet = new HashSet<Integer>();
        for (Integer index = 0; index < registerValues.size(); index++)
            indexesSet.add(index);

        LinkedList<MSRAMove<P, S>> maTransitions = new LinkedList<MSRAMove<P, S>>();
        for (HashSet<Integer> set : getPowerset(indexesSet))
            maTransitions.add(new MSRAMove<P, S>(from, to, guard, set, U));
        return maTransitions;
    }

    public boolean isFresh() {
        return false;
    }

    public boolean isStore() {
        return true;
    }

    public boolean isMultipleAssignment() {
        return false;
    }
}

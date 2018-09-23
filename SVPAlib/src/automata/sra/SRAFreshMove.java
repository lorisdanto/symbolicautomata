/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.Arrays;
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
		super(from, to, guard, Arrays.asList(registerIndex));
	}
	
	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        P registerPredicates = boolal.True();
        for (S registerData : registerValues)
            registerPredicates = boolal.MkAnd(registerPredicates, boolal.MkNot(boolal.MkAtom(registerData)));
		return boolal.IsSatisfiable(boolal.MkAnd(guard, registerPredicates));
	}

    @Override
    public S getWitness(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        P registerPredicates = boolal.True();
        for (S registerData : registerValues)
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
            registerPredicates = boolal.MkAnd(registerPredicates, boolal.MkNot(boolal.MkAtom(registerData)));
        return boolal.HasModel(boolal.MkAnd(guard, registerPredicates), input);
    }

	@Override
	public boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException {
		if (from.equals(t.from)) {
            if (registerIndexes != t.registerIndexes) {
                return true;
            }
			SRACheckMove<P, S> ct = (SRACheckMove<P, S>) t;
			if(ba.IsSatisfiable(ba.MkAnd(guard,ct.guard)))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("S: %s -%s/%s*-> %s", from, guard, registerIndexes.iterator().next(), to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s*\"]\n", from, to, guard, registerIndexes.iterator().next());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SRACheckMove<?, ?>) {
			SRACheckMove<?, ?> otherCasted = (SRACheckMove<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to && otherCasted.guard==guard && otherCasted.registerIndexes == registerIndexes;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new SRACheckMove<P, S>(from, to, guard, registerIndexes.iterator().next());
	}

    @Override
    public MSRAMove<P, S> asMultipleAssignment() {
       return new MSRAMove<P, S>(from, to, guard, new LinkedList<Integer>(), registerIndexes);
    }

    public boolean isFresh() {
        return true;
    }

	public boolean isMultipleAssignment() {
		return false;
	}
}

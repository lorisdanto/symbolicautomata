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
		super(from, to, guard, Arrays.asList(registerIndex));
    }	

	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
		return boolal.IsSatisfiable(boolal.MkAnd(guard, boolal.MkAtom(registerValues.get(registerIndexes.iterator().next()))));
	}

    @Override
    public S getWitness(BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        return boolal.generateWitness(boolal.MkAnd(guard, boolal.MkAtom(registerValues.get(registerIndexes.iterator().next()))));
    }

    @Override
    public boolean hasModel(S input, BooleanAlgebra<P, S> boolal, LinkedList<S> registerValues) throws TimeoutException {
        return boolal.HasModel(boolal.MkAnd(guard, boolal.MkAtom(registerValues.get(registerIndexes.iterator().next()))), input);
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
		return String.format("S: %s -%s/%s-> %s", from, guard, registerIndexes.iterator().next(), to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s\"]\n", from, to, guard, registerIndexes.iterator().next());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SRACheckMove<?, ?>) {
			SRACheckMove<?, ?> otherCasted = (SRACheckMove<?, ?>) other;
			return otherCasted.from == from && otherCasted.to == to && otherCasted.guard == guard && otherCasted.registerIndexes == registerIndexes;
		}

		return false;
	}

	@Override
	public Object clone() {
		  return new SRACheckMove<P, S>(from, to, guard, registerIndexes.iterator().next());
	}

    @Override
    public MSRAMove<P, S> asMultipleAssignment() {
       return new MSRAMove<P, S>(from, to, guard, registerIndexes, new LinkedList<Integer>());
    }

    public boolean isFresh() {
        return false;
    }

	public boolean isMultipleAssignment() {
		return false;
	}
}

/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

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
     * Fresh transitions only happen iff the predicate is true and the input symbol is different from all the registers
     * If this is true, then the input symbol is stored in the register mentioned
	 */
	public SRAFreshMove(Integer from, Integer to, P guard, Integer register) {
		super(from, to, guard, register);
	}
	
	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal) throws TimeoutException {
        // FIXME: Requires knowledge of the registers at time of transition.
        //        Possible solution: let guard = guard AND a not in R.
		return boolal.IsSatisfiable(guard);
	}
	
	@Override
	public boolean isDisjointFrom(SRAMove<P, S> t, BooleanAlgebra<P, S> ba) throws TimeoutException{
		if (from.equals(t.from)){			
			SRACheckMove<P, S> ct = (SRACheckMove<P, S>) t;
            // FIXME: Is having the same register required for disjunction?
			if(ba.IsSatisfiable(ba.MkAnd(guard,ct.guard)))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("S: %s -%s/%s*-> %s", from, guard, register, to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s*\"]\n", from, to, guard, register);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SRACheckMove<?, ?>) {
			SRACheckMove<?, ?> otherCasted = (SRACheckMove<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to && otherCasted.guard==guard && otherCasted.register == register;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new SRACheckMove<P, S>(from, to, guard, register);
	}

	@Override
	public S getWitness(BooleanAlgebra<P, S> ba) throws TimeoutException {
        // FIXME: Again, does the witness need to be equal to the register at time of transition?
		return ba.generateWitness(guard);
	}

	@Override
	public boolean hasModel(S el, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return ba.HasModel(guard, el);
	}
	
}

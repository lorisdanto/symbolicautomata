/**
 * 
 */
package automata.svpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.sat4j.specs.TimeoutException;

import automata.svpa.TaggedSymbol.SymbolTag;

import theory.BooleanAlgebra;
import utilities.Pair;

public class Internal<U, S> extends SVPAMove<U, S> {

	U guard;

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public Internal(Integer from, Integer to, U guard) {
		super(from, to, Tag.Internal);
		this.guard = guard;
	}

	public boolean isDisjointFrom(SVPAMove<U, S> t, BooleanAlgebra<U, S> ba) throws TimeoutException {
		if (t instanceof Internal)
			if (from.equals(t.from)) {
				List<U> conjuncts = new ArrayList<U>();
				conjuncts.add(guard);
				conjuncts.add(((Internal<U, S>) t).guard);
				if (ba.IsSatisfiable(ba.MkAnd(conjuncts)))
					return false;
			}
		return true;
	}

	public boolean isSatisfiable(BooleanAlgebra<U, S> boolal) throws TimeoutException {
		return boolal.IsSatisfiable(guard);
	}

	public Pair<Integer, Stack<Pair<Integer, S>>> getNextState(
			Pair<Integer, Stack<Pair<Integer, S>>> state,
			TaggedSymbol<S> input, BooleanAlgebra<U, S> ba) throws TimeoutException {

		if (input.tag == SymbolTag.Internal) {
			Integer currState = state.first;
			if (currState == from) {
				Stack<Pair<Integer, S>> currStack = state.second;
				if (ba.HasModel(guard, input.input)) {
					@SuppressWarnings("unchecked")
					Stack<Pair<Integer, S>> newStack = (Stack<Pair<Integer, S>>) currStack
							.clone();
					return new Pair<Integer, Stack<Pair<Integer, S>>>(to,
							newStack);
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return String.format("I: %s -%s-> %s",from,guard, to);
	}
	
	@Override
    public int hashCode() {
		int hashZero = (from+2)*2+from;
		int hashFirst = (to+hashZero)*hashZero+to;
    	return (hashFirst+guard.hashCode())*hashFirst+guard.hashCode();
    }

    @Override
	public boolean equals(Object other) {
		if (other instanceof Internal<?, ?>) {
			Internal<?, ?> otherCasted = (Internal<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to && otherCasted.guard==guard;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new Internal<U, S>(from, to, guard);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s\"]\n", from,to,guard);
	}
	
	@Override
	public boolean isEpsilonTransition() {
		return false;
	}
	
	@Override
	public S getWitness(BooleanAlgebra<U, S> ba) throws TimeoutException {
		return ba.generateWitness(guard);
	}
}

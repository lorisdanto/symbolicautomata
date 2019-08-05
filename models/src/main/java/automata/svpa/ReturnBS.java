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

public class ReturnBS<U, S> extends SVPAMove<U, S> {

	U guard;

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public ReturnBS(Integer from, Integer to, U guard) {
		super(from, to, Tag.ReturnBS);

		this.guard = guard;
	}

	public boolean isDisjointFrom(SVPAMove<U, S> t,
			BooleanAlgebra<U, S> ba) throws TimeoutException {
		if (t instanceof ReturnBS)
			if (from.equals(t.from)) {
				List<U> conjuncts = new ArrayList<U>();
				conjuncts.add(guard);
				conjuncts.add(((ReturnBS<U, S>) t).guard);
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
		
		if (input.tag == SymbolTag.Return) {
			Integer currState = state.first;
			if (currState == from) {
				Stack<Pair<Integer, S>> currStack = state.second;

				if (currStack.size() == 0
						&& ba.HasModel(guard, input.input)) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("R: %s -%s, _-> %s",from,guard, to);
	}
	
	@Override
    public int hashCode() {
		int hashZero = (from+4)*4+from;
		int hashFirst = (to+hashZero)*hashZero+to;
    	return (hashFirst+guard.hashCode())*hashFirst+guard.hashCode();
    }

    @Override
	public boolean equals(Object other) {
		if (other instanceof ReturnBS<?, ?>) {
			ReturnBS<?, ?> otherCasted = (ReturnBS<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to && otherCasted.guard==guard;
		}

		return false;
	}
	
	public Object clone(){
		  return new ReturnBS<U, S>(from.intValue(), to.intValue(), guard);
	}
	
	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"R%s/_\", fontcolor=blue]\n", from,to,guard.toString());
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

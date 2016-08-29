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

public class Return<U, S> extends SVPAMove<U, S> {

	Integer stackState;
	U guard;

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public Return(Integer from, Integer to, Integer stackState, U guard) {
		super(from, to, Tag.Return);

		this.guard = guard;
		this.stackState = stackState;
	}

	public boolean isDisjointFrom(SVPAMove<U, S> t, BooleanAlgebra<U, S> ba) throws TimeoutException {
		if (t instanceof Return)
			if (from.equals(t.from) && stackState == ((Return<U, S>) t).stackState){
				List<U> conjuncts = new ArrayList<U>();
				conjuncts.add(guard);
				conjuncts.add(((Return<U, S>) t).guard);
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
				if (currStack.size() > 0) {
					Pair<Integer, S> stackTop = currStack.peek();

					if (stackTop.first == stackState
							&& ba.HasModel(guard, stackTop.second, input.input)) {
						@SuppressWarnings("unchecked")
						Stack<Pair<Integer, S>> newStack = (Stack<Pair<Integer, S>>) currStack
								.clone();
						newStack.pop();
						return new Pair<Integer, Stack<Pair<Integer, S>>>(to,
								newStack);
					}
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
		return String.format("R: %s -%s, %s-> %s", from, guard, stackState, to);
	}

	@Override
    public int hashCode() {
		int hashZero = (from+3)*3+from;
		int hashFirst = (hashZero+to)*to+hashZero;
    	int hashSec = (hashFirst+stackState)*hashFirst+stackState;
    	return (hashSec+guard.hashCode())*hashSec+guard.hashCode();
    }

    @Override
	public boolean equals(Object other) {
		if (other instanceof Return<?, ?>) {
			Return<?, ?> otherCasted = (Return<?, ?>) other;
			return otherCasted.from == from && otherCasted.to == to
					&& otherCasted.guard == guard
					&& otherCasted.stackState == stackState;
		}

		return false;
	}

	public Object clone() {
		return new Return<U, S>(from, to, stackState, guard);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"R%s/%s\", fontcolor=blue]\n", from,to,guard, stackState);
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
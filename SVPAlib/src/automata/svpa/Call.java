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

public class Call<U,S> extends SVPAMove<U,S> {

	Integer stackState;
	U guard;
	
	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	public Call(Integer from, Integer to, Integer stackState, U guard) {
		super(from, to, Tag.Call);
		this.guard=guard;
		this.stackState = stackState;
	}
	
	public boolean isDisjointFrom(SVPAMove<U,S> t, BooleanAlgebra<U,S> ba) throws TimeoutException{
		if(t instanceof Call)
			if (from.equals(t.from)){
				List<U> conjuncts= new ArrayList<U>();
				conjuncts.add(guard);
				conjuncts.add(((Call<U,S>)t).guard);
				if(ba.IsSatisfiable(ba.MkAnd(conjuncts)))
					return false;
			}
		return true;
	}

	public boolean isSatisfiable(BooleanAlgebra<U,S> boolal) throws TimeoutException{
		return boolal.IsSatisfiable(guard);
	}
	
	public Pair<Integer, Stack<Pair<Integer, S>>> getNextState(Pair<Integer, Stack<Pair<Integer, S>>> state,
			TaggedSymbol<S> input, BooleanAlgebra<U, S> ba) throws TimeoutException{
		if(input.tag==SymbolTag.Call)
			if (ba.HasModel(guard, input.input)) {
				@SuppressWarnings("unchecked")
				Stack<Pair<Integer, S>> newStack = (Stack<Pair<Integer, S>>) state.second.clone();
				newStack.push(new Pair<Integer, S>(stackState,
						input.input));
				return new Pair<Integer, Stack<Pair<Integer, S>>>(
								to, newStack);
			}
		return null;		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("C: %s -%s, %s-> %s",from,guard, stackState, to);
	}

	@Override
    public int hashCode() {
		int hashZero = (from+0)*0+from;
		int hashFirst = (hashZero+to)*to+hashZero;
    	int hashSec = (hashFirst+stackState)*hashFirst+stackState;
    	return (hashSec+guard.hashCode())*hashSec+guard.hashCode();
    }

    @Override
	public boolean equals(Object other) {
		if (other instanceof Call<?, ?>) {
			Call<?, ?> otherCasted = (Call<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to && otherCasted.guard==guard && otherCasted.stackState==stackState;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new Call<U, S>(from, to, stackState, guard);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"C%s/%s\", fontcolor=red]\n", from,to,guard, stackState);
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

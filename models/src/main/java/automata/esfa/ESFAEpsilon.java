package automata.esfa;

import java.util.List;

import automata.esfa.ESFAMove;
import theory.BooleanAlgebra;

/**
 * Epsilon move of an SFA
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class ESFAEpsilon<U,S> extends ESFAMove<U,S> {

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	public ESFAEpsilon(Integer from, Integer to) {
		super(from, to);
	}

	@Override
	public boolean isDisjointFrom(ESFAMove<U,S> t, BooleanAlgebra<U,S> ba){		
		return t.from!=from;
	}

	@Override
	public boolean isSatisfiable(BooleanAlgebra<U, S> boolal) {
		return true;
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"&#949;\"]\n", from,to);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ESFAEpsilon<?, ?>) {
			ESFAEpsilon<?, ?> otherCasted = (ESFAEpsilon<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to;
		}

		return false;
	}
	
	@Override
	public String toString() {
		return String.format("E: %s --> %s", from, to);
	}
	
	@Override
	public Object clone(){
		  return new ESFAEpsilon<U, S>(from, to);
	}

	@Override
	public boolean isEpsilonTransition() {
		return true;
	}

	@Override
	public List<S> getWitness(BooleanAlgebra<U, S> boolal) {
		return null;
	}

	@Override
	public boolean hasModel(List<S> el, BooleanAlgebra<U, S> ba) {
		return false;
	}

}

package automata.esfa;


import java.util.List;

import automata.esfa.ESFAMove;
import theory.BooleanAlgebra;

/**
 * SFAInputMove
 * @param <P> set of predicates over the domain S*
 * @param <S> domain of the automaton alphabet
 */
public class ESFAInputMove<P,S> extends ESFAMove<P, S>{

	/**
	 * <code> P <code> is a predicate from S^k -> {0,1}
	 */
	public P guard; 
	public Integer lookahead;
	
	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	public ESFAInputMove(Integer from, Integer to, P guard, Integer lookahead) {
		super(from, to);
		this.guard=guard;
		this.lookahead = lookahead;
	}
	
	@Override
	public boolean isSatisfiable(BooleanAlgebra<P,S> boolal){
		return boolal.IsSatisfiable(guard);
	}
	
	@Override
	public boolean isDisjointFrom(ESFAMove<P,S> t, BooleanAlgebra<P,S> ba){
		// TODO Auto-generated method stub
		return false;
		/*
		 * if(t.isEpsilonTransition())
			return true;
		if (from.equals(t.from)){			
			ESFAInputMove<P, S> ct = (ESFAInputMove<P, S>) t;			
			if(ba.IsSatisfiable(ba.MkAnd(guard,ct.guard)))
				return false;
		}
		return true;
		*/
	}

	@Override
	public String toString() {
		return String.format("S: %s -%s(%s)-> %s",from,guard,lookahead, to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s\",lookahead=\"%s\"]\n", from,to,guard,lookahead);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ESFAInputMove<?, ?>) {
			ESFAInputMove<?, ?> otherCasted = (ESFAInputMove<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to && otherCasted.guard==guard&&lookahead==otherCasted.lookahead;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new ESFAInputMove<P, S>(from,to, guard,lookahead);
	}

	@Override
	public boolean isEpsilonTransition() {
		return false;
	}

	@Override
	public S getWitness(BooleanAlgebra<P, S> ba) {
		return ba.generateWitness(guard);
	}

	@Override
	public boolean hasModel(S el, BooleanAlgebra<P, S> ba) {
		return ba.HasModel(guard, el);
	}

	@Override
	public boolean hasModel(List<S> input, BooleanAlgebra<P, S> ba, Integer lookahead) {
		return ba.HasModel(guard, input.subList(0, lookahead-1), lookahead);
	}


	
}

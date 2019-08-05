/**
 * SVPAlib
 * automata
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package automata.safa;

import java.util.Collections;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

/**
 * SFAInputMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SAFAInputMove<P,S> {

	public Integer from;
	public PositiveBooleanExpression to;
	public Set<Integer> toStates;
	public int maxState;
	
	public P guard;
		
	public SAFAInputMove(Integer from, PositiveBooleanExpression to, P guard) {
		super();
		this.from = from;
		this.to = to;
		toStates = to.getStates();
		if (toStates.isEmpty()) {
			maxState = -1;
		} else {
			maxState = Collections.max(toStates);
		}
		if (maxState < from) {
			maxState = from;
		}
		this.guard = guard;
	}

	public boolean isSatisfiable(BooleanAlgebra<P,S> boolal) throws TimeoutException{
		return boolal.IsSatisfiable(guard);
	}
	
	public S getWitness(BooleanAlgebra<P, S> ba) throws TimeoutException {
		return ba.generateWitness(guard);
	}
	
	public boolean hasModel(S el, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return ba.HasModel(guard, el);
	}

	@Override
	public String toString() {
		return String.format("S: %s -%s-> %s",from,guard, to);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SAFAInputMove<?, ?>) {
			SAFAInputMove<?, ?> otherCasted = (SAFAInputMove<?, ?>) other;
			return otherCasted.from==from && otherCasted.to.equals(to) && otherCasted.guard==guard;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new SAFAInputMove<P, S>(from,to, guard);
	}

	
}

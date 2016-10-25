/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * SSTInputMove
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
*/
public class SSTInputMove<P, F, S> extends SSTMove<P, F, S>{

	public P guard;
	public FunctionalVariableUpdate<P, F, S> variableUpdate;
	
	/**
	 * SST Transition that from <code>from</code> to
	 * <code>to</code> with input <code>guard</code> and variable
	 * update <code>variableUpdate</code>
	 */
	public SSTInputMove(Integer from, 
			Integer to, P guard, 
			FunctionalVariableUpdate<P, F, S> variableUpdate) {
		super(from, to);		
		this.guard=guard;
		this.variableUpdate = variableUpdate;
	}
	
	@Override
	public boolean isSatisfiable(BooleanAlgebra<P,S> ba) throws TimeoutException{
		return ba.IsSatisfiable(guard);
	}
	
	@Override
	public boolean isDisjointFrom(SSTMove<P, F, S> t, BooleanAlgebra<P,S> ba) throws TimeoutException{
		if(t.isEpsilonTransition())
			return true;
		if (from.equals(t.from)){			
			SSTInputMove<P, F, S> ct = (SSTInputMove<P, F, S>) t;			
			if(ba.IsSatisfiable(ba.MkAnd(guard,ct.guard)))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("S: %s -%s/%s-> %s",from,guard, variableUpdate, to);
	}

	@Override
	public String toDotString() {
		String label = guard+"/\n";
		label += variableUpdate;
		return String.format("%s -> %s [label=\"%s\"]\n", from,to, label);
	}

	@Override
	public boolean equals(Object other) {
		//Not that correct
		if (other instanceof SSTInputMove<?,?, ?>) {
			SSTInputMove<?,?, ?> otherCasted = (SSTInputMove<?,?, ?>) other;
			return otherCasted.from==from 
					&& otherCasted.to==to 
					&& otherCasted.guard==guard;
//					&& otherCasted.variableUpdate==variableUpdate;
		}

		return false;
	}

	@Override
	public Object clone(){
		  return new SSTInputMove<P, F, S>(from,to, guard, variableUpdate);
	}

	@Override
	public boolean isEpsilonTransition() {
		return false;
	}

	@Override
	public S getWitness(BooleanAlgebra<P, S> ba) throws TimeoutException {		
		return ba.generateWitness(guard);
	}

	@Override
	public boolean hasModel(S el, BooleanAlgebra<P, S> ba) throws TimeoutException {
		return ba.HasModel(guard, el);
	}
	
}

/**
 * 
 */
package transducers.sst;

import theory.BooleanAlgebra;

public class SSTInputMove<P, F, S> extends SSTMove<P, F, S>{

	public P guard;
	public FunctionalVariableUpdate<P, F, S> variableUpdate;
	
	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
//	public SSTInputMove(Integer from, Integer to, U guard, List<Token<P, F, S>>[] variableUpdate) {
//		super(from, to, variableUpdate);
//		this.guard=guard;
//	}
	
	public SSTInputMove(Integer from, 
			Integer to, P guard, 
			FunctionalVariableUpdate<P, F, S> variableUpdate) {
		super(from, to);		
		this.guard=guard;
		this.variableUpdate = variableUpdate;
	}
	
	public boolean isSatisfiable(BooleanAlgebra<P,S> ba){
		return ba.IsSatisfiable(guard);
	}
	
	public boolean isDisjointFrom(SSTMove<P, F, S> t, BooleanAlgebra<P,S> ba){
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
	public S getWitness(BooleanAlgebra<P, S> ba) {		
		return ba.generateWitness(guard);
	}

	@Override
	public boolean hasModel(S el, BooleanAlgebra<P, S> ba) {
		return ba.HasModel(guard, el);
	}
	
}

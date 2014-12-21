/**
 * 
 */
package transducers.sst;

import theory.BooleanAlgebra;

public class SSTEpsilon<P, F, S> extends SSTMove<P, F, S> {	
	
	SimpleVariableUpdate<P, F, S> variableUpdate;
	
	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	
	public SSTEpsilon(Integer from, Integer to, SimpleVariableUpdate<P, F, S> variableUpdate) {
		super(from, to);
		this.variableUpdate = variableUpdate;
	}

	public boolean isDisjointFrom(SSTMove<P, F, S> t, BooleanAlgebra<P,S> ba){		
		return t.from!=from;
	}

	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal) {
		return true;
	}

	@Override
	public String toDotString() {
		String label = "&#949;/\n";
		label += variableUpdate;
		return String.format("%s -> %s [label=\"%s\"]\n", from, to, label);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SSTEpsilon<?,?, ?>) {
			SSTEpsilon<?, ?, ?> otherCasted = (SSTEpsilon<?, ?, ?>) other;
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
		  return new SSTEpsilon<P, F, S>(from, to, variableUpdate);
	}

	@Override
	public boolean isEpsilonTransition() {
		return true;
	}

	@Override
	public S getWitness(BooleanAlgebra<P, S> boolal) {
		return null;
	}

	@Override
	public boolean hasModel(S el, BooleanAlgebra<P, S> ba) {
		return false;
	}
	
}

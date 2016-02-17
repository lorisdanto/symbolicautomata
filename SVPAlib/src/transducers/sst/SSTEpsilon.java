/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import theory.BooleanAlgebra;

/**
 * SST epsilon transition
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SSTEpsilon<P, F, S> extends SSTMove<P, F, S> {	
	
	SimpleVariableUpdate<P, F, S> variableUpdate;
	
	/**
	 * An Epsilon transition from <code>from<code> to <code>to</code> performing the update
	 * <code>variableUpdate</code>
	 */
	public SSTEpsilon(Integer from, Integer to, SimpleVariableUpdate<P, F, S> variableUpdate) {
		super(from, to);
		this.variableUpdate = variableUpdate;
	}

	@Override
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
		return String.format("E: %s -%s-> %s", from, variableUpdate, to);
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

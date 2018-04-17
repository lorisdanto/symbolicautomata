/**
 * SVPAlib
 * transducers.sft
 * Mar 6, 2018
 * @author Loris D'Antoni
 */

package transducers.sft;

import java.util.List;
import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * SFTInputMove
 * @param <P>
 *			The type of predicates forming the Boolean algebra
 * @param <F>
 *			The type of functions S->S in the Boolean Algebra
 * @param <S>
 *			The domain of the Boolean algebra
*/
public class SFTInputMove<P, F, S> extends SFTMove<P, F, S>{

	public P guard;
	public List<F> outputFunctions; // a sequence of lambda-terms over a given label theory for sigma -> gamma

	/**
	 * SFT Transition that from <code>from</code> to
	 * <code>to</code> with input <code>guard</code> and a sequence of
	 * output functions <code>outputFunctions</code>
	 */
	public SFTInputMove(Integer from, Integer to, P guard, List<F> outputFunctions) {
		super(from, to);		
		this.guard=guard;
		this.outputFunctions = outputFunctions;
	}

	
	@Override
	public boolean isSatisfiable(BooleanAlgebra<P,S> ba) throws TimeoutException{
		return ba.IsSatisfiable(guard);
	}
	
	@Override
	public boolean isDisjointFrom(SFTMove<P, F, S> t, BooleanAlgebra<P,S> ba) throws TimeoutException{
		if(t.isEpsilonTransition())
			return true;
		if (from.equals(t.from)){			
			SFTInputMove<P, F, S> ct = (SFTInputMove<P, F, S>) t;			
			if(ba.IsSatisfiable(ba.MkAnd(guard, ct.guard)))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder stringOutputFunctions = new StringBuilder();
		for (F outputFunction: outputFunctions) {
			stringOutputFunctions.append(outputFunction.toString());
			stringOutputFunctions.append(' ');
		}
		return String.format("S: %s -%s/%s-> %s",from, guard, stringOutputFunctions.toString(), to);
	}

	@Override
	public String toDotString() {
		StringBuilder label = new StringBuilder(guard + "/\n");
		for (F outputFunction: outputFunctions) {
			label.append(outputFunction.toString());
			label.append('\n');
		}
		return String.format("%s -> %s [label=\"%s\"]\n", from, to, label.toString());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SFTInputMove<?, ?, ?>))
			return false;
		SFTInputMove<?, ?, ?> otherCasted = (SFTInputMove<?, ?, ?>) other;
		if (!otherCasted.from.equals(from))
			return false;
		if (!otherCasted.to.equals(to))
			return false;
		if (!otherCasted.guard.equals(guard))
			return false;
		if (otherCasted.outputFunctions.size() != outputFunctions.size())
			return false;
		for (int i = 0; i < outputFunctions.size(); i++)
			if (!outputFunctions.get(i).equals(otherCasted.outputFunctions.get(i)))
				return false;
		return true;
	}

	@Override
	public Object clone(){
		  return new SFTInputMove<P, F, S>(from, to, guard, outputFunctions);
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

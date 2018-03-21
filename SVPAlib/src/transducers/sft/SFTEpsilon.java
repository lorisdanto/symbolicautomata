/**
 * SVPAlib
 * transducers.sft
 * Mar 6, 2018
 * @author Loris D'Antoni
 */

package transducers.sft;

import java.util.List;

import theory.BooleanAlgebra;

/**
 * SFT epsilon transition
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of s S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SFTEpsilon<P, F, S> extends SFTMove<P, F, S> {

    // since it is an epsilon transition, there is no guard. We do not have to declare public P guard;
	public List<S> outputs;
	/**
	 * An Epsilon transition from <code>from<code> to <code>to</code> performing the <code>outputs</code>
	 */
	public SFTEpsilon(Integer from, Integer to, List<S> outputs) {
		super(from, to);
		this.outputs = outputs;
	}

	@Override
	public boolean isDisjointFrom(SFTMove<P, F, S> t, BooleanAlgebra<P,S> ba){
        return !t.from.equals(from);
	}

	@Override
	public boolean isSatisfiable(BooleanAlgebra<P, S> boolal) {
		return true;
	}

	@Override
	public String toDotString() {
        StringBuilder label = new StringBuilder("&#949;/\n");
        for (S output: outputs) {
            label.append(output.toString());
            label.append('\n');
        }
		return String.format("%s -> %s [label=\"%s\"]\n", from, to, label.toString());
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SFTEpsilon<?, ?, ?>))
			return false;
		SFTEpsilon<?, ?, ?> otherCasted = (SFTEpsilon<?, ?, ?>) other;
		if (!otherCasted.from.equals(from))
			return false;
		if (!otherCasted.to.equals(to))
			return false;
		if (otherCasted.outputs.size() != outputs.size())
			return false;
		for (int i = 0; i < outputs.size(); i++)
			if (!outputs.get(i).equals(otherCasted.outputs.get(i)))
				return false;
		return true;
	}
	
	@Override
	public String toString() {
        StringBuilder stringOutputs = new StringBuilder();
        for (S output: outputs) {
            stringOutputs.append(output.toString());
            stringOutputs.append(' ');
        }
		return String.format("E: %s -%s-> %s", from, stringOutputs.toString(), to);
	}
	
	@Override
	public Object clone(){
		  return new SFTEpsilon<P, F, S>(from, to, outputs);
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

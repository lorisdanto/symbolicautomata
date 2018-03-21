/**
 * SVPAlib
 * transducers.sft
 * Mar 20, 2018
 * @author Loris D'Antoni
 */

package transducers.sft;

import java.util.List;

import theory.BooleanAlgebra;

/**
 * the product of SFT epsilon transition
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of s S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SFTProductEpsilon<P, F, S> extends SFTMove<P, F, S> {

    // since it is an epsilon transition, there is no guard. We do not have to declare public P guard;
	public List<S> outputs1;
	public List<S> outputs2;
	
	/**
	 * An Epsilon transition from <code>from<code> to <code>to</code> performing the <code>outputs</code>
	 */
	public SFTProductEpsilon(Integer from, Integer to, List<S> outputs1, List<S> outputs2) {
		super(from, to);
		this.outputs1 = outputs1;
		this.outputs2 = outputs2;
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
        for (S output: outputs1) {
            label.append(output.toString());
            label.append('\n');
        }
        label.append("-----\n");
        for (S output: outputs2) {
            label.append(output.toString());
            label.append('\n');
        }
		return String.format("%s -> %s [label=\"%s\"]\n", from, to, label.toString());
	}
	
	@Override
	public boolean equals(Object other) {
        if (!(other instanceof SFTProductEpsilon<?, ?, ?>))
            return false;
        SFTProductEpsilon<?, ?, ?> otherCasted = (SFTProductEpsilon<?, ?, ?>) other;
        if (!otherCasted.from.equals(from))
            return false;
        if (!otherCasted.to.equals(to))
            return false;
        if (otherCasted.outputs1.size() != outputs1.size())
            return false;
        if (otherCasted.outputs2.size() != outputs2.size())
            return false;
        for (int i = 0; i < outputs1.size(); i++)
            if (!outputs1.get(i).equals(otherCasted.outputs1.get(i)))
                return false;
        for (int i = 0; i < outputs2.size(); i++)
            if (!outputs2.get(i).equals(otherCasted.outputs2.get(i)))
                return false;
        return true;
	}
	
	@Override
	public String toString() {
        StringBuilder stringOutputs = new StringBuilder();
        for (S output1: outputs1) {
            stringOutputs.append(output1.toString());
            stringOutputs.append(' ');
        }
        stringOutputs.append('|');
        for (S output2: outputs2) {
            stringOutputs.append(output2.toString());
            stringOutputs.append(' ');
        }
		return String.format("E: %s -%s-> %s", from, stringOutputs.toString(), to);
	}
	
	@Override
	public Object clone(){
		  return new SFTProductEpsilon<P, F, S>(from, to, outputs1, outputs2);
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

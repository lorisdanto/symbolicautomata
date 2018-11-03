/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.Collections;
import java.util.LinkedList;
import java.util.HashSet;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * SRAFreshMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRAFreshMove<P, S> extends SRAMove<P, S> {

    /**
	 * Constructs an SRA Fresh Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
     * Fresh transitions happen iff the predicate is true and the input symbol is different from all the registers.
     * If this is true, then the input symbol is stored in the register mentioned
	 */
	public SRAFreshMove(Integer from, Integer to, P guard, Integer registerIndex, Integer registerCount) {
		super(from, to, guard, Collections.emptySet(), new HashSet<Integer>(), Collections.singleton(registerIndex));
		for (Integer index = 0; index < registerCount; index++)
			I.add(index);
		this.registerIndex = registerIndex;

	}

	@Override
	public String toString() {
		// TODO: Change fresh * to dot.
		return String.format("S: %s -%s/%s*-> %s", from, guard, registerIndex, to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s*\"]\n", from, to, guard, registerIndex);
	}

	@Override
	public Object clone(){
		  return new SRAFreshMove<P, S>(from, to, guard, registerIndex, I.size());
	}

	public boolean isFresh() {
        return true;
    }

	public boolean isStore() {
		return false;
	}

	public boolean isMultipleAssignment() {
		return false;
	}
}

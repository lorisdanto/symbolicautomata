/**
 * SVPAlib
 * automata
 * Jul 25, 2018
 * @author Tiago Ferreira
 */
package automata.sra;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;

/**
 * SRACheckMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRACheckMove<P, S> extends SRAMove<P, S> {

	/**
	 * Constructs an SRA Check Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
     * Check transitions happen iff the predicate is true and the register mentioned matches the input symbol.
	 */
	public SRACheckMove(Integer from, Integer to, P guard, Integer registerIndex) {
		super(from, to, guard, Collections.singleton(registerIndex), Collections.emptySet(), Collections.emptySet());
		this.registerIndex = registerIndex;
    }	

	@Override
	public String toString() {
		return String.format("S: %s -%s/%s=-> %s", from, guard, registerIndex, to);
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"%s/%s=\"]\n", from, to, guard, E.iterator().next());
	}

	@Override
	public Object clone() {
		  return new SRACheckMove<P, S>(from, to, guard, registerIndex);
	}

    public boolean isFresh() {
        return false;
    }

	public boolean isStore() {
		return false;
	}

	public boolean isMultipleAssignment() {
		return false;
	}
}

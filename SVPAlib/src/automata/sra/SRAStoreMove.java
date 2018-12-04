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
 * SRAStoreMove
 * @param <P> set of predicates over the domain S
 * @param <S> domain of the automaton alphabet
 */
public class SRAStoreMove<P, S> extends SRAMove<P, S> {

    /**
     * Constructs an SRA Store Transition that starts from state <code>from</code> and ends at state
     * <code>to</code> with input <code>input</code>
     * Store transitions happen iff the predicate is true.
     * If this is true, then the input symbol is stored in the register mentioned.
     */
    public SRAStoreMove(Integer from, Integer to, P guard, Integer registerIndex) {
        super(from, to, guard, Collections.emptySet(), Collections.emptySet(), Collections.singleton(registerIndex));
        this.registerIndex = registerIndex;
    }

    @Override
    public String toString() {
        return String.format("S: %s -%s/%s->-> %s", from, guard,registerIndex, to);
    }

    @Override
    public String toDotString() {
        return String.format("%s -> %s [label=\"%s/%s->\"]\n", from, to, guard, registerIndex);
    }

    @Override
    public Object clone(){
        return new SRAStoreMove<P, S>(from, to, guard, registerIndex);
    }

    public boolean isFresh() {
        return false;
    }

    public boolean isStore() {
        return true;
    }

    public boolean isMultipleAssignment() {
        return false;
    }
}

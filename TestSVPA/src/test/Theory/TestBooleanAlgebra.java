package test.Theory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import theory.BooleanAlgebra;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class TestBooleanAlgebra {
   
    // ---------------------------------------
    // Predicates
    // ---------------------------------------
    UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();


    @Test
    public void testHasNDistinctWitnesses() {
        CharPred integers = new CharPred('0', '9');
        assertTrue(ba.hasNDistinctWitnesses(integers, 5));
        assertTrue(ba.hasNDistinctWitnesses(integers, 0));
        assertTrue(ba.hasNDistinctWitnesses(integers, 10));
        assertFalse(ba.hasNDistinctWitnesses(integers, 11));
    }
}	

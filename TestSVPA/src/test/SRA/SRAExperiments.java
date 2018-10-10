package test.SRA;

import automata.sra.SRA;
import automata.sra.SRACheckMove;
import automata.sra.SRAFreshMove;
import automata.sra.SRAMove;
import org.junit.Test;
import org.sat4j.specs.TimeoutException;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SRAExperiments {

    @Test
    public void testSSNChecker() throws TimeoutException {
        assertTrue(SSNChecker.accepts(validName1, ba));
        assertTrue(SSNChecker.accepts(validName2, ba));
        assertFalse(SSNChecker.accepts(invalidName1, ba));
        assertFalse(SSNChecker.accepts(invalidName2, ba));
        assertFalse(SSNChecker.accepts(invalidName3, ba));
    }

    // ---------------------------------------
    // Predicates
    // ---------------------------------------
    private UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
    private CharPred lowerAlpha = StdCharPred.LOWER_ALPHA;
    private CharPred upperAlpha = StdCharPred.UPPER_ALPHA;
    private CharPred comma = new CharPred(',');
    private CharPred space = new CharPred(' ');

    // Test strings
    List<Character> validName1 = lOfS("Tiago, Ferreira, TF"); // accepted by SSNChecker
    List<Character> validName2 = lOfS("Thomas, Thomson, TT"); // accepted by SSNChecker
    List<Character> invalidName1 = lOfS("Tiago, Ferreira, TA"); // not accepted by SSNChecker
    List<Character> invalidName2 = lOfS("Tiago, Ferreira, AA"); // not accepted by SSNChecker
    List<Character> invalidName3 = lOfS("Tiago, Ferreira, A"); // not accepted by SSNChecker
    List<Character> lb = lOfS("a3"); // accepted only by autB
    List<Character> lab = lOfS("a"); // accepted only by both autA and autB
    List<Character> lnot = lOfS("44"); // accepted only by neither autA nor autB

    private SRA<CharPred, Character> SSNChecker = getSSNChecker(ba);

	private SRA<CharPred, Character> getSSNChecker(UnaryCharIntervalSolver ba) {
		LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));

		Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
		// Read first initial and store it in register 0
		transitions.add(new SRAFreshMove<CharPred, Character>(0, 1, upperAlpha, 0));

		// Read an unbound number of lowercase letters for the rest of the first name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRACheckMove<CharPred, Character>(1, 1, lowerAlpha, 2));
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 1, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 2, comma, 2));

        // Read an unbound number of spaces.
        transitions.add(new SRACheckMove<CharPred, Character>(2, 2, space, 2));
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 2, space, 2));

        // Read the second initial and store it in register 1
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 3, upperAlpha, 1));

        // Read an unbound number of lowercase letters for the rest of the last name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(3, 3, lowerAlpha, 2));
        transitions.add(new SRACheckMove<CharPred, Character>(3, 3, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(3, 4, comma, 2));

        // Read an unbound number of spaces.
        transitions.add(new SRAFreshMove<CharPred, Character>(4, 4, space, 2));
        transitions.add(new SRACheckMove<CharPred, Character>(4, 4, space, 2));

        // Read the first initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(4, 5, upperAlpha, 0));

        // Read the second initial and compare it to register 1
        transitions.add(new SRACheckMove<CharPred, Character>(5, 6, upperAlpha, 1));

        // Read the second initial and check if it is a repeated initial
        transitions.add(new SRACheckMove<CharPred, Character>(2, 7, upperAlpha, 0));

        // Read an unbound number of lowercase letters for the rest of the last name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(7, 7, lowerAlpha, 2));
        transitions.add(new SRACheckMove<CharPred, Character>(7, 7, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(7, 8, comma, 2));

        // Read an unbound number of spaces.
        transitions.add(new SRAFreshMove<CharPred, Character>(8, 8, space, 2));
        transitions.add(new SRACheckMove<CharPred, Character>(8, 8, space, 2));

        // Read the first initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(8, 9, upperAlpha, 0));

        // Read the second initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(9, 10, upperAlpha, 0));

		try {
			return SRA.MkSRA(transitions, 0, Arrays.asList(6, 10), registers, ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// -------------------------
	// Auxiliary methods
	// -------------------------
	private List<Character> lOfS(String s) {
		List<Character> l = new ArrayList<Character>();
		char[] ca = s.toCharArray();
		for (int i = 0; i < s.length(); i++)
			l.add(ca[i]);
		return l;
	}

}

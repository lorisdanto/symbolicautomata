package test.SRA;

import automata.sra.*;
import org.junit.Test;
import org.sat4j.specs.TimeoutException;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSRAExperiments {

    @Test
    public void testSSNCheckerSRA() throws TimeoutException {
        assertTrue(SSNCheckerSRA.accepts(validName1, ba));
        assertTrue(SSNCheckerSRA.accepts(validName2, ba));
        assertFalse(SSNCheckerSRA.accepts(invalidName1, ba));
        assertFalse(SSNCheckerSRA.accepts(invalidName2, ba));
        assertFalse(SSNCheckerSRA.accepts(invalidName3, ba));
    }

    @Test
    public void testSSNCheckerMSRA() throws TimeoutException {
        assertTrue(SSNCheckerMSRA.accepts(validName1, ba));
        assertTrue(SSNCheckerMSRA.accepts(validName2, ba));
        assertFalse(SSNCheckerMSRA.accepts(invalidName1, ba));
        assertFalse(SSNCheckerMSRA.accepts(invalidName2, ba));
        assertFalse(SSNCheckerMSRA.accepts(invalidName3, ba));
    }

    @Test
    public void testSSNCheckerMSRAtoSRA() throws TimeoutException {
        SRA<CharPred, Character> toSRA = SSNCheckerMSRA.toSRA(ba, Long.MAX_VALUE);
        toSRA.createDotFile("ssnSra", "");
        assertTrue(toSRA.accepts(validName1, ba));
        assertTrue(toSRA.accepts(validName2, ba));
        assertFalse(toSRA.accepts(invalidName1, ba));
        assertFalse(toSRA.accepts(invalidName2, ba));
        assertFalse(toSRA.accepts(invalidName3, ba));
    }


//    @Test
//    public void testXMLCheckerSRA() throws TimeoutException {
//        boolean check = XMLCheckerSRA.createDotFile("xml", "");
//        assertTrue(check);
//        assertTrue(XMLCheckerSRA.accepts(validXML1, ba));
//        assertTrue(XMLCheckerSRA.accepts(validXML2, ba));
//        assertTrue(XMLCheckerSRA.accepts(validXML3, ba));
//        assertTrue(XMLCheckerSRA.accepts(validXML4, ba));
//        assertTrue(XMLCheckerSRA.accepts(validXML5, ba));
//        assertTrue(XMLCheckerSRA.accepts(validXML6, ba));
//        assertTrue(XMLCheckerSRA.accepts(validXML7, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML1, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML2, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML3, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML4, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML5, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML6, ba));
//        assertFalse(XMLCheckerSRA.accepts(invalidXML7, ba));
//    }

    // ---------------------------------------
    // Predicates
    // ---------------------------------------
    private UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
    private CharPred alpha = StdCharPred.ALPHA;
    private CharPred alphaNum = StdCharPred.ALPHA_NUM;
    private CharPred lowerAlpha = StdCharPred.LOWER_ALPHA;
    private CharPred upperAlpha = StdCharPred.UPPER_ALPHA;
    private CharPred comma = new CharPred(',');
    private CharPred space = new CharPred(' ');
    private CharPred open = new CharPred('<');
    private CharPred close = new CharPred('>');
    private CharPred slash = new CharPred('/');

    // SSN test strings
    private List<Character> validName1 = lOfS("Tiago, Ferreira, TF"); // accepted by SSNChecker
    private List<Character> validName2 = lOfS("Thomas, Thomson, TT"); // accepted by SSNChecker
    private List<Character> invalidName1 = lOfS("Tiago, Ferreira, TA"); // not accepted by SSNChecker
    private List<Character> invalidName2 = lOfS("Tiago, Ferreira, AA"); // not accepted by SSNChecker
    private List<Character> invalidName3 = lOfS("Tiago, Ferreira, A"); // not accepted by SSNChecker

    // XML test strings
    private List<Character> validXML1 = lOfS("<A></A>"); // accepted by XMLChecker
    private List<Character> validXML2 = lOfS("<AB></AB>"); // accepted by XMLChecker
    private List<Character> validXML3 = lOfS("<BB></BB>"); // accepted by XMLChecker
    private List<Character> validXML4 = lOfS("<ABB></ABB>"); // accepted by XMLChecker
    private List<Character> validXML5 = lOfS("<ABA></ABA>"); // accepted by XMLChecker
    private List<Character> validXML6 = lOfS("<AAB></AAB>"); // accepted by XMLChecker
    private List<Character> validXML7 = lOfS("<ABC></ABC>"); // accepted by XMLChecker
    private List<Character> invalidXML1 = lOfS("<A></>"); // not accepted by XMLChecker
    private List<Character> invalidXML2 = lOfS("<A><AB/>"); // not accepted by XMLChecker
    private List<Character> invalidXML3 = lOfS("<></A>"); // not accepted by XMLChecker
    private List<Character> invalidXML4 = lOfS("<A><A>"); // not accepted by XMLChecker
    private List<Character> invalidXML5 = lOfS("<AA></AB>"); // not accepted by XMLChecker
    private List<Character> invalidXML6 = lOfS("<AAA></CCC>"); // not accepted by XMLChecker
    private List<Character> invalidXML7 = lOfS("<ABA></ABC>"); // not accepted by XMLChecker

    // Automata
    private SRA<CharPred, Character> SSNCheckerSRA = getSSNCheckerSRA(ba);
    private SRA<CharPred, Character> SSNCheckerMSRA = getSSNCheckerMSRA(ba);
    private SRA<CharPred, Character> XMLCheckerSRA = getXMLCheckerSRA(ba);

    // TODO: Run over CSV, generate CSV data.
    // TODO: IP packets in the same subnet.
    // TODO: ISBN-10
    // https://regexr.com

	private SRA<CharPred, Character> getSSNCheckerSRA(UnaryCharIntervalSolver ba) {
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

    private SRA<CharPred, Character> getSSNCheckerMSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read the first initial and store it in register 0
        transitions.add(new MSRAMove<CharPred, Character>(0, 1, upperAlpha, Collections.emptyList(), Collections.singletonList(0)));

        // Read an unbound number of lowercase characters
        transitions.add(new MSRAMove<CharPred, Character>(1, 1, lowerAlpha, Collections.emptyList(), Collections.singletonList(2)));
        transitions.add(new MSRAMove<CharPred, Character>(1, 1, lowerAlpha, Collections.singletonList(2), Collections.emptyList()));

        // Read a comma
        transitions.add(new MSRAMove<CharPred, Character>(1, 2, comma, Collections.emptyList(), Collections.singletonList(2)));

        // Read an unbound number of spaces
        transitions.add(new MSRAMove<CharPred, Character>(2, 2, space, Collections.emptyList(), Collections.singletonList(2)));
        transitions.add(new MSRAMove<CharPred, Character>(2, 2, space, Collections.singletonList(2), Collections.emptyList()));

        // Read a different second initial or a repeated second initial and store it in 1
        transitions.add(new MSRAMove<CharPred, Character>(2, 3, upperAlpha, Collections.singletonList(0), Collections.singletonList(1)));
        transitions.add(new MSRAMove<CharPred, Character>(2, 3, upperAlpha, Collections.emptyList(), Collections.singletonList(1)));

        // Read an unbound number of lowercase characters
        transitions.add(new MSRAMove<CharPred, Character>(3, 3, lowerAlpha, Collections.emptyList(), Collections.singletonList(2)));
        transitions.add(new MSRAMove<CharPred, Character>(3, 3, lowerAlpha, Collections.singletonList(2), Collections.emptyList()));

        // Read a comma
        transitions.add(new MSRAMove<CharPred, Character>(3, 4, comma, Collections.emptyList(), Collections.singletonList(2)));

        // Read an unbound number of spaces
        transitions.add(new MSRAMove<CharPred, Character>(4, 4, space, Collections.emptyList(), Collections.singletonList(2)));
        transitions.add(new MSRAMove<CharPred, Character>(4, 4, space, Collections.singletonList(2), Collections.emptyList()));

        // Read a capital that matches both registers or a capital that matches the first register
        transitions.add(new MSRAMove<CharPred, Character>(4, 5, upperAlpha, Arrays.asList(0, 1), Collections.emptyList()));
        transitions.add(new MSRAMove<CharPred, Character>(4, 5, upperAlpha, Collections.singletonList(0), Collections.emptyList()));

        // Read a capital that matches both registers or a capital that matches the second register
        transitions.add(new MSRAMove<CharPred, Character>(5, 6, upperAlpha, Arrays.asList(0, 1), Collections.emptyList()));
        transitions.add(new MSRAMove<CharPred, Character>(5, 6, upperAlpha, Collections.singletonList(1), Collections.emptyList()));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(6), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getXMLCheckerSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read opening character
        transitions.add(new SRAFreshMove<CharPred, Character>(0, 1, open, 3));

        // Read repeated tag (AAA, BBB, ...)
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 2, alpha, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(2, 3, alpha, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(3, 4, alpha, 0));

        // Or read repated at the front tag (AAB, BBA, ...)
        transitions.add(new SRAFreshMove<CharPred, Character>(3, 4, alpha, 1));

        // Or read repeated in the ends tag (ABA, BAB, ...)
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 6, alpha, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(6, 4, alpha, 0));

        // Or read repeated at the end tag (ABB, BAA, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(6, 4, alpha, 1));

        // Or read unique tag (ABC, ...)
        transitions.add(new SRAFreshMove<CharPred, Character>(6, 4, alpha, 2));

        // Read closing character
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 5, close, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(3, 5, close, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(4, 5, close, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(6, 5, close, 3));

        // Read any content (or not)
        transitions.add(new SRAFreshMove<CharPred, Character>(5, 5, alphaNum, 3));
        transitions.add(new SRACheckMove<CharPred, Character>(5, 5, alphaNum, 3));

        // Read opening character and slash
        transitions.add(new SRAFreshMove<CharPred, Character>(5, 6, open, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(6, 7, slash, 3));

        // Read repeated tag (AAA, BBB, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(7, 8, alpha, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(8, 9, alpha, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(9, 10, alpha, 0));

        // Or read repated at the front tag (AAB, BBA, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(9, 10, alpha, 1));

        // Or read repeated in the ends tag (ABA, BAB, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(8, 11, alpha, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(11, 10, alpha, 0));

        // Or read unique tag (ABC, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(11, 10, alpha, 2));

        // Read closing character
        transitions.add(new SRAFreshMove<CharPred, Character>(8, 12, close, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(9, 12, close, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(10, 12, close, 3));
        transitions.add(new SRAFreshMove<CharPred, Character>(11, 12, close, 3));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(12), registers, ba);
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

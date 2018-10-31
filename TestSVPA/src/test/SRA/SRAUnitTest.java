package test.SRA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;

import automata.sra.*;
import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import theory.intervals.IntPred;
import theory.intervals.IntegerSolver;

import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Pair;

public class SRAUnitTest {

    @Test
	public void testCreateDot() {
		boolean check = autA.createDotFile("SRAa", "");
        check = autB.createDotFile("SRAb", "");
		assertTrue(check);
	}

	@Test
	public void testEmptyFull() throws TimeoutException {
		SRA<CharPred, Character> empty = SRA.getEmptySRA(ba);
		SRA<CharPred, Character> full = SRA.getFullSRA(ba);

		boolean check = empty.createDotFile("SRAempty", "");
		check = full.createDotFile("SRAfull", "");

		assertTrue(check);
	}

	@Test
	public void testMkSRA() {
		assertTrue(autA.stateCount() == 1);
		assertTrue(autA.getTransitionCount() == 2);

		assertTrue(autB.stateCount() == 2);
		assertTrue(autB.getTransitionCount() == 4);
	}

    @Test public void testIntersection() throws TimeoutException {
        SRA<CharPred, Character> intersection = autA.intersectionWith(autB, ba);
		boolean check = intersection.createDotFile("intersection", "");
		assertTrue(check);
        assertTrue(autA.accepts(la, ba));
        assertFalse(autA.accepts(lb, ba));
        assertTrue(autA.accepts(lab, ba));
        assertFalse(autA.accepts(lnot, ba));

        assertFalse(autB.accepts(la, ba));
        assertTrue(autB.accepts(lb, ba));
        assertTrue(autB.accepts(lab, ba));
        assertFalse(autB.accepts(lnot, ba));

        assertFalse(intersection.accepts(la, ba));
        assertFalse(intersection.accepts(lb, ba));
        assertFalse(intersection.accepts(lab, ba));
        assertFalse(intersection.accepts(lnot, ba));
    }

    @Test
    public void testEmptinessDisabledBecauseInitAssignment() throws TimeoutException {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add('a');
        registers.add('b');
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAFreshMove<CharPred, Character>(0, 1, ba.MkOr(new CharPred('a'), new CharPred('b')), 0));
        SRA<CharPred, Character> testSRA = SRA.MkSRA(transitions, 0, Collections.singleton(1), registers, ba);
        assertTrue(SRA.isLanguageEmpty(testSRA, ba, Long.MAX_VALUE));
    }

    @Test
    public void testEmptinessFreshDisable() throws TimeoutException {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add(null);
        registers.add(null);

        CharPred abPred = ba.MkOr(new CharPred('a'), new CharPred('b'));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAFreshMove<CharPred, Character>(0, 1, abPred, 0));
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 2, abPred, 1));
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 3, abPred, 1));


        SRA<CharPred, Character> testSRA = SRA.MkSRA(transitions, 0, Collections.singleton(3), registers, ba);
        assertTrue(SRA.isLanguageEmpty(testSRA, ba, Long.MAX_VALUE));
    }

    @Test
    public void testEmptinessFreshEnable() throws TimeoutException {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add(null);
        registers.add(null);

        CharPred abPred = ba.MkOr(new CharPred('a'), new CharPred('b'));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAFreshMove<CharPred, Character>(0, 1, abPred, 0));
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 2, abPred, 1));
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 3, alpha, 1));


        SRA<CharPred, Character> testSRA = SRA.MkSRA(transitions, 0, Collections.singleton(3), registers, ba);
        assertFalse(SRA.isLanguageEmpty(testSRA, ba, Long.MAX_VALUE));
    }

    @Test
    public void testAcceptance() throws TimeoutException {
        assertTrue(autIntOne.accepts(Collections.singletonList(6), intBa));
        assertFalse(autIntOne.accepts(Collections.singletonList(2), intBa));
        assertFalse(autIntTwo.accepts(Collections.singletonList(2), intBa));
        assertTrue(autIntTwo.accepts(Collections.singletonList(1), intBa));
    }

    @Test
    public void testSRACompilation() throws TimeoutException {
        boolean check1 = msraAut.createDotFile("msra", "");
        SRA<CharPred, Character> toSRA = msraAut.toSRA(ba, Long.MAX_VALUE);
        boolean check2 = toSRA.createDotFile("toSra", "");
        assertTrue(check1);
        assertTrue(check2);
    }

    @Test
    public void testSRACompilationAndEmptiness() throws TimeoutException {
        SRA<CharPred, Character> toSRA = msraAut.toSRA(ba, Long.MAX_VALUE);
        assertFalse(SRA.isLanguageEmpty(toSRA, ba, Long.MAX_VALUE));
    }

    @Test
    public void testNotSimilar() throws TimeoutException {
        assertFalse(SRA.canSimulate(getSRAa(ba), getSRAb(ba), ba, false, Long.MAX_VALUE));
    }

    @Test
    public void testBisimilarityReflexive() throws TimeoutException {
        assertTrue(SRA.canSimulate(getSRAa(ba), getSRAa(ba), ba, true, Long.MAX_VALUE));
    }


    @Test
    public void testSimulationCase3CornerCase() throws TimeoutException {
        CharPred abcPred = ba.MkOr(Arrays.asList(new CharPred('a'), new CharPred('b'), new CharPred('c')));

        // SRA1
        LinkedList<Character> registers1 = new LinkedList<Character>();

        registers1.add('a');
        registers1.add('b');

        Collection<SRAMove<CharPred, Character>> transitions1 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions1.add(new SRAFreshMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra1 = SRA.MkSRA(transitions1, 0, Collections.singleton(1), registers1, ba);

        // SRA2
        LinkedList<Character> registers2 = new LinkedList<Character>();

        registers2.add('c');
        registers2.add('d');

        Collection<SRAMove<CharPred, Character>> transitions2 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions2.add(new SRACheckMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra2 = SRA.MkSRA(transitions2, 0, Collections.singleton(1), registers2, ba);

        assertTrue(SRA.canSimulate(sra1, sra2, ba, true, Long.MAX_VALUE));
    }

    @Test
    public void testSimulationCase3CornerCaseDifferent() throws TimeoutException {
        CharPred abcPred = ba.MkOr(Arrays.asList(new CharPred('a'), new CharPred('b'), new CharPred('c')));

        // SRA1
        LinkedList<Character> registers1 = new LinkedList<Character>();

        registers1.add('a');
        registers1.add('b');

        Collection<SRAMove<CharPred, Character>> transitions1 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions1.add(new SRAFreshMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra1 = SRA.MkSRA(transitions1, 0, Collections.singleton(1), registers1, ba);

        // SRA2
        LinkedList<Character> registers2 = new LinkedList<Character>();

        registers2.add('c');
        registers2.add('d');

        Collection<SRAMove<CharPred, Character>> transitions2 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions2.add(new SRACheckMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra2 = SRA.MkSRA(transitions2, 0, new LinkedHashSet<>(), registers2, ba);

        assertFalse(SRA.canSimulate(sra1, sra2, ba, true, Long.MAX_VALUE));
    }

    @Test
    public void testSimilarButNotBisimilarBecauseFinalStates() throws TimeoutException {
        CharPred abcPred = ba.MkOr(Arrays.asList(new CharPred('a'), new CharPred('b'), new CharPred('c')));

        // SRA1
        LinkedList<Character> registers = new LinkedList<Character>();

        registers.add('a');

        Collection<SRAMove<CharPred, Character>> transitions1 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions1.add(new SRACheckMove<>(0, 1, abcPred, 0));

        Collection<SRAMove<CharPred, Character>> transitions2 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions2.add(new SRACheckMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra1 = SRA.MkSRA(transitions1, 0, Collections.emptyList(), registers, ba);

        // SRA2
        SRA<CharPred, Character> sra2 = SRA.MkSRA(transitions2, 0, Collections.singleton(1), registers, ba);

        assertTrue(SRA.canSimulate(sra1, sra2, ba, false, Long.MAX_VALUE));
        assertFalse(SRA.canSimulate(sra1, sra2, ba, true, Long.MAX_VALUE));
    }

//    @Test
//    public void testSimilarButNotBisimilar() throws TimeoutException {
//        CharPred abcPred = ba.MkOr(Arrays.asList(new CharPred('a'), new CharPred('b'), new CharPred('c')));
//
//        // SRA1
//        LinkedList<Character> registers1 = new LinkedList<Character>();
//
//        registers1.add('a');
//
//        Collection<SRAMove<CharPred, Character>> transitions1 = new LinkedList<SRAMove<CharPred, Character>>();
//        transitions1.add(new SRACheckMove<>(0, 1, abcPred, 0));
//
//        SRA<CharPred, Character> sra1 = SRA.MkSRA(transitions1, 0, Collections.emptyList(), registers1, ba, false);
//
//        // SRA2
//        LinkedList<Character> registers2 = new LinkedList<Character>();
//
//        registers2.add(null);
//
//        Collection<SRAMove<CharPred, Character>> transitions2 = new LinkedList<SRAMove<CharPred, Character>>();
//        transitions2.add(new SRAFreshMove<>(0, 1, abcPred, 0));
//
//        SRA<CharPred, Character> sra2 = SRA.MkSRA(transitions2, 0, Collections.singleton(1), registers2, ba, false);
//
//        sra1.createDotFile("sra1", "");
//        sra2.createDotFile("sra2", "");
//
//
//        assertTrue(SRA.canSimulate(sra1, sra2, ba, false, Long.MAX_VALUE));
//        assertFalse(SRA.canSimulate(sra1, sra2, ba, true, Long.MAX_VALUE));
//    }

    @Test
    public void testLanguageInclusion() throws TimeoutException {
        CharPred abcPred = ba.MkOr(Arrays.asList(new CharPred('a'), new CharPred('b'), new CharPred('c')));

        // SRA1
        LinkedList<Character> registers1 = new LinkedList<Character>();

        registers1.add('a');

        Collection<SRAMove<CharPred, Character>> transitions1 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions1.add(new SRACheckMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra1 = SRA.MkSRA(transitions1, 0, Collections.emptyList(), registers1, ba);

        // SRA2
        LinkedList<Character> registers2 = new LinkedList<Character>();

        registers2.add(null);

        Collection<SRAMove<CharPred, Character>> transitions2 = new LinkedList<SRAMove<CharPred, Character>>();
        transitions2.add(new SRAFreshMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra2 = SRA.MkSRA(transitions2, 0, Collections.singleton(1), registers2, ba);

        sra1.complete(ba);
        sra2.complete(ba);

        sra1.createDotFile("sra1", "");
        sra2.createDotFile("sra2", "");

        assertTrue(SRA.canSimulate(sra1, sra2, ba, false, Long.MAX_VALUE));
        assertFalse(SRA.canSimulate(sra1, sra2, ba, true, Long.MAX_VALUE));
    }

    @Test
    public void testMkComplete() throws TimeoutException {
        CharPred abcPred = ba.MkOr(Arrays.asList(new CharPred('a'), new CharPred('b'), new CharPred('c')));

        // SRA1
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add('a');

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRACheckMove<>(0, 1, abcPred, 0));

        SRA<CharPred, Character> sra = SRA.MkSRA(transitions, 0, Collections.singleton(1), registers, ba);
        sra.complete(ba);
        boolean check = sra.createDotFile("compSRA", "");
        assertTrue(check);
    }

    // ---------------------------------------
	// Predicates
	// ---------------------------------------
	UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
    IntegerSolver intBa = new IntegerSolver();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred num = StdCharPred.NUM;
	CharPred comma = new CharPred(',');
	Integer onlyX = 1;

	SRA<CharPred, Character> autA = getSRAa(ba);
	SRA<CharPred, Character> autB = getSRAb(ba);
	SRA<CharPred, Character> msraAut = getMSRA(ba);
    SRA<IntPred, Integer> autIntOne = getSRAIntOne(intBa);
    SRA<IntPred, Integer> autIntTwo = getSRAIntTwo(intBa);

	// Test strings
	List<Character> la = lOfS("aa"); // accepted only by autA
	List<Character> lb = lOfS("a3"); // accepted only by autB
	List<Character> lab = lOfS("a"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("44"); // accepted only by neither autA nor autB

    // [a-z]*
	private SRA<CharPred, Character> getSRAa(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add(null);
		Collection<SRAMove<CharPred, Character>> transitionsA = new LinkedList<SRAMove<CharPred, Character>>();
		transitionsA.add(new SRACheckMove<CharPred, Character>(0, 0, alpha, 0));
        transitionsA.add(new SRAFreshMove<CharPred, Character>(0, 0, alpha, 0));
		try {
			return SRA.MkSRA(transitionsA, 0, Collections.singleton(0), registers, ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z][0-9]*
	private SRA<CharPred, Character> getSRAb(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add(null);
		Collection<SRAMove<CharPred, Character>> transitionsB = new LinkedList<SRAMove<CharPred, Character>>();
		transitionsB.add(new SRACheckMove<CharPred, Character>(0, 1, alpha, 0));
        transitionsB.add(new SRAFreshMove<CharPred, Character>(0, 1, alpha, 0));
		transitionsB.add(new SRACheckMove<CharPred, Character>(1, 1, num, 0));
        transitionsB.add(new SRAFreshMove<CharPred, Character>(1, 1, num, 0));
		try {
			return SRA.MkSRA(transitionsB, 0, Collections.singleton(1), registers, ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    private SRA<IntPred, Integer> getSRAIntOne(IntegerSolver ba) {
        LinkedList<Integer> registers = new LinkedList<Integer>();
        registers.add(0);
        Collection<SRAMove<IntPred, Integer>> transitions = new LinkedList<SRAMove<IntPred, Integer>>();
        transitions.add(new SRAFreshMove<IntPred, Integer>(0, 1, new IntPred(5, null), 0));
        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(1), registers, ba);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SRA<IntPred, Integer> getSRAIntTwo(IntegerSolver ba) {
        LinkedList<Integer> registers = new LinkedList<Integer>();
        registers.add(2);
        Collection<SRAMove<IntPred, Integer>> transitions = new LinkedList<SRAMove<IntPred, Integer>>();
        transitions.add(new SRAFreshMove<IntPred, Integer>(0, 1, new IntPred(0, null), 0));
        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(1), registers, ba);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getMSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add(null);
        registers.add(null);
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new MSRAMove<CharPred, Character>(0, 1, alpha, Collections.emptyList(), Collections.singletonList(0)));
        transitions.add(new MSRAMove<CharPred, Character>(1, 2, alpha, Collections.singletonList(0), Collections.singletonList(1)));
        transitions.add(new MSRAMove<CharPred, Character>(2, 3, alpha, Arrays.asList(0, 1), Collections.emptyList()));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(3), registers, ba);
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

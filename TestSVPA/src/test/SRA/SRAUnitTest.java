package test.SRA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.sra.SRA;
import automata.sra.SRACheckMove;
import automata.sra.SRAFreshMove;
import automata.sra.SRAMove;

import theory.intervals.IntPred;
import theory.intervals.IntegerSolver;

import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;

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
		assertTrue(autB.getTransitionCount() == 2);
	}

    @Test public void testIntersection() throws TimeoutException {
        SRA<CharPred, Character> intersection = autA.intersectionWith(autB, ba);

        assertTrue(autA.accepts(la, ba));
        assertFalse(autA.accepts(lb, ba));
        assertTrue(autA.accepts(lab, ba));
        assertFalse(autA.accepts(lnot, ba));

        assertFalse(autB.accepts(la, ba));
        assertFalse(autB.accepts(lb, ba));
        assertTrue(autB.accepts(lab, ba));
        assertFalse(autB.accepts(lnot, ba));

        assertFalse(intersection.accepts(la, ba));
        assertFalse(intersection.accepts(lb, ba));
        assertFalse(intersection.accepts(lab, ba));
        assertFalse(intersection.accepts(lnot, ba));
    }
    @Test
    public void testAcceptance() throws TimeoutException {
        assertTrue(autIntOne.accepts(Arrays.asList(6), intBa));
        assertFalse(autIntOne.accepts(Arrays.asList(2), intBa));
        assertFalse(autIntTwo.accepts(Arrays.asList(2), intBa));
        assertTrue(autIntTwo.accepts(Arrays.asList(1), intBa));
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
        registers.add('z');
		Collection<SRAMove<CharPred, Character>> transitionsA = new LinkedList<SRAMove<CharPred, Character>>();
		transitionsA.add(new SRACheckMove<CharPred, Character>(0, 0, alpha, 0));
        transitionsA.add(new SRAFreshMove<CharPred, Character>(0, 0, alpha, 0));
		try {
			return SRA.MkSRA(transitionsA, 0, Arrays.asList(0), registers, ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z][0-9]*
	private SRA<CharPred, Character> getSRAb(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>();
        registers.add('z');
		Collection<SRAMove<CharPred, Character>> transitionsB = new LinkedList<SRAMove<CharPred, Character>>();
		transitionsB.add(new SRACheckMove<CharPred, Character>(0, 1, alpha, 0));
        transitionsB.add(new SRAFreshMove<CharPred, Character>(0, 1, alpha, 0));
		transitionsB.add(new SRACheckMove<CharPred, Character>(1, 1, num, 0));
        transitionsB.add(new SRAFreshMove<CharPred, Character>(1, 1, num, 0));
		transitionsB.add(new SRACheckMove<CharPred, Character>(0, 2, allAlpha, 0));
        transitionsB.add(new SRAFreshMove<CharPred, Character>(0, 2, allAlpha, 0));
		try {
			return SRA.MkSRA(transitionsB, 0, Arrays.asList(2), registers, ba);
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
            return SRA.MkSRA(transitions, 0, Arrays.asList(1), registers, ba);
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
            return SRA.MkSRA(transitions, 0, Arrays.asList(1), registers, ba);
        } catch (TimeoutException e) {
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

package test.SRA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.sra.SRA;
import automata.sra.SRACheckMove;
import automata.sra.SRAFreshMove;
import automata.sra.SRAMove;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Pair;

public class SRAUnitTest {

	@Test
	public void testCharTheory() throws TimeoutException {
        // TODO: Implement Char Theory tests.
    }

	@Test
	public void testCreateDot() {
		boolean check = autB.createDotFile("autb", "");
		assertTrue(check);
	}

	@Test
	public void testEmptyFull() throws TimeoutException {
		SFA<CharPred, Character> empty = SFA.getEmptySFA(ba);
		SFA<CharPred, Character> full = SFA.getFullSFA(ba);

		boolean check = empty.createDotFile("empty", "");
		check = full.createDotFile("full", "");

		assertTrue(check);
	}

	@Test
	public void testMkSFA() {
		assertTrue(autA.stateCount() == 2);
		assertTrue(autA.getTransitionCount() == 2);

		assertTrue(autB.stateCount() == 2);
		assertTrue(autB.getTransitionCount() == 2);
	}

    // ---------------------------------------
	// Predicates
	// ---------------------------------------
	UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred num = StdCharPred.NUM;
	CharPred comma = new CharPred(',');
	Integer onlyX = 1;

	SRA<CharPred, Character> autA = getSRAa(ba);
	SRA<CharPred, Character> autB = getSRAb(ba);

	// Test strings
	List<Character> la = lOfS("aa"); // accepted only by autA
	List<Character> lb = lOfS("a3"); // accepted only by autB
	List<Character> lab = lOfS("a"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("44"); // accepted only by neither autA nor autB

	// [a-z]+ ambiguous
	private SRA<CharPred, Character> justAlpha(UnaryCharIntervalSolver ba) {

		Collection<SRAMove<CharPred, Character>> transitionsA = new LinkedList<SRAMove<CharPred, Character>>();
		transitionsA.add(new SRACheckMove<CharPred, Character>(0, 1, alpha));
		try {
			return SRA.MkSRA(transitionsA, 0, Arrays.asList(1), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
    // [a-z]* with epsilon transition
	private SFA<CharPred, Character> getSFAa(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAEpsilon<CharPred, Character>(0, 1));
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alpha));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(0, 1), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    // [a-z]*
	private SFA<CharPred, Character> getSFAc(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alpha));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(0), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z][0-9]*
	private SFA<CharPred, Character> getSFAb(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsB = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsB.add(new SFAInputMove<CharPred, Character>(0, 1, alpha));
		transitionsB.add(new SFAInputMove<CharPred, Character>(1, 1, num));
		transitionsB.add(new SFAInputMove<CharPred, Character>(0, 2, allAlpha));
		try {
			return SFA.MkSFA(transitionsB, 0, Arrays.asList(1), ba);
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

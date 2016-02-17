package test.ESFA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import theory.CharPred;
import theory.CharSolver;
import theory.StdCharPred;
import automata.sfa.SFA;
import automata.sfa.SFAEpsilon;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import automata.esfa.*;

public class ESFAUnitTest {

	
	@Test
	public void testMkESFA() {
		assertTrue(autA.stateCount() == 5);
		assertTrue(autA.getTransitionCount() == 5);

		System.out.println(autA.getAmbiguousInput(ba));
		assertTrue(autA.accepts(la, ba));
		
		
		//assertTrue(autB.stateCount() == 2);
		//assertTrue(autB.getTransitionCount() == 2);
	}

	
	CharSolver ba = new CharSolver();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred b = new CharPred('b');
	CharPred c = new CharPred('c');
	CharPred num = StdCharPred.NUM;
	CharPred comma = new CharPred(',');
	Integer onlyX = 1;

	CartesianESFA<CharPred, Character> autA = getESFAa(ba);
//	SFA<CharPred, Character> autB = getSFAb(ba);

	// Test strings
	List<Character> la = lOfS("aa"); // accepted only by autA
	List<Character> lb = lOfS("a3"); // accepted only by autB
	List<Character> lab = lOfS("a"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("44"); // accepted only by neither autA nor autB

	private CartesianESFA<CharPred, Character> getESFAa(CharSolver kba) {
		Collection<ESFAMove<CharPred, Character>> transitionsA = new LinkedList<ESFAMove<CharPred, Character>>();
		transitionsA.add(new ESFAEpsilon<CharPred, Character>(1, 3));
		List<CharPred> guard_1 = new ArrayList<CharPred>();
		guard_1.add(a);
		List<CharPred> guard_2 = new ArrayList<CharPred>();
		guard_2.add(a);
		transitionsA.add(new CartesianESFAInputMove<CharPred, Character>(0, 1, guard_1));
		transitionsA.add(new CartesianESFAInputMove<CharPred, Character>(0, 2, guard_1));
		transitionsA.add(new CartesianESFAInputMove<CharPred, Character>(2, 4, guard_1));
		transitionsA.add(new CartesianESFAInputMove<CharPred, Character>(3, 4, guard_1));
		System.out.println(transitionsA.toString());
		return CartesianESFA.MkESFA(transitionsA, 0, Arrays.asList(4), kba);
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

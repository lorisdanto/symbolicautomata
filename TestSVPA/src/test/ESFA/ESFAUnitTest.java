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
import theory.kCharPred;
import theory.kCharSolver;

public class ESFAUnitTest {

	
	@Test
	public void testMkESFA() {
		assertTrue(autA.stateCount() == 3);
		assertTrue(autA.getTransitionCount() == 1);
		//assertTrue(autA.accepts(la, kba));
		
		
		//assertTrue(autB.stateCount() == 2);
		//assertTrue(autB.getTransitionCount() == 2);
	}

	
	kCharSolver kba = new kCharSolver();
	CharSolver ba = new CharSolver();
	kCharPred kalpha = new kCharPred();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred num = StdCharPred.NUM;
	CharPred comma = new CharPred(',');
	Integer onlyX = 1;

	ESFA<kCharPred, Character> autA = getESFAa(kba);
//	SFA<CharPred, Character> autB = getSFAb(ba);

	// Test strings
	List<Character> la = lOfS("a"); // accepted only by autA
	List<Character> lb = lOfS("a3"); // accepted only by autB
	List<Character> lab = lOfS("a"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("44"); // accepted only by neither autA nor autB

	private ESFA<kCharPred, Character> getESFAa(kCharSolver kba) {

		Collection<ESFAMove<kCharPred, Character>> transitionsA = new LinkedList<ESFAMove<kCharPred, Character>>();
		transitionsA.add(new ESFAEpsilon<kCharPred, Character>(0, 1));
		kalpha.setk(1);
		kalpha.addPre(a, 0);
		transitionsA.add(new ESFAInputMove<kCharPred, Character>(0, 2, kalpha,1));
		System.out.println(transitionsA.toString());
		return ESFA.MkESFA(transitionsA, 0, Arrays.asList(2), kba);
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

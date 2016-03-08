package test.SAFA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.safa.SAFA;
import logic.ltl.And;
import logic.ltl.Eventually;
import logic.ltl.LTLFormula;
import logic.ltl.Or;
import logic.ltl.Predicate;
import logic.ltl.True;
import theory.CharPred;
import theory.CharSolver;
import theory.StdCharPred;

public class LTLUnitTest {

	@Test
	public void testEventuallyAndOr() {
		LTLFormula<CharPred, Character> conj = new And<>(eva, evn);
		SAFA<CharPred, Character> sconj = conj.getSAFA(ba);
		LTLFormula<CharPred, Character> union = new Or<>(eva, evn);
		SAFA<CharPred, Character> sunion = union.getSAFA(ba);

		assertTrue(seva.accepts(la, ba));
		assertFalse(seva.accepts(lb, ba));
		assertTrue(seva.accepts(lab, ba));
		assertFalse(seva.accepts(lnot, ba));

		assertFalse(sevn.accepts(la, ba));
		assertTrue(sevn.accepts(lb, ba));
		assertTrue(sevn.accepts(lab, ba));
		assertFalse(sevn.accepts(lnot, ba));

		assertFalse(sconj.accepts(la, ba));
		assertFalse(sconj.accepts(lb, ba));
		assertTrue(sconj.accepts(lab, ba));
		assertFalse(sconj.accepts(lnot, ba));

		assertTrue(sunion.accepts(la, ba));
		assertTrue(sunion.accepts(lb, ba));
		assertTrue(sunion.accepts(lab, ba));
		assertFalse(sunion.accepts(lnot, ba));
	}

	@Test
	public void testLargeEquiv() {

		LTLFormula<CharPred, Character> tot = new True<>();
		for (int i = 100; i < 100 + 9; i++) {
			CharPred ch = new CharPred((char) i);
			LTLFormula<CharPred, Character> evch = ev(ba, ch);
			tot = new And<>(evch, tot);
		}
		SAFA<CharPred, Character> safa = tot.getSAFA(ba);

		long startTime = System.currentTimeMillis();

		try {

			SAFA.isEquivalent(safa, safa, ba);
		} catch (TimeoutException toe) {
			System.out.println(toe);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

		startTime = System.currentTimeMillis();

		SAFA.isReverseEquivalent(safa, safa, ba);

		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

	}

	// ---------------------------------------
	// Predicates
	// ---------------------------------------
	CharSolver ba = new CharSolver();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred num = StdCharPred.NUM;
	CharPred comma = new CharPred(',');
	Integer onlyX = 1;

	LTLFormula<CharPred, Character> eva = ev(ba, alpha);
	LTLFormula<CharPred, Character> evn = ev(ba, num);
	SAFA<CharPred, Character> seva = eva.getSAFA(ba);
	SAFA<CharPred, Character> sevn = evn.getSAFA(ba);

	// Test strings
	List<Character> la = lOfS("a#a"); // accepted only by autA
	List<Character> lb = lOfS("3#"); // accepted only by autB
	List<Character> lab = lOfS("a3"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("##"); // accepted only by neither autA nor autB

	// eventually p
	private LTLFormula<CharPred, Character> ev(CharSolver ba, CharPred p) {
		return new Eventually<CharPred, Character>(new Predicate<CharPred, Character>(p));
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

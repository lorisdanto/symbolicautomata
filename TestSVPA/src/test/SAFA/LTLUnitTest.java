package test.SAFA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.safa.SAFA;
import automata.safa.booleanexpression.BDDExpressionFactory;
import automata.safa.booleanexpression.SumOfProductsFactory;
import logic.ltl.And;
import logic.ltl.Eventually;
import logic.ltl.Globally;
import logic.ltl.LTLFormula;
import logic.ltl.Next;
import logic.ltl.Not;
import logic.ltl.Or;
import logic.ltl.Predicate;
import logic.ltl.True;
import logic.ltl.Until;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;
import theory.sat.SATBooleanAlgebra;
import utilities.Pair;

public class LTLUnitTest {
	@Test
	public void testEventuallyAndOr() throws TimeoutException {
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
	public void unary() throws TimeoutException {
		LTLFormula<CharPred, Character> GXt = new Globally<CharPred, Character>(new Next<CharPred, Character>(new True<CharPred, Character>()));
		SAFA<CharPred, Character> phi = GXt.getSAFA(ba);
		
		assertTrue(SAFA.isEmpty(phi, ba));
		assertTrue(SAFA.isEmpty(phi.getUnaryPathSAFA(ba), ba));
	}

	@Test
	public void testLargeEquiv() throws TimeoutException {
		int size = 2;

		LTLFormula<CharPred, Character> tot = new True<>();
		for (int i = 100; i < 100 + size; i++) {
			CharPred ch = new CharPred((char) i);
			LTLFormula<CharPred, Character> evch = ev(ba, ch);
			tot = new And<>(evch, tot);
		}
		SAFA<CharPred, Character> safa1 = tot.getSAFA(ba);

		tot = new True<>();
		for (int i = 100; i < 100 + size - 1; i++) {
			CharPred ch = new CharPred((char) i);
			LTLFormula<CharPred, Character> evch = ev(ba, ch);
			tot = new And<>(evch, tot);
		}
		SAFA<CharPred, Character> safa2 = tot.getSAFA(ba);

		long startTime = System.currentTimeMillis();

		boolean b = true;
		try {
			b = SAFA.isEquivalent(safa1, safa2, ba, SumOfProductsFactory.getInstance()).first;
		} catch (TimeoutException toe) {
			System.out.println(toe);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

		startTime = System.currentTimeMillis();

		Pair<Boolean, List<Character>> b1 = SAFA.areReverseEquivalent(safa1, safa2, ba);
		System.out.println(b1);

		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

		assertTrue(b == b1.first);

	}

	@Test
	public void testLargeEmptiness() throws TimeoutException {
		int sizeTot = 4;

		for (int size = 2; size < sizeTot; size++) {

			LTLFormula<CharPred, Character> tot = new True<>();
			for (int i = 100; i < 100 + size; i++) {
				CharPred ch = new CharPred((char) i);
				LTLFormula<CharPred, Character> evch = ev(ba, ch);
				tot = new And<>(evch, tot);
			}
			SAFA<CharPred, Character> safa1 = tot.getSAFA(ba);
			long startTime = System.currentTimeMillis();

			boolean b = true;
			try {
				b = SAFA.isEmpty(safa1, ba);
				assertFalse(b);
			} catch (TimeoutException toe) {
				System.out.println(toe);
			}

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(size + " " + elapsedTime);
		}
	}

	@Test
	public void testLargeEmptinessBDD() throws TimeoutException {
		int sizeTot = 7;
		BDDExpressionFactory bef = new BDDExpressionFactory(sizeTot);
		// PositiveBooleanExpressionFactory bef = new
		// PositiveBooleanExpressionFactory();
		// SumOfProductsFactory bef = SumOfProductsFactory.getInstance();

		for (int size = 5; size < sizeTot; size++) {

			BDDSolver bs = new BDDSolver(size);
			LTLFormula<BDD, BDD> tot = new True<>();
			List<LTLFormula<BDD, BDD>> conjuncts = new LinkedList<>();
			for (int i = 0; i < size; i++) {
				conjuncts.add(new Eventually<>(new Predicate<BDD, BDD>(bs.factory.ithVar(i))));
				// LTLFormula<Integer, boolean[]> evch = new Eventually<>(new
				// Predicate<Integer, boolean[]>(i));
				// tot = new And<>(evch, tot);
			}
			tot = new And<>(conjuncts);

			long startTime = System.currentTimeMillis();
			SAFA<BDD, BDD> safa1 = tot.getSAFA(bs);

			boolean b = true;
			try {
				b = SAFA.isEquivalent(safa1, SAFA.getEmptySAFA(bs), bs, bef).first;
				assertFalse(b);
			} catch (Exception toe) {
				System.out.println(toe);
			}

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(size + " " + elapsedTime);
		}
	}

	@Test
	public void testLargeEmptinessSAT() throws TimeoutException {
		int sizeTot = 4;

		for (int size = 2; size < sizeTot; size++) {
			System.out.println(size);

			SATBooleanAlgebra ba = new SATBooleanAlgebra(size + 1);
			LTLFormula<Integer, boolean[]> tot = new True<>();
			List<LTLFormula<Integer, boolean[]>> conjuncts = new LinkedList<>();
			for (int i = 1; i < size; i++) {
				conjuncts.add(new Eventually<>(new Predicate<Integer, boolean[]>(i)));
				// LTLFormula<Integer, boolean[]> evch = new Eventually<>(new
				// Predicate<Integer, boolean[]>(i));
				// tot = new And<>(evch, tot);
			}
			tot = new And<>(conjuncts);
			long startTime = System.currentTimeMillis();
			SAFA<Integer, boolean[]> safa1 = tot.getSAFA(ba);
			long stopTime = System.currentTimeMillis();
			System.out.println("BuildSAFA " + (stopTime - startTime));

			startTime = System.currentTimeMillis();
			boolean b = true;
			try {
				b = SAFA.isEmpty(safa1, ba);
				assertFalse(b);
			} catch (TimeoutException toe) {
				System.out.println(toe);
			}

			stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Emptiness " + elapsedTime);
		}
	}

	@Test
	public void testLargeEquivSAT() throws TimeoutException {
		int size = 2;
		SATBooleanAlgebra ba = new SATBooleanAlgebra(size + 1);
		LTLFormula<Integer, boolean[]> tot = new True<>();
		List<LTLFormula<Integer, boolean[]>> conjuncts = new LinkedList<>();
		for (int i = 1; i < size; i++) {
			conjuncts.add(new Eventually<>(new Predicate<Integer, boolean[]>(i)));
			// LTLFormula<Integer, boolean[]> evch = new Eventually<>(new
			// Predicate<Integer, boolean[]>(i));
			// tot = new And<>(evch, tot);
		}
		tot = new And<>(conjuncts);
		SAFA<Integer, boolean[]> safa1 = tot.getSAFA(ba);

		tot = new True<>();
		for (int i = size - 1; i >= 1; i--) {
			LTLFormula<Integer, boolean[]> evch = new Eventually<>(new Predicate<Integer, boolean[]>(i));
			tot = new And<>(evch, tot);
		}
		SAFA<Integer, boolean[]> safa2 = tot.getSAFA(ba);

		long startTime = System.currentTimeMillis();

		boolean b = true;
		try {
			b = SAFA.isEquivalent(safa1, safa2, ba, SumOfProductsFactory.getInstance()).first;
		} catch (TimeoutException toe) {
			System.out.println(toe);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

		startTime = System.currentTimeMillis();

		Pair<Boolean, List<boolean[]>> b1 = SAFA.areReverseEquivalent(safa1, safa2, ba);
		System.out.println(b1);

		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

		assertTrue(b == b1.first);
	}

	@Test
	public void testFiniteLTL() throws TimeoutException {
		LTLFormula<CharPred, Character> a = new Predicate<>(new CharPred('a'));
		LTLFormula<CharPred, Character> b = new Predicate<>(new CharPred('b'));
		LTLFormula<CharPred, Character> nextA = new Next<>(a);
		LTLFormula<CharPred, Character> globallyA = new Globally<>(a);
		LTLFormula<CharPred, Character> globallyNextA = new Globally<>(nextA);
		LTLFormula<CharPred, Character> finallyA = new Eventually<>(a);
		LTLFormula<CharPred, Character> aUntilB = new Until<>(a, b);
		LTLFormula<CharPred, Character> notA = new Not<>(a);

		assertFalse(models("", a));
		assertTrue(models("a", a));
		assertFalse(models("", nextA));
		assertFalse(models("a", nextA));
		assertTrue(models("ba", nextA));
		assertFalse(models("", globallyA));
		assertTrue(models("aaa", globallyA));
		assertFalse(models("aaab", globallyA));
		assertFalse(models("", globallyNextA));
		assertFalse(models("aaa", globallyNextA));
		assertFalse(models("bb", finallyA));
		assertTrue(models("bba", finallyA));
		assertFalse(models("", finallyA));
		assertTrue(models("ab", aUntilB));
		assertFalse(models("", aUntilB));
		assertTrue(models("b", aUntilB));
		assertTrue(models("b", notA));
		assertFalse(models("", notA));
		assertFalse(models("ab", notA));
		assertTrue(models("ba", notA));
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

	LTLFormula<CharPred, Character> eva = ev(ba, alpha);
	LTLFormula<CharPred, Character> evn = ev(ba, num);
	SAFA<CharPred, Character> seva = getseva();
	SAFA<CharPred, Character> sevn = getsevn();

	// Test strings
	List<Character> la = lOfS("a#a"); // accepted only by autA
	List<Character> lb = lOfS("3#"); // accepted only by autB
	List<Character> lab = lOfS("a3"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("##"); // accepted only by neither autA nor autB

	public SAFA<CharPred, Character> getseva() {
		try {
			return eva.getSAFA(ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SAFA<CharPred, Character> getsevn() {
		try {
			return evn.getSAFA(ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// eventually p
	private LTLFormula<CharPred, Character> ev(UnaryCharIntervalSolver ba, CharPred p) {
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

	private boolean models(String s, LTLFormula<CharPred, Character> phi) throws TimeoutException {
		LTLFormula<CharPred, Character> phin = phi.pushNegations(ba);
		SAFA<CharPred, Character> safa = phin.getSAFA(ba);
		return safa.accepts(lOfS(s), ba);
	}
}

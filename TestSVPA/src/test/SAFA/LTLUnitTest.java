package test.SAFA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.safa.SAFA;
import automata.safa.booleanexpression.SumOfProducts;
import automata.safa.booleanexpression.SumOfProductsFactory;
import bddalgebra.BDDSolver;
import logic.ltl.And;
import logic.ltl.Eventually;
import logic.ltl.LTLFormula;
import logic.ltl.Or;
import logic.ltl.Predicate;
import logic.ltl.True;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import theory.CharPred;
import theory.CharSolver;
import theory.SATBooleanAlgebra;
import theory.StdCharPred;
import utilities.Pair;

public class LTLUnitTest {

	@Test
	public void testEventuallyAndOr() {
		LTLFormula<CharPred, Character> conj = new And<>(eva, evn);
		SAFA<CharPred, Character, SumOfProducts> sconj = conj.getSAFA(ba, sop);
		LTLFormula<CharPred, Character> union = new Or<>(eva, evn);
		SAFA<CharPred, Character, SumOfProducts> sunion = union.getSAFA(ba, sop);

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
		int size = 7;

		LTLFormula<CharPred, Character> tot = new True<>();
		for (int i = 100; i < 100 + size; i++) {
			CharPred ch = new CharPred((char) i);
			LTLFormula<CharPred, Character> evch = ev(ba, ch);
			tot = new And<>(evch, tot);
		}
		SAFA<CharPred, Character, SumOfProducts> safa1 = tot.getSAFA(ba, sop);

		tot = new True<>();
		for (int i = 100; i < 100 + size - 1; i++) {
			CharPred ch = new CharPred((char) i);
			LTLFormula<CharPred, Character> evch = ev(ba, ch);
			tot = new And<>(evch, tot);
		}
		SAFA<CharPred, Character, SumOfProducts> safa2 = tot.getSAFA(ba, sop);

		long startTime = System.currentTimeMillis();

		boolean b = true;
		try {
			b = SAFA.isEquivalent(safa1, safa2, ba, sop);
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
	public void testLargeEmptiness() {
		int sizeTot = 7;

		for (int size = 2; size < sizeTot; size++) {

			LTLFormula<CharPred, Character> tot = new True<>();
			for (int i = 100; i < 100 + size; i++) {
				CharPred ch = new CharPred((char) i);
				LTLFormula<CharPred, Character> evch = ev(ba, ch);
				tot = new And<>(evch, tot);
			}
			SAFA<CharPred, Character, SumOfProducts> safa1 = tot.getSAFA(ba, sop);
			long startTime = System.currentTimeMillis();

			boolean b = true;
			try {
				b = SAFA.isEmpty(safa1, ba, sop);
				assertFalse(b);
			} catch (TimeoutException toe) {
				System.out.println(toe);
			}

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(size+" "+elapsedTime);
		}
	}
	
	@Test
	public void testLargeEmptinessBDD() {
		int sizeTot = 15;

		for (int size = 4; size < sizeTot; size++) {

			BDDSolver bs = new BDDSolver(size);
			LTLFormula<BDD, BDD> tot = new True<>();
			List<LTLFormula<BDD, BDD>> conjuncts = new LinkedList<>();
			for (int i = 0; i <  size; i++) {
				conjuncts.add(new Eventually<>(new Predicate<BDD, BDD>(
							bs.factory.ithVar(i)
						)));
//				LTLFormula<Integer, boolean[]> evch = new Eventually<>(new Predicate<Integer, boolean[]>(i));
//				tot = new And<>(evch, tot);
			}
			tot = new And<>(conjuncts);
			
			long startTime = System.currentTimeMillis();
			SAFA<BDD, BDD, SumOfProducts> safa1 = tot.getSAFANew(bs, sop);
			

			boolean b = true;
			try {
				b = SAFA.isEmpty(safa1, bs, sop);
				assertFalse(b);
			} catch (TimeoutException toe) {
				System.out.println(toe);
			}

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(size+" "+elapsedTime);
		}
	}
	
	@Test
	public void testLargeEmptinessSAT() {
		int sizeTot = 7;

		for (int size = 2; size < sizeTot; size++) {
			System.out.println(size);
			
			SATBooleanAlgebra ba = new SATBooleanAlgebra(size + 1);
			LTLFormula<Integer, boolean[]> tot = new True<>();
			List<LTLFormula<Integer, boolean[]>> conjuncts = new LinkedList<>();
			for (int i = 1; i <  size; i++) {
				conjuncts.add(new Eventually<>(new Predicate<Integer, boolean[]>(i)));
//				LTLFormula<Integer, boolean[]> evch = new Eventually<>(new Predicate<Integer, boolean[]>(i));
//				tot = new And<>(evch, tot);
			}
			tot = new And<>(conjuncts);
			long startTime = System.currentTimeMillis();
			SAFA<Integer, boolean[], SumOfProducts> safa1 = tot.getSAFA(ba, sop);
			long stopTime = System.currentTimeMillis();
			System.out.println("BuildSAFA "+(stopTime - startTime));
			
			startTime = System.currentTimeMillis();
			boolean b = true;
			try {
				b = SAFA.isEmpty(safa1, ba, sop);
				assertFalse(b);
			} catch (TimeoutException toe) {
				System.out.println(toe);
			}

			stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Emptiness "+elapsedTime);
		}
	}

	@Test
	public void testLargeEquivSAT() throws TimeoutException {
		int size = 2;
		SATBooleanAlgebra ba = new SATBooleanAlgebra(size + 1);
		LTLFormula<Integer, boolean[]> tot = new True<>();
		List<LTLFormula<Integer, boolean[]>> conjuncts = new LinkedList<>();
		for (int i = 1; i <  size; i++) {
			conjuncts.add(new Eventually<>(new Predicate<Integer, boolean[]>(i)));
//			LTLFormula<Integer, boolean[]> evch = new Eventually<>(new Predicate<Integer, boolean[]>(i));
//			tot = new And<>(evch, tot);
		}
		tot = new And<>(conjuncts);
		SAFA<Integer, boolean[], SumOfProducts> safa1 = tot.getSAFA(ba, sop);

		tot = new True<>();
		for (int i = size - 1; i >= 1; i--) {
		LTLFormula<Integer, boolean[]> evch = new Eventually<>(new Predicate<Integer, boolean[]>(i));
			tot = new And<>(evch, tot);
		}
		SAFA<Integer, boolean[], SumOfProducts> safa2 = tot.getSAFA(ba, sop);
		
		long startTime = System.currentTimeMillis();

		boolean b = true;
		try {
			b = SAFA.isEquivalent(safa1, safa2, ba, sop);
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
	SumOfProductsFactory sop = SumOfProductsFactory.getInstance();
	SAFA<CharPred, Character, SumOfProducts> seva = eva.getSAFA(ba, sop);
	SAFA<CharPred, Character, SumOfProducts> sevn = evn.getSAFA(ba, sop);

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

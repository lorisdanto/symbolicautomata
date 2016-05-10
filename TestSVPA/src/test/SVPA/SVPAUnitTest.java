package test.SVPA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import automata.AutomataException;
import automata.svpa.Call;
import automata.svpa.Internal;
import automata.svpa.Return;
import automata.svpa.SVPA;
import automata.svpa.SVPAMove;
import automata.svpa.TaggedSymbol;
import automata.svpa.TaggedSymbol.SymbolTag;
import theory.BooleanAlgebra;
import theory.characters.BinaryCharPred;
import theory.characters.CharPred;
import theory.characters.ICharPred;
import theory.characters.StdCharPred;
import theory.intervals.EqualitySolver;
import theory.intervals.UnaryCharIntervalSolver;

public class SVPAUnitTest {

	@Test
	public void testCreateDot() {
		autA.createDotFile("vpaA", "");
	}

	@Test
	public void testPropertiesAccessors() {
		assertTrue(autA.isDeterministic(ba));
		assertTrue(autA.stateCount == 2);
		assertTrue(autA.transitionCount == 6);
	}

	@Test
	public void testEmptyFull() {

		SVPA<ICharPred, Character> empty = SVPA.getEmptySVPA(ba);
		SVPA<ICharPred, Character> full = SVPA.getFullSVPA(ba);

		assertTrue(empty.isEmpty);
		assertFalse(full.isEmpty);
	}

	@Test
	public void testAccept() {
		assertTrue(autA.accepts(ab, ba));
		assertTrue(autA.accepts(anotb, ba));
		assertFalse(autA.accepts(notab, ba));
		assertFalse(autA.accepts(notanotb, ba));

		assertTrue(autB.accepts(ab, ba));
		assertFalse(autB.accepts(anotb, ba));
		assertTrue(autB.accepts(notab, ba));
		assertFalse(autB.accepts(notanotb, ba));
	}

	@Test
	public void testIntersectionWith() {

		// Compute intersection
		SVPA<ICharPred, Character> inters = autA.intersectionWith(autB, ba);

		assertTrue(inters.accepts(ab, ba));
		assertFalse(inters.accepts(anotb, ba));
		assertFalse(inters.accepts(notab, ba));
		assertFalse(inters.accepts(notanotb, ba));
	}

	@Test
	public void testUnion() {

		// Compute union
		SVPA<ICharPred, Character> inters = autA.unionWith(autB, ba);

		assertTrue(inters.accepts(ab, ba));
		assertTrue(inters.accepts(anotb, ba));
		assertTrue(inters.accepts(notab, ba));
		assertFalse(inters.accepts(notanotb, ba));
	}

	@Test
	public void testMkTotal() {

		SVPA<ICharPred, Character> totA = autA.mkTotal(ba);

		assertTrue(totA.accepts(ab, ba));
		assertTrue(totA.accepts(anotb, ba));
		assertFalse(totA.accepts(notab, ba));
		assertFalse(totA.accepts(notanotb, ba));

		assertTrue(totA.stateCount == autA.stateCount + 1);
		assertTrue(totA.transitionCount == 21);
	}
	//
	// @Test
	// public void testComplement() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> complementA = autA
	// .complement(ba);
	//
	//
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> complementB= autB
	// .complement(ba);
	//
	// TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Internal);
	//
	// TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Return);
	// TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6),
	// SymbolTag.Return);
	//
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> la = Arrays.asList(i1);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5);
	//
	//
	// assertTrue(autA.accepts(la, ba));
	// assertFalse(autA.accepts(lb, ba));
	// assertTrue(autA.accepts(lab, ba));
	// assertFalse(autA.accepts(lnot, ba));
	//
	// assertFalse(complementA.accepts(la, ba));
	// assertTrue(complementA.accepts(lb, ba));
	// assertFalse(complementA.accepts(lab, ba));
	// assertTrue(complementA.accepts(lnot, ba));
	//
	// assertFalse(autB.accepts(la, ba));
	// assertTrue(autB.accepts(lb, ba));
	// assertTrue(autB.accepts(lab, ba));
	// assertFalse(autB.accepts(lnot, ba));
	//
	// assertTrue(complementB.accepts(la, ba));
	// assertFalse(complementB.accepts(lb, ba));
	// assertFalse(complementB.accepts(lab, ba));
	// assertTrue(complementB.accepts(lnot, ba));
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	// @Test
	// public void testDeterminization() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> detA= autA
	// .determinize(ba);
	//
	// TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Internal);
	//
	// TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Return);
	// TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6),
	// SymbolTag.Return);
	//
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> la = Arrays.asList(c5,i1,r5);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5);
	//
	//
	// assertTrue(autA.accepts(la, ba));
	// assertFalse(autA.accepts(lb, ba));
	// assertTrue(autA.accepts(lab, ba));
	// assertFalse(autA.accepts(lnot, ba));
	//
	// assertTrue(detA.accepts(la, ba));
	// assertFalse(detA.accepts(lb, ba));
	// assertTrue(detA.accepts(lab, ba));
	// assertFalse(detA.accepts(lnot, ba));
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	// @Test
	// public void testEpsilonRem() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> epsFree= autA
	// .removeEpsilonMoves(ba);
	//
	// TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Internal);
	//
	// TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Return);
	// TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6),
	// SymbolTag.Return);
	//
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> la = Arrays.asList(i1);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5);
	//
	//
	// assertTrue(autA.accepts(la, ba));
	// assertFalse(autA.accepts(lb, ba));
	// assertTrue(autA.accepts(lab, ba));
	// assertFalse(autA.accepts(lnot, ba));
	//
	// assertTrue(epsFree.accepts(la, ba));
	// assertFalse(epsFree.accepts(lb, ba));
	// assertTrue(epsFree.accepts(lab, ba));
	// assertFalse(epsFree.accepts(lnot, ba));
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	//
	// @Test
	// public void testDiff() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> diff = autA
	// .minus(autB, z3p);
	//
	//
	// TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1),
	// SymbolTag.Internal);
	//
	// TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Call);
	// TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5),
	// SymbolTag.Return);
	// TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6),
	// SymbolTag.Return);
	//
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> a = Arrays.asList(i1);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> b = Arrays.asList(c5,r6);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> ab = Arrays.asList(c5,r5);
	// @SuppressWarnings("unchecked")
	// List<TaggedSymbol<IntExpr>> notab = Arrays.asList(c1,r5);
	//
	// assertTrue(autA.accepts(a, ba));
	// assertFalse(autA.accepts(b, ba));
	// assertTrue(autA.accepts(ab, ba));
	// assertFalse(autA.accepts(notab, ba));
	//
	// assertTrue(autB.accepts(b, ba));
	// assertFalse(autB.accepts(a, ba));
	// assertTrue(autB.accepts(ab, ba));
	// assertFalse(autB.accepts(notab, ba));
	//
	// assertFalse(diff.accepts(ab, ba));
	// assertTrue(diff.accepts(a, ba));
	// assertFalse(diff.accepts(b, ba));
	// assertFalse(diff.accepts(notab, ba));
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	// @Test
	// public void testEquivalence() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> cA = autA
	// .complement(ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> cUcA = autA
	// .unionWith(cA, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> ccA = cA
	// .complement(ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> cB = autB
	// .complement(ba);
	//
	// SVPA<Predicate<IntExpr>, IntExpr> cUcB = autB
	// .unionWith(cB, ba);
	//
	//
	// SVPA<Predicate<IntExpr>, IntExpr> ccB = cB
	// .complement(ba);
	//
	// assertFalse(autA.isEquivalentTo(autB, ba));
	//
	// autA.createDotFile("a", "");
	// autA.removeEpsilonMoves(ba).createDotFile("ae", "");
	// autA.removeEpsilonMoves(ba).mkTotal(ba).createDotFile("at", "");
	// autA.createDotFile("a1", "");
	// autA.createDotFile("a1", "");
	// autA.minus(ccA, ba).createDotFile("diff1", "");
	// ccA.minus(autA, ba).createDotFile("diff2", "");
	//
	// cA.createDotFile("ca", "");
	// ccA.createDotFile("cca", "");
	//
	// assertTrue(autA.isEquivalentTo(ccA, ba));
	//
	// assertTrue(autB.isEquivalentTo(ccB, ba));
	//
	// assertTrue(autA.isEquivalentTo(autA.intersectionWith(autA, ba), ba));
	// assertTrue(SVPA.getEmptySVPA(ba).isEquivalentTo(autA.minus(autA, ba),
	// ba));
	//
	// assertTrue(cUcA.isEquivalentTo(SVPA.getFullSVPA(ba), ba));
	//
	// assertTrue(cUcB.isEquivalentTo(SVPA.getFullSVPA(ba), ba));
	// assertTrue(cUcB.isEquivalentTo(cUcA, ba));
	// assertFalse(autB.isEquivalentTo(autA, ba));
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	// @Test
	// public void testEpsRemove() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// //First Automaton
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// //Second Automaton
	// SVPA<Predicate<IntExpr>, IntExpr> autAnoEps =
	// autA.removeEpsilonMoves(ba);
	//
	// assertFalse(autA.isEpsilonFree);
	// assertTrue(autAnoEps.isEpsilonFree);
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	// @Test
	// public void testGetWitness() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
	//
	// autA.getWitness(ba);
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }
	//
	// @Test
	// public void testReachRem() {
	// try {
	// Context c = new Context();
	// Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
	// BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
	//
	// Predicate<IntExpr> geq0 = new Predicate<IntExpr>("x", c.MkGe(
	// (IntExpr) c.MkConst(c.MkSymbol("x"), c.IntSort()),
	// c.MkInt(0)), c.IntSort());
	//
	// Collection<SVPAMove<Predicate<IntExpr>, IntExpr>> transA = new
	// LinkedList<SVPAMove<Predicate<IntExpr>, IntExpr>>();
	// transA.add(new Call<Predicate<IntExpr>, IntExpr>(0,1,0,
	// geq0));
	//
	// transA.add(new Return<Predicate<IntExpr>, IntExpr>(1,2,0,
	// geq0));
	//
	// //First Automaton
	// SVPA<Predicate<IntExpr>, IntExpr> autA = SVPA.MkSVPA(transA,
	// Arrays.asList(0), Arrays.asList(2), ba);
	//
	// assertFalse(autA.isEmpty);
	//
	// } catch (Z3Exception e) {
	// System.out.print(e);
	// } catch (AutomataException e) {
	// System.out.print(e);
	// }
	// }

	// ---------------------------------------
	// Predicates
	// ---------------------------------------
	UnaryCharIntervalSolver uba = new UnaryCharIntervalSolver();
	EqualitySolver ba = new EqualitySolver();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred num = StdCharPred.NUM;
	CharPred trueChar = StdCharPred.TRUE;
	CharPred trueRetChar = new CharPred(CharPred.MIN_CHAR, CharPred.MAX_CHAR, true);
	CharPred comma = new CharPred(',');
	Integer onlyX = 1;
	BinaryCharPred equality = new BinaryCharPred(StdCharPred.TRUE, true);

	TaggedSymbol<Character> ca = new TaggedSymbol<>('a', SymbolTag.Call);
	TaggedSymbol<Character> ra = new TaggedSymbol<>('a', SymbolTag.Return);

	TaggedSymbol<Character> cb = new TaggedSymbol<Character>('b', SymbolTag.Call);
	TaggedSymbol<Character> rb = new TaggedSymbol<Character>('b', SymbolTag.Return);

	TaggedSymbol<Character> c1 = new TaggedSymbol<Character>('1', SymbolTag.Call);
	TaggedSymbol<Character> r1 = new TaggedSymbol<Character>('1', SymbolTag.Return);

	TaggedSymbol<Character> ia = new TaggedSymbol<Character>('a', SymbolTag.Internal);
	TaggedSymbol<Character> ib = new TaggedSymbol<Character>('b', SymbolTag.Internal);
	TaggedSymbol<Character> i1 = new TaggedSymbol<Character>('1', SymbolTag.Internal);

	List<TaggedSymbol<Character>> matchedAlpha = Arrays.asList(ca, ra);
	List<TaggedSymbol<Character>> unmatchedAlpha = Arrays.asList(ca, ca, rb, ra);
	List<TaggedSymbol<Character>> hasNum = Arrays.asList(c1, r1, ca);
	List<TaggedSymbol<Character>> internalAlpha = Arrays.asList(ia, ib);
	List<TaggedSymbol<Character>> internalNum = Arrays.asList(ia, ib, i1);

	List<TaggedSymbol<Character>> ab = Arrays.asList(cb, ia, rb);
	List<TaggedSymbol<Character>> notab = Arrays.asList(cb, ia);
	List<TaggedSymbol<Character>> anotb = Arrays.asList(cb, ib, ca, ra, rb);
	List<TaggedSymbol<Character>> notanotb = Arrays.asList(ca, ib);

	SVPA<ICharPred, Character> autA = getSVPAa(ba);
	SVPA<ICharPred, Character> autB = getSVPAb(ba);
	SVPA<ICharPred, Character> autPeter = getCFGAutomata(ba);

	// Only accepts well-matched nested words of lower alphabetic chars
	private SVPA<ICharPred, Character> getSVPAa(BooleanAlgebra<ICharPred, Character> ba) {

		Collection<SVPAMove<ICharPred, Character>> transitions = new LinkedList<SVPAMove<ICharPred, Character>>();
		transitions.add(new Internal<ICharPred, Character>(0, 0, alpha));
		transitions.add(new Internal<ICharPred, Character>(1, 1, alpha));

		transitions.add(new Call<ICharPred, Character>(0, 1, 0, alpha));
		transitions.add(new Return<ICharPred, Character>(1, 0, 0, equality));
		transitions.add(new Call<ICharPred, Character>(1, 1, 1, alpha));
		transitions.add(new Return<ICharPred, Character>(1, 1, 1, equality));

		try {
			return SVPA.MkSVPA(transitions, Arrays.asList(0), Arrays.asList(0), ba);
		} catch (AutomataException e) {
			return null;
		}

	}

	// Contains a somewhere as internal doesn't care about other symbols
	private SVPA<ICharPred, Character> getSVPAb(BooleanAlgebra<ICharPred, Character> ba) {

		Collection<SVPAMove<ICharPred, Character>> transitions = new LinkedList<SVPAMove<ICharPred, Character>>();
		transitions.add(new Internal<ICharPred, Character>(0, 0, trueChar));
		transitions.add(new Internal<ICharPred, Character>(1, 1, trueChar));
		transitions.add(new Internal<ICharPred, Character>(0, 1, a));

		transitions.add(new Call<ICharPred, Character>(0, 0, 0, trueChar));
		transitions.add(new Return<ICharPred, Character>(0, 0, 0, trueRetChar));
		transitions.add(new Call<ICharPred, Character>(1, 1, 0, trueChar));
		transitions.add(new Return<ICharPred, Character>(1, 1, 0, trueRetChar));

		try {
			return SVPA.MkSVPA(transitions, Arrays.asList(0), Arrays.asList(1), ba);
		} catch (AutomataException e) {
			return null;
		}
	}

	// Test from Peter Ohman
	private static SVPA<ICharPred, Character> getCFGAutomata(BooleanAlgebra<ICharPred, Character> ba){
		Collection<SVPAMove<ICharPred, Character>> transitions = new LinkedList<SVPAMove<ICharPred, Character>>();

		// extra state "0" is prior to the entry of "main"
		transitions.add(new Internal<ICharPred, Character>(0, 1, new CharPred('1')));
		transitions.add(new Call<ICharPred, Character>(1, 2, 1, new CharPred('2')));
		transitions.add(new Internal<ICharPred, Character>(2, 3, new CharPred('3')));
		transitions.add(new Internal<ICharPred, Character>(2, 4, new CharPred('4')));
		transitions.add(new Call<ICharPred, Character>(3, 2, 3, new CharPred('2')));
		transitions.add(new Return<ICharPred, Character>(4, 5, 3, new CharPred('5',true)));
		transitions.add(new Return<ICharPred, Character>(4, 6, 1, new CharPred('6',true)));
		transitions.add(new Return<ICharPred, Character>(4, 8, 7, new CharPred('8',true)));
		transitions.add(new Internal<ICharPred, Character>(5, 4, new CharPred('4')));
		transitions.add(new Internal<ICharPred, Character>(6, 7, new CharPred('7')));
		transitions.add(new Call<ICharPred, Character>(7, 2, 7, new CharPred('2')));
		transitions.add(new Internal<ICharPred, Character>(8, 9, new CharPred('9')));

		try {
			SVPA<ICharPred, Character> svpa =  SVPA.MkSVPA(transitions, Arrays.asList(0), Arrays.asList(5), ba);
			List<TaggedSymbol<Character>> l = svpa.getWitness(ba);
			System.out.println(l);
			return svpa;
		} catch (AutomataException e) {
			return null;
		}
	}

}
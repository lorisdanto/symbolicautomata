package test.SAFA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.SATRelation;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import automata.safa.booleanexpression.SumOfProducts;
import automata.safa.booleanexpression.SumOfProductsFactory;
import theory.BooleanAlgebra;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;
import theory.safa.SAFABooleanAlgebra;

public class SAFAUnitTest {
	@Test
	public void testIntersection() throws TimeoutException {
		SAFA<CharPred, Character> intersection = atLeastOneAlpha.intersectionWith(atLeastOneNum, ba);

		assertTrue(atLeastOneAlpha.accepts(la, ba));
		assertFalse(atLeastOneAlpha.accepts(lb, ba));
		assertTrue(atLeastOneAlpha.accepts(lab, ba));
		assertFalse(atLeastOneAlpha.accepts(lnot, ba));

		assertFalse(atLeastOneNum.accepts(la, ba));
		assertTrue(atLeastOneNum.accepts(lb, ba));
		assertTrue(atLeastOneNum.accepts(lab, ba));
		assertFalse(atLeastOneNum.accepts(lnot, ba));

		assertFalse(intersection.accepts(la, ba));
		assertFalse(intersection.accepts(lb, ba));
		assertTrue(intersection.accepts(lab, ba));
		assertFalse(intersection.accepts(lnot, ba));
	}
	
	
	@Test
	public void testUnion() throws TimeoutException {
		SAFA<CharPred, Character> union = atLeastOneAlpha.unionWith(atLeastOneNum, ba);

		assertTrue(atLeastOneAlpha.accepts(la, ba));
		assertFalse(atLeastOneAlpha.accepts(lb, ba));
		assertTrue(atLeastOneAlpha.accepts(lab, ba));
		assertFalse(atLeastOneAlpha.accepts(lnot, ba));

		assertFalse(atLeastOneNum.accepts(la, ba));
		assertTrue(atLeastOneNum.accepts(lb, ba));
		assertTrue(atLeastOneNum.accepts(lab, ba));
		assertFalse(atLeastOneNum.accepts(lnot, ba));

		assertTrue(union.accepts(la, ba));
		assertTrue(union.accepts(lb, ba));
		assertTrue(union.accepts(lab, ba));
		assertFalse(union.accepts(lnot, ba));
	}
	
	@Test
	public void testEquivalence() throws TimeoutException {
		SAFA<CharPred, Character> intersection1 = atLeastOneAlpha.intersectionWith(atLeastOneNum, ba);
		SAFA<CharPred, Character> intersection2 = atLeastOneNum.intersectionWith(atLeastOneAlpha, ba);

		assertFalse(SAFA.areReverseEquivalent(atLeastOneAlpha, atLeastOneNum, ba).first);
		assertFalse(SAFA.areReverseEquivalent(atLeastOneNum, atLeastOneAlpha, ba).first);
		assertFalse(SAFA.areReverseEquivalent(atLeastOneAlpha, intersection1, ba).first);
		assertFalse(SAFA.areReverseEquivalent(intersection1, atLeastOneAlpha, ba).first);
		assertFalse(SAFA.areReverseEquivalent(atLeastOneAlpha, intersection2, ba).first);
		assertFalse(SAFA.areReverseEquivalent(intersection2, atLeastOneAlpha, ba).first);
		assertFalse(SAFA.areReverseEquivalent(atLeastOneNum, intersection1, ba).first);
		assertFalse(SAFA.areReverseEquivalent(intersection1, atLeastOneNum, ba).first);
		assertFalse(SAFA.areReverseEquivalent(atLeastOneNum, intersection2, ba).first);
		assertFalse(SAFA.areReverseEquivalent(intersection2, atLeastOneNum, ba).first);
		assertTrue(SAFA.areReverseEquivalent(intersection2, intersection1, ba).first);
		assertTrue(SAFA.areReverseEquivalent(intersection1, intersection2, ba).first);
	}

	@Test
	public void testListOfLists() throws TimeoutException {
		System.out.println("lists of lists");
		BooleanAlgebra<SAFA<CharPred,Character>,List<Character>> lol = new SAFABooleanAlgebra<>(ba, boolexpr);
		CharPred a = new CharPred('a');
		CharPred z = new CharPred('z');
		SAFA<SAFA<CharPred,Character>,List<Character>> evAandZ = eventually(lol, eventually(ba, a).intersectionWith(eventually(ba, z), ba));
		SAFA<SAFA<CharPred,Character>,List<Character>> evAandEvZ = eventually(lol, eventually(ba, a)).intersectionWith(eventually(lol, eventually(ba, z)), lol);
		assertFalse(SAFA.isEquivalent(evAandZ, evAandEvZ, lol, boolexpr).first);
		assertTrue(SAFA.isEquivalent(evAandZ.unionWith(evAandEvZ, lol), evAandEvZ, lol, boolexpr).first);
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

	// Boolean expression factory
	BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

	SAFA<CharPred, Character> atLeastOneAlpha = getSAFAatLeastOne(ba,alpha);
	SAFA<CharPred, Character> atLeastOneNum = getSAFAatLeastOne(ba,num);

	// Test strings
	List<Character> la = lOfS("a#a"); // accepted only by autA
	List<Character> lb = lOfS("3#"); // accepted only by autB
	List<Character> lab = lOfS("a3"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("##"); // accepted only by neither autA nor autB

	// at least one 
	private SAFA<CharPred, Character> getSAFAatLeastOne(UnaryCharIntervalSolver ba, CharPred p) {

		Collection<SAFAInputMove<CharPred, Character>> transitionsA = new LinkedList<>();
		PositiveBooleanExpression sp0 = boolexpr.MkState(0);
		PositiveBooleanExpression sp1 = boolexpr.MkState(1);
		transitionsA.add(new SAFAInputMove<CharPred, Character>(0, sp0, ba.True()));
		transitionsA.add(new SAFAInputMove<CharPred, Character>(0, sp1, p));
		transitionsA.add(new SAFAInputMove<CharPred, Character>(1, sp1, ba.True()));
		try {
			return SAFA.MkSAFA(transitionsA, boolexpr.MkState(0), Arrays.asList(1), ba);
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

	private <P,S> SAFA<P,S> eventually(BooleanAlgebra<P,S> ba, P predicate) throws TimeoutException {
		PositiveBooleanExpression initialState = boolexpr.MkState(0);
		Collection<Integer> finalStates = new LinkedList<>();
		Collection<SAFAInputMove<P,S>> transitions = new LinkedList<>();
		transitions.add(new SAFAInputMove<P,S>(0, boolexpr.True(), predicate));
		transitions.add(new SAFAInputMove<P,S>(0, boolexpr.MkState(0), ba.MkNot(predicate)));
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba);
	}

//    @Test
//    public void testSATRelation() throws TimeoutException {
//    	SATRelation rel = new SATRelation();
//    	PositiveBooleanExpression sp0 = boolexpr.MkState(0);
//    	PositiveBooleanExpression sp1 = boolexpr.MkState(1);
//        assertFalse(rel.isMember(sp0, sp0));
//        rel.add(sp0, sp0);
//        assertTrue(rel.isMember(sp0, sp0));
//        assertFalse(rel.isMember(sp1, sp1));
//        rel.add(sp1, sp1);
//
//        assertFalse(rel.isMember(sp0, sp1));
//
//        assertTrue(rel.isMember(boolexpr.MkAnd(sp0, sp1), boolexpr.MkAnd(sp1, sp0)));
//        assertTrue(rel.isMember(boolexpr.MkOr(sp0, sp1), boolexpr.MkOr(sp1, sp0)));
//        assertFalse(rel.isMember(boolexpr.MkOr(sp0, sp1), boolexpr.MkAnd(sp0, sp1)));
//        assertFalse(rel.isMember(boolexpr.MkOr(sp0, sp1), sp1));
//        rel.add(sp0, sp1);
//        assertTrue(rel.isMember(boolexpr.MkOr(sp0, sp1), sp1));
//    }

	@Test
	public void testForwardEquivalence() throws TimeoutException {
		BooleanExpressionFactory<SumOfProducts> pos = SumOfProductsFactory.getInstance();
		SAFA<CharPred, Character> intersection1 = atLeastOneAlpha.intersectionWith(atLeastOneNum, ba);
		SAFA<CharPred, Character> intersection2 = atLeastOneNum.intersectionWith(atLeastOneAlpha, ba);
		assertFalse(SAFA.isEquivalent(atLeastOneAlpha, atLeastOneNum, ba, pos).first);
		assertFalse(SAFA.isEquivalent(atLeastOneNum, atLeastOneAlpha, ba, pos).first);
		assertFalse(SAFA.isEquivalent(atLeastOneAlpha, intersection1, ba, pos).first);
		assertFalse(SAFA.isEquivalent(intersection1, atLeastOneAlpha, ba, pos).first);
		assertFalse(SAFA.isEquivalent(atLeastOneAlpha, intersection2, ba, pos).first);
		assertFalse(SAFA.isEquivalent(intersection2, atLeastOneAlpha, ba, pos).first);
		assertFalse(SAFA.isEquivalent(atLeastOneNum, intersection1, ba, pos).first);
		assertFalse(SAFA.isEquivalent(intersection1, atLeastOneNum, ba, pos).first);
		assertFalse(SAFA.isEquivalent(atLeastOneNum, intersection2, ba, pos).first);
		assertFalse(SAFA.isEquivalent(intersection2, atLeastOneNum, ba, pos).first);
		assertTrue(SAFA.isEquivalent(intersection2, intersection1, ba, pos).first);
		assertTrue(SAFA.isEquivalent(intersection1, intersection2, ba, pos).first);
	}

	@Test
	public void testNegate() throws TimeoutException {
		SAFA<CharPred, Character> a = atLeastOneAlpha.intersectionWith(atLeastOneNum, ba);
		SAFA<CharPred, Character> b = atLeastOneNum.intersectionWith(atLeastOneAlpha, ba);
		SAFA<CharPred, Character> notA = a.negate(ba);
		SAFA<CharPred, Character> notB = b.negate(ba);
		BooleanExpressionFactory<SumOfProducts> boolexpr = SumOfProductsFactory.getInstance();

		assertTrue(SAFA.isEmpty(a.intersectionWith(notA, ba), ba));
		assertTrue(SAFA.isEmpty(b.intersectionWith(notB, ba), ba));
		assertTrue(SAFA.isEquivalent(a, notA.negate(ba), ba, boolexpr).first);
		assertTrue(SAFA.isEquivalent(a, notB.negate(ba), ba, boolexpr).first);
	}
}
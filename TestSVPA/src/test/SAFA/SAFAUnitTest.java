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

import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.SATRelation;
import automata.safa.booleanexpression.SumOfProducts;
import theory.CharPred;
import theory.CharSolver;
import theory.StdCharPred;

public class SAFAUnitTest {

	@Test
	public void testIntersection() {
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
	public void testUnion() {
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
	public void testEquivalence() {
		SAFA<CharPred, Character> intersection1 = atLeastOneAlpha.intersectionWith(atLeastOneNum, ba);
		SAFA<CharPred, Character> intersection2 = atLeastOneNum.intersectionWith(atLeastOneAlpha, ba);

		assertFalse(SAFA.isReverseEquivalent(atLeastOneAlpha, atLeastOneNum, ba));
		assertFalse(SAFA.isReverseEquivalent(atLeastOneNum, atLeastOneAlpha, ba));
		assertFalse(SAFA.isReverseEquivalent(atLeastOneAlpha, intersection1, ba));
		assertFalse(SAFA.isReverseEquivalent(intersection1, atLeastOneAlpha, ba));
		assertFalse(SAFA.isReverseEquivalent(atLeastOneAlpha, intersection2, ba));
		assertFalse(SAFA.isReverseEquivalent(intersection2, atLeastOneAlpha, ba));
		assertFalse(SAFA.isReverseEquivalent(atLeastOneNum, intersection1, ba));
		assertFalse(SAFA.isReverseEquivalent(intersection1, atLeastOneNum, ba));
		assertFalse(SAFA.isReverseEquivalent(atLeastOneNum, intersection2, ba));
		assertFalse(SAFA.isReverseEquivalent(intersection2, atLeastOneNum, ba));
		assertTrue(SAFA.isReverseEquivalent(intersection2, intersection1, ba));
		assertTrue(SAFA.isReverseEquivalent(intersection1, intersection2, ba));
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

	SAFA<CharPred, Character> atLeastOneAlpha = getSAFAatLeastOne(ba,alpha);
	SAFA<CharPred, Character> atLeastOneNum = getSAFAatLeastOne(ba,num);

	// Test strings
	List<Character> la = lOfS("a#a"); // accepted only by autA
	List<Character> lb = lOfS("3#"); // accepted only by autB
	List<Character> lab = lOfS("a3"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("##"); // accepted only by neither autA nor autB

	// at least one 
	private SAFA<CharPred, Character> getSAFAatLeastOne(CharSolver ba, CharPred p) {

		Collection<SAFAInputMove<CharPred, Character>> transitionsA = new LinkedList<SAFAInputMove<CharPred, Character>>();
		SumOfProducts sp0 = new SumOfProducts(0);
		SumOfProducts sp1 = new SumOfProducts(1);
		transitionsA.add(new SAFAInputMove<CharPred, Character>(0, sp0, ba.True()));
		transitionsA.add(new SAFAInputMove<CharPred, Character>(0, sp1, p));
		transitionsA.add(new SAFAInputMove<CharPred, Character>(1, sp1, ba.True()));
		return SAFA.MkSAFA(transitionsA, 0, Arrays.asList(1), ba);
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


    @Test
    public void testSATRelation() throws TimeoutException {
    	SATRelation rel = new SATRelation();
    	SumOfProducts sp0 = new SumOfProducts(0);
        SumOfProducts sp1 = new SumOfProducts(1);
        assertFalse(rel.isMember(sp0, sp0));
        rel.add(sp0, sp0);
        assertTrue(rel.isMember(sp0, sp0));
        assertFalse(rel.isMember(sp1, sp1));
        rel.add(sp1, sp1);

        assertFalse(rel.isMember(sp0, sp1));

        assertTrue(rel.isMember(sp0.and(sp1), sp1.and(sp0)));
        assertTrue(rel.isMember(sp0.or(sp1), sp1.or(sp0)));
        assertFalse(rel.isMember(sp0.or(sp1), sp1.and(sp0)));
        assertFalse(rel.isMember(sp0.or(sp1), sp1));
        rel.add(sp0, sp1);
        assertTrue(rel.isMember(sp0.or(sp1), sp1));
    }

	@Test
	public void testForwardEquivalence() throws TimeoutException {
		SAFA<CharPred, Character> intersection1 = atLeastOneAlpha.intersectionWith(atLeastOneNum, ba);
		SAFA<CharPred, Character> intersection2 = atLeastOneNum.intersectionWith(atLeastOneAlpha, ba);
		assertFalse(SAFA.isEquivalent(atLeastOneAlpha, atLeastOneNum, ba));
		assertFalse(SAFA.isEquivalent(atLeastOneNum, atLeastOneAlpha, ba));
		assertFalse(SAFA.isEquivalent(atLeastOneAlpha, intersection1, ba));
		assertFalse(SAFA.isEquivalent(intersection1, atLeastOneAlpha, ba));
		assertFalse(SAFA.isEquivalent(atLeastOneAlpha, intersection2, ba));
		assertFalse(SAFA.isEquivalent(intersection2, atLeastOneAlpha, ba));
		assertFalse(SAFA.isEquivalent(atLeastOneNum, intersection1, ba));
		assertFalse(SAFA.isEquivalent(intersection1, atLeastOneNum, ba));
		assertFalse(SAFA.isEquivalent(atLeastOneNum, intersection2, ba));
		assertFalse(SAFA.isEquivalent(intersection2, atLeastOneNum, ba));
		assertTrue(SAFA.isEquivalent(intersection2, intersection1, ba));
		assertTrue(SAFA.isEquivalent(intersection1, intersection2, ba));
	}
}

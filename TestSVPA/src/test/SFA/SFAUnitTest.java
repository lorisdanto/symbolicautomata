package test.SFA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAEpsilon;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class SFAUnitTest {

	@Test
	public void testCharTheory() throws TimeoutException {

		LinkedList<SFAMove<CharPred, Character>> transitionsRex = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsRex.add(new SFAInputMove<CharPred, Character>(0, 2, new CharPred('0', '8')));
		transitionsRex.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('b')));
		transitionsRex.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('b')));
		transitionsRex.add(new SFAInputMove<CharPred, Character>(2, 2, new CharPred('0', '8')));

		LinkedList<Integer> finStates = new LinkedList<>();
		finStates.add(1);

		LinkedList<SFAMove<CharPred, Character>> transitionsLeft = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsLeft.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('0', '9')));
		transitionsLeft.add(new SFAInputMove<CharPred, Character>(0, 2, new CharPred('b')));
		transitionsLeft.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('b')));
		transitionsLeft.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('0', '9')));

		LinkedList<Integer> finStates2 = new LinkedList<>();
		finStates2.add(1);
		for (int i = 0; i < 100; i++) {
			SFA<CharPred, Character> rex = SFA.MkSFA(transitionsRex, 0, finStates, ba);
			SFA<CharPred, Character> left = SFA.MkSFA(transitionsLeft, 0, finStates2, ba);

			SFA<CharPred, Character> min = left.minus(rex, ba);
			if (min.isEmpty())
				System.out.println(min);
			assertFalse(rex.isEquivalentTo(left, ba));
		}

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

	@Test
	public void testEpsRemove() throws TimeoutException {
		SFA<CharPred, Character> autAnoEps = autA.removeEpsilonMoves(ba);

		assertFalse(autA.isEpsilonFree());
		assertTrue(autAnoEps.isEpsilonFree());
	}

	@Test
	public void testAmbiguity() throws TimeoutException {
		SFA<CharPred, Character> autAmb = getAmbSFA(ba);
		SFA<CharPred, Character> autUnamb = getUnambSFA(ba);
		SFA<CharPred, Character> epsAmb = getEpsAmbSFA(ba);
		SFA<CharPred, Character> epsUnamb = getEpsUnambSFA(ba);

		List<Character> a = autAmb.getAmbiguousInput(ba);
		List<Character> u = autUnamb.getAmbiguousInput(ba);
		
		List<Character> aeps = epsAmb.getAmbiguousInput(ba);
		List<Character> ueps = epsUnamb.getAmbiguousInput(ba);
		
		assertTrue(a != null);
		assertTrue(u == null);
		assertTrue(aeps != null);
		assertTrue(ueps == null);
	}

	@Test
	public void testIntersectionWith() throws TimeoutException {
		SFA<CharPred, Character> intersection = autA.intersectionWith(autB, ba);

		assertTrue(autA.accepts(la, ba));
		assertFalse(autA.accepts(lb, ba));
		assertTrue(autA.accepts(lab, ba));
		assertFalse(autA.accepts(lnot, ba));

		assertFalse(autB.accepts(la, ba));
		assertTrue(autB.accepts(lb, ba));
		assertTrue(autB.accepts(lab, ba));
		assertFalse(autB.accepts(lnot, ba));

		assertFalse(intersection.accepts(la, ba));
		assertFalse(intersection.accepts(lb, ba));
		assertTrue(intersection.accepts(lab, ba));
		assertFalse(intersection.accepts(lnot, ba));

	}

	@Test
	public void testMinimization() throws TimeoutException {
		SFA<CharPred, Character> autM = getSFAtoMin2(ba);

		SFA<CharPred, Character> min = autM.minimize(ba);

		assertTrue(autM.accepts(la, ba));
		assertFalse(autM.accepts(lnot, ba));
		assertTrue(min.accepts(la, ba));
		assertFalse(min.accepts(lnot, ba));

		assertTrue(min.stateCount() == 3);

		assertTrue(min.isEquivalentTo(autM, ba));
	}

	@Test
	public void testDeterminization() throws TimeoutException {
		SFA<CharPred, Character> detAutA = autA.determinize(ba);

		assertFalse(autA.isDeterministic(ba));
		assertTrue(detAutA.isDeterministic(ba));
		assertFalse(autA.isDeterministic(ba));
	}

	@Test
	public void testMkTotal() throws TimeoutException {
		SFA<CharPred, Character> autcSfa = getSFAc(ba);
		SFA<CharPred, Character> totc = autcSfa.mkTotal(ba);

		assertTrue(autcSfa.isDeterministic(ba));
		assertTrue(totc.isDeterministic(ba));
	}

	@Test
	public void testGetWitness() throws TimeoutException {
		SFA<CharPred, Character> ca = autA.complement(ba);

		boolean oneIsOk = false;
		for (Character e : ca.getWitness(ba))
			oneIsOk = oneIsOk || ba.HasModel(ba.MkNot(alpha), e);

		assertTrue(oneIsOk);
	}

	@Test
	public void testComplement() throws TimeoutException {
		SFA<CharPred, Character> complementA = autA.complement(ba);
		SFA<CharPred, Character> complementB = autB.complement(ba);

		assertTrue(autA.accepts(la, ba));
		assertFalse(autA.accepts(lb, ba));
		assertTrue(autA.accepts(lab, ba));
		assertFalse(autA.accepts(lnot, ba));

		assertFalse(complementA.accepts(la, ba));
		assertTrue(complementA.accepts(lb, ba));
		assertFalse(complementA.accepts(lab, ba));
		assertTrue(complementA.accepts(lnot, ba));

		assertFalse(autB.accepts(la, ba));
		assertTrue(autB.accepts(lb, ba));
		assertTrue(autB.accepts(lab, ba));
		assertFalse(autB.accepts(lnot, ba));

		assertTrue(complementB.accepts(la, ba));
		assertFalse(complementB.accepts(lb, ba));
		assertFalse(complementB.accepts(lab, ba));
		assertTrue(complementB.accepts(lnot, ba));
	}

	@Test
	public void testEquivalence() throws TimeoutException {
		SFA<CharPred, Character> cA = autA.complement(ba);
		SFA<CharPred, Character> cUcA = autA.unionWith(cA, ba);
		SFA<CharPred, Character> ccA = cA.complement(ba);
		SFA<CharPred, Character> cB = autB.complement(ba);
		SFA<CharPred, Character> cUcB = autB.unionWith(cB, ba);
		SFA<CharPred, Character> ccB = cB.complement(ba);

		assertFalse(autA.isEquivalentTo(autB, ba));

		assertTrue(autA.isEquivalentTo(ccA, ba));

		boolean res = autB.isEquivalentTo(ccB, ba);

		assertTrue(res);

		assertTrue(cUcA.isEquivalentTo(SFA.getFullSFA(ba), ba));

		assertTrue(cUcB.isEquivalentTo(SFA.getFullSFA(ba), ba));
		assertTrue(cUcB.isEquivalentTo(cUcA, ba));
	}

	@Test
	public void testEquivalenceHK() throws TimeoutException {
		SFA<CharPred, Character> cA = autA.complement(ba);
		SFA<CharPred, Character> cUcA = autA.unionWith(cA, ba);
		SFA<CharPred, Character> ccA = cA.complement(ba);
		SFA<CharPred, Character> cB = autB.complement(ba);
		SFA<CharPred, Character> cUcB = autB.unionWith(cB, ba);
		SFA<CharPred, Character> ccB = cB.complement(ba);

		assertFalse(autA.isHopcroftKarpEquivalentTo(autB, ba).first);

		assertTrue(autA.isHopcroftKarpEquivalentTo(ccA, ba).first);

		boolean res = autB.isHopcroftKarpEquivalentTo(ccB, ba).first;

		assertTrue(res);
		assertTrue(cUcA.isHopcroftKarpEquivalentTo(SFA.getFullSFA(ba), ba).first);
		assertTrue(cUcB.isHopcroftKarpEquivalentTo(SFA.getFullSFA(ba), ba).first);
		assertTrue(cUcB.isHopcroftKarpEquivalentTo(cUcA, ba).first);
	}

	@Test
	public void testUnion() throws TimeoutException {
		SFA<CharPred, Character> union = autA.unionWith(autB, ba);

		assertTrue(autA.accepts(la, ba));
		assertFalse(autA.accepts(lb, ba));
		assertTrue(autA.accepts(lab, ba));
		assertFalse(autA.accepts(lnot, ba));

		assertFalse(autB.accepts(la, ba));
		assertTrue(autB.accepts(lb, ba));
		assertTrue(autB.accepts(lab, ba));
		assertFalse(autB.accepts(lnot, ba));

		assertTrue(union.accepts(la, ba));
		assertTrue(union.accepts(lb, ba));
		assertTrue(union.accepts(lab, ba));
		assertFalse(union.accepts(lnot, ba));
	}

	@Test
	public void testDifference() throws TimeoutException {
		SFA<CharPred, Character> difference = autA.minus(autB, ba);

		assertTrue(autA.accepts(la, ba));
		assertFalse(autA.accepts(lb, ba));
		assertTrue(autA.accepts(lab, ba));
		assertFalse(autA.accepts(lnot, ba));

		assertFalse(autB.accepts(la, ba));
		assertTrue(autB.accepts(lb, ba));
		assertTrue(autB.accepts(lab, ba));
		assertFalse(autB.accepts(lnot, ba));

		assertTrue(difference.accepts(la, ba));
		assertFalse(difference.accepts(lb, ba));
		assertFalse(difference.accepts(lab, ba));
		assertFalse(difference.accepts(lnot, ba));
	}

	@Test
	public void testMinus() throws TimeoutException {
		SFA<CharPred, Character> justA = justAlpha(ba);
		SFA<CharPred, Character> plus = justA.concatenateWith(SFA.star(justA, ba), ba);

		SFA<CharPred, Character> sfaAMPlus = justA.minus(plus, ba);
		assertTrue(sfaAMPlus.isEmpty());

		SFA<CharPred, Character> sfaPlusMA = plus.minus(justA, ba);
		assertFalse(sfaPlusMA.isEmpty());
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

	SFA<CharPred, Character> autA = getSFAa(ba);
	SFA<CharPred, Character> autB = getSFAb(ba);

	// Test strings
	List<Character> la = lOfS("aa"); // accepted only by autA
	List<Character> lb = lOfS("a3"); // accepted only by autB
	List<Character> lab = lOfS("a"); // accepted only by both autA and autB
	List<Character> lnot = lOfS("44"); // accepted only by neither autA nor autB

	// [a-z]+ ambiguous
	private SFA<CharPred, Character> justAlpha(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, alpha));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z]+ ambiguous
	private SFA<CharPred, Character> getAmbSFA(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(1, 1, alpha));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z]+ ambiguous
	private SFA<CharPred, Character> getEpsAmbSFA(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAEpsilon<CharPred, Character>(0, 1));
		transitionsA.add(new SFAEpsilon<CharPred, Character>(0, 2));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1, 2), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z]+ unambiguos
	private SFA<CharPred, Character> getUnambSFA(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, alpha));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// [a-z]+ ambiguous
	private SFA<CharPred, Character> getEpsUnambSFA(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new SFAEpsilon<CharPred, Character>(0, 1));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1), ba);
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

	// [a-z]+
	private SFA<CharPred, Character> getSFAtoMin2(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();

		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(1, 2, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(2, 2, alpha));
		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1, 2), ba);
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
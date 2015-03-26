package test.SFA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import theory.CharPred;
import theory.CharSolver;
import automata.AutomataException;
import automata.fsa.Epsilon;
import automata.fsa.InputMove;
import automata.fsa.SFA;
import automata.fsa.SFAMove;

public class SFAUnitTest {

	@Test
	public void testCharTheory() {
		CharSolver solver = new CharSolver();
		
		
		LinkedList<SFAMove<CharPred, Character>> transitionsRex = new LinkedList<SFAMove<CharPred, Character>>();
        transitionsRex.add(new InputMove<CharPred, Character>(0, 2, new CharPred('0','8')));
        transitionsRex.add(new InputMove<CharPred, Character>(0, 1, new CharPred('b')));
        transitionsRex.add(new InputMove<CharPred, Character>(2, 1, new CharPred('b')));
        transitionsRex.add(new InputMove<CharPred, Character>(2, 2, new CharPred('0','8')));
        
        LinkedList<Integer> finStates = new LinkedList<>();
        finStates.add(1);
        
        LinkedList<SFAMove<CharPred, Character>> transitionsLeft = new LinkedList<SFAMove<CharPred, Character>>();
        transitionsLeft.add(new InputMove<CharPred, Character>(0, 1, new CharPred('0','9')));
        transitionsLeft.add(new InputMove<CharPred, Character>(0, 2, new CharPred('b')));
        transitionsLeft.add(new InputMove<CharPred, Character>(1, 2, new CharPred('b')));
        transitionsLeft.add(new InputMove<CharPred, Character>(1, 1, new CharPred('0','9')));
        
        LinkedList<Integer> finStates2 = new LinkedList<>();
        finStates2.add(1);
        for(int i=0;i<100;i++){
        	SFA<CharPred, Character> rex = SFA.MkSFA(transitionsRex, 0, finStates, solver);
        	SFA<CharPred, Character> left = SFA.MkSFA(transitionsLeft, 0, finStates2, solver);        
        	
        	SFA<CharPred, Character> min = left.minus(rex, solver);
        	if(min.isEmpty)
        		System.out.println(min);
        	assertFalse(rex.isEquivalentTo(left, solver));
        }
        
		
	}
	
	@Test
	public void testCreateDot() {
		try {

			CharSolver ba = new CharSolver();
			
			SFA<CharPred, Character> autB = getSFAb(ba);		
			boolean check = autB.createDotFile("autb", "");

			assertTrue(check);

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testEmptyFull() {
		CharSolver ba = new CharSolver();

			SFA<CharPred, Character> empty = SFA.getEmptySFA(ba);	
			SFA<CharPred, Character> full = SFA.getFullSFA(ba);	


			boolean check = empty.createDotFile("empty", "");
			check = full.createDotFile("full", "");
			
			assertTrue(check);

	}

	@Test
	public void testMkSFA() {

		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);

			SFA<CharPred, Character> autB = getSFAb(ba);			

			assertTrue(autA.stateCount() == 2);
			assertTrue(autA.transitionCount == 2);			
			
			assertTrue(autB.stateCount() == 2);
			assertTrue(autB.transitionCount == 3);

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testEpsRemove() {
		try {
			CharSolver ba = new CharSolver();
			
			//First Automaton
			SFA<CharPred, Character> autA = getSFAa(ba);
			
			//Second Automaton
			SFA<CharPred, Character> autAnoEps = autA.removeEpsilonMoves(ba);
			
			assertFalse(autA.isEpsilonFree);
			assertTrue(autAnoEps.isEpsilonFree);

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testAmbiguity() {
		try {
			CharSolver ba = new CharSolver();
			
			//First Automaton
			SFA<CharPred, Character> autAmb = getAmbSFA(ba);
			SFA<CharPred, Character> autUnamb = getUnambSFA(ba);
			
			List<Character> a = autAmb.getAmbiguousInput(ba);
			
			List<Character> u = autUnamb.getAmbiguousInput(ba);			
			assertTrue(a!=null);
			assertTrue(u==null);

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testIntersectionWith() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);

			SFA<CharPred, Character> autB = getSFAb(ba);

			SFA<CharPred, Character> intersection = autA
					.intersectionWith(autB, ba);

			List<Character> la = Arrays.asList((char) 10,
					(char) 10);
			List<Character> lb = Arrays.asList((char) 11,
					(char) 3);
			List<Character> lab = Arrays.asList((char) 11,
					(char) 11);
			List<Character> lnot = Arrays.asList((char) 2);


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

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testMinimization() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autM = getSFAtoMin(ba);
			
			SFA<CharPred, Character> min = autM.minimize(ba);

			
			List<Character> la = Arrays.asList((char) 10,
					(char) 11);
			List<Character> lnot = Arrays.asList((char) 2,
					(char) 2);


			assertTrue(autM.accepts(la, ba));
			assertFalse(autM.accepts(lnot, ba));
			assertTrue(min.accepts(la, ba));
			assertFalse(min.accepts(lnot, ba));
			
			assertTrue(min.stateCount()==2);

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testDeterminization() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);
			SFA<CharPred, Character> detAutA = autA.determinize(ba);
			
			assertFalse(autA.isDeterministic(ba));
			assertTrue(detAutA.isDeterministic(ba));
			assertFalse(autA.isDeterministic(ba));
			
		} catch (AutomataException e) {
			System.out.print(e);
		}
	}	
	
	@Test
	public void testMkTotal() {
		try {
			
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autcSfa = getSFAc(ba);
			SFA<CharPred, Character> totc = autcSfa.mkTotal(ba);
			
			assertTrue(autcSfa.isDeterministic(ba));
			assertTrue(totc.isDeterministic(ba));
			
		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testGetWitness() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);
			SFA<CharPred, Character> ca = autA.complement(ba);
			
			CharPred le0 = new CharPred((char)0,(char)10);
			
			
			boolean oneIsOk = false;
			for(Character e: ca.getWitness(ba))
				oneIsOk = oneIsOk || ba.HasModel(le0, e);

			assertTrue(oneIsOk);

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}	
	
	@Test
	public void testComplement() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);

			SFA<CharPred, Character> complementA = autA
					.complement(ba);
			
			SFA<CharPred, Character> autB = getSFAb(ba);

			SFA<CharPred, Character> complementB= autB
					.complement(ba);


			List<Character> la = Arrays.asList((char) 10,
					(char) 10);
			List<Character> lb = Arrays.asList((char) 11,
					(char) 3);
			List<Character> lab = Arrays.asList((char) 11,
					(char) 11);
			List<Character> lnot = Arrays.asList((char) 2);


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

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	

	@Test
	public void testEquivalence() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);
			
			SFA<CharPred, Character> cA = autA
					.complement(ba);	
			
			SFA<CharPred, Character> cUcA = autA
					.unionWith(cA, ba);
			
			SFA<CharPred, Character> ccA = cA
					.complement(ba);
			
			SFA<CharPred, Character> autB = getSFAb(ba);

			SFA<CharPred, Character> cB = autB
					.complement(ba);	
			
			SFA<CharPred, Character> cUcB = autB
					.unionWith(cB, ba);
			
			SFA<CharPred, Character> ccB = cB
					.complement(ba);
			
			assertFalse(autA.isEquivalentTo(autB, ba));	
			
			
			
			assertTrue(autA.isEquivalentTo(ccA, ba));			
			
			
			
			boolean res = autB.isEquivalentTo(ccB, ba);
			
			assertTrue(res);
			
			assertTrue(cUcA.isEquivalentTo(SFA.getFullSFA(ba), ba));
			
			assertTrue(cUcB.isEquivalentTo(SFA.getFullSFA(ba), ba));
			assertTrue(cUcB.isEquivalentTo(cUcA, ba));

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testUnion() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);

			SFA<CharPred, Character> autB = getSFAb(ba);

			SFA<CharPred, Character> union = autA
					.unionWith(autB, ba);

			List<Character> la = Arrays.asList((char) 10,
					(char) 10);
			List<Character> lb = Arrays.asList((char) 11,
					(char) 3);
			List<Character> lab = Arrays.asList((char) 11,
					(char) 11);
			List<Character> lnot = Arrays.asList((char) 2);
			
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

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}
	
	@Test
	public void testDifference() {
		try {
			CharSolver ba = new CharSolver();

			SFA<CharPred, Character> autA = getSFAa(ba);

			SFA<CharPred, Character> autB = getSFAb(ba);

			SFA<CharPred, Character> difference = autA
					.minus(autB, ba);

			List<Character> la = Arrays.asList((char) 10,
					(char) 10);
			List<Character> lb = Arrays.asList((char) 11,
					(char) 3);
			List<Character> lab = Arrays.asList((char) 11,
					(char) 11);
			List<Character> lnot = Arrays.asList((char) 2);


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

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}

	private SFA<CharPred, Character> getAmbSFA(CharSolver ba) throws 
			AutomataException {

		CharPred geq0 = new CharPred((char)10,(char)Character.MAX_VALUE);

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new InputMove<CharPred, Character>(0, 0,
				geq0));
		transitionsA.add(new InputMove<CharPred, Character>(0, 1,
				geq0));
		transitionsA.add(new InputMove<CharPred, Character>(1, 1,
				geq0));
		return SFA.MkSFA(transitionsA,
				0, Arrays.asList(1), ba);
	}
	
	private SFA<CharPred, Character> getUnambSFA(CharSolver ba) throws 
			AutomataException {

		CharPred geq0 = new CharPred((char)10,(char)Character.MAX_VALUE);

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();	
		transitionsA.add(new InputMove<CharPred, Character>(0, 0,
				geq0));
		transitionsA.add(new InputMove<CharPred, Character>(0, 1,
				geq0));
		return SFA.MkSFA(transitionsA,
				0, Arrays.asList(1), ba);
	}
	
	
	private SFA<CharPred, Character> getSFAa(CharSolver ba) throws 
			AutomataException {

		CharPred geq0 = new CharPred((char)10,(char)Character.MAX_VALUE);

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsA.add(new Epsilon<CharPred, Character>(0, 1));		
		transitionsA.add(new InputMove<CharPred, Character>(0, 0,
				geq0));
		return SFA.MkSFA(transitionsA,
				0, Arrays.asList(0,1), ba);
	}
	
	private SFA<CharPred, Character> getSFAtoMin(CharSolver ba) throws 
			AutomataException {

		CharPred geq0 = new CharPred((char)10,(char)Character.MAX_VALUE);

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
		
		transitionsA.add(new InputMove<CharPred, Character>(0, 1,geq0));
		transitionsA.add(new InputMove<CharPred, Character>(1, 2,
				geq0));
		transitionsA.add(new InputMove<CharPred, Character>(2, 2,
				geq0));
		return SFA.MkSFA(transitionsA,
				0, Arrays.asList(1,2), ba);
	}
	
	private SFA<CharPred, Character> getSFAc(CharSolver ba) throws
			AutomataException {

		CharPred geq0 = new CharPred((char)10,(char)Character.MAX_VALUE);

		Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();	
		transitionsA.add(new InputMove<CharPred, Character>(0, 0,
				geq0));
		return SFA.MkSFA(transitionsA,
				0, Arrays.asList(0), ba);
	}
	
	private SFA<CharPred, Character> getSFAb(CharSolver ba) throws
	AutomataException {

		CharPred leq3 = new CharPred((char)10,(char)30);
		CharPred leq3gtm1 = new CharPred((char)11,(char)30);		
		CharPred leqm1 = new CharPred((char)1,(char)6);	


		Collection<SFAMove<CharPred, Character>> transitionsB = new LinkedList<SFAMove<CharPred, Character>>();
		transitionsB.add(new InputMove<CharPred, Character>(0, 1,
				leq3gtm1));
		transitionsB.add(new InputMove<CharPred, Character>(1, 1,
				leq3));
		transitionsB.add(new InputMove<CharPred, Character>(1, 1,
				leqm1));
		transitionsB.add(new InputMove<CharPred, Character>(0, 2,
				leq3));
		return SFA.MkSFA(transitionsB,
				0, Arrays.asList(1), ba);
	}

}

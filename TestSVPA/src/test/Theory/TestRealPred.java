package test.Theory;
import theory.intervals.RealPred;
import theory.intervals.RealSolver;
import theory.intervals.StdRealPred;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;
import utilities.Quadruple;

public class TestRealPred {
	RealSolver ba = new RealSolver();
	
	@Test
	public void testSat() {
		RealPred p = new RealPred(0.0, false, 1.0, true);
		assertTrue(ba.IsSatisfiable(p));
		assertTrue(p.isSatisfiedBy(0.5));
		assertTrue(p.isSatisfiedBy(0.0));
		assertFalse(p.isSatisfiedBy(1.0));
		
		RealPred p2 = new RealPred(-1.0);
		assertTrue(p2.isSatisfiedBy(-1.0));
	}
	

	@Test
	public void testBooleanOperators() {
		RealPred p1 = StdRealPred.TRUE;
		RealPred p2 = new RealPred(0.0, false, 1.0, true);
		RealPred p3 = ba.MkNot(p2);
		
		assertTrue(p3.isSatisfiedBy(-1.0));
		assertFalse(p3.isSatisfiedBy(0.0));
		assertTrue(p3.isSatisfiedBy(1.0));
		assertTrue(ba.AreEquivalent(p1, ba.MkOr(p2,p3)));
		
		RealPred p4 = new RealPred(-1.0, false, 0.0, false);
		
		RealPred p5 = new RealPred(0.5, false, null, false);
		RealPred p6 = ba.MkAnd(p5, p2);
		assertTrue(ba.AreEquivalent(p6, new RealPred(0.5, false, 1.0, true)));
		
		RealPred p7 = ba.MkAnd(p4, p2);
		RealPred p8 = new RealPred(0.0);
		
		assertTrue(ba.AreEquivalent(p7, p8));
	}
	
	/*
	@Test
	public void testGetSep() {
		ArrayList<List<Double>> data = new ArrayList<List<Double>>(Arrays.asList(Arrays.asList(0.0, 1.0, 3.0), Arrays.asList(0.0, 1.0, 2.0, 3.0)));
		ba.GetSeparatingPredicates(data, Long.MAX_VALUE);
	}*/
}

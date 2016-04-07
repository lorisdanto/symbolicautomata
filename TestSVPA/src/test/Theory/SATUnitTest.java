package test.Theory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import theory.sat.SATBooleanAlgebra;

public class SATUnitTest {
	int universe = 3;
	SATBooleanAlgebra ba = new SATBooleanAlgebra(universe);

	@Test
	public void testEquivalent() {
		Integer p = ba.MkAnd(1,ba.MkAnd(2,3));
		Integer q = ba.MkAnd(1,ba.MkAnd(3,2));
		Integer r = ba.MkAnd(ba.MkAnd(1,2),3);
		Integer s = ba.MkOr(ba.MkOr(1,2),3);

		assertTrue(ba.AreEquivalent(p, q));
		assertTrue(ba.AreEquivalent(q, r));
		assertFalse(ba.AreEquivalent(p, s));
		assertTrue(ba.AreEquivalent(ba.MkAnd(1,ba.MkOr(2,3)),
				ba.MkOr(ba.MkAnd(1,2), ba.MkAnd(1,3))));
		assertTrue(ba.AreEquivalent(ba.True(), ba.MkOr(ba.MkNot(p), p)));
		assertTrue(ba.AreEquivalent(ba.False(), ba.MkAnd(ba.MkNot(p), p)));
		assertFalse(ba.AreEquivalent(ba.True(), ba.False()));
	}
	
	@Test
	public void testSatisfiable() {
		Integer p = ba.MkAnd(1, 2);
		Integer q = ba.MkNot(1);
		assertTrue(ba.IsSatisfiable(p));
		assertTrue(ba.IsSatisfiable(q));
		assertFalse(ba.IsSatisfiable(ba.MkAnd(p,q)));
		assertTrue(ba.IsSatisfiable(ba.True()));
		assertFalse(ba.IsSatisfiable(ba.False()));
	}
	
	@Test
	public void testHasModel() {
		boolean[] m1 = { true, false, false };
		boolean[] m2 = { true, true, false };
		assertTrue(ba.HasModel(1, m1));
		assertFalse(ba.HasModel(2, m1));
		assertTrue(ba.HasModel(ba.MkOr(1, 2), m1));
		assertFalse(ba.HasModel(ba.MkAnd(1, 2), m1));
		assertTrue(ba.HasModel(ba.MkOr(1, 2), m2));
		assertTrue(ba.HasModel(ba.MkAnd(1, 2), m2));
	}

	@Test
	public void testGenerateWitness() {
		Integer p1 = ba.MkAnd(3,ba.MkOr(1,-3));
		boolean[] m1 = ba.generateWitness(p1);
		assertTrue(ba.HasModel(p1, m1));
		Integer p2 = ba.MkAnd(ba.MkOr(-2, 3),ba.MkOr(1,-3));
		boolean[] m2 = ba.generateWitness(p2);
		assertTrue(ba.HasModel(p2, m2));
	}
}

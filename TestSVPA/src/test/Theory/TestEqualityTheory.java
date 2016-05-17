package test.Theory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import theory.characters.BinaryCharPred;
import theory.characters.CharPred;
import theory.characters.ICharPred;
import theory.characters.StdCharPred;
import theory.intervals.EqualitySolver;

public class TestEqualityTheory {

	EqualitySolver ba = new EqualitySolver();
	CharPred alpha = StdCharPred.LOWER_ALPHA;
	CharPred allAlpha = StdCharPred.ALPHA;
	CharPred a = new CharPred('a');
	CharPred num = StdCharPred.NUM;
	CharPred trueChar = StdCharPred.TRUE;
	CharPred comma = new CharPred(',');
	BinaryCharPred equality = new BinaryCharPred(StdCharPred.TRUE, true);
	
	@Test
	public void testNot() {
		ICharPred notEquality = ba.MkNot(equality);
		ICharPred notNotEquality = ba.MkNot(notEquality);
		
		assertTrue(ba.AreEquivalent(equality, notNotEquality));
	}
	
	@Test
	public void testSat() {
		
		assertTrue(ba.IsSatisfiable(equality));
		ICharPred notEquality = ba.MkNot(equality);
		assertTrue(ba.IsSatisfiable(notEquality));		
	}
}

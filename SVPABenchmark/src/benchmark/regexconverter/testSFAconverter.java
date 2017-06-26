package benchmark.regexconverter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import benchmark.SFAprovider;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class testSFAconverter {
	UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();

	@Test
	public void testConcateAndUnion() throws TimeoutException {
		String regex = "abc|de";
		SFAprovider test = new SFAprovider(regex, solver);
		SFA<CharPred, Character> sfa = test.getSFA();
		assertFalse(sfa.accepts(lOfS("ab"), solver));
		assertTrue(sfa.accepts(lOfS("abc"), solver));
		assertTrue(sfa.accepts(lOfS("de"), solver));
	}
	
	@Test
	public void testMetaChar() throws TimeoutException {
		String regex = "\\s";
		SFAprovider test = new SFAprovider(regex, solver);
		SFA<CharPred, Character> sfa = test.getSFA();
		assertFalse(sfa.accepts(lOfS("a"), solver));
		assertTrue(sfa.accepts(lOfS(" "), solver));
		
		String regex2 = "\\S";
		SFAprovider test2 = new SFAprovider(regex2, solver);
		SFA<CharPred, Character> sfa2 = test2.getSFA();
		assertTrue(sfa2.accepts(lOfS("a"), solver));
		assertFalse(sfa2.accepts(lOfS(" "), solver));
		
		String regex3 = "\\w";
		SFAprovider test3 = new SFAprovider(regex3, solver);
		SFA<CharPred, Character> sfa3 = test3.getSFA();
		assertTrue(sfa3.accepts(lOfS("a"), solver));
		assertTrue(sfa3.accepts(lOfS("_"), solver));
		assertTrue(sfa3.accepts(lOfS("1"), solver));
		assertFalse(sfa3.accepts(lOfS(" "), solver));
	}
	
	@Test
	public void testCharacterClass() throws TimeoutException {
		String regex1 = "[\\d]";
		SFAprovider test1 = new SFAprovider(regex1, solver);
		SFA<CharPred, Character> sfa1 = test1.getSFA();
		assertFalse(sfa1.accepts(lOfS("10"), solver));
		assertTrue(sfa1.accepts(lOfS("1"), solver));
		
		String regex2 = "[\\a123bc!@#$s%^&*(){}]";
		SFAprovider test2 = new SFAprovider(regex2, solver);
		SFA<CharPred, Character> sfa2 = test2.getSFA();
		assertFalse(sfa2.accepts(lOfS("10"), solver));
		assertTrue(sfa2.accepts(lOfS("1"), solver));
		assertTrue(sfa2.accepts(lOfS("a"), solver));
		assertTrue(sfa2.accepts(lOfS("^"), solver));
		assertTrue(sfa2.accepts(lOfS("s"), solver));
		
		
		String regex3 = "[a-zA-Z1-9]";
		SFAprovider test3 = new SFAprovider(regex3, solver);
		SFA<CharPred, Character> sfa3 = test3.getSFA();
		assertFalse(sfa3.accepts(lOfS("10"), solver));
		assertTrue(sfa3.accepts(lOfS("8"), solver));
		assertTrue(sfa3.accepts(lOfS("b"), solver));
		assertTrue(sfa3.accepts(lOfS("c"), solver));
		assertTrue(sfa3.accepts(lOfS("C"), solver));
		
		String regex4 = "\\a|[b-zA-Z1-9]";
		SFAprovider test4 = new SFAprovider(regex4, solver);
		SFA<CharPred, Character> sfa4 = test4.getSFA();
		assertFalse(sfa4.accepts(lOfS("\\a"), solver));
		assertTrue(sfa4.accepts(lOfS("a"), solver));
	}
	
	@Test
	public void testNotCharacterClass() throws TimeoutException {
		String regex1 = "[^\\d]";
		SFAprovider test1 = new SFAprovider(regex1, solver);
		SFA<CharPred, Character> sfa1 = test1.getSFA();
		assertTrue(sfa1.accepts(lOfS("a"), solver));
		assertFalse(sfa1.accepts(lOfS("1"), solver));
		
		String regex2 = "[^\\a123bc!@#$s%^&*(){}]";
		SFAprovider test2 = new SFAprovider(regex2, solver);
		SFA<CharPred, Character> sfa2 = test2.getSFA();
		assertTrue(sfa2.accepts(lOfS("["), solver));
		assertFalse(sfa2.accepts(lOfS("1"), solver));
		assertFalse(sfa2.accepts(lOfS("a"), solver));
		assertFalse(sfa2.accepts(lOfS("^"), solver));
		assertFalse(sfa2.accepts(lOfS("s"), solver));
		
		
		String regex3 = "[^a-zA-Z1-9]";
		SFAprovider test3 = new SFAprovider(regex3, solver);
		SFA<CharPred, Character> sfa3 = test3.getSFA();
		assertTrue(sfa3.accepts(lOfS("0"), solver));
		assertFalse(sfa3.accepts(lOfS("8"), solver));
		assertFalse(sfa3.accepts(lOfS("b"), solver));
		assertFalse(sfa3.accepts(lOfS("c"), solver));
		assertFalse(sfa3.accepts(lOfS("C"), solver));
		
		String regex4 = "[^b-zA-Z1-9]";
		SFAprovider test4 = new SFAprovider(regex4, solver);
		SFA<CharPred, Character> sfa4 = test4.getSFA();
		assertTrue(sfa4.accepts(lOfS("a"), solver));
		assertFalse(sfa4.accepts(lOfS("A"), solver));
	}
	
	@Test
	public void testRepetition() throws TimeoutException {
		String regex1 = "[\\d]{2}";
		SFAprovider test1 = new SFAprovider(regex1, solver);
		SFA<CharPred, Character> sfa1 = test1.getSFA();
		assertFalse(sfa1.accepts(lOfS("1"), solver));
		assertTrue(sfa1.accepts(lOfS("11"), solver));
		
		String regex2 = "a{2,}";
		SFAprovider test2 = new SFAprovider(regex2, solver);
		SFA<CharPred, Character> sfa2 = test2.getSFA();
		assertFalse(sfa2.accepts(lOfS("a"), solver));
		assertTrue(sfa2.accepts(lOfS("aa"), solver));
		assertTrue(sfa2.accepts(lOfS("aaa"), solver));
		assertTrue(sfa2.accepts(lOfS("aaaa"), solver));
		assertTrue(sfa2.accepts(lOfS("aaaaaaaaaa"), solver));
		
		
		String regex3 = "1{2,3}";
		SFAprovider test3 = new SFAprovider(regex3, solver);
		SFA<CharPred, Character> sfa3 = test3.getSFA();
		assertFalse(sfa3.accepts(lOfS("1"), solver));
		assertTrue(sfa3.accepts(lOfS("11"), solver));
		assertTrue(sfa3.accepts(lOfS("111"), solver));
		assertFalse(sfa3.accepts(lOfS("1111"), solver));
		assertFalse(sfa3.accepts(lOfS("1111111111"), solver));
		
	}
	
	@Test
	public void testStarAndPlusAndOptional() throws TimeoutException {
		String regex1 = "[\\d]+";
		SFAprovider test1 = new SFAprovider(regex1, solver);
		SFA<CharPred, Character> sfa1 = test1.getSFA();
		assertFalse(sfa1.accepts(lOfS("a"), solver));
		assertTrue(sfa1.accepts(lOfS("1234567890"), solver));
		assertTrue(sfa1.accepts(lOfS("111234567890"), solver));
		assertTrue(sfa1.accepts(lOfS("11111112345678901111111111"), solver));
		
		String regex2 = "a*";
		SFAprovider test2 = new SFAprovider(regex2, solver);
		SFA<CharPred, Character> sfa2 = test2.getSFA();
		assertFalse(sfa2.accepts(lOfS("b"), solver));
		assertTrue(sfa2.accepts(lOfS(""), solver));
		assertTrue(sfa2.accepts(lOfS("a"), solver));
		assertTrue(sfa2.accepts(lOfS("aa"), solver));
		assertTrue(sfa2.accepts(lOfS("aaaaaaaaaa"), solver));
		
		
		String regex3 = "[abc]*";
		SFAprovider test3 = new SFAprovider(regex3, solver);
		SFA<CharPred, Character> sfa3 = test3.getSFA();
		assertFalse(sfa3.accepts(lOfS("d"), solver));
		assertTrue(sfa3.accepts(lOfS(""), solver));
		assertTrue(sfa3.accepts(lOfS("aaaa"), solver));
		assertTrue(sfa3.accepts(lOfS("abcbcbcbccb"), solver));
		assertTrue(sfa3.accepts(lOfS("cccccabababa"), solver));
		
		
		String regex4 = "de?|f[abc]?";
		SFAprovider test4 = new SFAprovider(regex4, solver);
		SFA<CharPred, Character> sfa4 = test4.getSFA();
		assertTrue(sfa4.accepts(lOfS("d"), solver));
		assertTrue(sfa4.accepts(lOfS("de"), solver));
		assertTrue(sfa4.accepts(lOfS("fa"), solver));
		assertTrue(sfa4.accepts(lOfS("f"), solver));
		assertFalse(sfa4.accepts(lOfS("def"), solver));
	}
	
	
	@Test
	public void testAnchor() throws TimeoutException {
		String regex1 = "^ab*";
		SFAprovider test1 = new SFAprovider(regex1, solver);
		SFA<CharPred, Character> sfa1 = test1.getSFA();
		assertFalse(sfa1.accepts(lOfS("b"), solver));
		assertTrue(sfa1.accepts(lOfS("a"), solver));
		assertTrue(sfa1.accepts(lOfS("ab"), solver));
		assertTrue(sfa1.accepts(lOfS("abbbbb"), solver));
		
		String regex2 = "b*a$";
		SFAprovider test2 = new SFAprovider(regex2, solver);
		SFA<CharPred, Character> sfa2 = test2.getSFA();
		assertFalse(sfa2.accepts(lOfS("b"), solver));
		assertFalse(sfa2.accepts(lOfS("c"), solver));
		
		assertFalse(sfa2.accepts(lOfS("ab"), solver)); 
		
		assertTrue(sfa2.accepts(lOfS("a"), solver));
		assertTrue(sfa2.accepts(lOfS("ba"), solver));
		assertTrue(sfa2.accepts(lOfS("bbbbbbba"), solver));
		
		
		String regex3 = "^a1$";
		SFAprovider test3 = new SFAprovider(regex3, solver);
		SFA<CharPred, Character> sfa3 = test3.getSFA();
		assertFalse(sfa3.accepts(lOfS("1"), solver));
		assertFalse(sfa3.accepts(lOfS("v"), solver));
		assertTrue(sfa3.accepts(lOfS("a1"), solver));
		assertFalse(sfa3.accepts(lOfS("aaaa"), solver));
		
		
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

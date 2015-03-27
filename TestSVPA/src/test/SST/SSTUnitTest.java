package test.SST;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import theory.CharFunc;
import theory.CharPred;
import theory.CharSolver;
import transducers.sst.CharConstant;
import transducers.sst.CharFunction;
import transducers.sst.ConstantToken;
import transducers.sst.FunctionalVariableUpdate;
import transducers.sst.SST;
import transducers.sst.SSTEpsilon;
import transducers.sst.SSTInputMove;
import transducers.sst.SSTMove;
import transducers.sst.SimpleVariableUpdate;
import transducers.sst.StringVariable;
import transducers.sst.Token;
import utilities.Pair;
import automata.AutomataException;

public class SSTUnitTest {

	@Test
	public void testMkSST() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTa(ba);

			assertTrue(sstA.stateCount() == 2);
			assertTrue(sstA.transitionCount() == 3);

			assertTrue(sstA.stateCount() == 2);
			assertTrue(sstA.transitionCount() == 3);

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testAccept() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTaNoEps(ba);

			List<Character> goodInput = lOfS("a2c");
			List<Character> badInput = lOfS("#2c");

			assertTrue(sstA.accepts(goodInput, ba));
			assertTrue(!sstA.accepts(badInput, ba));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testOutput() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTaNoEps(ba);

			List<Character> input1 = lOfS("a2c");
			List<Character> input2 = lOfS("acc");

			List<Character> output1 = sstA.outputOn(input1, ba);
			List<Character> output2 = sstA.outputOn(input2, ba);

			assertTrue(ba.stringOfList(output1).equals("ac"));
			assertTrue(ba.stringOfList(output2).equals("acc"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testEpsilonRemoval() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTa(ba);

			SST<CharPred, CharFunc, Character> sstAnoEps = sstA
					.removeEpsilonMoves(ba);

			List<Character> input1 = lOfS("a2c");
			List<Character> input2 = lOfS("acc");

			List<Character> output1 = sstAnoEps.outputOn(input1, ba);
			List<Character> output2 = sstAnoEps.outputOn(input2, ba);

			assertTrue(ba.stringOfList(output1).equals("ac"));
			assertTrue(ba.stringOfList(output2).equals("acc"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testEpsilonAndBaseSST() {

		CharSolver ba = new CharSolver();
		SST<CharPred, CharFunc, Character> sstEps = getEpsToSemicolon(ba);
		SST<CharPred, CharFunc, Character> sstBase = getAlphaToUpperCase(ba);

		List<Character> input1 = lOfS("");
		List<Character> input2 = lOfS("a");
		List<Character> input3 = lOfS("ab");

		assertTrue(sstEps.accepts(input1, ba));
		assertTrue(!sstEps.accepts(input2, ba));
		assertTrue(!sstEps.accepts(input3, ba));

		List<Character> output1 = sstEps.outputOn(input1, ba);

		assertTrue(ba.stringOfList(output1).equals(";"));

		assertTrue(!sstBase.accepts(input1, ba));
		assertTrue(sstBase.accepts(input2, ba));
		assertTrue(!sstBase.accepts(input3, ba));

		List<Character> output2 = sstBase.outputOn(input2, ba);

		assertTrue(ba.stringOfList(output2).equals("A"));
	}

	@Test
	public void testShuffle() {
		CharSolver ba = new CharSolver();
		SST<CharPred, CharFunc, Character> sstBase = getAlphaToUpperCase(ba);

		Collection<Pair<SST<CharPred, CharFunc, Character>, SST<CharPred, CharFunc, Character>>> combinedSstPairsWitEps = new ArrayList<Pair<SST<CharPred, CharFunc, Character>, SST<CharPred, CharFunc, Character>>>();
		combinedSstPairsWitEps
				.add(new Pair<SST<CharPred, CharFunc, Character>, SST<CharPred, CharFunc, Character>>(
						sstBase, sstBase));
		SST<CharPred, CharFunc, Character> sstShuffle = SST.computeShuffle(
				combinedSstPairsWitEps, ba, false);		

		List<Character> input1 = lOfS("a");
		List<Character> input2 = lOfS("ab");
		List<Character> input3 = lOfS("abc");
		List<Character> input4 = lOfS("abcd");

		assertTrue(!sstShuffle.accepts(input1, ba));
		assertTrue(sstShuffle.accepts(input2, ba));
		assertTrue(sstShuffle.accepts(input3, ba));
		assertTrue(sstShuffle.accepts(input4, ba));

		List<Character> output2= sstShuffle.outputOn(input2, ba);
		List<Character> output3= sstShuffle.outputOn(input3, ba);
		List<Character> output4= sstShuffle.outputOn(input4, ba);

		assertTrue(ba.stringOfList(output2).equals("AB"));
		assertTrue(ba.stringOfList(output3).equals("ABBC"));
		assertTrue(ba.stringOfList(output4).equals("ABBCCD"));

	}

	@Test
	public void testCombine() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sst1 = delNumKeepLettOnlyEndLett(ba);
			SST<CharPred, CharFunc, Character> sst2 = getSSTd(ba);
			SST<CharPred, CharFunc, Character> combined = sst1.combineWith(
					sst2, ba);

			List<Character> input1 = lOfS("a2c");
			List<Character> input2 = lOfS("a22");
			List<Character> input3 = lOfS("#");

			assertTrue(combined.accepts(input1, ba));
			assertTrue(!combined.accepts(input2, ba));
			assertTrue(!combined.accepts(input3, ba));

			List<Character> output1 = combined.outputOn(input1, ba);
			assertTrue(ba.stringOfList(output1).equals("acac"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testConcatenation() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sst1 = getLetterCopy(ba);
			SST<CharPred, CharFunc, Character> sst2 = getNumberCopy(ba);
			SST<CharPred, CharFunc, Character> combined = sst1.concatenateWith(
					sst2, ba);

			List<Character> input1 = lOfS("a2");
			List<Character> input2 = lOfS("a22");
			List<Character> input3 = lOfS("aa");
			List<Character> input4 = lOfS("2a");

			assertTrue(combined.accepts(input1, ba));
			assertTrue(combined.accepts(input2, ba));
			assertTrue(combined.accepts(input3, ba));
			assertTrue(!combined.accepts(input4, ba));

			List<Character> output1 = combined.outputOn(input1, ba);
			assertTrue(ba.stringOfList(output1).equals("a2"));
			List<Character> output2 = combined.outputOn(input2, ba);
			assertTrue(ba.stringOfList(output2).equals("a22"));
			List<Character> output3 = combined.outputOn(input3, ba);
			assertTrue(ba.stringOfList(output3).equals("aa"));

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}

	@Test
	public void testUnion() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sst1 = delNumKeepLettOnlyEndLett(ba);
			SST<CharPred, CharFunc, Character> sst2 = getNumberCopy(ba);
			SST<CharPred, CharFunc, Character> union = sst1.unionWith(sst2, ba);

			List<Character> input1 = lOfS("a2");
			List<Character> input2 = lOfS("2a");
			List<Character> input3 = lOfS("aa");
			List<Character> input4 = lOfS("22");

			assertTrue(!union.accepts(input1, ba));
			assertTrue(union.accepts(input2, ba));
			assertTrue(union.accepts(input3, ba));
			assertTrue(union.accepts(input4, ba));

			List<Character> output2 = union.outputOn(input2, ba);
			assertTrue(ba.stringOfList(output2).equals("a"));
			List<Character> output3 = union.outputOn(input3, ba);
			assertTrue(ba.stringOfList(output3).equals("aa"));
			List<Character> output4 = union.outputOn(input4, ba);
			assertTrue(ba.stringOfList(output4).equals("22"));

		} catch (AutomataException e) {
			System.out.print(e);
		}
	}

	@Test
	public void testStar() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sst1 = getCommaSepDelNumKeepAlph(ba);
			SST<CharPred, CharFunc, Character> star = sst1.star(ba);
			SST<CharPred, CharFunc, Character> leftStar = sst1.leftStar(ba);

			List<Character> input1 = lOfS("a2,bb,");
			List<Character> input2 = lOfS("a22,b");

			assertTrue(star.accepts(input1, ba));
			assertTrue(!star.accepts(input2, ba));
			assertTrue(leftStar.accepts(input1, ba));
			assertTrue(!leftStar.accepts(input2, ba));

			List<Character> output1r = star.outputOn(input1, ba);
			assertTrue(ba.stringOfList(output1r).equals("a,bb,"));

			List<Character> output1l = leftStar.outputOn(input1, ba);
			assertTrue(ba.stringOfList(output1l).equals("bb,a,"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	// ---------------------------------------
	// Predicates
	// ---------------------------------------
	CharPred alpha = new CharPred('a', 'z');
	CharPred num = new CharPred('1', '9');
	CharPred comma = new CharPred(',');
	List<String> onlyX = Arrays.asList("x");

	// ---------------------------------------
	// SSTs
	// ---------------------------------------

	// SST with one epsilon transition and two states, deletes all numbers and
	// keeps all letters
	// S: 0 -[a-z]/x{c+0};-> 0
	// E: 0 --> 1
	// S: 0 -[1-9]/x;-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getSSTa(CharSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTEpsilon<CharPred, CharFunc, Character>(0, 1,
				justXsimple()));

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one epsilon transition and two states, deletes all numbers and
	// keeps all letters
	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getSSTaNoEps(CharSolver ba)
			throws AutomataException {
		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with two states, deletes all numbers and
	// keeps all letters. Only defined if string ends with letter
	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// S: 0 -[a-z]/x{c+0};-> 1
	// Initial States: 0
	// Output Function: F(1)=x;
	private SST<CharPred, CharFunc, Character> delNumKeepLettOnlyEndLett(
			CharSolver ba) throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 1,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(1, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one state, deletes all numbers and
	// keeps all letters. Always defined
	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getSSTd(CharSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one state, keeps all letters. Defined only on letters
	// S: 0 -[a-z]/x{c+0};-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getLetterCopy(CharSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one state, keeps all numbers. Defined only on numbers
	// S: 0 -[1-9]/x{c+0};-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getNumberCopy(CharSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQxid()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// S: 0 -[,]/x{c+0};-> 0
	// Initial States: 0
	// Output Function: F(1)=x;
	private SST<CharPred, CharFunc, Character> getCommaSepDelNumKeepAlph(
			CharSolver ba) throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 1,
				comma, xEQxid()));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(1, justXsimple());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// S: F(0) = a
	private SST<CharPred, CharFunc, Character> getEpsToSemicolon(CharSolver ba) {

		List<ConstantToken<CharPred, CharFunc, Character>> output = new ArrayList<ConstantToken<CharPred, CharFunc, Character>>();
		output.add(new CharConstant<CharPred, CharFunc, Character>(';'));
		return SST.getEpsilonSST(output, ba);
	}

	// S: F(0) = a
	private SST<CharPred, CharFunc, Character> getAlphaToUpperCase(CharSolver ba) {

		List<Token<CharPred, CharFunc, Character>> output = new ArrayList<Token<CharPred, CharFunc, Character>>();
		output.add(new CharFunction<CharPred, CharFunc, Character>(CharFunc
				.ToUpperCase()));
		return SST.getBaseSST(alpha, output, ba);
	}

	// -------------------------
	// Variable Assignments
	// -------------------------

	private FunctionalVariableUpdate<CharPred, CharFunc, Character> xEQx() {
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");
		LinkedList<Token<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new FunctionalVariableUpdate<CharPred, CharFunc, Character>(
				justX);
	}

	private SimpleVariableUpdate<CharPred, CharFunc, Character> justXsimple() {
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new SimpleVariableUpdate<CharPred, CharFunc, Character>(justX);
	}

	private FunctionalVariableUpdate<CharPred, CharFunc, Character> xEQxid() {
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(CharFunc.ID()));
		return new FunctionalVariableUpdate<>(xa);
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

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
import org.sat4j.specs.TimeoutException;

import automata.AutomataException;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.characters.CharFunc;
import theory.characters.CharOffset;
import theory.characters.CharPred;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import transducers.sst.CharConstant;
import transducers.sst.CharFunction;
import transducers.sst.ConstantToken;
import transducers.sst.FunctionalVariableUpdate;
import transducers.sst.OutputUpdate;
import transducers.sst.SST;
import transducers.sst.SSTEpsilon;
import transducers.sst.SSTInputMove;
import transducers.sst.SSTMove;
import transducers.sst.SSTVariable;
import transducers.sst.SimpleVariableUpdate;
import transducers.sst.Token;
import utilities.Pair;

public class SSTUnitTest {

	@Test
	public void testMkSST() {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testAccept() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testOutput() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testEpsilonRemoval() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testEpsilonAndBaseSST() throws TimeoutException {

		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
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
	public void testShuffle() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
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

		List<Character> output2 = sstShuffle.outputOn(input2, ba);
		List<Character> output3 = sstShuffle.outputOn(input3, ba);
		List<Character> output4 = sstShuffle.outputOn(input4, ba);

		assertTrue(ba.stringOfList(output2).equals("AB"));
		assertTrue(ba.stringOfList(output3).equals("ABBC"));
		assertTrue(ba.stringOfList(output4).equals("ABBCCD"));

	}

	@Test
	public void testShuffleWithAut() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		SST<CharPred, CharFunc, Character> sstBase = getAlphaToUpperCase(ba);
		SST<CharPred, CharFunc, Character> sstsst = sstBase.concatenateWith(
				sstBase, ba);
		SFA<CharPred, Character> domain = sstBase.getDomain(ba);

		SST<CharPred, CharFunc, Character> sstShuffle = SST.computeShuffle(
				sstsst, domain, ba, false).normalize(ba);

		// SST<CharPred, CharFunc, Character> deb =
		// sstShuffle.removeEpsilonMoves(ba);

		List<Character> input1 = lOfS("a");
		List<Character> input2 = lOfS("ab");
		List<Character> input3 = lOfS("abc");
		List<Character> input4 = lOfS("abcd");

		assertTrue(!sstShuffle.accepts(input1, ba));
		assertTrue(sstShuffle.accepts(input2, ba));
		assertTrue(sstShuffle.accepts(input3, ba));
		assertTrue(sstShuffle.accepts(input4, ba));

		List<Character> output2 = sstShuffle.outputOn(input2, ba);
		List<Character> output3 = sstShuffle.outputOn(input3, ba);
		List<Character> output4 = sstShuffle.outputOn(input4, ba);

		assertTrue(ba.stringOfList(output2).equals("AB"));
		assertTrue(ba.stringOfList(output3).equals("ABBC"));
		assertTrue(ba.stringOfList(output4).equals("ABBCCD"));

	}

	@Test
	public void testPreImage() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		SST<CharPred, CharFunc, Character> sstBase = getLetterCopy(ba);

		SFA<CharPred, Character> atLeast2As = atLeastTwoAs(ba);

		SFA<CharPred, Character> tcdom = sstBase.getPreImage(atLeast2As, ba);
		assertTrue(tcdom.isEquivalentTo(atLeast2As, ba));
		
		assertTrue(sstBase.typeCheck(atLeast2As, atLeast2As, ba));
	}
	
	@Test
	public void testRestrict() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		SST<CharPred, CharFunc, Character> sstBase = getLetterCopy(ba);

		SFA<CharPred, Character> atLeast2As = atLeastTwoAs(ba);

		SST<CharPred, CharFunc, Character> rest = sstBase.restrictInput(atLeast2As, ba);
		assertTrue(rest.getDomain(ba).isEquivalentTo(atLeast2As, ba));
	}

	@Test
	public void testCombine() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testConcatenation() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testUnion() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	public void testStar() throws TimeoutException {

		try {
			UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

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
	CharPred a = new CharPred('a');
	CharPred num = new CharPred('1', '9');
	CharPred comma = new CharPred(',');
	int onlyX = 1;

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
	private SST<CharPred, CharFunc, Character> getSSTa(UnaryCharIntervalSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTEpsilon<CharPred, CharFunc, Character>(0, 1,
				justXsimple()));

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one epsilon transition and two states, deletes all numbers and
	// keeps all letters
	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getSSTaNoEps(UnaryCharIntervalSolver ba)
			throws AutomataException {
		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

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
			UnaryCharIntervalSolver ba) throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 1,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(1, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one state, deletes all numbers and
	// keeps all letters. Always defined
	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getSSTd(UnaryCharIntervalSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SST with one state, keeps all letters. Defined only on letters
	// S: 0 -[a-z]/x{c+0};-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getLetterCopy(UnaryCharIntervalSolver ba) {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SFA that accepts strings contatining at least two as
	private SFA<CharPred, Character> atLeastTwoAs(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new ArrayList<SFAMove<CharPred, Character>>();

		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(1, 1, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(2, 2, alpha));
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, a));
		transitionsA.add(new SFAInputMove<CharPred, Character>(1, 2, a));

		// Output function just outputs x

		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(2), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// SST with one state, keeps all numbers. Defined only on numbers
	// S: 0 -[1-9]/x{c+0};-> 0
	// Initial States: 0
	// Output Function: F(0)=x;
	private SST<CharPred, CharFunc, Character> getNumberCopy(UnaryCharIntervalSolver ba)
			throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQxid()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// S: 0 -[a-z]/x{c+0};-> 0
	// S: 0 -[1-9]/x;-> 0
	// S: 0 -[,]/x{c+0};-> 0
	// Initial States: 0
	// Output Function: F(1)=x;
	private SST<CharPred, CharFunc, Character> getCommaSepDelNumKeepAlph(
			UnaryCharIntervalSolver ba) throws AutomataException {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xEQxid()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, xEQx()));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 1,
				comma, xEQxid()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(1, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// S: F(0) = a
	private SST<CharPred, CharFunc, Character> getEpsToSemicolon(UnaryCharIntervalSolver ba) {

		List<ConstantToken<CharPred, CharFunc, Character>> output = new ArrayList<ConstantToken<CharPred, CharFunc, Character>>();
		output.add(new CharConstant<CharPred, CharFunc, Character>(';'));
		return SST.getEpsilonSST(output, ba);
	}

	// S: F(0) = a
	private SST<CharPred, CharFunc, Character> getAlphaToUpperCase(UnaryCharIntervalSolver ba) {

		List<Token<CharPred, CharFunc, Character>> output = new ArrayList<Token<CharPred, CharFunc, Character>>();
		output.add(new CharFunction<CharPred, CharFunc, Character>(CharOffset.TO_UPPER_CASE));
		return SST.getBaseSST(alpha, output, ba);
	}

	// -------------------------
	// Variable Assignments
	// -------------------------

	private FunctionalVariableUpdate<CharPred, CharFunc, Character> xEQx() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<Token<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new FunctionalVariableUpdate<CharPred, CharFunc, Character>(
				justX);
	}

	private OutputUpdate<CharPred, CharFunc, Character> justXout() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new OutputUpdate<CharPred, CharFunc, Character>(justX);
	}

	private SimpleVariableUpdate<CharPred, CharFunc, Character> justXsimple() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new SimpleVariableUpdate<CharPred, CharFunc, Character>(justX);
	}

	private FunctionalVariableUpdate<CharPred, CharFunc, Character> xEQxid() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(CharOffset.IDENTITY));
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
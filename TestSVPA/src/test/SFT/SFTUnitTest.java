package test.SFT;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import theory.characters.*;
import transducers.sft.SFT;
import transducers.sft.SFTMove;
import transducers.sft.SFTInputMove;
import transducers.sft.SFTEpsilon;
import automata.sfa.SFA;
import automata.sfa.SFAEpsilon;
import automata.sfa.SFAMove;
import automata.sfa.SFAInputMove;

import theory.intervals.UnaryCharIntervalSolver;

/**
* SFT Tester.
*
* @version 1.0
*/
public class SFTUnitTest {

	private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

	private static SFT<CharPred, CharFunc, Character> mySFT111;
	private static SFT<CharPred, CharFunc, Character> mySFT121;
	private static SFT<CharPred, CharFunc, Character> mySFT122;
	private static SFT<CharPred, CharFunc, Character> mySFT123;
	private static SFT<CharPred, CharFunc, Character> mySFT131;
	private static SFT<CharPred, CharFunc, Character> mySFT211;
	private static SFT<CharPred, CharFunc, Character> mySFT221;
	private static SFT<CharPred, CharFunc, Character> mySFT222;
	private static SFT<CharPred, CharFunc, Character> mySFT223;
	private static SFT<CharPred, CharFunc, Character> mySFT231;
	private static SFT<CharPred, CharFunc, Character> mySFT232;
	private static SFT<CharPred, CharFunc, Character> mySFT241;
	private static SFT<CharPred, CharFunc, Character> mySFT242;
	private static SFT<CharPred, CharFunc, Character> mySFT251;
	private static SFT<CharPred, CharFunc, Character> mySFT252;
	private static SFT<CharPred, CharFunc, Character> mySFT261;
	private static SFT<CharPred, CharFunc, Character> mySFT311;
	private static SFT<CharPred, CharFunc, Character> mySFT321;
	private static SFT<CharPred, CharFunc, Character> mySFT331;
	private static SFT<CharPred, CharFunc, Character> mySFT411;
	private static SFT<CharPred, CharFunc, Character> mySFT421;
	private static List<SFT<CharPred, CharFunc, Character>> allSFTs = new ArrayList<SFT<CharPred, CharFunc, Character>>();



	@BeforeClass
	public static void beforeClass() throws Exception {
		// firstly, construct all SFTs

		// 1. one state with one final state
		// (since there is only one state, the state must be the final state)
		// 1.1 no transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions111 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		Map<Integer, Set<List<Character>>> finStatesAndTails111 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails111.put(1, new HashSet<List<Character>>());
		mySFT111 = SFT.MkSFT(transitions111, 1, finStatesAndTails111, ba);

		// 1.2 one input transition
		// 1.2.1 the output function is only a CharConstant
		List<SFTMove<CharPred, CharFunc, Character>> transitions121 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1211 = new ArrayList<CharFunc>();
		output1211.add(new CharConstant('b'));
		transitions121.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('a'), output1211));
		Map<Integer, Set<List<Character>>> finStatesAndTails121 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails121.put(1, new HashSet<List<Character>>());
		mySFT121 = SFT.MkSFT(transitions121, 1, finStatesAndTails121, ba);

		// 1.2.2 the output function is only a CharOffset
		List<SFTMove<CharPred, CharFunc, Character>> transitions122 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1221 = new ArrayList<CharFunc>();
		output1221.add(new CharOffset(1));
		transitions122.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1221));
		Map<Integer, Set<List<Character>>> finStatesAndTails122 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails122.put(1, new HashSet<List<Character>>());
		mySFT122 = SFT.MkSFT(transitions122, 1, finStatesAndTails122, ba);

		// 1.2.3 there are many output functions in the transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions123 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1231 = new ArrayList<CharFunc>();
		output1231.add(CharOffset.IDENTITY);
		output1231.add(new CharConstant('b'));
		transitions123.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('a'), output1231));
		Map<Integer, Set<List<Character>>> finStatesAndTails123 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails123.put(1, new HashSet<List<Character>>());
		mySFT123 = SFT.MkSFT(transitions123, 1, finStatesAndTails123, ba);

		// epsilon transition is not allowed to point from one state to itself

		// 1.3 many input transitions
		List<SFTMove<CharPred, CharFunc, Character>> transitions131 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1311 = new ArrayList<CharFunc>();
		output1311.add(CharOffset.IDENTITY);
		transitions131.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('a'), output1311));
		List<CharFunc> output1312 = new ArrayList<CharFunc>();
		output1312.add(new CharOffset(1));
		output1312.add(new CharConstant('d'));
		transitions131.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b'), output1312));
		Map<Integer, Set<List<Character>>> finStatesAndTails131 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails131.put(1, new HashSet<List<Character>>());
		mySFT131 = SFT.MkSFT(transitions131, 1, finStatesAndTails131, ba);

		// The situation where one state has many epsilon transitions is not allowed.

		// 2. two states with one final state
		// 2.1 no transition (the final state is unreachable)
		List<SFTMove<CharPred, CharFunc, Character>> transitions211 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		Map<Integer, Set<List<Character>>> finStatesAndTails211 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails211.put(2, new HashSet<List<Character>>());
		mySFT211 = SFT.MkSFT(transitions211, 1, finStatesAndTails211, ba);

		// 2.2 one input transition
		// 2.2.1 the output function is only a CharConstant
		List<SFTMove<CharPred, CharFunc, Character>> transitions221 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2211 = new ArrayList<CharFunc>();
		output2211.add(new CharConstant('b'));
		transitions221.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output2211));
		Map<Integer, Set<List<Character>>> finStatesAndTails221 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails221.put(2, new HashSet<List<Character>>());
		mySFT221 = SFT.MkSFT(transitions221, 1, finStatesAndTails221, ba);

		// 2.2.2 the output function is only a CharOffset
		List<SFTMove<CharPred, CharFunc, Character>> transitions222 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2221 = new ArrayList<CharFunc>();
		output2221.add(new CharOffset(1));
		// 1 -- [bc]/+1 --> 2
		transitions222.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'c'), output2221));
		Map<Integer, Set<List<Character>>> finStatesAndTails222 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails222.put(2, new HashSet<List<Character>>());
		mySFT222 = SFT.MkSFT(transitions222, 1, finStatesAndTails222, ba);

		// 2.2.3 there are multiple output functions in the transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions223 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2231 = new ArrayList<CharFunc>();
		output2231.add(CharOffset.IDENTITY);
		output2231.add(new CharConstant('b'));
		transitions223.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output2231));
		Map<Integer, Set<List<Character>>> finStatesAndTails223 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails223.put(2, new HashSet<List<Character>>());
		mySFT223 = SFT.MkSFT(transitions223, 1, finStatesAndTails223, ba);

		// 2.3 one epsilon transition
		// 2.3.1 only one output in the transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions231 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<Character> output2331 = new ArrayList<Character>();
		output2331.add('a');
		transitions231.add(new SFTEpsilon<CharPred, CharFunc, Character>(1, 2, output2331));
		Map<Integer, Set<List<Character>>> finStatesAndTails231 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails231.put(2, new HashSet<List<Character>>());
		mySFT231 = SFT.MkSFT(transitions231, 1, finStatesAndTails231, ba);

		// 2.3.2 many outputs in the transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions232 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<Character> output2321 = new ArrayList<Character>();
		output2321.add('a');
		output2321.add('b');
		output2321.add('b');
		transitions232.add(new SFTEpsilon<CharPred, CharFunc, Character>(1, 2, output2321));
		Map<Integer, Set<List<Character>>> finStatesAndTails232 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails232.put(2, new HashSet<List<Character>>());
		mySFT232 = SFT.MkSFT(transitions232, 1, finStatesAndTails232, ba);

		// 2.4 many input transitions
		// 2.4.1 the transitions are in the same direction
		List<SFTMove<CharPred, CharFunc, Character>> transitions241 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2411 = new ArrayList<CharFunc>();
		output2411.add(new CharConstant('b'));
		transitions241.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output2411));
		List<CharFunc> output2412 = new ArrayList<CharFunc>();
		output2412.add(CharOffset.IDENTITY);
		transitions241.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'z'), output2412));
		Map<Integer, Set<List<Character>>> finStatesAndTails241 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails241.put(2, new HashSet<List<Character>>());
		mySFT241 = SFT.MkSFT(transitions241, 1, finStatesAndTails241, ba);

		// 2.4.2 the transitions are in opposite directions
		List<SFTMove<CharPred, CharFunc, Character>> transitions242 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2421 = new ArrayList<CharFunc>();
		output2421.add(CharOffset.IDENTITY);
		transitions242.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output2421));
		List<CharFunc> output2422 = new ArrayList<CharFunc>();
		output2422.add(new CharConstant('c'));
		transitions242.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('b'), output2422));
		Map<Integer, Set<List<Character>>> finStatesAndTails242 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails242.put(2, new HashSet<List<Character>>());
		mySFT242 = SFT.MkSFT(transitions242, 1, finStatesAndTails242, ba);

		// The situation where one state has many epsilon transitions is not allowed.

		// 2.5 one input transitions and one epsilon transition
		// 2.5.1 the transitions are in the same direction
		List<SFTMove<CharPred, CharFunc, Character>> transitions251 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2511 = new ArrayList<CharFunc>();
		output2511.add(new CharConstant('b'));
		transitions251.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output2511));
		List<Character> output2512 = new ArrayList<Character>();
		output2512.add('a');
		transitions251.add(new SFTEpsilon<CharPred, CharFunc, Character>(1, 2, output2512));
		Map<Integer, Set<List<Character>>> finStatesAndTails251 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails251.put(2, new HashSet<List<Character>>());
		mySFT251 = SFT.MkSFT(transitions251, 1, finStatesAndTails251, ba);

		// 2.5.2 the transitions are in opposite directions
		List<SFTMove<CharPred, CharFunc, Character>> transitions252 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<Character> output2521 = new ArrayList<Character>();
		output2521.add('a');
		transitions252.add(new SFTEpsilon<CharPred, CharFunc, Character>(1, 2, output2521));
		List<CharFunc> output2522 = new ArrayList<CharFunc>();
		output2522.add(new CharConstant('b'));
		transitions252.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('a'), output2522));
		Map<Integer, Set<List<Character>>> finStatesAndTails252 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails252.put(2, new HashSet<List<Character>>());
		mySFT252 = SFT.MkSFT(transitions252, 1, finStatesAndTails252, ba);

		// 2.6 many input transitions and one epsilon transition
		// 2.6.1 the transitions are in the same direction
		List<SFTMove<CharPred, CharFunc, Character>> transitions261 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<Character> output2611 = new ArrayList<Character>();
		output2611.add('a');
		transitions261.add(new SFTEpsilon<CharPred, CharFunc, Character>(1, 2, output2611));
		List<CharFunc> output2622 = new ArrayList<CharFunc>();
		output2622.add(new CharConstant('d'));
		transitions261.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('b'), output2622));
		List<CharFunc> output2623 = new ArrayList<CharFunc>();
		output2623.add(CharOffset.IDENTITY);
		transitions261.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('c'), output2623));
		Map<Integer, Set<List<Character>>> finStatesAndTails261 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails261.put(2, new HashSet<List<Character>>());
		mySFT261 = SFT.MkSFT(transitions261, 1, finStatesAndTails261, ba);

		// the case of two states with two final states is less general than case 4.2 which will be tested later
		// So it is omitted

		// 3. many states with one final state
		// 3.1 n states with n - 1 transitions
		List<SFTMove<CharPred, CharFunc, Character>> transitions311 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output3111 = new ArrayList<CharFunc>();
		output3111.add(new CharOffset(1));
		transitions311.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output3111));
		List<Character> output3112 = new ArrayList<Character>();
		output3112.add('b');
		transitions311.add(new SFTEpsilon<CharPred, CharFunc, Character>(2, 3, output3112));
		Map<Integer, Set<List<Character>>> finStatesAndTails311 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails311.put(3, new HashSet<List<Character>>());
		mySFT311 = SFT.MkSFT(transitions311, 1, finStatesAndTails311, ba);

		// 3.2 n states with more than n - 1 transitions
		List<SFTMove<CharPred, CharFunc, Character>> transitions321 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<Character> output3211 = new ArrayList<Character>();
		output3211.add('a');
		transitions321.add(new SFTEpsilon<CharPred, CharFunc, Character>(1, 2, output3211));
		List<CharFunc> output3212 = new ArrayList<CharFunc>();
		output3212.add(CharOffset.IDENTITY);
		transitions321.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 3, new CharPred('b'), output3212));
		List<CharFunc> output3213 = new ArrayList<CharFunc>();
		output3213.add(new CharConstant('d'));
		transitions321.add(new SFTInputMove<CharPred, CharFunc, Character>(3, 2, new CharPred('c'), output3213));
		Map<Integer, Set<List<Character>>> finStatesAndTails321 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails321.put(3, new HashSet<List<Character>>());
		mySFT321 = SFT.MkSFT(transitions321, 1, finStatesAndTails321, ba);

		// 3.3 n states with self-pointing transitions
		List<SFTMove<CharPred, CharFunc, Character>> transitions331 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output3311 = new ArrayList<CharFunc>();
		output3311.add(new CharConstant('b'));
		output3311.add(CharOffset.IDENTITY);
		transitions331.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'z'), output3311));
		List<CharFunc> output3312 = new ArrayList<CharFunc>();
		output3312.add(new CharConstant('c'));
		output3312.add(CharOffset.IDENTITY);
		transitions331.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 3, new CharPred('a', 'y'), output3312));
		List<CharFunc> output3313 = new ArrayList<CharFunc>();
		output3313.add(CharOffset.IDENTITY);
		transitions331.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('a'), output3313));
		List<CharFunc> output3314 = new ArrayList<CharFunc>();
		output3314.add(CharOffset.IDENTITY);
		transitions331.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 2, new CharPred('z'), output3314));
		List<CharFunc> output3315 = new ArrayList<CharFunc>();
		output3315.add(new CharConstant('r'));
		output3315.add(new CharConstant('e'));
		output3315.add(new CharConstant('p'));
		output3315.add(new CharConstant('e'));
		output3315.add(new CharConstant('a'));
		output3315.add(new CharConstant('t'));
		transitions331.add(new SFTInputMove<CharPred, CharFunc, Character>(3, 3, new CharPred('a', 'z'), output3315));
		Map<Integer, Set<List<Character>>> finStatesAndTails331 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails331.put(3, new HashSet<List<Character>>());
		mySFT331 = SFT.MkSFT(transitions331, 1, finStatesAndTails331, ba);

		// 4. many states with one many state
		// 4.1 the initial state is not a final state
		List<SFTMove<CharPred, CharFunc, Character>> transitions411 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output4111 = new ArrayList<CharFunc>();
		output4111.add(new CharConstant('b'));
		output4111.add(CharOffset.IDENTITY);
		transitions411.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'z'), output4111));
		List<CharFunc> output4112 = new ArrayList<CharFunc>();
		output4112.add(new CharConstant('c'));
		output4112.add(CharOffset.IDENTITY);
		transitions411.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 3, new CharPred('a', 'y'), output4112));
		List<CharFunc> output4113 = new ArrayList<CharFunc>();
		output4113.add(CharOffset.IDENTITY);
		transitions411.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('a'), output4113));
		List<CharFunc> output4114 = new ArrayList<CharFunc>();
		output4114.add(CharOffset.IDENTITY);
		transitions411.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 2, new CharPred('z'), output4114));
		List<CharFunc> output4115 = new ArrayList<CharFunc>();
		output4115.add(new CharConstant('r'));
		output4115.add(new CharConstant('e'));
		output4115.add(new CharConstant('p'));
		output4115.add(new CharConstant('e'));
		output4115.add(new CharConstant('a'));
		output4115.add(new CharConstant('t'));
		transitions411.add(new SFTInputMove<CharPred, CharFunc, Character>(3, 3, new CharPred('a', 'z'), output4115));
		Map<Integer, Set<List<Character>>> finStatesAndTails411 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails411.put(2, new HashSet<List<Character>>());
		finStatesAndTails411.put(3, new HashSet<List<Character>>());
		mySFT411 = SFT.MkSFT(transitions411, 1, finStatesAndTails411, ba);

		// 4.2 the initial state is also a final state
		List<SFTMove<CharPred, CharFunc, Character>> transitions421 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output4211 = new ArrayList<CharFunc>();
		output4211.add(new CharConstant('c'));
		transitions421.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output4211));
		List<Character> output4212 = new ArrayList<Character>();
		output4212.add('f');
		output4212.add('i');
		output4212.add('n');
		output4212.add('a');
		output4212.add('l');
		transitions421.add(new SFTEpsilon<CharPred, CharFunc, Character>(2, 3, output4212));
		List<CharFunc> output4213 = new ArrayList<CharFunc>();
		output4213.add(CharOffset.IDENTITY);
		output4213.add(new CharConstant('d'));
		transitions421.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'z'), output4213));
		Map<Integer, Set<List<Character>>> finStatesAndTails421 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails421.put(1, new HashSet<List<Character>>());
		finStatesAndTails421.put(3, new HashSet<List<Character>>());
		mySFT421 = SFT.MkSFT(transitions421, 1, finStatesAndTails421, ba);

		// Secondly, store all SFTs to a list. The list is used in method testCompose
		allSFTs.add(mySFT111);
		allSFTs.add(mySFT121);
		allSFTs.add(mySFT122);
		allSFTs.add(mySFT123);
		allSFTs.add(mySFT131);
		allSFTs.add(mySFT211);
		allSFTs.add(mySFT221);
		allSFTs.add(mySFT222);
		allSFTs.add(mySFT223);
		allSFTs.add(mySFT231);
		allSFTs.add(mySFT232);
		allSFTs.add(mySFT241);
		allSFTs.add(mySFT242);
		allSFTs.add(mySFT251);
		allSFTs.add(mySFT252);
		allSFTs.add(mySFT261);
		allSFTs.add(mySFT311);
		allSFTs.add(mySFT321);
		allSFTs.add(mySFT331);
		allSFTs.add(mySFT411);
		allSFTs.add(mySFT421);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		// there is no code
	}

	/**
	 *
	 * Method: stateCount()
	 *
	 */
	@Test
	public void testStateCount() throws Exception {
		int number;
		number = mySFT111.stateCount();
		assertEquals(number, 1);
		number = mySFT121.stateCount();
		assertEquals(number, 1);
		number = mySFT122.stateCount();
		assertEquals(number, 1);
		number = mySFT123.stateCount();
		assertEquals(number, 1);
		number = mySFT131.stateCount();
		assertEquals(number, 1);
		assertEquals(number, 1);
		number = mySFT211.stateCount();
		assertEquals(number, 2);
		number = mySFT221.stateCount();
		assertEquals(number, 2);
		number = mySFT222.stateCount();
		assertEquals(number, 2);
		number = mySFT223.stateCount();
		assertEquals(number, 2);
		number = mySFT231.stateCount();
		assertEquals(number, 2);
		number = mySFT232.stateCount();
		assertEquals(number, 2);
		number = mySFT241.stateCount();
		assertEquals(number, 2);
		number = mySFT242.stateCount();
		assertEquals(number, 2);
		number = mySFT251.stateCount();
		assertEquals(number, 2);
		number = mySFT252.stateCount();
		assertEquals(number, 2);
		number = mySFT261.stateCount();
		assertEquals(number, 2);
		number = mySFT311.stateCount();
		assertEquals(number, 3);
		number = mySFT321.stateCount();
		assertEquals(number, 3);
		number = mySFT331.stateCount();
		assertEquals(number, 3);
		number = mySFT411.stateCount();
		assertEquals(number, 3);
		number = mySFT421.stateCount();
		assertEquals(number, 3);
	}

	/**
	 *
	 * Method: transitionCount()
	 *
	 */
	@Test
	public void testTransitionCount() throws Exception {
		int number;
		number = mySFT111.transitionCount();
		assertEquals(number, 0);
		number = mySFT121.transitionCount();
		assertEquals(number, 1);
		number = mySFT122.transitionCount();
		assertEquals(number, 1);
		number = mySFT123.transitionCount();
		assertEquals(number, 1);
		number = mySFT131.transitionCount();
		assertEquals(number, 2);
		number = mySFT211.transitionCount();
		assertEquals(number, 0);
		number = mySFT221.transitionCount();
		assertEquals(number, 1);
		number = mySFT222.transitionCount();
		assertEquals(number, 1);
		number = mySFT223.transitionCount();
		assertEquals(number, 1);
		number = mySFT231.transitionCount();
		assertEquals(number, 1);
		number = mySFT232.transitionCount();
		assertEquals(number, 1);
		number = mySFT241.transitionCount();
		assertEquals(number, 2);
		number = mySFT242.transitionCount();
		assertEquals(number, 2);
		number = mySFT251.transitionCount();
		assertEquals(number, 2);
		number = mySFT252.transitionCount();
		assertEquals(number, 2);
		number = mySFT261.transitionCount();
		assertEquals(number, 3);
		number = mySFT311.transitionCount();
		assertEquals(number, 2);
		number = mySFT321.transitionCount();
		assertEquals(number, 3);
		number = mySFT331.transitionCount();
		assertEquals(number, 5);
		number = mySFT411.transitionCount();
		assertEquals(number, 5);
		number = mySFT421.transitionCount();
		assertEquals(number, 3);
	}

	/**
	 *
	 * Method: MkSFT(Collection<SFTMove<P, F, S>> transitions, Integer initialState, Collection<Integer> finalStates, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testMkSFT() throws Exception {
		// test the correctness of determinism of SFTs

		// case 1: test case provided by George Argyros on Jun 4th, 2018
		List <SFTMove <CharPred, CharFunc, Character>> transitions1 = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates1;
		transitions1.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('b'),
				stringToCharFunc("")));
		transitions1.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('a'),
				stringToCharFunc("d")));
		HashSet<Integer> finalStatesSet1 = new HashSet<>();
		finalStatesSet1.add(0);
		finalStates1 = setToFTMap(finalStatesSet1);
		SFT<CharPred, CharFunc, Character> myMkSFT1 = SFT.MkSFT(transitions1, 0, finalStates1, ba);
		assertTrue(myMkSFT1.isDeterministic());

		// case 2: As to SFTs created in method beforeClass, if it has epsilon transitions, it of course is 
		// non-deterministic. Otherwise, it is deterministic because its transitions' guards are not overlapped.
		assertTrue(mySFT111.isDeterministic());
		assertTrue(mySFT121.isDeterministic());
		assertTrue(mySFT122.isDeterministic());
		assertTrue(mySFT123.isDeterministic());
		assertTrue(mySFT131.isDeterministic());
		assertTrue(mySFT211.isDeterministic());
		assertTrue(mySFT221.isDeterministic());
		assertTrue(mySFT222.isDeterministic());
		assertTrue(mySFT223.isDeterministic());
		assertFalse(mySFT231.isDeterministic());
		assertFalse(mySFT232.isDeterministic());
		assertTrue(mySFT241.isDeterministic());
		assertTrue(mySFT242.isDeterministic());
		assertFalse(mySFT251.isDeterministic());
		assertFalse(mySFT252.isDeterministic());
		assertFalse(mySFT261.isDeterministic());
		assertFalse(mySFT311.isDeterministic());
		assertFalse(mySFT321.isDeterministic());
		assertTrue(mySFT331.isDeterministic());
		assertTrue(mySFT411.isDeterministic());
		assertFalse(mySFT421.isDeterministic());

		// case 3: some transitions' guards are overlapped but these transitions output different things
		List <SFTMove <CharPred, CharFunc, Character>> transitions2 = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates2;
		transitions2.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('a', 'd'),
				stringToCharFunc("cat")));
		transitions2.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('b', 'e'),
				stringToCharFunc("dog")));
		HashSet<Integer> finalStatesSet2 = new HashSet<>();
		finalStatesSet2.add(0);
		finalStates2 = setToFTMap(finalStatesSet2);
		SFT<CharPred, CharFunc, Character> myMkSFT2 = SFT.MkSFT(transitions2, 0, finalStates2, ba);
		assertFalse(myMkSFT2.isDeterministic());

		List <SFTMove <CharPred, CharFunc, Character>> transitions3 = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates3;
		transitions3.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 1,
				new CharPred('a', 'd'),
				stringToCharFunc("cat")));
		transitions3.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('b', 'e'),
				stringToCharFunc("dog")));
		HashSet<Integer> finalStatesSet3 = new HashSet<>();
		finalStatesSet3.add(0);
		finalStatesSet3.add(1);
		finalStates3 = setToFTMap(finalStatesSet3);
		SFT<CharPred, CharFunc, Character> myMkSFT3 = SFT.MkSFT(transitions3, 0, finalStates3, ba);
		assertFalse(myMkSFT3.isDeterministic());

		// case 4: some transitions' guards are overlapped but these transitions output the same things
		List <SFTMove <CharPred, CharFunc, Character>> transitions4 = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates4;
		transitions4.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('a', 'd'),
				stringToCharFunc("cat")));
		transitions4.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('b', 'e'),
				stringToCharFunc("cat")));
		HashSet<Integer> finalStatesSet4 = new HashSet<>();
		finalStatesSet4.add(0);
		finalStates4 = setToFTMap(finalStatesSet4);
		SFT<CharPred, CharFunc, Character> myMkSFT4 = SFT.MkSFT(transitions4, 0, finalStates4, ba);
		assertFalse(myMkSFT4.isDeterministic());

		List <SFTMove <CharPred, CharFunc, Character>> transitions5 = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates5;
		transitions5.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 1,
				new CharPred('a', 'd'),
				stringToCharFunc("cat")));
		transitions5.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('b', 'e'),
				stringToCharFunc("cat")));
		HashSet<Integer> finalStatesSet5 = new HashSet<>();
		finalStatesSet5.add(0);
		finalStatesSet5.add(1);
		finalStates5 = setToFTMap(finalStatesSet5);
		SFT<CharPred, CharFunc, Character> myMkSFT5 = SFT.MkSFT(transitions5, 0, finalStates5, ba);
		assertFalse(myMkSFT5.isDeterministic());
	}

	/**
	 *
	 * Method: getEmptySFT(BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testGetEmptySFT() throws Exception {
		SFT<CharPred, CharFunc, Character> empty = SFT.getEmptySFT(ba);
		int stateSize = empty.stateCount();
		assertEquals(stateSize, 1); // there is an initial state
		int transitionSize = empty.transitionCount();
		assertEquals(transitionSize, 0);
		int initial = empty.getInitialState();
		assertEquals(initial, 0);
		// empty sft is just case 1.1, so no more test is needed
	}

	/**
	 *
	 * Method: outputOn(List<S> input, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testOutputOnForInputBa() throws Exception {
		// since this method is just a wrapper of following method also called outputOn, we only need to test one method
	}

	/**
	 *
	 * Method: outputOn(SFT<P, F, S> sftWithEps, List<S> input, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testOutputOnForSftWithEpsInputBa() throws Exception {
		// it is tested in method testCompose
	}

	/**
	 *
	 * Method: composeWith(SFT<P, F, S> sft, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testComposeWith() throws Exception {
		List<List<Character>> inputs = new ArrayList<List<Character>>(); // it is a corpus of all kinds of inputs
		inputs.add(stringToListOfCharacter(""));
		inputs.add(stringToListOfCharacter("a"));
		inputs.add(stringToListOfCharacter("b"));
		inputs.add(stringToListOfCharacter("c"));
		inputs.add(stringToListOfCharacter("d"));
		inputs.add(stringToListOfCharacter("z"));
		inputs.add(stringToListOfCharacter("1"));
		inputs.add(stringToListOfCharacter(" "));
		inputs.add(stringToListOfCharacter("bb"));
		inputs.add(stringToListOfCharacter("ab"));
		inputs.add(stringToListOfCharacter("ac"));
		inputs.add(stringToListOfCharacter("ccc"));
		inputs.add(stringToListOfCharacter("abcz"));
		inputs.add(stringToListOfCharacter("bcsaee"));
		inputs.add(stringToListOfCharacter("12-3"));
		inputs.add(stringToListOfCharacter("1b- *&@3"));

		for (SFT<CharPred, CharFunc, Character> firstSft: allSFTs)
			for (SFT<CharPred, CharFunc, Character> secondSft: allSFTs) {
				SFT<CharPred, CharFunc, Character> composed = firstSft.composeWith(secondSft, ba);
				for (List<Character> input: inputs) {
					List<Character> composedOutputList = composed.outputOn(input, ba);
					String composedOutput;
					if (composedOutputList == null)
						composedOutput = null;
					else
						composedOutput = composedOutputList.toString();
					List<Character> mediumOutputList = firstSft.outputOn(input, ba);
					String finalOutput;
					if (mediumOutputList == null)
						finalOutput = null;
					else {
						List<Character> finalOutputList = secondSft.outputOn(mediumOutputList, ba);
						if (finalOutputList == null)
							finalOutput = null;
						else
							finalOutput = finalOutputList.toString();
					}
					assertEquals(composedOutput, finalOutput);
				}
			}

		// the output functions of the first SFT is empty
		List<SFTMove<CharPred, CharFunc, Character>> transitions1 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1 = new ArrayList<CharFunc>();
		transitions1.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 1, new CharPred('b', 'c'), output1));
		Map<Integer, Set<List<Character>>> finStatesAndTails1 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails1.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> mySFT1 = SFT.MkSFT(transitions1, 0, finStatesAndTails1, ba);

		List<SFTMove<CharPred, CharFunc, Character>> transitions2 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output21 = new ArrayList<CharFunc>();
		output21.add(new CharConstant('a'));
		transitions2.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 1, new CharPred('d'), output21));
		List<CharFunc> output22 = new ArrayList<CharFunc>();
		output22.add(new CharOffset(1));
		output22.add(new CharConstant('e'));
		transitions2.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('c'), output22));
		Map<Integer, Set<List<Character>>> finStatesAndTails2 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails2.put(2, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> mySFT2 = SFT.MkSFT(transitions2, 0, finStatesAndTails2, ba);

		SFT<CharPred, CharFunc, Character> composed = mySFT1.composeWith(mySFT2, ba);
		assertEquals(2, composed.getStates().size());
		assertEquals(1, composed.getTransitions().size());
		for (SFTInputMove<CharPred, CharFunc, Character> transition: composed.getInputMovesFrom(composed.getStates()))
			assertEquals(0, transition.outputFunctions.size());

	}

	/**
	 *
	 * Method: compose(SFT<P, F, S> sft1withEps, SFT<P, F, S> sft2withEps, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testCompose() throws Exception {
		// since method compose is just a wrapper of method composeWith, we could test method compose to test method
		// composeWith incidentally


	}

	/**
	 * convert a string into a list of characters
	 * @param input a string
	 * @return a list of class Character
	 */
	private static List<Character> stringToListOfCharacter(String input) {
		List<Character> output = new ArrayList<Character>();
		for (char character: input.toCharArray())
			output.add(character);
		return output;
	}

	/**
	 *
	 * Method: decide1equality(SFT<P, F, S> otherSft, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testDecide1equalityForOtherSftBa() throws Exception {
		// part 1
		for (int i = 0; i < allSFTs.size(); i++) {
			for (int j = 0; j < allSFTs.size(); j++) {
				if (i == j) {
					assertTrue(allSFTs.get(i).decide1equality(allSFTs.get(j), ba));
				}
			}
		}

		// part 2
		/* According to page 2, deciding whether SFTs A abd B are equivalent can be reduced to two independent tasks:
		1. domain equivalence: Domain(A) = Domain(B)
		2. partial equivalence: for all a belongs to MkAnd(Domain(A), Domain(B)), T_A(a) = T_B(a)
		decide1equality only checks the partial equivalence, so if A's abd B's domains are not equal, decide1equality
		does not work properly.
		Unfortunately, SFTs constructed in beforeClass have various domains so that I have to construct some new SFTs
		to test decide1equality. */

		// Here I assume all SFTs' domains are CharPred('b', 'c')
		List<SFT<CharPred, CharFunc, Character>> SFTlibrary = new ArrayList<SFT<CharPred, CharFunc, Character>>();
		// 1. one state with one final state
		// 1.1 there is only one input transition and the output function is only a CharConstant
		List<SFTMove<CharPred, CharFunc, Character>> transitions121 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1211 = new ArrayList<CharFunc>();
		output1211.add(new CharConstant('b'));
		transitions121.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1211));
		Map<Integer, Set<List<Character>>> finStatesAndTails121 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails121.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT11 = SFT.MkSFT(transitions121, 1, finStatesAndTails121, ba);
		SFTlibrary.add(SFT11);

		// 1.2 there is only one input transition and the output function is only a CharOffset
		List<SFTMove<CharPred, CharFunc, Character>> transitions122 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1221 = new ArrayList<CharFunc>();
		output1221.add(new CharOffset(1));
		transitions122.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1221));
		Map<Integer, Set<List<Character>>> finStatesAndTails122 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails122.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT12 = SFT.MkSFT(transitions122, 1, finStatesAndTails122, ba);
		SFTlibrary.add(SFT12);

		// 1.3 there is only one input transition and there are many output functions in the transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions123 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1231 = new ArrayList<CharFunc>();
		output1231.add(CharOffset.IDENTITY);
		output1231.add(new CharConstant('b'));
		transitions123.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1231));
		Map<Integer, Set<List<Character>>> finStatesAndTails123 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails123.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT13 = SFT.MkSFT(transitions123, 1, finStatesAndTails123, ba);
		SFTlibrary.add(SFT13);

		// 1.4 many input transitions
		List<SFTMove<CharPred, CharFunc, Character>> transitions131 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1311 = new ArrayList<CharFunc>();
		output1311.add(CharOffset.IDENTITY);
		transitions131.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b'), output1311));
		List<CharFunc> output1312 = new ArrayList<CharFunc>();
		output1312.add(new CharOffset(1));
		output1312.add(new CharConstant('d'));
		transitions131.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('c'), output1312));
		Map<Integer, Set<List<Character>>> finStatesAndTails131 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails131.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT14 = SFT.MkSFT(transitions131, 1, finStatesAndTails131, ba);
		SFTlibrary.add(SFT14);

		// 2. two states with one final state
		// 2.1 the transitions are in opposite directions
		List<SFTMove<CharPred, CharFunc, Character>> transitions242 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2421 = new ArrayList<CharFunc>();
		output2421.add(CharOffset.IDENTITY);
		transitions242.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'c'), output2421));
		List<CharFunc> output2422 = new ArrayList<CharFunc>();
		output2422.add(new CharConstant('c'));
		transitions242.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('b', 'c'), output2422));
		Map<Integer, Set<List<Character>>> finStatesAndTails242 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails242.put(2, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT21 = SFT.MkSFT(transitions242, 1, finStatesAndTails242, ba);
		SFTlibrary.add(SFT21);

		for (int i = 0; i < SFTlibrary.size(); i++) {
			for (int j = 0; j < SFTlibrary.size(); j++) {
				if (i == j)
					assertTrue(SFTlibrary.get(i).decide1equality(SFTlibrary.get(j), ba));
				else
					assertFalse(SFTlibrary.get(i).decide1equality(SFTlibrary.get(j), ba));
			}
		}

		// part 3
		// two stats, one final state, only one transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions1 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output11 = new ArrayList<CharFunc>();
		output11.add(CharOffset.IDENTITY);
		transitions1.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'c'), output11));
		Map<Integer, Set<List<Character>>> finStatesAndTails1 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails1.put(2, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT1 = SFT.MkSFT(transitions1, 1, finStatesAndTails1, ba);

		// three stats, one final state, one input transition and one epsilon transition
		// Since the epsilon transition's outputs is empty, SFT1 should be equivalent with SFT2
		List<SFTMove<CharPred, CharFunc, Character>> transitions2 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output21 = new ArrayList<CharFunc>();
		output21.add(CharOffset.IDENTITY);
		transitions2.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'c'), output21));
		List<Character> output22 = new ArrayList<Character>();
		transitions2.add(new SFTEpsilon<CharPred, CharFunc, Character>(2, 3, output22));
		Map<Integer, Set<List<Character>>> finStatesAndTails2 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails1.put(3, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT2 = SFT.MkSFT(transitions2, 1, finStatesAndTails2, ba);

		assertTrue(SFT1.decide1equality(SFT2, ba));

		// part 4 decide1equality should recognize that when input is 'b', lambda x.x is equivalent to lambda x.b

		List<SFTMove<CharPred, CharFunc, Character>> mytransitions1 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1 = new ArrayList<CharFunc>();
		output1.add(new CharConstant('b'));
		mytransitions1.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b'), output1));
		Map<Integer, Set<List<Character>>> myfinStatesAndTails1 = new HashMap<Integer, Set<List<Character>>>();
		myfinStatesAndTails1.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> mySFT1 = SFT.MkSFT(mytransitions1, 1, myfinStatesAndTails1, ba);

		List<SFTMove<CharPred, CharFunc, Character>> mytransitions2 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2 = new ArrayList<CharFunc>();
		output2.add(CharOffset.IDENTITY);
		mytransitions2.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b'), output2));
		Map<Integer, Set<List<Character>>> myfinStatesAndTails2 = new HashMap<Integer, Set<List<Character>>>();
		myfinStatesAndTails2.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> mySFT2 = SFT.MkSFT(mytransitions2, 1, myfinStatesAndTails2, ba);

		assertTrue(mySFT1.decide1equality(mySFT2, ba));

		// part 5 a more complicated transducer
		SFT testSFTTotal = getTestSFTTotal();
		SFT SFTCounterexample = getTestSFTCounterexample();
		assertFalse(testSFTTotal.decide1equality(SFTCounterexample, ba));
	}

	public static List<CharFunc> stringToCharFunc(String output) {
		List <CharFunc> charList = new LinkedList<>();
		for (Character c: output.toCharArray()) {
			charList.add(new CharConstant(c));
		}
		return charList;
	}

	public static Map<Integer, Set<List<Character>>> setToFTMap(Collection <Integer> finalStates) {
		HashMap <Integer, Set<List<Character>>> stateMap = new HashMap<>();

		for (Integer sid : finalStates) {
			stateMap.put(sid, new HashSet<List<Character>>());
		}

		return stateMap;
	}


	public static List<Character> strToList(String s) {
		List <Character> ret = new ArrayList<>();

		for (int i = 0; i < s.length(); i ++) {
			ret.add(s.charAt(i));
		}
		return ret;
	}

	public static SFT<CharPred, CharFunc, Character> getTestSFTTotal() throws Exception {
		List <SFTMove <CharPred, CharFunc, Character>> transitions = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates;

		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 1,
				new CharPred('a'),
				stringToCharFunc("d")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 2,
				new CharPred('b'),
				stringToCharFunc("")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				1, 3,
				new CharPred('a'),
				stringToCharFunc("d")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				1, 3,
				new CharPred('b'),
				stringToCharFunc("c")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				2, 3,
				new CharPred('a'),
				stringToCharFunc("dd")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				2, 3,
				new CharPred('b'),
				stringToCharFunc("c")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				3, 3,
				new CharPred('a'),
				stringToCharFunc("s")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				3, 3,
				new CharPred('b'),
				stringToCharFunc("w")));

		HashSet<Integer> finalStatesSet = new HashSet<>();
		finalStatesSet.add(0);
		finalStatesSet.add(1);
		finalStatesSet.add(2);
		finalStatesSet.add(3);
		finalStates = setToFTMap(finalStatesSet);
		return SFT.MkSFT(transitions, 0, finalStates, ba);
	}


	public static SFT<CharPred, CharFunc, Character> getTestSFTCounterexample() throws Exception {
		List <SFTMove <CharPred, CharFunc, Character>> transitions = new LinkedList<>();
		Map<Integer, Set<List<Character>>> finalStates;

		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('b'),
				stringToCharFunc("")));
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(
				0, 0,
				new CharPred('a'),
				stringToCharFunc("d")));
		HashSet<Integer> finalStatesSet = new HashSet<>();
		finalStatesSet.add(0);
		finalStates = setToFTMap(finalStatesSet);
		return SFT.MkSFT(transitions, 0, finalStates, ba);
	}

	/**
	 *
	 * Method: decide1equality(SFT<P, F, S> sft1withEps, SFT<P, F, S> sft2withEps, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testDecide1equalityForSft1withEpsSft2withEpsBa() throws Exception {
		// since decide1equality(SFT<P, F, S> otherSft, BooleanAlgebraSubst<P, F, S> ba) is just a wrapper of the method,
		// we could test the method incidentally by testing decide1equality(SFT<P, F, S> otherSft,
		// BooleanAlgebraSubst<P, F, S> ba)
	}

	/**
	 *
	 * Method: witness1disequality(SFT<P, F, S> otherSft, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testWitness1disequalityForOtherSftBa() throws Exception {
		for (int i = 0; i < allSFTs.size(); i++) {
			for (int j = 0; j < allSFTs.size(); j++) {
				if (i == j) {
					List<Character> witness = allSFTs.get(i).witness1disequality(allSFTs.get(j), ba);
					assertEquals(null, witness);
				}
			}
		}

		// Here I assume all SFTs' domains are CharPred('b', 'c')
		List<SFT<CharPred, CharFunc, Character>> SFTlibrary = new ArrayList<SFT<CharPred, CharFunc, Character>>();
		// 1. one state with one final state
		// 1.1 there is only one input transition and the output function is only a CharConstant
		List<SFTMove<CharPred, CharFunc, Character>> transitions121 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1211 = new ArrayList<CharFunc>();
		output1211.add(new CharConstant('b'));
		transitions121.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1211));
		Map<Integer, Set<List<Character>>> finStatesAndTails121 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails121.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT11 = SFT.MkSFT(transitions121, 1, finStatesAndTails121, ba);
		SFTlibrary.add(SFT11);

		// 1.2 there is only one input transition and the output function is only a CharOffset
		List<SFTMove<CharPred, CharFunc, Character>> transitions122 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1221 = new ArrayList<CharFunc>();
		output1221.add(new CharOffset(1));
		transitions122.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1221));
		Map<Integer, Set<List<Character>>> finStatesAndTails122 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails122.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT12 = SFT.MkSFT(transitions122, 1, finStatesAndTails122, ba);
		SFTlibrary.add(SFT12);

		// 1.3 there is only one input transition and there are many output functions in the transition
		List<SFTMove<CharPred, CharFunc, Character>> transitions123 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1231 = new ArrayList<CharFunc>();
		output1231.add(CharOffset.IDENTITY);
		output1231.add(new CharConstant('b'));
		transitions123.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('b', 'c'), output1231));
		Map<Integer, Set<List<Character>>> finStatesAndTails123 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails123.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT13 = SFT.MkSFT(transitions123, 1, finStatesAndTails123, ba);
		SFTlibrary.add(SFT13);

		// 1.4 many input transitions
		List<SFTMove<CharPred, CharFunc, Character>> transitions131 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1311 = new ArrayList<CharFunc>();
		output1311.add(CharOffset.IDENTITY);
		transitions131.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('d'), output1311));
		List<CharFunc> output1312 = new ArrayList<CharFunc>();
		output1312.add(new CharOffset(1));
		output1312.add(new CharConstant('d'));
		transitions131.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('c'), output1312));
		Map<Integer, Set<List<Character>>> finStatesAndTails131 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails131.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT14 = SFT.MkSFT(transitions131, 1, finStatesAndTails131, ba);
		SFTlibrary.add(SFT14);

		// 2. two states with one final state
		// 2.1 the transitions are in opposite directions
		List<SFTMove<CharPred, CharFunc, Character>> transitions242 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2421 = new ArrayList<CharFunc>();
		output2421.add(new CharOffset(3));
		transitions242.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b', 'c'), output2421));
		List<CharFunc> output2422 = new ArrayList<CharFunc>();
		output2422.add(new CharConstant('c'));
		transitions242.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('b', 'c'), output2422));
		Map<Integer, Set<List<Character>>> finStatesAndTails242 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails242.put(2, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> SFT21 = SFT.MkSFT(transitions242, 1, finStatesAndTails242, ba);
		SFTlibrary.add(SFT21);

		for (int i = 0; i < SFTlibrary.size(); i++) {
			for (int j = 0; j < SFTlibrary.size(); j++) {
				List<Character> witness = SFTlibrary.get(i).witness1disequality(SFTlibrary.get(j), ba);
				if (i == j) {
					assertEquals(null, witness);
				} else {
					if (witness != null) {
						List <Character> output1 = SFTlibrary.get(i).outputOn(witness, ba);
						List <Character> output2 = SFTlibrary.get(j).outputOn(witness, ba);
						assertFalse(output1.equals(output2));
					}
				}
			}
		}
	}

	@Test
	public void testadd() throws Exception {

	}

	/**
	 *
	 * Method: witness1disequality(SFT<P, F, S> sft1withEps, SFT<P, F, S> sft2withEps, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testWitness1disequalityForSft1withEpsSft2withEpsBa() throws Exception {
		// since witness1disequality(SFT<P, F, S> otherSft, BooleanAlgebraSubst<P, F, S> ba) is just a wrapper of the method,
		// we could test the method incidentally by testing witness1disequality(SFT<P, F, S> otherSft,
		// BooleanAlgebraSubst<P, F, S> ba)
	}

	/**
	 *
	 * Method: getDomain(BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testGetDomain() throws Exception {
		SFA<CharPred, Character> mySFA111 = mySFT111.getDomain(ba);
		SFA<CharPred, Character> mySFA121 = mySFT121.getDomain(ba);
		SFA<CharPred, Character> mySFA122 = mySFT122.getDomain(ba);
		SFA<CharPred, Character> mySFA123 = mySFT123.getDomain(ba);
		SFA<CharPred, Character> mySFA131 = mySFT131.getDomain(ba);
		SFA<CharPred, Character> mySFA211 = mySFT211.getDomain(ba);
		SFA<CharPred, Character> mySFA221 = mySFT221.getDomain(ba);
		SFA<CharPred, Character> mySFA222 = mySFT222.getDomain(ba);
		SFA<CharPred, Character> mySFA223 = mySFT223.getDomain(ba);
		SFA<CharPred, Character> mySFA231 = mySFT231.getDomain(ba);
		SFA<CharPred, Character> mySFA232 = mySFT232.getDomain(ba);
		SFA<CharPred, Character> mySFA241 = mySFT241.getDomain(ba);
		SFA<CharPred, Character> mySFA242 = mySFT242.getDomain(ba);
		SFA<CharPred, Character> mySFA251 = mySFT251.getDomain(ba);
		SFA<CharPred, Character> mySFA252 = mySFT252.getDomain(ba);
		SFA<CharPred, Character> mySFA261 = mySFT261.getDomain(ba);
		SFA<CharPred, Character> mySFA311 = mySFT311.getDomain(ba);
		SFA<CharPred, Character> mySFA321 = mySFT321.getDomain(ba);
		SFA<CharPred, Character> mySFA331 = mySFT331.getDomain(ba);
		SFA<CharPred, Character> mySFA411 = mySFT411.getDomain(ba);
		SFA<CharPred, Character> mySFA421 = mySFT421.getDomain(ba);

		Integer expectedInitialState = 1;
		assertEquals(expectedInitialState, mySFA111.getInitialState());
		assertEquals(expectedInitialState, mySFA121.getInitialState());
		assertEquals(expectedInitialState, mySFA122.getInitialState());
		assertEquals(expectedInitialState, mySFA123.getInitialState());
		assertEquals(expectedInitialState, mySFA131.getInitialState());
		// mySFA211 will be tested soon
		assertEquals(expectedInitialState, mySFA221.getInitialState());
		assertEquals(expectedInitialState, mySFA222.getInitialState());
		assertEquals(expectedInitialState, mySFA223.getInitialState());
		assertEquals(expectedInitialState, mySFA231.getInitialState());
		assertEquals(expectedInitialState, mySFA232.getInitialState());
		assertEquals(expectedInitialState, mySFA241.getInitialState());
		assertEquals(expectedInitialState, mySFA242.getInitialState());
		assertEquals(expectedInitialState, mySFA251.getInitialState());
		assertEquals(expectedInitialState, mySFA252.getInitialState());
		assertEquals(expectedInitialState, mySFA261.getInitialState());
		assertEquals(expectedInitialState, mySFA311.getInitialState());
		assertEquals(expectedInitialState, mySFA321.getInitialState());
		assertEquals(expectedInitialState, mySFA331.getInitialState());
		assertEquals(expectedInitialState, mySFA411.getInitialState());
		assertEquals(expectedInitialState, mySFA421.getInitialState());
		expectedInitialState = 0;
		assertEquals(expectedInitialState, mySFA211.getInitialState()); // since the final state of mySFA211 is
		// unreachable, the initial state is 0

		Collection<Integer> states;
		Collection<Integer> zeroState = new HashSet<Integer>();
		Collection<Integer> oneState = new HashSet<Integer>();
		Collection<Integer> twoStates= new HashSet<Integer>();
		Collection<Integer> threeStates = new HashSet<Integer>();
		zeroState.add(0);
		oneState.add(1);
		twoStates.add(1);
		twoStates.add(2);
		threeStates.add(1);
		threeStates.add(2);
		threeStates.add(3);
		states = mySFA111.getStates();
		assertEquals(oneState, states);
		states = mySFA121.getStates();
		assertEquals(oneState, states);
		states = mySFA122.getStates();
		assertEquals(oneState, states);
		states = mySFA123.getStates();
		assertEquals(oneState, states);
		states = mySFA131.getStates();
		assertEquals(oneState, states);
		states = mySFA211.getStates();
		assertEquals(zeroState, states);
		states = mySFA221.getStates();
		assertEquals(twoStates, states);
		states = mySFA222.getStates();
		assertEquals(twoStates, states);
		states = mySFA223.getStates();
		assertEquals(twoStates, states);
		states = mySFA231.getStates();
		assertEquals(twoStates, states);
		states = mySFA232.getStates();
		assertEquals(twoStates, states);
		states = mySFA241.getStates();
		assertEquals(twoStates, states);
		states = mySFA242.getStates();
		assertEquals(twoStates, states);
		states = mySFA251.getStates();
		assertEquals(twoStates, states);
		states = mySFA252.getStates();
		assertEquals(twoStates, states);
		states = mySFA261.getStates();
		assertEquals(twoStates, states);
		states = mySFA311.getStates();
		assertEquals(threeStates, states);
		states = mySFA321.getStates();
		assertEquals(threeStates, states);
		states = mySFA331.getStates();
		assertEquals(threeStates, states);
		states = mySFA411.getStates();
		assertEquals(threeStates, states);
		states = mySFA421.getStates();
		assertEquals(threeStates, states);
	}

	/**
	 *
	 * Method: inverseImage(SFA<P, S> sfaWithEps, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testInverseImage() throws Exception {
		List<List<Character>> inputs = new ArrayList<List<Character>>(); // it is a corpus of all kinds of inputs
		//inputs.add(stringToListOfCharacter(""));
		inputs.add(stringToListOfCharacter("a"));
		inputs.add(stringToListOfCharacter("b"));
		inputs.add(stringToListOfCharacter("c"));
		inputs.add(stringToListOfCharacter("d"));
		inputs.add(stringToListOfCharacter("z"));
		inputs.add(stringToListOfCharacter("1"));
		inputs.add(stringToListOfCharacter(" "));
		inputs.add(stringToListOfCharacter("bb"));
		inputs.add(stringToListOfCharacter("ab"));
		inputs.add(stringToListOfCharacter("ac"));
		inputs.add(stringToListOfCharacter("ccc"));
		inputs.add(stringToListOfCharacter("abcz"));
		inputs.add(stringToListOfCharacter("bcsaee"));
		inputs.add(stringToListOfCharacter("12-3"));

		// I. one state with one final state
		// i. no transition
		LinkedList<SFAMove<CharPred, Character>> transitions11 = new LinkedList<SFAMove<CharPred, Character>>();
		List<Integer> finStates11 = new LinkedList<Integer>();
		finStates11.add(1);
		SFA<CharPred, Character> mySA11 = SFA.MkSFA(transitions11, 1, finStates11, ba);

		for (SFT<CharPred, CharFunc, Character> sft: allSFTs) {
			SFA<CharPred, Character> domain = sft.getDomain(ba);
			SFA<CharPred, Character> image = sft.inverseImage(mySA11, ba);
			for (List<Character> input: inputs)
				assertFalse(image.accepts(input, ba));
		}
	}

	/**
	 *
	 * Method: typeCheck(SFA<P, S> input, SFT<P, F, S> transducer, SFA<P, S> output, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testTypeCheck() throws Exception {
		// input1: all strings in the form of a*
		LinkedList<SFAMove<CharPred, Character>> transitions11 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions11.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('a')));
		List<Integer> finStates11 = new LinkedList<Integer>();
		finStates11.add(1);
		SFA<CharPred, Character> input1 = SFA.MkSFA(transitions11, 1, finStates11, ba);

		// input2: all strings in the form of b*
		LinkedList<SFAMove<CharPred, Character>> transitions12 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions12.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('b')));
		List<Integer> finStates12 = new LinkedList<Integer>();
		finStates12.add(1);
		SFA<CharPred, Character> input2 = SFA.MkSFA(transitions12, 1, finStates12, ba);

		// input3: only accept one single 'a'
		LinkedList<SFAMove<CharPred, Character>> transitions13 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions13.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		List<Integer> finStates13 = new LinkedList<Integer>();
		finStates13.add(2);
		SFA<CharPred, Character> input3 = SFA.MkSFA(transitions13, 1, finStates13, ba);

		// input4: only accept one single 'b'
		LinkedList<SFAMove<CharPred, Character>> transitions14 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions14.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('b')));
		List<Integer> finStates14 = new LinkedList<Integer>();
		finStates14.add(2);
		SFA<CharPred, Character> input4 = SFA.MkSFA(transitions14, 1, finStates14, ba);

		// transducer1: convert all characters a to c and discard other characters
		List<SFTMove<CharPred, CharFunc, Character>> transitions21 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output21 = new ArrayList<CharFunc>();
		output21.add(new CharConstant('c'));
		transitions21.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('a'), output21));
		Map<Integer, Set<List<Character>>> finStatesAndTails21 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails21.put(1, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> transducer1 = SFT.MkSFT(transitions21, 1, finStatesAndTails21, ba);

		// transducer2: convert an 'a' to a 'c' and a 'b' to 'd'
		List<SFTMove<CharPred, CharFunc, Character>> transitions22 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output221 = new ArrayList<CharFunc>();
		output221.add(new CharConstant('c'));
		transitions22.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a'), output221));
		List<CharFunc> output222 = new ArrayList<CharFunc>();
		output222.add(new CharConstant('d'));
		transitions22.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('b'), output222));
		Map<Integer, Set<List<Character>>> finStatesAndTails22 = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails22.put(2, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> transducer2 = SFT.MkSFT(transitions22, 1, finStatesAndTails22, ba);

		// output1: all strings in the form of c*
		LinkedList<SFAMove<CharPred, Character>> transitions31 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions31.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('c')));
		List<Integer> finStates31 = new LinkedList<Integer>();
		finStates31.add(1);
		SFA<CharPred, Character> output1 = SFA.MkSFA(transitions31, 1, finStates31, ba);

		// output2: only accepts a 'c'
		LinkedList<SFAMove<CharPred, Character>> transitions32 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions32.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('c')));
		List<Integer> finStates32 = new LinkedList<Integer>();
		finStates32.add(2);
		SFA<CharPred, Character> output2 = SFA.MkSFA(transitions32, 1, finStates32, ba);

		assertTrue(SFT.typeCheck(input1, transducer1, output1, ba));
		assertTrue(SFT.typeCheck(input2, transducer1, output1, ba));
		assertTrue(SFT.typeCheck(input3, transducer2, output2, ba));
		assertFalse(SFT.typeCheck(input4, transducer2, output2, ba));
	}

	/**
	 *
	 * Method: domainRestriction(SFA<P, S> sfaWithEps, BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testDomainRestriction() throws Exception {
		for (SFT<CharPred, CharFunc, Character> sft: allSFTs) {
			SFT<CharPred, CharFunc, Character> restricted = sft.domainRestriction(sft.getDomain(ba), ba);
			assertTrue(sft.decide1equality(restricted, ba));
		}

		// I. one state with one final state
		// i. no transition
		LinkedList<SFAMove<CharPred, Character>> transitions11 = new LinkedList<SFAMove<CharPred, Character>>();
		List<Integer> finStates11 = new LinkedList<Integer>();
		finStates11.add(1);
		SFA<CharPred, Character> mySA11 = SFA.MkSFA(transitions11, 1, finStates11, ba);
		for (SFT<CharPred, CharFunc, Character> sft: allSFTs) {
			SFT<CharPred, CharFunc, Character> restricted = sft.domainRestriction(mySA11, ba);
			assertTrue(mySFT111.decide1equality(restricted, ba));
		}

		// II. two states with one final state
		// i. onr arc, one transition condition
		LinkedList<SFAMove<CharPred, Character>> transitions21 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions21.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('b')));
		List<Integer> finStates21 = new LinkedList<Integer>();
		finStates21.add(2);
		SFA<CharPred, Character> restriction = SFA.MkSFA(transitions21, 1, finStates21, ba);

		List<SFTMove<CharPred, CharFunc, Character>> transitions222 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output2221 = new ArrayList<CharFunc>();
		output2221.add(new CharOffset(1));
		transitions222.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('c'), output2221));
		Map<Integer, Set<List<Character>>> finStates222 = new HashMap<Integer, Set<List<Character>>>();
		finStates222.put(2, new HashSet<List<Character>>());
		SFT<CharPred, CharFunc, Character> expectedSFT = SFT.MkSFT(transitions222, 1, finStates222, ba);

		assertTrue(expectedSFT.decide1equality(mySFT222.domainRestriction(restriction, ba), ba));
	}

	/**
	 *
	 * Method: getOutputSFA(BooleanAlgebraSubst<P, F, S> ba)
	 *
	 */
	@Test
	public void testGetOutputSFA() throws Exception {
		// SFT that produces no output
		List<SFTMove<CharPred, CharFunc, Character>> transitions121 = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		List<CharFunc> output1211 = new ArrayList<CharFunc>();
		// 1 -- [ac]/[] --> 2
		transitions121.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('a', 'c'), output1211));
		Map<Integer, Set<List<Character>>> finStatesAndTails121 = new HashMap<Integer, Set<List<Character>>>();
		// 2 -- [] --> *
		finStatesAndTails121.put(2, new HashSet<List<Character>>());
		// 1 is initial, 2 is final with tail []
		SFT<CharPred, CharFunc, Character> noOutputFunctions = SFT.MkSFT(transitions121, 1, finStatesAndTails121, ba);

		// SFA that accepts only epsilon
		LinkedList<SFAMove<CharPred, Character>> transitions1 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions1.add(new SFAEpsilon<CharPred,Character>(1, 2));
		List<Integer> finStates1 = new LinkedList<Integer>();
		finStates1.add(2);
		SFA<CharPred, Character> expected1 = SFA.MkSFA(transitions1, 1, finStates1, ba);
		
		// SFT output is epsilon
		assertTrue(expected1.isEquivalentTo(noOutputFunctions.getOutputSFA(ba), ba));

		// II. two states with one final state
		// i. onr arc, one transition condition
		LinkedList<SFAMove<CharPred, Character>> transitions21 = new LinkedList<SFAMove<CharPred, Character>>();
		// 1 -- [cd] --> 2
		transitions21.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('c', 'd')));
		List<Integer> finStates21 = new LinkedList<Integer>();
		finStates21.add(2);
		SFA<CharPred, Character> expected2 = SFA.MkSFA(transitions21, 1, finStates21, ba);

		SFA<CharPred, Character> output2 = mySFT222.getOutputSFA(ba);
		
		assertTrue(expected2.isEquivalentTo(output2, ba));
	}

	/**
	 *
	 * Method: getFinalStates()
	 *
	 */
	@Test
	public void testGetFinalStates() throws Exception {
		Collection<Integer> finalStates = new HashSet<Integer>();
		finalStates.add(1);
		assertEquals(finalStates, mySFT111.getFinalStates());
		assertEquals(finalStates, mySFT121.getFinalStates());
		assertEquals(finalStates, mySFT122.getFinalStates());
		assertEquals(finalStates, mySFT123.getFinalStates());
		assertEquals(finalStates, mySFT131.getFinalStates());
		finalStates = new HashSet<Integer>();
		finalStates.add(2);
		assertEquals(finalStates, mySFT211.getFinalStates());
		assertEquals(finalStates, mySFT221.getFinalStates());
		assertEquals(finalStates, mySFT222.getFinalStates());
		assertEquals(finalStates, mySFT223.getFinalStates());
		assertEquals(finalStates, mySFT231.getFinalStates());
		assertEquals(finalStates, mySFT232.getFinalStates());
		assertEquals(finalStates, mySFT241.getFinalStates());
		assertEquals(finalStates, mySFT242.getFinalStates());
		assertEquals(finalStates, mySFT251.getFinalStates());
		assertEquals(finalStates, mySFT252.getFinalStates());
		assertEquals(finalStates, mySFT261.getFinalStates());
		finalStates = new HashSet<Integer>();
		finalStates.add(3);
		assertEquals(finalStates, mySFT311.getFinalStates());
		assertEquals(finalStates, mySFT321.getFinalStates());
		assertEquals(finalStates, mySFT331.getFinalStates());
		finalStates = new HashSet<Integer>();
		finalStates.add(2);
		finalStates.add(3);
		assertEquals(finalStates, mySFT411.getFinalStates());
		finalStates = new HashSet<Integer>();
		finalStates.add(1);
		finalStates.add(3);
		assertEquals(finalStates, mySFT421.getFinalStates());
	}

	/**
	 *
	 * Method: getInitialState()
	 *
	 */
	@Test
	public void testGetInitialState() throws Exception {
		Integer expectedInitialState = 1;
		assertEquals(expectedInitialState, mySFT111.getInitialState());
		assertEquals(expectedInitialState, mySFT121.getInitialState());
		assertEquals(expectedInitialState, mySFT122.getInitialState());
		assertEquals(expectedInitialState, mySFT123.getInitialState());
		assertEquals(expectedInitialState, mySFT131.getInitialState());
		assertEquals(expectedInitialState, mySFT211.getInitialState());
		assertEquals(expectedInitialState, mySFT221.getInitialState());
		assertEquals(expectedInitialState, mySFT222.getInitialState());
		assertEquals(expectedInitialState, mySFT223.getInitialState());
		assertEquals(expectedInitialState, mySFT231.getInitialState());
		assertEquals(expectedInitialState, mySFT232.getInitialState());
		assertEquals(expectedInitialState, mySFT241.getInitialState());
		assertEquals(expectedInitialState, mySFT242.getInitialState());
		assertEquals(expectedInitialState, mySFT251.getInitialState());
		assertEquals(expectedInitialState, mySFT252.getInitialState());
		assertEquals(expectedInitialState, mySFT261.getInitialState());
		assertEquals(expectedInitialState, mySFT311.getInitialState());
		assertEquals(expectedInitialState, mySFT321.getInitialState());
		assertEquals(expectedInitialState, mySFT331.getInitialState());
		assertEquals(expectedInitialState, mySFT411.getInitialState());
		assertEquals(expectedInitialState, mySFT421.getInitialState());
	}

	/**
	 *
	 * Method: getStates()
	 *
	 */
	@Test
	public void testGetStates() throws Exception {
		Collection<Integer> states;
		Collection<Integer> oneState = new HashSet<Integer>();
		Collection<Integer> twoStates= new HashSet<Integer>();
		Collection<Integer> threeStates = new HashSet<Integer>();
		oneState.add(1);
		twoStates.add(1);
		twoStates.add(2);
		threeStates.add(1);
		threeStates.add(2);
		threeStates.add(3);
		states = mySFT111.getStates();
		assertEquals(oneState, states);
		states = mySFT121.getStates();
		assertEquals(oneState, states);
		states = mySFT122.getStates();
		assertEquals(oneState, states);
		states = mySFT123.getStates();
		assertEquals(oneState, states);
		states = mySFT131.getStates();
		assertEquals(oneState, states);
		states = mySFT211.getStates();
		assertEquals(twoStates, states);
		states = mySFT221.getStates();
		assertEquals(twoStates, states);
		states = mySFT222.getStates();
		assertEquals(twoStates, states);
		states = mySFT223.getStates();
		assertEquals(twoStates, states);
		states = mySFT231.getStates();
		assertEquals(twoStates, states);
		states = mySFT232.getStates();
		assertEquals(twoStates, states);
		states = mySFT241.getStates();
		assertEquals(twoStates, states);
		states = mySFT242.getStates();
		assertEquals(twoStates, states);
		states = mySFT251.getStates();
		assertEquals(twoStates, states);
		states = mySFT252.getStates();
		assertEquals(twoStates, states);
		states = mySFT261.getStates();
		assertEquals(twoStates, states);
		states = mySFT311.getStates();
		assertEquals(threeStates, states);
		states = mySFT321.getStates();
		assertEquals(threeStates, states);
		states = mySFT331.getStates();
		assertEquals(threeStates, states);
		states = mySFT411.getStates();
		assertEquals(threeStates, states);
		states = mySFT421.getStates();
		assertEquals(threeStates, states);
	}

	/**
	 *
	 * Method: clone()
	 *
	 */
	@Test
	public void testClone() throws Exception {
		for (SFT<CharPred, CharFunc, Character> sft: allSFTs) {
			SFT<CharPred, CharFunc, Character> clone = (SFT<CharPred, CharFunc, Character>) sft.clone();
			assertTrue(sft.decide1equality(clone, ba));
		}
	}

	/**
	 *
	 * Method: toString()
	 *
	 */
	@Test
	public void testToString() throws Exception {
		// since it is just used for debugging, we have tested toString by looking at debugger's console
	}

	/**
	 *
	 * Method: addTransition(SFTMove<P, F, S> transition, BooleanAlgebraSubst<P, F, S> ba, boolean skipSatCheck)
	 *
	 */
	@Test
	public void testAddTransition() throws Exception {
		// it is tested in beforeClass()
	}
}

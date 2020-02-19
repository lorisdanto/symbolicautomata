package correction;

import automata.sfa.SFAInputMove;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFAMove;
import strings.EditDistanceStrToSFA;
import automata.sfa.SFA;
import utilities.Pair;
import strings.EditDistanceStrToStr;

import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class StringCorrectionUnitTest {

	private static SFA<CharPred, Character> mySA11;
	private static SFA<CharPred, Character> mySA12;
	private static SFA<CharPred, Character> mySA13;
	private static SFA<CharPred, Character> mySA21;
	private static SFA<CharPred, Character> mySA22;
	private static SFA<CharPred, Character> mySA23;
	private static SFA<CharPred, Character> mySA24;
	private static SFA<CharPred, Character> mySA25;
	private static SFA<CharPred, Character> mySA26;
	private static SFA<CharPred, Character> mySA31;
	private static SFA<CharPred, Character> mySA32;
	private static SFA<CharPred, Character> mySA33;
	private static SFA<CharPred, Character> mySA34;
	private static SFA<CharPred, Character> mySA35;
	private static SFA<CharPred, Character> mySA41;
	private static SFA<CharPred, Character> mySA42;
	private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

	@BeforeClass
	public static void before() throws Exception {
		// I. one state with one final state
		// i. no transition
		LinkedList<SFAMove<CharPred, Character>> transitions11 = new LinkedList<SFAMove<CharPred, Character>>();
		LinkedList<Integer> finStates11 = new LinkedList<>();
		finStates11.add(1);
		mySA11 = SFA.MkSFA(transitions11, 1, finStates11, ba);

		// ii. one transition
		LinkedList<SFAMove<CharPred, Character>> transitions12 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions12.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('a')));
		LinkedList<Integer> finStates12 = new LinkedList<>();
		finStates12.add(1);
		mySA12 = SFA.MkSFA(transitions12, 1, finStates12, ba);

		// iii. multiple transitions
		LinkedList<SFAMove<CharPred, Character>> transitions13 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions13.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('a')));
		transitions13.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('b')));
		LinkedList<Integer> finStates13 = new LinkedList<>();
		finStates13.add(1);
		mySA13 = SFA.MkSFA(transitions13, 1, finStates13, ba);

		// II. two states with one final state () it is not necessary to test the case
		// of 2 states with 2 final states
		// i. onr arc, one transition condition
		LinkedList<SFAMove<CharPred, Character>> transitions21 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions21.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		LinkedList<Integer> finStates21 = new LinkedList<>();
		finStates21.add(2);
		mySA21 = SFA.MkSFA(transitions21, 1, finStates21, ba);

		// ii. one arc, multiple transition conditions
		LinkedList<SFAMove<CharPred, Character>> transitions22 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions22.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions22.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('b')));
		LinkedList<Integer> finStates22 = new LinkedList<>();
		finStates22.add(2);
		mySA22 = SFA.MkSFA(transitions22, 1, finStates22, ba);

		// iii. two arcs with the same transition condition
		LinkedList<SFAMove<CharPred, Character>> transitions23 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions23.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions23.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('a')));
		LinkedList<Integer> finStates23 = new LinkedList<>();
		finStates23.add(2);
		mySA23 = SFA.MkSFA(transitions23, 1, finStates23, ba);

		// iv. two arcs with different transition conditions
		LinkedList<SFAMove<CharPred, Character>> transitions24 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions24.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions24.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('b')));
		LinkedList<Integer> finStates24 = new LinkedList<>();
		finStates24.add(2);
		mySA24 = SFA.MkSFA(transitions24, 1, finStates24, ba);

		// v. two arcs with more than 2 and different transition conditions
		LinkedList<SFAMove<CharPred, Character>> transitions25 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions25.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions25.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('b')));
		transitions25.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('c')));
		transitions25.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('d')));
		transitions25.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('e')));
		LinkedList<Integer> finStates25 = new LinkedList<>();
		finStates25.add(2);
		mySA25 = SFA.MkSFA(transitions25, 1, finStates25, ba);

		// vi. unreachable finite state
		LinkedList<SFAMove<CharPred, Character>> transitions26 = new LinkedList<SFAMove<CharPred, Character>>();
		LinkedList<Integer> finStates26 = new LinkedList<>();
		finStates26.add(2);
		mySA26 = SFA.MkSFA(transitions26, 1, finStates26, ba);

		// III. multiple states with one final state
		// i. n sates with n - 1 arcs and the same transition condition
		LinkedList<SFAMove<CharPred, Character>> transitions31 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions31.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions31.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('a')));
		LinkedList<Integer> finStates31 = new LinkedList<>();
		finStates31.add(3);
		mySA31 = SFA.MkSFA(transitions31, 1, finStates31, ba);

		// ii. n sates with n - 1 arcs and the different transition condition
		LinkedList<SFAMove<CharPred, Character>> transitions32 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions32.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions32.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('b')));
		transitions32.add(new SFAInputMove<CharPred, Character>(3, 4, new CharPred('c')));
		transitions32.add(new SFAInputMove<CharPred, Character>(4, 5, new CharPred('d')));
		transitions32.add(new SFAInputMove<CharPred, Character>(5, 6, new CharPred('e')));
		LinkedList<Integer> finStates32 = new LinkedList<>();
		finStates32.add(6);
		mySA32 = SFA.MkSFA(transitions32, 1, finStates32, ba);

		// iii. n sates with n arcs
		LinkedList<SFAMove<CharPred, Character>> transitions33 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions33.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions33.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('a')));
		transitions33.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('b')));
		LinkedList<Integer> finStates33 = new LinkedList<>();
		finStates33.add(3);
		mySA33 = SFA.MkSFA(transitions33, 1, finStates33, ba);

		// iv. n sates with more than n arcs
		LinkedList<SFAMove<CharPred, Character>> transitions34 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions34.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions34.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('b')));
		transitions34.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('c')));
		transitions34.add(new SFAInputMove<CharPred, Character>(3, 2, new CharPred('b')));
		transitions34.add(new SFAInputMove<CharPred, Character>(3, 1, new CharPred('c')));
		LinkedList<Integer> finStates34 = new LinkedList<>();
		finStates34.add(3);
		mySA34 = SFA.MkSFA(transitions34, 1, finStates34, ba);

		// v. n sates with self-pointing arcs
		LinkedList<SFAMove<CharPred, Character>> transitions35 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions35.add(new SFAInputMove<CharPred, Character>(1, 1, new CharPred('a')));
		transitions35.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('b')));
		transitions35.add(new SFAInputMove<CharPred, Character>(2, 2, new CharPred('a')));
		transitions35.add(new SFAInputMove<CharPred, Character>(2, 2, new CharPred('b')));
		transitions35.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('c')));
		transitions35.add(new SFAInputMove<CharPred, Character>(3, 3, new CharPred('c')));
		LinkedList<Integer> finStates35 = new LinkedList<>();
		finStates35.add(3);
		mySA35 = SFA.MkSFA(transitions35, 1, finStates35, ba);

		// IV. multiple states with multiple final states
		// i. the initial state is not a final state
		LinkedList<SFAMove<CharPred, Character>> transitions41 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions41.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions41.add(new SFAInputMove<CharPred, Character>(2, 1, new CharPred('b')));
		transitions41.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('c')));
		LinkedList<Integer> finStates41 = new LinkedList<>();
		finStates41.add(2);
		finStates41.add(3);
		mySA41 = SFA.MkSFA(transitions41, 1, finStates41, ba);

		// ii. the initial state is also a final state
		LinkedList<SFAMove<CharPred, Character>> transitions42 = new LinkedList<SFAMove<CharPred, Character>>();
		transitions42.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred('a')));
		transitions42.add(new SFAInputMove<CharPred, Character>(2, 3, new CharPred('a')));
		transitions42.add(new SFAInputMove<CharPred, Character>(3, 2, new CharPred('b')));
		LinkedList<Integer> finStates42 = new LinkedList<>();
		finStates42.add(1);
		finStates42.add(3);
		mySA42 = SFA.MkSFA(transitions42, 1, finStates42, ba);
	}

	/**
	 *
	 * Method: computeEditDistance(SFA<CharPred, Character> inpSFA, String inpStr)
	 *
	 */
	@Test
	public void testComputeEditDistance() throws TimeoutException {
		int distance;
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA11, "");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA11, "a");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA11, "aaa");
		assertEquals(3, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA11, "bc");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA11, "cab");
		assertEquals(3, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA12, "");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA12, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA12, "aaa");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA12, "bc");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA12, "cab");
		assertEquals(2, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA13, "");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA13, "bc");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA13, "ab");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA13, "abba");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA13, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA13, "cd");
		assertEquals(2, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA21, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA21, "b");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA21, "abc");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA21, "aaa");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA21, "bcd");
		assertEquals(3, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA22, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA22, "ab");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA22, "bc");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA22, "cde");
		assertEquals(3, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA23, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA23, "b");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA23, "abc");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA23, "aa");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA23, "aaa");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA23, "bcd");
		assertEquals(3, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "b");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "abc");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "acb");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "aa");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "aaa");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA24, "bcd");
		assertEquals(3, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA25, "a");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA25, "b");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA25, "abc");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA25, "aa");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA25, "aaa");
		assertEquals(1, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA26, "");
		assertEquals(-1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA26, "a");
		assertEquals(-1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA26, "ab");
		assertEquals(-1, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA31, "a");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA31, "aa");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA31, "ab");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA31, "bce");
		assertEquals(3, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA31, "aaa");
		assertEquals(1, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "a");
		assertEquals(4, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "aa");
		assertEquals(4, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "ab");
		assertEquals(3, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "bce");
		assertEquals(2, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "aaa");
		assertEquals(4, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "abcde");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "bcaab");
		assertEquals(4, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "aaaaaaaaa");
		assertEquals(8, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA33, "a");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA33, "b");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA33, "ab");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA33, "abc");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA33, "abb");
		assertEquals(1, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA34, "a");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA34, "ab");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA34, "abbb");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA34, "abcac");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA34, "abcad");
		assertEquals(1, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA35, "aaabbbccc");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA35, "accbc");
		assertEquals(2, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA41, "ac");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA41, "abc");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA41, "bb");
		assertEquals(2, distance);

		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA42, "");
		assertEquals(0, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA42, "a");
		assertEquals(1, distance);
		distance = EditDistanceStrToSFA.computeShortestEditDistance(mySA42, "aab");
		assertEquals(1, distance);

	}

	/**
	 *
	 * Method: getCorrectString(SFA<CharPred, Character> inpSFA, String inpStr)
	 *
	 */
	@Test
	public void testGetCorrectString() throws TimeoutException {
		int resultEditDistance;
		String correctString;
		correctString = EditDistanceStrToSFA.getCorrectString(mySA11, "");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA11, "a");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA11, "aaa");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA11, "bc");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA11, "cab");
		assertEquals("", correctString);

		correctString = EditDistanceStrToSFA.getCorrectString(mySA12, "");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA12, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA12, "aaa");
		assertEquals("aaa", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA12, "bc");
		assertTrue(correctString.equals("aa") || correctString.equals("a") || correctString.equals(""));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA12, "cab");
		assertTrue(correctString.equals("a") || correctString.equals("aa") || correctString.equals("aaa"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA13, "");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA13, "bc");
		assertTrue(correctString.equals("ba") || correctString.equals("b"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA13, "ab");
		assertEquals("ab", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA13, "abba");
		assertEquals("abba", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA13, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA13, "cd");
		assertTrue(correctString.equals("") || correctString.equals("a") || correctString.equals("b")
				|| correctString.equals("ab") || correctString.equals("ba"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA21, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA21, "b");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA21, "abc");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA21, "aaa");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA21, "bcd");
		assertEquals("a", correctString);

		correctString = EditDistanceStrToSFA.getCorrectString(mySA22, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA22, "ab");
		assertTrue(correctString.equals("a") || correctString.equals("b"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA22, "bc");
		assertEquals("b", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA22, "cde");
		assertTrue(correctString.equals("a") || correctString.equals("b"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA23, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA23, "b");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA23, "abc");
		assertTrue(correctString.equals("a") || correctString.equals("aaa"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA23, "aa");
		assertTrue(correctString.equals("a") || correctString.equals("aaa"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA23, "aaa");
		assertEquals("aaa", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA23, "bcd");
		assertTrue(correctString.equals("a") || correctString.equals("aaa"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "b");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "abc");
		assertEquals("aba", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "acb");
		assertTrue(correctString.equals("a") || correctString.equals("aba"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "aa");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "aaa");
		assertEquals("aba", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA24, "bcd");
		assertTrue(correctString.equals("a") || correctString.equals("aba"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA25, "a");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA25, "b");
		assertEquals("b", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA25, "abc");
		assertTrue(correctString.equals("a") || correctString.equals("b") || correctString.equals("aca")
				|| correctString.equals("acb") || correctString.equals("ada") || correctString.equals("adb")
				|| correctString.equals("aea") || correctString.equals("aeb") || correctString.equals("bca")
				|| correctString.equals("bcb"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA25, "aa");
		assertEquals("a", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA25, "aaa");
		assertTrue(correctString.equals("aca") || correctString.equals("ada") || correctString.equals("aea"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA31, "a");
		assertEquals("aa", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA31, "aa");
		assertEquals("aa", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA31, "ab");
		assertEquals("aa", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA31, "bce");
		assertEquals("aa", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA31, "aaa");
		assertEquals("aa", correctString);

		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "a");
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "aa");
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "ab");
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "bce");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "bce");
		assertEquals(EditDistanceStrToStr.getEditDistance("bce", correctString), resultEditDistance);
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "aaa");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "aaa");
		assertEquals(EditDistanceStrToStr.getEditDistance("aaa", correctString), resultEditDistance);
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "abcde");
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "bcaab");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "bcaab");
		assertEquals(EditDistanceStrToStr.getEditDistance("bcaab", correctString), resultEditDistance);
		assertEquals("abcde", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA32, "aaaaaaaaa");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA32, "aaaaaaaaa");
		assertEquals(EditDistanceStrToStr.getEditDistance("aaaaaaaaa", correctString), resultEditDistance);
		assertEquals("abcde", correctString);

		correctString = EditDistanceStrToSFA.getCorrectString(mySA33, "a");
		assertEquals("ab", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA33, "b");
		assertEquals("ab", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA33, "ab");
		assertEquals("ab", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA33, "abc");
		assertEquals("ab", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA33, "abb");
		assertEquals("ab", correctString);

		correctString = EditDistanceStrToSFA.getCorrectString(mySA34, "a");
		assertTrue(correctString.equals("ab") || correctString.equals("ac"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA34, "ab");
		assertEquals("ab", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA34, "abbb");
		assertEquals("abbb", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA34, "abcac");
		assertEquals("abcac", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA34, "abcad");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA34, "abcad");
		assertEquals(EditDistanceStrToStr.getEditDistance("abcad", correctString), resultEditDistance);
		assertTrue(correctString.equals("abcab") || correctString.equals("abcac"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA35, "aaabbbccc");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA35, "aaabbbccc");
		assertEquals(EditDistanceStrToStr.getEditDistance("aaabbbccc", correctString), resultEditDistance);
		assertEquals("aaabbbccc", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA35, "accbc");
		resultEditDistance = EditDistanceStrToSFA.computeShortestEditDistance(mySA35, "accbc");
		assertEquals(EditDistanceStrToStr.getEditDistance("accbc", correctString), resultEditDistance);
		assertTrue(correctString.equals("abc") || correctString.equals("aaabc") || correctString.equals("aabbc")
				|| correctString.equals("ababc") || correctString.equals("abbbc") || correctString.equals("aabc")
				|| correctString.equals("abbc") || correctString.equals("bccc"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA41, "ac");
		assertEquals("ac", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA41, "abc");
		assertTrue(correctString.equals("aba") || correctString.equals("ac"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA41, "bb");
		assertTrue(correctString.equals("a") || correctString.equals("ac"));

		correctString = EditDistanceStrToSFA.getCorrectString(mySA42, "");
		assertEquals("", correctString);
		correctString = EditDistanceStrToSFA.getCorrectString(mySA42, "a");
		assertTrue(correctString.equals("") || correctString.equals("aa"));
		correctString = EditDistanceStrToSFA.getCorrectString(mySA42, "aab");
		assertTrue(correctString.equals("abaa") || correctString.equals("aa"));
	}
}

package benchmark.SFT;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.sat4j.specs.TimeoutException;
import theory.characters.CharConstant;
import theory.characters.CharFunc;
import theory.characters.CharOffset;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import transducers.sft.SFT;
import transducers.sft.SFTMove;
import transducers.sft.SFTInputMove;

/**
 * extracts from a given input stream of characters of all substreams of the form ['<', x, ,'>'], where x != '<'
 * It is an implementation of the example on page 4 of a paper called Symbolic Finite State Transducers: Algorithms And
 * Applications
 * You could use JUnit to test method GetTagsBySFT or just use normal way to run the main method in order to extract tags
 */
public class GetTag {

	/**
	 * extracts from a given input stream of characters of all substreams of the form ['<', x, ,'>'], where x != '<'
	 * @param input an input stream
	 * @return all substreams
	 */
	public static String GetTags(String input) {
		int q = 0; // keeps track of the relative position in the pattern ['<', x, '>']
		char c = (char)0; // the previous character
		StringBuilder result = new StringBuilder();
		for (char x: input.toCharArray()) {
			if (q == 0) {
				if (x == '<')
					q = 1;
				else
					q = 0;
			} else if (q == 1) {
				if (x == '<')
					q = 1;
				else
					q = 2;
			} else if (q == 2) {
				if (x == '>') {
					result.append('<');
					result.append(c);
					result.append('>');
				}
				q = (x == '<' ? 1 : 0);
			}
			c = x;
		}
		return result.toString();
	}

	private static SFT<CharPred, CharFunc, Character> sft = null;
	private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

	private static SFT<CharPred, CharFunc, Character> MkGetTagsSFT() throws TimeoutException {
		List<SFTMove<CharPred, CharFunc, Character>> transitions = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();

		List<CharFunc> output00 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, ba.MkNot(new CharPred('<')), output00));

		List<CharFunc> output01 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 1, new CharPred('<'), output01));

		List<CharFunc> output11 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('<'), output11));

		List<CharFunc> output12 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, ba.MkNot(new CharPred('<')), output12));

		List<CharFunc> output13 = new ArrayList<CharFunc>();
		output13.add(new CharConstant('<'));
		output13.add(CharOffset.IDENTITY);
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 3, ba.MkNot(new CharPred('<')), output13));

		List<CharFunc> output20 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 0, ba.MkAnd(ba.MkNot(new CharPred('<')), ba.MkNot(new CharPred('>'))), output20));

		List<CharFunc> output21 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('<'), output21));

		List<CharFunc> output30 = new ArrayList<CharFunc>();
		output30.add(CharOffset.IDENTITY);
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(3, 0, new CharPred('>'), output30));

		Map<Integer, Set<List<Character>>> finStatesAndTails = new HashMap<Integer, Set<List<Character>>>();
		finStatesAndTails.put(0, new HashSet<List<Character>>());
		finStatesAndTails.put(1, new HashSet<List<Character>>());
		finStatesAndTails.put(2, new HashSet<List<Character>>());

		return SFT.MkSFT(transitions, 0, finStatesAndTails, ba);
	}


	/**
	 * the implementation of the SFT in left column on page 4 on the paper named after Symbolic Finite State Transducers:
	 * Algorithms And Applications
	 */
	public static String GetTagsBySFT(String input) throws TimeoutException {
		// if the required SFT has not been created yet, then generate it. Otherwise we could just use it directly
		// instead of generating it every time GetTagsBySFT is called, which is quite time consuming.
		if (sft == null)
			sft = MkGetTagsSFT();

		List<Character> output = sft.outputOn(stringToListOfCharacter(input), ba);

		return ba.stringOfList(output);
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


	// examples of how to use GetTags and GetTagsBySFT
	public static void main(String args[]) throws TimeoutException {
		System.out.println(GetTags("<<s><<>><f><t"));
		System.out.println(GetTags("<a<a>"));

		System.out.println(GetTagsBySFT("<<s><<>><f><t")); // much slower, please be patient
		System.out.println(GetTagsBySFT("<a<a>"));
	}

	@Test
	public void test() throws Exception {
		List<String> inputs = new ArrayList<String>();
		// two examples given on paper
		inputs.add("<<s><<>><f><t");
		inputs.add("<a<a>");
		// more examples created by myself
		inputs.add("<a>");
		inputs.add("<a");
		inputs.add("<<>");
		inputs.add("<><");
		inputs.add("><");
		inputs.add("advs");
		inputs.add("assadvd<><>");
		inputs.add("< >");
		inputs.add("<!@#$%^&*()>");
		inputs.add("<<<>>>");
		inputs.add("<<>>>>>>");
		inputs.add("<<<<<<<<>>");

		for (String input: inputs)
			assertEquals(GetTags(input), GetTagsBySFT(input));

	}
}
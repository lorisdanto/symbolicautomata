package test.SFT;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

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

	private static SFT<CharPred, CharFunc, Character> MkGetTagsSFT(){
		List<SFTMove<CharPred, CharFunc, Character>> transitions = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
		CharPred notLessThan = ba.MkOr(new CharPred(CharPred.MIN_CHAR, (char)('<' - 1)), new CharPred((char)('<' + 1), CharPred.MAX_CHAR));
		CharPred notGreaterThan = ba.MkOr(new CharPred(CharPred.MIN_CHAR, (char)('>' - 1)), new CharPred((char)('>' + 1), CharPred.MAX_CHAR));

		List<CharFunc> output00 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, notLessThan, output00));

		List<CharFunc> output01 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 1, new CharPred('<'), output01));

		List<CharFunc> output11 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('<'), output11));

		List<CharFunc> output12 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, notLessThan, output12));

		List<CharFunc> output13 = new ArrayList<CharFunc>();
		output13.add(new CharConstant('<'));
		output13.add(CharOffset.IDENTITY);
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 3, notLessThan, output13));

		List<CharFunc> output20 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 0, ba.MkAnd(notLessThan, notGreaterThan), output20));

		List<CharFunc> output21 = new ArrayList<CharFunc>();
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('<'), output21));

		List<CharFunc> output30 = new ArrayList<CharFunc>();
		output30.add(CharOffset.IDENTITY);
		transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(3, 0, new CharPred('>'), output30));

		List<Integer> finStates = new ArrayList<Integer>();
		finStates.add(0);
		finStates.add(1);
		finStates.add(2);

		return SFT.MkSFT(transitions, 0, finStates, ba);
	}


	/**
	 * the implementation of the SFT in left column on page 4 on the paper named after Symbolic Finite State Transducers:
	 * Algorithms And Applications
	 */
	public static String GetTagsBySFT(String input) {
		// if the required SFT has not been created yet, then generate it. Otherwise we could just use it directly
		// instead of generating it every time GetTagsBySFT is called, which is quite time consuming.
		if (sft == null)
			sft = MkGetTagsSFT();

		List<Character> output = new ArrayList<Character>();
		try {
			List<Character> actualInput = stringToListOfCharacter(input);
			int a = 1 + 1;
			output = sft.outpzutOn(actualInput, ba);

			//output = sft.outpzutOn(stringToListOfCharacter(input), ba);
		} catch (TimeoutException te) {
			te.printStackTrace();
		}

		return listOfCharacterToString(output);
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
	 * convert a list of char into a string
	 * @param input a list of characters
	 * @return a list of class Character
	 */
	private static String listOfCharacterToString(List<Character> input) {
		StringBuilder output = new StringBuilder();
		for (char character: input)
			output.append(character);
		return output.toString();
	}

	// examples of how to use GetTags and GetTagsBySFT
	public static void main(String args[]) {
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
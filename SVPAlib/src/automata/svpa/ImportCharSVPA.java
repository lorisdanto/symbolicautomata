package automata.svpa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.sat4j.specs.TimeoutException;

import com.google.common.collect.ImmutableList;

import automata.AutomataException;
import theory.characters.BinaryCharPred;
import theory.characters.CharPred;
import theory.characters.ICharPred;
import theory.intervals.EqualitySolver;
import utilities.Pair;


public class ImportCharSVPA {

	private static final Pattern firstLinePattern = Pattern.compile("^Automaton:\\s+(?<transitions>\\d+)\\s+transitions,\\s+(?<states>\\d+)\\s+states\\s*$");
	private static final Pattern transitionPattern = Pattern.compile("^(?<type>[IRC]):\\s+(?<from>\\d+)\\s+\\-(?<pred>.*)\\-\\>\\s+(?<to>\\d+)\\s*$");

	private static final Pattern stackStatePattern = Pattern.compile("^(?<rest>.*),\\s+(?<state>\\d+)$");
	private static final Pattern charPattern = Pattern.compile("(\\\\u\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit}|\\\\.|.)");
	private static final Pattern binaryPattern = Pattern.compile("^neq\\s+(?<neq>.*),\\s+eq\\s+(?<eq>.*)$");
	private static final Pattern neqPattern = Pattern.compile("\\((?<first>.*?), (?<second>.*?)\\)");

	private static final Map<String, Character> reEscapeMap = new HashMap<String, Character>();
	static {
		reEscapeMap.put("\\-", '-');
		reEscapeMap.put("\\(", '(');
		reEscapeMap.put("\\)", ')');
		reEscapeMap.put("\\[", '[');
		reEscapeMap.put("\\]", ']');
		reEscapeMap.put("\\t", '\t');
		reEscapeMap.put("\\b", '\b');
		reEscapeMap.put("\\n", '\n');
		reEscapeMap.put("\\r", '\r');
		reEscapeMap.put("\\f", '\f');
		reEscapeMap.put("\\\'", '\'');
		reEscapeMap.put("\\\"", '\"');
		reEscapeMap.put("\\\\", '\\');
	}

	/**
	 * Builds an appropriate SVPA based on data imported from a string.
	 * The string should contain the result of a call to VPAutomaton.toString().
	 * @param inStr the string containing the data to import
	 * @return the created SVPA
	 */
	public static SVPA<ICharPred, Character> importSVPA(String inStr) throws AutomataException, IOException {
		try (Scanner input = new Scanner(inStr)) {
			return importSVPA(input);
		} catch (IOException e) {
			System.err.println("Error while reading SVPA input string");
			throw e;
		}
	}

	/**
	 * Builds an appropriate SVPA based on data imported from the specified file.
	 * The file should contain the result of a call to VPAutomaton.toString().
	 * @param inFile the file containing the data to import
	 * @return the created SVPA
	 */
	public static SVPA<ICharPred, Character> importSVPA(File inFile) throws AutomataException, IOException {
		try (Scanner input = new Scanner(inFile)) {
			return importSVPA(input);
		} catch (IOException e) {
			System.err.println("Invalid file or error while reading file");
			throw e;
		}
	}

	/**
	 * Builds an appropriate SVPA based on data imported from the specified file.
	 * The file should contain the result of a call to VPAutomaton.toString().
	 * @param inFile the file containing the data to import
	 * @return the created SVPA
	 */
	private static SVPA<ICharPred, Character> importSVPA(Scanner input) throws AutomataException, IOException {
		Collection<SVPAMove<ICharPred, Character>> transitions =
				new LinkedList<SVPAMove<ICharPred, Character>>();
		Collection<Integer> initialStates = new LinkedList<Integer>();
		Collection<Integer> finalStates = new LinkedList<Integer>();

		// the first line tells us how many transitions and states we should
		// expect
		String firstLine = getFirstNonEmptyLine(input);
		if (firstLine == null)
			throw new AutomataException("No lines in provided file");
		Matcher firstMatcher = firstLinePattern.matcher(firstLine);
		if(!firstMatcher.find())
			throw new AutomataException("Invalid first line encountered");
		int transitionsCount = -1;
		//int statesCount = -1;
		try {
			transitionsCount = Integer.parseInt(firstMatcher.group("transitions"));
			//statesCount = Integer.parseInt(firstMatcher.group("states"));
		} catch (NumberFormatException e) {
			throw new AutomataException("Invalid number of states or transitions " +
					"for line:\n" + firstLine);
		}

		// read transitions
		if (!getFirstNonEmptyLine(input).equals("Transitions"))
			throw new AutomataException("Expected 'Transitions' line");
		while (true) {
			String line = getFirstNonEmptyLine(input);
			if (line == null)
				throw new AutomataException("Unexpected end of input");
			if (line.equals("Initial States"))
				break;

			transitions.add(handleTransitionLine(line));
		}
		if (transitions.size() != transitionsCount)
			throw new AutomataException("Mismatch in imported SVPA.  Expected " +
					"number of transitions (" + transitionsCount + ") does not match " +
					"actual number of transitions (" + transitions.size() + ")");

		// read initial states
		while (true) {
			String line = getFirstNonEmptyLine(input);
			if (line == null)
				throw new AutomataException("Unexpected end of input");
			if (line.equals("Final States"))
				break;

			try {
				initialStates.add(Integer.parseInt(line));
			} catch (NumberFormatException e) {
				throw new AutomataException("Invalid initial state integer: " + line);
			}
		}

		// read final states
		while (true) {
			String line = getFirstNonEmptyLine(input);
			if (line == null)
				break;

			try {
				finalStates.add(Integer.parseInt(line));
			} catch (NumberFormatException e) {
				throw new AutomataException("Invalid final state integer: " + line);
			}
		}

		try {
			return SVPA.MkSVPA(transitions, initialStates,
					finalStates, new EqualitySolver());
		}
		catch (AutomataException e) {
			System.err.println("Unable to create imported SVPA");
			e.printStackTrace();
			System.exit(1);
		} catch (TimeoutException e) {
			System.out.println("Timeout in SVPA creation");
			e.printStackTrace();
		}

		// unreachable
		throw new AutomataException("SVPA creation failed in a mysterious " +
			                          "manner.  This should be unreachable.");
	}

	/**
	 * Utility method to get the first non-empty line from the specified Scanner
	 * object.
	 * @param input the input stream
	 * @return the first non-empty line, or null if end-of-input is reached
	 */
	private static String getFirstNonEmptyLine(Scanner input) {
		while (input.hasNextLine()) {
			String line = input.nextLine().trim();
			if (!line.isEmpty())
				return line;
		}

		return null;
	}

	/**
	 * Process a single transition with a CharPred or BinaryCharPred.
	 * @param line the line containing the transitions data as produced by
	 *             toString()
	 * @param transitions the collection
	 */
	private static SVPAMove<ICharPred, Character> handleTransitionLine(String line) throws AutomataException {
		Matcher transitionMatcher = transitionPattern.matcher(line);
		if(!transitionMatcher.find())
			throw new AutomataException("Invalid transition line encountered");

		int fromState = -1;
		int toState = -1;
		try {
			fromState = Integer.parseInt(transitionMatcher.group("from"));
			toState = Integer.parseInt(transitionMatcher.group("to"));
		} catch (NumberFormatException e) {
			throw new AutomataException("Invalid 'from' or 'to' state for " +
					"transition line:\n" + line);
		}

		String predicateString = transitionMatcher.group("pred");
		String transitionType = transitionMatcher.group("type");
		switch (transitionType) {
		case "I": {
			return new Internal<ICharPred, Character>(fromState, toState,
					parseCharPredicate(predicateString));
		}

		case "C": {
			Pair<Integer, String> splitData = stackStateFromPred(predicateString);
			int stackState = splitData.first;
			String rest = splitData.second;

			return new Call<ICharPred, Character>(fromState, toState, stackState,
					parseCharPredicate(rest));
		}

		case "R": {
			Pair<Integer, String> splitData = stackStateFromPred(predicateString);
			int stackState = splitData.first;
			String rest = splitData.second;

			ICharPred predicate = null;
			try {
				// try to parse the string as a unary char predicate
				predicate = parseCharPredicate(rest);
				predicate.setAsReturn();
			} catch (AutomataException eUnary) {
				// otherwise, try to parse as a binary char predicate
				try {
					predicate = parseBinaryCharPredicate(rest);
				} catch (AutomataException eBinary) {
					throw new AutomataException("Unable to parse predicate as either " +
							"unary or binary.\n" +
							"Unary error: " + eUnary.getMessage() + "\n" +
							"Binary error: " + eBinary.getMessage());
				}
			}

			return new Return<ICharPred, Character>(fromState, toState, stackState,
					predicate);
		}

		default:
			throw new AutomataException("Invalid transitions type.  Expected " +
					"'I', 'C', or 'R'; got '" + transitionType + "'");
		}

	}

	/**
	 * Parse the predicate string as a binary CharPred.
	 * @param predicate the predicate
	 * @return the generated BinaryCharPred object based on parsed data
	 * @throws AutomataException if the pedicate is not a valid representation of
	 *                           a BinaryCharPred
	 */
	private static BinaryCharPred parseBinaryCharPredicate(String predicate) throws AutomataException {
		Matcher binaryMatcher = binaryPattern.matcher(predicate);
		if (!binaryMatcher.find())
			throw new AutomataException("Inappropriate format for binary " +
					"predicate '" + predicate + "'");
		String neqPart = binaryMatcher.group("neq");
		String eqPart = binaryMatcher.group("eq");

		// the "equal" part is just a unary char predicate
		CharPred eqPred = parseCharPredicate(eqPart);

		// the "not equal" part is a list of pairs of unary char predicates
		ArrayList<Pair<CharPred,CharPred>> neqPred =
				new ArrayList<Pair<CharPred,CharPred>>();
		if (neqPart.charAt(0) != '[' || neqPart.charAt(neqPart.length() - 1) != ']')
			throw new AutomataException("Invalid neq predicate for binary char " +
					"predicate: '" + predicate + "'");
		if (neqPart.length() == 2) {
			// empty neq part can be empty
			// OK
		} else {
			neqPart = neqPart.substring(1, neqPart.length()-1);
			Matcher neqMatcher = neqPattern.matcher(neqPart);
			while (neqMatcher.find()) {
				CharPred firstPred = parseCharPredicate(neqMatcher.group("first"));
				CharPred secondPred = parseCharPredicate(neqMatcher.group("second"));
				neqPred.add(new Pair<CharPred, CharPred>(firstPred, secondPred));
			}
			if (neqPred.size() < 1)
				throw new AutomataException("Invalid empty neq predicate");
		}

		return new BinaryCharPred(eqPred, neqPred);
		//return new BinaryCharPred(new CharPred(CharPred.MIN_CHAR, CharPred.MAX_CHAR, true), false);
	}

	/**
	 * Parse the predicate string as a unary CharPred.
	 * @param predicate the predicate
	 * @return the generated CharPred object based on parsed data
	 * @throws AutomataException if the pedicate is not a valid representation of
	 *                           a CharPred
	 */
	private static CharPred parseCharPredicate(String predicate) throws AutomataException {
		LinkedList<ImmutablePair<Character, Character>> intervals =
				new LinkedList<ImmutablePair<Character, Character>>();

		if (predicate.charAt(0) != '[' || predicate.charAt(predicate.length() - 1) != ']')
			throw new AutomataException("Invalid char predicate: '" + predicate + "'");
		predicate = predicate.substring(1, predicate.length()-1);

		Matcher charMatcher = charPattern.matcher(predicate);
		Character previousChar = null;
		boolean pairNextChar = false;
		while (charMatcher.find()) {
			assert charMatcher.groupCount() == 1;
			String matchedChar = charMatcher.group();
			if (matchedChar.equals("-")) {
				if (previousChar == null)
					throw new AutomataException("Incomplete character range in " +
							"predicate '" + predicate + "' (missing range begin)");
				pairNextChar = true;
			} else {
				// turn matched character value into a char
				Character thisChar = null;
				if (reEscapeMap.containsKey(matchedChar)) {
					thisChar = reEscapeMap.get(matchedChar);
				} else if (matchedChar.charAt(0) == '\\' &&
						matchedChar.charAt(1) == 'u' &&
						matchedChar.length() == 6) {
					try {
						int charVal = Integer.parseInt(matchedChar.substring(2), 16);
						thisChar = (char)charVal;
					} catch (NumberFormatException e) {
						throw new AutomataException("Invalid char unicode value '" +
								matchedChar + "'");
					}
				} else {
					if (matchedChar.length() != 1)
						throw new AutomataException("Unexpected bad char extracted from " +
								"predicate: '" + matchedChar + "'");
					thisChar = matchedChar.charAt(0);
				}

				// add the appropriate character range to the intevals (based on whether
				// a character range is currently in progress via "-" or not)
				if (pairNextChar) {
					intervals.add(ImmutablePair.of(previousChar, thisChar));
					previousChar = null;
					pairNextChar = false;
				} else {
					intervals.add(ImmutablePair.of(thisChar, thisChar));
					previousChar = thisChar;
				}
			}
		}

		// the predicate begins a range of characters (with a character followed
		// by "-") but never finishes it
		if (pairNextChar)
			throw new AutomataException("Incomplete character range in predicate '" +
					predicate + "' (missing range end)");

		return new CharPred(ImmutableList.copyOf(intervals));
	}

	/**
	 * Extracts the expected stack state from the predicate string.
	 * @param predicate the predicate data, as a String
	 * @return a Pair of (the stack state, the rest) where the stack state is an
	 *         integer
	 * @throws AutomataException if predicate does not contain a valid stack state
	 */
	private static Pair<Integer, String> stackStateFromPred(String predicate) throws AutomataException {
		Matcher stackStateMatcher = stackStatePattern.matcher(predicate);
		if (!stackStateMatcher.find())
			throw new AutomataException("No stack state found in predicate '" +
					predicate + "'");

		String stackState = stackStateMatcher.group("state");
		String theRest = stackStateMatcher.group("rest");
		try {
			return new Pair<Integer, String>(Integer.parseInt(stackState), theRest);
		} catch (NumberFormatException e) {
			// fall-through
		}

		throw new AutomataException("Invalid stack state '" + stackState + "'");
	}

}

/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utilities.Pair;

/**
 * CharPred: a set of characters represented as contiguous intervals
 */
public class CharPred {
	public ArrayList<Pair<Character, Character>> intervals;

	/**
	 * The empty set
	 */
	public CharPred() {
		intervals = new ArrayList<Pair<Character, Character>>();
	}

	/**
	 * The set containing only the character <code>c</code>
	 */
	public CharPred(Character c) {
		intervals = new ArrayList<Pair<Character, Character>>();
		intervals.add(new Pair<Character, Character>(c, c));
	}

	/**
	 * The set containing only the interval <code>[bot,top]</code> (extremes
	 * included)
	 */
	public CharPred(Character bot, Character top) {
		intervals = new ArrayList<Pair<Character, Character>>();
		if (bot <= top) {
			intervals.add(new Pair<Character, Character>(bot, top));
		}
	}

	/**
	 * The set containing all intervals (the intervals must arrive in order and
	 * must not overlap)
	 */
	public CharPred(ArrayList<Pair<Character, Character>> intervals) {
		for (Pair<Character, Character> interval : intervals) {
			if (interval.first > interval.second)
				throw new IllegalArgumentException(String.format(
						"Illegal interval [%s-%s]", interval.first,
						interval.second));
		}

		for (int i = 1; i < intervals.size(); i++) {
			Pair<Character, Character> curr = intervals.get(i);
			Pair<Character, Character> prev = intervals.get(i - 1);
			if (prev.second > curr.first)
				throw new IllegalArgumentException(
						"The intervals are not correctly ordered");
			if (prev.second >= curr.first)
				throw new IllegalArgumentException("The intervals are adjacent");
		}

		this.intervals = intervals;
	}

	/**
	 * @return the set [A-Z]
	 */
	public final static CharPred upperAlpha() {
		return new CharPred('A', 'Z');
	}

	/**
	 * @return the set [a-z]
	 */
	public final static CharPred lowerAlpha() {
		return new CharPred('a', 'z');
	}

	/**
	 * @return the set [A-Za-z]
	 */
	public final static CharPred alpha() {
		ArrayList<Pair<Character, Character>> intervals = new ArrayList<Pair<Character, Character>>();
		intervals.add(new Pair<Character, Character>('A', 'Z'));
		intervals.add(new Pair<Character, Character>('a', 'z'));
		return new CharPred(intervals);
	}
	
	/**
	 * @return the set [0-9A-Za-z]
	 */
	public final static CharPred alphaNum() {
		ArrayList<Pair<Character, Character>> intervals = new ArrayList<Pair<Character, Character>>();
		intervals.add(new Pair<Character, Character>('0', '9'));
		intervals.add(new Pair<Character, Character>('A', 'Z'));
		intervals.add(new Pair<Character, Character>('a', 'z'));
		return new CharPred(intervals);
	}

	/**
	 * @return the set [0-9]
	 */
	public final static CharPred num() {
		return new CharPred('0', '9');
	}
	
	/**
	 * @return the set [\\t-\\r\\s]
	 */
	public final static CharPred spaces() {
		ArrayList<Pair<Character, Character>> intervals = new ArrayList<Pair<Character, Character>>();
		intervals.add(new Pair<Character, Character>('\t', '\r'));
		intervals.add(new Pair<Character, Character>(' ', ' '));
		return new CharPred(intervals);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Pair<Character, Character> pair : intervals) {
			if (pair.first == pair.second)
				sb.append(printChar(pair.first));
			else {
				sb.append(printChar(pair.first));
				sb.append("-");
				sb.append(printChar(pair.second));
			}
		}
		sb.append("]");

		return sb.toString();
	}

	// Only prints readable chars, otherwise print unicode
	public static String printChar(char c) {
		Map<Character, String> unescapeMap = new HashMap<Character, String>();
		unescapeMap.put('\t', "\\t");
		unescapeMap.put('\b', "\\b");
		unescapeMap.put('\n', "\\n");
		unescapeMap.put('\r', "\\r");
		unescapeMap.put('\f', "\\f");
		unescapeMap.put('\'', "\\\'");
		unescapeMap.put('\"', "\\\"");
		if (unescapeMap.containsKey(c)) {
			return unescapeMap.get(c);
		} else if (c < 0x20 || c > 0x7f) {
			return String.format("\\u%04x", (int) c);
		} else {
			return Character.toString(c);
		}
	}

}

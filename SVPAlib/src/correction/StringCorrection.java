package correction;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.Move;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class StringCorrection {
	// save computed p, l, v, f values for later use
	private static HashMap<String, Pair> pStorage = null;
	private static HashMap<String, Boolean> lStorage = null;
	private static HashMap<String, Pair> vStorage = null;
	private static HashMap<String, Pair> fStorage = null;
	// let -1 represent positive infinity
	private static final int INFINITY = -1;

	/**
	 * Find the string which is accepted by given FSA and has lowest edit
	 * distance to the input string
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result string that is accepted by SFA
	 */
	public static String getCorrectString(SFA<CharPred, Character> inpSFA, String inpStr) {
		Pair p = getCorrectPair(inpSFA, inpStr);
		LinkedList<CharPred> l = p.charSet;
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		return ba.stringOfListOfCharPred(l);
	}

	/**
	 * Find the lowest edit distance from input string to a string that is
	 * accepted by given FSA
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result edit distance
	 */
	public static int computeEditDistance(SFA<CharPred, Character> inpSFA, String inpStr) {
		Pair p = getCorrectPair(inpSFA, inpStr);
		return p.editDistance;
	}

	/**
	 * Find the lowest edit distance from input string to a string that is
	 * accepted by given FSA
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result string represented by a linked list of char predicates
	 */
	public static LinkedList<CharPred> getCorrectCharPredList(SFA<CharPred, Character> inpSFA, String inpStr) {
		Pair p = getCorrectPair(inpSFA, inpStr);
		return p.charSet;
	}

	/**
	 * Find the string which is accepted by given FSA and has lowest edit
	 * distance to the input string
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result string represented by a linked list of char predicates
	 */
	private static Pair getCorrectPair(SFA<CharPred, Character> inpSFA, String inpStr) {
		pStorage = new HashMap<String, Pair>();
		lStorage = new HashMap<String, Boolean>();
		fStorage = new HashMap<String, Pair>();
		vStorage = new HashMap<String, Pair>();
		Pair p = new Pair(INFINITY, null);
		for (Integer i : inpSFA.getFinalStates()) {
			Pair termF = F(inpStr.length(), i, inpSFA, inpStr);
			if (lt(termF.editDistance, p.editDistance)) {
				p.editDistance = termF.editDistance;
				p.charSet = termF.charSet;
			}
		}
		return p;
	}

	/**
	 * F refers to the lowest number of edit operations needed to force FSA to
	 * goal state, given the first j characters of the input string
	 * 
	 * @param j
	 *            number of first characters of input string
	 * @param S
	 *            goal state
	 * @param templ
	 *            given SFA
	 * @param inpStr
	 *            input string
	 * @return lowest number of edit operations and corresponding string segment
	 */
	private static Pair F(int j, int S, SFA<CharPred, Character> templ, String inpStr) {
		String lookUp = String.format("%d,%d", j, S);
		if (fStorage.containsKey(lookUp)) {
			return fStorage.get(lookUp);
		}
		if (j == 0) {
			if (S == templ.getInitialState()) {
				Pair result = new Pair(0, new LinkedList<CharPred>());
				fStorage.put(lookUp, result);
				return result;
			} else {
				Pair result = new Pair(INFINITY, new LinkedList<CharPred>());
				fStorage.put(lookUp, result);
				return result;
			}
		}
		Pair minCost = new Pair(INFINITY, new LinkedList<CharPred>());
		for (Integer i : templ.getStates()) {
			Pair termF = F(j - 1, i, templ, inpStr);
			Pair termV = V(i, S, inpStr.charAt(j - 1), templ);
			int newCost = add(termF.editDistance, termV.editDistance);
			if (lt(newCost, minCost.editDistance)) {
				minCost.editDistance = newCost;
				LinkedList<CharPred> l = new LinkedList<CharPred>();
				l.addAll(termF.charSet);
				l.addAll(termV.charSet);
				minCost.charSet = l;
			}
		}
		fStorage.put(lookUp, minCost);
		return minCost;
	}

	/**
	 * V refers to the lowest number of edit operations needed to change
	 * character c into a string beta which will force the FSA from state T to
	 * state S
	 * 
	 * @param T
	 *            origin state
	 * @param S
	 *            destination state
	 * @param c
	 *            a single character
	 * @param templ
	 *            given SFA
	 * @return lowest number of edit operations and corresponding string beta
	 */
	private static Pair V(int T, int S, Character c, SFA<CharPred, Character> templ) {
		String lookUp = String.format("%d,%d,%c", T, S, c);
		if (vStorage.containsKey(lookUp)) {
			return vStorage.get(lookUp);
		}
		Pair pResult = P(templ.stateCount() - 1, T, S, templ);
		Pair p = new Pair(pResult.editDistance, (LinkedList<CharPred>) pResult.charSet.clone());
		if (p.editDistance == 0) {
			if (T == S) {
				Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
				for (Move<CharPred, Character> q : arcs) {
					if (q.to == S) {
						SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
						if (curr.guard.isSatisfiedBy(c)) {
							LinkedList<CharPred> l = new LinkedList<>();
							l.add(new CharPred(c));
							Pair temp = new Pair(0, l);
							vStorage.put(lookUp, temp);
							return temp;
						}
					}
				}
			}
			Pair temp = new Pair(1, new LinkedList<CharPred>());
			vStorage.put(lookUp, temp);
			return temp;
		} else {
			Pair term = p;
			boolean lRes = L(templ.stateCount() - 1, T, S, c, templ);
			if (lRes) {
				for (int j = 0; j < term.charSet.size(); j++) {
					if (term.charSet.get(j).isSatisfiedBy(c)) {
						term.charSet.set(j, new CharPred(c));
						break;
					}
				}
			}
			int i = lRes ? 1 : 0;
			term.editDistance = sub(term.editDistance, i);
			vStorage.put(lookUp, term);
			return term;
		}
	}

	/**
	 * P refers to the length of the shortest string which force the FSA from
	 * state T to state S, passing only through states numbered k or less
	 * 
	 * @param k
	 *            max state passed
	 * @param T
	 *            origin state
	 * @param S
	 *            destination state
	 * @param templ
	 *            given SFA
	 * @return edit distance P and corresponding string segment
	 */
	private static Pair P(int k, int T, int S, SFA<CharPred, Character> templ) {
		String lookUp = String.format("%d,%d,%d", k, T, S);
		if (pStorage.containsKey(lookUp)) {
			return pStorage.get(lookUp);
		}
		if (T == S) {
			Pair res = new Pair(0, new LinkedList<CharPred>());
			pStorage.put(lookUp, res);
			return res;
		}

		if (k == 0) {
			Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
			for (Move<CharPred, Character> q : arcs) {
				if (q.to == S) {
					SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
					LinkedList<CharPred> l = new LinkedList<CharPred>();
					l.add(curr.guard);
					Pair res = new Pair(1, l);
					pStorage.put(lookUp, res);
					return res;
				}
			}
			return new Pair(INFINITY, new LinkedList<CharPred>());
		}
		Pair term1 = P(k - 1, T, S, templ);
		Pair term2 = P(k - 1, T, k, templ);
		Pair term3 = P(k - 1, k, S, templ);
		Pair result;
		if (le(term1.editDistance, add(term2.editDistance, term3.editDistance))) {
			result = new Pair(term1.editDistance, term1.charSet);
		} else {
			LinkedList<CharPred> l = new LinkedList<CharPred>();
			l.addAll(term2.charSet);
			l.addAll(term3.charSet);
			result = new Pair(add(term2.editDistance, term3.editDistance), l);
		}
		pStorage.put(lookUp, result);
		return result;
	}

	/**
	 * L indicates whether or not character c is accepted by some arc along a
	 * path of shortest length from T to S, passing only through states numbered
	 * k or less
	 * 
	 * @param k
	 *            max state passed
	 * @param T
	 *            origin state
	 * @param S
	 *            destination state
	 * @param c
	 *            a single character
	 * @param templ
	 *            given SFA
	 * @return truth of indicator L
	 */
	private static boolean L(int k, int T, int S, Character c, SFA<CharPred, Character> templ) {
		String lookUp = String.format("%d,%d,%d,%c", k, T, S, c);
		if (lStorage.containsKey(lookUp)) {
			return lStorage.get(lookUp);
		}
		if (k == 0) {
			Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
			for (Move<CharPred, Character> q : arcs) {
				if (q.to == S) {
					SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
					if (curr.guard.isSatisfiedBy(c)) {
						lStorage.put(lookUp, true);
						return true;
					}
				}
			}
			lStorage.put(lookUp, false);
			return false;
		}
		boolean result;
		Pair term1 = P(k - 1, T, S, templ);
		Pair term2 = P(k - 1, T, k, templ);
		Pair term3 = P(k - 1, k, S, templ);
		if (gt(term1.editDistance, add(term2.editDistance, term3.editDistance))) {
			result = L(k - 1, T, k, c, templ) || L(k - 1, k, S, c, templ);
		} else if (eq(term1.editDistance, add(term2.editDistance, term3.editDistance))) {
			result = L(k - 1, T, k, c, templ) || L(k - 1, k, S, c, templ) || L(k - 1, T, S, c, templ);
		} else {
			result = L(k - 1, T, S, c, templ);
		}
		lStorage.put(lookUp, result);
		return result;
	}

	/**
	 * wrapper functions to deal with infinity calculation and comparison
	 */
	private static int add(int a, int b) {
		if (a == INFINITY || b == INFINITY) {
			return INFINITY;
		} else {
			return a + b;
		}
	}

	private static int sub(int a, int b) {
		if (a == INFINITY && b != INFINITY)
			return INFINITY;
		else
			return a - b;
	}

	private static boolean lt(int a, int b) {
		if (a == INFINITY)
			a = Integer.MAX_VALUE;
		if (b == INFINITY)
			b = Integer.MAX_VALUE;
		return a < b;
	}

	private static boolean gt(int a, int b) {
		if (a == INFINITY)
			a = Integer.MAX_VALUE;
		if (b == INFINITY)
			b = Integer.MAX_VALUE;
		return a > b;
	}

	private static boolean eq(int a, int b) {
		return a == b;
	}

	private static boolean le(int a, int b) {
		if (a == INFINITY)
			a = Integer.MAX_VALUE;
		if (b == INFINITY)
			b = Integer.MAX_VALUE;
		return a <= b;
	}

}

/**
 * This is a wrapper class. It contains a double variable representing edit
 * distance and a linked list representing a string segment
 */
class Pair {
	protected int editDistance;
	protected LinkedList<CharPred> charSet;

	protected Pair(int i, LinkedList<CharPred> s) {
		editDistance = i;
		charSet = s;
	}
}

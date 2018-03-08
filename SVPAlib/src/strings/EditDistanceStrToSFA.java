package strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.Move;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Pair;
import utilities.IntegerPair;
import utilities.Triple;
import utilities.Quadruple;
/**
 * This class helps to find the shortest edit distance from a given string to
 * an SFA and the corresponding string accepted by SFA. 
 */
public class EditDistanceStrToSFA {
	// save computed p, l, v, f values for later use
	private static HashMap<Triple<Integer, Integer, Integer>, Pair<Integer, LinkedList<CharPred>>> pStorage = null;
	private static HashMap<Quadruple<Integer, Integer, Integer, Character>, Boolean> lStorage = null;
	private static HashMap<Triple<Integer, Integer, Character>, Pair<Integer, LinkedList<CharPred>>> vStorage = null;
	private static HashMap<IntegerPair, Pair<Integer, LinkedList<CharPred>>> fStorage = null;
	// let -1 represent positive infinity
	private static final int INFINITY = -1;

	/**
	 * Find the string which is accepted by given FSA and has lowest edit distance
	 * to the input string
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result string that is accepted by SFA
	 */
	public static String getCorrectString(SFA<CharPred, Character> inpSFA, String inpStr) {
		Pair<Integer, LinkedList<CharPred>> p = getCorrectPair(inpSFA, inpStr);
		LinkedList<CharPred> l = p.second;
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		return ba.stringOfListOfCharPred(l);
	}

	/**
	 * Find the lowest edit distance from input string to a string that is accepted
	 * by given FSA
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result edit distance
	 */
	public static int computeShortestEditDistance(SFA<CharPred, Character> inpSFA, String inpStr) {
		Pair<Integer, LinkedList<CharPred>> p = getCorrectPair(inpSFA, inpStr);
		return p.first;
	}

	/**
	 * Find the lowest edit distance from input string to a string that is accepted
	 * by given FSA
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result string represented by a linked list of char predicates
	 */
	public static LinkedList<CharPred> getCorrectCharPredList(SFA<CharPred, Character> inpSFA, String inpStr) {
		Pair<Integer, LinkedList<CharPred>> p = getCorrectPair(inpSFA, inpStr);
		return p.second;
	}

	/**
	 * Find the string which is accepted by given FSA and has lowest edit distance
	 * to the input string
	 * 
	 * @param inpSFA
	 *            given symbolic finite automata
	 * @param inpStr
	 *            input string
	 * @return result string represented by a linked list of char predicates
	 */
	private static Pair<Integer, LinkedList<CharPred>> getCorrectPair(SFA<CharPred, Character> inpSFA, String inpStr) {
		pStorage = new HashMap<Triple<Integer, Integer, Integer>, Pair<Integer, LinkedList<CharPred>>>();
		lStorage = new HashMap<Quadruple<Integer, Integer, Integer, Character>, Boolean>();
		fStorage = new HashMap<IntegerPair, Pair<Integer, LinkedList<CharPred>>>();
		vStorage = new HashMap<Triple<Integer, Integer, Character>, Pair<Integer, LinkedList<CharPred>>>();
		Pair<Integer, LinkedList<CharPred>> p = new Pair<>(INFINITY, null);
		for (Integer i : inpSFA.getFinalStates()) {
			Pair<Integer, LinkedList<CharPred>> termF = subStrToS(inpStr.length(), i, inpSFA, inpStr);
			if (lt(termF.first, p.first)) {
				p.first = termF.first;
				p.second = termF.second;
			}
		}
		return p;
	}

	/**
	 * F refers to the lowest number of edit operations needed to force FSA to goal
	 * state, given the first j characters of the input string
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
	private static Pair<Integer, LinkedList<CharPred>> subStrToS(int j, int S, SFA<CharPred, Character> templ, String inpStr) {
		IntegerPair lookUp = new IntegerPair(j, S);
		if (fStorage.containsKey(lookUp)) {
			return fStorage.get(lookUp);
		}
		if (j == 0) {
			if (S == templ.getInitialState()) {
				Pair<Integer, LinkedList<CharPred>> result = new Pair<Integer, LinkedList<CharPred>>(0,
						new LinkedList<CharPred>());
				fStorage.put(lookUp, result);
				return result;
			} else {
				Pair<Integer, LinkedList<CharPred>> result = new Pair<Integer, LinkedList<CharPred>>(INFINITY,
						new LinkedList<CharPred>());
				fStorage.put(lookUp, result);
				return result;
			}
		}
		Pair<Integer, LinkedList<CharPred>> minCost = new Pair<Integer, LinkedList<CharPred>>(INFINITY,
				new LinkedList<CharPred>());
		for (Integer i : templ.getStates()) {
			Pair<Integer, LinkedList<CharPred>> termF = subStrToS(j - 1, i, templ, inpStr);
			Pair<Integer, LinkedList<CharPred>> termV = moveFromSToTGivenC(i, S, inpStr.charAt(j - 1), templ);
			int newCost = add(termF.first, termV.first);
			if (lt(newCost, minCost.first)) {
				minCost.first = newCost;
				LinkedList<CharPred> l = new LinkedList<CharPred>();
				l.addAll(termF.second);
				l.addAll(termV.second);
				minCost.second = l;
			}
		}
		fStorage.put(lookUp, minCost);
		return minCost;
	}

	/**
	 * V refers to the lowest number of edit operations needed to change character c
	 * into a string beta which will force the FSA from state T to state S
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
	private static Pair<Integer, LinkedList<CharPred>> moveFromSToTGivenC(int T, int S, Character c, SFA<CharPred, Character> templ) {
		Triple<Integer, Integer, Character> lookUp = new Triple<>(T, S, c);
		if (vStorage.containsKey(lookUp)) {
			return vStorage.get(lookUp);
		}
		Pair<Integer, LinkedList<CharPred>> pResult = shortestStrFromSToT(templ.stateCount() - 1, T, S, templ);
		Pair<Integer, LinkedList<CharPred>> p = new Pair<Integer, LinkedList<CharPred>>(pResult.first,
				(LinkedList<CharPred>) pResult.second.clone());
		if (p.first == 0) {
			if (T == S) {
				Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
				for (Move<CharPred, Character> q : arcs) {
					if (q.to == S) {
						SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
						if (curr.guard.isSatisfiedBy(c)) {
							LinkedList<CharPred> l = new LinkedList<>();
							l.add(new CharPred(c));
							Pair<Integer, LinkedList<CharPred>> temp = new Pair<Integer, LinkedList<CharPred>>(0, l);
							vStorage.put(lookUp, temp);
							return temp;
						}
					}
				}
			}
			Pair<Integer, LinkedList<CharPred>> temp = new Pair<Integer, LinkedList<CharPred>>(1,
					new LinkedList<CharPred>());
			vStorage.put(lookUp, temp);
			return temp;
		} else {
			Pair<Integer, LinkedList<CharPred>> term = p;
			boolean lRes = cContainedInShortedStrFromSToT(templ.stateCount() - 1, T, S, c, templ);
			if (lRes) {
				for (int j = 0; j < term.second.size(); j++) {
					if (term.second.get(j).isSatisfiedBy(c)) {
						term.second.set(j, new CharPred(c));
						break;
					}
				}
			}
			int i = lRes ? 1 : 0;
			term.first = sub(term.first, i);
			vStorage.put(lookUp, term);
			return term;
		}
	}

	/**
	 * P refers to the length of the shortest string which force the FSA from state
	 * T to state S, passing only through states numbered k or less
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
	private static Pair<Integer, LinkedList<CharPred>> shortestStrFromSToT(int k, int T, int S, SFA<CharPred, Character> templ) {
		Triple<Integer, Integer, Integer> lookUp = new Triple<>(k, T, S);
		if (pStorage.containsKey(lookUp)) {
			return pStorage.get(lookUp);
		}
		if (T == S) {
			Pair<Integer, LinkedList<CharPred>> res = new Pair<Integer, LinkedList<CharPred>>(0,
					new LinkedList<CharPred>());
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
					Pair<Integer, LinkedList<CharPred>> res = new Pair<Integer, LinkedList<CharPred>>(1, l);
					pStorage.put(lookUp, res);
					return res;
				}
			}
			return new Pair<Integer, LinkedList<CharPred>>(INFINITY, new LinkedList<CharPred>());
		}
		Pair<Integer, LinkedList<CharPred>> term1 = shortestStrFromSToT(k - 1, T, S, templ);
		Pair<Integer, LinkedList<CharPred>> term2 = shortestStrFromSToT(k - 1, T, k, templ);
		Pair<Integer, LinkedList<CharPred>> term3 = shortestStrFromSToT(k - 1, k, S, templ);
		Pair<Integer, LinkedList<CharPred>> result;
		if (le(term1.first, add(term2.first, term3.first))) {
			result = new Pair<Integer, LinkedList<CharPred>>(term1.first, term1.second);
		} else {
			LinkedList<CharPred> l = new LinkedList<CharPred>();
			l.addAll(term2.second);
			l.addAll(term3.second);
			result = new Pair<Integer, LinkedList<CharPred>>(add(term2.first, term3.first), l);
		}
		pStorage.put(lookUp, result);
		return result;
	}

	/**
	 * L indicates whether or not character c is accepted by some arc along a path
	 * of shortest length from T to S, passing only through states numbered k or
	 * less
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
	private static boolean cContainedInShortedStrFromSToT(int k, int T, int S, Character c,
			SFA<CharPred, Character> templ) {
		Quadruple<Integer, Integer, Integer, Character> lookUp = new Quadruple<>(k, T, S, c);
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
		Pair<Integer, LinkedList<CharPred>> term1 = shortestStrFromSToT(k - 1, T, S, templ);
		Pair<Integer, LinkedList<CharPred>> term2 = shortestStrFromSToT(k - 1, T, k, templ);
		Pair<Integer, LinkedList<CharPred>> term3 = shortestStrFromSToT(k - 1, k, S, templ);
		if (gt(term1.first, add(term2.first, term3.first))) {
			result = cContainedInShortedStrFromSToT(k - 1, T, k, c, templ)
					|| cContainedInShortedStrFromSToT(k - 1, k, S, c, templ);
		} else if (eq(term1.first, add(term2.first, term3.first))) {
			if (T == k || k == S) {
				result = cContainedInShortedStrFromSToT(k - 1, T, S, c, templ);
			} else {
				result = cContainedInShortedStrFromSToT(k - 1, T, k, c, templ)
						|| cContainedInShortedStrFromSToT(k - 1, k, S, c, templ)
						|| cContainedInShortedStrFromSToT(k - 1, T, S, c, templ);
			}
		} else {
			result = cContainedInShortedStrFromSToT(k - 1, T, S, c, templ);
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

package correction;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.Move;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import theory.characters.CharPred;

public class StringCorrection {
	private static SFA<CharPred, Character> templ = null;
	private static Collection<Integer> goalStates = null;
	private static Collection<Integer> allStates = null;
	private static String w = null;
	private static int numStates;
	private static HashMap<String, Pair> pStore = null;
	private static HashMap<String, Boolean> lStore = null;

	public static LinkedList<CharPred> getCorrectString(SFA<CharPred, Character> inpSFA, String inpStr) {
		w = inpStr;
		templ = inpSFA;
		numStates = inpSFA.stateCount();
		goalStates = inpSFA.getFinalStates();
		allStates = inpSFA.getStates();
		pStore = new HashMap<String, Pair>();
		lStore = new HashMap<String, Boolean>();

		double minDist = Double.POSITIVE_INFINITY;
		LinkedList<CharPred> resultStr = null;
		for (Integer i : goalStates) {
			Pair termF = F(w.length(), i);
			if (termF.editDistance < minDist) {
				minDist = termF.editDistance;
				resultStr = termF.charSet;
			}
		}
		System.out.println(minDist);
		for (CharPred cp : resultStr) {
			System.out.print(cp.toString());
		}
		return resultStr;
	}

	private static Pair F(int j, int S) {
		if (j == 0) {
			if (S == templ.getInitialState()) {
				return new Pair(0.0, new LinkedList<CharPred>());
			} else {
				return new Pair(Double.POSITIVE_INFINITY, new LinkedList<CharPred>());
			}
		}
		Pair minCost = new Pair(Double.POSITIVE_INFINITY, new LinkedList<CharPred>());
		for (Integer i : allStates) {
			Pair termF = F(j - 1, i);
			Pair termV = V(i, S, w.charAt(j - 1));
			double newCost = termF.editDistance + termV.editDistance;
			if (newCost < minCost.editDistance) {
				minCost.editDistance = newCost;
				LinkedList<CharPred> l = new LinkedList<CharPred>();
				l.addAll(termF.charSet);
				l.addAll(termV.charSet);
				minCost.charSet = l;
			}
		}
		return minCost;
	}

	private static Pair V(int T, int S, Character c) {
		Pair p = P(numStates - 1, T, S);
		if (p.editDistance == 0) {
			if (T == S) {
				Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
				for (Move<CharPred, Character> q : arcs) {
					if (q.to == S) {
						SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
						if (curr.guard.isSatisfiedBy(c)) {
							LinkedList<CharPred> l = new LinkedList<>();
							l.add(new CharPred(c));
							return new Pair(0.0, l);
						}
					}
				}
				return new Pair(1.0, new LinkedList<CharPred>());
			} else {
				return new Pair(1.0, new LinkedList<CharPred>());
			}
		} else {

			Pair term = p;
			if (L(numStates - 1, T, S, c)) {
				for (int j = 0; j < term.charSet.size(); j++) {
					if (term.charSet.get(j).isSatisfiedBy(c)) {
						term.charSet.set(j, new CharPred(c));
						break;
					}
				}
			}
			int i = L(numStates - 1, T, S, c) ? 1 : 0;
			term.editDistance -= i;
			return term;
		}
	}

	private static Pair P(int k, int T, int S) {
		String lookUp = String.format("%d$%d$%d", k, T, S);
		// if (pStore.containsKey(lookUp)) {
		// return pStore.get(lookUp);
		// }
		if (T == S)
			return new Pair(0.0, new LinkedList<CharPred>());
		if (k == 0) {
			Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
			for (Move<CharPred, Character> q : arcs) {
				if (q.to == S) {
					SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
					LinkedList<CharPred> l = new LinkedList<CharPred>();
					l.add(curr.guard);
					Pair res = new Pair(1.0, l);
					// pStore.put(lookUp, res);
					return res;
				}
			}
			return new Pair(Double.POSITIVE_INFINITY, new LinkedList<CharPred>());
		}
		Pair term1 = P(k - 1, T, S);
		Pair term2 = P(k - 1, T, k);
		Pair term3 = P(k - 1, k, S);
		Pair result;
		if (term1.editDistance <= term2.editDistance + term3.editDistance) {
			result = term1;
		} else {
			LinkedList<CharPred> l = new LinkedList<CharPred>();
			l.addAll(term2.charSet);
			l.addAll(term3.charSet);
			result = new Pair(term2.editDistance + term3.editDistance, l);
		}
		// pStore.put(lookUp, result);
		return result;
	}

	private static boolean L(int k, int T, int S, Character c) {
		String lookUp = String.format("%d$%d$%d$%c", k, T, S, c);
		// if (lStore.containsKey(lookUp)) {
		// return lStore.get(lookUp);
		// }
		if (k == 0) {
			Collection<Move<CharPred, Character>> arcs = templ.getMovesFrom(T);
			for (Move<CharPred, Character> q : arcs) {
				if (q.to == S) {
					SFAInputMove<CharPred, Character> curr = (SFAInputMove<CharPred, Character>) q;
					if (curr.guard.isSatisfiedBy(c)) {
						lStore.put(lookUp, true);
						return true;
					}
				}
			}
			lStore.put(lookUp, false);
			return false;
		}
		boolean result;
		Pair term1 = P(k - 1, T, S);
		Pair term2 = P(k - 1, T, k);
		Pair term3 = P(k - 1, k, S);
		if (term1.editDistance > term2.editDistance + term3.editDistance) {
			result = L(k - 1, T, k, c) || L(k - 1, k, S, c);
		} else if (term1.editDistance == term2.editDistance + term3.editDistance) {
			result = L(k - 1, T, k, c) || L(k - 1, k, S, c) || L(k - 1, T, S, c);
		} else {
			result = L(k - 1, T, S, c);
		}
		lStore.put(lookUp, result);
		return result;
	}

}

class Pair {
	protected double editDistance;
	protected LinkedList<CharPred> charSet;

	protected Pair(double i, LinkedList<CharPred> s) {
		editDistance = i;
		charSet = s;
	}
}

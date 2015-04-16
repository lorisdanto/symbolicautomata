package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import utilities.Pair;

public abstract class BooleanAlgebra<P, S> {

	public abstract P MkNot(P p);

	public abstract P MkOr(Collection<P> p1);

	public abstract P MkOr(P p1, P p2);

	public abstract P MkAnd(Collection<P> p1);

	public abstract P MkAnd(P p1, P p2);

	public abstract P True();

	public abstract P False();

	public abstract boolean AreEquivalent(P p1, P p2);

	public abstract boolean IsSatisfiable(P p1);

	public abstract boolean HasModel(P p1, S el);

	public abstract boolean HasModel(P p1, S el1, S el2);

	public abstract S generateWitness(P p1);

	public abstract Pair<S, S> generateWitnesses(P p1);

	/**
	 * Given a set of <code>predicates</code>, returns all the satisfiable
	 * Boolean combinations
	 * 
	 * @return a set of pairs (p,{i1,..,in}) where p is and ij is 0 or 1 base on
	 *         whether pij is used positively or negatively
	 */
	public Collection<Pair<P, ArrayList<Integer>>> GetMinterms(
			ArrayList<P> predicates) {
		return GetMinterms(predicates, True());
	}

	private Collection<Pair<P, ArrayList<Integer>>> GetMinterms(
			ArrayList<P> predicates, P startPred) {
		HashSet<Pair<P, ArrayList<Integer>>> minterms = new HashSet<Pair<P, ArrayList<Integer>>>();
		GetMintermsRec(predicates, 0, startPred, new ArrayList<Integer>(),
				minterms);
		return minterms;
	}

	private void GetMintermsRec(ArrayList<P> predicates, int n, P currPred,
			ArrayList<Integer> setBits,
			HashSet<Pair<P, ArrayList<Integer>>> minterms) {
		if (!IsSatisfiable(currPred))
			return;

		// Keep exploring the tree, if leaf done
		if (n == predicates.size())
			minterms.add(new Pair<P, ArrayList<Integer>>(currPred, setBits));
		else {
			ArrayList<Integer> posList = new ArrayList<Integer>(setBits);
			posList.add(1);
			GetMintermsRec(predicates, n + 1,
					MkAnd(currPred, predicates.get(n)), posList, minterms);

			ArrayList<Integer> negList = new ArrayList<Integer>(setBits);
			negList.add(0);
			GetMintermsRec(predicates, n + 1,
					MkAnd(currPred, MkNot(predicates.get(n))), negList,
					minterms);
		}
	}

}

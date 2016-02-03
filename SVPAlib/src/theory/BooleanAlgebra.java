/**
 * SVPAlib
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import utilities.Pair;

/**
 * BooleanAlgebra over the domain <code>S</code>
 * @param <P> The type of predicates forming the Boolean algebra 
 * @param <S> The domain of the Boolean algebra
 */
public abstract class BooleanAlgebra<P, S> {

	/**
	 * @return the complement of <code>p</code>
	 */
	public abstract P MkNot(P p);

	/**
	 * @return the disjunction of the predicates in <code>pset</code>
	 */
	public abstract P MkOr(Collection<P> pset);

	/**
	 * @return the predicate <code>p1</code> or <code>p2</code>
	 */
	public abstract P MkOr(P p1, P p2);

	/**
	 * @return the conjunction of the predicates in <code>pset</code>
	 */
	public abstract P MkAnd(Collection<P> pset);

	/**
	 * @return the predicate <code>p1</code> and <code>p2</code>
	 */
	public abstract P MkAnd(P p1, P p2);

	/**
	 * @return the predicate true
	 */
	public abstract P True();

	/**
	 * @return the predicate false
	 */
	public abstract P False();

	/**
	 * @return true iff <code>p1</code> and <code>p2</code> are equivalent
	 */
	public abstract boolean AreEquivalent(P p1, P p2);

	/**
	 * @return true iff <code>p1</code> is satisfiable
	 */
	public abstract boolean IsSatisfiable(P p1);

	/**
	 * @return true iff <code>el</code> is a model of <code>p1</code>
	 */
	public abstract boolean HasModel(P p1, S el);

	/**
	 * @return true iff <code>(el1,el2)</code> is a model of a binary predicate <code>p1</code> (used for SVPA)
	 */
	public abstract boolean HasModel(P p1, S el1, S el2);

	public abstract boolean HasModel(P p1, List<S> ellist,Integer lookahead);
	/**
	 * @return a witness of the predicate <code>p1</code> if satisfiable, null otherwise
	 */
	public abstract S generateWitness(P p1);

	/**
	 * @return a pair witness of the binary predicate <code>p1</code> if satisfiable, null otherwise
	 */
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

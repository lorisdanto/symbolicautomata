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

import org.sat4j.specs.TimeoutException;

import utilities.Pair;

/**
 * BooleanAlgebra over the domain <code>S</code>
 * @param <P> The type of predicates forming the Boolean algebra 
 * @param <S> The domain of the Boolean algebra
 */
public abstract class BooleanAlgebra<P, S> {

	/**
	 * @return the predicate accepting only <code>s</code>
	 * @throws TimeoutException 
	 */
	public abstract P MkAtom(S s);
	
	/**
	 * @return the complement of <code>p</code>
	 * @throws TimeoutException 
	 */
	public abstract P MkNot(P p) throws TimeoutException;

	/**
	 * @return the disjunction of the predicates in <code>pset</code>
	 * @throws TimeoutException 
	 */
	public abstract P MkOr(Collection<P> pset) throws TimeoutException;

	/**
	 * @return the predicate <code>p1</code> or <code>p2</code>
	 * @throws TimeoutException 
	 */
	public abstract P MkOr(P p1, P p2) throws TimeoutException;

	/**
	 * @return the conjunction of the predicates in <code>pset</code>
	 * @throws TimeoutException 
	 */
	public abstract P MkAnd(Collection<P> pset) throws TimeoutException;

	/**
	 * @return the predicate <code>p1</code> and <code>p2</code>
	 * @throws TimeoutException 
	 */
	public abstract P MkAnd(P p1, P p2) throws TimeoutException;

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
	 * @throws TimeoutException 
	 */
	public abstract boolean AreEquivalent(P p1, P p2) throws TimeoutException;

	/**
	 * @return true iff <code>p1</code> is satisfiable
	 */
	public abstract boolean IsSatisfiable(P p1)  throws TimeoutException;

	/**
	 * @return true iff <code>el</code> is a model of <code>p1</code>
	 */
	public abstract boolean HasModel(P p1, S el) throws TimeoutException;

	/**
	 * @return true iff <code>(el1,el2)</code> is a model of a binary predicate <code>p1</code> (used for SVPA)
	 */
	public abstract boolean HasModel(P p1, S el1, S el2) throws TimeoutException;

	/**
	 * @return a witness of the predicate <code>p1</code> if satisfiable, null otherwise
	 */
	public abstract S generateWitness(P p1) throws TimeoutException;

	/**
	 * @return a pair witness of the binary predicate <code>p1</code> if satisfiable, null otherwise
	 */
	public abstract Pair<S, S> generateWitnesses(P p1) throws TimeoutException;

	/**
	 * Given a set of <code>predicates</code>, returns all the satisfiable
	 * Boolean combinations
	 * 
	 * @return a set of pairs (p,{i1,..,in}) where p is and ij is 0 or 1 base on
	 *         whether pij is used positively or negatively
	 * @throws TimeoutException 
	 */
	public Collection<Pair<P, ArrayList<Integer>>> GetMinterms(
			ArrayList<P> predicates) {
		try {
			return GetMinterms(predicates, True(), Long.MAX_VALUE);
		} catch (TimeoutException e) {			
			e.printStackTrace();
			System.out.println("Minterm construction timeout");
			return null;
		}
	}
	
	/**
	 * Given a set of <code>predicates</code>, returns all the satisfiable
	 * Boolean combinations
	 * 
	 * @return a set of pairs (p,{i1,..,in}) where p is and ij is 0 or 1 base on
	 *         whether pij is used positively or negatively
	 * @throws TimeoutException 
	 */
	public Collection<Pair<P, ArrayList<Integer>>> GetMinterms(
			ArrayList<P> predicates, long timeout) throws TimeoutException {
		return GetMinterms(predicates, True(), timeout);
	}
	
	private Collection<Pair<P, ArrayList<Integer>>> GetMinterms(
			ArrayList<P> predicates, P startPred, long timeout) throws TimeoutException {
		HashSet<Pair<P, ArrayList<Integer>>> minterms = new HashSet<Pair<P, ArrayList<Integer>>>();
		GetMintermsRec(predicates, 0, startPred, new ArrayList<Integer>(),
				minterms, System.currentTimeMillis(), timeout);
		return minterms;
	}

	private void GetMintermsRec(ArrayList<P> predicates, int n, P currPred,
			ArrayList<Integer> setBits,
			HashSet<Pair<P, ArrayList<Integer>>> minterms, long startime, long timeout) throws TimeoutException {
		
		if(System.currentTimeMillis() - startime > timeout || n>2500)
			throw new TimeoutException("Minterm construction timeout");
			
		if (!IsSatisfiable(currPred))
			return;
		
		if (n == predicates.size())
			minterms.add(new Pair<P, ArrayList<Integer>>(currPred, setBits));
		else {
			ArrayList<Integer> posList = new ArrayList<Integer>(setBits);
			posList.add(1);
			P pn =predicates.get(n);
			GetMintermsRec(predicates, n + 1,
					MkAnd(currPred, pn), posList, minterms, startime, timeout);

			ArrayList<Integer> negList = new ArrayList<Integer>(setBits);
			negList.add(0);
			GetMintermsRec(predicates, n + 1,
					MkAnd(currPred, MkNot(pn)), negList,
					minterms, startime, timeout);
		}
	}
	
	/**
	 * Returns a list of disjoint predicates [p1,...,pn] that accepts the elements [S1...SN] and that has union equal to true.
	 */
	public ArrayList<P> GetSeparatingPredicates(
			ArrayList<Collection<S>> groups, long timeout) throws TimeoutException {
		ArrayList<P> out = new ArrayList<>();
		if(groups.size()<=1){
			out.add(True());
			return out;
		}
		
		//Find largest group
		int maxGroup = 0;
		int maxSize = groups.get(0).size();
		for(int i=1;i<groups.size();i++){
			int ithSize = groups.get(i).size();
			if(ithSize>maxSize){
				maxSize=ithSize;
				maxGroup=i;
			}
		}
		
		//Build negated predicate
		P largePred = False(); 
		for(int i=0;i<groups.size();i++){			
			if(i!=maxGroup)
				for(S s: groups.get(i))
					largePred = MkOr(largePred, MkAtom(s));
		}
		largePred = MkNot(largePred);
		
		//Build list of predicates
		for(int i=0;i<groups.size();i++){			
			if(i!=maxGroup){
				P ithPred = False();
				for(S s: groups.get(i))
					ithPred = MkOr(ithPred, MkAtom(s));
				out.add(ithPred);
			}
			else
				out.add(largePred);
		}
		
		return out;		
	}
}

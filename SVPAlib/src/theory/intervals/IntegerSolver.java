package theory.intervals;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.sat4j.specs.TimeoutException;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import theory.BooleanAlgebra;
import utilities.Pair;

public class IntegerSolver extends BooleanAlgebra<IntPred, Integer> {

	@Override
	public IntPred MkAtom(Integer s) {
		return new IntPred(s);
	}

	@Override
	public IntPred MkNot(IntPred p) {
		return new IntPred(IntPred.invertIntervals(p.intervals));
	}

	@Override
	public IntPred MkOr(Collection<IntPred> pset) {
		IntPred or = StdIntPred.FALSE;
		for (IntPred a : pset) {
			or = MkOr(or, a);
		}
		return or;
	}

	@Override
	public IntPred MkOr(IntPred p1, IntPred p2) {
		return MkNot(MkAnd(MkNot(p1), MkNot(p2)));
	}

	@Override
	public IntPred MkAnd(Collection<IntPred> pset) {
		IntPred and = StdIntPred.TRUE;
		for (IntPred a : pset) { 
			and = MkAnd(and, a);
		}
		return and;
	}

	@Override
	public IntPred MkAnd(IntPred p1, IntPred p2) {
		if(checkNotNull(p1).intervals.isEmpty() || checkNotNull(p2).intervals.isEmpty())
			return False();
		
		List<ImmutablePair<Integer, Integer>> newIntervals = 
				new ArrayList<ImmutablePair<Integer, Integer>>();
		
		for (int i = 0, j = 0; i < p1.intervals.size() && j < p2.intervals.size(); ) {
			ImmutablePair<Integer, Integer> cur1, cur2;
			cur1 = p1.intervals.get(i);
			cur2 = p2.intervals.get(j);
			
			Integer lo = null;
			if (cur1.left != null) { 
				if (cur2.left != null) 
					lo = Math.max(cur1.left, cur2.left);
				else
					lo = cur1.left;
			}
			else
				lo = cur2.left;
			Integer hi = null;
			if (cur1.right != null) { 
				if (cur2.right != null)
					hi = Math.min(cur1.right, cur2.right);
				else
					hi = cur1.right;
			}
			else
				hi = cur2.right;
			
			if (lo == null || hi == null || lo <= hi)
				newIntervals.add(ImmutablePair.of(lo,  hi));
			
			if (cur1.right == hi)
				i++;
			else
				j++;
		}
		
		return new IntPred(ImmutableList.copyOf(newIntervals));
	}

	@Override
	public IntPred True() {
		return StdIntPred.TRUE;
	}

	@Override
	public IntPred False() {
		return StdIntPred.FALSE;
	}

	@Override
	public boolean AreEquivalent(IntPred p1, IntPred p2) {
		checkNotNull(p1);
		checkNotNull(p2);

		boolean nonEquivalent = IsSatisfiable(MkAnd(p1, MkNot(p2))) || IsSatisfiable(MkAnd(MkNot(p1), p2));
		return !nonEquivalent;
	}

	@Override
	public boolean IsSatisfiable(IntPred p) {
		return !checkNotNull(p).intervals.isEmpty();
	}

	@Override
	public boolean HasModel(IntPred p, Integer el) {
		return checkNotNull(p).isSatisfiedBy(checkNotNull(el));
	}

	@Override
	public boolean HasModel(IntPred p1, Integer el1, Integer el2) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Integer generateWitness(IntPred p) {
		if (checkNotNull(p).intervals.isEmpty()) {
			return null;
		} else {
			if (p.intervals.get(0).left != null)
				return p.intervals.get(0).left;
			if (p.intervals.get(0).right != null)
				return p.intervals.get(0).right;
			return 0;
		}
	}

	@Override
	public Pair<Integer, Integer> generateWitnesses(IntPred p1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * Returns a list of disjoint predicates [p1,...,pn] that accepts the elements [S1...SN] and that has union equal to true.
	 */
	@Override
	public ArrayList<IntPred> GetSeparatingPredicates(
			ArrayList<Collection<Integer>> groups, long timeout) throws TimeoutException {
		
		ArrayList<IntPred> out = new ArrayList<>();
		if(groups.size()<=1){
			out.add(True());
			return out;
		}
		boolean empty = true;
		for(Collection<Integer> g : groups) {
			if (g.size() > 0) {
				empty = false;
				break;
			}
		}
		if (empty) { 
			out.add(True());
			for (int i = 1; i < groups.size(); i++)
				out.add(False());
			return out;
		}
		
		Map<Integer, Integer> index = new HashMap<Integer, Integer>();
		List<Integer> arr = new ArrayList<Integer>(); //the sorted evidence
		
		for (int i = 0; i < groups.size(); i++) {
			out.add(False());
			arr.addAll(groups.get(i));
			for (Integer e : groups.get(i))
				index.put(e, i);
		}
		
		Collections.sort(arr);
		Integer left = null;
		Integer right = null;
		for(int i = 0; i < arr.size() - 1; i++) {
			if (index.get(arr.get(i)).equals(index.get(arr.get(i+1))))
				continue;
			//right = (int)Math.floor((double)(arr.get(i) + arr.get(i+1)) / 2.0); //this binary searches
			right = arr.get(i+1) - 1; //this is optimal if you assume lexicographically minimal counterexamples
			int ind = index.get(arr.get(i));
			out.set(ind, MkOr(out.get(ind), new IntPred(left, right)));
			left = right + 1;
		}
		int ind = index.get(arr.get(arr.size() - 1));
		out.set(ind,  MkOr(out.get(ind), new IntPred(left, null)));
		
		return out;
	}

}

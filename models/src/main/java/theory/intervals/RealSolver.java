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
import utilities.Quadruple;

public class RealSolver extends BooleanAlgebra<RealPred, Double> {

	@Override
	public RealPred MkAtom(Double s) {
		return new RealPred(s);
	}

	@Override
	public RealPred MkNot(RealPred p) {
		ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> l = RealPred.invertIntervals(p.intervals);
		RealPred r = new RealPred(l);
		return r;
	}

	@Override
	public RealPred MkOr(Collection<RealPred> pset) {
		RealPred or = StdRealPred.FALSE;
		for (RealPred a : pset) {
			or = MkOr(or, a);
		}
		return or;
	}

	@Override
	public RealPred MkOr(RealPred p1, RealPred p2) {
		return MkNot(MkAnd(MkNot(p1), MkNot(p2)));
	}

	@Override
	public RealPred MkAnd(Collection<RealPred> pset) {
		RealPred and = StdRealPred.TRUE;
		for (RealPred a : pset) { 
			and = MkAnd(and, a);
		}
		return and;
	}

	@Override
	public RealPred MkAnd(RealPred p1, RealPred p2) {
		if(checkNotNull(p1).intervals.isEmpty() || checkNotNull(p2).intervals.isEmpty())
			return False();
		
		List<Quadruple<Double,Boolean,Double,Boolean>> newIntervals = 
				new ArrayList<Quadruple<Double,Boolean,Double,Boolean>>();
		
		for (int i = 0, j = 0; i < p1.intervals.size() && j < p2.intervals.size(); ) {
			Quadruple<Double,Boolean,Double,Boolean> cur1, cur2;
			cur1 = p1.intervals.get(i);
			cur2 = p2.intervals.get(j);
			
			
			
			Double lo = null;
			Boolean lo_open = false; 
			if (cur1.first != null) { 
				if (cur2.first != null) { 
					if(cur1.first > cur2.first) {
						lo = cur1.first;
						lo_open = cur1.second; 
					} else if (cur1.first < cur2.first) {
						lo = cur2.first;
						lo_open = cur2.second; 
					} else {
						lo = cur1.first;
						lo_open = cur1.second || cur2.second;
					}
				}
				else {
					lo = cur1.first;
					lo_open = cur1.second;
				}
			}
			else {
				lo = cur2.first;
				lo_open = cur2.second;
			}
				
			Double hi = null;
			Boolean hi_open = false;
			if (cur1.third != null) { 
				if (cur2.third != null) {
					if(cur1.third < cur2.third) {
						hi = cur1.third;
						hi_open = cur1.fourth; 
					} else if (cur1.third > cur2.third) {
						hi = cur2.third;
						hi_open = cur2.fourth; 
					} else {
						hi = cur1.third;
						hi_open = cur1.fourth || cur2.fourth;
					}
				}
				else {
					hi = cur1.third;
					hi_open = cur1.fourth;
				}
			}
			else {
				hi = cur2.third;
				hi_open = cur2.fourth;
			}
			
			if (lo == null || hi == null || lo < hi || (lo.equals(hi) && !lo_open && !hi_open)) {
				newIntervals.add(new Quadruple<Double,Boolean,Double,Boolean>(lo, lo_open, hi, hi_open));
			}
			
			if (cur1.third == hi)
				i++;
			else {
				j++;
			}
		}
		
		return new RealPred(ImmutableList.copyOf(newIntervals));
	}

	@Override
	public RealPred True() {
		return StdRealPred.TRUE;
	}

	@Override
	public RealPred False() {
		return StdRealPred.FALSE;
	}

	@Override
	public boolean AreEquivalent(RealPred p1, RealPred p2) {
		checkNotNull(p1);
		checkNotNull(p2);
		
		boolean b1 = IsSatisfiable(MkAnd(p1,MkNot(p2)));
		boolean b2 = IsSatisfiable(MkAnd(p2,MkNot(p1)));
		
		boolean nonEquivalent =  b1 || b2 ;
		return !nonEquivalent;
	}

	@Override
	public boolean IsSatisfiable(RealPred p) {
		return !checkNotNull(p).intervals.isEmpty();
	}

	@Override
	public boolean HasModel(RealPred p, Double el) {
		return checkNotNull(p).isSatisfiedBy(checkNotNull(el));
	}

	@Override
	public boolean HasModel(RealPred p1, Double el1, Double el2) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Double generateWitness(RealPred p) {
		if (checkNotNull(p).intervals.isEmpty()) {
			return null;
		} else {
			if(p.intervals.get(0).first == null && p.intervals.get(0).third == null) {
				return 0.0;
			}
			if(p.intervals.get(0).first == null && p.intervals.get(0).third != null) {
				return p.intervals.get(0).third - 1.0;
			}
			if(p.intervals.get(0).first != null && p.intervals.get(0).third == null) {
				return p.intervals.get(0).first + 1.0;
			}
			return p.intervals.get(0).first + ((p.intervals.get(0).third - p.intervals.get(0).first) / 2.0);
		}
	}

	@Override
	public Pair<Double, Double> generateWitnesses(RealPred p1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * Returns a list of disjoint predicates [p1,...,pn] that accepts the elements [S1...SN] and that has union equal to true.
	 */
	@Override
	public ArrayList<RealPred> GetSeparatingPredicates(
			ArrayList<Collection<Double>> groups, long timeout) throws TimeoutException {
		
		ArrayList<RealPred> out = new ArrayList<>();
		if(groups.size()<=1){
			out.add(True());
			return out;
		}
		boolean empty = true;
		for(Collection<Double> g : groups) {
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
		
		Map<Double, Integer> index = new HashMap<Double, Integer>();
		List<Double> arr = new ArrayList<Double>(); //the sorted evidence
		
		for (int i = 0; i < groups.size(); i++) {
			out.add(False());
			arr.addAll(groups.get(i));
			for (Double e : groups.get(i))
				index.put(e, i);
		}
		
		Collections.sort(arr);
		Double left = null;
		Double right = null;
		for(int i = 0; i < arr.size() - 1; i++) {
			if (index.get(arr.get(i)).equals(index.get(arr.get(i+1))))
				continue;
			//right = (int)Math.floor((double)(arr.get(i) + arr.get(i+1)) / 2.0); //this binary searches
			right = arr.get(i+1);
			int ind = index.get(arr.get(i));
			out.set(ind, MkOr(out.get(ind), new RealPred(left, false, right, true)));
			left = right;
		}
		int ind = index.get(arr.get(arr.size() - 1));
		out.set(ind,  MkOr(out.get(ind), new RealPred(left, false, null, true)));
		
		return out;
	}

}

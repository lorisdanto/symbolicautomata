package theory.intervals;

import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import utilities.Quadruple;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class RealPred {
	
	public final ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> intervals;
	
	/**
	 * The set containing only the integer <code>n</code>
	 */
	public RealPred(Double n) {
		this(n,false, n, false);
	}
	
	/**
	 * The set containing only the interval <code>[bot,top]</code> (extremes
	 * included)
	 * <code> bot</code> or <code>top</code> assigned to <code>null</code>
	 * indicates the interval is unbounded for that value
	 */
	public RealPred(Double bot, Boolean open1, Double top, Boolean open2) {
		this(ImmutableList.of(new Quadruple(bot,open1,top,open2)));
	}
	
	public RealPred(ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> intervals) {
		for (Quadruple<Double,Boolean,Double,Boolean> interval : checkNotNull(intervals)) {
			checkArgument(interval.first == null || interval.second == null || interval.third == null || interval.fourth == null ||
					interval.first <= interval.third);
		}

		this.intervals = sortIntervals(checkNotNull(intervals));
	}
	
	private static ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> sortIntervals(
			ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> intervals) {
		
		for (Quadruple<Double,Boolean,Double,Boolean>  interval : checkNotNull(intervals)) {
			checkArgument(interval.first == null || interval.third == null ||
					interval.first <= interval.third);
		}
		
		List<Quadruple<Double,Boolean,Double,Boolean>>  sortLeft = new ArrayList<Quadruple<Double,Boolean,Double,Boolean>>(intervals);
		Collections.sort(sortLeft, new Comparator<Quadruple<Double,Boolean,Double,Boolean>>() {
			public int compare(Quadruple<Double,Boolean,Double,Boolean> o1,
					Quadruple<Double,Boolean,Double,Boolean> o2) {
				if (checkNotNull(o1).first == null)
					return -1;
				if (checkNotNull(o2).first == null)
					return 1;
				if(o1.first.equals(o2.first) && (!o1.second) && o2.second)
					return -1;
				if(o1.first.equals(o2.first) && o1.second && !o2.second)
					return 1;
				if(o1.first > o2.first)
					return 1;
				if(o1.first < o2.first)
					return -1;
				if(o1.first == o2.first)
					return 0;
				assert false;
				return 0;
			}
		});
		
		ImmutableList.Builder<Quadruple<Double,Boolean,Double,Boolean>> ansBuilder = ImmutableList.builder();
		for (int i = 0; i < sortLeft.size(); i++) {
			Double left = sortLeft.get(i).first;
			Boolean leftOpen = sortLeft.get(i).second;
			Double right = sortLeft.get(i).third;
			Boolean rightOpen = sortLeft.get(i).fourth;
			for (int j = i + 1; j < sortLeft.size(); j++){
				//checks if sortLeft.get(j) overlaps (or touches) with sortLeft.get(i) at all
				if (!(right == null ||
					sortLeft.get(j).first == null || 
					sortLeft.get(j).first < right ||
					sortLeft.get(j).first.equals(right) && 
						(!sortLeft.get(j).second || !rightOpen)))
						break;
				if (right != null) {
					if (sortLeft.get(j).third == null)
						right = null;
					else if(right < sortLeft.get(j).third) {
						right = sortLeft.get(j).third;
						rightOpen = sortLeft.get(j).fourth;
					}
					else if(right.equals(sortLeft.get(j).third)) //if either is closed, right interval is closed
						rightOpen = sortLeft.get(j).fourth && rightOpen;
				}
				i++;
			}
			Quadruple<Double,Boolean,Double,Boolean> newInterval = new Quadruple<Double,Boolean,Double,Boolean>(left, leftOpen, right, rightOpen);
			ansBuilder.add(newInterval);
		}
		
		ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> ans = ansBuilder.build();
		for (int i = 1; i < ans.size(); i++) { 
			Quadruple<Double,Boolean,Double,Boolean> curr = ans.get(i);
			Quadruple<Double,Boolean,Double,Boolean> prev = ans.get(i - 1);
			checkArgument(checkNotNull(prev.third) < checkNotNull(curr.first) ||
						(prev.third.equals(curr.first) && prev.fourth && curr.second));
		}
		
		return ans;
	}
	
	
	public static ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> invertIntervals(
			ImmutableList<Quadruple<Double,Boolean,Double,Boolean>> intervals) {
		
		List<Quadruple<Double,Boolean,Double,Boolean>> ret = 
			new ArrayList<Quadruple<Double,Boolean,Double,Boolean>>();
		
		intervals = sortIntervals(intervals);
		if (intervals.isEmpty())
			return ImmutableList.copyOf(StdRealPred.TRUE.intervals);
		
		Double end_point = null;
		Boolean end_open = true;
		for (int i = 0; i < intervals.size(); i++) {
			Quadruple<Double,Boolean,Double,Boolean> interval = intervals.get(i);
			
			if (interval.first != null) {
				ret.add(new Quadruple<Double,Boolean,Double,Boolean>(end_point, end_open, interval.first, !interval.second));
				if (interval.third == null)
					break;
			}
			
			if(interval.third != null) {
				end_point = interval.third;
				end_open = ! interval.fourth;
			}
			
			if(i == intervals.size() - 1 && interval.third != null) {
				ret.add(new Quadruple<Double,Boolean,Double,Boolean>(end_point, end_open, null, true));
			}
		}
		return ImmutableList.copyOf(ret);
	}
	
	public boolean isSatisfiedBy(double n) {
		for (Quadruple<Double,Boolean,Double,Boolean> interval : intervals) {
			if (inInterval(interval, n)) 
				return true;
		}
		return false;
	}
	
	private static boolean inInterval(Quadruple<Double,Boolean,Double,Boolean> interval, double n) {
		if (interval.first == null && interval.third == null)
			return true;
		if( (interval.first != null && interval.first.equals(n) && !interval.second) || 
				(interval.third != null && interval.third.equals(n) && !interval.fourth)) //
			return true;
		if (interval.first == null)
			return n < interval.third;
		if (interval.third == null)
			return n > interval.first;
		return interval.first < n && n < interval.third;
	}
	
	@Override
	public String toString() {
		if (intervals.isEmpty())
			return "empty";
		List<String> retArr = new ArrayList<String>();
		for (Quadruple<Double,Boolean,Double,Boolean> interval : intervals) { 
			String temp = "";
			if(interval.second || interval.first == null)
				temp += "(";
			else
				temp += "[";
			if (interval.first == null)
				temp += "-inf";
			else 
				temp += interval.first.toString();
			temp += ",";
			if (interval.third == null) 
				temp += "inf";
			else
				temp += interval.third.toString();
			if(interval.fourth || interval.third == null)
				temp += ")";
			else
				temp += "]";
			retArr.add(temp);
		}
		String ret = retArr.get(0);
		for(int i = 1; i < retArr.size(); i++)
			ret += "U" + retArr.get(i);
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) { 
		if (obj instanceof RealPred) 
			return Objects.equals(intervals, ((RealPred)obj).intervals);
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(RealPred.class, intervals);
	}

}

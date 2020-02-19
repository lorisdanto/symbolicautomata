package theory.intervals;

import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class IntPred {
	
	public final ImmutableList<ImmutablePair<Integer, Integer>> intervals;
	
	/**
	 * The set containing only the integer <code>n</code>
	 */
	public IntPred(int n) {
		this(n, n);
	}
	
	/**
	 * The set containing only the interval <code>[bot,top]</code> (extremes
	 * included)
	 * <code> bot</code> or <code>top</code> assigned to <code>null</code>
	 * indicates the interval is unbounded for that value
	 */
	public IntPred(Integer bot, Integer top) {
		this(ImmutableList.of(ImmutablePair.of(bot, top)));
	}
	
	public IntPred(ImmutableList<ImmutablePair<Integer, Integer>> intervals) {
		for (ImmutablePair<Integer, Integer> interval : checkNotNull(intervals)) {
			checkArgument(interval.left == null || interval.right == null ||
					interval.left <= interval.right);
		}

		this.intervals = sortIntervals(checkNotNull(intervals));
	}
	
	private static ImmutableList<ImmutablePair<Integer, Integer>> sortIntervals(
			ImmutableList<ImmutablePair<Integer, Integer>> intervals) {
		
		for (ImmutablePair<Integer, Integer> interval : checkNotNull(intervals)) {
			checkArgument(interval.left == null || interval.right == null ||
					interval.left <= interval.right);
		}
		
		List<ImmutablePair<Integer, Integer>> sortLeft = new ArrayList<ImmutablePair<Integer, Integer>>(intervals);
		Collections.sort(sortLeft, new Comparator<ImmutablePair<Integer, Integer>>() {
			public int compare(ImmutablePair<Integer, Integer> o1,
					ImmutablePair<Integer, Integer> o2) {
				if (checkNotNull(o1).left == null)
					return -1;
				if (checkNotNull(o2).left == null)
					return 1;
				return o1.left - o2.left;
			}
		});
		
		ImmutableList.Builder<ImmutablePair<Integer, Integer>> ansBuilder = ImmutableList.builder();
		for (int i = 0; i < sortLeft.size(); i++) {
			Integer left = sortLeft.get(i).left;
			Integer right = sortLeft.get(i).right;
			for (int j = i + 1; j < sortLeft.size() && 
					(right == null || sortLeft.get(j).left == null || sortLeft.get(j).left <= right + 1)
					; j++) {
				if (right != null) {
					if (sortLeft.get(j).right == null)
						right = null;
					else
						right = Math.max(right,  sortLeft.get(j).right);
				}
				i++;
			}
			ansBuilder.add(ImmutablePair.of(left,  right));
		}
		
		ImmutableList<ImmutablePair<Integer, Integer>> ans = ansBuilder.build();
		for (int i = 1; i < ans.size(); i++) { 
			ImmutablePair<Integer, Integer> curr = ans.get(i);
			ImmutablePair<Integer, Integer> prev = ans.get(i - 1);
			checkArgument(checkNotNull(prev.right) < checkNotNull(curr.left));
			checkArgument(prev.right + 1 < curr.left);
		}
		
		return ans;
	}
	
	public static ImmutableList<ImmutablePair<Integer, Integer>> invertIntervals(
			ImmutableList<ImmutablePair<Integer, Integer>> intervals) {
		
		List<ImmutablePair<Integer, Integer>> ret = 
			new ArrayList<ImmutablePair<Integer, Integer>>();
		
		intervals = sortIntervals(intervals);
		if (intervals.isEmpty())
			return ImmutableList.copyOf(StdIntPred.TRUE.intervals);
		
		Integer end_point = null;
		for (int i = 0; i < intervals.size(); i++) {
			ImmutablePair<Integer, Integer> interval = intervals.get(i);
			if (interval.left != null) {
				ret.add(ImmutablePair.of(end_point,  interval.left - 1));
				if (interval.right == null)
					break;
			}
			if (interval.right != null) {
				end_point = interval.right + 1;
			}
			if (i == intervals.size() - 1 && interval.right != null) { 
				ret.add(ImmutablePair.of(interval.right + 1,  null));
			}
		}
		return ImmutableList.copyOf(ret);
	}
	
	public boolean isSatisfiedBy(int n) {
		for (ImmutablePair<Integer, Integer> interval : intervals) {
			if (inInterval(interval, n)) 
				return true;
		}
		return false;
	}
	
	private static boolean inInterval(ImmutablePair<Integer, Integer> interval, int n) {
		if (interval.left == null && interval.right == null)
			return true;
		if (interval.left == null)
			return n <= interval.right;
		if (interval.right == null)
			return n >= interval.left;
		return interval.left <= n && n <= interval.right;
	}
	
	@Override
	public String toString() {
		if (intervals.isEmpty())
			return "empty";
		List<String> retArr = new ArrayList<String>();
		for (ImmutablePair<Integer, Integer> pair : intervals) { 
			String temp = "[";
			if (pair.left == null)
				temp += "-inf";
			else 
				temp += pair.left.toString();
			temp += ",";
			if (pair.right == null) 
				temp += "inf";
			else
				temp += pair.right.toString();
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
		if (obj instanceof IntPred) 
			return Objects.equals(intervals, ((IntPred)obj).intervals);
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(IntPred.class, intervals);
	}

}

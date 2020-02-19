package theory.intervals;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ImmutableList;

public class StdIntPred {

	public final static IntPred TRUE = new IntPred(null, null);
	public final static IntPred FALSE = new IntPred(ImmutableList.<ImmutablePair<Integer, Integer>>of());
	
}

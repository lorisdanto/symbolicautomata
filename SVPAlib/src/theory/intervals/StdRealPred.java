package theory.intervals;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ImmutableList;

import utilities.Quadruple;

public class StdRealPred {

	public final static RealPred TRUE = new RealPred(null, true, null, true);
	public final static RealPred FALSE = new RealPred(ImmutableList.<Quadruple<Double,Boolean,Double,Boolean>>of());
	
}

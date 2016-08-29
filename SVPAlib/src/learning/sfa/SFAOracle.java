package learning.sfa;

import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import theory.BooleanAlgebra;


public class SFAOracle<P, S> extends Oracle<P, S> {
	
	private SFA<P, S> toLearn;
	private BooleanAlgebra<P, S> ba;
	
	public SFAOracle(SFA<P, S> toLearn, BooleanAlgebra<P, S> ba) {
		//this.toLearn = (SFA<P, S>)toLearn.clone();
		this.toLearn = toLearn;
		this.ba = ba;
	}
	
	@Override
	protected List<S> checkEquivalenceImpl(SFA<P, S> compareTo) throws TimeoutException {
		SFA<P, S> sdiff = SFA.union(SFA.difference(toLearn, compareTo, ba, Long.MAX_VALUE),
									SFA.difference(compareTo, toLearn, ba, Long.MAX_VALUE), 
									ba);
		return sdiff.getWitness(ba);
	}
	
	@Override
	protected boolean checkMembershipImpl(List<S> w) throws TimeoutException {
		return toLearn.accepts(w, ba);
	}
}



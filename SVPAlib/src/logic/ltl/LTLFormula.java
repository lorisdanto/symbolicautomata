package logic.ltl;

import java.util.HashSet;
import java.util.Set;

import automata.safa.SAFA;

public abstract class LTLFormula<P,S> {

	public abstract SAFA<P,S> getSAFA();
	
	public Set<LTLFormula<P,S>> getCLphi(){
		Set<LTLFormula<P,S>> cl = new HashSet<>();
		accumulateCLphi(cl);
		return cl;
	}
	
	abstract void accumulateCLphi(Set<LTLFormula<P,S>> cl);
	
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}

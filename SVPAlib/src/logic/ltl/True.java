package logic.ltl;

import java.util.Set;

import automata.safa.SAFA;

public class True<P, S> extends LTLFormula<P, S> {

	public True() {
		super();
	}

	@Override
	public SAFA<P, S> getSAFA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void accumulateCLphi(Set<LTLFormula<P, S>> cl) {
		cl.add(this);
		cl.add(new Not<P,S>(this));
	}

	@Override
	public int hashCode() {
		return 11;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof True))
			return false;
		return true;
	}		
}

package logic.ltl;

import java.util.Set;

import automata.safa.SAFA;

public class Next<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> phi;

	public Next(LTLFormula<P, S> phi) {
		super();
		this.phi = phi;
	}

	@Override
	public SAFA<P, S> getSAFA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void accumulateCLphi(Set<LTLFormula<P, S>> cl) {
		cl.add(this);
		cl.add(new Not<P, S>(this));
		phi.accumulateCLphi(cl);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phi == null) ? 0 : phi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Next))
			return false;
		@SuppressWarnings("unchecked")
		Next<P, S> other = (Next<P, S>) obj;
		if (phi == null) {
			if (other.phi != null)
				return false;
		} else if (!phi.equals(other.phi))
			return false;
		return true;
	}

}

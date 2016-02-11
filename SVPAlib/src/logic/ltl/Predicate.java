package logic.ltl;

import java.util.Set;

import automata.safa.SAFA;

public class Predicate<P, S> extends LTLFormula<P, S> {

	protected P predicate;

	public Predicate(P predicate) {
		super();
		this.predicate = predicate;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Predicate))
			return false;
		@SuppressWarnings("unchecked")
		Predicate<P,S> other = (Predicate<P,S>) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (predicate != other.predicate)
			return false;
		return true;
	}

	
}

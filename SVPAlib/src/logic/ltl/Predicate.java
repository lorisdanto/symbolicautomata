package logic.ltl;

import automata.safa.SAFA;

public class Predicate<P, S> extends LTLFormula<P, S> {

	protected P predicate;

	public Predicate(P predicate) {
		super();
		this.predicate = predicate;
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

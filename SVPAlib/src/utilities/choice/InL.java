package utilities.choice;

import java.util.Objects;

public class InL<L, R> extends Choice<L, R> {

	public L left;	
	
	public InL(L val) {
		left = val;
	}
	
	@Override
	public boolean isLeft() {
		return true;
	}

	@Override
	public boolean isRight() {
		return false;
	}

	@Override 
	public String toString() { 
		return "L:" + left.toString();
	}
	
	@Override
	public boolean equals(Object o) { 
		if (!(o instanceof InL<?, ?>))
			return false;
		return left.equals(((InL<?, ?>)o).left);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(InL.class, left);
	}
}

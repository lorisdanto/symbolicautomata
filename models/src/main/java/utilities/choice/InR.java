package utilities.choice;

import java.util.Objects;

public class InR<L, R> extends Choice<L, R> {
	
	public R right;
	
	public InR(R val) { 
		right = val;
	}
	
	@Override
	public boolean isLeft() {
		return false;
	}

	@Override
	public boolean isRight() {
		return true;
	}
	
	@Override 
	public String toString() { 
		return "R:" + right.toString();
	}

	@Override
	public boolean equals(Object o) { 
		if (!(o instanceof InR<?, ?>))
			return false;
		return right.equals(((InR<?, ?>)o).right);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(InR.class, right);
	}
}

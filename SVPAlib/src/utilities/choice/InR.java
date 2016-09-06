package utilities.choice;

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

}

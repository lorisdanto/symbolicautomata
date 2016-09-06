package utilities.choice;

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
}

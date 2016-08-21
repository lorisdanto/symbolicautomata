package utilities.choice;

public class InL<L, R> extends Choice<L, R> {

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

}

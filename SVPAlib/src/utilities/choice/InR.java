package utilities.choice;

public class InR<L, R> extends Choice<L, R> {

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

}

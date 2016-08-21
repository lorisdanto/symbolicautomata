package utilities.choice;

public abstract class Choice<L, R> {
	
	public L left;
	public R right;
	
	public abstract boolean isLeft();
	
	public abstract boolean isRight();
	
}

package theory.svpa.equalityalgebra;

public abstract class EqualityPredicate<P,S> {
	
	boolean isReturn = false;
	
	public void setAsReturn(){
		isReturn = true;
	}
	
	public boolean isReturn(){
		return isReturn;
	}
	
}

package theory.characters;

public abstract class ICharPred {
	
	boolean isReturn = false;
	
	public void setAsReturn(){
		isReturn = true;
	}
	
	public boolean isReturn(){
		return isReturn;
	}
}

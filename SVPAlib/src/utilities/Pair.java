package utilities;

import java.io.Serializable;


public class Pair<A, B> implements Serializable{

	private static final long serialVersionUID = 6686660062988122937L;
	
	public A first;
	public B second;

    protected Pair(){}
    
    public Pair(A first, B second) {
    	super();
    	this.first = first;
    	this.second = second;
    }

    @Override
    public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;

    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    @Override
    public boolean equals(Object other) {
    	if (other instanceof Pair) {
    		Pair<?, ?> otherPair = (Pair<?, ?>) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) );
    	}

    	return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public A getFirst() {
    	return first;
    }

    public void setFirst(A first) {
    	this.first = first;
    }

    public B getSecond() {
    	return second;
    }

    public void setSecond(B second) {
    	this.second = second;
    }
}
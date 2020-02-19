/**
 * 
 */
package utilities;

public class IntegerPair extends Pair<Integer,Integer> {
	
	private static final long serialVersionUID = -4974702686583519556L;

	public IntegerPair(Integer first, Integer second) {
		super(first, second);
	}

	/**
	 * Returns the first part of the pair
	 * @return Returns the first.
	 */
	public Integer getFirst() {
		return first;
	}

	/**
	 * Sets value for the first part of the pair
	 * @param first
	 *            The first to set.
	 */
	public void setFirst(Integer first) {
		this.first = first;
	}

	/**
	 * Returns the second part of the pair
	 * @return Returns the second.
	 */
	public Integer getSecond() {
		return second;
	}

	/**
	 * Sets value for the second part of the pair
	 * @param second
	 *            The second to set.
	 */
	public void setSecond(Integer second) {
		this.second = second;
	}
	
	public int hashCode() {
        return (first+second)*(first+second+1)/2+second;
    }
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Pair<?,?>))
			return false;		
		IntegerPair s = (IntegerPair) o;
		return s.first==first && s.second==second;
	}
	
	public String toString(){
		return "P(" + first + "," + second + ")\n";
	}
}

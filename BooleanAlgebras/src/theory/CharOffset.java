/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

import java.util.ArrayList;

import utilities.Pair;

/**
 * CharFunc: a character function of the form x0+off where off is an offset
 */
public class CharOffset implements CharFunc{

	public long increment;

	/**
	 * @return the identity function
	 */
	public static CharOffset ID() {
		return new CharOffset(0);
	}

	/**
	 * @return the function the gives lower-case (only works correctly on
	 *         upper-case letters)
	 */
	public static CharOffset ToLowerCase() {
		return new CharOffset(32);
	}

	/**
	 * @return the function the gives upper-case (only works correctly on
	 *         lower-case letters)
	 */
	public static CharOffset ToUpperCase() {
		return new CharOffset(-32);
	}

	/**
	 * The function x0+increment
	 */
	public CharOffset(long increment) {
		if(increment> Character.MAX_VALUE)
			throw new IllegalArgumentException("The offset has to be smaller than Character.MAX_VALUE");
		if(increment< -Character.MAX_VALUE)
			throw new IllegalArgumentException("The offset has to be greater than minus Character.MAX_VALUE");
			
		this.increment = increment;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("c+");
		sb.append(increment);

		return sb.toString();
	}

	public CharFunc SubstIn(CharFunc f1) {
		if(f1 instanceof CharConstant)
			return f1;	
		else{
			CharOffset co = (CharOffset)f1;				
			return new CharOffset(increment+co.increment);
		}
	}	
	
	public CharPred SubstIn(CharPred p, CharSolver cs) {
		ArrayList<Pair<Character,Character>> intervals = new ArrayList<Pair<Character,Character>>();
		for (Pair<Character, Character> interval : p.intervals) {
			intervals.add(
					new Pair<Character, Character>(
							(char)(interval.first-increment), 
							(char)(interval.second-increment)));			
		}
		return new CharPred(intervals);
	}

	public Character InstantiateWith(Character c) {
		//TODO safety check?
		return (char) (c+increment);
	}
}

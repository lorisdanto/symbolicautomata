/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

/**
 * CharFunc: a character function of the form x0+off where off is an offset
 */
public class CharFunc {

	public long increment;

	/**
	 * @return the identity function
	 */
	public static CharFunc ID() {
		return new CharFunc(0);
	}

	/**
	 * @return the function the gives lower-case (only works correctly on
	 *         upper-case letters)
	 */
	public static CharFunc ToLowerCase() {
		return new CharFunc(32);
	}

	/**
	 * @return the function the gives upper-case (only works correctly on
	 *         lower-case letters)
	 */
	public static CharFunc ToUpperCase() {
		return new CharFunc(-32);
	}

	/**
	 * The function x0+increment
	 */
	public CharFunc(long increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("c+");
		sb.append(increment);

		return sb.toString();
	}

}

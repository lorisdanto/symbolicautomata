/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory.characters;

import theory.intervals.UnaryCharIntervalSolver;

/**
 * CharFunc: a character function of the form x0+off where off is an offset
 */
public interface CharFunc {

	/**
	 * @return the result of replacing f1's argument with this
	 */
	CharFunc substIn(CharFunc f1);

	/**
	 * @return the result of replacing p's argument with this
	 */
	CharPred substIn(CharPred p, UnaryCharIntervalSolver unaryCharIntervalSolver);

	/**
	 * @return the result of replacing this's argument with c
	 */
	char instantiateWith(char c);

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

}
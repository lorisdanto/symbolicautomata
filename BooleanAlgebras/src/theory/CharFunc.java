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
public interface CharFunc {

	/**
	 * @return the result of replacing f1's argument with this
	 */
	CharFunc SubstIn(CharFunc f1);
	
	/**
	 * @return the result of replacing p's argument with this
	 */
	CharPred SubstIn(CharPred p, CharSolver cs);

	/**
	 * @return the result of replacing this's argument with c
	 */
	Character InstantiateWith(Character c);
}

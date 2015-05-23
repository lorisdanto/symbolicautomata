/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

import static com.google.common.base.Preconditions.checkNotNull;

public class CharConstant implements CharFunc {

	public CharConstant(char c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return String.format("x -> %s", CharPred.printChar(c));
	}

	public CharFunc substIn(CharFunc f1) {
		return new CharConstant(checkNotNull(f1).instantiateWith(c));
	}

	public CharPred substIn(CharPred p, CharSolver cs) {
		return checkNotNull(p).isSatisfiedBy(c) ? StdCharPred.TRUE : StdCharPred.FALSE;
	}

	public char instantiateWith(char ch) {
		return c;
	}

	public final char c;

}

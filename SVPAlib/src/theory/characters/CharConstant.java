/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory.characters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import theory.intervals.UnaryCharIntervalSolver;

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

	public CharPred substIn(CharPred p, UnaryCharIntervalSolver cs) {
		return checkNotNull(p).isSatisfiedBy(c) ? StdCharPred.TRUE : StdCharPred.FALSE;
	}

	public char instantiateWith(char ch) {
		return c;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharConstant) {
			return Objects.equals(c, ((CharConstant)obj).c);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(CharConstant.class, c);
	}

	public final char c;

}
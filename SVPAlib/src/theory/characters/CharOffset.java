/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory.characters;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ImmutableList;

import theory.intervals.UnaryCharIntervalSolver;

public class CharOffset implements CharFunc {

	public CharOffset(long increment) {
		checkArgument(increment >= -CharPred.MAX_CHAR &&
				increment <= CharPred.MAX_CHAR);
		this.increment = increment;
	}

	@Override
	public String toString() {
		return String.format("x + %d", increment);
	}

	public CharFunc substIn(CharFunc f1) {
		if(checkNotNull(f1) instanceof CharConstant) {
			return f1;
		} else {
			CharOffset co = (CharOffset)f1;
                        return new CharOffset(increment + co.increment);
		}
	}
	
        public long charSnap(long input) {
            if (input < CharPred.MIN_CHAR) {
                return CharPred.MIN_CHAR;
            } else if (input > CharPred.MAX_CHAR) {
                return CharPred.MAX_CHAR;
            } else {
                return input;
            }
        }
        
	public CharPred substIn(CharPred p, UnaryCharIntervalSolver cs) {
		ImmutableList.Builder<ImmutablePair<Character,Character>> intervals = ImmutableList.builder();
        for (ImmutablePair<Character, Character> interval : checkNotNull(p).intervals) {
            // long leftPrime = charSnap(interval.left - increment);
            // long rightPrime = charSnap(interval.right - increment);
            /* the commented code above is written by professor D'Antoni
            I believe that interval.left - increment should be interval.left + increment and
            interval.right - increment should be interval.right + increment
            For example, suppose we want to convert 'D - F' whose ASCII values is 68 - 70 to 'd - e' whose
            ASCII values is 100 - 102.
            TO_LOWER_CASE which is also defined by professor is new CharOffset('a' - 'A');
            By looking up the ASCII table, we could get that 'a' - 'A' = 97 - 65 = 32 which means the increment
            of TO_LOWER_CASE is 32.
            If leftPrime = charSnap(interval.left - increment) and rightPrime = charSnap(interval.right -
            increment), then TO_LOWER_CASE.substIn(new CharPred('D', 'F'), cs)'s result will be
            CharPred('$', '&'), which is not what we want. */
            long leftPrime = charSnap(interval.left + increment);
            long rightPrime = charSnap(interval.right + increment);
            intervals.add(ImmutablePair.of((char)leftPrime, (char)rightPrime));
        }
		return new CharPred(intervals.build());
	}

	public char instantiateWith(char c) {
            return (char)charSnap(c + increment);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharOffset) {
			return Objects.equals(increment, ((CharOffset)obj).increment);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(CharOffset.class, increment);
	}

	public final long increment;

	public static final CharOffset IDENTITY = new CharOffset(0);
	public static final CharOffset TO_LOWER_CASE = new CharOffset('a' - 'A');
	public static final CharOffset TO_UPPER_CASE = new CharOffset('A' - 'a');

}
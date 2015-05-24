/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

public class CharOffset implements CharFunc {

	public CharOffset(long increment) {
		checkArgument(increment >= -CharPred.MAX_CHAR &&
				increment <= CharPred.MAX_CHAR);
		this.increment = increment;
	}

	@Override
	public String toString() {
		return String.format("x -> x + %d", increment);
	}

	public CharFunc substIn(CharFunc f1) {
		if(checkNotNull(f1) instanceof CharConstant) {
			return f1;
		} else {
			CharOffset co = (CharOffset)f1;				
			return new CharOffset(increment + co.increment);
		}
	}
	
	public CharPred substIn(CharPred p, CharSolver cs) {
		ImmutableList.Builder<ImmutablePair<Character,Character>> intervals = ImmutableList.builder();
		for (ImmutablePair<Character, Character> interval : checkNotNull(p).intervals) {
			intervals.add(ImmutablePair.of((char)(interval.left - increment),
					(char)(interval.right - increment)));
		}
		return new CharPred(intervals.build());
	}

	public char instantiateWith(char c) {
		//TODO safety check?
		return (char)(c + increment);
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

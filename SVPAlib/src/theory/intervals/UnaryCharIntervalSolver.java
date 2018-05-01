/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory.intervals;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.sun.xml.internal.bind.annotation.XmlLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ImmutableList;

import theory.BooleanAlgebraSubst;
import theory.characters.*;
import utilities.Pair;

/**
 * CharSolver: an interval based solver for the theory of characters
 */
public class UnaryCharIntervalSolver extends BooleanAlgebraSubst<CharPred, CharFunc, Character> {

	@Override
	public CharPred MkNot(CharPred u) {
		List<ImmutablePair<Character,Character>> newIntervals =
				new ArrayList<ImmutablePair<Character, Character>>();

		if(checkNotNull(u).intervals.isEmpty()) {
			return StdCharPred.TRUE;
		}

		Character curBot = u.intervals.get(0).left;
		if(CharPred.MIN_CHAR < curBot) {
			newIntervals.add(ImmutablePair.of(CharPred.MIN_CHAR, (char)(curBot - 1)));
		}

		char prevTop = u.intervals.get(0).right;
		for(int i = 1; i < u.intervals.size(); i++) {
			ImmutablePair<Character,Character> curr = u.intervals.get(i);
			curBot = curr.left;
			char newIntLo = (char)(prevTop + 1);
			char newIntHi = (char)(curBot - 1);
			if (newIntLo <= newIntHi) {
				newIntervals.add(ImmutablePair.of(newIntLo, newIntHi));
			}

			prevTop = curr.right;
		}

		if(prevTop < CharPred.MAX_CHAR) {
			newIntervals.add(ImmutablePair.of((char)(prevTop + 1), CharPred.MAX_CHAR));
		}

		return new CharPred(ImmutableList.copyOf(newIntervals));
	}

	@Override
	public CharPred MkOr(Collection<CharPred> clctn) {
		CharPred or = StdCharPred.FALSE;
		for(CharPred a : clctn) {
			or = MkOr(or, a);
		}
		return or;
	}

	@Override
	public CharPred MkOr(CharPred u1, CharPred u2) {
		return MkNot(MkAnd(MkNot(u1), MkNot(u2)));
	}

	@Override
	public CharPred MkAnd(Collection<CharPred> clctn) {
		CharPred and = StdCharPred.TRUE;
		for(CharPred a : clctn) {
			and = MkAnd(and, a);
		}
		return and;
	}

	@Override
	public CharPred MkAnd(CharPred u1, CharPred u2) {
		if(checkNotNull(u1).intervals.isEmpty() || checkNotNull(u2).intervals.isEmpty()) {
			return False();
		}

		List<ImmutablePair<Character,Character>> newIntervals =
				new ArrayList<ImmutablePair<Character, Character>>();

		for (int i = 0, j = 0; i < u1.intervals.size() && j < u2.intervals.size(); ) {
			ImmutablePair<Character, Character> cur1 = u1.intervals.get(i);
			ImmutablePair<Character, Character> cur2 = u2.intervals.get(j);

			char lo = (char)Math.max(cur1.left, cur2.left);
			char hi = (char)Math.min(cur1.right, cur2.right);
			if (lo <= hi) {
				newIntervals.add(ImmutablePair.of(lo, hi));
			}

			if (cur1.right == hi) {
				i++;
			} else {
				j++;
			}
		}

		return new CharPred(ImmutableList.copyOf(newIntervals));
	}

	@Override
	public CharPred True() {
		return StdCharPred.TRUE;
	}

	@Override
	public CharPred False() {
		return StdCharPred.FALSE;
	}

	@Override
	public boolean AreEquivalent(CharPred u1, CharPred u2) {
		checkNotNull(u1);
		checkNotNull(u2);

		boolean nonEquivalent = IsSatisfiable(MkAnd(u1, MkNot(u2))) ||
				IsSatisfiable(MkAnd(MkNot(u1),u2));
		return !nonEquivalent;
	}

	@Override
	public boolean IsSatisfiable(CharPred u) {
		return !checkNotNull(u).intervals.isEmpty();
	}

	@Override
	public boolean HasModel(CharPred u, Character s) {
		return checkNotNull(u).isSatisfiedBy(checkNotNull(s));
	}

	@Override
	public boolean HasModel(CharPred u, Character s, Character s1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Character generateWitness(CharPred u) {
		if (checkNotNull(u).intervals.isEmpty()) {
			return null;
		} else {
			Random r = new Random();
			int whichInterval = r.nextInt(u.intervals.size());
			ImmutablePair<Character, Character> interval = u.intervals.get(whichInterval);
			int diff = interval.right-interval.left;
			Character c= (char)(Character.valueOf(interval.left) + (diff==0?0:r.nextInt(diff+1)));
			return c;
		}
	}

	@Override
	public Pair<Character, Character> generateWitnesses(CharPred u) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public CharFunc MkSubstFuncFunc(CharFunc f1, CharFunc f2) {
		return checkNotNull(f2).substIn(checkNotNull(f1));
	}

	@Override
	public CharPred MkSubstFuncPred(CharFunc f, CharPred p) {
		return checkNotNull(f).substIn(checkNotNull(p), this);
	}

	@Override
	public Character MkSubstFuncConst(CharFunc f, Character c) {
		return checkNotNull(f).instantiateWith(checkNotNull(c));
	}

	@Override
	public CharFunc MkFuncConst(Character c) {
		return new CharConstant(checkNotNull(c));
	}

	@Override
	public boolean CheckGuardedEquality(CharPred p, CharFunc f1, CharFunc f2) {
		CharPred f1IsNotEqualTof2;
		if (checkNotNull(f1) instanceof CharConstant && checkNotNull(f2) instanceof CharConstant)
			f1IsNotEqualTof2 = ((CharConstant) f1).c == ((CharConstant) f2).c ? False() : True();
		else if (checkNotNull(f1) instanceof CharConstant && checkNotNull(f2) instanceof CharOffset)
			f1IsNotEqualTof2 = MkNot(MkSubstFuncPred(f2, new CharPred(((CharConstant) f1).c)));
		else if (checkNotNull(f1) instanceof CharOffset && checkNotNull(f2) instanceof CharConstant)
			f1IsNotEqualTof2 = MkNot(MkSubstFuncPred(f1, new CharPred(((CharConstant) f2).c)));
		else // checkNotNull(f1) instanceof CharOffset && checkNotNull(f2) instanceof CharOffset
			f1IsNotEqualTof2 = ((CharOffset) f1).increment == ((CharOffset) f2).increment ? False() : True();
		return !IsSatisfiable(MkAnd(p, f1IsNotEqualTof2));
	}

	@Override
	public CharPred getRestrictedOutput(CharPred p, CharFunc f) {
		if (!IsSatisfiable(checkNotNull(p)))
			return False();
		if (checkNotNull(f) instanceof CharConstant)
			return new CharPred(((CharConstant) f).c);
		else { // checkNotNull(f) instanceof CharOffset
			// \phi(y) = \exists x. \phi(x) \wedge f(x)=y
			// first of all, get all x
			p = this.MkAnd(f.substIn(True(), this), p);
			// Then compute y based on x
			ImmutableList.Builder<ImmutablePair<Character,Character>> intervals = ImmutableList.builder();
			for (ImmutablePair<Character, Character> interval : checkNotNull(p).intervals) {
				long leftPrime = interval.left + ((CharOffset) f).increment;
				long rightPrime = interval.right + ((CharOffset) f).increment;
				intervals.add(ImmutablePair.of((char)leftPrime, (char)rightPrime));
			}
			return new CharPred(intervals.build());
		}
	}

	/**
	 * returns a string of a list of CharPred
	 * @param charPreds
	 * @return string of a list of CharPred
	 */
	public String stringOfListOfCharPred(List<CharPred> charPreds){
		StringBuilder sb = new StringBuilder();
		for(CharPred p : checkNotNull(charPreds)) {
			sb.append(generateWitness(p));
		}
		return sb.toString();
	}

	/**
	 * returns the string of a list of chars
	 * @param chars
	 * @return
	 */
	public String stringOfList(List<Character> chars){
		StringBuilder sb = new StringBuilder();
		for(Character c : checkNotNull(chars)) {
			sb.append(c);
		}
		return sb.toString();
	}

	@Override
	public CharPred MkAtom(Character s) {
		return new CharPred(s);
	}

}
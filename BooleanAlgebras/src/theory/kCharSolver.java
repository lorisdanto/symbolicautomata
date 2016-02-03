package theory;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import utilities.Pair;

import com.google.common.collect.ImmutableList;

/**
 * CharSolver: an interval based solver for the theory of characters
 */
public class kCharSolver extends BooleanAlgebraSubst<kCharPred, CharFunc, Character> {

	@Override
	public CharFunc MkSubstFuncFunc(CharFunc f1, CharFunc f2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Character MkSubstFuncConst(CharFunc f, Character c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred MkSubstFuncPred(CharFunc f, kCharPred p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred MkNot(kCharPred p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred MkOr(Collection<kCharPred> pset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred MkOr(kCharPred p1, kCharPred p2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred MkAnd(Collection<kCharPred> pset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred MkAnd(kCharPred p1, kCharPred p2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred True() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public kCharPred False() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean AreEquivalent(kCharPred p1, kCharPred p2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean IsSatisfiable(kCharPred p1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean HasModel(kCharPred p1, Character el) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean HasModel(kCharPred p1, Character el1, Character el2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean HasModel(kCharPred p1, List<Character> ellist, Integer lookahead) {
		return checkNotNull(p1).isSatisfiedBy(checkNotNull(ellist));
	}

	@Override
	public Character generateWitness(kCharPred p1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Character, Character> generateWitnesses(kCharPred p1) {
		// TODO Auto-generated method stub
		return null;
	}
    


}
package theory.binaryalgebra;

import theory.BooleanAlgebra;

import java.util.Collection;
import java.util.HashSet;

import org.sat4j.specs.TimeoutException;

import utilities.Pair;

public class BinaryBooleanAlgebra extends BooleanAlgebra <HashSet <Boolean>, Boolean> {

	public boolean AreEquivalent(HashSet <Boolean> arg0, HashSet <Boolean> arg1) {
		return (arg0 == arg1);
	}

	@Override
	public HashSet <Boolean> False() {
		return new HashSet <Boolean>();
	}

	@Override
	public HashSet <Boolean> True() {
		HashSet <Boolean> res = new HashSet <Boolean>();
		res.add(true);
		res.add(false);
		return res;
	}

	@Override
	public boolean HasModel(HashSet <Boolean> arg0, Boolean arg1) throws TimeoutException {
		return arg0.contains(arg1);
	}

	@Override
	public boolean HasModel(HashSet <Boolean> arg0, Boolean arg1, Boolean arg2) throws TimeoutException {
		return false;
	}

	@Override
	public boolean IsSatisfiable(HashSet <Boolean> arg0) throws TimeoutException {
		return (arg0.size() > 0);
	}

	@Override
	public HashSet <Boolean> MkAnd(Collection<HashSet<Boolean>> arg0) throws TimeoutException {
		HashSet <Boolean> res = True();
		for (HashSet <Boolean> s : arg0) {
			res.retainAll(s);
		}
		return res;
	}

	@Override
	public HashSet <Boolean> MkAnd(HashSet <Boolean> arg0, HashSet <Boolean> arg1) throws TimeoutException {
		HashSet <Boolean> res = new HashSet <Boolean>(arg0);
		res.retainAll(arg1);
		return res;
	}

	@Override
	public HashSet <Boolean> MkAtom(Boolean arg0) {
		HashSet <Boolean> res = new HashSet <Boolean>();
		res.add(arg0);
		return res;
	}

	@Override
	public HashSet <Boolean> MkNot(HashSet <Boolean> arg0) throws TimeoutException {
		HashSet <Boolean> res = True();
		res.removeAll(arg0);
		return res;
	}

	@Override
	public HashSet <Boolean> MkOr(Collection<HashSet <Boolean>> arg0) throws TimeoutException {
		HashSet <Boolean> res = False();		
		for (HashSet <Boolean> e: arg0) {
			res.addAll(e);
		}
		return res;
	}

	@Override
	public HashSet <Boolean> MkOr(HashSet <Boolean> arg0, HashSet <Boolean> arg1) throws TimeoutException {
		HashSet <Boolean> res = new HashSet <>(arg0);
		res.addAll(arg1);
		return res;
	}

	@Override
	public Boolean generateWitness(HashSet <Boolean> arg0) throws TimeoutException {
		if (arg0.size() == 0) {
			return null;
		}
		return arg0.iterator().next();
	}

	@Override
	public Pair<Boolean, Boolean> generateWitnesses(HashSet <Boolean> arg0) throws TimeoutException {
		if (arg0.size() != 2) {
			return null;
		}
		return new Pair<Boolean, Boolean>(true, false);
	}

}
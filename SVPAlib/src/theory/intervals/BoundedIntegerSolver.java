package theory.intervals;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

public class BoundedIntegerSolver extends BooleanAlgebra<IntPred, Integer> {

	private IntegerSolver ba;
	private IntPred domain;
	
	public BoundedIntegerSolver(Integer lb, Integer ub) {
		ba = new IntegerSolver();
		domain = new IntPred(lb, ub);
	}
	
	@Override
	public IntPred MkAtom(Integer s) {
		checkArgument(ba.HasModel(domain, s));
		return ba.MkAnd(domain, ba.MkAtom(s));
	}

	@Override
	public IntPred MkNot(IntPred p) throws TimeoutException {
		return ba.MkAnd(domain, ba.MkNot(p));
	}

	@Override
	public IntPred MkOr(Collection<IntPred> pset) throws TimeoutException {
		return ba.MkAnd(domain, ba.MkOr(pset));
	}

	@Override
	public IntPred MkOr(IntPred p1, IntPred p2) throws TimeoutException {
		return ba.MkAnd(domain, ba.MkOr(p1, p2));
	}

	@Override
	public IntPred MkAnd(Collection<IntPred> pset) throws TimeoutException {
		return ba.MkAnd(domain, ba.MkAnd(pset));
	}

	@Override
	public IntPred MkAnd(IntPred p1, IntPred p2) throws TimeoutException {
		return ba.MkAnd(domain, ba.MkAnd(p1, p2));
	}

	@Override
	public IntPred True() {
		return ba.MkAnd(domain, ba.True());
	}

	@Override
	public IntPred False() {
		return ba.MkAnd(domain, ba.False());
	}

	@Override
	public boolean AreEquivalent(IntPred p1, IntPred p2)
			throws TimeoutException {
		return ba.AreEquivalent(p1, p2); //assumes p1 and p2 are equivalent even outside the domain
	}

	@Override
	public boolean IsSatisfiable(IntPred p1) throws TimeoutException {
		return ba.IsSatisfiable(ba.MkAnd(domain, p1));
	}

	@Override
	public boolean HasModel(IntPred p1, Integer el) throws TimeoutException {
		return ba.HasModel(ba.MkAnd(domain, p1), el);
	}

	@Override
	public boolean HasModel(IntPred p1, Integer el1, Integer el2)
			throws TimeoutException {
		return ba.HasModel(ba.MkAnd(domain, p1), el1, el2);
	}

	@Override
	public Integer generateWitness(IntPred p1) throws TimeoutException {
		return ba.generateWitness(p1); //assumes p1 is contained in the domain
	}

	@Override
	public Pair<Integer, Integer> generateWitnesses(IntPred p1)
			throws TimeoutException {
		return ba.generateWitnesses(p1);
	}
	
	@Override
	public ArrayList<IntPred> GetSeparatingPredicates(
			ArrayList<Collection<Integer>> groups, long timeout) throws TimeoutException {
		ArrayList<IntPred> out = ba.GetSeparatingPredicates(groups, timeout);
		ArrayList<IntPred> ret = new ArrayList<IntPred>();
		for (IntPred p : out) { 
			ret.add(ba.MkAnd(domain, p));
		}
		return ret;
	}

}

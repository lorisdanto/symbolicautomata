package theory.sfa;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;
import utilities.Pair;

public class SFABooleanAlgebra <P,D> extends BooleanAlgebra <SFA <P,D>, List <D>> {

	private BooleanAlgebra <P,D> ba;

	public SFABooleanAlgebra(BooleanAlgebra <P,D> algebra) {
		ba = algebra;
	}

	@Override
	public boolean AreEquivalent(SFA<P, D> arg0, SFA<P, D> arg1) throws TimeoutException {
		return SFA.areEquivalent(arg0, arg1, ba);
	}

	@Override
	public SFA<P, D> False() {
		try {
			return SFA.getEmptySFA(ba);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean HasModel(SFA<P, D> arg0, List<D> arg1) throws TimeoutException {
		return arg0.accepts(arg1, ba);
	}

	@Override
	public boolean HasModel(SFA<P, D> arg0, List<D> arg1, List<D> arg2) throws TimeoutException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean IsSatisfiable(SFA<P, D> arg0) throws TimeoutException {
		return !SFA.areEquivalent(arg0, SFA.getEmptySFA(ba), ba);
	}

	@Override
	public SFA<P, D> MkAnd(Collection<SFA<P, D>> pset) throws TimeoutException {
		SFA<P,D> result = SFA.getFullSFA(ba);
		for (SFA<P,D> aut : pset) {
			result = result.intersectionWith(aut, ba);
		}
		return result;
	}

	@Override
	public SFA<P, D> MkAnd(SFA<P, D> arg0, SFA<P, D> arg1) throws TimeoutException {
		return arg0.intersectionWith(arg1, ba);
	}

	@Override
	public SFA<P, D> MkAtom(List<D> s) {
		LinkedList<SFAMove<P, D>> moves = new LinkedList<>();
		SFA<P, D> sfa;

		for(int i=0;i<s.size();i++){
			moves.add(new SFAInputMove<P,D>(i, i+1, ba.MkAtom(s.get(i))));
		}
		HashSet<Integer> finStates = new HashSet<>();
		finStates.add(s.size());
		try {
			sfa = SFA.MkSFA(moves, 0, finStates, ba);
			return sfa;
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}



	@Override
	public SFA<P, D> MkOr(SFA<P, D> arg0, SFA<P, D> arg1) throws TimeoutException {
		return arg0.unionWith(arg1, ba);
	}

	@Override
	public SFA<P, D> True() {
		try {
			return SFA.getFullSFA(ba);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<D> generateWitness(SFA<P, D> arg0) throws TimeoutException {
		return arg0.getWitness(ba);
	}

	@Override
	public Pair<List<D>, List<D>> generateWitnesses(SFA<P, D> arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("SFABooleanAlgebra.generateWitnesses");
	}

	@Override
	public SFA<P, D> MkNot(SFA<P, D> arg0) throws TimeoutException {
		return arg0.complement(ba);
	}

	@Override
	public SFA<P, D> MkOr(Collection<SFA<P, D>> pset) throws TimeoutException {
		SFA <P,D> result = SFA.getEmptySFA(ba);
		for (SFA <P,D> aut : pset) {
			result = result.unionWith(aut, ba);
		}
		return result;
	}

}
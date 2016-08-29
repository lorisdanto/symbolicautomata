package theory.safa;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;
import utilities.Pair;

public class SAFABooleanAlgebra<P,S> extends BooleanAlgebra<SAFA<P,S>, List<S>> {
	BooleanAlgebra<P,S> ba;
	BooleanExpressionFactory<PositiveBooleanExpression> boolexpr;

	public SAFABooleanAlgebra(BooleanAlgebra<P,S> ba,
			BooleanExpressionFactory<PositiveBooleanExpression> boolexpr) {
		this.ba = ba;
		this.boolexpr = boolexpr;
	}

	@Override
	public SAFA<P, S> MkNot(SAFA<P, S> p) throws TimeoutException {
		return p.negate(ba);
	}

	@Override
	public SAFA<P, S> MkOr(Collection<SAFA<P, S>> pset) throws TimeoutException {
		SAFA<P,S> result = SAFA.getEmptySAFA(ba);
		for (SAFA<P,S> aut : pset) {
			result = result.unionWith(aut, ba);
		}
		return result;
	}

	@Override
	public SAFA<P, S> MkOr(SAFA<P, S> p1, SAFA<P, S> p2) throws TimeoutException {
		return p1.unionWith(p2, ba);
	}

	@Override
	public SAFA<P, S> MkAnd(Collection<SAFA<P, S>> pset) throws TimeoutException {
		SAFA<P,S> result = SAFA.getFullSAFA(ba);
		for (SAFA<P,S> aut : pset) {
			result = result.intersectionWith(aut, ba);
		}
		return result;
	}

	@Override
	public SAFA<P, S> MkAnd(SAFA<P, S> p1, SAFA<P, S> p2) throws TimeoutException {
		return p1.intersectionWith(p2, ba);
	}

	@Override
	public SAFA<P, S> True() {
		return SAFA.getFullSAFA(ba);
	}

	@Override
	public SAFA<P, S> False() {
		return SAFA.getEmptySAFA(ba);
	}

	@Override
	public boolean AreEquivalent(SAFA<P, S> p1, SAFA<P, S> p2) {
		try {
			return SAFA.isEquivalent(p1, p2, ba, boolexpr).getFirst();
		} catch (TimeoutException e) {
			System.exit(-1);
			return false;
		}
	}

	@Override
	public boolean IsSatisfiable(SAFA<P, S> p1) {
		try {
			return !SAFA.isEmpty(p1, ba);
		} catch (TimeoutException e) {
			System.exit(-1);
			return false;
		}
	}

	@Override
	public boolean HasModel(SAFA<P, S> p1, List<S> el) throws TimeoutException {
		return p1.accepts(el, ba);
	}

	@Override
	public boolean HasModel(SAFA<P, S> p1, List<S> el1, List<S> el2) {
		throw new IllegalArgumentException("SAFABooleanAlgebra.HasModel");
	}

	@Override
	public List<S> generateWitness(SAFA<P, S> p1) {
		try {
			Pair<Boolean, List<S>> result = SAFA.isEquivalent(p1, False(), ba, boolexpr);
			if (result.first) {
				throw new IllegalArgumentException("generateWitness: unsat");
			}
			return result.second;
		} catch (TimeoutException e) {
			System.exit(-1);
			return null;
		}
	}

	@Override
	public Pair<List<S>, List<S>> generateWitnesses(SAFA<P, S> p1) {
		throw new IllegalArgumentException("SAFABooleanAlgebra.generateWitnesses");
	}

	@Override
	public SAFA<P, S> MkAtom(List<S> s)  {
		LinkedList<SFAMove<P, S>> moves = new LinkedList<>(); 
		for(int i=0;i<s.size();i++){
			moves.add(new SFAInputMove<P,S>(i, i+1, ba.MkAtom(s.get(i))));
		}
		HashSet<Integer> finStates =  new HashSet<>();
		finStates.add(s.size());
		SFA<P, S> sfa;
		try {
			sfa = SFA.MkSFA(moves, 0, finStates, ba);
			return sfa.getSAFA(ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

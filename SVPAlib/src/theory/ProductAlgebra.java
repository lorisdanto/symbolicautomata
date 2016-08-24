package theory;

import java.util.Collection;

import org.sat4j.specs.TimeoutException;

import utilities.Pair;

public class ProductAlgebra<P1, S1, P2, S2> extends BooleanAlgebra<Pair<P1, P2>, Pair<S1, S2>> {

	private BooleanAlgebra<P1,S1> ba1;
	private BooleanAlgebra<P2,S2> ba2;
	
	public ProductAlgebra(BooleanAlgebra<P1,S1> ba1, BooleanAlgebra<P2,S2> ba2) {
		this.ba1 = ba1;
		this.ba2 = ba2;
	}
	
	@Override
	public Pair<P1, P2> MkAtom(Pair<S1, S2> s) throws TimeoutException {
		return new Pair<P1, P2>(ba1.MkAtom(s.first), ba2.MkAtom(s.second));
	}

	@Override
	public Pair<P1, P2> MkNot(Pair<P1, P2> p) throws TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<P1, P2> MkOr(Collection<Pair<P1, P2>> pset) throws TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<P1, P2> MkOr(Pair<P1, P2> p1, Pair<P1, P2> p2) throws TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<P1, P2> MkAnd(Collection<Pair<P1, P2>> pset) throws TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<P1, P2> MkAnd(Pair<P1, P2> p1, Pair<P1, P2> p2) throws TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<P1, P2> True() {
		return new Pair<P1, P2>(ba1.True(), ba2.True());
	}

	@Override
	public Pair<P1, P2> False() {
		return new Pair<P1, P2>(ba1.False(), ba2.False());
	}

	@Override
	public boolean AreEquivalent(Pair<P1, P2> p1, Pair<P1, P2> p2) {
		return ba1.AreEquivalent(p1.first, p2.first) && ba2.AreEquivalent(p1.second, p2.second);
	}

	@Override
	public boolean IsSatisfiable(Pair<P1, P2> p1) {
		return ba1.IsSatisfiable(p1.first) && ba2.IsSatisfiable(p1.second);
	}

	@Override
	public boolean HasModel(Pair<P1, P2> p1, Pair<S1, S2> el) {
		return ba1.HasModel(p1.first, el.first) && ba2.HasModel(p1.second, el.second); 
	}

	@Override
	public boolean HasModel(Pair<P1, P2> p1, Pair<S1, S2> el1, Pair<S1, S2> el2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Pair<S1, S2> generateWitness(Pair<P1, P2> p1) {
		S1 wit1 = ba1.generateWitness(p1.first);
		S2 wit2 = ba2.generateWitness(p1.second);
		if (wit1 == null || wit2 == null)
			return null;
		return new Pair<S1, S2>(wit1, wit2);
	}

	@Override
	public Pair<Pair<S1, S2>, Pair<S1, S2>> generateWitnesses(Pair<P1, P2> p1) {
		// TODO Auto-generated method stub
		return null;
	}

}

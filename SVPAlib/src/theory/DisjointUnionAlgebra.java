package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.sat4j.specs.TimeoutException;

import static com.google.common.base.Preconditions.checkArgument;

import utilities.Pair;
import utilities.choice.Choice;
import utilities.choice.InL;
import utilities.choice.InR;

public class DisjointUnionAlgebra<P1, S1, P2, S2> extends BooleanAlgebra<Pair<P1,P2>, Choice<S1,S2>> {

	private BooleanAlgebra<P1,S1> ba1;
	private BooleanAlgebra<P2,S2> ba2;
	
	public DisjointUnionAlgebra(BooleanAlgebra<P1,S1> ba1, BooleanAlgebra<P2,S2> ba2) {
		this.ba1 = ba1;
		this.ba2 = ba2;
	}
	
	@Override
	public Pair<P1, P2> MkAtom(Choice<S1, S2> s) {
		if (s.isLeft()){
			InL<S1, S2> cast = (InL<S1, S2>)s; 
			return new Pair<P1, P2>(ba1.MkAtom(cast.left), ba2.False());
		}
		else{ //s.isRight()
			InR<S1, S2> cast = (InR<S1, S2>)s; 		
			return new Pair<P1, P2>(ba1.False(), ba2.MkAtom(cast.right));
		}
	}

	@Override
	public Pair<P1, P2> MkNot(Pair<P1, P2> p) throws TimeoutException {
		return new Pair<P1, P2>(ba1.MkNot(p.first), ba2.MkNot(p.second));
	}

	@Override
	public Pair<P1, P2> MkOr(Collection<Pair<P1, P2>> pset) throws TimeoutException {
		Collection<P1> p1set = new ArrayList<P1>();
		Collection<P2> p2set = new ArrayList<P2>();
		for (Pair<P1, P2> p : pset) {
			p1set.add(p.first);
			p2set.add(p.second);
		}
		return new Pair<P1, P2>(ba1.MkOr(p1set), ba2.MkOr(p2set));
	}

	@Override
	public Pair<P1, P2> MkOr(Pair<P1, P2> p1, Pair<P1, P2> p2) throws TimeoutException {
		return new Pair<P1, P2>(ba1.MkOr(p1.first, p2.first), ba2.MkOr(p1.second, p2.second));
	}

	@Override
	public Pair<P1, P2> MkAnd(Collection<Pair<P1, P2>> pset) throws TimeoutException {
		Collection<P1> p1set = new ArrayList<P1>();
		Collection<P2> p2set = new ArrayList<P2>();
		for (Pair<P1, P2> p : pset) {
			p1set.add(p.first);
			p2set.add(p.second);
		}
		return new Pair<P1, P2>(ba1.MkAnd(p1set), ba2.MkAnd(p2set));
	}

	@Override
	public Pair<P1, P2> MkAnd(Pair<P1, P2> p1, Pair<P1, P2> p2) throws TimeoutException {
		return new Pair<P1, P2>(ba1.MkAnd(p1.first, p2.first), ba2.MkAnd(p1.second, p2.second));
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
	public boolean AreEquivalent(Pair<P1, P2> p1, Pair<P1, P2> p2) throws TimeoutException {
		return ba1.AreEquivalent(p1.first, p2.first) && ba2.AreEquivalent(p1.second, p2.second);
	}

	@Override
	public boolean IsSatisfiable(Pair<P1, P2> p) throws TimeoutException {
		return ba1.IsSatisfiable(p.first) || ba2.IsSatisfiable(p.second); 
	}

	@Override
	public boolean HasModel(Pair<P1, P2> p, Choice<S1, S2> s) throws TimeoutException {
		if (s.isLeft()){
			InL<S1, S2> cast = (InL<S1, S2>)s; 
			return ba1.HasModel(p.first, cast.left);
		}
		else{ //s.isRight()
			InR<S1, S2> cast = (InR<S1, S2>)s; 		
			return ba2.HasModel(p.second, cast.right);
		}
	}

	@Override
	public boolean HasModel(Pair<P1, P2> p, Choice<S1, S2> s1, Choice<S1, S2> s2) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/*
	 * currently prioritizes the first algebra
	 */
	@Override
	public Choice<S1, S2> generateWitness(Pair<P1, P2> p) throws TimeoutException {
		S1 witL = ba1.generateWitness(p.first);
		if (witL != null) 
			return new InL<S1, S2>(witL);
		S2 witR = ba2.generateWitness(p.second);
		if (witR != null)
			return new InR<S1, S2>(witR);
		return null;
	}

	@Override
	public Pair<Choice<S1, S2>, Choice<S1, S2>> generateWitnesses(Pair<P1, P2> p) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<Pair<P1, P2>> GetSeparatingPredicates(
			ArrayList<Collection<Choice<S1, S2>>> groups, long timeout) throws TimeoutException {
		ArrayList<Collection<S1>> g1 = new ArrayList<Collection<S1>>();
		ArrayList<Collection<S2>> g2 = new ArrayList<Collection<S2>>();
		for (Collection<Choice<S1, S2>> c : groups) {
			Collection<S1> s1set = new HashSet<S1>();
			Collection<S2> s2set = new HashSet<S2>();
			for(Choice<S1, S2> p : c) {
				if (p.isLeft()){
					InL<S1, S2> cast = (InL<S1, S2>)p; 
					s1set.add(cast.left);
				}
				else{ //s.isRight()
					InR<S1, S2> cast = (InR<S1, S2>)p; 		
					s2set.add(cast.right);
				}	
			}
			g1.add(s1set);
			g2.add(s2set);
		}
		ArrayList<P1> preds1 = ba1.GetSeparatingPredicates(g1, timeout);
		ArrayList<P2> preds2 = ba2.GetSeparatingPredicates(g2, timeout);
		checkArgument(preds1.size() == preds2.size());
		ArrayList<Pair<P1, P2>> ret = new ArrayList<Pair<P1, P2>>();
		for(int i = 0; i < preds1.size(); i++) {
			ret.add(new Pair<P1, P2>(preds1.get(i), preds2.get(i)));
		}
		return ret;
	}
}

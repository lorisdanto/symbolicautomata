
/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory.svpa.equalityalgebra;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.NotImplementedException;
import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

/**
 * CharSolver: an interval based solver for the theory of characters For binary
 * predicates currently assumes equality
 */

// TODO lift to arbitrary boolean algebras
public class EqualityAlgebra<P,S> extends BooleanAlgebra<EqualityPredicate<P,S>, S> {

	BooleanAlgebra<P, S> unarySolver;

	public EqualityAlgebra(BooleanAlgebra<P, S> unarySolver) {
		this.unarySolver = unarySolver;
	}

	@Override
	public EqualityPredicate<P,S> MkNot(EqualityPredicate<P,S> p) throws TimeoutException {
		if (p instanceof UnaryPredicate<?,?>) {
			return new UnaryPredicate<P,S>(unarySolver.MkNot(((UnaryPredicate<P,S>) p).getPredicate()),p.isReturn());
		} else {
			BinaryEqualityPredicate<P,S> u = (BinaryEqualityPredicate<P,S>) p;				
			P newEq = unarySolver.MkNot(u.equals);
			ArrayList<Pair<P,P>> newUneq = new ArrayList<>();
															
			P leftover = unarySolver.True();
			for (Pair<P,P> pair : u.notEqual) {
				leftover = unarySolver.MkAnd(leftover, unarySolver.MkNot(pair.first));
				
				P newRight = unarySolver.MkNot(pair.second);
				if(unarySolver.IsSatisfiable(newRight))
					newUneq.add(new Pair<P,P>(pair.first, newRight));
			}
			if(unarySolver.IsSatisfiable(leftover))
				newUneq.add(new Pair<P,P>(leftover, unarySolver.True()));				
			
			return new BinaryEqualityPredicate<P,S>(newEq, newUneq);
		}
	}

	@Override
	public EqualityPredicate<P,S> MkOr(Collection<EqualityPredicate<P,S>> clctn) throws TimeoutException {
		EqualityPredicate<P,S> or = False();
		for (EqualityPredicate<P,S> a : clctn) {
			or = MkOr(or, a);
		}
		return or;
	}

	@Override
	public EqualityPredicate<P,S> MkOr(EqualityPredicate<P,S> u1, EqualityPredicate<P,S> u2) throws TimeoutException {
		if (u1 instanceof UnaryPredicate<?,?>) {
			UnaryPredicate<P,S> u1c = (UnaryPredicate<P,S>) u1;
			if (u2 instanceof UnaryPredicate<?,?>) {
				UnaryPredicate<P,S> u2c = (UnaryPredicate<P,S>) u2;				
				if(u1c.isReturn()){
					if(u2c.isReturn()){
						P cp = unarySolver.MkOr(u1c.getPredicate(), u2c.getPredicate());
						return new UnaryPredicate<>(cp, true);
					}else{
						throw new NotImplementedException("This should not happen for SVPA");
					}
				}else{
					if(u2c.isReturn()){
						// u1 is UnaryPredicate<?,?> and call, u2 is UnaryPredicate<?,?> and return
						ArrayList<Pair<P,P>> newUneq = new ArrayList<>();
						newUneq.add(new Pair<P,P>(unarySolver.True(),u2c.getPredicate()));
						newUneq.add(new Pair<P,P>(u1c.getPredicate(),unarySolver.True()));
						return new BinaryEqualityPredicate<P,S>(unarySolver.MkOr(u1c.getPredicate(),u2c.getPredicate()), newUneq);						
					}else{
						// u1 is UnaryPredicate<?,?> and call, u2 is UnaryPredicate<?,?> and call
						return new UnaryPredicate<>(unarySolver.MkOr(u1c.getPredicate(),u2c.getPredicate()));
					}
				}
			} else {
				BinaryEqualityPredicate<P,S> u2c = (BinaryEqualityPredicate<P,S>) u2;
				// u1 is UnaryPredicate<?,?>, u2 is BinaryEqualityPredicate<?,?>
				if(u1c.isReturn()){
					P newEq = unarySolver.MkOr(u1c.getPredicate(), u2c.equals);					
					ArrayList<Pair<P,P>> newUneq = new ArrayList<>(u2c.notEqual);
					newUneq.add(new Pair<P,P>(unarySolver.True(), u1c.getPredicate()));
					
					BinaryEqualityPredicate<P,S> pp= new BinaryEqualityPredicate<>(newEq, newUneq);
					pp.normalize(unarySolver);
					return pp;
				}else{
					P newEq = unarySolver.MkOr(u1c.getPredicate(), u2c.equals);
					
					ArrayList<Pair<P,P>> newUneq = new ArrayList<>(u2c.notEqual);
					newUneq.add(new Pair<P,P>(u1c.getPredicate(), unarySolver.True()));
					
					BinaryEqualityPredicate<P,S> pp= new BinaryEqualityPredicate<>(newEq, newUneq);
					pp.normalize(unarySolver);
					return pp;
				}						
			}
		} else {
			//u1 is BinaryEqualityPredicate<?,?>
			BinaryEqualityPredicate<P,S> u1c = (BinaryEqualityPredicate<P,S>) u1;
			if (u2 instanceof UnaryPredicate<?,?>) {
				UnaryPredicate<P,S> u2c = (UnaryPredicate<P,S>) u2;
				if(u2c.isReturn()){
					// u1 is BinaryEqualityPredicate<?,?> and return, u2 is UnaryPredicate<?,?> and return										
					return MkOr(u2, u1);
				}else{
					// u1 is BinaryEqualityPredicate<?,?> and return, u2 is UnaryPredicate<?,?> and call
					throw new NotImplementedException("This should not happen for SVPA");
				}				
			} else {
				//u1 is BinaryEqualityPredicate<?,?>, u2 is a binaryUnaryPredicate<?,?>
				BinaryEqualityPredicate<P,S> u2c = (BinaryEqualityPredicate<P,S>) u2;
				P newEq = unarySolver.MkOr(u1c.equals, u2c.equals);

				ArrayList<Pair<P,P>> newUneq = new ArrayList<>(u1c.notEqual);
				newUneq.addAll(u2c.notEqual);
				BinaryEqualityPredicate<P,S> pp= new BinaryEqualityPredicate<>(newEq, newUneq);
				pp.normalize(unarySolver);
				return pp;
			}
		}
	}

	@Override
	public EqualityPredicate<P,S> MkAnd(Collection<EqualityPredicate<P,S>> clctn) throws TimeoutException {
		EqualityPredicate<P,S> and = True();
		for (EqualityPredicate<P,S> a : clctn) {
			and = MkAnd(and, a);
		}
		return and;
	}

	@Override
	public EqualityPredicate<P,S> MkAnd(EqualityPredicate<P,S> u1, EqualityPredicate<P,S> u2) throws TimeoutException {
		if (u1 instanceof UnaryPredicate<?,?>) {
			UnaryPredicate<P,S> u1c = (UnaryPredicate<P,S>) u1;
			if (u2 instanceof UnaryPredicate<?,?>) {
				UnaryPredicate<P,S> u2c = (UnaryPredicate<P,S>) u2;				
				if(u1c.isReturn()){
					if(u2c.isReturn()){
						// u1 is UnaryPredicate<?,?> and return, u2 is UnaryPredicate<?,?> and return
						return new UnaryPredicate<>(unarySolver.MkAnd(u1c.getPredicate(), u2c.getPredicate()),true);
					}else{
						// u1 is UnaryPredicate<?,?> and return, u2 is UnaryPredicate<?,?> and call
						throw new NotImplementedException("This should not happen for SVPA");
					}
				}else{
					if(u2c.isReturn()){
						// u1 is UnaryPredicate<?,?> and call, u2 is UnaryPredicate<?,?> and return
						ArrayList<Pair<P,P>> newUneq = new ArrayList<>();
						newUneq.add(new Pair<P,P>(u1c.getPredicate(),u2c.getPredicate()));
						return new BinaryEqualityPredicate<>(unarySolver.MkAnd(u1c.getPredicate(),u2c.getPredicate()), newUneq);
					}else{
						// u1 is UnaryPredicate<?,?> and call, u2 is UnaryPredicate<?,?> and call
						return new UnaryPredicate<>(unarySolver.MkAnd(u1c.getPredicate(), u2c.getPredicate()));
					}
				}
			} else {
				BinaryEqualityPredicate<P,S> u2c = (BinaryEqualityPredicate<P,S>) u2;
				// u1 is UnaryPredicate<?,?>, u2 is BinaryEqualityPredicate<?,?>
				if(u1c.isReturn()){
					P newEq = unarySolver.MkAnd(u1c.getPredicate(), u2c.equals);				
					ArrayList<Pair<P,P>> newUneq = new ArrayList<>();
					for(Pair<P,P> pair: u2c.notEqual){
						P conj = unarySolver.MkAnd(pair.second,u1c.getPredicate());
						if(unarySolver.IsSatisfiable(conj))
							newUneq.add(new Pair<P,P>(pair.first,conj));
					}
					
					return new BinaryEqualityPredicate<>(newEq, newUneq);
				}else{
					P newEq = unarySolver.MkAnd(u1c.getPredicate(), u2c.equals);				
					ArrayList<Pair<P,P>> newUneq = new ArrayList<>();
					for(Pair<P,P> pair: u2c.notEqual){
						P conj = unarySolver.MkAnd(pair.first,u1c.getPredicate());
						if(unarySolver.IsSatisfiable(conj))
							newUneq.add(new Pair<P,P>(conj,pair.second));
					}
					return new BinaryEqualityPredicate<>(newEq, newUneq);
				}						
			}
		} else {
			//u1 is BinaryEqualityPredicate<?,?>
			BinaryEqualityPredicate<P,S> u1c = (BinaryEqualityPredicate<P,S>) u1;
			if (u2 instanceof UnaryPredicate<?,?>) {
				UnaryPredicate<P,S> u2c = (UnaryPredicate<P,S>) u2;
				if(u2c.isReturn()){
					// u1 is BinaryEqualityPredicate<?,?> and return, u2 is UnaryPredicate<?,?> and return										
					return MkAnd(u2, u1);
				}else{
					// u1 is BinaryEqualityPredicate<?,?> and return, u2 is UnaryPredicate<?,?> and call
					throw new NotImplementedException("You are using a call predicate in a return transition");
				}				
			} else {
				//u1 is BinaryEqualityPredicate<?,?>, u2 is a binaryUnaryPredicate<?,?>
				BinaryEqualityPredicate<P,S> u2c = (BinaryEqualityPredicate<P,S>) u2;
				P newEq = unarySolver.MkAnd(u1c.equals, u2c.equals);

				ArrayList<Pair<P,P>> newUneq = new ArrayList<>();
				for (Pair<P,P> pair1 : u1c.notEqual) {
					for (Pair<P,P> pair2 : u2c.notEqual) {
						P newFirst = unarySolver.MkAnd(pair1.first, pair2.first);
						if (unarySolver.IsSatisfiable(newFirst)) {
							P newSecond = unarySolver.MkAnd(pair1.second, pair2.second);
							if (unarySolver.IsSatisfiable(newSecond))
								newUneq.add(new Pair<P,P>(newFirst, newSecond));
						}
					}
				}
				return new BinaryEqualityPredicate<>(newEq, newUneq);
			}
		}		
	}

	@Override
	public EqualityPredicate<P,S> True() {
		return new UnaryPredicate<>(unarySolver.True());
	}

	@Override
	public EqualityPredicate<P,S> False() {
		return new UnaryPredicate<>(unarySolver.False());
	}

	@Override
	public boolean AreEquivalent(EqualityPredicate<P,S> u1, EqualityPredicate<P,S> u2) throws TimeoutException {
		boolean nonEquivalent = IsSatisfiable(MkAnd(u1, MkNot(u2))) || IsSatisfiable(MkAnd(MkNot(u1), u2));
		return !nonEquivalent;
	}

	@Override
	public boolean IsSatisfiable(EqualityPredicate<P,S> p) throws TimeoutException {
		if (p instanceof UnaryPredicate<?,?>) {
			return unarySolver.IsSatisfiable(((UnaryPredicate<P,S>) p).getPredicate());
		} else {
			BinaryEqualityPredicate<P,S> u = (BinaryEqualityPredicate<P,S>) p;
			if(unarySolver.IsSatisfiable(u.equals))
				return true;
			else{
				for(Pair<P,P> pair: u.notEqual){
					P left = unarySolver.MkAnd(pair.first,unarySolver.MkNot(pair.second));
					if(unarySolver.IsSatisfiable(left))
						return true;
					P right = unarySolver.MkAnd(pair.second,unarySolver.MkNot(pair.first));
					if(unarySolver.IsSatisfiable(right))
						return true;					
					S c1 = unarySolver.generateWitness(pair.first);
					return unarySolver.IsSatisfiable(unarySolver.MkAnd(pair.second,unarySolver.MkNot(unarySolver.MkAtom(c1))));
				}
				return false;
			}
		}
	}

	@Override
	public boolean HasModel(EqualityPredicate<P,S> p, S s) throws TimeoutException {
		if (p instanceof UnaryPredicate<?,?>) 
			return unarySolver.HasModel(((UnaryPredicate<P,S>)p).getPredicate(), s);
		else 
			throw new IllegalArgumentException("shouldn't ask for a unary witness on a binary predicate");
	}

	@Override
	public boolean HasModel(EqualityPredicate<P,S> p, S s1, S s2) throws TimeoutException {
		if (p instanceof UnaryPredicate<?,?>) 
			return unarySolver.HasModel(((UnaryPredicate<P,S>)p).getPredicate(), s1);
		else{ 
			BinaryEqualityPredicate<P,S> pc = (BinaryEqualityPredicate<P,S>) p;
			P atom1 = unarySolver.MkAtom(s1);
			P atom2 = unarySolver.MkAtom(s2);
			if(unarySolver.AreEquivalent(atom1,atom2)){
				return unarySolver.HasModel(pc.equals,s1);
			}else{
				for(Pair<P,P> pair: pc.notEqual)
					if(unarySolver.HasModel(pair.first, s1) && unarySolver.HasModel(pair.second, s2))
						return true;					
				
				return false;
			}
		}
	}

	@Override
	public S generateWitness(EqualityPredicate<P,S> p) throws TimeoutException {
		if (p instanceof UnaryPredicate<?,?>) 
			return unarySolver.generateWitness(((UnaryPredicate<P,S>)p).getPredicate());
		else{ 
			throw new NotImplementedException("This shouldn't happen");
		}
	}

	@Override
	public Pair<S, S> generateWitnesses(EqualityPredicate<P,S> p) throws TimeoutException {
		if (p instanceof UnaryPredicate<?,?>) {
			S c =  unarySolver.generateWitness(((UnaryPredicate<P,S>)p).getPredicate());
			return new Pair<S, S>(c, c);
		} else {
			BinaryEqualityPredicate<P,S> u = (BinaryEqualityPredicate<P,S>) p;
			if(unarySolver.IsSatisfiable(u.equals)){
				S c =  unarySolver.generateWitness(u.equals);
				return new Pair<S, S>(c, c);
			}
			else{
				for(Pair<P,P> pair: u.notEqual){
					P left = unarySolver.MkAnd(pair.first,unarySolver.MkNot(pair.second));
					if(unarySolver.IsSatisfiable(left)){
						S cl =  unarySolver.generateWitness(left);
						S cr =  unarySolver.generateWitness(pair.second);
						return new Pair<S, S>(cl, cr);
					}
					P right = unarySolver.MkAnd(pair.second,unarySolver.MkNot(pair.first));
					if(unarySolver.IsSatisfiable(right)){
						S cl =  unarySolver.generateWitness(pair.first);
						S cr =  unarySolver.generateWitness(right);
						return new Pair<S, S>(cl, cr);
					}	
					S cl = unarySolver.generateWitness(pair.first);
					P leftover =unarySolver.MkAnd(pair.second,unarySolver.MkNot(unarySolver.MkAtom(cl)));
					if(unarySolver.IsSatisfiable(leftover)){
						S cr =  unarySolver.generateWitness(leftover);
						return new Pair<S, S>(cl, cr);
					}
				}				
			}
		}
		return null;
	}

	@Override
	public EqualityPredicate<P,S> MkAtom(S s){
		return new UnaryPredicate<P,S>(unarySolver.MkAtom(s));
	}

}

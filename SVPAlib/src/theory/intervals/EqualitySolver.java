
/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory.intervals;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import theory.characters.BinaryCharPred;
import theory.characters.CharPred;
import theory.characters.ICharPred;
import theory.characters.StdCharPred;
import utilities.Pair;

/**
 * CharSolver: an interval based solver for the theory of characters For binary
 * predicates currently assumes equality
 */

// TODO lift to arbitrary boolean algebras
public class EqualitySolver extends BooleanAlgebra<ICharPred, Character> {

	UnaryCharIntervalSolver usolver;

	public EqualitySolver() {
		usolver = new UnaryCharIntervalSolver();
	}

	@Override
	public ICharPred MkNot(ICharPred p) {
		if (p instanceof CharPred) {
			CharPred cp = usolver.MkNot((CharPred) p);
			if(p.isReturn())
				cp.setAsReturn();
			return cp;
		} else {
			BinaryCharPred u = (BinaryCharPred) p;				
			CharPred newEq = usolver.MkNot(u.equals);
			ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>();
															
			CharPred leftover = usolver.True();
			for (Pair<CharPred, CharPred> pair : u.notEqual) {
				leftover = usolver.MkAnd(leftover, usolver.MkNot(pair.first));
				
				CharPred newRight = usolver.MkNot(pair.second);
				if(usolver.IsSatisfiable(newRight))
					newUneq.add(new Pair<CharPred, CharPred>(pair.first, newRight));
			}
			if(usolver.IsSatisfiable(leftover))
				newUneq.add(new Pair<CharPred, CharPred>(leftover, usolver.True()));				
			
			return new BinaryCharPred(newEq, newUneq);
		}
	}

	@Override
	public ICharPred MkOr(Collection<ICharPred> clctn) throws TimeoutException {
		ICharPred or = StdCharPred.FALSE;
		for (ICharPred a : clctn) {
			or = MkOr(or, a);
		}
		return or;
	}

	@Override
	public ICharPred MkOr(ICharPred u1, ICharPred u2) throws TimeoutException {
		if (u1 instanceof CharPred) {
			CharPred u1c = (CharPred) u1;
			if (u2 instanceof CharPred) {
				CharPred u2c = (CharPred) u2;				
				if(u1c.isReturn()){
					if(u2c.isReturn()){
						// u1 is CharPred and return, u2 is CharPred and return
						CharPred cp = usolver.MkOr(u1c, u2c);
						cp.setAsReturn();
						return cp;
					}else{
						// u1 is CharPred and return, u2 is CharPred and call
						throw new NotImplementedException("This should not happen for SVPA");
					}
				}else{
					if(u2c.isReturn()){
						// u1 is CharPred and call, u2 is CharPred and return
						ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>();
						newUneq.add(new Pair<CharPred, CharPred>(StdCharPred.TRUE,u2c));
						newUneq.add(new Pair<CharPred, CharPred>(u1c,StdCharPred.TRUE));
						return new BinaryCharPred(usolver.MkOr(u1c,u2c), newUneq);
					}else{
						// u1 is CharPred and call, u2 is CharPred and call
						return usolver.MkOr(u1c, u2c);
					}
				}
			} else {
				BinaryCharPred u2c = (BinaryCharPred) u2;
				// u1 is CharPred, u2 is BinaryCharPred
				if(u1c.isReturn()){
					CharPred newEq = usolver.MkOr(u1c, u2c.equals);					
					ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>(u2c.notEqual);
					newUneq.add(new Pair<CharPred, CharPred>(StdCharPred.TRUE, u1c));
					
					BinaryCharPred pp= new BinaryCharPred(newEq, newUneq);
					pp.normalize(usolver);
					return pp;
				}else{
					CharPred newEq = usolver.MkOr(u1c, u2c.equals);
					
					ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>(u2c.notEqual);
					newUneq.add(new Pair<CharPred, CharPred>(u1c, StdCharPred.TRUE));
					
					BinaryCharPred pp= new BinaryCharPred(newEq, newUneq);
					pp.normalize(usolver);
					return pp;
				}						
			}
		} else {
			//u1 is BinaryCharPred
			BinaryCharPred u1c = (BinaryCharPred) u1;
			if (u2 instanceof CharPred) {
				CharPred u2c = (CharPred) u2;
				if(u2c.isReturn()){
					// u1 is BinaryCharPred and return, u2 is CharPred and return										
					return MkOr(u2, u1);
				}else{
					// u1 is BinaryCharPred and return, u2 is CharPred and call
					throw new NotImplementedException("This should not happen for SVPA");
				}				
			} else {
				//u1 is BinaryCharPred, u2 is a binaryCharPred
				BinaryCharPred u2c = (BinaryCharPred) u2;
				CharPred newEq = usolver.MkOr(u1c.equals, u2c.equals);

				ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>(u1c.notEqual);
				newUneq.addAll(u2c.notEqual);
				BinaryCharPred pp= new BinaryCharPred(newEq, newUneq);
				pp.normalize(usolver);
				return pp;
			}
		}
	}

	@Override
	public ICharPred MkAnd(Collection<ICharPred> clctn) {
		ICharPred and = StdCharPred.TRUE;
		for (ICharPred a : clctn) {
			and = MkAnd(and, a);
		}
		return and;
	}

	@Override
	public ICharPred MkAnd(ICharPred u1, ICharPred u2) {
		if (u1 instanceof CharPred) {
			CharPred u1c = (CharPred) u1;
			if (u2 instanceof CharPred) {
				CharPred u2c = (CharPred) u2;				
				if(u1c.isReturn()){
					if(u2c.isReturn()){
						// u1 is CharPred and return, u2 is CharPred and return
						CharPred cp = usolver.MkAnd(u1c, u2c);
						cp.setAsReturn();
						return cp;
					}else{
						// u1 is CharPred and return, u2 is CharPred and call
						throw new NotImplementedException("This should not happen for SVPA");
					}
				}else{
					if(u2c.isReturn()){
						// u1 is CharPred and call, u2 is CharPred and return
						ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>();
						newUneq.add(new Pair<CharPred, CharPred>(u1c,u2c));
						return new BinaryCharPred(usolver.MkAnd(u1c,u2c), newUneq);
					}else{
						// u1 is CharPred and call, u2 is CharPred and call
						return usolver.MkAnd(u1c, u2c);
					}
				}
			} else {
				BinaryCharPred u2c = (BinaryCharPred) u2;
				// u1 is CharPred, u2 is BinaryCharPred
				if(u1c.isReturn()){
					CharPred newEq = usolver.MkAnd(u1c, u2c.equals);				
					ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>();
					for(Pair<CharPred,CharPred> pair: u2c.notEqual){
						CharPred conj = usolver.MkAnd(pair.second,u1c);
						if(usolver.IsSatisfiable(conj))
							newUneq.add(new Pair<CharPred, CharPred>(pair.first,conj));
					}
					
					BinaryCharPred pp= new BinaryCharPred(newEq, newUneq);
					return pp;
				}else{
					CharPred newEq = usolver.MkAnd(u1c, u2c.equals);				
					ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>();
					for(Pair<CharPred,CharPred> pair: u2c.notEqual){
						CharPred conj = usolver.MkAnd(pair.first,u1c);
						if(usolver.IsSatisfiable(conj))
							newUneq.add(new Pair<CharPred, CharPred>(conj,pair.second));
					}
					BinaryCharPred pp= new BinaryCharPred(newEq, newUneq);
					return pp;
				}						
			}
		} else {
			//u1 is BinaryCharPred
			BinaryCharPred u1c = (BinaryCharPred) u1;
			if (u2 instanceof CharPred) {
				CharPred u2c = (CharPred) u2;
				if(u2c.isReturn()){
					// u1 is BinaryCharPred and return, u2 is CharPred and return										
					return MkAnd(u2, u1);
				}else{
					// u1 is BinaryCharPred and return, u2 is CharPred and call
					throw new NotImplementedException("You are using a call predicate in a return transition");
				}				
			} else {
				//u1 is BinaryCharPred, u2 is a binaryCharPred
				BinaryCharPred u2c = (BinaryCharPred) u2;
				CharPred newEq = usolver.MkAnd(u1c.equals, u2c.equals);

				ArrayList<Pair<CharPred, CharPred>> newUneq = new ArrayList<>();
				for (Pair<CharPred, CharPred> pair1 : u1c.notEqual) {
					for (Pair<CharPred, CharPred> pair2 : u2c.notEqual) {
						CharPred newFirst = usolver.MkAnd(pair1.first, pair2.first);
						if (usolver.IsSatisfiable(newFirst)) {
							CharPred newSecond = usolver.MkAnd(pair1.second, pair2.second);
							if (usolver.IsSatisfiable(newSecond))
								newUneq.add(new Pair<CharPred, CharPred>(newFirst, newSecond));
						}
					}
				}
				return new BinaryCharPred(newEq, newUneq);
			}
		}		
	}

	@Override
	public ICharPred True() {
		return StdCharPred.TRUE;
	}

	@Override
	public ICharPred False() {
		return StdCharPred.FALSE;
	}

	@Override
	public boolean AreEquivalent(ICharPred u1, ICharPred u2) {
		checkNotNull(u1);
		checkNotNull(u2);

		boolean nonEquivalent = IsSatisfiable(MkAnd(u1, MkNot(u2))) || IsSatisfiable(MkAnd(MkNot(u1), u2));
		return !nonEquivalent;
	}

	@Override
	public boolean IsSatisfiable(ICharPred p) {
		if (p instanceof CharPred) {
			return usolver.IsSatisfiable((CharPred) p);
		} else {
			BinaryCharPred u = (BinaryCharPred) p;
			if(usolver.IsSatisfiable(u.equals))
				return true;
			else{
				for(Pair<CharPred,CharPred> pair: u.notEqual){
					CharPred left = usolver.MkAnd(pair.first,usolver.MkNot(pair.second));
					if(usolver.IsSatisfiable(left))
						return true;
					CharPred right = usolver.MkAnd(pair.second,usolver.MkNot(pair.first));
					if(usolver.IsSatisfiable(right))
						return true;					
					Character c1 = usolver.generateWitness(pair.first);
					return usolver.IsSatisfiable(usolver.MkAnd(pair.second,usolver.MkNot(new CharPred(c1))));
				}
				return false;
			}
		}
	}

	@Override
	public boolean HasModel(ICharPred p, Character s) {
		if (p instanceof CharPred) 
			return usolver.HasModel((CharPred)p, s);
		else 
			throw new IllegalArgumentException("shouldn't ask for a unary witness on a binary predicate");
	}

	@Override
	public boolean HasModel(ICharPred p, Character s1, Character s2) {
		if (p instanceof CharPred) 
			return usolver.HasModel((CharPred)p, s1);
		else{ 
			BinaryCharPred pc = (BinaryCharPred) p;
			if(s1==s2){
				return usolver.HasModel(pc.equals,s1);
			}else{
				for(Pair<CharPred,CharPred> pair: pc.notEqual){
					if(usolver.HasModel(pair.first, s1) && usolver.HasModel(pair.second, s2))
						return true;					
				}
				return false;
			}
		}
	}

	@Override
	public Character generateWitness(ICharPred p) {
		if (p instanceof CharPred) 
			return usolver.generateWitness((CharPred)p);
		else{ 
			throw new NotImplementedException("This shouldn't happen");
		}
	}

	@Override
	public Pair<Character, Character> generateWitnesses(ICharPred p) {
		if (p instanceof CharPred) {
			Character c =  usolver.generateWitness((CharPred)p);
			return new Pair<Character, Character>(c, c);
		} else {
			BinaryCharPred u = (BinaryCharPred) p;
			if(usolver.IsSatisfiable(u.equals)){
				Character c =  usolver.generateWitness(u.equals);
				return new Pair<Character, Character>(c, c);
			}
			else{
				for(Pair<CharPred,CharPred> pair: u.notEqual){
					CharPred left = usolver.MkAnd(pair.first,usolver.MkNot(pair.second));
					if(usolver.IsSatisfiable(left)){
						Character cl =  usolver.generateWitness(left);
						Character cr =  usolver.generateWitness(pair.second);
						return new Pair<Character, Character>(cl, cr);
					}
					CharPred right = usolver.MkAnd(pair.second,usolver.MkNot(pair.first));
					if(usolver.IsSatisfiable(right)){
						Character cl =  usolver.generateWitness(pair.first);
						Character cr =  usolver.generateWitness(right);
						return new Pair<Character, Character>(cl, cr);
					}	
					Character cl = usolver.generateWitness(pair.first);
					CharPred leftover =usolver.MkAnd(pair.second,usolver.MkNot(new CharPred(cl)));
					if(usolver.IsSatisfiable(leftover)){
						Character cr =  usolver.generateWitness(leftover);
						return new Pair<Character, Character>(cl, cr);
					}
				}				
			}
		}
		return null;
	}

	/**
	 * returns the string of a list of chars
	 * 
	 * @param chars
	 * @return
	 */
	public String stringOfList(List<Character> chars) {
		StringBuilder sb = new StringBuilder();
		for (Character c : checkNotNull(chars)) {
			sb.append(c);
		}
		return sb.toString();
	}

	@Override
	public ICharPred MkAtom(Character s){
		return new CharPred(s);
	}

}

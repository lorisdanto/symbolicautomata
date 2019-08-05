
/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory.svpa.equalityalgebra;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

/**
 * P: a set of Sacters represented as contiguous intervals
 */
public class BinaryEqualityPredicate<P,S> extends EqualityPredicate<P, S> {
	
	public ArrayList<Pair<P,P>> notEqual;
	public P equals;	
	
	private BinaryEqualityPredicate(){
		setAsReturn();
	}
	
	/**
	 * Return language is p, and it forces equality with call if forceEquality=true
	 */
	public BinaryEqualityPredicate(P p, boolean forceEquality, BooleanAlgebra<P, S> uba) {
		this();
		checkArgument(p != null);
		notEqual = new ArrayList<Pair<P,P>>();
		equals = p; 
		if(!forceEquality){
			notEqual.add(new Pair<P, P>(uba.True(), p));
		}
		
	}
	
	public void normalize(BooleanAlgebra<P, S> ba) throws TimeoutException{
		ArrayList<Pair<P,P>> newNotEqual = new ArrayList<Pair<P,P>>();
		
		ArrayList<P> firstProj = new ArrayList<>();
		for(Pair<P,P> pair: notEqual)
			firstProj.add(pair.first);
		
		Collection<Pair<P,ArrayList<Integer>>> minterms = ba.GetMinterms(firstProj);		
		for(Pair<P,ArrayList<Integer>> minterm:minterms){
			P currA = minterm.first;
			P currB = ba.False();
			for (int bit = 0; bit < notEqual.size(); bit++) 					
				if (minterm.second.get(bit) == 1)
					currB = ba.MkOr(currB, notEqual.get(bit).second);
				
			newNotEqual.add(new Pair<>(currA, currB));
		}
		
		notEqual = newNotEqual;
	}
	
	public BinaryEqualityPredicate(P eq, ArrayList<Pair<P,P>> notEqual) {
		this();
		checkArgument(eq != null && notEqual!=null);
		this.equals = eq;
		this.notEqual = notEqual;	
	}
	
	/**
	 * c and r without caring about equality
	 * @throws TimeoutException 
	 */
	public BinaryEqualityPredicate(P c, P r, BooleanAlgebra<P, Character> ba, BooleanAlgebra<P, S> uba) throws TimeoutException {
		this();
		checkArgument(c != null && r!=null);
		notEqual = new ArrayList<Pair<P,P>>();
		equals = ba.False();
		equals = ba.MkAnd(c,r); 
		notEqual.add(new Pair<P, P>(c,r));
	}

	public boolean isSatisfiedBy(S c1, S c2, BooleanAlgebra<P, S> uba) throws TimeoutException {
		P atomC1 = uba.MkAtom(c1);
		P atomC2 = uba.MkAtom(c2);
		if(uba.AreEquivalent(atomC1, atomC2))		
			return uba.HasModel(equals, c1);
		else
			for(Pair<P,P> pair: notEqual)
				if(uba.HasModel(pair.first,c1) && uba.HasModel(pair.second,c2))
					return true;
		return false;
	}
}

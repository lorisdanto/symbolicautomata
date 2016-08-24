package theory.cartesian;

import java.util.ArrayList;
import java.util.Collection;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

public class CartesianProduct<P1,P2> {
	
	ArrayList<Pair<P1,P2>> products;
	
	public CartesianProduct(){
		products = new ArrayList<>();
	}
	
	/**
	 * Return language is p, and it forces equality with call if forceEquality=true
	 */
	public CartesianProduct(P1 p1, P2 p2) {
		this();
		products = new ArrayList<>();
		products.add(new Pair<>(p1,p2));
	}	
	
	public CartesianProduct(ArrayList<Pair<P1,P2>> products) {		
		this.products = products;
	}
	
	public <S1,S2> void normalize(BooleanAlgebra<P1,S1> ba1,BooleanAlgebra<P2,S2> ba2) throws TimeoutException{
		ArrayList<Pair<P1,P2>> newProducts = new ArrayList<Pair<P1,P2>>();
		
		ArrayList<P1> firstProj = new ArrayList<>();
		for(Pair<P1,P2> pair: products)
			firstProj.add(pair.first);
		
		Collection<Pair<P1, ArrayList<Integer>>> minterms = ba1.GetMinterms(firstProj);		
		for(Pair<P1, ArrayList<Integer>> minterm:minterms){
			P1 currA = minterm.first;
			P2 currB = ba2.False();
			for (int bit = 0; bit < products.size(); bit++) 					
				if (minterm.second.get(bit) == 1)
					currB = ba2.MkOr(currB, products.get(bit).second);
				
			newProducts.add(new Pair<>(currA, currB));
		}
		
		products = newProducts;
	}
	
	public ArrayList<Pair<P1,P2>> getProducts(){
		return products;
	}
}

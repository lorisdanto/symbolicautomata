package theory.cartesian;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

public class CartesianProduct<P1,P2> {
	
	List<Pair<P1,P2>> products;
	
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
	
	public CartesianProduct(List<Pair<P1,P2>> products) {
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
			boolean addflag = false;
			for (int bit = 0; bit < products.size(); bit++) {
				if (minterm.second.get(bit) == 1) {
					addflag = true;
					currB = ba2.MkOr(currB, products.get(bit).second);
				}
			}

			if (addflag) //avoid adding unsat regions
				newProducts.add(new Pair<>(currA, currB));
		}
		
		products = newProducts;
		reduce(ba1, ba2);
	}
	
	public <S1,S2> void reduce(BooleanAlgebra<P1, S1> ba1, BooleanAlgebra<P2, S2> ba2) throws TimeoutException {
		List<Pair<P1,P2>> newProducts = new ArrayList<Pair<P1, P2>>();

		//consolidate based on y-component
		List<Pair<P1,P2>> consolid = new ArrayList<Pair<P1, P2>>();
		while (products.size() > 0) {
			Pair<P1, P2> curr = products.remove(0);
			for (int i = products.size() - 1; i >= 0; i--) {
				if (ba2.AreEquivalent(curr.second, products.get(i).second)) {
					curr.setFirst(ba1.MkOr(curr.first, products.get(i).first));
					products.remove(i);
				}
			}
			consolid.add(curr);
		}
		//consolidate based on x-component
		while (consolid.size() > 0) {
			Pair<P1, P2> curr = consolid.remove(0);
			for (int i = consolid.size() - 1; i >= 0; i--) {
				if (ba1.AreEquivalent(curr.first, consolid.get(i).first)) {
					curr.setSecond(ba2.MkOr(curr.second, consolid.get(i).second));
					consolid.remove(i);
				}
			}
			newProducts.add(curr);
		}
		products = newProducts;
	}

	public List<Pair<P1,P2>> getProducts(){
		return products;
	}

	@Override
	public String toString() {
		String ret = "";
		if (products.size() > 0)
			ret += "(" + products.get(0).first.toString() + "x" + products.get(0).second.toString() + ")";
		for (int i = 1; i < products.size(); i++)
			ret += "U(" + products.get(i).first.toString() + "x" + products.get(i).second.toString() + ")";
		return ret;
	}
}

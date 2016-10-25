package theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import theory.cartesian.CartesianProduct;
import utilities.Pair;

public class ProductAlgebra<P1, S1, P2, S2> extends BooleanAlgebra<CartesianProduct<P1, P2>, Pair<S1, S2>> {

	private BooleanAlgebra<P1,S1> ba1;
	private BooleanAlgebra<P2,S2> ba2;
	
	public ProductAlgebra(BooleanAlgebra<P1,S1> ba1, BooleanAlgebra<P2,S2> ba2) {
		this.ba1 = ba1;
		this.ba2 = ba2;
	}

	@Override
	public CartesianProduct<P1, P2> MkAtom(Pair<S1, S2> s) {
		return new CartesianProduct<>(ba1.MkAtom(s.first), ba2.MkAtom(s.second));
	}

	@Override
	public CartesianProduct<P1, P2> MkNot(CartesianProduct<P1, P2> p) throws TimeoutException {						
		ArrayList<Pair<P1, P2>> newProducts = new ArrayList<>();													
		P1 leftover = ba1.True();
		for (Pair<P1, P2> pair : p.getProducts()) {
			leftover = ba1.MkAnd(leftover, ba1.MkNot(pair.first));
			
			P2 newRight = ba2.MkNot(pair.second);
			if(ba2.IsSatisfiable(newRight))
				newProducts.add(new Pair<P1, P2>(pair.first, newRight));
		}
		if(ba1.IsSatisfiable(leftover))
			newProducts.add(new Pair<P1, P2>(leftover, ba2.True()));				
		
		return new CartesianProduct<>(newProducts);		
	}

	@Override
	public CartesianProduct<P1, P2> MkOr(Collection<CartesianProduct<P1, P2>> pset) throws TimeoutException {
		CartesianProduct<P1, P2> or = False();
		for (CartesianProduct<P1, P2> a : pset) {
			or = MkOr(or, a);
		}
		return or;
	}

	@Override
	public CartesianProduct<P1, P2> MkOr(CartesianProduct<P1, P2> p1, CartesianProduct<P1, P2> p2)
			throws TimeoutException {
			
		ArrayList<Pair<P1, P2>> newProducts = new ArrayList<>(p1.getProducts());
		newProducts.addAll(p2.getProducts());
		CartesianProduct<P1, P2> pp= new CartesianProduct<>(newProducts);
		pp.normalize(ba1, ba2);
		return pp;
	}

	@Override
	public CartesianProduct<P1, P2> MkAnd(Collection<CartesianProduct<P1, P2>> pset) throws TimeoutException {
		CartesianProduct<P1, P2> and = True();
		for (CartesianProduct<P1, P2> a : pset) {
			and = MkAnd(and, a);
		}
		return and;
	}

	@Override
	public CartesianProduct<P1, P2> MkAnd(CartesianProduct<P1, P2> p1, CartesianProduct<P1, P2> p2)
			throws TimeoutException {

		ArrayList<Pair<P1, P2>> newProducts = new ArrayList<>();
		for (Pair<P1, P2> pair1 : p1.getProducts()) 
			for (Pair<P1, P2> pair2 : p2.getProducts()) {
				P1 newFirst = ba1.MkAnd(pair1.first, pair2.first);
				if (ba1.IsSatisfiable(newFirst)) {
					P2 newSecond = ba2.MkAnd(pair1.second, pair2.second);
					if (ba2.IsSatisfiable(newSecond))
						newProducts.add(new Pair<P1, P2>(newFirst, newSecond));
				}
			}

		return new CartesianProduct<>(newProducts);
	}

	@Override
	public CartesianProduct<P1, P2> True() {
		return new CartesianProduct<>(ba1.True(),ba2.True());
	}

	@Override
	public CartesianProduct<P1, P2> False() {
		return new CartesianProduct<>();
	}

	@Override
	public boolean AreEquivalent(CartesianProduct<P1, P2> p1, CartesianProduct<P1, P2> p2) throws TimeoutException {
		return IsSatisfiable(MkAnd(p1, MkNot(p2))) || IsSatisfiable(MkAnd(MkNot(p1), p2));
	}

	@Override
	public boolean IsSatisfiable(CartesianProduct<P1, P2> p1) throws TimeoutException {
		for(Pair<P1,P2> p: p1.getProducts())
			if(ba1.IsSatisfiable(p.first) && ba2.IsSatisfiable(p.second))
				return true;
		
		return false;
	}

	@Override
	public boolean HasModel(CartesianProduct<P1, P2> p1, Pair<S1, S2> el) throws TimeoutException {
		for(Pair<P1,P2> p: p1.getProducts())
			if(ba1.HasModel(p.first,el.first) && ba2.HasModel(p.second,el.second))
				return true;
		
		return false;
	}

	@Override
	public boolean HasModel(CartesianProduct<P1, P2> p1, Pair<S1, S2> el1, Pair<S1, S2> el2) throws TimeoutException {
		for(Pair<P1,P2> p: p1.getProducts())
			if(ba1.HasModel(p.first,el1.first,el2.first) && ba2.HasModel(p.second,el1.second,el2.second))
				return true;
		
		return false;
	}

	@Override
	public Pair<S1, S2> generateWitness(CartesianProduct<P1, P2> p1) throws TimeoutException {
		List<P1> proj1 = new ArrayList<P1>();
		for (Pair<P1, P2> p : p1.getProducts())
			proj1.add(p.first);
		try {
			P1 union1 = ba1.MkOr(proj1);
			S1 x = ba1.generateWitness(union1);
			List<P2> proj2 = new ArrayList<P2>();
			for (int i = 0; i < proj1.size(); i++)
				if (ba1.HasModel(proj1.get(i), x))
					proj2.add(p1.getProducts().get(i).second);
			P2 union2 = ba2.MkOr(proj2);
			S2 y = ba2.generateWitness(union2);
			return new Pair<>(x, y);
		} catch (TimeoutException e) {
			for(Pair<P1,P2> p: p1.getProducts())
				if(ba1.IsSatisfiable(p.first) && ba2.IsSatisfiable(p.second))
					return new Pair<>(ba1.generateWitness(p.first), ba2.generateWitness(p.second));
		}
		
		return null;
	}

	@Override
	public Pair<Pair<S1, S2>, Pair<S1, S2>> generateWitnesses(CartesianProduct<P1, P2> p1) throws TimeoutException {
		for(Pair<P1,P2> p: p1.getProducts())
			if(ba1.IsSatisfiable(p.first) && ba2.IsSatisfiable(p.second)){
				Pair<S1, S1> w1 = ba1.generateWitnesses(p.first);
				Pair<S2, S2> w2 = ba2.generateWitnesses(p.second);
				return new Pair<>(new Pair<>(w1.first, w2.first),new Pair<>(w1.second, w2.second));
			}
		
		return null;
	}
	
	@Override
	public ArrayList<CartesianProduct<P1, P2>> GetSeparatingPredicates(
			ArrayList<Collection<Pair<S1, S2>>> groups, long timeout) throws TimeoutException {

		//compile all the concrete evidence we have for the x-values
		List<S1> xevid = new ArrayList<S1>();
		Map<S1, Set<Integer>> groupmap = new HashMap<S1, Set<Integer>>();
		for (int i = 0; i < groups.size(); i++) {
			for (Pair<S1, S2> p : groups.get(i)) {
				if (!xevid.contains(p.first))
					xevid.add(p.first);
				if (groupmap.get(p.first) == null)
					groupmap.put(p.first, new HashSet<Integer>());
				groupmap.get(p.first).add(i);
			}
		}

		ArrayList<Collection<S1>> xgroups = new ArrayList<Collection<S1>>();
		for (S1 s : xevid)
			xgroups.add(Arrays.asList(s));
		ArrayList<P1> xpreds = ba1.GetSeparatingPredicates(xgroups, timeout);

		ArrayList<Pair<P1, P2>> xypreds = new ArrayList<Pair<P1, P2>>();
		ArrayList<Integer> belongs = new ArrayList<Integer>();
		for (int i = 0; i < xpreds.size(); i++) {
			//if the evidence was from a single group then this predicate needs no y-component
			if (groupmap.get(xevid.get(i)).size() == 1) {
				xypreds.add(new Pair<P1, P2>(xpreds.get(i), ba2.True()));
				belongs.addAll(groupmap.get(xevid.get(i))); //add all in a set of size 1
			}
			//otherwise you have to split along the y-values in those groups that correspond to this x-evidence
			if (groupmap.get(xevid.get(i)).size() > 1) {
				ArrayList<Collection<S2>> ygroups = new ArrayList<Collection<S2>>();
				ArrayList<Integer> source = new ArrayList<Integer>();
				for (int index : groupmap.get(xevid.get(i))) {
					Set<S2> yevid = new HashSet<S2>();
					for (Pair<S1, S2> p : groups.get(index)) {
						if (p.first.equals(xevid.get(i))) {
							yevid.add(p.second);
						}
					}
					ygroups.add(yevid);
					source.add(index);
				}
				ArrayList<P2> ypreds = ba2.GetSeparatingPredicates(ygroups, timeout);
				for(int j = 0; j < ypreds.size(); j++) {
					xypreds.add(new Pair<P1, P2>(xpreds.get(i), ypreds.get(j)));
					belongs.add(source.get(j));
				}
			}
		}

		ArrayList<ArrayList<Pair<P1, P2>>> prods = new ArrayList<ArrayList<Pair<P1, P2>>>();
		ArrayList<CartesianProduct<P1, P2>> ret = new ArrayList<CartesianProduct<P1, P2>>();
		for (int i = 0; i < groups.size(); i++)
			prods.add(new ArrayList<Pair<P1, P2>>());
		for (int i = 0; i < xypreds.size(); i++)
			prods.get(belongs.get(i)).add(xypreds.get(i));
		for (int i = 0; i < prods.size(); i++)
			ret.add(new CartesianProduct<P1, P2>(prods.get(i)));
		for (CartesianProduct<P1, P2> region : ret)
			region.normalize(ba1, ba2);
		return ret;
	}

}

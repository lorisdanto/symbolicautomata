package Learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import theory.ProductAlgebra;
import theory.cartesian.CartesianProduct;
import theory.intervals.IntPred;
import utilities.Pair;

//Implements a separating predicates method following the partial order learning from
//I. Mens and O. Maler "Learning Regular Languages over Large Ordered Alphabets" (LMCS 2015)
public class MMConeAlgebra extends ProductAlgebra<IntPred, Integer, IntPred, Integer> {
	BooleanAlgebra<IntPred, Integer> ba;
	public MMConeAlgebra(BooleanAlgebra<IntPred, Integer> ba) {
		super(ba,ba);
		this.ba = ba;
	}

	/*
	 * This makes a very strong assumption that the predicates (and groups)
	 * are both monotone and connected; i.e.
	 * you cannot have a single transition of the form ([0,5]x[0,5]) U ([10,20]x[10,20])
	 * This assumption comes from their paper and holds on their example automaton,
	 * but is unsound in general.
	 */
	@Override
	public ArrayList<CartesianProduct<IntPred, IntPred>> GetSeparatingPredicates(
			ArrayList<Collection<Pair<Integer, Integer>>> groups, long timeout) throws TimeoutException {
		//System.out.println("groups:" + groups);
		List<Collection<Pair<Integer, Integer>>> copy =
				new ArrayList<Collection<Pair<Integer, Integer>>>(groups);
		Collections.sort(copy, new Comparator<Collection<Pair<Integer, Integer>>>() {
			@Override
			public int compare(Collection<Pair<Integer, Integer>> o1, Collection<Pair<Integer, Integer>> o2) {
				if (o1.size() == 0)
					return -1;
				if (o2.size() == 0)
					return 1;
				for (Pair<Integer, Integer> p1 : o1) {
					for (Pair<Integer, Integer> p2 : o2) {
						if (p1.first <= p2.first && p1.second <= p2.second)
							return -1;
						if (p1.first >= p2.first && p1.second >= p2.second)
							return 1;
					}
				}
				return 0;
			}
		});
		//System.out.println("sorted:" + copy);
		Map<Integer, Integer> invmove = new HashMap<Integer, Integer>();
		for (int i = 0; i < copy.size(); i++) {
			for (int j = 0; j < groups.size(); j++) {
				if (copy.get(i).equals(groups.get(j)))
					invmove.put(j,i);
			}
		}
		boolean orig = false;
		for (Collection<Pair<Integer, Integer>> col : copy) {
			for (Pair<Integer, Integer> p : col) {
				if (p.equals(new Pair<Integer, Integer>(0,0))) {
					orig = true;
					break;
				}
			}
		}
		if (!orig) {
			for (int i = 0; i < copy.size(); i++) {
				if (copy.get(i).size() > 0) {
					//System.out.println("adding (0,0) to " + copy.get(i));
					copy.get(i).add(new Pair<Integer, Integer>(0,0));
					break;
				}
			}
		}
		//System.out.println(invmove);
		ArrayList<CartesianProduct<IntPred, IntPred>> temp = new ArrayList<CartesianProduct<IntPred, IntPred>>();
		for (int i = 0; i < copy.size(); i++) {
			List<Pair<IntPred, IntPred>> pr = new ArrayList<Pair<IntPred, IntPred>>();
			for (Pair<Integer, Integer> pair : copy.get(i))
				pr.add(new Pair<IntPred,IntPred>(ba.MkAnd(ba.True(),new IntPred(pair.first,null)), ba.MkAnd(ba.True(),new IntPred(pair.second,null))));
			CartesianProduct<IntPred, IntPred> prod = new CartesianProduct<IntPred, IntPred>(pr);
			temp.add(prod);
		}
		//System.out.println("cones:" + temp);
		for (int i = 0; i < temp.size() - 1; i++) {
			for (int j = i + 1; j < temp.size(); j++) {
				temp.set(i, MkAnd(temp.get(i), MkNot(temp.get(j))));
			}
		}
		//System.out.println("preds:" + temp);
		ArrayList<CartesianProduct<IntPred, IntPred>> ret = new ArrayList<CartesianProduct<IntPred, IntPred>>();
		//System.out.println(temp);
		for (int i = 0; i < groups.size(); i++) {
			CartesianProduct<IntPred, IntPred> t = temp.get(invmove.get(i));
			t.normalize(this.ba, this.ba);
			ret.add(t);
			//ret.add(temp.get(invmove.get(i)).normalize(this.ba, this.ba));
		}
		//System.out.println(ret);
		return ret;
	}

}

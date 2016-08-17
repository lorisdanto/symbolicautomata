package learning.sfa;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

import org.sat4j.specs.TimeoutException;


public class Learner<P, S> {

	public Learner() {
		
	}
	
	public SFA<P, S> learn(Oracle<P, S> o, BooleanAlgebra<P, S> ba) throws TimeoutException {
		ObsTable table = new ObsTable();
		//initialize the table with epsilon and its continuation
		table.states.add(new ArrayList<S>());
		table.diff.add(new ArrayList<S>());
		table.rows.put(table.states.get(0), new ArrayList<Boolean>(Arrays.asList(o.checkMembership(table.states.get(0))))); //does the SFA accept the empty string
		table.boundary.add(Arrays.asList(ba.generateWitness(ba.True())));
		table.rows.put(table.boundary.get(0), new ArrayList<Boolean>(Arrays.asList(o.checkMembership(table.boundary.get(0)))));
		
		SFA<P, S> conjecture = null;
		List<S> cx = null;
		
		while(true) {
			fill(table, o);
			List<List<S>> newstates = table.checkClosed();
			while(newstates.size() > 0) {
				for (List<S> w : newstates) 
					table.promote(w, ba);
				fill(table, o);
				newstates = table.checkClosed();
			}
			
			//System.out.println("========Obs Table========");
			//System.out.println(table);
			
			conjecture = buildSFA(table, ba).mkTotal(ba);
			
			//System.out.println("========SFA guess========");
			//System.out.println(conjecture);

			cx = o.checkEquivalence(conjecture);
			if (cx == null) {
				//System.out.println("final table\n" + table);
				return conjecture;
			}
			
			//System.out.println("========counterex========");
			//System.out.println((cx == null ? "none" : cx));
			
			//process the counterexample
			table.process(cx, ba);
			
			//System.out.println("========TBLpostCX========");
			//System.out.println(table);

			//Scanner scanner = new Scanner(System.in);
			//scanner.nextLine();
		}
	}
	
	
	private SFA<P, S> buildSFA(ObsTable table, BooleanAlgebra<P, S> ba) throws TimeoutException {
		List<Integer> states = new ArrayList<Integer>();
		Integer init = 0;
		List<Integer> fin = new ArrayList<Integer>();
		List<SFAMove<P, S>> trans = new ArrayList<SFAMove<P, S>>();
		
		//maps from the list of +/- to the index of the state
		Map<List<Boolean>, Integer> signature = new HashMap<List<Boolean>, Integer>(); 
		
		for (int i = 0; i < table.states.size(); i++) {
			states.add(i);
			if (table.rows.get(table.states.get(i)).get(0))
				fin.add(i);
			signature.put(table.rows.get(table.states.get(i)), i);
		}
		
		List<List<S>> SUR = new ArrayList<List<S>>(table.states);
		SUR.addAll(table.boundary);
		Map<List<Boolean>, Map<List<Boolean>, Set<S>>> groups = new HashMap<List<Boolean>, Map<List<Boolean>, Set<S>>>();
		for (List<Boolean> s1 : signature.keySet()) {
			Map<List<Boolean>, Set<S>> temp = new HashMap<List<Boolean>, Set<S>>();
			for(List<Boolean> s2 : signature.keySet())
				temp.put(s2, new HashSet<S>());
			groups.put(s1, temp);
		}
			
		for (List<S> w : SUR) { //for each state (and things equivalent to the states)
			Map<List<Boolean>, Set<S>> out = new HashMap<List<Boolean>, Set<S>>();
			for (List<S> w_ext : SUR) { 
				if (w_ext.size() != w.size() + 1)
					continue;
				boolean prefix = true;
				for (int i = 0; i < w.size(); i++) {
					if (!w.get(i).equals(w_ext.get(i))) {
						prefix = false;
						break;
					}
				}
				if (!prefix)
					continue;
				
				S evid = w_ext.get(w_ext.size() - 1);
				groups.get(table.rows.get(w)).get(table.rows.get(w_ext)).add(evid);
			}
		}
		
		for (int i = 0; i < table.states.size(); i++) {
			ArrayList<Collection<S>> groups_arr = new ArrayList<Collection<S>>();
			List<Boolean> s1 = table.rows.get(table.states.get(i));
			for (int j = 0; j < table.states.size(); j++) {
				List<Boolean> s2 = table.rows.get(table.states.get(j));
				groups_arr.add(groups.get(s1).get(s2));
			}
			ArrayList<P> preds = ba.GetSeparatingPredicates(groups_arr, Long.MAX_VALUE);
			checkArgument(preds.size() == table.states.size());
			for (int j = 0; j < preds.size(); j++) //should we be checking non-emptiness?
				trans.add(new SFAInputMove<P, S>(i, j, preds.get(j)));
		}
		
		return SFA.MkSFA(trans, init, fin, ba);

	}
	
	
	private void fill(ObsTable table, Oracle<P, S> o) {
		fillAux(table.states, table, o);
		fillAux(table.boundary, table, o);
	}
	
	private void fillAux(List<List<S>> ws, ObsTable table, Oracle<P, S> o) {
		for (List<S> w : ws) {
			for (int i = table.rows.get(w).size(); i < table.diff.size(); i++) {
				List<S> w_ext = new ArrayList<S>(w);
				w_ext.addAll(table.diff.get(i));
				table.rows.get(w).add(o.checkMembership(w_ext));
			}
		}
	}
	
	
	private class ObsTable {
		public List<List<S>> states;
		public List<List<S>> boundary;
		public Map<List<S>, ArrayList<Boolean>> rows;
		public List<List<S>> diff; //parallel to rows, the differentiating evidence
		
		@Override
		public String toString() {
			String ret = "E:";
			for (List<S> w : diff) ret += " " + w;
			ret += "\nS:\n";
			for (List<S> w : states) {
				ret += " " + w + " :";
				for (boolean b : rows.get(w)) {
					if (b) ret += " +";
					else ret += " -";
				}
				ret += "\n";
			}
			ret += "R:";
			for (List<S> w : boundary) {
				ret += "\n " + w + " :";
				for (boolean b : rows.get(w)) { 
					if (b) ret += " +";
					else ret += " -";
				}
			}
			return ret;
		}
		
		public ObsTable() {
			states = new ArrayList<List<S>>();
			boundary = new ArrayList<List<S>>();
			rows = new HashMap<List<S>, ArrayList<Boolean>>();
			diff = new ArrayList<List<S>>();
		}
		
		/*
		 * returns a (possibly empty) list of the rows that need to be states
		 */
		public List<List<S>> checkClosed() { 
			List<List<S>> ret = new ArrayList<List<S>>();
			Set<List<Boolean>> si, ri;
			si = new HashSet<List<Boolean>>();
			ri = new HashSet<List<Boolean>>();
			for (List<S> w : states) si.add(rows.get(w));
			for (List<S> w : boundary) ri.add(rows.get(w));
			if (!si.containsAll(ri)) {
				for (List<S> w : boundary) {
					if (!si.contains(rows.get(w))) { 
						ret.add(w);
						si.add(rows.get(w));
					}
				}
			}
			return ret;
		}
		
		/*
		 * moves from the boundary to the states and adds the successors to the boundary
		 */
		public void promote(List<S> w, BooleanAlgebra<P, S> ba) {
			checkArgument(boundary.contains(w) && !states.contains(w));
			List<S> w_ext = new ArrayList<S>(w);
			w_ext.add(ba.generateWitness(ba.True()));
			boundary.remove(w);
			states.add(w);
			boundary.add(w_ext);
			rows.put(w_ext, new ArrayList<Boolean>());
		}
		
		/*
		 * this is "buggy" in that it sometimes makes new states
		 * that aren't actually new states--they have the same signature
		 * but in order to detect that this happens, we would need
		 * to perform additional membership queries
		 * 
		 * It is not really a bug in terms of the soundness or completeness,
		 * as the "extra states" are collapsed into the one to which
		 * they are equivalent
		 * 
		 * This "bug" only shows up when using non-lexicographically minimal
		 * counterexamples.
		 */
		public void process(List<S> cx, BooleanAlgebra<P, S> ba) {
			List<List<S>> SUR = new ArrayList<List<S>>(states);
			SUR.addAll(boundary);
			List<S> cand = new ArrayList<S>();
			for (int i = 0; i < cx.size(); i++) {
				List<S> curr = new ArrayList<S>();
				for (int j = 0; j <= i; j++)
					curr.add(cx.get(j));
				
				if (SUR.contains(curr))
					cand = curr;
				else
					break;
			}
			if (states.contains(cand)) {
				while(cand.size() < cx.size()) {
					cand = new ArrayList<S>(cand);
					cand.add(cx.get(cand.size()));
					boundary.add(cand);
					rows.put(cand,  new ArrayList<Boolean>());
				}
			} else {//boundary.contains(cand)
				checkArgument(boundary.contains(cand));
				List<S> suffix = new ArrayList<S>();
				for (int i = cand.size(); i < cx.size(); i++)
					suffix.add(cx.get(i));
				while(suffix.size() > 0) {
					diff.add(suffix);
					suffix = new ArrayList<S>(suffix);
					suffix.remove(0);
				}
				//promote(cand, ba);
				states.add(cand);
				boundary.remove(cand);
				rows.put(cand, new ArrayList<Boolean>());
				while(cand.size() < cx.size()) {
					cand = new ArrayList<S>(cand);
					cand.add(cx.get(cand.size()));
					boundary.add(cand);
					rows.put(cand, new ArrayList<Boolean>());
				}
			}
		}
		
	}
}

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
			
			System.out.println("========Obs Table========");
			System.out.println(table);
			
			conjecture = table.buildSFA(ba).mkTotal(ba);
			
			System.out.println("========SFA guess========");
			System.out.println(conjecture);

			cx = o.checkEquivalence(conjecture);
			if (cx == null) {
				System.out.println("final table\n" + table);
				System.out.println("# equiv: " + o.getNumEquivalence() + "\n# mem: " + o.getNumMembership());
				return conjecture;
			}
			
			System.out.println("========counterex========");
			System.out.println((cx == null ? "none" : cx));
			
			//process the counterexample
			table.process(cx, ba);
			table.rows.get(cx).add(!conjecture.accepts(cx, ba)); //save a membership query
			
			System.out.println("========TBLpostCX========");
			System.out.println(table);

			//Scanner scanner = new Scanner(System.in);
			//scanner.nextLine();
		}
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
		
		public Map<List<Boolean>, Map<List<Boolean>, Set<S>>> trans; //the concrete transition relations
		public Map<List<Boolean>, Map<List<Boolean>, P>> preds; //the predicate transition relation
		
		public void buildTrans() { 
			trans = new HashMap<List<Boolean>, Map<List<Boolean>, Set<S>>>();
			for (List<S> state : states) {
				Map<List<Boolean>, Set<S>> temp = new HashMap<List<Boolean>, Set<S>>();
				for (List<S> statep: states) { 
					temp.put(rows.get(statep), new HashSet<S>());
				}
				trans.put(rows.get(state), temp);
			}
			
			List<List<S>> SUR = new ArrayList<List<S>>(states);
			SUR.addAll(boundary);
			
			for (List<S> w : SUR) { 
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
					trans.get(rows.get(w)).get(rows.get(w_ext)).add(evid);
				}
			}
		}

		public SFA<P, S> buildSFA(BooleanAlgebra<P, S> ba) throws TimeoutException {
			Integer init = 0;
			List<Integer> fin = new ArrayList<Integer>();
			List<SFAMove<P, S>> moves = new ArrayList<SFAMove<P, S>>(); 
			
			for (int i = 0; i < states.size(); i++) {
				if (rows.get(states.get(i)).get(0))
					fin.add(i);
			}
			
			buildTrans();
			//System.out.println("trans reln conc:\n" + trans);
			preds = new HashMap<List<Boolean>, Map<List<Boolean>, P>>();
			
			for (int i = 0; i < states.size(); i++) {
				preds.put(rows.get(states.get(i)), new HashMap<List<Boolean>, P>());
				ArrayList<Collection<S>> groups_arr = new ArrayList<Collection<S>>();
				List<Boolean> s1 = rows.get(states.get(i));
				for (int j = 0; j < states.size(); j++) {
					List<Boolean> s2 = rows.get(states.get(j));
					groups_arr.add(trans.get(s1).get(s2));
				}
				ArrayList<P> sepPreds = ba.GetSeparatingPredicates(groups_arr, Long.MAX_VALUE);
				checkArgument(sepPreds.size() == states.size());
				for (int j = 0; j < sepPreds.size(); j++) {
					//should we be checking non-emptiness?
					moves.add(new SFAInputMove<P, S>(i, j, sepPreds.get(j)));
					preds.get(rows.get(states.get(i))).put(rows.get(states.get(j)), sepPreds.get(j));
				}
			}
			//System.out.println("trans reln pred:\n" + preds);
			
			return SFA.MkSFA(moves, init, fin, ba);

		}

		
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
			trans = null;
			preds = null;
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
		 * moves from the boundary to the states and potentially adds a successor to the boundary
		 */
		public void promote(List<S> w, BooleanAlgebra<P, S> ba) {
			promote(w, ba, false);
		}
		
		public void promote(List<S> w, BooleanAlgebra<P, S> ba, boolean suppressCheck) {
			checkArgument(boundary.contains(w) && !states.contains(w));
			boundary.remove(w);
			states.add(w);
			//see if its continuation needs to be added to the boundary
			if (!suppressCheck) {
				boolean found = false;
				for (List<S> word : boundary) {
					if (word.size() != w.size() + 1)
						continue;
					boolean equiv = true;
					for (int i = 0; i < w.size(); i++) {
						if (!word.get(i).equals(w.get(i))) {
							equiv = false;
							break;
						}
					}
					if (!equiv)
						continue;
					found = true;
					break;
				}
				if (!found) {
					List<S> w_ext = new ArrayList<S>(w);
					w_ext.add(ba.generateWitness(ba.True()));
					boundary.add(w_ext);
					rows.put(w_ext, new ArrayList<Boolean>());
				}
			}
		}
		
		//assumes the table has not been modified since the last call to buildTrans()
		public void process(List<S> cx, BooleanAlgebra<P, S> ba) {
			/*
			 * find a factorization u.b.v (u,v strings, b char) such that
			 * > we have concrete evidence for u in the table
			 * > we don't have concrete evidence for u.b in the table
			 * 
			 * u.b ... u.b.v are added to the boundary
			 * 
			 * if u is in the boundary AND
			 *   there is a character c in u for which
			 *   the corresponding group has characters only in the boundary
			 * then b.v and all suffixes are added to diff
			 * (when the table is next filled and closed, u will be promoted to a state)
			 */
			
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
			
			//add u.b ... u.b.v to the boundary
			List<S> addToBoundary = cand;
			for (int i = cand.size(); i < cx.size(); i++) {
				addToBoundary = new ArrayList<S>(addToBoundary);
				addToBoundary.add(cx.get(i));
				boundary.add(addToBoundary);
				rows.put(addToBoundary, new ArrayList<Boolean>());
			}
			
			//find if u.b takes us to a new state
			//first follow u
			boolean makeState = false;
			List<Boolean> curr = rows.get(states.get(0));
			for (S c : cand) {
				boolean check = false;
				for (List<Boolean> to : trans.get(curr).keySet()) {
					if (trans.get(curr).get(to).contains(c)) {
						check = true;
						List<List<S>> temp = new ArrayList<List<S>>();
						for (S evid : trans.get(curr).get(to)) 
							temp.add(Arrays.asList(evid));
						if (boundary.containsAll(temp)) {
							makeState = true;
						}
						break;
					}
				}
				checkArgument(check);
				if (makeState)
					break;
			}
			//check b here
			if (!makeState) { 
				boolean check = false;
				for (List<Boolean> to : preds.get(curr).keySet()) {
					if (ba.HasModel(preds.get(curr).get(to), cx.get(cand.size()))) {
						check = true;
						if (boundary.containsAll(trans.get(curr).get(to)))
							makeState = true;
						break;
					}
				}
				checkArgument(check);
			}
			//if there is a new state, add b.v and all its suffixes to diff
			if (makeState) {
				List<S> suffix = new ArrayList<S>();
				for (int i = cand.size(); i < cx.size(); i++) 
					suffix.add(cx.get(i));
				while (suffix.size() > 0) {
					diff.add(suffix);
					suffix = new ArrayList<S>(suffix);
					suffix.remove(0);
				}
			}
		}
		
	}
}

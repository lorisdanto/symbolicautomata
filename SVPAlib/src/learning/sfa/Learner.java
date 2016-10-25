package learning.sfa;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;


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
			evidClose(table, o);
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
			
			//System.out.println("trans:\n" + table.trans);
			//System.out.println("preds:\n" + table.preds);

			System.out.println("========SFA guess========");
			System.out.println(conjecture);

			checkArgument(table.consistent(conjecture, ba));

			cx = o.checkEquivalence(conjecture);
			if (cx == null) {
				System.out.println("final table\n" + table);
				System.out.println("# equiv: " + o.getNumEquivalence() + "\n# mem: " + o.getNumMembership());
				return conjecture;
			}
			
			System.out.println("========counterex========");
			System.out.println((cx == null ? "none" : cx));
			
			//process the counterexample
			process(table, o, cx, ba);
			//table.rows.get(cx).add(!conjecture.accepts(cx, ba)); //save a membership query
			
			System.out.println("========TBLpostCX========");
			System.out.println(table);

			//Scanner scanner = new Scanner(System.in);
			//scanner.nextLine();
		}
	}
	
	private void evidClose(ObsTable table, Oracle<P, S> o) {
		//for all states s and suffixes e, s.e must be in the table
		List<List<S>> SUR = new ArrayList<List<S>>(table.states);
		SUR.addAll(table.boundary);
		for (List<S> suffix : table.diff) {
			for (List<S> s : table.states) { 
				List<S> se = new ArrayList<S>(s);
				se.addAll(suffix);
				if (!SUR.contains(se)) {
					SUR.add(se);
					table.boundary.add(se);
					table.rows.put(se, new ArrayList<Boolean>());
				}
			}
		}
	}
	
	private void process(ObsTable table, Oracle<P, S> o, List<S> cx, BooleanAlgebra<P, S> ba) throws TimeoutException {
		//add the counterexample and all its prefixes to the boundary
		List<List<S>> prefixes = new ArrayList<List<S>>();
		for (int i = 1; i <= cx.size(); i++) {
			List<S> prefix = new ArrayList<S>();
			for (int j = 0; j < i; j++)
				prefix.add(cx.get(j));
			prefixes.add(prefix);
		}

		List<List<S>> SUR = new ArrayList<List<S>>(table.states);
		SUR.addAll(table.boundary);

		for (List<S> p : prefixes) {
			if (!SUR.contains(p)) {
				table.boundary.add(p);
				table.rows.put(p, new ArrayList<Boolean>());
			}
		}

		//perform appropriate membership queries
		fill(table, o);
		//if this is sufficient to show some elements of the boundary should be states, take care of that
		List<List<S>> newstates = table.checkClosed();
		if (newstates.size() > 0) {
			while(newstates.size() > 0) {
				for (List<S> w : newstates)
					table.promote(w, ba);
				fill(table, o);
				newstates = table.checkClosed();
			}
			SUR = new ArrayList<List<S>>(table.states);
			SUR.addAll(table.boundary);
		}

		//decompose the counterexample into all possible configurations of
		//u.b.v where u and v are strings and b is a character
		//we will check if u needs to be a new state
		for (int i = 0; i < cx.size(); i++) {
			List<S> u = new ArrayList<S>();
			for (int j = 0; j < i; j++)
				u.add(cx.get(j));
			S b = cx.get(i);
			List<S> ub = new ArrayList<S>(u);
			ub.add(b);
			List<S> v = new ArrayList<S>();
			for (int j = i + 1; j < cx.size(); j++)
				v.add(cx.get(j));
			List<S> bv = new ArrayList<S>();
			bv.add(b);
			bv.addAll(v);

			if (!table.boundary.contains(u))
				continue;

			//for loop just to find the state to which the hypothesis says u is equivalent
			for (List<S> state : table.states) {
				if (!table.rows.get(state).equals(table.rows.get(u)))
					continue;
				//verify that the state equivalent to u behaves the same way upon b as does u
				List<S> sb = new ArrayList<S>(state);
				sb.add(b);
				if (!SUR.contains(sb)) {
					table.boundary.add(sb);
					SUR.add(sb);
					table.rows.put(sb, new ArrayList<Boolean>());
					table.rows.get(sb).add(o.checkMembership(sb));
				}
				//if it doesn't, u needs to be a state
				if (!table.rows.get(sb).get(0).equals(table.rows.get(ub).get(0))) {
					table.diff.add(bv);
					fill(table, o);
					newstates = table.checkClosed();
					if (newstates.size() > 0) {
						while(newstates.size() > 0) {
							for (List<S> w : newstates)
								table.promote(w, ba);
							fill(table, o);
							newstates = table.checkClosed();
						}
						SUR = new ArrayList<List<S>>(table.states);
						SUR.addAll(table.boundary);
					}
					break;
				}
			}
		}
	}
		
	
	private void fill(ObsTable table, Oracle<P, S> o) throws TimeoutException {
		fillAux(table.states, table, o);
		fillAux(table.boundary, table, o);
	}
	
	private void fillAux(List<List<S>> ws, ObsTable table, Oracle<P, S> o) throws TimeoutException {
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

		//sanity check to verify a conjectured automaton is consistent with all the table's evidence
		public boolean consistent(SFA<P, S> sfa, BooleanAlgebra<P, S> ba) throws TimeoutException {
			List<List<S>> SUR = new ArrayList<List<S>>(states);
			SUR.addAll(boundary);
			for (List<S> w : SUR) {
				for (int i = 0; i < diff.size(); i++) {
					List<S> w_ext = new ArrayList<S>(w);
					w_ext.addAll(diff.get(i));
					if (!rows.get(w).get(i).equals(sfa.accepts(w_ext, ba))) {
						System.out.println("inconsistent on " + w_ext);
						return false;
					}
				}
			}
			return true;
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
		public void promote(List<S> w, BooleanAlgebra<P, S> ba) throws TimeoutException {
			promote(w, ba, false);
		}
		
		public void promote(List<S> w, BooleanAlgebra<P, S> ba, boolean suppressCheck) throws TimeoutException {
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

	}
}

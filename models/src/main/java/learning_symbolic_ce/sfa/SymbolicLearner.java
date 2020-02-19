package learning_symbolic_ce.sfa;

//import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utilities.Pair;

import java.lang.Integer;


import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.BooleanAlgebra;
import learning.sfa.*;
import utilities.Triple;

//import java.util.concurrent.TimeoutException;

//An implementation of the SFA learning algorithm from
//S. Drews and L. D'Antoni "Learning Symbolic Automata" (TACAS 2017)
public class SymbolicLearner<P, S> extends Learner<P, S>{

	public boolean debugOutput; //controls whether to write intermediary steps to System.out
	
	public SymbolicLearner() {
		super();
	}
	
	public SymbolicLearner(boolean debugOutput) {
		super(debugOutput);
	}
	
	private void log(String heading, Object value) {
		if (this.debugOutput) {
			System.out.println("========" + heading + "========");
			System.out.println(value);
		}
	}

	public SFA<P, S> learn(SymbolicOracle<P, S> o, BooleanAlgebra<P, S> ba) throws TimeoutException {
		ObsTable table = new ObsTable(ba.generateWitness(ba.True()), ba);
		long fulltime = System.nanoTime();
		
		SFA<P, S> conjecture = null;
		
		//Counterexamples are now lists of predicates
		List<P> cx = null;
		HashSet<Pair<List<P>, Boolean>> counterexamples = new HashSet<Pair<List<P>, Boolean>>();
		//HashMap<List<S>, Boolean> membershipQueries;
		
		while (true) {
			table.fill(o);
			
			//this.log("TBL after fill", table);

			boolean consflag = true, closeflag = true, partflag = true;
			do {
				//make-consistent can add to E,
				//so after it is run we need to
				//(i)  fill the table
				//(ii) check closed again
				if (consflag) {
					consflag = table.make_consistent();
				}
				if (consflag) {
					
					table.fill(o);
					
					//this.log("TBL after mkcons", table);

					boolean distflag = table.distribute();
					if (distflag) {
						table.fill(o);
					}
					
					closeflag = true;
				}
				//close can add to R,
				//so after it is run we need to
				//(i)  fill the table
				//(ii) check consistency again
				if (closeflag) {
					closeflag = table.close(o);
				}
				if (closeflag) {
					table.fill(o);
					consflag = true;
				}
				//note that evidence-closure is handled by the other subroutines
				assert true;
				consflag = (table.completeObservedPartition(o)) || consflag;
				table.fill(o);
			} while (consflag || closeflag);
			
			this.log("Obs Table", table);
			
			conjecture = table.buildSFA(ba);
			conjecture = conjecture.mkTotal(ba);

			this.log("SFA guess", conjecture);

			//System.out.println("sanity checking consistency");
			//checkArgument(table.consistent(conjecture, ba));
			//System.out.println("passed");
			
			cx = o.checkEquivalence(conjecture);
			
			//Can't guarantee that a repeated counterexample is still maximal single-path 
			/*
			Boolean repeatCE = false; 
			for(Pair<List<P>, Boolean> ce : counterexamples) {
				if(!conjecture.accepts(ce.first, ba).equals(ce.second)) {
					cx = ce.first;
					repeatCE = true;
					break;
				}
			}
			if(!repeatCE) {
				cx = o.checkEquivalence(conjecture);
				List<P> cx2 = new ArrayList<P>(cx);
				Pair<List<P>, Boolean> cxPair = new Pair<List<P>, Boolean>(cx2, !conjecture.accepts(cx2, ba))
				counterexamples.add();
				
			}
			//cx = checkEquivWithRepeats(conjecture, counterexamples, o);
			counterexamples.add(cx);
			*/
			
			//!!UPDATE THE PARTITIONS AFTER EACH NEW CE YOU IDIOT
			
			//System.out.println(cx);
			if (cx == null) {
				System.out.println(String.valueOf(System.nanoTime() - fulltime));
				this.log("statistics", 
						"# equiv: " + o.getNumEquivalence() + 
						"\n# mem: " + o.getNumMembership());
				return conjecture;
			}

			this.log("counterex", (cx == null ? "none" : cx));
			
			//process the counterexample
			table.process(cx, ba, table, o);
			Boolean b = false;
			b = !b;
			//this.log("TBLpostCX", table);

			//Scanner scanner = new Scanner(System.in);
			//scanner.nextLine();
		}
	}
	
	//List<P> checkEquivalenceWithPast(SFA<P, S> conj, HashSet<List<P>> counterexamples, (SymbolicOracle<P, S> o)
	
	private class ObsTable {
		public List<List<S>> S, R, E, SUR;
		public Map<List<S>, Boolean> f;
		public S arbchar;
		public BooleanAlgebra<P, S> ba;
		public int fillMems = 0;
				
		//maps an integer representing a state to an "observed partition"
		//observed partition is a list of predicate/state pairs
		HashMap<List<S>, ArrayList<Triple<P, List<S>, S>>> partitions;
		HashMap<List<S>, P> nullMap;
		
		//returns the string in S with the same row as aString. 
		//returns null if no such state exists.  
		public List<S> getAccessString(List<S> aString, SymbolicOracle<P,S> o) throws TimeoutException{
			List<Boolean> row1 = safeRow(aString,o);
			if(row1 == null) {
				assert false;
			}
			for(List<S> s : S) {
				List<Boolean> sRow = row(s);
				if(row(s) == null) {
					assert false;
				}
				if(row1.equals(sRow)) {
					return s;
				}
			}
			return null;
		}
		
		public ObsTable(S arbchar, BooleanAlgebra<P, S> ba) {
			S = new ArrayList<List<S>>();
			R = new ArrayList<List<S>>();
			SUR = new ArrayList<List<S>>();
			E = new ArrayList<List<S>>();
			f = new HashMap<List<S>, Boolean>();
			this.arbchar = arbchar;
			this.ba = ba;
			
			//ArrayList<Object> is of form: Predicate, List<S> target state, S transitioning character (optional?);
			partitions = new HashMap<List<S>, ArrayList<Triple<P, List<S>, S>>>();
			nullMap = new HashMap<List<S>, P>();
			
			ArrayList<Triple<P, List<S>, S>> initParts = new ArrayList<Triple<P, List<S>, S>>();
			Triple<P, List<S>, S> lambdaParts = new Triple<P, List<S>, S>(ba.True(), null, null);
			initParts.add(lambdaParts);
			//!! should we query to get the second output?
			
			S.add(new ArrayList<S>());
			SUR.add(new ArrayList<S>());
			List<S> r = new ArrayList<S>();
			r.add(arbchar);
			R.add(r);
			SUR.add(r);
			E.add(new ArrayList<S>());
			
			partitions.put(S.get(0), initParts);
			nullMap.put(S.get(0), ba.True());
		}
		
		//auxiliary method that checks whether
		//w is a strict prefix of we
		private boolean isPrefix(List<S> w, List<S> we) {
			if (w.size() >= we.size())
				return false;
			for (int i = 0; i < w.size(); i++)
				if (!w.get(i).equals(we.get(i)))
					return false;
			return true;
		}
		
		//assumes isPrefix(w, we)
		private List<S> getSuffix(List<S> w, List<S> we) {
			List<S> suffix = new ArrayList<S>();
			for (int k = w.size(); k < we.size(); k++)
				suffix.add(we.get(k));
			return suffix;
		}
		

		
		/*
		public void process(List<S> cx) {
			List<List<S>> prefixes = new ArrayList<List<S>>();
			for (int i = 1; i <= cx.size(); i++) {
				List<S> prefix = new ArrayList<S>();
				for (int j = 0; j < i; j++)
					prefix.add(cx.get(j));
				prefixes.add(prefix);
			}
			
			for (List<S> p : prefixes) {
				if (!SUR.contains(p)) {
					R.add(p);
					SUR.add(p);
				}
			}
		}
		*/

		public void process(List<P> cx, BooleanAlgebra<P,S> ba, ObsTable table, SymbolicOracle<P,S> o) throws TimeoutException{
			//HashMap<List<S>, ArrayList<Triple<P, List<S>, S>>> newPartitions = new HashMap<List<S>, ArrayList<Triple<P, List<S>, S>>>(partitions);
 			//HashMap<List<S>, P> newNullMap = new HashMap<List<S>, P>(nullMap);
 			//System.out.println(String.valueOf(this.S.size()));
 			//System.out.println(this);
			List<S> ceString = new ArrayList<S>();
 			boolean changedPred = false; 
			for(int i=0; i<cx.size(); i++) {
				List<S> accessString = getAccessString(ceString, o);
				ceString.add(ba.generateWitness(cx.get(i)));
				P currentPred = cx.get(i);
				boolean foundOverlap = false;
				ArrayList<Triple<P, List<S>, S>> newPart = new ArrayList<Triple<P, List<S>, S>>(partitions.get(accessString));
				
				for(Triple<P, List<S>, S> part : partitions.get(accessString)) {
					if(ba.AreEquivalent(part.first, currentPred)) { //
						foundOverlap = true;
						break;
					} 
					 //There is some overlap, so predicates are updated
					if (ba.IsSatisfiable(ba.MkAnd(currentPred, part.first))) {
						foundOverlap = true;
						//ArrayList<Triple<P, List<S>, S>> currPart = table.partitions.get(accessString); 
						P firstPred = ba.MkAnd(part.first, currentPred);
						if(ba.IsSatisfiable(firstPred)) {
							newPart.add(new Triple<P, List<S>, S>(firstPred, null, null));
						} else {
						//	assert false;
						}
						P secPred = ba.MkAnd(part.first, ba.MkNot(currentPred));
						if(ba.IsSatisfiable(secPred)) {
							newPart.add(new Triple<P, List<S>, S>(secPred, null, null));
						} else {
							//assert false;
						}
						newPart.remove(part);
						currentPred = ba.MkAnd(currentPred, ba.MkNot(part.first));
						changedPred = true;
					}
				}
				partitions.put(accessString, newPart);
				extendByPartitions(accessString, newPart);
				assert foundOverlap : "partitions are not complete";
				//only change the outgoing predicate for a single state
				//maybe change later
				if(changedPred) { break; }
			}
			if(!changedPred) {
				assert false : "no difference was found between counter-example and hypothesis";
			}
			List<S> newR = new ArrayList<S>();
			//!! changed to add all prefixes to R 
			for(int i=0; i < cx.size(); i++) {
				//System.out.println("1");
				newR.add(ba.generateWitness(cx.get(i)));
				table.R.add(new ArrayList<S>(newR)); //!! probably where things went wrong wrt evidence closure
				table.SUR.add(new ArrayList<S>(newR)); 
			}
			
		}
		
		//!! make more efficient in terms of new elements added to R
		//Ensures that accessString is extended in R by a representative element from each partition.  
		public void extendByPartitions(List<S> accessString,  ArrayList<Triple<P, List<S>, S>> newPart) throws TimeoutException{
			//List<Boolean> sRow = row(accessString);
			for(Triple<P, List<S>, S> part : newPart){
				Boolean extensionExists = false;
				for(List<S> extension : SUR) {
					if(extension.size() == 0) {continue;}
					//List<Boolean> eRow = row(extension.subList(0, extension.size()-1));
					//assert eRow != null;
					//if(!eRow.equals(sRow)) {continue;}
					if(!extension.subList(0, extension.size()-1).equals(accessString)) {continue;}
					if(ba.HasModel(part.first, extension.get(extension.size()-1))){
						extensionExists = true;
					}			
				}
				if(!extensionExists) {
					S ext = ba.generateWitness(part.first);
					if(ext == null) {
						assert false;
					}
					List<S> newR = new ArrayList<S>(accessString);
					newR.add(ext);
					List<S> newSUR = new ArrayList<S>(newR);
					R.add(newR);
					SUR.add(newSUR);
					assert newR.get(0) != null;
					assert newSUR.get(0) != null;
				}
			}
		}
		
		//sanity check to verify a conjectured automaton
		//is consistent with the observation table
		public boolean consistent(SFA<P, S> sfa, BooleanAlgebra<P, S> ba) throws TimeoutException {
			for (List<S> w : SUR) {
				for (List<S> e : E) {
					List<S> we = new ArrayList<S>(w);
					we.addAll(e);
					if (!f.get(we).equals(sfa.accepts(we, ba))) {
						//System.out.println("inconsistent on " + we);
						return false;
					}
				}
			}
			return true;
		}
		
		private void addState(List<S> stateString, SymbolicOracle<P,S> o) throws TimeoutException{
			S.add(stateString);
			SUR.add(stateString);
			f.put(stateString, o.checkMembership(stateString));
			
			//checks that the immediate prefix of stateString is in S
			/*List<S> pref = new ArrayList<S>(stateString);
			pref.remove(pref.size() - 1);
			if(!S.contains(pref)) {
				assert false;
			}*/
			
			partitions = new HashMap<List<S>, ArrayList<Triple<P, List<S>, S>>>();
			nullMap = new HashMap<List<S>, P>();
			
			
			
			//!! inefficient (just need to reset partitions where state has split)
			for(List<S> accessString : S) {
				Triple<P, List<S>, S> part = new Triple<P, List<S>, S>(ba.True(), null, null);
				
				ArrayList<Triple<P, List<S>, S>> part2 = new ArrayList<Triple<P, List<S>, S>>();
				part2.add(part);
				
				partitions.put(accessString, part2);
			}
			
			
			//handle evidence-closure
			for (List<S> e : E) { 
				List<S> se = new ArrayList<S>(stateString);
				se.addAll(e);
				if (!SUR.contains(se)) {
					R.add(se);
					SUR.add(se);
				}
			}
			
			//in case all the e in E are more than single char,
			//ensure continuation r.a in SUR
			boolean cont = false;
			for (List<S> w : SUR) {
				if (w.size() != stateString.size() + 1)
					continue;
				if (isPrefix(stateString, w)) {
					cont = true;
					break;
				}
			}
			if (!cont) {
				List<S> sa = new ArrayList<S>(stateString);
				sa.add(arbchar);
				R.add(sa);
				SUR.add(sa);
			}
			//completeObservedPartition(o, ba);
			assert true;
			//fill(o); //!! maybe don't need this here.
		}
		
		public void addExperiment(List<S> ex) {
			E.add(ex);
			for(List<S> s : S) {
				List<S> se = new ArrayList<S>(s);
				se.addAll(ex);
				if(!SUR.contains(se)) {
					this.SUR.add(se);
					this.R.add(se);
				}
			}
		}
		
		//returns true if a new state was added
		public Boolean completeObservedPartition(SymbolicOracle<P, S> o) throws TimeoutException {
			
			//Assembles map from rows to states.
			HashMap<List<Boolean>, List<S>> states = new HashMap<List<Boolean>, List<S>>();
			//!! probably don't need this
			for (List<S> s : S) {
				states.put(row(s), s);
			}
			/*
			for (List<S> w : SUR) {
				states.put(row(w), w);
			}*/
			
			for(List<S> accessString : partitions.keySet()) {
				ArrayList<Triple<P, List<S>, S>> part = partitions.get(accessString);
				for(Triple<P, List<S>, S> pred : part) {
					//assert pred.size() != 0 : "empty pred";
					if(pred.third == null) {
						
						//!! avoid reusing witnesses check if a member of pred.first extends the access string 
						//to an element of SUR
						
						S wit = ba.generateWitness(pred.first);
						assert wit != null;
						List<S> extension = new ArrayList<S>(accessString);
						extension.add(wit);
					
						List<Boolean> predRow = safeRow(extension, o);
						
						/*if(predRow == null) { //adding a new string to S
							addState(extension, o);
							addExperiment(Arrays.asList(wit));
							return true;
						}*/
						if(predRow == null) { //!! inefficient. don't want to recall fill/consistent/close each time
							R.add(extension);
							SUR.add(extension);
							return true;
						} else if (states.get(predRow) == null) {
							//assert false;
							addState(extension, o);
							//System.out.println("0");
							addExperiment(Arrays.asList(wit));
							return true;
						} else if(pred.second == null){ //need to find
							List<S> targetState = states.get(predRow);
							assert targetState != null;
							pred.second = targetState;
						} else {
							assert false : "predicate's target state but not witness is set";
						}
						
						pred.third = wit;
					}
					Boolean b = 0 == 0;
					b = !b;
					
					
				}
			}
			return false;
		}
		
		public SFA<P, S> buildSFA(BooleanAlgebra<P, S> ba) throws TimeoutException {
			HashMap<List<Boolean>, List<S>> states = new HashMap<List<Boolean>, List<S>>();
			for (List<S> s : S) {
				List<Boolean> sRow = row(s);
				states.put(sRow, s);
			}
			for (List<S> w : SUR) {
				List<Boolean> wRow = row(w);
				assert states.get(wRow) != null;
				states.put(wRow, states.get(wRow));
			}
			
			List<SFAMove<P, S>> moves = new ArrayList<SFAMove<P, S>>();
			for(int i=0; i < S.size(); i++) {
				List<S> accessString = S.get(i);
				HashMap<List<S>, P> sepPred = new HashMap<List<S>, P>();
				ArrayList<Triple<P, List<S>, S>> test = partitions.get(accessString);
				assert test != null;
				assert partitions != null; 
				for(Triple<P, List<S>, S> part : partitions.get(accessString)){
					if(!sepPred.containsKey(part.second)) {
						sepPred.put((List<S>)part.second, part.first);
					}
					else {
						P currPred = sepPred.get(part.second);
						sepPred.put((List<S>)part.second, ba.MkOr(currPred, part.first));
					}
				}
				for(List<S> s : S) {
					if(sepPred.get(s) != null) {
						moves.add(new SFAInputMove<P,S>(i, S.indexOf(s), sepPred.get(s)));
					}
				}
			}
			
			List<Integer> fin = new ArrayList<Integer>();
			for (int i = 0; i < S.size(); i++) {
				List<S> sElt = S.get(i);
				if(sElt==null) {
					break;
				}
				if(f.get(sElt) == null) {
					assert false : "f is missing a state";
				}
				if (f.get(sElt)) {
					fin.add(i);
				}
			}
			
			Integer init = S.indexOf(new ArrayList<S>());
			if( !init.equals(0)) {
				assert false;
			}
			
			
			SFA<P,S> aut = SFA.MkSFA(moves, init, fin, ba);
			//!! aut might be inconsistent with OT depending on state of observed partition.
			
			//if(!consistent(aut, ba)) {
			//	assert consistent(aut, ba) : "sfa is not consisten with OT";
			//}
			return aut;
		}
		

		
		/*
		public SFA<P, S> buildSFA(BooleanAlgebra<P, S> ba) throws TimeoutException {
			//first build the evidence automaton's transition system
			Map<List<Boolean>, Map<List<Boolean>, Set<S>>> trans;
			trans = new HashMap<List<Boolean>, Map<List<Boolean>, Set<S>>>();
			for (List<S> s : S) {
				Map<List<Boolean>, Set<S>> temp = new HashMap<List<Boolean>, Set<S>>();
				for (List<S> sp : S)
					temp.put(row(sp), new HashSet<S>());
				trans.put(row(s), temp);
			}
			for (List<S> w : SUR) {
				for (List<S> wa : SUR) {
					if (wa.size() != w.size() + 1 || !isPrefix(w, wa))
						continue;
					S evid = wa.get(wa.size() - 1);
					trans.get(row(w)).get(row(wa)).add(evid);
				}
			}
			
			//now generalize the evidence into predicates
			List<SFAMove<P, S>> moves = new ArrayList<SFAMove<P, S>>();
			for (int i = 0; i < S.size(); i++) {
				List<Boolean> sb = row(S.get(i));
				ArrayList<Collection<S>> groups_arr = new ArrayList<Collection<S>>();
				for (List<S> sp : S) {
					groups_arr.add(trans.get(sb).get(row(sp)));
				}
				ArrayList<P> sepPreds = ba.GetSeparatingPredicates(groups_arr, Long.MAX_VALUE);
				checkArgument(sepPreds.size() == S.size());
				for (int j = 0; j < sepPreds.size(); j++)
					moves.add(new SFAInputMove<P, S>(i, j, sepPreds.get(j)));
			}
			
			//build and return the SFA
			Integer init = 0;
			List<Integer> fin = new ArrayList<Integer>();
			for (int i = 0; i < S.size(); i++) {
				if (f.get(S.get(i)))
					fin.add(i);
			}
			//System.out.println("SFAmoves:" + moves);
			//System.out.println("init:" + init + "\nfin:" + fin);
			//System.out.println("building");
			SFA ret = SFA.MkSFA(moves, init, fin, ba);
			//System.out.println("returning");
			return ret;
			
			//return SFA.MkSFA(moves, init, fin, ba);
		}
		*/

		public List<Boolean> row(List<S> w) {
			return row(w, null);
		}
		
		public List<Boolean> row(List<S> w, List<S> ignore) {
			List<Boolean> ret = new ArrayList<Boolean>();
			for(List<S> e : E) {
				if (ignore != null && ignore.equals(e))
					continue;
				List<S> we = new ArrayList<S>(w);
				we.addAll(e);
				ret.add(f.get(we)); //assumes f.containsKey(we)
			}
			if (ret.contains(null)) {
				return null;
			}
			return ret;
		}
		
		//returns a row of w when it is not guaranteed that f contains we
		public List<Boolean> safeRow(List<S> w, SymbolicOracle<P,S> o) throws TimeoutException {
			List<Boolean> ret = new ArrayList<Boolean>();
			for(List<S> e : E) {
				List<S> we = new ArrayList<S>(w);
				we.addAll(e);
				if(f.get(we) != null) {
					ret.add(f.get(we));
				} else {
					Boolean mem = o.checkMembership(we);
					f.put(we, mem);
					ret.add(mem);
				}
			}
			if (ret.contains(null)) {
				return null;
			}
			return ret;
		}
		
		public void fill(SymbolicOracle<P, S> o) throws TimeoutException {
			//removes duplicates before filling in table
			SUR = new ArrayList<List<S>>(new LinkedHashSet<List<S>>(SUR));
			S = new ArrayList<List<S>>(new LinkedHashSet<List<S>>(S));
			R = new ArrayList<List<S>>(new LinkedHashSet<List<S>>(R));
			R.removeAll(S);
			E = new ArrayList<List<S>>(new LinkedHashSet<List<S>>(E)); //!! need to maintain order in E.
			
			//SUR = new ArrayList<List<S>>(new HashSet<List<S>>(SUR));
			//S = new ArrayList<List<S>>(new HashSet<List<S>>(S));
			//R = new ArrayList<List<S>>(new HashSet<List<S>>(R));
			//E = new ArrayList<List<S>>(new HashSet<List<S>>(E));
			//S.remove(new ArrayList<S>());
			//S.add(0, new ArrayList<S>());
			//E.remove(new ArrayList<S>());
			//E.add(0, new ArrayList<S>());
			if(f.containsKey(new ArrayList<S>())) {
				assert true;
			}
			for (List<S> w : SUR) {
				//System.out.println(w);
				for (List<S> e : E) {
					List<S> we = new ArrayList<S>(w);
					we.addAll(e);
					if(!f.containsKey(we)){
						//System.out.println(we);
						if(!we.isEmpty() && we.get(0) == null) {
							assert true;
							assert true;
						}
						fillMems++;
						f.put(we, o.checkMembership(we));
					}
				}
			}
		}
		
		//returns true if makes a change, needs to be applied until returns false
		public boolean close(SymbolicOracle<P,S> o) throws TimeoutException{
			Set<List<Boolean>> sigs = new HashSet<List<Boolean>>();
			for (List<S> s : S)
				sigs.add(row(s));
			List<S> best_r = null;
			for (List<S> r : R) {
				List<Boolean> rRow = row(r);
				if (!sigs.contains(rRow)) {
					//for membership query efficiency,
					//instead of just moving r to S, move the shortest r' with row(r) = row(r')
					best_r = r;
					for (List<S> rp : R) {
						if (!row(r).equals(row(rp)))
							continue;
						if (r.equals(rp))
							continue;
						if (rp.size() < best_r.size())
							best_r = rp;
					}
					break;
				}
			}
			if (best_r == null)
				return false;
			
			List<S> r = best_r;
			this.addState(r,o);
			R.remove(r);
		
			
			return true;
		}
		
		//returns true if makes a change, needs to be applied until returns false
		public boolean make_consistent() { 
			for (int i = 0; i < SUR.size(); i++) {
				for (int j = i + 1; j < SUR.size(); j++) {
					List<S> w1 = SUR.get(i);
					List<S> w2 = SUR.get(j);
					if (!row(w1).equals(row(w2))) {
						continue;
					}
					Set<List<S>> cont1 = new HashSet<List<S>>();
					Set<List<S>> cont2 = new HashSet<List<S>>();
					for (List<S> wa : SUR) {
						if (isPrefix(w1, wa))
							cont1.add(wa);
						if (isPrefix(w2, wa))
							cont2.add(wa);
					}
					for (List<S> w1a : cont1) {
						List<S> suffix1 = getSuffix(w1, w1a);
						for (List<S> w2a : cont2) {
							List<S> suffix2 = getSuffix(w2, w2a);
							if (!suffix1.equals(suffix2)) {
								continue;
							}
							List<Boolean> r1 = row(w1a);
							List<Boolean> r2 = row(w2a);
							if (!r1.equals(r2)) {
								//at this point,
								//row(w1) == row(w2) but row(w1e) != row(w2e)
								//find the problematic suffix in E and concatenate it to the common suffix
								List<S> e = new ArrayList<S>(suffix1);
								for (int k = 0; k < E.size(); k++){
									if (!r1.get(k).equals(r2.get(k))) {
										e.addAll(E.get(k));
										break;
									}
								}
								//System.out.println("2");
								addExperiment(e);
								//distribute the old evidence in a separate function
								//i.e. find pairs u1,u2 in SUR with row(u1) = row(u2)
								//     but after adding e to E, row(u1) != row(u2)
								//this requires filling the table, first
								//handle evidence-closure
								/*for (List<S> s : S) {
									List<S> se = new ArrayList<S>(s);
									se.addAll(e);
									if (!SUR.contains(se)) {
										R.add(se);
										SUR.add(se);
									}
								}*/
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		
		//this is called assuming that make_consistent added an element (exactly one element) to E
		public boolean distribute() {
			List<S> e = E.get(E.size() - 1);
			//System.out.println("mkcons added: " + e.toString());
			Set<List<S>> toAdd = new HashSet<List<S>>();
			boolean addFlag;
			//find pairs u1,u2 in SUR with row(u1) = row(u2) but f(u1e) != f(u2e)
			//(where row does not include the e index)
			List<S> u1, u2, u1e, u2e;
			for (int i = 0; i < SUR.size(); i++) {
				for (int j = i + 1; j < SUR.size(); j++) {
					u1 = SUR.get(i);
					u2 = SUR.get(j);
					if (!row(u1,e).equals(row(u2,e)))
						continue;
					u1e = new ArrayList<S>(u1);
					u1e.addAll(e);
					u2e = new ArrayList<S>(u2);
					u2e.addAll(e); 
					if (f.get(u1e).equals(f.get(u2e)))
						continue;
					//if a continuation of u1 by b is in the table, u2b needs to be in the table
					//and vice-versa
					for (List<S> unb : SUR) {
						if (unb.size() == u1.size() + 1 && isPrefix(u1,unb)) {
							List<S> b = getSuffix(u1,unb); //b is a word of length 1
							//actually, don't need to add u2b if
							//there already exists wb with row(w) = row(u2)
							addFlag = true;
							for (List<S> w : SUR) {
								if (!row(w).equals(row(u2)))
									continue;
								List<S> wb = new ArrayList<S>(w);
								wb.addAll(b);
								if (SUR.contains(wb) || toAdd.contains(wb)) {
									addFlag = false;
									break;
								}
							}
							if (addFlag) {
								List<S> u2b = new ArrayList<S>(u2);
								u2b.addAll(b);
								if (!SUR.contains(u2b))
									toAdd.add(u2b);
							}
						}
						if (unb.size() == u2.size() + 1 && isPrefix(u2,unb)) {
							List<S> b = getSuffix(u2,unb);
							addFlag = true;
							for (List<S> w : SUR) {
								if (!row(w).equals(row(u1)))
									continue;
								List<S> wb = new ArrayList<S>(w);
								wb.addAll(b);
								if (SUR.contains(wb) || toAdd.contains(wb)) {
									addFlag = false;
									break;
								}
							}
							if (addFlag) {
								List<S> u1b = new ArrayList<S>(u1);
								u1b.addAll(b);
								if (!SUR.contains(u1b))
									toAdd.add(u1b);
							}
						}
					}
				}
			}
			//System.out.println("distributing evidence by adding:");
			//for (List<A> w : toAdd)
			//	System.out.println(w.toString());
			//Scanner scanner = new Scanner(System.in);
			//scanner.nextLine();
			R.addAll(toAdd);
			SUR.addAll(toAdd);
			return toAdd.size() > 0;
		}
		
		//When a new state S is added, a new experiment e is added. 
		//Finds all S' that agree with S on everything but e (i.e., states that may have been split from S)
		// 
		public List<List<S>> findSplitStates(List<S> newState, SymbolicOracle<P,S> o) throws TimeoutException{
			List<List<S>> splitStates = new ArrayList<List<S>>();
			List<Boolean> newRow = this.safeRow(newState, o);
			newRow.remove(newRow.size()-1);
			for(List<S> state : S) {
				List<Boolean> stateRow = this.safeRow(state, o);
				stateRow.remove(stateRow.size() - 1);
				if(stateRow.equals(newRow)) {
					splitStates.add(new ArrayList<S>(state));
				}
			}
			
			return splitStates;
		}
		
		@Override
		public String toString() {
			String ret = "E:";
			for (List<S> w : E) ret += " " + w;
			ret += "\nS:\n";
			for (List<S> w : S) {
				ret += " " + w + " :";
				for (List<S> e : E) {
					List<S> we = new ArrayList<S>(w);
					we.addAll(e);
					if (f.containsKey(we)) {
						if (f.get(we)) ret += " +";
						else ret += " -";
					}
					else ret += "  ";
				}
				ret += "\n";
			}
			ret += "R:";
			for (List<S> w : R) {
				ret += "\n " + w + " :";
				for (List<S> e : E) {
					List<S> we = new ArrayList<S>(w);
					we.addAll(e);
					if (f.containsKey(we)) {
						if (f.get(we)) ret += " +";
						else ret += " -";
					}
					else ret += "  ";
				}
			}
			return ret;
		}

	}
}
package learning_symbolic_ce.sfa;


import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAMove;
import automata.sfa.SFAInputMove;
import theory.BooleanAlgebra;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class SinglePathSFAOracle<P, S> extends SymbolicOracle<P, S> {

	private SFA<P, S> toLearn;
	private BooleanAlgebra<P, S> ba;
	
	//assumes toLearn automaton is canonical.
	public SinglePathSFAOracle (SFA<P, S> toLearn, BooleanAlgebra<P, S> ba) throws TimeoutException{
		//this.toLearn = (SFA<P, S>)toLearn.clone();
		this.toLearn = toLearn.minimize(ba);
		this.ba = ba;
	}
	
	public void printStats(){
		try {
			long unixTime = System.currentTimeMillis() / 1000L;
			 PrintWriter writer = new PrintWriter("statsOutput" + String.valueOf(unixTime) + ".txt", "UTF-8");
			 writer.println("Number Membership Queries: " + String.valueOf(this.getNumMembership()));
			 writer.println("Number Equivalence Queries: " + String.valueOf(this.getNumEquivalence()));
			 writer.close();
		}  catch (FileNotFoundException e1) {
			   assert false :  "File cannot be created";
			} catch (UnsupportedEncodingException e2) {
			   assert false : "This will literally never happen";
			} 
	}
	
	@Override
	protected List<P> checkEquivalenceImpl(SFA<P, S> compareTo) throws TimeoutException {
		SFA<P,S> d1 = SFA.difference(toLearn, compareTo, ba, Long.MAX_VALUE);
		SFA<P,S> d2 = SFA.difference(compareTo, toLearn, ba, Long.MAX_VALUE);
		SFA<P,S> sdiff = SFA.union(d1,d2,ba);

		List<S> wit = sdiff.getWitness(ba);
		if(wit == null) { //learned correct automaton.
			printStats();
			return null;
		}
		
		List<SFAMove<P,S>> symbWit = new ArrayList<SFAMove<P,S>>(); //The list of transitions followed the witness
		List<P> symbCE = new ArrayList<P>(); //The subset of symWit that is actually a counterexample
		if(wit.isEmpty()) {return symbCE;}
		
		
		ArrayList<SFAMove<P,S>> transitions = new ArrayList<SFAMove<P,S>>(toLearn.getTransitions());
		Integer currState = toLearn.getInitialState();
		int witPos = 0;
		while (witPos < wit.size()) {
			for(int i = 0; i < transitions.size(); i++) {
				SFAMove<P,S> move = transitions.get(i);
				assert !move.isEpsilonTransition() : "epsilon transition in target automaton";
				if(move.from.equals(currState) && move.hasModel(wit.get(witPos), ba)){
					currState = move.to;
					P guard = ((SFAInputMove<P,S>)move).guard;
					symbWit.add(new SFAInputMove<P,S>(witPos, witPos+1, guard));
					witPos += 1;
					break;
				}
			}
		}
		
		List<Integer> finalState = new ArrayList<Integer>();
		finalState.add(symbWit.size());
		SFA<P,S> targetPathAutomaton = SFA.MkSFA(symbWit, 0, finalState, ba);
		SFA<P,S> ceAut;
		Boolean negCE = compareTo.accepts(wit, ba);
		
		if(negCE) {
			assert !toLearn.accepts(wit, ba) : "both target and hypothesis accept counter-example";
		} else {
			assert toLearn.accepts(wit, ba) : "both target and hypothesis accept counter-example";
		}
		
		
		if(negCE) {
			ceAut = compareTo.intersectionWith(targetPathAutomaton, ba, Long.MAX_VALUE);
		}
		else {
			ceAut = targetPathAutomaton.minus(compareTo, ba);
		}
		ceAut = ceAut.minimize(ba);

		ArrayList<Integer> currentStates = new ArrayList<Integer>(ceAut.getFinalStates());
		
		
		ArrayList<Integer> nextStates;
		//ArrayList<SFAMove<P,S>> ceTransitions = new ArrayList<SFAMove<P,S>>(ceAut.getTransitions());
		int counter = wit.size();
		while(counter > 0) {
			nextStates = new ArrayList<Integer>();
			Collection<SFAMove<P, S>> currTransitions = ceAut.getTransitionsTo(currentStates);
			Collection<P> guards = new ArrayList<P>();
			for(SFAMove<P,S> move : currTransitions) {
				guards.add(((SFAInputMove<P,S>)move).guard);
				nextStates.add(move.from);
			}
			symbCE.add(0,ba.MkOr(guards));
			currentStates = new ArrayList<Integer>(nextStates);
			counter--;
		}
		
		return symbCE;
	}
	
	@Override
	protected boolean checkMembershipImpl(List<S> w) throws TimeoutException {
		return toLearn.accepts(w, ba);
	}
}

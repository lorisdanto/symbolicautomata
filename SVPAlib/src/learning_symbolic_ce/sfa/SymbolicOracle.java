package learning_symbolic_ce.sfa;

import java.util.List;
import org.sat4j.specs.TimeoutException;
import automata.sfa.SFA;


public abstract class SymbolicOracle<P, S>{
	private int numEquivalence = 0;
	private int numMembership = 0;
	
	//Returns a symbolic counter-example as a list of predicates
	protected abstract List<P> checkEquivalenceImpl(SFA<P, S> compareTo) throws TimeoutException;
	
	protected abstract boolean checkMembershipImpl(List<S> w) throws TimeoutException;
	
	/**
	 * 
	 * @param compareTo The guessed SFA
	 * @return null if equivalent, else a minimal-length list of characters that distinguishes the automata
	 * @throws TimeoutException 
	 */
	public final List<P> checkEquivalence(SFA<P, S> compareTo) throws TimeoutException {
		numEquivalence++;
		return checkEquivalenceImpl(compareTo);
	}
	
	public final boolean checkMembership(List<S> w) throws TimeoutException {
		numMembership++;
		return checkMembershipImpl(w);
	}
	
	public int getNumEquivalence() { 
		return numEquivalence;
	}
	
	public int getNumMembership() { 
		return numMembership;
	}
}

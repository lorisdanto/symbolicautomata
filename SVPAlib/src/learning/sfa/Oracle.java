package learning.sfa;

import java.util.List;

import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;


public abstract class Oracle<P, S> {
	
	/**
	 * 
	 * @param compareTo The guessed SFA
	 * @return null if equivalent, else a minimal-length list of characters that distinguishes the automata
	 * @throws TimeoutException 
	 */
	public abstract List<S> checkEquivalence(SFA<P, S> compareTo) throws TimeoutException;
	
	public abstract boolean checkMembership(List<S> w);
}

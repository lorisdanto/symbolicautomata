/**
 * Membership oracle instantiation for the SFA algebra.
 * @author George Argyros
 */
package algebralearning.sfa;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.MembershipOracle;
import automata.sfa.SFA;
import theory.BooleanAlgebra;
/**
 * 
 * Membership oracle class used by the SFA Learning algorithm implementation.
 *
 * @param <P> The type of predicates used by the SFA.
 * @param <D> The underlying domain used by the SFA.
 */
public class SFAMembershipOracle <P, D> extends MembershipOracle <List<D>> {
	
	private SFA <P,D> sfa;
	private BooleanAlgebra <P,D> ba;
	private Hashtable <List<D>, Boolean> cache;	
	private Integer distinctQueries;
	private Integer cachedQueries;
	
	/**
	 *  
	 * @param target the SFA which is to be queried.
	 * @param b The boolean algebra used by the target SFA.
	 */
	public SFAMembershipOracle(SFA <P,D> target, BooleanAlgebra <P,D> b) {		
		if (target == null || b == null) {
			throw new AssertionError("SFA and boolean algebra cannot be null");
		}		
		sfa = target;
		ba = b;
		cache = new Hashtable<>();
		distinctQueries = 0;
		cachedQueries = 0;
	}
	
	/**
	 * 
	 * @param input the input for the target SFA.
	 * @return true/false depending whether sfa accepts or rejects input.
	 */
	public boolean query(List <D> input) throws TimeoutException {		
		Boolean res; 
		if (input == null) {
			throw new AssertionError("Null input in membership query");
		}
		if (cache.containsKey(input)) {
			cachedQueries ++;
			return cache.get(input);
		}
		distinctQueries ++;
		res = sfa.accepts(input, ba);
		cache.put(new LinkedList <D>(input), res);
		return res;
	}
	
	/**
	 * @return the number of distinct queries performed so far.
	 */
	public Integer getDistinctQueries() {
		return distinctQueries;
	}
	/**
	 * @return the number of cached queries performed so far.
	 */
	public Integer getCachedQueries() {
		return cachedQueries;
	}
	
	
}

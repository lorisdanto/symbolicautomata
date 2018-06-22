/**
 * Equivalence oracle instantiation for the SFA algebra.
 * @author George Argyros
 */

package algebralearning.sfa;

import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.EquivalenceOracle;
import automata.sfa.SFA;
import theory.BooleanAlgebra;

/**
 * Implements the equivalence oracle for SFAs. 
 * 
 * @param <P> The type of predicates in the target SFA. 
 * @param <D> The domain of the algebra of the target SFA.
 */
public class SFAEquivalenceOracle <P,D> extends EquivalenceOracle <SFA <P,D>, List <D>> {

	private SFA <P,D> correctModel;
	private BooleanAlgebra <P,D> ba;
	private List <D> cachedCe; 	
	private Integer distinctCeNum;
	private Integer cachedCeNum;
	
	/**
	 * 
	 * @param cModel the target SFA for which equivalence will be checked against.
	 * @param b the boolean algebra used by the cModel SFA.
	 */
	public SFAEquivalenceOracle(SFA <P,D> cModel, BooleanAlgebra <P,D> b) {		
		correctModel = cModel;
		ba = b;
		distinctCeNum = 0;
		cachedCeNum = 0;
		// cachedCe contains the last counterexample and the model is checked against it before 
		// invoking the more expensive equivalence test. 
		cachedCe = null;
	}

	/**
	 * Internal method to compare the provided model with the correctModel SFA.
	 * 
	 * @param model model to compare against.
	 * @return An input under which the two models differ or null if they are equivalent.
	 * @throws TimeoutException
	 */
	private List<D> compareWithTarget(SFA <P,D> model) throws TimeoutException {
		
		SFA <P,D> diff;
		
		diff = model.complement(ba);
		diff = diff.intersectionWith(correctModel, ba);
		if (!diff.isEmpty()) {
			return diff.getWitness(ba);
		}
		diff = correctModel.complement(ba);
		diff = diff.intersectionWith(model, ba);
		if (!diff.isEmpty()) {
			return diff.getWitness(ba);
		}		
		return null;		
	}
    
    public List <D>getCounterexample (SFA <P,D> model) throws TimeoutException {
    		List <D> ce;
    		
    		if ((cachedCe != null) && 
    				(model.accepts(cachedCe, ba) != correctModel.accepts(cachedCe, ba))) {
    			cachedCeNum ++;
    			return cachedCe;
    		}    			    		
    		ce = compareWithTarget(model);    		
    		if (ce == null) {    			
    			return null;
    		} else {    			
    			cachedCe = new LinkedList <D>(ce);
    			distinctCeNum ++;
    			return ce;
    		}
    }

    /**
     * @return the number of cached counterexamples used so far.
     */
    	public Integer getCachedCeNum() {
    		   return cachedCeNum; 		
    	}
    	
    	/**
    	 * @return the number of distinct counterexamples used so far.
    	 */
    	public Integer getDistinctCeNum() {
 		   return distinctCeNum; 		
 	}    	
}

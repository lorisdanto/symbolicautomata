/**
 * Learning algorithm for disjunctions (equality algebra)
 *
 * @author George Argyros
 */
package algebralearning.equality;

import java.util.HashSet;
import org.sat4j.specs.TimeoutException;

import algebralearning.AlgebraLearner;
import algebralearning.oracles.*;
import theory.BooleanAlgebra;

/**
 * For details see the paper and the comments below.
 * 
 * @param <P> The type of predicates in the equality algebra
 * @param <D> The domain of the algebra 
 */
public class EqualityAlgebraLearner <P,D> extends AlgebraLearner <P,D> {
	
	private BooleanAlgebra <P,D> ba;
	private HashSet <D> posExamples;
	private HashSet <D> negExamples;
	private MembershipOracle <D>memb;
	

	public EqualityAlgebraLearner(MembershipOracle <D> m, BooleanAlgebra <P,D> b) {
		ba = b;
		memb = m;
		posExamples = new HashSet <>();
		negExamples = new HashSet <>();
	}
	
	private P generatePositiveModel() throws TimeoutException {
		P model = ba.False();
		if (negExamples.size() == 0) {
			return ba.True();
		}		
		for (D e : posExamples) {
			model = ba.MkOr(ba.MkAtom(e), model);
		}
		return model;
	}
	
	private P generateNegativeModel() throws TimeoutException {
		P model = ba.False();
		for (D e : negExamples) {
			model = ba.MkOr(ba.MkAtom(e), model);
		}
		return ba.MkNot(model);		
	}
	
	/**
	 * The algorithm will either generate a disjunction of the form ~(x1 v x2 v ... v xn)
	 * if the number of positive examples are more than the negatives or a disjunction of 
	 * the form (x1 v x2 v ... v xn) if the negative samples are more than the positive.
	 * It can be shown that this method will always converge with at most 2k counterexamples where
	 * k is the size of the target disjunction.
	 * 
	 * @return the model produced based on the given samples in posExamples and negExamples
	 * @throws TimeoutException
	 */
	private P generateModel( ) throws TimeoutException {
		if (posExamples.size() == 0) {
			return ba.False();
		} 
		if (negExamples.size() == 0) {
			return ba.True();
		}
		if (posExamples.size() >= negExamples.size()) {
			return generateNegativeModel();
		} 		
		return generatePositiveModel();		
	}
	
	/**
	 * Updates the set of positive or negative samples with a given counterexample
	 * 
	 * @param atom the counterexample provided
	 * @throws TimeoutException
	 */
	private void updateExampleLists(D atom) throws TimeoutException {		
		if (memb.query(atom)) {
			posExamples.add(atom);
		} else {
			negExamples.add(atom);			
		}
		return;
	}
	
	/**** PUBLIC METHODS ******/
		
	
	public P getModel() throws TimeoutException {		
		return generateModel();
	}
		
	public P updateModel(D ce) throws TimeoutException {		
		if (ba.HasModel(generateModel(), ce) == memb.query(ce)) {
			throw new AssertionError("Provided counterexample is not a counterexample");
		}		
		updateExampleLists(ce);
		return generateModel();		
	}

	public P getModelFinal(EquivalenceOracle <P,D> equiv) throws TimeoutException {		
		D ce;
		P model = getModel();
		while ((ce = equiv.getCounterexample(model)) != null) {
			model = updateModel(ce);
		}
		return model;
	}

}

/**
 * SVPAlib
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory;

import java.util.List;

/**
 * BooleanAlgebraSubst: A Boolean Alegbra with substitution
 * @param <P> The type of predicates forming the Boolean algebra
 * @param <PL> The type of predicates over lists of variables 
 * @param <F> The type of functions S->S in the Boolean Algebra 
 * @param <S> The domain of the Boolean algebra
 */
public abstract class BooleanAlgebraSubstAndOutputs<P, PL, F, S> extends BooleanAlgebraSubst<P, F, S>{
	
	/**
	 * Returns a list of P predicates whose conjunction expresses the PL p
	 * e.g.: for \phi(x, y) = (x = 1) \wedge (x = y), computes [x = 1, x = 1]
	 * which stands for \phi(x, y) = (x = 1) \wedge (y = 1)
	 */
	public abstract List<P> getCartesianPredicate(PL p);
	
	/**
	 * Returns a list of list of P predicates whose disjunction expresses the PL p
	 * e.g.: for \phi(x, y) = (0 < x < 3) \wedge (x = y), computes [[x = 1, x = 1], [x = 2, x = 2]]
	 * which stands for \phi(x, y) = ((x = 1) \wedge (y = 1)) \vee ((x = 2) \wedge (y = 2))
	 */
	public abstract List<List<P>> getMonadicPredicate(PL p);
	
	/**
	 * get the restricted output based on <code>p</code> and <code>f</code>
	 * @return \psi(y) = \exists x. \phi(x) \wedge f(x)=y
	 */
	public abstract PL getRestrictedOutput(P p, List<F> fs);
	
}

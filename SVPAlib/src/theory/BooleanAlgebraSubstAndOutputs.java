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
 * @param <F> The type of functions S->S in the Boolean Algebra 
 * @param <S> The domain of the Boolean algebra
 */
public abstract class BooleanAlgebraSubstAndOutputs<P, PL, F, S> extends BooleanAlgebraSubst<P, F, S>{
	
	/**
	 * hacky for now
	 */
	public abstract List<P> getCartesianPredicate(PL p);
	
	/**
	 * get the restricted output based on <code>p</code> and <code>f</code>
	 * @return \psi(y) = \exists x. \phi(x) \wedge f(x)=y
	 */
	public abstract PL getRestrictedOutput(P p, List<F> fs);
	
}

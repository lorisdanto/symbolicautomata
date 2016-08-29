/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory.svpa.equalityalgebra;

/**
 * A unary predicate wrapper for the equality algebra of binary/unary predicates used by SVPA. 
 * See constructors for more information.
 */
public class UnaryPredicate<P,S> extends EqualityPredicate<P, S>{
	
	P predicate;
	
	/**
	 * A unary predicate describing the elements in <code>predicate</code>.
	 * This should only be used on calls and internals of SVPAs.
	 */
	public UnaryPredicate(P predicate) {
		this(predicate, false);		
	}
	
	/**
	 * The unary predicate describing the elements in <code>predicate</code>
	 * If isReturn=true, the set describes a binary predicate (where the first component is true 
	 * and the second id predicate)  and should only be used for return of SVPAs.
	 */
	public UnaryPredicate(P predicate, boolean isReturn) {
		this.isReturn=isReturn;
		this.predicate = predicate;
		
	}

	public P getPredicate() {
		return predicate;
	}
}

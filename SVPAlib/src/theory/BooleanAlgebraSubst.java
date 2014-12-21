package theory;



public abstract class BooleanAlgebraSubst<P,F,S> extends BooleanAlgebra<P, S>{

	public abstract F MkSubstFuncFunc(F f1, F f2);
	
	public abstract S MkSubstFuncConst(F f, S c);
	
	public abstract P MkSubstFuncPred(F f, P p);
	
}

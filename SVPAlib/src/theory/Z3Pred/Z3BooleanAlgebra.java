package Z3Pred;

import java.util.Collection;
import java.util.HashMap;

import theory.BooleanAlgebra;
import theory.BooleanAlgebraSubst;
import utilities.Pair;

import com.microsoft.z3.*;
public class Z3BooleanAlgebra extends BooleanAlgebraSubst<BoolExpr, Expr, Expr>{
	Context ctx;
	public Z3BooleanAlgebra (Context c){
		ctx = c;
	}
    boolean prove (Context ctx, BoolExpr f, boolean useMBQI)
    {
        BoolExpr[] assumptions = new BoolExpr[0];
        return prove(ctx, f, useMBQI, assumptions);
    }

    boolean prove(Context ctx, BoolExpr f, boolean useMBQI,
            BoolExpr... assumptions)
    {
        System.out.println("Proving: " + f);
        Solver s = ctx.mkSolver();
        Params p = ctx.mkParams();
        p.add("mbqi", useMBQI);
        s.setParameters(p);
        for (BoolExpr a : assumptions)
            s.add(a);
        s.add(ctx.mkNot(f));
        Status q = s.check();

        switch (q)
        {
        case UNKNOWN:
        	return false;
        case SATISFIABLE:
        	return true;
        case UNSATISFIABLE:
        	return false;
        }
        return false;
    }

	@Override
	public Expr MkSubstFuncFunc(Expr f1, Expr f2) {
		Expr[] f = new Expr[1];
		f[0] = f2;
		return f1.substituteVars(f);
	}

	@Override
	public Expr MkSubstFuncConst(Expr f1, Expr c) {
		Expr[] f = new Expr[1];
		f[0] = c;
		return f1.substituteVars(f);
	}

	@Override
	public BoolExpr MkSubstFuncPred(Expr f, BoolExpr p) {
		Expr[] s = new Expr[1];
		s[0] = f;
		return (BoolExpr) p.substituteVars(s);
	}

	@Override
	public BoolExpr MkNot(BoolExpr p) {
		return ctx.mkNot(p);
	}

	@Override
	public BoolExpr MkOr(Collection<BoolExpr> pset) {
        BoolExpr[] parray = pset.toArray(new BoolExpr[pset.size()]);
		return ctx.mkOr(parray);
	}

	@Override
	public BoolExpr MkOr(BoolExpr p1, BoolExpr p2) {
        BoolExpr[] parray = new BoolExpr[2];
        parray[0] = p1;
        parray[1] = p2;
		return ctx.mkOr(parray);
	}

	@Override
	public BoolExpr MkAnd(Collection<BoolExpr> pset) {
        BoolExpr[] parray = pset.toArray(new BoolExpr[pset.size()]);
		return ctx.mkAnd(parray);
	}

	@Override
	public BoolExpr MkAnd(BoolExpr p1, BoolExpr p2) {
        BoolExpr[] parray = new BoolExpr[2];
        parray[0] = p1;
        parray[1] = p2;
		return ctx.mkAnd(parray);
	}

	@Override
	public BoolExpr True() {
		return ctx.mkTrue();
	}

	@Override
	public BoolExpr False() {
		return ctx.mkFalse();
	}

	@Override
	public boolean AreEquivalent(BoolExpr p1, BoolExpr p2) {
    	boolean nonEquivalent = IsSatisfiable(MkAnd(p1, MkNot(p2))) ||
    			IsSatisfiable(MkAnd(MkNot(p1),p2)); 
    	return !nonEquivalent;
	}

	@Override
	public boolean IsSatisfiable(BoolExpr p1) {
		return prove(ctx,p1,false);
	}

	@Override
	public boolean HasModel(BoolExpr p1, Expr el) {
		return p1.substituteVars(new Expr[]{el}).isTrue();
	}

	@Override
	public boolean HasModel(BoolExpr p1, Expr el1, Expr el2) {
		return false;
	}

	@Override
	public Expr generateWitness(BoolExpr p1) {
		//TODO 
		Solver solver = ctx.mkSolver();
		solver.add(p1);
		Model m = solver.getModel();
		return m.getConstInterp(m.getConstDecls()[0]);
	}
	

	@Override
	public Pair<Expr, Expr> generateWitnesses(BoolExpr p1) {
		return null;
	}


}

package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import utilities.Pair;


public abstract class BooleanAlgebra<U,S> {

	public abstract U MkNot(U p);
	
	public abstract U MkOr(Collection<U> p1);
	
	public abstract U MkOr(U p1, U p2);
	
	public abstract U MkAnd(Collection<U> p1);
	
	public abstract U MkAnd(U p1, U p2);	
	
	public abstract U True();
	
	public abstract U False();
	
	public abstract boolean AreEquivalent(U p1,U p2);
	
	public abstract boolean IsSatisfiable(U p1);
	
	public abstract boolean HasModel(U p1, S el);
	
	public abstract boolean HasModel(U p1, S el1,S el2);
	
	public abstract S generateWitness(U p1);
	
	public abstract Pair<S,S> generateWitnesses(U p1);
	
	//Minterm generation
    public Collection<Pair<U, ArrayList<Integer>>> GetMinterms(ArrayList<U> internalPredicates)
    {
        return GetMinterms(internalPredicates, True());
    }

    public Collection<Pair<U, ArrayList<Integer>>> GetMinterms(ArrayList<U> predicates, U startPred)
    {
    	HashSet<Pair<U,ArrayList<Integer>>> minterms = new HashSet<Pair<U,ArrayList<Integer>>>();
        GetMintermsRec(predicates, 0, startPred, new ArrayList<Integer>(), minterms);
    	return minterms;
    }

    private void GetMintermsRec(ArrayList<U> predicates, int n, U currPred, ArrayList<Integer> setBits,
    		HashSet<Pair<U, ArrayList<Integer>>> minterms)
    {
        if (!IsSatisfiable(currPred))
            return;

        //Keep exploring the tree, if leaf done
        if (n == predicates.size())
            minterms.add(new Pair<U, ArrayList<Integer>>(currPred, setBits));
        else
        {
        	ArrayList<Integer> posList = new ArrayList<Integer>(setBits);
            posList.add(1);
            GetMintermsRec(predicates, n + 1, MkAnd(currPred, predicates.get(n)), posList,minterms);

            ArrayList<Integer> negList = new ArrayList<Integer>(setBits);
            negList.add(0);
            GetMintermsRec(predicates, n + 1, MkAnd(currPred, MkNot(predicates.get(n))),negList,minterms);
        }
    }
	
}

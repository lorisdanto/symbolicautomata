
/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory.bddalgebra;

import java.util.Collection;

import java.lang.UnsupportedOperationException;
import theory.BooleanAlgebra;
import theory.bdd.BDD;
import theory.bdd.BDDFactory;
import utilities.Pair;

/**
 * CharSolver: an interval based solver for the theory of characters
 */
public class BDDSolver extends BooleanAlgebra<BDD, BDD> {

	public BDDFactory factory;
	
	public BDDSolver(int numVars) {
		super();		
		factory = BDDFactory.init(500000 , 125000);
		factory.setVarNum(numVars);
	}
	
	public BDDSolver(int numVars, int numNodes, int chaceSize) {
		super();
		factory = BDDFactory.init(numNodes, chaceSize);
		factory.setVarNum(numVars);
	}

	@Override
	public BDD MkNot(BDD p) {
		return p.not();
	}

	@Override
	public BDD MkOr(Collection<BDD> pset) {
		BDD acc = factory.zero();
		for(BDD bdd:pset)
			acc=acc.or(bdd);
		return acc;
	}

	@Override
	public BDD MkOr(BDD p1, BDD p2) {
		return p1.or(p2);
	}

	@Override
	public BDD MkAnd(Collection<BDD> pset) {
		BDD acc = factory.one();
		for(BDD bdd:pset)
			acc=acc.and(bdd);
		return acc;
	}

	@Override
	public BDD MkAnd(BDD p1, BDD p2) {
		return p1.and(p2);
	}

	@Override
	public BDD True() {		
		return factory.one();
	}

	@Override
	public BDD False() {
		return factory.zero();
	}

	@Override
	public boolean AreEquivalent(BDD p1, BDD p2) {
		return p1.equals(p2);
	}

	@Override
	public boolean IsSatisfiable(BDD p1) {
		return !p1.isZero();
	}

	@Override
	public boolean HasModel(BDD p1, BDD el) {
		return IsSatisfiable(p1.and(el));
	}

	@Override
	public boolean HasModel(BDD p1, BDD el1, BDD el2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	private BDD extractWitness(BDD p) {
		BDD res = factory.one();
		BDD copy = p;
		//res = res.and(factory.ithVar(0));
		//res.and(factory.ithVar(1));
		//res.printDot();
		//factory.ithVar(1).and(factory.ithVar(0)).printDot();
		Integer index = 0;
		res = factory.one();
		for (int i = 0; i < factory.varNum(); i++) {
			if (!factory.ithVar(i).and(copy).equals(factory.zero())) {
				res = res.and(factory.ithVar(index));			;
				copy = copy.and(factory.ithVar(index));
			} else {
				res = res.and(factory.nithVar(index));
				copy = copy.and(factory.nithVar(index));
			}
			index ++;
		}
		return res;
	}
	
	@Override
	public BDD generateWitness(BDD p1) {
		BDD sat = extractWitness(p1);//p1.satOne();
		/*
		sat.not().printDot();
		if (!sat.not().and(p1).equals(factory.zero())) {
			throw new AssertionError("Witness is buggy 1");
		}
		if (sat.and(p1).equals(factory.zero())) {
			throw new AssertionError("Witness is buggy 2");
		}*/		
		return sat;
	}

	@Override
	public Pair<BDD, BDD> generateWitnesses(BDD p1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public BDD MkAtom(BDD s) {
		return s;
	}
    
	

}

package automata.safa;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.LinkedList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import automata.safa.booleanexpression.*;

public class SATRelation extends SAFARelation {
	private ISolver solver;
	private int maxid = 1; // name of the next available literal.  Always odd.

	public SATRelation(ISolver s) {
		solver = s;
	}
	
	public SATRelation() {
		solver = SolverFactory.newDefault();
	}
	
	private int fresh() {
		int fresh = maxid;
		maxid += 2;
		solver.registerLiteral(fresh);
		return fresh;
	}
	
	private void unsafeAddClause(VecInt clause) {
		try {
			solver.addClause(clause);
			//System.out.println("Add clause: " + clause.toString());
		} catch (ContradictionException ex) {
			// should never happen
			ex.printStackTrace();
			System.err.println("Contradiction when adding clause: " + clause.toString());
			System.exit(-1);
		}
	}

	private int mkAnd(List<Integer> cube) {
		if (cube.isEmpty()) {
			throw new IllegalArgumentException("mkAnd requires at least one literal");
		} else if (cube.size() == 1) {
			return cube.get(0);
		} else {
			int cubeName = fresh();
			//System.out.println(cubeName + " = And " + cube.toString());
			VecInt cubeImpliesCubeName = new VecInt();
		
			cubeImpliesCubeName.push(cubeName);
			for (Integer literal : cube) {
				// cubeName => literal
				VecInt cubeNameImpliesLit = new VecInt();
				cubeNameImpliesLit.push(-cubeName);
				cubeNameImpliesLit.push(literal);
				unsafeAddClause(cubeNameImpliesLit);
			
				cubeImpliesCubeName.push(-literal);
			}
			// cube => cubeName
			unsafeAddClause(cubeImpliesCubeName);
			return cubeName;
		}
	}

	private int mkOr(List<Integer> clause) {
		if (clause.isEmpty()) {
			throw new IllegalArgumentException("mkOr requires at least one literal");
		} else if (clause.size() == 1) {
			return clause.get(0);
		} else {
			int clauseName = fresh();
			//System.out.println(clauseName + " = Or " + clause.toString());
			// clauseName => clause
			VecInt clauseNameImpliesClause = new VecInt();
			clauseNameImpliesClause.push(-clauseName);
			for (Integer literal : clause) {
				// literal => cubeName
				VecInt litImpliesClauseName = new VecInt();

				clauseNameImpliesClause.push(literal);
				litImpliesClauseName.push(clauseName);
				litImpliesClauseName.push(-literal);
				unsafeAddClause(litImpliesClauseName);
			}
			unsafeAddClause(clauseNameImpliesClause);
			return clauseName;
		}
	}

	/**
	 * Return a literal l and assert into the current context that l is equivalent to the state
	 * 	formula p
	 * @param p
	 * @param offset Either 2 or 4.  Each state in p is represented by an even literal, which is
	 * 	either 2 or 0 mod 4, depending on offset.  LHS state formulas should pass 2, RHS state formulas
	 * 	should pass 4.
	 * @return
	 */
	private int mkStateFormula(BooleanExpression p, int offset) {
		if (p instanceof SumOfProducts) {
			SumOfProducts sop = (SumOfProducts) p;
			List<Integer> disjuncts = new LinkedList<>();
			for (List<Integer> cube : sop.getCubes()) {
				List<Integer> litCube = new LinkedList<>();
				for (Integer state : cube) {
					litCube.add(4 * state + offset);
				}
				disjuncts.add(mkAnd(litCube));
			}
			return mkOr(disjuncts);
		} else {
			throw new IllegalArgumentException("can only compute state formula for SumOfProducts");
		}
	}
	
	private int mkIff(BooleanExpression p, BooleanExpression q) {
		int pname = mkStateFormula(p, 2);
		int qname = mkStateFormula(q, 4);
		
		List<Integer> positive = new LinkedList<>();
		positive.add(pname);
		positive.add(qname);
		int positiveName = mkAnd(positive);

		List<Integer> negative = new LinkedList<>();
		negative.add(-pname);
		negative.add(-qname);
		int negativeName = mkAnd(negative);

		
		List<Integer> posneg = new LinkedList<>();
		posneg.add(positiveName);
		posneg.add(negativeName);
		return mkOr(posneg);
	}
	
	public boolean isMember(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		VecInt mem = new VecInt();
		mem.push(-mkIff(p, q));
		return !solver.isSatisfiable(mem, false);
	}
	
	public void add(BooleanExpression p, BooleanExpression q) {
		VecInt pair = new VecInt();
		pair.push(mkIff(p, q));
		unsafeAddClause(pair);
	}
}

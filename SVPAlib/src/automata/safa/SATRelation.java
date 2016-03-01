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
	
	public  SATRelation() {
		solver = SolverFactory.newDefault();
	}
	
	private int fresh() {
		int fresh = maxid;
		fresh += 2;
		return fresh;
	}
	
	private void unsafeAddClause(VecInt clause) {
		try {
			solver.addClause(clause);
		} catch (ContradictionException ex) {
			// should never happen
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	private int mkAnd(List<Integer> cube) {
		int cubeName = fresh();
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

	private int mkOr(List<Integer> clause) {
		int clauseName = fresh();
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
		int pname = mkStateFormula(p, 0);
		int qname = mkStateFormula(q, 2);
		int iffName = fresh();
		// ...
		return iffName;
	}
	
	public boolean isMember(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		int pairName = mkIff(p, q);
		return !solver.isSatisfiable(new VecInt(-pairName), false);
	}
	
	public void add(BooleanExpression p, BooleanExpression q) {
		int pairName = mkIff(p, q);
		unsafeAddClause(new VecInt(pairName));
	}
}

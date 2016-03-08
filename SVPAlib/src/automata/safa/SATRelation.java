package automata.safa;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import automata.safa.booleanexpression.*;

public class SATRelation extends SAFARelation {
	private ISolver solver;
	private int maxid = 1; // name of the next available literal.  Always odd.

	// Hash consing
	private HashMap<List<Integer>, Integer> andCache;
	private HashMap<List<Integer>, Integer> orCache;

	public SATRelation(ISolver s) {
		solver = s;
		andCache = new HashMap<>();
		orCache = new HashMap<>();
	}
	
	public SATRelation() {
		this(SolverFactory.newDefault());
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
		} else if (andCache.containsKey(cube)) {
			return andCache.get(cube);
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
			andCache.put(cube, cubeName);
			return cubeName;
		}
	}

	private int mkAnd(int left, int right) {
		List<Integer> conjuncts = new LinkedList<>();
		conjuncts.add(left);
		conjuncts.add(right);
		return mkAnd(conjuncts);
	}

	private int mkOr(List<Integer> clause) {
		if (clause.isEmpty()) {
			throw new IllegalArgumentException("mkOr requires at least one literal");
		} else if (clause.size() == 1) {
			return clause.get(0);
		} else if (orCache.containsKey(clause)) {
			return orCache.get(clause);
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
			orCache.put(clause, clauseName);
			return clauseName;
		}
	}

	private int mkOr(int left, int right) {
		List<Integer> disjuncts = new LinkedList<>();
		disjuncts.add(left);
		disjuncts.add(right);
		return mkOr(disjuncts);
	}

	/**
	 * Return a literal l and assert into the current context that l is equivalent to the state
	 * 	formula p
	 * @param p
	 * @param stateToLiteral
	 * 	Function used to convert states into literals.  The range of stateToLiteral should be even
	 * 	(to avoid conflict with intermediate literals) and non-zero (zero is not a valid literal).
	 * @return
	 * 	Literal representing the state formula p
	 */
	private int mkStateFormula(BooleanExpression p, Function<Integer,Integer> stateToLiteral) {
		if (p instanceof SumOfProducts) {
			SumOfProducts sop = (SumOfProducts) p;
			List<Integer> disjuncts = new LinkedList<>();
			for (List<Integer> cube : sop.getCubes()) {
				List<Integer> litCube = new LinkedList<>();
				for (Integer state : cube) {
					litCube.add(stateToLiteral.apply(state));
				}
				disjuncts.add(mkAnd(litCube));
			}
			return mkOr(disjuncts);
		} else if (p instanceof PositiveId) {
			PositiveId id = (PositiveId) p;
			return stateToLiteral.apply(id.state);
		} else if (p instanceof PositiveAnd) {
			PositiveAnd and = (PositiveAnd) p;
			return mkAnd(mkStateFormula(and.left, stateToLiteral),
					mkStateFormula(and.right, stateToLiteral));
		} else if (p instanceof PositiveOr) {
			PositiveOr or = (PositiveOr) p;
			return mkOr(mkStateFormula(or.left, stateToLiteral),
					mkStateFormula(or.right, stateToLiteral));
		} else {
			throw new IllegalArgumentException("mkStateFormula: unknown class");
		}
	}
	
	private int mkIff(BooleanExpression p, BooleanExpression q) {
		// p and q are drawn from different vocabularies (0 in p is not the same as 0 in q), so
		// rename them apart.
		int pname = mkStateFormula(p, ((s) -> 4 * s + 2));
		int qname = mkStateFormula(q, ((s) -> 4 * s + 4));
		
		return mkOr(mkAnd(pname, qname), mkAnd(-pname, -qname));
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

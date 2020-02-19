package automata.safa;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SATRelation extends SAFARelation {
	private class SATFactory extends BooleanExpressionFactory<Integer> {
		private ISolver solver;
		private int maxid = 3; // name of the next available literal.  Always odd.  1 reserved for always-true literal

		// Hash consing
		private HashMap<List<Integer>, Integer> andCache;
		private HashMap<List<Integer>, Integer> orCache;

		public SATFactory(ISolver s) {
			solver = s;
			andCache = new HashMap<>();
			orCache = new HashMap<>();
			VecInt trueClause = new VecInt();
			trueClause.push(1);
			unsafeAddClause(trueClause);
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

		public Integer MkAnd(List<Integer> cube) {
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

		public Integer MkAnd(Integer left, Integer right) {
			List<Integer> conjuncts = new LinkedList<>();
			conjuncts.add(left);
			conjuncts.add(right);
			return MkAnd(conjuncts);
		}

		public Integer MkOr(List<Integer> clause) {
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

		public Integer MkOr(Integer left, Integer right) {
			List<Integer> disjuncts = new LinkedList<>();
			disjuncts.add(left);
			disjuncts.add(right);
			return MkOr(disjuncts);
		}

		public Integer MkState(int state) {
			return 2*state;
		}

		public Integer False() {
			return -1;
		}

		public Integer True() {
			return 1;
		}

		public ISolver getSolver() {
			return solver;
		}
	}

	SATFactory factory;
	BooleanExpressionMorphism<Integer> coerce;

	public SATRelation(ISolver s) {
		factory = new SATFactory(s);
		coerce = new BooleanExpressionMorphism<>((state) -> 2 * state + 2, factory);
	}

	public SATRelation() {
		this(SolverFactory.newDefault());
	}
	
	private int mkIff(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		int pname = coerce.apply(p);
		int qname = coerce.apply(q);
		
		return factory.MkOr(factory.MkAnd(pname, qname), factory.MkAnd(-pname, -qname));
	}
	
	public boolean isMember(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		VecInt mem = new VecInt();
		mem.push(-mkIff(p, q));
		return !factory.getSolver().isSatisfiable(mem, false);
	}
	
	public boolean add(BooleanExpression p, BooleanExpression q) throws TimeoutException {
		VecInt pair = new VecInt();
		pair.push(mkIff(p, q));
		try {
			factory.solver.addClause(pair);
			return true;
		} catch (ContradictionException e) {
			return false;
		}
	}
}

package theory.sat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

public class SATBooleanAlgebra extends BooleanAlgebra<Integer, boolean[]> {
	private ISolver solver;
	private int maxid; // next fresh variable starting with universe + 2. {1,
						// ..., universe} correspond to members of the
						// universe, and universe+1 is an always-true variable.
	private int universe; // size of the universe

	// Hash consing
	private HashMap<Set<Integer>, Integer> andCache;
	private HashMap<Set<Integer>, Integer> orCache;
	private HashMap<Integer, Pair<Boolean, TreeSet<Integer>>> reverseCache;

	public SATBooleanAlgebra(ISolver s, int universeSize) {
		if (universeSize < 0) {
			throw new IllegalArgumentException("Universe size must be >= 0");
		}
		solver = s;
		universe = universeSize;
		andCache = new HashMap<>();
		orCache = new HashMap<>();
		reverseCache = new HashMap<>();
		maxid = universeSize + 2;
		VecInt trueClause = new VecInt();
		trueClause.push(universe + 1);
		reverseCache.put(universe + 1, new Pair<>(true, new TreeSet<Integer>()));
		unsafeAddClause(trueClause);
	}

	public SATBooleanAlgebra(int universeSize) {
		this(SolverFactory.newDefault(), universeSize);
	}

	private int fresh() {
		int fresh = maxid;
		maxid++;
		solver.registerLiteral(fresh);
		return fresh;
	}

	private Set<Integer> promoteAnd(Integer p) {
		TreeSet<Integer> singleton = new TreeSet<>();
		singleton.add(p);
		if (reverseCache.containsKey(p)) {
			Pair<Boolean, TreeSet<Integer>> cached = reverseCache.get(p);
			if (cached.getFirst()) {
				return cached.getSecond();
			} else {
				return singleton;
			}
		} else if (reverseCache.containsKey(-p)) {
			Pair<Boolean, TreeSet<Integer>> cached = reverseCache.get(-p);
			if (!cached.getFirst()) {
				TreeSet<Integer> result = new TreeSet<>();
				for (Integer q : cached.getSecond()) {
					result.add(-q);
				}
				return result;
			} else {
				return singleton;
			}
		} else {
			return singleton;
		}
	}

	private Set<Integer> promoteOr(Integer p) {
		TreeSet<Integer> singleton = new TreeSet<>();
		singleton.add(p);
		if (reverseCache.containsKey(p)) {
			Pair<Boolean, TreeSet<Integer>> cached = reverseCache.get(p);
			if (!cached.getFirst()) {
				return cached.getSecond();
			} else {
				return singleton;
			}
		} else if (reverseCache.containsKey(-p)) {
			Pair<Boolean, TreeSet<Integer>> cached = reverseCache.get(-p);
			if (cached.getFirst()) {
				TreeSet<Integer> result = new TreeSet<>();
				for (Integer q : cached.getSecond()) {
					result.add(-q);
				}
				return result;
			} else {
				return singleton;
			}
		} else {
			return singleton;
		}
	}

	private void unsafeAddClause(VecInt clause) {
		try {
			solver.addClause(clause);
			// System.out.println("Add clause: " + clause.toString());
		} catch (ContradictionException ex) {
			// should never happen
			ex.printStackTrace();
			System.err.println("Contradiction when adding clause: " + clause.toString());
			System.exit(-1);
		}
	}

	private boolean unsafeIsSatisfiable(VecInt assumptions) {
		try {
			return solver.isSatisfiable(assumptions, false);
		} catch (TimeoutException ex) {
			ex.printStackTrace();
			System.exit(-1);
			return false;
		}
	}

	public Integer MkAnd(Collection<Integer> subnormalCube) {
		TreeSet<Integer> cube = new TreeSet<>();
		for (Integer p : subnormalCube) {
			cube.addAll(promoteAnd(p));
		}

		for (Integer p : cube) {
			if (cube.contains(-p)) {
				return False();
			}
		}

		if (cube.isEmpty()) {
			return True();
		} else if (cube.size() == 1) {
			return cube.iterator().next();
		} else if (andCache.containsKey(cube)) {
			return andCache.get(cube);
		} else {
			int cubeName = fresh();
			// System.out.println(cubeName + " = And " + cube.toString());
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
			reverseCache.put(cubeName, new Pair<>(true, cube));
			return cubeName;
		}
	}

	public Integer MkAnd(Integer left, Integer right) {
		Set<Integer> conjuncts = new TreeSet<>();
		conjuncts.add(left);
		conjuncts.add(right);
		return MkAnd(conjuncts);
	}

	public Integer MkOr(Collection<Integer> subnormalClause) {
		TreeSet<Integer> clause = new TreeSet<>();
		for (Integer p : subnormalClause) {
			clause.addAll(promoteOr(p));
		}

		for (Integer p : clause) {
			if (clause.contains(-p)) {
				return True();
			}
		}

		if (clause.isEmpty()) {
			return False();
		} else if (clause.size() == 1) {
			return clause.iterator().next();
		} else if (orCache.containsKey(clause)) {
			return orCache.get(clause);
		} else {
			int clauseName = fresh();
			// System.out.println(clauseName + " = Or " + clause.toString());
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
			reverseCache.put(clauseName, new Pair<>(false, clause));
			return clauseName;
		}
	}

	public Integer MkOr(Integer left, Integer right) {
		Set<Integer> disjuncts = new TreeSet<>();
		disjuncts.add(left);
		disjuncts.add(right);
		return MkOr(disjuncts);
	}

	public Integer False() {
		return -(universe + 1);
	}

	public Integer True() {
		return universe + 1;
	}

	public ISolver getSolver() {
		return solver;
	}

	public Integer MkNot(Integer id) {
		return -id;
	}

	@Override
	public boolean AreEquivalent(Integer p, Integer q) {
		VecInt equiv = new VecInt();
		equiv.push(-MkOr(MkAnd(p, q), MkAnd(-p, -q)));
		return !unsafeIsSatisfiable(equiv);
	}

	@Override
	public boolean IsSatisfiable(Integer p) {
		VecInt pclause = new VecInt();
		pclause.push(p);
		return unsafeIsSatisfiable(pclause);
	}

	@Override
	public boolean HasModel(Integer p, boolean[] model) {
		VecInt assumption = new VecInt();
		if (model.length != universe) {
			throw new IllegalArgumentException("Model size not equal to universe size");
		}
		for (int i = 0; i < model.length; i++) {
			if (model[i]) {
				assumption.push(i + 1);
			} else {
				assumption.push(-(i + 1));
			}
		}
		assumption.push(-p);
		return !unsafeIsSatisfiable(assumption);
	}

	@Override
	public boolean HasModel(Integer p1, boolean[] el1, boolean[] el2) {
		throw new UnsupportedOperationException("SATBooleanAlgebra.HasModel(_,_,_) is not implemented");
	}

	@Override
	public boolean[] generateWitness(Integer p) {
		if (!IsSatisfiable(p)) {
			throw new IllegalArgumentException("Cannot generate witness (unsatisfiable)");
		}
		boolean[] model = new boolean[universe];
		for (int i = 0; i < universe; i++) {
			model[i] = solver.model(i + 1);
		}
		return model;
	}

	@Override
	public Pair<boolean[], boolean[]> generateWitnesses(Integer p) {
		throw new UnsupportedOperationException("SATBooleanAlgebra.generateWitnesses is not implemented");
	}

	@Override
	public Integer MkAtom(boolean[] s) {
		Integer phi = True();
		for (int i = 0; i < s.length; i++)
			phi = MkAnd(phi, i + 1);
		return phi;
	}
}
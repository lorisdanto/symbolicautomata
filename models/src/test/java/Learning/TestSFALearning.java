package Learning;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import learning.sfa.Learner;
import learning.sfa.Oracle;
import learning.sfa.SFAOracle;
import theory.BooleanAlgebra;
import theory.ProductAlgebra;
import theory.cartesian.CartesianProduct;
import theory.intervals.BoundedIntegerSolver;
import theory.intervals.IntPred;
import utilities.Pair;

public class TestSFALearning {

	@Test
	public void testPaperExample() throws TimeoutException {
		BooleanAlgebra<IntPred, Integer> ba = new BoundedIntegerSolver(0,null);

		Integer init = 1;
		List<Integer> fin = Arrays.asList(1);
		List<SFAMove<IntPred, Integer>> trans = new ArrayList<SFAMove<IntPred, Integer>>();
		trans.add(new SFAInputMove<IntPred, Integer>(1, 1, new IntPred(0, 50)));
		trans.add(new SFAInputMove<IntPred, Integer>(1, 1, new IntPred(101, null)));
		trans.add(new SFAInputMove<IntPred, Integer>(1, 2, new IntPred(51, 100)));
		trans.add(new SFAInputMove<IntPred, Integer>(2, 3, new IntPred(21, null)));
		trans.add(new SFAInputMove<IntPred, Integer>(2, 4, new IntPred(0, 20)));
		trans.add(new SFAInputMove<IntPred, Integer>(3, 3, new IntPred(null, null)));
		trans.add(new SFAInputMove<IntPred, Integer>(4, 1, new IntPred(null, 20)));
		trans.add(new SFAInputMove<IntPred, Integer>(4, 3, new IntPred(21, null)));
		SFA<IntPred, Integer> given = SFA.MkSFA(trans, init, fin, ba, false);

		Learner<IntPred, Integer> ell = new Learner<IntPred, Integer>();
		Oracle<IntPred, Integer> o = new SFAOracle<IntPred, Integer>(given, ba);
		SFA<IntPred, Integer> learned = ell.learn(o, ba);

		assertTrue(SFA.areEquivalent(given, learned, ba));
		assertTrue(learned.getStates().size() <= given.getStates().size());
	}

	@Test
	public void testMMExample() throws TimeoutException {
		BooleanAlgebra<IntPred, Integer> ba = new BoundedIntegerSolver(0,null);

		Integer init = 1;
		List<Integer> fin = Arrays.asList(2, 3);
		List<SFAMove<IntPred, Integer>> trans = new ArrayList<SFAMove<IntPred, Integer>>();
		trans.add(new SFAInputMove<IntPred, Integer>(1, 2, new IntPred(null, 50)));
		trans.add(new SFAInputMove<IntPred, Integer>(1, 4, new IntPred(51, null)));
		trans.add(new SFAInputMove<IntPred, Integer>(2, 3, new IntPred(null, 30)));
		trans.add(new SFAInputMove<IntPred, Integer>(2, 4, new IntPred(31, null)));
		trans.add(new SFAInputMove<IntPred, Integer>(3, 4, new IntPred(null, null)));
		trans.add(new SFAInputMove<IntPred, Integer>(4, 1, new IntPred(null, 20)));
		trans.add(new SFAInputMove<IntPred, Integer>(4, 2, new IntPred(51, 80)));
		trans.add(new SFAInputMove<IntPred, Integer>(4, 3, new IntPred(21, 50)));
		trans.add(new SFAInputMove<IntPred, Integer>(4, 4, new IntPred(81, null)));
		SFA<IntPred, Integer> given = SFA.MkSFA(trans, init, fin, ba, false);

		Learner<IntPred, Integer> ell = new Learner<IntPred, Integer>();
		Oracle<IntPred, Integer> o = new SFAOracle<IntPred, Integer>(given, ba);
		SFA<IntPred, Integer> learned = ell.learn(o, ba);

		assertTrue(SFA.areEquivalent(given, learned, ba));
		assertTrue(learned.getStates().size() <= given.getStates().size());
	}

	@Test
	public void testMMPartialOrderExample() throws TimeoutException {
		BooleanAlgebra<IntPred, Integer> ba = new BoundedIntegerSolver(0, 100);
		ProductAlgebra<IntPred, Integer, IntPred, Integer> prodmeta =
				new ProductAlgebra<IntPred, Integer, IntPred, Integer>(ba, ba);

		Integer init = 1;
		List<Integer> fin = Arrays.asList(2, 3);

		List<SFAMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>> trans =
				new ArrayList<SFAMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>>();

		//transitions out of state 1
		CartesianProduct<IntPred, IntPred> temp = new CartesianProduct<>(Arrays.asList(
				new Pair<>(new IntPred(null,45), new IntPred(null,70)),
				new Pair<>(new IntPred(null,60), new IntPred(null,50))));
		temp.normalize(ba,ba);
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(1, 2, temp));
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(1, 4, prodmeta.MkNot(temp)));

		//transitions out of state 2
		temp = new CartesianProduct<>(Arrays.asList(
				new Pair<>(new IntPred(null,30), new IntPred(null,80)),
				new Pair<>(new IntPred(null,40), new IntPred(null,40)),
				new Pair<>(new IntPred(null,80), new IntPred(null,15))));
		temp.normalize(ba,ba);
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(2, 3, temp));
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(2, 4, prodmeta.MkNot(temp)));

		//transitions out of state 3
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(3, 4, prodmeta.True()));

		//transitions out of state 4
		temp = new CartesianProduct<>(new IntPred(null,20), new IntPred(null,30));
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(4, 1, temp));
		temp = new CartesianProduct<>(Arrays.asList(
				new Pair<>(new IntPred(null,55), new IntPred(31,50)),
				new Pair<>(new IntPred(21,70), new IntPred(null,35))));
		temp.normalize(ba, ba);
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(4, 3, temp));
		temp = new CartesianProduct<>(Arrays.asList(
				new Pair<>(new IntPred(null,60), new IntPred(51,90)),
				new Pair<>(new IntPred(56,70), new IntPred(36,70)),
				new Pair<>(new IntPred(71,90), new IntPred(null,50))));
		temp.normalize(ba, ba);
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(4, 2, temp));
		temp = new CartesianProduct<>(Arrays.asList(
				new Pair<>(new IntPred(null,null), new IntPred(91,null)),
				new Pair<>(new IntPred(61,null), new IntPred(71,null)),
				new Pair<>(new IntPred(71,null), new IntPred(51,null)),
				new Pair<>(new IntPred(91,null), new IntPred(null,null))));
		temp.normalize(ba, ba);
		trans.add(new SFAInputMove<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(4, 4, temp));

		SFA<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>> given =
				SFA.MkSFA(trans, init, fin, prodmeta, false);


		Learner<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>> ell =
				new Learner<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>();

		// test learning using our product algebra meta-separating-predicates
		Oracle<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>> o1 =
				new SFAOracle<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(given, prodmeta);
		SFA<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>> learned1 = ell.learn(o1, prodmeta);

		assertTrue(SFA.areEquivalent(given, learned1, prodmeta));
		assertTrue(learned1.getStates().size() <= given.getStates().size());

		// test learning using MM-style cones for separating predicates
		ProductAlgebra<IntPred, Integer, IntPred, Integer> prodcone = new MMConeAlgebra(ba);
		Oracle<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>> o2 =
				new SFAOracle<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>>(given, prodcone);
		SFA<CartesianProduct<IntPred, IntPred>, Pair<Integer, Integer>> learned2 = ell.learn(o2, prodcone);

		assertTrue(SFA.areEquivalent(given, learned2, prodmeta));
		assertTrue(learned2.getStates().size() <= given.getStates().size());

		// for sanity, ensure that these did not give different results
		assertTrue(SFA.areEquivalent(learned1, learned2, prodmeta));
		assertTrue(learned1.getStates().size() == learned2.getStates().size());
	}

}

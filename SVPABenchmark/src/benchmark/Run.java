package benchmark;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import LTLparser.LTLNode;
import LTLparser.LTLParserProvider;
import automata.safa.SAFA;
import automata.safa.booleanexpression.SumOfProductsFactory;
import benchmark.ltlconverter.LTLConverter;
import logic.ltl.LTLFormula;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import utilities.Pair;

public class Run {

	static long timeout = 30000;

	public static void main(String[] args) {
//		RunSelfEquivLTL();
		RunLTLEmptiness();
	}

	public static void RunLTLEmptiness() {

		try {
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					try {
						RunSelfEquivLTLFile(filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void RunEmptiness(Path filePath) throws IOException {
		// parser p;
		// p.
		List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

		for (LTLNode ltl : nodes) {
			Pair<BDDSolver,LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
			BDDSolver bdds = pair.first;
			LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
			SAFA<BDD, BDD> safa = tot.getSAFA(bdds);

			long startTime = System.currentTimeMillis();

			boolean b = true;
			long stopTime = System.currentTimeMillis();

			try {
				b = SAFA.isEmpty(safa, bdds);
				stopTime = System.currentTimeMillis();
			} catch (TimeoutException toe) {
				stopTime = System.currentTimeMillis() + timeout;
			}

			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime);

			startTime = System.currentTimeMillis();

			Pair<Boolean, List<BDD>> b1 = SAFA.areReverseEquivalent(safa, SAFA.getEmptySAFA(bdds), bdds);

			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime);

			if (b != b1.first)
				throw new IllegalArgumentException("b and b1 should be the same");
		}
	}
	
	public static void RunSelfEquivLTL() {

		try {
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					try {
						RunSelfEquivLTLFile(filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void RunSelfEquivLTLFile(Path filePath) throws IOException {
		// parser p;
		// p.
		List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

		for (LTLNode ltl : nodes) {
			Pair<BDDSolver,LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
			BDDSolver bdds = pair.first;
			LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
			SAFA<BDD, BDD> safa = tot.getSAFA(bdds);

			long startTime = System.currentTimeMillis();

			boolean b = true;
			long stopTime = System.currentTimeMillis();

			try {
				b = SAFA.isEquivalent(safa, safa, bdds, SumOfProductsFactory.getInstance());
				stopTime = System.currentTimeMillis();
			} catch (TimeoutException toe) {
				stopTime = System.currentTimeMillis() + timeout;
			}

			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime);

			startTime = System.currentTimeMillis();

			Pair<Boolean, List<BDD>> b1 = SAFA.areReverseEquivalent(safa, safa, bdds);

			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime);

			if (b != b1.first)
				throw new IllegalArgumentException("b and b1 should be the same");
		}
	}

}

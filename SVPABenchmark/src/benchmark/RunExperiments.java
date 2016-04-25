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

public class RunExperiments {
	static long timeout = 1000;

	public static void main(String[] args) throws InterruptedException {

		RunLTLEmptiness();
		// RunSelfEquivLTL();
		//

	}

	public static void RunLTLEmptiness() {
		try {
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)){ //&& filePath.toString().endsWith("zp3.ltl")) {
					try {
						TestThread tt = new TestThread(filePath, true);
						Thread thread = new Thread(tt);
						System.out.println(filePath.toString());
						thread.start();
						long endTimeMillis = System.currentTimeMillis() + timeout;
						while (thread.isAlive()) {
							if (System.currentTimeMillis() > endTimeMillis) {
								System.out.println("TIMEOUT");
								tt.kill();
								break;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void RunEmptiness(Path filePath, TestThread tt) throws IOException {
		List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

		for (LTLNode ltl : nodes) {
			Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
			BDDSolver bdds = pair.first;
			LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
			SAFA<BDD, BDD> safa = tot.getSAFA(bdds);

			long startTime = System.currentTimeMillis();

			boolean b = true;
			long stopTime = System.currentTimeMillis();
			if (tt.isRunning) {
				System.out.println("Congruence");
				try {
					b = SAFA.isEmpty(safa, bdds);
					stopTime = System.currentTimeMillis();
				} catch (TimeoutException toe) {
					stopTime = System.currentTimeMillis() + timeout;
				}

				long elapsedTime = stopTime - startTime;
				if (tt.isRunning) {
					System.out.println(elapsedTime);

					startTime = System.currentTimeMillis();
					System.out.println("Reverse");
					Pair<Boolean, List<BDD>> b1 = SAFA.areReverseEquivalent(safa, SAFA.getEmptySAFA(bdds), bdds);

					stopTime = System.currentTimeMillis();
					elapsedTime = stopTime - startTime;
					if (tt.isRunning) {
						System.out.println(elapsedTime);

						if (b != b1.first)
							throw new IllegalArgumentException("b and b1 should be the same");
					}
				}
			}
		}
	}

	public static void RunSelfEquivLTL() {

		try {
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				TestThread tt = new TestThread(filePath, false);
				Thread thread = new Thread(tt);

				if (Files.isRegularFile(filePath)) {
					try {
						thread.start();
						long endTimeMillis = System.currentTimeMillis() + timeout;
						while (thread.isAlive()) {
							if (System.currentTimeMillis() > endTimeMillis) {
								System.out.println("TIMEOUT");
								tt.kill();
								break;
							}
						}
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

	public static void RunSelfEquivLTLFile(Path filePath, TestThread tt) throws IOException {
		// parser p;
		// p.
		List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

		for (LTLNode ltl : nodes) {
			Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
			BDDSolver bdds = pair.first;
			LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
			SAFA<BDD, BDD> safa = tot.getSAFA(bdds);
			if (tt.isRunning) {
				long startTime = System.currentTimeMillis();

				boolean b = true;
				long stopTime = System.currentTimeMillis();

				try {
					b = SAFA.isEquivalent(safa, safa, bdds, SumOfProductsFactory.getInstance()).getFirst();
					stopTime = System.currentTimeMillis();
				} catch (TimeoutException toe) {
					stopTime = System.currentTimeMillis() + timeout;
				}
				if (tt.isRunning) {
					long elapsedTime = stopTime - startTime;
					System.out.println(elapsedTime);

					startTime = System.currentTimeMillis();

					Pair<Boolean, List<BDD>> b1 = SAFA.areReverseEquivalent(safa, safa, bdds);
					if (tt.isRunning) {
						stopTime = System.currentTimeMillis();
						elapsedTime = stopTime - startTime;
						System.out.println(elapsedTime);

						if (b != b1.first)
							throw new IllegalArgumentException("b and b1 should be the same");
					}
				}
			}
		}
	}
}

class TestThread implements Runnable {

	public volatile boolean isRunning = true;
	public Path filePath;
	public boolean isEmptiness;

	public TestThread(Path filePath, boolean isEmptiness) {
		this.filePath = filePath;
		this.isEmptiness = isEmptiness;
	}

	@Override
	public void run() {
		try {
			if (isEmptiness)
				RunExperiments.RunEmptiness(filePath, this);
			else
				RunExperiments.RunSelfEquivLTLFile(filePath, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void kill() {
		isRunning = false;
	}
}

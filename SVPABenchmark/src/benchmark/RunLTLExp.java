package benchmark;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.sat4j.specs.TimeoutException;

import LTLparser.LTLNode;
import LTLparser.LTLParserProvider;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.booleanexpression.BDDExpression;
import automata.safa.booleanexpression.BDDExpressionFactory;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import benchmark.ltlconverter.LTLConverter;
import logic.ltl.LTLFormula;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import utilities.Pair;
import utilities.Timers;

public class RunLTLExp {
	static long timeout = 60000;

	static int fromCounter = 0;
	static String emptinessOutputFile = "results/emptiness";
	static String equivalenceOutputFile = "results/selfEquivalence";
	static String ranEquivalenceOutputFile = "results/ranEquivalence";
	static String containedString = "counter";
	static String notContainedString = "random";
	static boolean skipRev = true;
	static boolean useBDDs = true;

	public static void main(String[] args) throws InterruptedException {

		useBDDs = false;
		skipRev = false;
		RunLTLEmptiness();
		//RunLTLSelfEquiv();
		//RunLTLEquivChangeState();

		skipRev = true;
		useBDDs = true;
		//RunLTLEmptiness();
		//RunLTLSelfEquiv();
		//RunLTLEquivChangeState();
	}

	public static void RunLTLEmptiness() {
		try {
			FileWriter fw = new FileWriter(emptinessOutputFile + (useBDDs ? "BDD" : "") + ".csv");
			fw.append("formula, size, total, solver, subsumption, reverse\n");
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath) && filePath.toString().contains(containedString)
						&& !filePath.toString().contains(notContainedString)
						&& (filePath.toString().endsWith(".ltl")
								|| filePath.toString().endsWith(".form"))
						) {
					try {
						System.out.println(filePath);
						
						List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

						
						
						int counter = 0;
						for (LTLNode ltl : nodes) {
							fw.append(filePath.getFileName().toString());
							System.out.println(counter);
							if (counter > 0)
								fw.append(counter + "");
							fw.append(", ");

							if (counter >= fromCounter) {
								Timers.setTimeout(Long.MAX_VALUE);
								Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
								BDDSolver bdds = pair.first;
								LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
								SAFA<BDD, BDD> safa = tot.getSAFA(bdds);
								// safa = safa.getUnaryPathSAFA(bdds);

								fw.append(pair.second.getSize() + ", ");

								boolean result = true;
								boolean to1 = false;
								boolean to2 = false;

								try {
									if (useBDDs) {
										BooleanExpressionFactory<BDDExpression> bef = new BDDExpressionFactory(
												safa.stateCount() + 1);
										result = SAFA.isEquivalent(safa, SAFA.getEmptySAFA(bdds), bdds, bef, timeout)
												.getFirst();

									} else {
										result = SAFA.isEquivalent(safa, SAFA.getEmptySAFA(bdds), bdds,
												SAFA.getBooleanExpressionFactory(), timeout).getFirst();
									}
									fw.append(Timers.getFull() + ", " + Timers.getSolver() + ", "
											+ Timers.getSubsumption() + ", ");
									System.out.print(Timers.getFull() + ", " + Timers.getSolver() + ", "
											+ Timers.getSubsumption() + ", ");
								} catch (TimeoutException toe) {
									to1 = true;
									fw.append(timeout + ", " + timeout + ", " + timeout + ", ");
									System.out.print(timeout + ", " + timeout + ", " + timeout + ", ");
								}catch (NullPointerException np) {
									to1 = true;
									fw.append(timeout + ", " + timeout + ", " + timeout + ", ");
									System.out.print(timeout + ", " + timeout + ", " + timeout + ", ");
								}

								if (!skipRev) {
									long startTime2 = System.currentTimeMillis();
									try {
										boolean result2 = SAFA.areReverseEquivalent(safa, SAFA.getEmptySAFA(bdds), bdds,
												timeout).first;
										if (!to1 && result != result2)
											throw new IllegalArgumentException("bug");
										fw.append(System.currentTimeMillis() - startTime2 + ", ");
										System.out.print(System.currentTimeMillis() - startTime2 + ", ");
									} catch (TimeoutException toe) {
										fw.append(timeout + ", ");
										System.out.print(timeout + ", ");
										to2 = true;
									}catch (NullPointerException np) {
										fw.append(timeout + ", ");
										System.out.print(timeout + ", ");
										to2 = true;
									}

									if (!(to1 && to2)) {
										fw.append(result + "");
										System.out.print(result);
									} else {
										fw.append("TO");
										System.out.print("TO");
									}
								}

								fw.append("\n");
								System.out.println();
							}
							counter++;
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void RunLTLEquivChangeState() {
		try {
			Random r = new Random(200);

			FileWriter fw = new FileWriter(ranEquivalenceOutputFile + (useBDDs ? "BDD" : "") + ".csv");
			fw.append("formula, size, total, solver, subsumption, reverse\n");
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath) && filePath.toString().contains(containedString)
						&& !filePath.toString().contains(notContainedString)
						&& (filePath.toString().endsWith(".ltl")
								|| filePath.toString().endsWith(".form"))
						) {
					try {
						List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

						System.out.println(filePath);

						int counter = 0;
						for (LTLNode ltl : nodes) {
							fw.append(filePath.getFileName().toString());
							System.out.println(counter);
							if (counter > 0)
								fw.append(counter + "");
							fw.append(", ");

							if (counter >= fromCounter) {
								Timers.setTimeout(Long.MAX_VALUE);
								Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
								BDDSolver bdds = pair.first;
								LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);

								SAFA<BDD, BDD> safa1 = null;
								SAFA<BDD, BDD> safa2 = null;
								safa1 = tot.getSAFA(bdds);

								ArrayList<Integer> states = new ArrayList<>(safa1.getStates());

								BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA
										.getBooleanExpressionFactory();
								safa2 = SAFA.MkSAFA(safa1.getInputMoves(),
										boolexpr.MkAnd(safa1.getInitialState(),
												boolexpr.MkState(states.get(r.nextInt(states.size())))),
										safa1.getFinalStates(), bdds);

								fw.append(pair.second.getSize() + ", ");

								boolean result = true;
								boolean to1 = false;
								boolean to2 = false;

								try {
									if (useBDDs) {
										BooleanExpressionFactory<BDDExpression> bef = new BDDExpressionFactory(
												safa1.stateCount() + safa2.stateCount());
										result = SAFA.isEquivalent(safa1, safa2, bdds, bef, timeout).getFirst();

									} else {
										result = SAFA.isEquivalent(safa1, safa2, bdds,
												SAFA.getBooleanExpressionFactory(), timeout).getFirst();
									}
									fw.append(Timers.getFull() + ", " + Timers.getSolver() + ", "
											+ Timers.getSubsumption() + ", ");
									System.out.print(Timers.getFull() + ", " + Timers.getSolver() + ", "
											+ Timers.getSubsumption() + ", ");
								} catch (TimeoutException toe) {
									to1 = true;
									fw.append(timeout + ", " + timeout + ", " + timeout + ", ");
									System.out.print(timeout + ", " + timeout + ", " + timeout + ", ");
								} catch (NullPointerException np) {
									to1 = true;
									fw.append(timeout + ", " + timeout + ", " + timeout + ", ");
									System.out.print(timeout + ", " + timeout + ", " + timeout + ", ");
								}

								if (!skipRev) {
									long startTime2 = System.currentTimeMillis();
									boolean result2 = true;
									try {
										result2 = SAFA.areReverseEquivalent(safa1, safa2, bdds, timeout).first;
										fw.append(System.currentTimeMillis() - startTime2 + ", ");
										System.out.print(System.currentTimeMillis() - startTime2 + ", ");
										if (!to1 && result != result2)
											throw new IllegalArgumentException("bug");
									} catch (TimeoutException toe) {
										fw.append(timeout + ", ");
										System.out.print(timeout + ", ");
										to2 = true;
									} catch (NullPointerException np) {
										fw.append(timeout + ", ");
										System.out.print(timeout + ", ");
										to2 = true;
									}

								}

								if (!(to1 && to2)) {
									fw.append(result + "");
									System.out.print(result);
								} else {
									fw.append("TO");
									System.out.print("TO");
								}

								fw.append("\n");
								System.out.println();
							}
							counter++;
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void RunLTLSelfEquiv() {
		try {
			FileWriter fw = new FileWriter(equivalenceOutputFile + (useBDDs ? "BDD" : "") + ".csv");
			fw.append("formula, size, total, solver, subsumption\n");
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath) && filePath.toString().contains(containedString)
						&& !filePath.toString().contains(notContainedString)
						&& (filePath.toString().endsWith(".ltl")
								|| filePath.toString().endsWith(".form"))
						) {
					try {
						List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

						System.out.println(filePath);

						int counter = 0;
						for (LTLNode ltl : nodes) {
							fw.append(filePath.getFileName().toString());
							System.out.println(counter);
							if (counter > 0)
								fw.append(counter + "");
							fw.append(", ");

							if (counter >= fromCounter) {
								Timers.setTimeout(Long.MAX_VALUE);
								Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
								BDDSolver bdds = pair.first;
								LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
								SAFA<BDD, BDD> safa = null;
								safa = tot.getSAFA(bdds);

								fw.append(pair.second.getSize() + ", ");

								boolean result = true;
								boolean to1 = false;
								boolean to2 = false;

								try {
									if (useBDDs) {
										BooleanExpressionFactory<BDDExpression> bef = new BDDExpressionFactory(
												safa.stateCount() + safa.stateCount());
										result = SAFA.isEquivalent(safa, safa, bdds, bef, timeout).getFirst();

									} else {
										result = SAFA.isEquivalent(safa, safa, bdds, SAFA.getBooleanExpressionFactory(),
												timeout).getFirst();
									}

									fw.append(Timers.getFull() + ", " + Timers.getSolver() + ", "
											+ Timers.getSubsumption() + ", ");
									System.out.print(Timers.getFull() + ", " + Timers.getSolver() + ", "
											+ Timers.getSubsumption() + ", ");
								} catch (TimeoutException toe) {
									to1 = true;
									fw.append(timeout + ", " + timeout + ", " + timeout + ", ");
									System.out.print(timeout + ", " + timeout + ", " + timeout + ", ");
								}catch (NullPointerException np) {
									to1 = true;
									fw.append(timeout + ", " + timeout + ", " + timeout + ", ");
									System.out.print(timeout + ", " + timeout + ", " + timeout + ", ");
								}

								if (!skipRev) {
									long startTime2 = System.currentTimeMillis();
									try {
										result = SAFA.areReverseEquivalent(safa, safa, bdds, timeout).first;
										fw.append(System.currentTimeMillis() - startTime2 + ", ");
										System.out.print(System.currentTimeMillis() - startTime2 + ", ");
										if (!result)
											throw new IllegalArgumentException("bug");
									} catch (TimeoutException toe) {
										fw.append(timeout + ", ");
										System.out.print(timeout + ", ");
										to2 = true;
									} catch (NullPointerException np) {
										fw.append(timeout + ", ");
										System.out.print(timeout + ", ");
										to2 = true;
									}

									if (!(to1 && to2)) {
										fw.append(result + "");
										System.out.print(result);
									} else {
										fw.append("TO");
										System.out.print("TO");
									}
								}

								fw.append("\n");
								System.out.println();
							}
							counter++;
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
package benchmark.ltlconverter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Set;

import LTLparser.AlwaysNode;
import LTLparser.AndNode;
import LTLparser.EquivalenceNode;
import LTLparser.EventuallyNode;
import LTLparser.FalseNode;
import LTLparser.FormulaNode;
import LTLparser.IdNode;
import LTLparser.ImplicationNode;
import LTLparser.NegationNode;
import LTLparser.NextNode;
import LTLparser.OrNode;
import LTLparser.ReleaseNode;
import LTLparser.StrongReleaseNode;
import LTLparser.TrueNode;
import LTLparser.UntilNode;
import LTLparser.WeakUntilNode;
import LTLparser.XorNode;
import logic.ltl.And;
import logic.ltl.Eventually;
import logic.ltl.False;
import logic.ltl.Globally;
import logic.ltl.LTLFormula;
import logic.ltl.Next;
import logic.ltl.Not;
import logic.ltl.Or;
import logic.ltl.Predicate;
import logic.ltl.True;
import logic.ltl.Until;
import logic.ltl.WeakUntil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import utilities.Pair;

public class LTLConverter {

	public static Pair<BDDSolver, LTLFormula<BDD, BDD>> getLTLBDD(FormulaNode phi) {
		Set<String> atoms = phi.returnLeafNodes();
		HashMap<String, Integer> atomToInt = new HashMap<String, Integer>();
		for (String atom : atoms)
			atomToInt.put(atom, atomToInt.size());
		BDDSolver bdds = new BDDSolver(atomToInt.size());
		return new Pair<BDDSolver, LTLFormula<BDD, BDD>>(bdds,
				getLTLBDD(phi, atomToInt, bdds, new HashMap<String, LTLFormula<BDD, BDD>>()));
	}

	public static LTLFormula<BDD, BDD> getLTLBDD(FormulaNode phi, HashMap<String, Integer> atomToInt, BDDSolver bdds,
			HashMap<String, LTLFormula<BDD, BDD>> formulas) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		phi.unparse(pw, 0);
		String s = sw.toString();
		if (formulas.containsKey(s)) {
			return formulas.get(s);
		} else {

			LTLFormula<BDD, BDD> outputLTL = null;

			if (phi instanceof AlwaysNode) {
				AlwaysNode cphi = (AlwaysNode) phi;
				LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);

				outputLTL = new Globally<BDD, BDD>(subphi);
			} else {
				if (phi instanceof AndNode) {
					AndNode cphi = (AndNode) phi;
					LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);
					LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt, bdds, formulas);

					outputLTL = new And<BDD, BDD>(left, right);
				} else {
					if (phi instanceof EquivalenceNode) {
						EquivalenceNode cphi = (EquivalenceNode) phi;
						LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);
						LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt, bdds, formulas);

						outputLTL = new And<BDD, BDD>(new Or<BDD, BDD>(new Not<BDD, BDD>(left), right),
								new Or<BDD, BDD>(right, new Not<BDD, BDD>(left)));
					} else {
						if (phi instanceof EventuallyNode) {
							EventuallyNode cphi = (EventuallyNode) phi;
							LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);

							outputLTL = new Eventually<BDD, BDD>(subphi);
						} else {
							if (phi instanceof FalseNode) {
								return new False<BDD, BDD>();
							} else {
								if (phi instanceof IdNode) {
									IdNode cphi = (IdNode) phi;
									outputLTL = new Predicate<BDD, BDD>(
											bdds.factory.ithVar(atomToInt.get(cphi.getName())));
								} else {
									if (phi instanceof ImplicationNode) {
										ImplicationNode cphi = (ImplicationNode) phi;
										LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds,
												formulas);
										LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt, bdds,
												formulas);

										outputLTL = new Or<BDD, BDD>(new Not<>(left), right);
									} else {
										if (phi instanceof NegationNode) {
											NegationNode cphi = (NegationNode) phi;
											LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds,
													formulas);
											outputLTL = new Not<BDD, BDD>(subphi);
										} else {
											if (phi instanceof NextNode) {
												NextNode cphi = (NextNode) phi;
												LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt,
														bdds, formulas);
												outputLTL = new Next<BDD, BDD>(subphi);
											} else {
												if (phi instanceof OrNode) {
													OrNode cphi = (OrNode) phi;
													LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt,
															bdds, formulas);
													LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt,
															bdds, formulas);

													outputLTL = new Or<BDD, BDD>(left, right);
												} else {
													if (phi instanceof ReleaseNode) {
														ReleaseNode cphi = (ReleaseNode) phi;
														LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(),
																atomToInt, bdds, formulas);
														LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(),
																atomToInt, bdds, formulas);

														outputLTL = new Not<BDD, BDD>(
																new Until<>(new Not<>(left), new Next<>(right)));
													} else {
														if (phi instanceof StrongReleaseNode) {
															// StrongReleaseNode
															// cphi =
															// (StrongReleaseNode)
															// phi;
															throw new NotImplementedException();
														} else {
															if (phi instanceof TrueNode) {
																outputLTL = new True<BDD, BDD>();
															} else {
																if (phi instanceof UntilNode) {
																	UntilNode cphi = (UntilNode) phi;
																	LTLFormula<BDD, BDD> left = getLTLBDD(
																			cphi.getMyLTL1(), atomToInt, bdds,
																			formulas);
																	LTLFormula<BDD, BDD> right = getLTLBDD(
																			cphi.getMyLTL2(), atomToInt, bdds,
																			formulas);

																	outputLTL = new Until<BDD, BDD>(left, right);
																} else {
																	if (phi instanceof WeakUntilNode) {
																		WeakUntilNode cphi = (WeakUntilNode) phi;
																		LTLFormula<BDD, BDD> left = getLTLBDD(
																				cphi.getMyLTL1(), atomToInt, bdds,
																				formulas);
																		LTLFormula<BDD, BDD> right = getLTLBDD(
																				cphi.getMyLTL2(), atomToInt, bdds,
																				formulas);

																		outputLTL = new WeakUntil<BDD, BDD>(left,
																				right);
																	} else {
																		if (phi instanceof XorNode) {
																			XorNode cphi = (XorNode) phi;

																			LTLFormula<BDD, BDD> left = getLTLBDD(
																					cphi.getMyLTL1(), atomToInt, bdds,
																					formulas);
																			LTLFormula<BDD, BDD> right = getLTLBDD(
																					cphi.getMyLTL2(), atomToInt, bdds,
																					formulas);

																			outputLTL = new Or<BDD, BDD>(
																					new And<BDD, BDD>(
																							new Not<BDD, BDD>(left),
																							right),
																					new And<BDD, BDD>(right,
																							new Not<BDD, BDD>(left)));
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}

						}
					}
				}
			}
			formulas.put(s, outputLTL);
			return outputLTL;
		}
	}

}

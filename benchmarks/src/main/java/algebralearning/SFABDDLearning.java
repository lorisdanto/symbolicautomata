package algebralearning;



import org.sat4j.specs.TimeoutException;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import utilities.Pair;

import LTLparser.LTLNode;
import LTLparser.LTLParserProvider;
import algebralearning.bdd.BDDAlgebraLearnerFactory;
import algebralearning.sfa.SFAAlgebraLearner;
import algebralearning.sfa.SFAEquivalenceOracle;
import algebralearning.sfa.SFAMembershipOracle;
import automata.safa.SAFA;
import automata.sfa.SFA;
import ltlconverter.LTLConverter;
import logic.ltl.LTLFormula;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import java.util.List;

public class SFABDDLearning {


	public static void runLTLExperiments(String inputFilename, String outFilename) throws IOException, TimeoutException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename));
		List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(inputFilename));
		String outLine;

		for (LTLNode ltl : nodes) {
			Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
			BDDSolver bdds = pair.first;
			LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
			SAFA<BDD, BDD> safa = tot.getSAFA(bdds);
			SFA<BDD,BDD> model, target = SAFA.getReverseSFA(safa, bdds).determinize(bdds).minimize(bdds).mkTotal(bdds);

			BDDAlgebraLearnerFactory balf = new BDDAlgebraLearnerFactory(bdds.factory);
			SFAMembershipOracle<BDD, BDD> memb = new SFAMembershipOracle<>(target, bdds);
			SFAEquivalenceOracle<BDD, BDD> equiv = new SFAEquivalenceOracle<>(target, bdds);
			SFAAlgebraLearner<BDD, BDD> learner = new SFAAlgebraLearner<>(memb, bdds, balf);

			model = learner.getModelFinal(equiv);

			outLine = String.format("%d %d %d %d ", model.stateCount(), model.getTransitionCount(), memb.getDistinctQueries(), equiv.getDistinctCeNum());
			outLine += String.format("%d %d %d %d\n", equiv.getCachedCeNum(), learner.getNumCEGuardUpdates(), learner.getNumDetCE(), learner.getNumCompCE());
			writer.write(outLine);			
		}
	    	writer.close();
	    	return;
	}	
		
	/*
	 * basedir parameter should point at the directory containing the LTL files.
	 */
	public void runAllLTLExperiments(String basedir) throws TimeoutException, IOException {
		for (int i = 2; i <= 2; i += 2) {
			for (int j = 10; j <= 40; j += 10) {
				String outFilename = String.format(basedir + "/P0.5N%dL%d.ltl", i, j);
				String filename=String.format(basedir + "/P0.5N%dL%d.ltl", i, j);
				System.out.format("Now processing: %s\n", filename);
				runLTLExperiments(filename, outFilename);				
			}
		}
		return;
	}
}

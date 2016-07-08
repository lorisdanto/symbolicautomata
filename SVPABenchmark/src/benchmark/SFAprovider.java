package benchmark;

import java.util.List;

import org.sat4j.specs.TimeoutException;

import RegexParser.RegexNode;
import RegexParser.RegexParserProvider;
import automata.sfa.SFA;
import benchmark.regexconverter.RegexConverter;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class SFAprovider {
	public SFAprovider(String regex, UnaryCharIntervalSolver solver){
		String[] str = {regex};
		List<RegexNode> nodes = RegexParserProvider.parse(str);
		
		try {
			this.mySFA =RegexConverter.toSFA(nodes.get(0), solver);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SFA<CharPred, Character> getSFA(){
		return mySFA;
	}
	
	
	private SFA<CharPred, Character> mySFA;
	
}

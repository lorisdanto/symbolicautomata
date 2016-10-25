
/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory.characters;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

/**
 * CharPred: a set of characters represented as contiguous intervals
 */
public class BinaryCharPred extends ICharPred {
	
	public ArrayList<Pair<CharPred,CharPred>> notEqual;
	public CharPred equals;	
	
	private BinaryCharPred(){
		setAsReturn();
	}
	
	/**
	 * Return language is p, and it forces equality with call if forceEquality=true
	 */
	public BinaryCharPred(CharPred p, boolean forceEquality) {
		this();
		checkArgument(p != null);
		notEqual = new ArrayList<Pair<CharPred,CharPred>>();
		equals = p; 
		if(!forceEquality){
			notEqual.add(new Pair<CharPred, CharPred>(StdCharPred.TRUE, p));
		}
		
	}
	
	public void normalize(BooleanAlgebra<CharPred, Character> ba) throws TimeoutException{
		ArrayList<Pair<CharPred,CharPred>> newNotEqual = new ArrayList<Pair<CharPred,CharPred>>();
		
		ArrayList<CharPred> firstProj = new ArrayList<>();
		for(Pair<CharPred,CharPred> pair: notEqual)
			firstProj.add(pair.first);
		
		Collection<Pair<CharPred,ArrayList<Integer>>> minterms = ba.GetMinterms(firstProj);		
		for(Pair<CharPred,ArrayList<Integer>> minterm:minterms){
			CharPred currA = minterm.first;
			CharPred currB = ba.False();
			for (int bit = 0; bit < notEqual.size(); bit++) 					
				if (minterm.second.get(bit) == 1)
					currB = ba.MkOr(currB, notEqual.get(bit).second);
				
			newNotEqual.add(new Pair<>(currA, currB));
		}
		
		notEqual = newNotEqual;
	}
	
	public BinaryCharPred(CharPred eq, ArrayList<Pair<CharPred,CharPred>> notEqual) {
		this();
		checkArgument(eq != null && notEqual!=null);
		this.equals = eq;
		this.notEqual = notEqual;	
	}
	
	/**
	 * c and r without caring about equality
	 * @throws TimeoutException 
	 */
	public BinaryCharPred(CharPred c, CharPred r, BooleanAlgebra<CharPred, Character> ba) throws TimeoutException {
		this();
		checkArgument(c != null && r!=null);
		notEqual = new ArrayList<Pair<CharPred,CharPred>>();
		equals = StdCharPred.FALSE;
		equals = ba.MkAnd(c,r); 
		notEqual.add(new Pair<CharPred, CharPred>(c,r));
	}

	public boolean isSatisfiedBy(char c1, char c2) {
		if(c1 == c2)
			return equals.isSatisfiedBy(c1);
		else
			for(Pair<CharPred,CharPred> pair: notEqual)
				if(pair.first.isSatisfiedBy(c1) && pair.second.isSatisfiedBy(c2))
					return true;
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("neq ");
		sb.append(notEqual);
		sb.append(", eq ");
		sb.append(equals);
		return sb.toString();
	}

	// Only prints readable chars, otherwise print unicode
	public static String printChar(char c) {
		Map<Character, String> unescapeMap = new HashMap<Character, String>();
		unescapeMap.put('-', "\\-");
		unescapeMap.put('(', "\\(");
		unescapeMap.put(')', "\\)");
		unescapeMap.put('[', "\\[");
		unescapeMap.put(']', "\\]");
		unescapeMap.put('\t', "\\t");
		unescapeMap.put('\b', "\\b");
		unescapeMap.put('\n', "\\n");
		unescapeMap.put('\r', "\\r");
		unescapeMap.put('\f', "\\f");
		unescapeMap.put('\'', "\\\'");
		unescapeMap.put('\"', "\\\"");
		unescapeMap.put('\\', "\\\\");
		if (unescapeMap.containsKey(c)) {
			return unescapeMap.get(c);
		} else if (c < 0x20 || c > 0x7f) {
			return String.format("\\u%04x", (int) c);
		} else {
			return Character.toString(c);
		}
	}

}

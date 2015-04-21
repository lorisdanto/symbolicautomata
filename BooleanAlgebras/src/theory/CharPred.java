/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

import java.util.ArrayList;
import utilities.Pair;

/**
 * CharPred: a set of characters represented as contiguous intervals
 */
public class CharPred {
    public ArrayList<Pair<Character,Character>> intervals;
    
    /**
     * The empty set
     */
    public CharPred() { 
        intervals = new ArrayList<Pair<Character, Character>>();
    }
    
    /**
     * The set containing only the character <code>c</code>
     */
    public CharPred(Character c) { 
        intervals = new ArrayList<Pair<Character, Character>>();
        intervals.add(new Pair<Character, Character>(c,c));
    }
    
    /**
     * The set containing only the interval <code>[bot,top]</code> (extremes included)
     */
    public CharPred(Character bot,Character top) { 
        intervals = new ArrayList<Pair<Character, Character>>();
        if(bot<=top){
            intervals.add(new Pair<Character, Character>(bot,top));
        }
    }
    
    /**
     * The set containing all intervals (the intervals must arrive in order and must not overlap)
     */
    public CharPred(ArrayList<Pair<Character,Character>> intervals) { 
        //TODO need to normalize
        this.intervals= intervals;
    }

    /**
     * @return the set [A-Z]
     */
    public final static CharPred upperAlpha(){
    	return new CharPred('A','Z');
    }
    
    /**
     * @return the set [a-z]
     */
    public final static CharPred lowerAlpha(){
    	return new CharPred('a','z');
    }
    
    /**
     * @return the set [A-Za-z]
     */
    public final static CharPred alpha(){
    	ArrayList<Pair<Character,Character>> intervals = new ArrayList<Pair<Character,Character>>();
    	intervals.add(new Pair<Character, Character>('A', 'Z'));
    	intervals.add(new Pair<Character, Character>('a', 'z'));
    	return new CharPred(intervals);
    }
    
    /**
     * @return the set [0-9]
     */
    public final static CharPred num(){
    	return new CharPred('0','9');
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Pair<Character,Character> pair:intervals){
            if(pair.first== pair.second)
                sb.append(printChar(pair.first));
            else{
                sb.append(printChar(pair.first));
                sb.append("-");
                sb.append(printChar(pair.second));
            }
        }
        sb.append("]");
                
        return sb.toString();
    }  
    
	// Only prints readable chars, otherwise print unicode
    private static String printChar(Character c){    
    	if(Character.isSpaceChar(c) || c<33 || c>126)
    		return "\\u" + Integer.toHexString(c | 0x10000).substring(1);
    	return c.toString();
    }

}


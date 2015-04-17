package theory;

import java.util.ArrayList;
import utilities.Pair;

public class CharPred {
    public ArrayList<Pair<Character,Character>> intervals;
    
    public CharPred() { 
        intervals = new ArrayList<Pair<Character, Character>>();
    }
    
    public CharPred(Character c) { 
        intervals = new ArrayList<Pair<Character, Character>>();
        intervals.add(new Pair<Character, Character>(c,c));
    }
      
    public CharPred(Character bot,Character top) { 
        intervals = new ArrayList<Pair<Character, Character>>();
        if(bot<=top){
            intervals.add(new Pair<Character, Character>(bot,top));
        }
    }
    
    public CharPred(ArrayList<Pair<Character,Character>> intervals) { 
        //TODO need to normalize
        this.intervals= intervals;
    }

    public final static CharPred upperAlpha(){
    	return new CharPred('A','Z');
    }
    
    public final static CharPred lowerAlpha(){
    	return new CharPred('a','z');
    }
    
    public final static CharPred alpha(){
    	ArrayList<Pair<Character,Character>> intervals = new ArrayList<Pair<Character,Character>>();
    	intervals.add(new Pair<Character, Character>('A', 'Z'));
    	intervals.add(new Pair<Character, Character>('a', 'z'));
    	return new CharPred(intervals);
    }
    
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
    
    private static String printChar(Character c){
    	// Only print readable chars, otherwise print unicode
    	if(Character.isSpaceChar(c) || c<33 || c>126)
    		return "\\u" + Integer.toHexString(c | 0x10000).substring(1);
    	return c.toString();
    }

}


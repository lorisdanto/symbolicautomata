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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Pair<Character,Character> pair:intervals){
            if(pair.first== pair.second)
                sb.append(pair.first);
            else{
                sb.append(pair.first);
                sb.append("-");
                sb.append(pair.second);
            }
        }
        sb.append("]");
                
        return sb.toString();
    }        

}


/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */
package theory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utilities.Pair;

/**
 * CharSolver: an interval based solver for the theory of characters
 */
public class CharSolver extends BooleanAlgebraSubst<CharPred, CharFunc, Character>{

    final Character minChar = Character.MIN_VALUE;
    final Character maxChar = Character.MAX_VALUE;
    
    
    @Override
    public CharPred MkNot(CharPred u) {
        ArrayList<Pair<Character,Character>> intervals = u.intervals;
        ArrayList<Pair<Character,Character>> newIntervals = new ArrayList<Pair<Character, Character>>();
        
        if(intervals.isEmpty())
            return True();
        
        Pair<Character,Character> curr = intervals.get(0);
        Character curBot = curr.first;
        Character prevTop = curr.second;
        if(minChar<curr.first)
            newIntervals.add(new Pair<Character, Character>((char)0,(char)(curBot-1)));
        for(int i=1;i<intervals.size();i++){
            curr = intervals.get(i);
            curBot = curr.first;            
            newIntervals.add(new Pair<Character, Character>((char)(prevTop+1),(char)(curBot-1)));            
            prevTop = curr.second;
        }
        if(prevTop<maxChar)
            newIntervals.add(new Pair<Character, Character>((char)(prevTop+1),(char)(maxChar)));
        
        return new CharPred(newIntervals);
    }

    @Override
    public CharPred MkOr(Collection<CharPred> clctn) {
        CharPred or = False();
       for(CharPred a:clctn)
           or = MkAnd(or,a);
       return or;
    }

    @Override
    public CharPred MkOr(CharPred u1, CharPred u2) {
        return MkNot(MkAnd(MkNot(u1), MkNot(u2)));
    }

    @Override
    public CharPred MkAnd(Collection<CharPred> clctn) {
       CharPred and = True();
       for(CharPred a:clctn)
           and = MkAnd(and,a);
       return and;
    }

    @Override
    public CharPred MkAnd(CharPred u1, CharPred u2) {
        ArrayList<Pair<Character,Character>> intervals1 = u1.intervals;
        ArrayList<Pair<Character,Character>> intervals2 = u2.intervals;
        ArrayList<Pair<Character,Character>> newIntervals = new ArrayList<Pair<Character, Character>>();
        
        if(intervals1.isEmpty() || intervals2.isEmpty())
            return False();        
        
        int lastInd2 = 0;        
        for (Pair<Character, Character> cur1 : intervals1) {
            Character bot1 = cur1.first;
            Character top1 = cur1.second;
            for(int ind2 =lastInd2;ind2<intervals2.size();ind2++){
                Pair<Character,Character> cur2 = intervals2.get(ind2);
                Character bot2 = cur2.first;
                Character top2 = cur2.second;
                //Interval is after curr one
                if(bot2>top1){
                    lastInd2 = ind2;
                    break;
                }
                if(top2>=bot1){
                    int newBot = Math.max(bot1, bot2);
                    int newTop = Math.min(top1, top2);
                    newIntervals.add(new Pair<Character, Character>((char)newBot,(char)newTop));     
                    if(top2> top1){
                        lastInd2 =ind2;
                        break;
                    }
                }
            }
        }
        
        return new CharPred(newIntervals);
    }

    @Override
    public CharPred True() {
        return new CharPred(minChar, maxChar);
    }

    @Override
    public CharPred False() {
        return new CharPred();
    }

    @Override
    public boolean AreEquivalent(CharPred u1, CharPred u2) {
        CharPred u1minusu2 = MkAnd(MkNot(u2),u1);
        if(IsSatisfiable(u1minusu2))
            return false;
        CharPred u2minusu1 = MkAnd(MkNot(u1),u2);
        return !IsSatisfiable(u2minusu1);
    }

    @Override
    public boolean IsSatisfiable(CharPred u) {
        return !u.intervals.isEmpty();
    }

    @Override
    public boolean HasModel(CharPred u, Character s) {
        ArrayList<Pair<Character,Character>> intervals = u.intervals;
        for(Pair<Character,Character> interval:intervals)
        	if(interval.first<=s && s<=interval.second)
        		return true;
        return false;
    }

    @Override
    public boolean HasModel(CharPred u, Character s, Character s1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Character generateWitness(CharPred u) {
        ArrayList<Pair<Character,Character>> intervals = u.intervals;
        if(intervals.isEmpty())
            return null;
        return intervals.get(0).first;                
    }

    @Override
    public Pair<Character, Character> generateWitnesses(CharPred u) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

	@Override
	public CharFunc MkSubstFuncFunc(CharFunc f1, CharFunc f2) {
		return f2.SubstIn(f1);		
	}

	@Override
	public CharPred MkSubstFuncPred(CharFunc f, CharPred p) {
		return f.SubstIn(p, this);
	}

	@Override
	public Character MkSubstFuncConst(CharFunc f, Character c) {
		return f.InstantiateWith(c);
	}
    
	/**
	 * returns the string of a list of chars
	 * @param chars
	 * @return
	 */
	public String stringOfList(List<Character> chars){
		StringBuilder sb = new StringBuilder();
		for(Character c: chars)
			sb.append(c);
		return sb.toString();
	}
}

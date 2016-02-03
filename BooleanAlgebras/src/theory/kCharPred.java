package theory;
import com.google.common.collect.ImmutableList;

import automata.esfa.ESFAInputMove;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class kCharPred {
	public Map<Integer, CharPred> kprelist;
	public Integer k;
	public static final char MIN_CHAR = Character.MIN_VALUE;
    public static final char MAX_CHAR = Character.MAX_VALUE;
    
    public kCharPred() {
    	this.k = 0;
    	kprelist = new HashMap<Integer, CharPred>();
	}
    public void setk(Integer lookahead){
    	this.k = lookahead;
    }
    public void addPre(CharPred toadd, Integer var){
    	kprelist.put(var, toadd);
    }
    
    public boolean isSatisfiedBy(List<Character> c) {
    	for(Map.Entry<Integer, CharPred> entry : kprelist.entrySet()){
    		if(entry.getValue().isSatisfiedBy(c.get(entry.getKey())))
    			return false;
    	}
		return true;
	}
    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
    	for(Map.Entry<Integer, CharPred> entry : kprelist.entrySet()){
    		sb.append("("+entry.getKey()+","+entry.getValue().toString()+").");
    	}

		return sb.toString();
	}
}

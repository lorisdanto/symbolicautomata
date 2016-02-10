package automata.safa;

import java.util.Collection;
import java.util.Set;

public abstract class BooleanExpr {	
	
	public abstract Set<Integer> getStates();
	
	@Override
	public abstract Object clone();
	
	public abstract boolean hasModel(Collection<Integer> elements);

	public abstract BooleanExpr unionWith(BooleanExpr p1);

	public abstract BooleanExpr interesectWith(BooleanExpr p1);
	
	public abstract BooleanExpr offset(int offset);
}

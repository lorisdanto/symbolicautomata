package automata.safa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SumOfProducts<S> implements BooleanExpr<S>{

	public List<List<S>> dnf;

	public SumOfProducts(List<List<S>> dnf) {
		super();
		this.dnf = dnf;
	}
	
	public Set<S> getStates(){
		HashSet<S> acc = new HashSet<S>();
		dnf.stream().forEach(acc::addAll);
		return acc;
	}
	
	//TODO equals clone...
}

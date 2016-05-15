package automata.safa;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BooleanExpressionMorphism<R> implements LatticeMorphism<BooleanExpression, R> {
	BooleanExpressionFactory<R> boolexpr;
	Function<Integer, R> sigma;
	Map<BooleanExpression, R> cache; 

	public BooleanExpressionMorphism(Function<Integer, R> sigma,
			BooleanExpressionFactory<R> boolexpr) {
		this.boolexpr = boolexpr;
		this.sigma = sigma;
		this.cache = new HashMap<>();
	}

	public R MkAnd(R left, R right) {
		return boolexpr.MkAnd(left, right);
	}

	public R MkOr(R left, R right) {
		return boolexpr.MkOr(left,  right);
	}
	
	public R True() {
		return boolexpr.True();
	}

	public R False() {
		return boolexpr.False();
	}

	public R apply(int state) {
		return sigma.apply(state);
	}
	
	public R apply(BooleanExpression phi) {
		if (cache.containsKey(phi)) {
			return cache.get(phi);
		} else {
			R result = phi.apply(this);
			cache.put(phi, result);
			return result;
		}
	}
	
	public void clear() {
		cache.clear();
	}
}

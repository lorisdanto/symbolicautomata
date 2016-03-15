package utilities;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Memo<T,R> implements Function<T,R> {
	private Function<T,R> fn;

	private HashMap<T,R> cache;

	public Memo(Function<T,R> f) {
		fn = f;
		cache = new HashMap<>();
	}
	
	// Memoize a recursive function
	public Memo(BiFunction<Function<T,R>,T,R> f) {
		Function<T, R> app = (y) -> this.apply(y);
		fn = (x) -> f.apply(app, x);
	}

	public R apply(T x) {
		if (cache.containsKey(x)) {
			return cache.get(x);
		} else {
			R result = fn.apply(x);
			cache.put(x, result);
			return result;
		}
	}

	public void clear() {
		cache.clear();
	}
}

package automata.safa;

public interface LatticeMorphism<T, R> {
	public R MkAnd(R left, R right);
	public R MkOr(R left, R right);
	public R True();
	public R False();
	public R apply(int state);
	public R apply(T phi);
}

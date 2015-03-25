package theory;

public class CharFunc {

	public int increment;

	public static CharFunc ID() {
		return new CharFunc(0);
	}
	
	public static CharFunc ToLowerCase() {
		return new CharFunc(32);
	}
	
	public static CharFunc ToUpperCase() {
		return new CharFunc(-32);
	}
	
	public CharFunc(int increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("c+");
		sb.append(increment);

		return sb.toString();
	}

}

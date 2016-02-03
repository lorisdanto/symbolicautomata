package utilities;

public class BitVecUtil {

	//Get the N-th bit of an integer starting with 0 and from left
	public static int get_nth_bit(int N,int position) {
		return (N >> position) & 1;
	}
}

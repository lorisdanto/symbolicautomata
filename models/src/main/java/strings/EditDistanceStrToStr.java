package strings;

public class EditDistanceStrToStr {
	/**
	 * Compute the edit distance between two strings. 
	 *
	 * @param A the first string
	 * @param B the second string
	 * @return the edit distance between A and B
	 */
	public static int getEditDistance(String A, String B) {
		if (A.equals(B)) {
			return 0;
		}
		int[][] dp = new int[A.length() + 1][B.length() + 1];
		for (int i = 1; i <= A.length(); i++)
			dp[i][0] = i;
		for (int j = 1; j <= B.length(); j++)
			dp[0][j] = j;
		for (int i = 1; i <= A.length(); i++) {
			for (int j = 1; j <= B.length(); j++) {
				if (A.charAt(i - 1) == B.charAt(j - 1))
					dp[i][j] = dp[i - 1][j - 1];
				else {
					dp[i][j] = Math.min(dp[i - 1][j] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + 1));
				}
			}
		}
		return dp[A.length()][B.length()];
	}
}

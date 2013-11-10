package parser;

public class Utility {

	// Source: https://github.com/threedaymonk/text/blob/master/lib/text/levenshtein.rb
	public static int levenshteinDistance(String s, String t) {
		int n = s.length();
		int m = t.length();

		if (n == 0) return m;
		if (m == 0) return n;

		int[] d = arrayOfNumbers(0, m);
		int x = 0;

		for (int i=0; i<n; i++) {
			int e = i + 1;
			for (int j=0; j<m; j++) {
				int cost = s.charAt(i) == t.charAt(j) ? 0 : 1;
				x = Math.min(
						d[j+1] + 1, Math.min(// insertion
						e + 1, // deletion
						d[j] + cost // substitution
				));
				d[j] = e;
				e = x;
			}
			d[m] = x;
		}
		return x;
	}
	
	private static int[] arrayOfNumbers(int lower, int upper) {
		int[] result = new int[upper-lower+1];
		for (int i=lower; i<=upper; i++) {
			result[i-lower] = i;
		}
		return result;
	}

	// Source: http://rosettacode.org/wiki/Longest_common_subsequence
	public static String longestCommonSubsequence(String a, String b) {
		int[][] lengths = new int[a.length()+1][b.length()+1];

		// row 0 and column 0 are initialized to 0 already

		for (int i = 0; i < a.length(); i++)
			for (int j = 0; j < b.length(); j++)
				if (a.charAt(i) == b.charAt(j)) {
					lengths[i+1][j+1] = lengths[i][j] + 1;
				}
				else {
					lengths[i+1][j+1] = Math.max(lengths[i+1][j], lengths[i][j+1]);
				}

		// read the substring out from the matrix
		StringBuffer sb = new StringBuffer();
		for (int x = a.length(), y = b.length();
				x != 0 && y != 0; ) {
			if (lengths[x][y] == lengths[x-1][y]) {
				x--;
			}
			else if (lengths[x][y] == lengths[x][y-1]) {
				y--;
			}
			else {
				assert a.charAt(x-1) == b.charAt(y-1);
				sb.append(a.charAt(x-1));
				x--;
				y--;
			}
		}

		return sb.reverse().toString();
	}
}

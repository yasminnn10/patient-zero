public class Strains {

    public static int editDistance(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] d = new int[n + 1][m + 1];

         for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    int replaceCost = d[i - 1][j - 1] + 1;
                    int deleteCost  = d[i - 1][j]     + 1;
                    int insertCost  = d[i][j - 1]     + 1;
                    d[i][j] = Math.min(replaceCost, Math.min(deleteCost, insertCost));
                }
            }
        }

        return d[n][m];
    }

    public static int lcsLength(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] d = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1] + 1;
                } else {
                    d[i][j] = Math.max(d[i - 1][j], d[i][j - 1]);
                }
            }
        }

        return d[n][m];
    }
}

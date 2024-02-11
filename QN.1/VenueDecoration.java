// 1.a
public class VenueDecoration {
    public static int minCost(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0)
            return 0;

        int n = costs.length;
        int k = costs[0].length;

        // dp[i][j] represents the minimum cost to decorate venues up to the i-th venue
        // with the j-th theme, while adhering to the adjacency constraint
        int[][] dp = new int[n][k];

        // Initialize dp array with the costs of the first venue
        for (int j = 0; j < k; j++) {
            dp[0][j] = costs[0][j];
        }

        // Iterate through venues starting from the second one
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < k; j++) {
                // Initialize the minimum cost for the current venue and theme to be maximum
                // possible
                int minCost = Integer.MAX_VALUE;

                // Check the minimum cost of decorating the current venue with the current theme
                // considering the cost of previous venues with different themes
                for (int l = 0; l < k; l++) {
                    if (l != j) { // Adjacency constraint
                        minCost = Math.min(minCost, dp[i - 1][l] + costs[i][j]);
                    }
                }

                dp[i][j] = minCost;
            }
        }

        // Find the minimum cost among all possible themes for the last venue
        int minTotalCost = Integer.MAX_VALUE;
        for (int j = 0; j < k; j++) {
            minTotalCost = Math.min(minTotalCost, dp[n - 1][j]);
        }

        return minTotalCost;
    }

    public static void main(String[] args) {
        int[][] costs = { { 1, 3, 2 }, { 4, 6, 8 }, { 3, 1, 5 } };
        System.out.println(minCost(costs));
    }
}

// Output of this:
// 7
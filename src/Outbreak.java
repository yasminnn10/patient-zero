import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    private static class DpResult {
        long costTested;     // Стоимость поддерева, если корень ТЕСТИРУЕТСЯ
        long costNotTested;  // Стоимость поддерева, если корень НЕ ТЕСТИРУЕТСЯ

        DpResult(long costTested, long costNotTested) {
            this.costTested = costTested;
            this.costNotTested = costNotTested;
        }
    }

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        // Строим ориентированное дерево из неориентированных соседей, начиная с корня 0
        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            children[i] = new ArrayList<>();
        }
        boolean[] visited = new boolean[n];
        buildTree(0, -1, nb, children, visited);

        long[] dpTested = new long[n];
        long[] dpNotTested = new long[n];

        // 1. Пост-ордер обход (снизу вверх) для вычисления минимальных стоимостей
        computeDp(0, children, cost, dpTested, dpNotTested);

        // 2. Восстановление ответа (сверху вниз)
        boolean[] tested = new boolean[n];
        // Решаем для корня (0): выбираем вариант с минимальной стоимостью
        boolean rootTested = dpTested[0] <= dpNotTested[0];
        rebuild(0, rootTested, children, dpTested, dpNotTested, tested);

        return tested;
    }

    private static void buildTree(int u, int parent, int[][] nb, List<Integer>[] children, boolean[] visited) {
        visited[u] = true;
        for (int v : nb[u]) {
            if (v != parent && !visited[v]) {
                children[u].add(v);
                buildTree(v, u, nb, children, visited);
            }
        }
    }

    private static void computeDp(int u, List<Integer>[] children, int[] cost, long[] dpTested, long[] dpNotTested) {
        dpTested[u] = cost[u];
        dpNotTested[u] = 0;

        for (int v : children[u]) {
            computeDp(v, children, cost, dpTested, dpNotTested);
            // Если u ТЕСТИРУЕТСЯ, то для каждого ребенка v нам выгоднее взять минимум между (v тестируется) и (v не тестируется)
            dpTested[u] += Math.min(dpTested[v], dpNotTested[v]);
            // Если u НЕ ТЕСТИРУЕТСЯ, то каждый ребенок v ОБЯЗАН тестироваться (иначе ребро (u,v) не будет покрыто)
            dpNotTested[u] += dpTested[v];
        }
    }

    private static void rebuild(int u, boolean isTested, List<Integer>[] children, long[] dpTested, long[] dpNotTested, boolean[] tested) {
        tested[u] = isTested;
        for (int v : children[u]) {
            if (isTested) {
                // Если u протестирован, ребенок v может быть протестирован или нет — выбираем то, что дешевле в поддереве v
                boolean childTested = dpTested[v] <= dpNotTested[v];
                rebuild(v, childTested, children, dpTested, dpNotTested, tested);
            } else {
                // Если u НЕ протестирован, ребенок v ОБЯЗАН быть протестирован
                rebuild(v, true, children, dpTested, dpNotTested, tested);
            }
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) return 0;

        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            children[i] = new ArrayList<>();
        }
        boolean[] visited = new boolean[n];
        buildTree(0, -1, nb, children, visited);

        int[] maxLinkResult = new int[1]; // хранит глобальный максимум длины пути (в ребрах)
        dfsHeight(0, children, maxLinkResult);

        return maxLinkResult[0];
    }

    private static int dfsHeight(int u, List<Integer>[] children, int[] maxLinkResult) {
        if (children[u].isEmpty()) {
            return 0;
        }

        int max1 = 0; // длина самого длинного пути вниз среди детей
        int max2 = 0; // длина второго по длине пути вниз среди детей

        for (int v : children[u]) {
            int h = dfsHeight(v, children, maxLinkResult) + 1; // +1 ребро до ребенка v
            if (h > max1) {
                max2 = max1;
                max1 = h;
            } else if (h > max2) {
                max2 = h;
            }
        }

        // Самый длинный путь через текущую вершину u равен сумме двух наибольших путей вниз в ее поддеревьях
        int currentPathThroughU = max1 + max2;
        if (currentPathThroughU > maxLinkResult[0]) {
            maxLinkResult[0] = currentPathThroughU;
        }

        return max1; // Возвращаем наибольшую высоту вниз для родительского узла
    }
}

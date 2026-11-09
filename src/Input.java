import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Input {

    public record Strains(String a, String b) {}

    public record Markers(String marker, String[] panel) {}

    public record Tree(int n, int[] a, int[] b, int[] cost) {
        public int links() { return a.length; }

        public int[][] neighbours() {
            int[] deg = new int[n];
            for (int i = 0; i < a.length; i++) { deg[a[i]]++; deg[b[i]]++; }
            int[][] nb = new int[n][];
            for (int v = 0; v < n; v++) nb[v] = new int[deg[v]];
            int[] fill = new int[n];
            for (int i = 0; i < a.length; i++) { nb[a[i]][fill[a[i]]++] = b[i]; nb[b[i]][fill[b[i]]++] = a[i]; }
            return nb;
        }
    }

    static List<String> lines(String path) throws IOException {
        String t = new String(Files.readAllBytes(Path.of(path)), java.nio.charset.StandardCharsets.UTF_8);
        if (t.startsWith("\uFEFF")) t = t.substring(1);
        List<String> l = new ArrayList<>(Arrays.asList(t.split("\\r?\\n", -1)));
        while (!l.isEmpty() && l.get(l.size() - 1).isBlank()) l.remove(l.size() - 1);
        return l;
    }

    public static Strains readStrains(String path) throws IOException {
        List<String> l = lines(path);
        return new Strains(l.get(0).split("\\s+")[1].trim(), l.get(1).split("\\s+")[1].trim());
    }

    public static String readGenome(String path) throws IOException {
        return lines(path).get(0).trim();
    }

    public static Markers readMarkers(String path) throws IOException {
        List<String> l = lines(path);
        String marker = null; List<String> panel = new ArrayList<>();
        for (String line : l) {
            if (line.isBlank()) continue;
            String[] p = line.trim().split("\\s+");
            if (p[0].equals("marker")) marker = p[1]; else panel.add(p[1]);
        }
        return new Markers(marker, panel.toArray(new String[0]));
    }

    public static Tree readTree(String path) throws IOException {
        List<String> l = lines(path);
        int n = Integer.parseInt(l.get(0).trim());
        int[] a = new int[n - 1], b = new int[n - 1], cost = new int[n];
        for (int i = 0; i < n - 1; i++) { String[] p = l.get(1 + i).trim().split("\\s+"); a[i] = Integer.parseInt(p[0]); b[i] = Integer.parseInt(p[1]); }
        String[] c = l.get(n).trim().split("\\s+");
        for (int i = 0; i < n; i++) cost[i] = Integer.parseInt(c[i]);
        return new Tree(n, a, b, cost);
    }
}

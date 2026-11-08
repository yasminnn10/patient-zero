import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int j = lengthOfCurrentBorderOrLenSoFar(0); // длина текущего бордера

        for (int i = 1; i < m; i++) {
            while (j > 0 && p.charAt(i) != p.charAt(j)) {
                j = pi[j - 1];
            }
            if (p.charAt(i) == p.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }

    private static int lengthOfCurrentBorderOrLenSoFar(int val) {
        return val;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        int n = text.length();
        int m = p.length();
        if (m == 0 || n < m) {
            return new int[0];
        }

        int[] pi = prefixFunction(p);
        List<Integer> matches = new ArrayList<>();
        int j = 0; // количество совпавших символов префикса

        for (int i = 0; i < n; i++) {
            while (j > 0 && text.charAt(i) != p.charAt(j)) {
                j = pi[j - 1];
            }
            if (text.charAt(i) == p.charAt(j)) {
                j++;
            }
            if (j == m) {
                matches.add(i - m + 1);
                // Важно: после полного совпадения откатываем j по префикс-функции,
                // чтобы не пропустить перекрывающиеся вхождения (например, "AAA" и "AA")
                j = pi[j - 1];
            }
        }

        // Превращаем список в примитивный массив int[]
        int[] result = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            result[i] = matches.get(i);
        }
        return result;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || a.length() < k || b.length() < k) {
            return 0;
        }

        long base = 31;
        long mod = 1_000_000_009L;

        // Вычисляем base^(k-1) % mod для быстрого удаления старшего символа катящегося хеша
        long highestPower = 1;
        for (int i = 0; i < k - 1; i++) {
            highestPower = (highestPower * base) % mod;
        }

        // Собираем хеши всех окон строки b и группируем их по значению хеша (для обработки возможных коллизий)
        Map<Long, List<String>> bHashes = new HashMap<>();

        long currentHashB = 0;
        for (int i = 0; i < k; i++) {
            currentHashB = (currentHashB * base + b.charAt(i)) % mod;
        }

        String wordB0 = b.substring(0, k);
        bHashes.computeIfAbsent(currentHashB, h -> new ArrayList<>()).add(wordB0);

        for (int i = 1; i <= b.length() - k; i++) {
            long prevCharVal = b.charAt(i - 1);
            long nextCharVal = b.charAt(i + k - 1);

            currentHashB = (currentHashB - prevCharVal * highestPower) % mod;
            if (currentHashB < 0) {
                currentHashB += mod;
            }
            currentHashB = (currentHashB * base + nextCharVal) % mod;

            String subB = b.substring(i, i + k);
            bHashes.computeIfAbsent(currentHashB, h -> new ArrayList<>()).add(subB);
        }

        // Теперь вычисляем хеши для окон строки a и сверяем их с bHashes
        long currentHashA = 0;
        for (int i = 0; i < k; i++) {
            currentHashA = (currentHashA * base + a.charAt(i)) % mod;
        }

        int count = 0;

        // Проверяем первое окно строки a
        if (bHashes.containsKey(currentHashA)) {
            String subA = a.substring(0, k);
            for (String candidate : bHashes.get(currentHashA)) {
                if (candidate.equals(subA)) {
                    count++;
                    break;
                }
            }
        }

        // Проходим остальные окна строки a
        for (int i = 1; i <= a.length() - k; i++) {
            long prevCharVal = a.charAt(i - 1);
            long nextCharVal = a.charAt(i + k - 1);

            currentHashA = (currentHashA - prevCharVal * highestPower) % mod;
            if (currentHashA < 0) {
                currentHashA += mod;
            }
            currentHashA = (currentHashA * base + nextCharVal) % mod;

            if (bHashes.containsKey(currentHashA)) {
                String subA = a.substring(i, i + k);
                List<String> candidates = bHashes.get(currentHashA);
                boolean foundMatch = false;
                for (String candidate : candidates) {
                    if (candidate.equals(subA)) {
                        foundMatch = true;
                        break;
                    }
                }
                if (foundMatch) {
                    count++;
                }
            }
        }

        return count;
    }
}

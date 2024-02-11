
//2.b
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {
    public static List<Integer> findSecretReceivers(int n, int[][] intervals, int firstPerson) {
        boolean[] secretReceived = new boolean[n];
        secretReceived[firstPerson] = true; // Person 0 initially has the secret

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];
            for (int i = start; i <= end; i++) {
                secretReceived[i] = true; // Mark individuals within the interval as having received the secret
            }
        }

        List<Integer> secretReceivers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (secretReceived[i]) {
                secretReceivers.add(i); // Add index of individuals who received the secret
            }
        }

        return secretReceivers;
    }

    public static void main(String[] args) {
        int n = 5;
        int[][] intervals = { { 0, 2 }, { 1, 3 }, { 2, 4 } };
        int firstPerson = 0;
        List<Integer> result = findSecretReceivers(n, intervals, firstPerson);
        System.out.println("The set of Individuals who will eventually know the secret: " + result);
    }
}
// Output:
// The set of Individuals who will eventually know the secret: [0, 1, 2, 3, 4]

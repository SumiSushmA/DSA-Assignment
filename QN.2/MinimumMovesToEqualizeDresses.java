
//2.a 
import java.util.Arrays;

public class MinimumMovesToEqualizeDresses {

    public static int minMovesToEqualizeDresses(int[] sewingMachines) {
        if (sewingMachines == null || sewingMachines.length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty.");
        }

        int totalDresses = Arrays.stream(sewingMachines).sum();
        int numMachines = sewingMachines.length;

        if (totalDresses % numMachines != 0) {
            return -1;
        }

        int targetDresses = totalDresses / numMachines;
        int moves = 0;
        int balance = 0;

        for (int dresses : sewingMachines) {
            balance += dresses - targetDresses;
            moves = Math.max(moves, Math.abs(balance));
        }

        return moves;
    }

    public static void main(String[] args) {
        int[] sewingMachines = { 1, 0, 5 };
        int minMoves = minMovesToEqualizeDresses(sewingMachines);

        if (minMoves == -1) {
            System.out.println("It is not possible to equalize the number of dresses.");
        } else {
            System.out.println("Minimum moves to equalize dresses: " + minMoves);
        }
    }
}

// Output:
// Minimum moves to equalize dresses: 3

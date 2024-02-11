import java.util.Arrays;

public class EngineBuilding {

    public static int minTimeToBuildEngines(int[] engines, int splitCost) {
        Arrays.sort(engines); // This is used for sorting array
        return minTimeToBuildEnginesHelper(engines, splitCost, 0, engines.length - 1); // method to call helper to
                                                                                       // calculate minimumtime
    }

    private static int minTimeToBuildEnginesHelper(int[] engines, int splitCost, int left, int right) {
        if (left > right) {
            return 0;
        }
        if (left == right) {
            return engines[left];
        }
        int mid = left + (right - left) / 2; // midpoint calculation
        int timeToBuildLeft = minTimeToBuildEnginesHelper(engines, splitCost, left, mid);
        int timeToBuildRight = minTimeToBuildEnginesHelper(engines, splitCost, mid + 1, right);
        int timeToSplitAndMerge = splitCost + Math.max(timeToBuildLeft, timeToBuildRight);
        return timeToSplitAndMerge;
    }

    public static void main(String[] args) {
        int[] engines = { 1, 2, 3 };
        int splitCost = 1;
        int minTime = minTimeToBuildEngines(engines, splitCost);
        System.out.println("Minimum time to build all engines: " + minTime);
    }
}
// Output:
// Minimum time to build all engines: 4

// 3.a
import java.util.Comparator;
import java.util.PriorityQueue;

public class ScoreTracker {
    private PriorityQueue<Double> lowerHalf; // Max heap
    private PriorityQueue<Double> upperHalf; // Min heap
    private int count;

    public ScoreTracker() {
        lowerHalf = new PriorityQueue<>(Comparator.reverseOrder());
        upperHalf = new PriorityQueue<>();
        count = 0;
    }

    public void addScore(double score) {
        if (count % 2 == 0) {
            lowerHalf.offer(score);
            upperHalf.offer(lowerHalf.poll());
        } else {
            upperHalf.offer(score);
            lowerHalf.offer(upperHalf.poll());
        }
        count++;
    }

    public double getMedianScore() {
        if (count % 2 == 0) {
            return (lowerHalf.peek() + upperHalf.peek()) / 2.0;
        } else {
            return upperHalf.peek();
        }
    }

    public static void main(String[] args) {
        ScoreTracker scoreTracker = new ScoreTracker();
        scoreTracker.addScore(85.5);
        scoreTracker.addScore(92.3);
        scoreTracker.addScore(77.8);
        scoreTracker.addScore(90.1);
        double median1 = scoreTracker.getMedianScore();
        System.out.println("Median 1: " + median1);

        scoreTracker.addScore(81.2);
        scoreTracker.addScore(88.7);
        double median2 = scoreTracker.getMedianScore();
        System.out.println("Median 2: " + median2);
    }
}

// Output:
// Median 1: 87.8
// Median 2: 87.1

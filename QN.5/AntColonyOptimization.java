
// 5.a
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntColonyOptimization {
    private double[][] distance;
    private double[][] pheromones;
    private double alpha = 1.0; // Pheromone importance
    private double beta = 2.0; // Distance importance
    private double evaporation = 0.5;
    private double Q = 500; // Pheromone left on trail per tour
    private double antFactor = 0.8;
    private int numCities;
    private int numAnts;
    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();
    private double bestTourLength = Double.MAX_VALUE;
    private List<Integer> bestTourOrder;

    // Inner class to represent an Ant
    private class Ant {
        List<Integer> tour = new ArrayList<>();
        boolean[] visited;
        double tourLength;

        public Ant(int numCities) {
            visited = new boolean[numCities];
            tourLength = 0;
        }

        void visitCity(int city) {
            tour.add(city);
            visited[city] = true;
        }

        boolean visited(int i) {
            return visited[i];
        }

        void clear() {
            tour.clear();
            tourLength = 0;
            for (int i = 0; i < visited.length; i++) {
                visited[i] = false;
            }
        }
    }

    public AntColonyOptimization(int numCities, double[][] distance) {
        this.numCities = numCities;
        this.distance = distance;
        this.numAnts = (int) (numCities * antFactor);
        this.pheromones = new double[numCities][numCities];

        for (int i = 0; i < numAnts; i++) {
            ants.add(new Ant(numCities));
        }

        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] = 1;
            }
        }
    }

    public void startAntOptimization(int numIterations) {
        for (int iteration = 0; iteration < numIterations; iteration++) {
            for (Ant ant : ants) {
                ant.clear();
                ant.visitCity(random.nextInt(numCities));

                while (ant.tour.size() < numCities) {
                    int nextCity = selectNextCity(ant);
                    ant.visitCity(nextCity);
                    ant.tourLength += distance[ant.tour.get(ant.tour.size() - 2)][nextCity];
                }

                // Complete the tour by returning to the start city
                ant.tourLength += distance[ant.tour.get(ant.tour.size() - 1)][ant.tour.get(0)];

                // Update the best tour if current tour is better
                if (ant.tourLength < bestTourLength) {
                    bestTourLength = ant.tourLength;
                    bestTourOrder = new ArrayList<>(ant.tour);
                }
            }

            // Update pheromones
            evaporatePheromones();
            for (Ant ant : ants) {
                double pheromoneToAdd = Q / ant.tourLength;
                for (int i = 0; i < numCities - 1; i++) {
                    pheromones[ant.tour.get(i)][ant.tour.get(i + 1)] += pheromoneToAdd;
                }
                // Complete the cycle by updating the pheromone for the path from the last city
                // back to the first city
                pheromones[ant.tour.get(numCities - 1)][ant.tour.get(0)] += pheromoneToAdd;
            }
        }

        System.out.println("Best tour length: " + bestTourLength);
        System.out.println("Best tour order: " + bestTourOrder);
    }

    private int selectNextCity(Ant ant) {
        int fromCity = ant.tour.get(ant.tour.size() - 1);
        double[] probabilities = new double[numCities];
        double sum = 0.0;

        for (int i = 0; i < numCities; i++) {
            if (!ant.visited(i)) {
                probabilities[i] = Math.pow(pheromones[fromCity][i], alpha)
                        * Math.pow(1.0 / distance[fromCity][i], beta);
                sum += probabilities[i];
            }
        }

        // Convert to probabilities
        for (int i = 0; i < numCities; i++) {
            if (sum > 0) {
                probabilities[i] /= sum;
            }
        }

        // Roulette wheel selection
        return rouletteWheelSelection(probabilities);
    }

    private void evaporatePheromones() {
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] *= (1 - evaporation);
            }
        }
    }

    private int rouletteWheelSelection(double[] probabilities) {
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < numCities; i++) {
            cumulativeProbability += probabilities[i];
            if (cumulativeProbability >= randomValue) {
                return i;
            }
        }
        return numCities - 1;
    }

    public static void main(String[] args) {
        // Example usage
        int numCities = 4;
        double[][] distanceMatrix = {
                { 0, 2, 3, 4 },
                { 2, 0, 4, 5 },
                { 3, 4, 0, 1 },
                { 4, 5, 1, 0 }
        };
        AntColonyOptimization aco = new AntColonyOptimization(numCities, distanceMatrix);
        aco.startAntOptimization(100); // Number of iterations
    }
}

// Output:
// Best tour length: 11.0
// Best tour order: [2, 3, 0, 1]

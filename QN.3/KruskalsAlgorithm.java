
//3.b
import java.util.*;

class Edge implements Comparable<Edge> {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    // Compare edges based on their weights
    @Override
    public int compareTo(Edge other) {
        return this.weight - other.weight;
    }
}

class DisjointSet {
    int[] parent, rank;

    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i; // Each node is its own parent initially
            rank[i] = 0; // Rank of each node is initially 0
        }
    }

    public int find(int x) {
        if (parent[x] != x)
            parent[x] = find(parent[x]);
        return parent[x];
    }

    public void union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot == yRoot)
            return;
        if (rank[xRoot] < rank[yRoot])
            parent[xRoot] = yRoot;
        else if (rank[xRoot] > rank[yRoot])
            parent[yRoot] = xRoot;
        else {
            parent[yRoot] = xRoot;
            rank[xRoot]++;
        }
    }
}

public class KruskalsAlgorithm {
    int V;// Number of vertices in the graph
    List<Edge> edges; // List of edges in the graph

    public KruskalsAlgorithm(int V) {
        this.V = V;
        edges = new ArrayList<>();
    }

    public void addEdge(int src, int dest, int weight) { // Method to add an edge to the graph
        edges.add(new Edge(src, dest, weight));
    }

    public List<Edge> minimumSpanningTree() {
        List<Edge> mst = new ArrayList<>();
        Collections.sort(edges);

        DisjointSet ds = new DisjointSet(V);
        for (Edge edge : edges) {
            int src = edge.src;
            int dest = edge.dest;
            int srcRoot = ds.find(src);
            int destRoot = ds.find(dest);
            if (srcRoot != destRoot) {
                mst.add(edge);
                ds.union(srcRoot, destRoot);
            }
        }
        return mst;
    }

    public static void main(String[] args) {
        KruskalsAlgorithm graph = new KruskalsAlgorithm(4);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 4);

        List<Edge> mst = graph.minimumSpanningTree();
        System.out.println("Edges in the Minimum Spanning Tree:");
        for (Edge edge : mst) {
            System.out.println(edge.src + " - " + edge.dest + " : " + edge.weight);
        }
    }
}

// Output:
// Edges in the Minimum Spanning Tree:
// 2 - 3 : 4
// 0 - 3 : 5
// 0 - 1 : 10
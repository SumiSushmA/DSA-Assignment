
//5.b
import java.util.*;

public class NetworkImpactFinder {
    int[] disc, low;
    int time = 1;
    List<List<Integer>> ans = new ArrayList<>();
    Map<Integer, List<Integer>> edgeMap = new HashMap<>(); // Map to represent the graph

    public List<Integer> findImpactedDevices(int n, List<List<Integer>> connections, int targetDevice) {
        disc = new int[n]; // Initialize discovery time array
        low = new int[n]; // Initialize low value array

        for (int i = 0; i < n; i++)
            edgeMap.put(i, new ArrayList<Integer>());

        // Populate the adjacency lists based on connections
        for (List<Integer> conn : connections) {
            edgeMap.get(conn.get(0)).add(conn.get(1));
            edgeMap.get(conn.get(1)).add(conn.get(0));
        }

        // Perform DFS traversal to find critical connections
        dfs(targetDevice, -1);

        // Check if the target device is a source node in any connection
        boolean isSourceNode = false;
        for (List<Integer> conn : connections) {
            if (conn.get(0) == targetDevice) {
                isSourceNode = true;
                break;
            }
        }

        // If target device is not a source node, no devices are impacted
        if (!isSourceNode) {
            return new ArrayList<>();
        }

        // Store the impacted devices (other than the target device)
        Set<Integer> impactedDevicesSet = new HashSet<>();
        for (List<Integer> connection : ans) {
            int u = connection.get(0);
            int v = connection.get(1);

            if (u == targetDevice) {
                impactedDevicesSet.add(v);
            } else if (v == targetDevice) {
                impactedDevicesSet.add(u);
            }
        }

        // Find additional affected devices that are indirectly impacted
        Set<Integer> additionalAffectedDevices = new HashSet<>();
        for (int affectedDevice : impactedDevicesSet) {
            for (int neighbor : edgeMap.get(affectedDevice)) {
                if (!impactedDevicesSet.contains(neighbor)) {
                    additionalAffectedDevices.add(neighbor);
                }
            }
        }

        // Combine directly and indirectly impacted devices, excluding the target device
        impactedDevicesSet.addAll(additionalAffectedDevices);
        impactedDevicesSet.remove(targetDevice);

        return new ArrayList<>(impactedDevicesSet);
    }

    // Depth-first search method to find critical connections
    public void dfs(int curr, int prev) {
        disc[curr] = low[curr] = time++;
        for (int next : edgeMap.get(curr)) {
            if (next == prev)
                continue;
            if (disc[next] == 0) {
                dfs(next, curr);
                low[curr] = Math.min(low[curr], low[next]);
                if (low[next] > disc[curr])
                    ans.add(Arrays.asList(curr, next)); // Found a critical connection
            } else {
                low[curr] = Math.min(low[curr], disc[next]);
            }
        }
    }

    public static void main(String[] args) {
        NetworkImpactFinder network = new NetworkImpactFinder();

        int n = 8; // Number of devices
        List<List<Integer>> connections = new ArrayList<>();
        connections.add(Arrays.asList(0, 1));
        connections.add(Arrays.asList(0, 2));
        connections.add(Arrays.asList(1, 3));
        connections.add(Arrays.asList(1, 6));
        connections.add(Arrays.asList(2, 4));
        connections.add(Arrays.asList(4, 6));
        connections.add(Arrays.asList(4, 5));
        connections.add(Arrays.asList(5, 7));

        int targetDevice = 4; // Target device to find impacted devices

        // Find impacted devices given the target device
        List<Integer> impactedDevices = network.findImpactedDevices(n, connections, targetDevice);

        System.out.println("Impacted Devices (other than target device " + targetDevice + "): " + impactedDevices);
    }
}

// Output:
// Impacted Devices (other than target device 4): [5, 7]
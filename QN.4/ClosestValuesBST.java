
//4.b
import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

public class ClosestValuesBST {
    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        LinkedList<Integer> closestValues = new LinkedList<>(); // LinkedList to maintain the closest k values

        // Perform inorder traversal to get sorted values from BST
        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }

            curr = stack.pop();
            if (closestValues.size() < k) {
                closestValues.add(curr.val); // Add initial k values to the list
            } else {

                if (Math.abs(target - curr.val) < Math.abs(target - closestValues.getFirst())) {
                    closestValues.removeFirst();
                    closestValues.add(curr.val);
                } else {

                    if (curr.val > target)
                        break;
                }
            }

            curr = curr.right;
        }

        return closestValues;
    }

    // Helper method to create a BST from array
    public TreeNode arrayToBST(int[] arr, int start, int end) {
        if (start > end)
            return null;
        int mid = (start + end) / 2;
        TreeNode root = new TreeNode(arr[mid]);
        root.left = arrayToBST(arr, start, mid - 1);
        root.right = arrayToBST(arr, mid + 1, end);
        return root;
    }

    // Main method to test the ClosestValuesInBST class
    public static void main(String[] args) {
        ClosestValuesBST closestValuesInBST = new ClosestValuesBST();
        int[] arr = { 1, 2, 3, 4, 5, 6, 7 };
        TreeNode root = closestValuesInBST.arrayToBST(arr, 0, arr.length - 1);
        double target = 3.8;
        int k = 2;
        List<Integer> result = closestValuesInBST.closestKValues(root, target, k);
        System.out.println("Closest values to " + target + " are: " + result);
    }
}

// Output:
// Closest values to 3.8 are: [3, 4]

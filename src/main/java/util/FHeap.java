package util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class FHeap {

    // heap size
    private int size = 0;

    public static final class Node {

        private Node next; // Next element
        private Node prev; // Previous element
        private Node child; // Child node

        private String element; // Element being stored here
        private double cost; // Its priority or the cost in our case

        private int degree = 0; // Number of children

        // constructor
        private Node(String id, double priority) {
            next = prev = this;
            element = id;
            cost = priority;
        }

        // get the value
        public String getId() {
            return element;
        }

        // set the value
        public void setValue(String value) {
            element = value;
        }

        // get priority
        public double getCost() {
            return cost;
        }

    }

    // min element in heap
    private Node min = null;

    // check if heap is empty
    public boolean isEmpty() {
        return min == null;
    }

    // Inserts element with priority into the Fibonacci heap.
    public Node enqueue(String id, double priority) {
        if (Double.isNaN(priority))
            throw new IllegalArgumentException(priority + " is invalid.");
        Node result = new Node(id, priority);
        // Merge result list with the tree list.
        min = mergeLists(min, result);
        size = size + 1;
        // return the element
        return result;
    }

    // Dequeueing the Fibonacci heap.
    public Node dequeueMin() {
        if (isEmpty())
            throw new NoSuchElementException("Heap is empty.");

        size = size - 1;

        // store min element
        Node minElement = min;

        if (min.next == min) // check if other elements exist
            min = null;
        else {
            min.prev.next = min.next;
            min.next.prev = min.prev;
            min = min.next;
        }

        // Clear the parent fields of all of the min element's children to make
        // them roots
        if (minElement.child != null) {
            // store first visited child
            Node current = minElement.child;
            do
                current = current.next;
            while (current != minElement.child);
        }

        // merge into topmost list, store one in variable
        min = mergeLists(min, minElement.child);

        if (min == null)
            return minElement;

        // arraylist to keep the degrees of the trees
        List<Node> treeTable = new ArrayList<Node>();

        // make sure if the node is visited once
        List<Node> toVisit = new ArrayList<Node>();

        // make sure to add all
        for (Node current = min; toVisit.isEmpty() || toVisit.get(0) != current; current = current.next)
            toVisit.add(current);

        // traversing
        for (Node current : toVisit) {
            while (true) {

                while (current.degree >= treeTable.size())
                    treeTable.add(null);

                if (treeTable.get(current.degree) == null) {
                    treeTable.set(current.degree, current);
                    break;
                }

                // merging
                Node other = treeTable.get(current.degree);
                treeTable.set(current.degree, null); // Clear the slot

                // check for smaller roots
                Node min = (other.cost < current.cost) ? other : current;
                Node max = (other.cost < current.cost) ? current : other;

                // merge max into min's child list.
                max.next.prev = max.prev;
                max.prev.next = max.next;

                max.next = max.prev = max;
                min.child = mergeLists(min.child, max);

                // Increase min's degree
                min.degree = min.degree + 1;

                current = min;
            }

            // update min
            if (current.cost <= min.cost)
                min = current;
        }
        return minElement;
    }

    // Merge the two lists together into one circularly-linked list
    private static Node mergeLists(Node one, Node two) {

        if (one == null && two == null)
            return null;
        else if (one != null && two == null)
            return one;
        else if (one == null && two != null)
            return two;
        else {

            Node oneNext = one.next;
            one.next = two.next;
            one.next.prev = one;
            two.next = oneNext;
            two.next.prev = two;

            // check for smaller one
            return one.cost < two.cost ? one : two;
        }
    }

}

package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private class Node {

        Task data;
        Node prev;
        Node next;

        private Node(Task data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    private ArrayList<Task> getTasks() {
        Node currentNode = head;
        ArrayList<Task> tasks = new ArrayList<>();
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private Node linkLast(Task task) {
        final Node tailNode = tail;
        Node node = new Node(task, tail, null);
        tail = node;
        if (tailNode == null) {
            head = node;
        } else {
            tailNode.next = node;
        }
        return node;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void addTask(Task task) {
        Node node = nodeMap.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        node = linkLast(task);
        nodeMap.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }
}

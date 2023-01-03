package manager;

import tasks.Task;

import java.util.*;

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
        if (node.prev == null) { // head
            head = node.next;
            node.next.prev = null;
        } else if (node.next == null) { // tail
            tail = node.prev;
            node.prev.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    @Override
    public void remove(int id) { // Добавьте его вызов при удалении задач, чтобы они также удалялись из истории просмотров.
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }
}

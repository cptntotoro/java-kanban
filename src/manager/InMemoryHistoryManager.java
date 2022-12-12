package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_COUNTER = 10;

    private List<Task> viewedTasks = new LinkedList<>();

    @Override
    public void addTask(Task task) {
        if (viewedTasks.size() >= MAX_COUNTER) {
            viewedTasks.remove(0);
        }
        viewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewedTasks;
    }
}

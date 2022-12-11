package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private int counter = 0;
    private static final int MAX_COUNTER = 10;

    private List<Task> viewedTasks = new ArrayList<>(MAX_COUNTER);

    @Override
    public void addTask(Task task) {

        if (viewedTasks.size() < MAX_COUNTER) {
            viewedTasks.add(counter, task);
        } else {
            viewedTasks.set(counter, task);
        }

        counter++;

        if (counter > MAX_COUNTER - 1) {
            counter = 0;
        }
    }

    @Override
    public List<Task> getHistory() {
        return viewedTasks;
    }
}

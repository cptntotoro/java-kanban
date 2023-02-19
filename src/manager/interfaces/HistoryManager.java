package manager.interfaces;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addTask(Task task);

    List<Task> getHistory();

    void remove(int id);

}

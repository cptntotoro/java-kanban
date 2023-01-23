package manager;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultHistory() {
        return new FileBackedTasksManager(new File("resources/task.csv"));
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}

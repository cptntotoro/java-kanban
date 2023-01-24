package manager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}

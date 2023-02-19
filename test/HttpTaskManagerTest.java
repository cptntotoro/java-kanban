import http.servers.KVServer;
import manager.HttpTaskManager;
import manager.exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{

    KVServer kvServer;

    @BeforeEach
    public void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(8078);
    }

    @AfterEach
    protected void stop() {
        kvServer.stop();
    }

    @Test
    public void loadFromKVServer() {
        initializeTasks();

        List<Task> allTasks = taskManager.getAllTasks();
        assertFalse(allTasks.isEmpty(), "Tasks have not been acquired.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertFalse(allSubtasks.isEmpty(), "Subtasks have not been acquired.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Epics have not been acquired.");

        List<Task> taskHistory = taskManager.getHistory();
        assertFalse(taskHistory.isEmpty(), "Task history must not be empty.");

        HttpTaskManager taskManager2 = new HttpTaskManager(8078);
        taskManager2.load();
        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from KVServer must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from KVServer must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from KVServer must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from KVServer must match.");
    }

    @Test
    public void loadFromEmptyKVServer() {

        HttpTaskManager taskManager2 = new HttpTaskManager(8078);
        taskManager2.load();

        List<Task> allTasks = taskManager2.getAllTasks();
        assertTrue(allTasks.isEmpty(), "KVServer must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager2.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "KVServer must not contain any subtasks.");

        List<Epic> allEpics = taskManager2.getAllEpics();
        assertTrue(allEpics.isEmpty(), "KVServer must not contain any epics.");

        List<Task> taskHistory = taskManager2.getHistory();
        assertTrue(taskHistory.isEmpty(), "KVServer must not contain task history.");
    }

    @Test
    public void loadFromNonExistentKVServer() {
        kvServer.stop();

        assertThrows(
                ManagerSaveException.class,
                () -> new HttpTaskManager(8078)
                , "Unreachable KVServer.");
        assertThrows(
                ConnectException.class,
                () -> {
                    try {
                        new HttpTaskManager(8078);
                    } catch (ManagerSaveException ex) {
                        throw ex.getCause();
                    }
                }
                , "Unreachable KVServer.");
    }

    @Test
    public void saveAndLoadFromKVServerEmptyListOfTasks() {

        List<Task> allTasks = taskManager.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty list must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty list must not contain any subtasks.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertTrue(allEpics.isEmpty(), "Empty list must not contain any epics.");

        List<Task> taskHistory = taskManager.getHistory();
        assertTrue(taskHistory.isEmpty(), "Empty list must not contain task history.");

        taskManager.save();

        HttpTaskManager taskManager2 = new HttpTaskManager(8078);
        taskManager2.load();
        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from KVServer must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from KVServer must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from KVServer must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from KVServer must match.");
    }



    @Test
    public void loadFromKVServerEmptyEpic() {
        epic2 = taskManager.getEpicById(taskManager.createEpic(new Epic("Empty Epic", "Empty epic description")));

        List<Task> allTasks = taskManager.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty list must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty list must not contain any subtasks.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Epic doesn't exist.");

        List<Task> taskHistory = taskManager.getHistory();
        assertFalse(taskHistory.isEmpty(), "Task history must not be empty.");

        HttpTaskManager taskManager2 = new HttpTaskManager(8078);
        taskManager2.load();

        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from KVServer must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from KVServer must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from KVServer must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from KVServer must match.");
    }


    @Test
    public void loadFromKVServerEmptyHistory() {
        taskManager.createEpic(new Epic("Empty Epic", "Empty epic description"));

        List<Task> allTasks = taskManager.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty list must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty list must not contain any subtasks.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Epic doesn't exist.");

        List<Task> taskHistory = taskManager.getHistory();
        assertTrue(taskHistory.isEmpty(), "Task history must be empty.");

        HttpTaskManager taskManager2 = new HttpTaskManager(8078);
        taskManager2.load();

        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from KVServer must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from KVServer must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from KVServer must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from KVServer must match.");
    }
}

package manager;

import manager.exceptions.ManagerReadException;
import manager.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    public void setUp() {
        file = new File("resources/test.csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    protected void tearDown() {
        if(file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void loadFromFile() {
        initializeTasks();

        List<Task> allTasks = taskManager.getAllTasks();
        assertFalse(allTasks.isEmpty(), "Tasks have not been acquired.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertFalse(allSubtasks.isEmpty(), "Subtasks have not been acquired.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Epics have not been acquired.");

        List<Task> taskHistory = taskManager.getHistory();
        assertFalse(taskHistory.isEmpty(), "Task history must not be empty.");

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from file must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from file must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from file must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from file must match.");
    }

    @Test
    public void loadFromEmptyFile() {

        assertDoesNotThrow(() -> {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.close();
        }, "File has not been created.");

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        List<Task> allTasks = taskManager2.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty file must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager2.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty file must not contain any subtasks.");

        List<Epic> allEpics = taskManager2.getAllEpics();
        assertTrue(allEpics.isEmpty(), "Empty file must not contain any epics.");

        List<Task> taskHistory = taskManager2.getHistory();
        assertTrue(taskHistory.isEmpty(), "Empty file must not contain task history.");
    }

    @Test
    public void loadFromNonExistentFile() {
        file = new File("resources/NonExistentFile.csv");
        assertThrows(
                ManagerReadException.class,
                () -> FileBackedTaskManager.loadFromFile(file)
                , "Unreachable file.");
    }

    @Test
    public void saveAndLoadFromFileEmptyListOfTasks() {

        List<Task> allTasks = taskManager.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty list must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty list must not contain any subtasks.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertTrue(allEpics.isEmpty(), "Empty list must not contain any epics.");

        List<Task> taskHistory = taskManager.getHistory();
        assertTrue(taskHistory.isEmpty(), "Empty list must not contain task history.");

        taskManager.save();

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from file must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from file must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from file must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from file must match.");
    }


//    тесты на эпики были реализованы в рамках проверки менедженров,
//    т.к. сами эпики ничего специфического из себя не представляют,
//    весь отличающийся функционал, вроде расчета времени исполнения или статуса, реализован в менеджерах
//    какие именно "тесты на эпик" вы имеете в виду?
    @Test
    public void loadFromFileEmptyEpic() {
        epic2 = taskManager.getEpicById(taskManager.createEpic(new Epic("Empty Epic", "Empty epic description")));

        List<Task> allTasks = taskManager.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty list must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty list must not contain any subtasks.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Epic doesn't exist.");

        List<Task> taskHistory = taskManager.getHistory();
        assertFalse(taskHistory.isEmpty(), "Task history must not be empty.");

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from file must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from file must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from file must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from file must match.");
    }


    @Test
    public void loadFromFileEmptyHistory() {
        taskManager.createEpic(new Epic("Empty Epic", "Empty epic description"));

        List<Task> allTasks = taskManager.getAllTasks();
        assertTrue(allTasks.isEmpty(), "Empty list must not contain any tasks.");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.isEmpty(), "Empty list must not contain any subtasks.");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Epic doesn't exist.");

        List<Task> taskHistory = taskManager.getHistory();
        assertTrue(taskHistory.isEmpty(), "Task history must be empty.");

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertIterableEquals(allTasks, taskManager2.getAllTasks(), "Tasks written and read from file must match.");
        assertIterableEquals(allSubtasks, taskManager2.getAllSubtasks(), "Subtasks written and read from file must match.");
        assertIterableEquals(allEpics, taskManager2.getAllEpics(), "Epics written and read from file must match.");
        assertIterableEquals(taskHistory, taskManager2.getHistory(), "Task history written and read from file must match.");
    }
}

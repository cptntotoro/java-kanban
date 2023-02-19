import manager.interfaces.HistoryManager;

import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    private Task task1;
    private Task task2;
    private Task task3;


    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task(0, "Task1", TaskStatus.NEW, "Description1");
        task2 = new Task(1, "Task2", TaskStatus.NEW, "Description2");
        task3 = new Task(2, "Task3", TaskStatus.NEW, "Description3");
    }

    @Test
    void addTask() {
        historyManager.addTask(task1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History must contain 1 task..");
    }

    @Test
    void addTasks() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History must contain 2 tasks.");
    }

    @Test
    void addTaskDuplicate() {
        historyManager.addTask(task1);
        historyManager.addTask(task1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Task duplicates must be rewritten.");
    }

    @Test
    void remove() {
        historyManager.addTask(task1);
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "History is not empty.");
    }

    @Test
    void getEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "History is not empty.");
    }

    @Test
    void deleteLinkedListHead() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "History must contain 3 tasks.");

        historyManager.remove(task1.getId());
        assertIterableEquals(List.of(task2, task3), historyManager.getHistory(), "History task lists must be equal.");
    }

    @Test
    void deleteLinkedListTail() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "History must contain 3 tasks.");

        historyManager.remove(task3.getId());
        assertIterableEquals(List.of(task1, task2), historyManager.getHistory(), "History task lists must be equal.");
    }

    @Test
    void deleteLinkedListBody() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "History must contain 3 tasks.");

        historyManager.remove(task2.getId());
        assertIterableEquals(List.of(task1, task3), historyManager.getHistory(), "History task lists must be equal.");
    }

}
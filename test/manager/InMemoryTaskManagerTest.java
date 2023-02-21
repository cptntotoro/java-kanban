package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void getPrioritizedTasks() {
        Task task0 = taskManager.getTaskById(taskManager.createTask(new Task("Task0", "Description0", null, 60)));
        Integer epic1Id = taskManager.createEpic(new Epic("Not Empty Epic", "Not empty epic description1"));
        Subtask subtask1 = taskManager.getSubtaskById(
                taskManager.createSubtask(new Subtask("Subtask1", "Description1", epic1Id, LocalDateTime.of(2000, 1, 1, 1, 1), 60))
        );
        Task task1 = taskManager.getTaskById(taskManager.createTask(new Task("Task1", "Description1", LocalDateTime.of(2000, 3, 1, 1, 1), 60)));
        Task task2 = taskManager.getTaskById(taskManager.createTask(new Task("Task2", "Description2", LocalDateTime.of(2000, 4, 1, 1, 1), 60)));
        Task task3 = taskManager.getTaskById(taskManager.createTask(new Task("Task3", "Description3", LocalDateTime.of(2000, 2, 1, 1, 1), 60)));
        Task task4 = taskManager.getTaskById(taskManager.createTask(new Task("Task4", "Description4", null, 60)));

        assertIterableEquals(List.of(subtask1, task3, task1, task2, task0, task4), taskManager.getPrioritizedTasks(), "Incorrect task sorting.");

        task0.setStartTime(LocalDateTime.of(1999, 1, 1, 1, 1));
        taskManager.updateTask(task0);
        assertIterableEquals(List.of(task0, subtask1, task3, task1, task2, task4), taskManager.getPrioritizedTasks(), "Incorrect task sorting.");

        taskManager.deleteTaskById(task3.getId());
        assertIterableEquals(List.of(task0, subtask1, task1, task2, task4), taskManager.getPrioritizedTasks(), "Incorrect task sorting.");
    }

    @Test
    public void overlappingTasks() {
        assertNotNull(taskManager.createTask(new Task("Task1", "Description1", LocalDateTime.of(2000, 1, 1, 1, 2), 60)),
                "Task must be created.");
        assertNull(taskManager.createTask(new Task("Task2", "Description2", LocalDateTime.of(2000, 1, 1, 1, 3), 60)),
                "Task with start time overlapping the existing task duration must not be created.");
        assertNull(taskManager.createTask(new Task("Task2", "Description2", LocalDateTime.of(2000, 1, 1, 1, 1), 60)),
                "Task with end time overlapping the existing task duration must not be created.");
        assertNull(taskManager.createTask(new Task("Task2", "Description2", LocalDateTime.of(2000, 1, 1, 1, 3), 30)),
                "Task with duration boxed inside the existing task task duration must not be created.");
        assertNull(taskManager.createTask(new Task("Task2", "Description2", LocalDateTime.of(2000, 1, 1, 1, 1), 120)),
                "Task with duration wrapping the existing task task duration must not be created.");
    }
}

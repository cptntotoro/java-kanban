import manager.interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Integer taskId;
    protected Epic epic1;
    protected Integer epic1Id;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Integer subtask1Id;
    protected Subtask subtask2;
    protected Subtask subtask3;

    protected void initializeTasks() {
        epic1Id = taskManager.createEpic(new Epic("Not Empty Epic", "Not empty epic description1"));
        epic1 = taskManager.getEpicById(epic1Id);
        subtask1Id = taskManager.createSubtask(
                new Subtask("Subtask1", "Description1", epic1.getId(), LocalDateTime.of(2000, 1, 1, 1, 1), 60)
        );
        subtask1 = taskManager.getSubtaskById(subtask1Id);
        subtask2 = taskManager.getSubtaskById(
                taskManager.createSubtask(new Subtask("Subtask2", "Description2", epic1.getId(), LocalDateTime.of(2000, 2, 1, 1, 1), 60))
        );
        subtask3 = taskManager.getSubtaskById(taskManager.createSubtask(
                new Subtask("Subtask3", "Description3", epic1.getId(), LocalDateTime.of(2000, 3, 1, 1, 1), 60))
        );
        epic2 = taskManager.getEpicById(taskManager.createEpic(new Epic("Empty Epic", "Empty epic description")));
        taskId = taskManager.createTask(new Task("Task1", "Description1", LocalDateTime.of(2000, 6, 1, 1, 1), 60));
        task = taskManager.getTaskById(taskId);
    }


    @Test
    void getAllTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "" + "List of tasks must be empty.");

        initializeTasks();
        tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "List of tasks mustn't be empty.");
        assertIterableEquals(List.of(task), tasks, "Task lists must be equal.");
    }


    @Test
    void getAllSubtasks() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "List of subtasks must be empty.");

        initializeTasks();
        subtasks = taskManager.getAllSubtasks();
        assertFalse(subtasks.isEmpty(), "List of subtasks mustn't be empty.");
        assertIterableEquals(List.of(subtask1, subtask2, subtask3), subtasks, "Subtask lists must be equal.");
    }

    @Test
    void getAllEpics() {
        List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "List of epics must be empty.");

        initializeTasks();
        epics = taskManager.getAllEpics();
        assertFalse(epics.isEmpty(), "List of epics mustn't be empty.");
        assertIterableEquals(List.of(epic1, epic2), epics, "Epic lists must be equal.");
    }

    @Test
    void deleteAllTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Task manager must be empty.");

        initializeTasks();
        tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Tasks were not found.");

        taskManager.deleteAllTasks();
        tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "All tasks must be deleted.");
    }

    @Test
    void deleteAllSubtasks() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Task manager must be empty.");

        initializeTasks();
        subtasks = taskManager.getAllSubtasks();
        assertFalse(subtasks.isEmpty(), "Subtasks were not found.");

        taskManager.deleteAllSubtasks();
        subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "All subtasks must be deleted.");

        TaskStatus statusOfCleanedEpic = epic1.getStatus();
        assertEquals(TaskStatus.NEW, statusOfCleanedEpic, "Epic without assigned subtasks must get status NEW.");
    }

    @Test
    void deleteAllEpics() {
        List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Task manager must be empty.");

        initializeTasks();
        epics = taskManager.getAllEpics();
        assertFalse(epics.isEmpty(), "Epics were not found.");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertFalse(subtasks.isEmpty(), "Subtasks of existing epics were not found.");

        taskManager.deleteAllEpics();
        epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "All epics must be deleted.");

        subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "All subtasks must be deleted.");
    }

    @Test
    void getTaskById() {
        initializeTasks();
        Task task = taskManager.getTaskById(100);
        assertNull(task, "Task with unassigned id was found.");

        task = taskManager.getTaskById(taskId);
        assertNotNull(task, "Task with id = " + taskId + " was not found.");
    }

    @Test
    void getSubtaskById() {
        initializeTasks();
        Subtask subtask = taskManager.getSubtaskById(100);
        assertNull(subtask, "Subtask with unassigned id was found.");

        subtask = taskManager.getSubtaskById(subtask1Id);
        assertNotNull(subtask, "Subtask with id = " + subtask1Id + " was not found.");
    }

    @Test
    void getEpicById() {
        initializeTasks();
        Epic epic = taskManager.getEpicById(100);
        assertNull(epic, "Epic with unassigned id was found.");

        epic = taskManager.getEpicById(epic1Id);
        assertNotNull(epic, "Epic with id = " + epic1Id + " was not found.");
    }

    @Test
    void createTask() {
        Task task = new Task("Task name", "Task description");
        Integer taskId = taskManager.createTask(task);
        Task taskReceived = taskManager.getTaskById(taskId);
        assertNotNull(taskReceived, "Task was not found.");
    }

    @Test
    void createSubtask() {
        initializeTasks();

        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic1Id, LocalDateTime.of(2000, 9, 1, 1, 1), 60);
        Integer subtaskId = taskManager.createSubtask(subtask);
        Subtask subtaskReceived = taskManager.getSubtaskById(subtaskId);
        assertNotNull(subtaskReceived, "Subtask was not found.");

        boolean isSubtaskInEpic = epic1.getSubtasksIds().contains(subtaskId);
        assertTrue(isSubtaskInEpic, "Subtask must belong to the assigned epic.");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Epic name", "Epic description");
        Integer epicId = taskManager.createEpic(epic);
        Epic epicReceived = taskManager.getEpicById(epicId);
        assertNotNull(epicReceived, "Epic was not found.");
    }

    @Test
    void updateTask() {
        initializeTasks();

        assertEquals(TaskStatus.NEW, task.getStatus(), "Task status must be NEW.");

        task.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task);
        TaskStatus currentTaskStatus = task.getStatus();
        assertEquals(TaskStatus.DONE, currentTaskStatus, "Task status must be updated.");

        task.setName("Updated task name");
        taskManager.updateTask(task);
        String currentTaskName = task.getName();
        assertEquals("Updated task name", currentTaskName, "Task name must be updated.");

        task.setDescription("Updated task description");
        taskManager.updateTask(task);
        String currentTaskDescription = task.getDescription();
        assertEquals("Updated task description", currentTaskDescription, "Task description must be updated.");
    }

    @Test
    void updateSubtask() {
        initializeTasks();

        assertEquals(TaskStatus.NEW, subtask1.getStatus(), "Subtask status must be NEW.");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        TaskStatus currentSubtaskStatus = subtask1.getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, currentSubtaskStatus, "Subtask status must be updated to IN_PROGRESS.");

        TaskStatus currentEpicStatus = epic1.getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, currentEpicStatus, "Epic status must be updated.");

        subtask1.setName("Updated subtask name");
        taskManager.updateSubtask(subtask1);
        String currentSubtaskName = subtask1.getName();
        assertEquals("Updated subtask name", currentSubtaskName, "Subtask name must be updated.");

        subtask1.setDescription("Updated subtask description");
        taskManager.updateSubtask(subtask1);
        String currentSubtaskDescription = subtask1.getDescription();
        assertEquals("Updated subtask description", currentSubtaskDescription, "Subtask description must be updated.");
    }

    @Test
    void updateEpic() {
        initializeTasks();

        epic1.setName("Updated epic name");
        taskManager.updateEpic(epic1);
        String currentEpicName = epic1.getName();
        assertEquals("Updated epic name", currentEpicName, "Epic name must be updated.");

        epic1.setDescription("Updated epic description");
        taskManager.updateEpic(epic1);
        String currentEpicDescription = epic1.getDescription();
        assertEquals("Updated epic description", currentEpicDescription, "Epic description must be updated.");
    }

    @Test
    void deleteTaskById() {

        Task taskNull = taskManager.getTaskById(100);
        assertNull(taskNull, "Non-existent ids must not be attached to tasks.");

        assertDoesNotThrow(() -> taskManager.deleteTaskById(100), "An exception has been cast.");

        initializeTasks();

        assertNotNull(task, "Task must not be null.");

        taskManager.deleteTaskById(taskId);
        taskNull = taskManager.getTaskById(taskId);
        assertNull(taskNull, "Deleted task must not be accessible by its id.");
    }

    @Test
    void deleteSubtaskById() {
        Subtask subtaskNull = taskManager.getSubtaskById(100);
        assertNull(subtaskNull, "Non-existent ids must not be attached to subtasks.");

        assertDoesNotThrow(() -> taskManager.deleteSubtaskById(100), "An exception has been cast.");

        initializeTasks();

        assertNotNull(subtask1, "Subtask must not be null.");

        taskManager.deleteSubtaskById(subtask1Id);
        subtaskNull = taskManager.getSubtaskById(subtask1Id);
        assertNull(subtaskNull, "Deleted subtask must not be accessible by its id.");
    }

    @Test
    void deleteEpicById() {
        Epic epicNull = taskManager.getEpicById(100);
        assertNull(epicNull, "Non-existent ids must not be attached to epics.");

        assertDoesNotThrow(() -> taskManager.deleteEpicById(100), "An exception has been cast.");

        initializeTasks();

        assertNotNull(epic1, "Epic must not be null.");

        List<Integer> subtasksIds = epic1.getSubtasksIds();
        taskManager.deleteEpicById(epic1Id);
        subtasksIds.forEach(id -> assertNull(taskManager.getSubtaskById(id), "Subtasks must be deleted."));

        epicNull = taskManager.getEpicById(epic1Id);
        assertNull(epicNull, "Deleted epic must not be accessible by its id.");
    }

    @Test
    void getSubtasksOfEpic() {
        initializeTasks();

        List<Subtask> subtasksOfNullEpic = taskManager.getSubtasksOfEpic(100);
        assertNull(subtasksOfNullEpic, "Non-existent epics must not have subtasks.");

        List<Integer> subtaskIdsOfEmptyEpic = epic2.getSubtasksIds();
        assertTrue(subtaskIdsOfEmptyEpic.isEmpty(), "Empty epic must not contain subtasks.");

        List<Subtask> subtasksOfEmptyEpic = taskManager.getSubtasksOfEpic(epic2.getId());
        assertTrue(subtasksOfEmptyEpic.isEmpty(), "Empty epic must not contain subtasks.");

        List<Subtask> subtaskIdsOfEpic = taskManager.getSubtasksOfEpic(epic1Id);
        assertIterableEquals(List.of(subtask1, subtask2, subtask3), subtaskIdsOfEpic, "Subtask lists must be equal.");
    }

    @Test
    void getHistory() {
        initializeTasks();
        assertIterableEquals(List.of(epic1, subtask1, subtask2, subtask3, epic2, task), taskManager.getHistory(), "Wrong history.");

        taskManager.getTaskById(taskId);
        assertIterableEquals(List.of(epic1, subtask1, subtask2, subtask3, epic2, task), taskManager.getHistory(), "Wrong history.");

        taskManager.getSubtaskById(subtask1Id);
        assertIterableEquals(List.of(epic1, subtask2, subtask3, epic2, task, subtask1), taskManager.getHistory(), "Wrong history.");
    }

    private void updateSubtaskStatus(int subtaskId, TaskStatus subtaskStatus) {
        Subtask subtask = taskManager.getSubtaskById(subtaskId);
        subtask.setStatus(subtaskStatus);
        taskManager.updateSubtask(subtask);
    }

    @Test
    void updateEpicStatus() {
        initializeTasks();
        TaskStatus epicStatus = epic2.getStatus();
        assertEquals(TaskStatus.NEW, epicStatus);

        epicStatus = epic1.getStatus();
        assertEquals(TaskStatus.NEW, epicStatus);

        updateSubtaskStatus(subtask1.getId(), TaskStatus.DONE);
        updateSubtaskStatus(subtask2.getId(), TaskStatus.DONE);
        updateSubtaskStatus(subtask3.getId(), TaskStatus.DONE);
        epicStatus = epic1.getStatus();
        assertEquals(TaskStatus.DONE, epicStatus);

        updateSubtaskStatus(subtask2.getId(), TaskStatus.NEW);
        epicStatus = epic1.getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus);

        updateSubtaskStatus(subtask1.getId(), TaskStatus.IN_PROGRESS);
        updateSubtaskStatus(subtask2.getId(), TaskStatus.IN_PROGRESS);
        updateSubtaskStatus(subtask3.getId(), TaskStatus.IN_PROGRESS);
        epicStatus = epic1.getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus);
    }

    @Test
    void timeTask() {
        Integer taskId = taskManager.createTask(new Task("Task1", "Description1"));
        Task task = taskManager.getTaskById(taskId);
        assertNull(task.getStartTime());
        assertEquals(task.getDuration(), 0);
        assertNull(task.getEndTime());

        task.setStartTime(LocalDateTime.of(1999, 1, 1, 1, 1));
        task.setDuration(30);
        taskManager.updateTask(task);
        task = taskManager.getTaskById(taskId);
        assertEquals(task.getStartTime(), LocalDateTime.of(1999, 1, 1, 1, 1));
        assertEquals(task.getDuration(), 30);
        assertEquals(task.getEndTime(), LocalDateTime.of(1999, 1, 1, 1, 31));
    }

    @Test
    void timeEpic() {
        Integer epic1Id = taskManager.createEpic(new Epic("Not Empty Epic", "Not empty epic description1"));
        Epic epic = taskManager.getEpicById(epic1Id);
        Subtask subtask1 = taskManager.getSubtaskById(
                taskManager.createSubtask(new Subtask("Subtask1", "Description1", epic1Id, null, 60))
        );
        Subtask subtask2 = taskManager.getSubtaskById(
                taskManager.createSubtask(new Subtask("Subtask2", "Description2", epic1Id, null, 60))
        );
        Subtask subtask3 = taskManager.getSubtaskById(
                taskManager.createSubtask(new Subtask("Subtask3", "Description3", epic1Id, null, 60))
        );

        assertNull(epic.getStartTime());
        assertEquals(epic.getDuration(), 0);
        assertNull(epic.getEndTime());

        subtask1.setStartTime(LocalDateTime.of(1999, 1, 1, 1, 1));
        subtask1.setDuration(30);
        taskManager.updateSubtask(subtask1);
        assertEquals(epic.getStartTime(), LocalDateTime.of(1999, 1, 1, 1, 1));
        assertEquals(epic.getDuration(), 30);
        assertEquals(epic.getEndTime(), LocalDateTime.of(1999, 1, 1, 1, 31));

        subtask2.setStartTime(LocalDateTime.of(1999, 1, 1, 1, 1));
        subtask2.setDuration(30);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getStartTime(), LocalDateTime.of(1999, 1, 1, 1, 1));
        assertEquals(epic.getDuration(), 30);
        assertEquals(epic.getEndTime(), LocalDateTime.of(1999, 1, 1, 1, 31));

        subtask3.setStartTime(LocalDateTime.of(2001, 1, 1, 1, 1));
        subtask3.setDuration(30);
        taskManager.updateSubtask(subtask3);
        assertEquals(epic.getStartTime(), LocalDateTime.of(1999, 1, 1, 1, 1));
        assertEquals(epic.getDuration(), Duration.between(LocalDateTime.of(1999, 1, 1, 1, 1), LocalDateTime.of(2001, 1, 1, 1, 31)).toMinutes());
        assertEquals(epic.getEndTime(), LocalDateTime.of(2001, 1, 1, 1, 31));

        taskManager.deleteSubtaskById(subtask3.getId());
        assertEquals(epic.getStartTime(), LocalDateTime.of(1999, 1, 1, 1, 1));
        assertEquals(epic.getDuration(), 30);
        assertEquals(epic.getEndTime(), LocalDateTime.of(1999, 1, 1, 1, 31));
    }
}
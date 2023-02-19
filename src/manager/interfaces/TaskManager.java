package manager.interfaces;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    Integer createTask(Task task);

    Integer createSubtask(Subtask subtask);

    Integer createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    List<Subtask> getSubtasksOfEpic(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}

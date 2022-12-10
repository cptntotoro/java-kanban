package manager;

import tasks.Epic;
import tasks.TaskStatus;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public int getNewId() {
        return idCounter++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks = new HashMap<>();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            epic.setSubtasksIds(new ArrayList<>());
        }
    }

    @Override
    public void deleteAllEpics() {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Integer createTask(Task task) {
        if (task == null) {
            return null;
        }
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        return subtask.getId();
    }

    @Override
    public Integer createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic.getId();
    }

   @Override
   public void updateTask(Task task) {
       if (tasks.containsKey(task.getId())) {
           tasks.replace(task.getId(), task);
       }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksList = new ArrayList<>();
            for (Integer subtaskId : subtasks.keySet()) {
                if (epics.get(id).getSubtasksIds().contains(subtaskId)) {
                    subtasksList.add(subtasks.get(subtaskId));
                }
            }
            return subtasksList;
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int id) {
        if (epics.get(id).getSubtasksIds().isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else {
            TaskStatus status = null;
            for (Integer subtaskId : epics.get(id).getSubtasksIds()) {
                if (status == null) {
                    status = subtasks.get(subtaskId).getStatus();
                } else if (status != subtasks.get(subtaskId).getStatus()) {
                    status = TaskStatus.IN_PROGRESS;
                    break;
                }
            }
            epics.get(id).setStatus(status);
        }
    }

}


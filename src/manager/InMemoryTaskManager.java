package manager;

import tasks.Epic;
import tasks.enums.TaskStatus;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getHistoryManager();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;

    protected void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    private int generateNewId() {
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
        deleteItemHistory(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        deleteItemHistory(subtasks.keySet());
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setSubtasksIds(new ArrayList<>());
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteItemHistory(epics.keySet());
        deleteItemHistory(subtasks.keySet());
        epics.clear();
        subtasks.clear();
    }

    private void deleteItemHistory(Set<Integer> ids) {
        for (int id : ids) {
            historyManager.remove(id);
        }
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
        task.setId(generateNewId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        subtask.setId(generateNewId());
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
        epic.setId(generateNewId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.replace(subtask.getId(), subtask) != null) {
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.replace(epic.getId(), epic) != null) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        historyManager.remove(id);
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
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


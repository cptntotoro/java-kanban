package manager;

import tasks.Epic;
import tasks.enums.TaskStatus;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getHistoryManager();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private final Set<Task> prioritizedTasks = new TreeSet<>((Task o1, Task o2) -> {
        if (o1.getStartTime() != null && o2.getStartTime() != null) {
            return o1.getStartTime().isBefore(o2.getStartTime()) ? -1 : 1;
        } else if (o1.getStartTime() == null){
            return 1;
        } else {
            return -1;
        }
    });

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
        prioritizedTasks.removeIf(task -> tasks.containsKey(task.getId()));
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        deleteItemHistory(subtasks.keySet());
        prioritizedTasks.removeIf(subtask -> subtasks.containsKey(subtask.getId()));
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setSubtasksIds(new ArrayList<>());
            updateEpicState(epic.getId());
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteItemHistory(epics.keySet());
        deleteItemHistory(subtasks.keySet());
        prioritizedTasks.removeIf(subtask -> subtasks.containsKey(subtask.getId()));
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
        if (task != null) {
            historyManager.addTask(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.addTask(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTask(epic);
        }
        return epic;
    }

    @Override
    public Integer createTask(Task task) {
        if (task == null || (task.getStartTime() != null && isCrossedTask(task))) {
            return null;
        }

        task.setId(generateNewId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        if (subtask == null || (subtask.getStartTime() != null && isCrossedTask(subtask))) {
            return null;
        }
        subtask.setId(generateNewId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicState(epic.getId());
        prioritizedTasks.add(subtask);
        return subtask.getId();
    }

    @Override
    public Integer createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(generateNewId());
        epics.put(epic.getId(), epic);
        updateEpicState(epic.getId());
        return epic.getId();
    }

    private boolean isCrossedTask(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        for (Task prioritizedTask : getPrioritizedTasks()) {
            LocalDateTime startTimeComp = prioritizedTask.getStartTime();
            LocalDateTime endTimeComp = prioritizedTask.getEndTime();

//            Первое условие проверяет, что голова временного отрезка зацепила существующий временной отрезок
//            Второе условие проверяет, что хвост временного отрезка зацепил существующий временной отрезок
//            Третье условие проверяет, что временной отрезок оборачивает существующий временной отрезок
//            А если мы будем проверять только условие "startTime isAfter startTimeComp && endTime isBefore endTimeComp",
//            то будем отлавливать только частный случай, когда и голова, и хвост временного отрезка оказались в существующем временном отрезке
            if(startTime != null && startTimeComp != null) {
                if ((startTime.isAfter(startTimeComp) && startTime.isBefore(endTimeComp)) ||
                        (endTime.isAfter(startTimeComp) && endTime.isBefore(endTimeComp)) ||
                        (startTime.isBefore(startTimeComp) && endTime.isAfter(endTimeComp))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || (task.getStartTime() != null && isCrossedTask(task))) {
            return;
        }
        tasks.replace(task.getId(), task);
        prioritizedTasks.removeIf(task1 -> task1.getId() == task.getId());
        prioritizedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || (subtask.getStartTime() != null && isCrossedTask(subtask))) {
            return;
        }

        if (subtasks.replace(subtask.getId(), subtask) != null) {
            updateEpicState(subtask.getEpicId());
            prioritizedTasks.removeIf(subtask1 -> subtask1.getId() == subtask.getId());
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.replace(epic.getId(), epic) != null) {
            updateEpicState(epic.getId());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(tasks1 -> tasks1.getId() == id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if(subtask != null) {
            int epicId = subtask.getEpicId();
            subtasks.remove(id);
            historyManager.remove(id);
            Epic epic = epics.get(epicId);
            epic.deleteSubtaskId(id);
            updateEpicState(epic.getId());
            prioritizedTasks.removeIf(subtask1 -> subtask1.getId() == subtask.getId());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if(epic != null) {
            for (int subtaskId : epic.getSubtasksIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            prioritizedTasks.removeIf(subtask -> epic.getSubtasksIds().contains(subtask.getId()));
            epics.remove(id);
            historyManager.remove(id);
        }
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

    private void updateEpicState(int id) {
        if (epics.get(id).getSubtasksIds().isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else {
            TaskStatus status = null;
            LocalDateTime minStartTime = null;
            LocalDateTime maxEndTime = null;
            for (Integer subtaskId : epics.get(id).getSubtasksIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (status == null) {
                    status = subtask.getStatus();
                } else if (status != subtasks.get(subtaskId).getStatus()) {
                    status = TaskStatus.IN_PROGRESS;
                    break;
                }

                if(subtask.getStartTime() != null) {
                    if(minStartTime == null || subtask.getStartTime().isBefore(minStartTime)) {
                        minStartTime = subtask.getStartTime();
                    }
                    if(maxEndTime == null || subtask.getStartTime().isAfter(maxEndTime)) {
                        maxEndTime = subtask.getStartTime().plusMinutes(subtask.getDuration());
                    }
                }
            }
            epics.get(id).setStatus(status);
            epics.get(id).setStartTime(minStartTime);
            epics.get(id).setEndTime(maxEndTime);
            if(minStartTime != null) {
                epics.get(id).setDuration(Duration.between(minStartTime, maxEndTime).toMinutes());
            }
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}


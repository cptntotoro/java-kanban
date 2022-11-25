import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;

    public int getNewId() {
        return idCounter++;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks () {
        tasks = new HashMap<>();
    }

    public void deleteAllSubtasks () {
        subtasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            epic.setSubtasksIds(new ArrayList<>());
        }
    }

    public void deleteAllEpics () {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById (int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById (int id) {
        return epics.get(id);
    }

    public Integer createTask (Task task) {
        if (task == null) {
            return null;
        }
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public Integer createSubtask (Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpicById((subtask).getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        return subtask.getId();
    }

    public Integer createEpic (Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic.getId();
    }

   public void updateTask (Task task) {
       if (tasks.containsKey(task.getId())) {
           tasks.replace(task.getId(), task);
       }
    }

    public void updateSubtask (Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void updateEpic (Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    public void deleteTaskById (int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById (int id) {
        int epicId = getSubtaskById(id).getEpicId();
        subtasks.remove(id);
        Epic epic = getEpicById(epicId);
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    public void deleteEpicById (int id) {
        Epic epic = getEpicById(id);
        for (int subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public List<Subtask> getSubtasksOfEpic(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksList = new ArrayList<>();
            for (Integer subtaskId : subtasks.keySet()) {
                if (getEpicById(id).getSubtasksIds().contains(subtaskId)) {
                    subtasksList.add(getSubtaskById(subtaskId));
                }
            }
            return subtasksList;
        }
        return null;
    }

    private void updateEpicStatus(int id) {
        if (getEpicById(id).getSubtasksIds().isEmpty()) {
            getEpicById(id).setStatus(Statuses.NEW);
        } else {
            Statuses status = null;
            for (Integer subtaskId : getEpicById(id).getSubtasksIds()) {
                if (status == null) {
                    status = getSubtaskById(subtaskId).getStatus();
                } else if (status != getSubtaskById(subtaskId).getStatus()) {
                    status = Statuses.IN_PROGRESS;
                    break;
                }
            }
            getEpicById(id).setStatus(status);
        }
    }

}


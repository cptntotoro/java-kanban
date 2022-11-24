import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    int idCounter = 1;

    public int idGenerator() {
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
    }

    public void deleteAllEpics () {
        epics = new HashMap<>();
    }

    public Task getTaskByID (int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskByID (int id) {
        return subtasks.get(id);
    }

    public Epic getEpicByID (int id) {
        return epics.get(id);
    }

    public Integer createItem (Task task) {
        if (task == null) {
            System.out.println("Невозможно создать объект.");
            return null;
        }
        task.setId(idGenerator());
        if (task.getClass() == Task.class) {
            tasks.put(task.getId(), task);
        } else if (task.getClass() == Subtask.class) {
            subtasks.put(task.getId(), (Subtask) task);
            Epic epic = getEpicByID(((Subtask) task).getEpicId());
            epic.addSubtaskId(task.getId());
            updateEpicStatus(epic.getId());
        } else {
            epics.put(task.getId(), (Epic) task);
            updateEpicStatus(task.getId());
        }
        return task.getId();
    }

    public void updateTask (Task task) {
        tasks.replace(task.getId(), task);
    }

    public void updateSubtask (Subtask subtask) {
        subtasks.replace(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateEpic (Epic epic) {
        epics.replace(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    public void deleteTaskByID (int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskByID (int id) {
        int epicId = getSubtaskByID(id).getEpicId();
        subtasks.remove(id);
        Epic epic = getEpicByID(epicId);
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    public void deleteEpicByID (int id) {
        Epic epic = getEpicByID(id);
        for (int subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public List<Subtask> getSubtasksOfEpic(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksList = new ArrayList<>();
            for (Integer subtaskId : subtasks.keySet()) {
                if (getEpicByID(id).getSubtasksIds().contains(subtaskId)) {
                    subtasksList.add(getSubtaskByID(subtaskId));
                }
            }
            return subtasksList;
        }
        return null;
    }

    private void updateEpicStatus(int id) {
        Statuses status = null;
        if (getEpicByID(id).getSubtasksIds().isEmpty()) {
            getEpicByID(id).setStatus(Statuses.NEW);
        } else {
            for (Integer subtaskId : getEpicByID(id).getSubtasksIds()) {
                if (status == null) {
                    status = getSubtaskByID(subtaskId).getStatus();
                } else if (status != getSubtaskByID(subtaskId).getStatus()) {
                    status = Statuses.IN_PROGRESS;
                    break;
                }
            }
            getEpicByID(id).setStatus(status);
        }
    }

}


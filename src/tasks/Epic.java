package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final TaskType type = TaskType.EPIC;
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void deleteSubtaskId(int subtaskId) {
        subtasksIds.remove((Integer) subtaskId);
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + ",subtaskIds:" + subtasksIds.toString();
    }

    public String toString(Task task) {
        return super.toString();
    }

}
package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final TaskType type = TaskType.EPIC;
    private List<Integer> subtasksIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public Epic(int id, String name, TaskStatus status, String description, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(id, name, status, description, startTime, duration);
        this.endTime = endTime;
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return super.toString() + ",subtaskIds:" + subtasksIds.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return type == epic.type && Objects.equals(subtasksIds, epic.subtasksIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, subtasksIds, endTime);
    }
}
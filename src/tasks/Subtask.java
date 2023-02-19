package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task implements Serializable {

    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, int epicId, LocalDateTime startTime, long duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, TaskStatus status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, TaskStatus status, String description, int epicId, LocalDateTime startTime, long duration) {
        super(id, name, status, description, startTime, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + ",epicId:" + getEpicId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId && type == subtask.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, epicId);
    }
}

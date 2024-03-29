package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable {
    protected TaskType type;
    private String name;
    private String description;
    private Integer id;
    private TaskStatus status = TaskStatus.NEW;

    private LocalDateTime startTime;
    private long duration;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = TaskType.TASK;
    }

    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.type = TaskType.TASK;
    }

    public Task(int id, String name, TaskStatus status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.type = TaskType.TASK;
    }

     public Task(int id, String name, TaskStatus status, String description, LocalDateTime startTime, long duration) {
         this(id, name, status, description);
         this.startTime = startTime;
         this.duration = duration;
         this.type = TaskType.TASK;
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(duration) : null;
    }

    @Override
    public String toString() {
        return "ID:" + id + ",name:" + name + ",description:" + description + ",status:" + status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, startTime, duration);
    }
}
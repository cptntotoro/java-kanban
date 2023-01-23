package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

public class Task {
    private final TaskType type = TaskType.TASK;
    private String name;
    private String description;
    private int id;
    private TaskStatus status = TaskStatus.NEW;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, TaskStatus status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "ID:" + id + ",название:" + name + ",описание:" + description + ",статус:" + status;
    }
}
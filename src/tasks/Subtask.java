package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

public class Subtask extends Task {

    private final TaskType type = TaskType.SUBTASK;
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, TaskStatus status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
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

}

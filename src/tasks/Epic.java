package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void addSubtaskId (int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void deleteSubtaskId (int subtaskId) {
        subtasksIds.remove((Integer) subtaskId);
    }

    @Override
    public String toString() {
        return super.toString() + ",subtaskIds:" + subtasksIds.toString();
    }

}
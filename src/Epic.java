import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
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
package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static Task taskFromString(String str) {

        final String[] strItems = str.split(",");
        final int id = Integer.parseInt(strItems[0]);
        final TaskType type = TaskType.valueOf(strItems[1]);
        final String name = strItems[2];
        final TaskStatus taskStatus = TaskStatus.valueOf(strItems[3]);
        final String description = strItems[4];
        final LocalDateTime startTime = !strItems[5].equals("null") ? LocalDateTime.parse(strItems[5]) : null;
        final long duration = Long.parseLong(strItems[6]);
        switch (type) {
            case TASK:
                return new Task(id, name, taskStatus, description, startTime, duration);
            case EPIC:
                return new Epic(id, name, taskStatus, description, startTime, duration, startTime != null ? startTime.plusMinutes(duration) : null);
            case SUBTASK:
                final int epicId = Integer.parseInt(strItems[7]);
                return new Subtask(id, name, taskStatus, description, epicId, startTime, duration);
            default:
                throw new TaskFormatException("Invalid task type.");
        }
    }

    public static List<Integer> historyFromString(String str) {
        List<Integer> historyIds = new ArrayList<>();
        if(str.isEmpty()) {
            return historyIds;
        }
        final String[] strItems = str.split(",");
        for (String strItem : strItems) {
            historyIds.add(Integer.parseInt(strItem));
        }
        return historyIds;
    }

    public static String toString(Task task) {
        String str = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + (task.getStartTime() != null ? task.getStartTime().toString() : "null") + "," + task.getDuration();
        if(task instanceof Subtask) {
            str = str + "," + ((Subtask)task).getEpicId();
        }
        return str;
    }

    public static String toString(List<Task> tasksInHistory) {
        String taskIds = "";
        for (int i = 0; i < tasksInHistory.size(); i++) {
            taskIds += tasksInHistory.get(i).getId();
            if (i < tasksInHistory.size() - 1) {
                taskIds += ",";
            }
        }
        return taskIds;
    }

}

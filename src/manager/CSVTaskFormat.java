package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static Task taskFromString(String str) {

        final String[] strItems = str.split(",");
        final int id = Integer.parseInt(strItems[0]);
        final TaskType type = TaskType.valueOf(strItems[1]); // опираться на тип, чтобы понимать, надо ли парсить след. строчки
        final String name = strItems[2];
        final TaskStatus taskStatus = TaskStatus.valueOf(strItems[3]);
        final String description = strItems[4];
        switch (type) {
            case TASK:
                return new Task(id, name, taskStatus, description);
            case EPIC:
                return new Epic(id, name, taskStatus, description);
            case SUBTASK:
                final int epicId = Integer.parseInt(strItems[5]);
                return new Subtask(id, name, taskStatus, description, epicId);
        }
        return null;
    }

    public static List<Integer> historyFromString(String str) {
        // TODO: Восстановить список id для истории
        List<Integer> historyIds = new ArrayList<>();
        final String[] strItems = str.split(",");
        for (String strItem : strItems) {
            historyIds.add(Integer.parseInt(strItem));
        }
        return historyIds;
    }

    public static String toString(Task task) {
        String str = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
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

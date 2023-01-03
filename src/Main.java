import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        int taskId1 = taskManager.createTask(new Task("Task1", "Description1"));
        int taskId2 = taskManager.createTask(new Task("Task2", "Description2"));
        int epicId1 = taskManager.createEpic(new Epic("Epic1", "Description1"));
        int subtaskId1 = taskManager.createSubtask(new Subtask("Subtask1", "Subtask1", epicId1));
        int subtaskId2 = taskManager.createSubtask(new Subtask("Subtask2", "Subtask2", epicId1));
        int subtaskId3 = taskManager.createSubtask(new Subtask("Subtask3", "Subtask3", epicId1));
        int epicId2 = taskManager.createEpic(new Epic("Epic2", "Description2"));

        System.out.println("Test1: Запросы задач");

        taskManager.getTaskById(taskId1);
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(taskId2);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(epicId1);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(subtaskId1);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(subtaskId2);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(subtaskId3);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(epicId2);
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(taskId2);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(subtaskId1);
        System.out.println(taskManager.getHistory());

        System.out.println("Test2: Удаление задачи");

        taskManager.deleteTaskById(taskId1);
        System.out.println(taskManager.getHistory());

        System.out.println("Test3: Удаление эпика");

        taskManager.deleteEpicById(epicId1);
        System.out.println(taskManager.getHistory());

    }
}

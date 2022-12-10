import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.TaskStatus;
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
        int epicId2 = taskManager.createEpic(new Epic("Epic2", "Description2"));
        int subtaskId3 = taskManager.createSubtask(new Subtask("Subtask3", "Subtask3", epicId2));

        System.out.println("Test1");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test2");
        Task task1 = taskManager.getTaskById(taskId1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        Task task2 = taskManager.getTaskById(taskId2);
        task2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task2);

        System.out.println(taskManager.getAllTasks());

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test3");
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println(taskManager.getAllEpics());

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test4");
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epicId1));

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test5");
        Subtask subtask3 = taskManager.getSubtaskById(subtaskId3);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epicId2));

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test6");
        taskManager.deleteSubtaskById(subtaskId1);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epicId1));

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test7");
        taskManager.deleteEpicById(epicId2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("History");
        System.out.println(taskManager.getHistory());


        System.out.println("Test8");
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId1);

        System.out.println("History");
        System.out.println(taskManager.getHistory());

    }
}

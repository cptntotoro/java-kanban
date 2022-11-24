public class Main {

    public static void main(String[] args) {

        Manager taskManager = new Manager();

        int taskId1 = taskManager.createItem(new Task("Task1", "Description1"));
        int taskId2 = taskManager.createItem(new Task("Task2", "Description2"));
        int epicId1 = taskManager.createItem(new Epic("Epic1", "Description1"));
        int subtaskId1 = taskManager.createItem(new Subtask("Subtask1", "Subtask1", epicId1));
        int subtaskId2 =taskManager.createItem(new Subtask("Subtask2", "Subtask2", epicId1));
        int epicId2 = taskManager.createItem(new Epic("Epic2", "Description2"));
        int subtaskId3 =taskManager.createItem(new Subtask("Subtask3", "Subtask3", epicId2));

        System.out.println("Test1");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());


        System.out.println("Test2");
        Task task1 = taskManager.getTaskByID(taskId1);
        task1.setStatus(Statuses.IN_PROGRESS);
        taskManager.updateTask(task1);

        Task task2 = taskManager.getTaskByID(taskId2);
        task2.setStatus(Statuses.DONE);
        taskManager.updateTask(task2);

        System.out.println(taskManager.getAllTasks());


        System.out.println("Test3");
        Subtask subtask1 = taskManager.getSubtaskByID(subtaskId1);
        subtask1.setStatus(Statuses.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println(taskManager.getAllEpics());


        System.out.println("Test4");
        Subtask subtask2 = taskManager.getSubtaskByID(subtaskId2);
        subtask2.setStatus(Statuses.DONE);
        taskManager.updateSubtask(subtask2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epicId1));


        System.out.println("Test5");
        Subtask subtask3 = taskManager.getSubtaskByID(subtaskId3);
        subtask3.setStatus(Statuses.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epicId2));


        System.out.println("Test6");
        taskManager.deleteSubtaskByID(subtaskId1);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epicId1));


        System.out.println("Test7");
        taskManager.deleteEpicByID(epicId2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

    }
}

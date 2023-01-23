package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefaultHistory();

        int taskId1 = taskManager.createTask(new Task("Task1", "Description1"));
        int taskId2 = taskManager.createTask(new Task("Task2", "Description2"));
        int epicId1 = taskManager.createEpic(new Epic("Epic1", "Description1"));
        int subtaskId1 = taskManager.createSubtask(new Subtask("Subtask1", "Subtask1", epicId1));
        int subtaskId2 = taskManager.createSubtask(new Subtask("Subtask2", "Subtask2", epicId1));
        int subtaskId3 = taskManager.createSubtask(new Subtask("Subtask3", "Subtask3", epicId1));
        int epicId2 = taskManager.createEpic(new Epic("Epic2", "Description2"));

        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);
        taskManager.getEpicById(epicId1);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId3);
        taskManager.getEpicById(epicId2);
        taskManager.getTaskById(taskId2);
        taskManager.getSubtaskById(subtaskId1);

        taskManager.deleteTaskById(taskId1);
        taskManager.deleteEpicById(epicId2);

        TaskManager taskManager2 = FileBackedTasksManager.loadFromFile(((FileBackedTasksManager) taskManager).getFile());


        System.out.println("Таски менеджера 1");
        System.out.println(taskManager.getAllTasks());

        System.out.println();
        System.out.println();

        System.out.println("Таски менеджера 2");
        System.out.println(taskManager2.getAllTasks());


        System.out.println();
        System.out.println("---");
        System.out.println();


        System.out.println("Сабтаски менеджера 1");
        System.out.println(taskManager.getAllSubtasks());

        System.out.println();
        System.out.println();

        System.out.println("Сабтаски менеджера 2");
        System.out.println(taskManager2.getAllSubtasks());


        System.out.println();
        System.out.println("---");
        System.out.println();


        System.out.println("Эпики менеджера 1");
        System.out.println(taskManager.getAllEpics());

        System.out.println();
        System.out.println();

        System.out.println("Эпики менеджера 2");
        System.out.println(taskManager2.getAllEpics());


        System.out.println();
        System.out.println("---");
        System.out.println();


        System.out.println("История менеджера 1");
        System.out.println(taskManager.getHistory());

        System.out.println();
        System.out.println();

        System.out.println("История менеджера 2");
        System.out.println(taskManager2.getHistory());


        System.out.println();
        System.out.println("---");
        System.out.println();


        System.out.println("Тест: сравнение историй менеджеров");
        System.out.println(taskManager.getHistory().toString().equals(taskManager2.getHistory().toString()));
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        List<String> fileContent;
        try {
            fileContent = Files.readAllLines(Path.of(file.getPath()));
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения файла", e);
        }
        Map<Integer, Task> allTypesOfTasks = new HashMap<>();
        int maxId = 1;
        for (int i = 1; i < fileContent.size() - 2; i++) {
            Task task = CSVTaskFormat.taskFromString(fileContent.get(i));
            allTypesOfTasks.put(task.getId(), task);
            taskManager.setIdCounter(task.getId());
            switch (task.getType()) {
                case TASK:
                    taskManager.createTask(task);
                    break;
                case EPIC:
                    taskManager.createEpic((Epic) task);
                    break;
                case SUBTASK:
                    taskManager.createSubtask((Subtask) task);
                    break;
            }
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        taskManager.setIdCounter(maxId);

        List<Integer> taskIdsFromHistory = CSVTaskFormat.historyFromString(fileContent.get(fileContent.size() - 1));
        for (Integer taskId : taskIdsFromHistory) {
            taskManager.historyManager.addTask(allTypesOfTasks.get(taskId));
        }
        return taskManager;
    }

    public File getFile() {
        return file;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            writeAllTypesOfTasks(writer, super.getAllTasks());
            writeAllTypesOfTasks(writer, super.getAllEpics());
            writeAllTypesOfTasks(writer, super.getAllSubtasks());
            writer.newLine();
            writer.write(CSVTaskFormat.toString(super.getHistory()));
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи", exception);
        }
    }

    private <T extends Task> void writeAllTypesOfTasks(Writer writer, List<T> allTypesOfTasks) throws IOException {
        for (T task : allTypesOfTasks) {
            writer.write(CSVTaskFormat.toString(task) + "\n");
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }
}
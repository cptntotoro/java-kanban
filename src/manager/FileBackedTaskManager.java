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

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerReadException {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        List<String> fileContent;
        try {
            fileContent = Files.readAllLines(Path.of(file.getPath()));
        } catch (IOException e) {
            throw new ManagerReadException("File reading error.", e);
        }

        if(fileContent.size() < 3) {
            return taskManager;
        }

        Map<Integer, Task> allTypesOfTasks = new HashMap<>();
        int maxId = 1;
        for (int i = 1; i < fileContent.size(); i++) {
            if (fileContent.get(i).isEmpty()) {
                break;
            }
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

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            writeAllTypesOfTasks(writer, getAllTasks());
            writeAllTypesOfTasks(writer, getAllEpics());
            writeAllTypesOfTasks(writer, getAllSubtasks());
            writer.newLine();
            writer.write(CSVTaskFormat.toString(super.getHistory()));
        } catch (IOException exception) {
            throw new ManagerSaveException("File save error.", exception);
        }
    }

    private <T extends Task> void writeAllTypesOfTasks(Writer writer, List<T> allTypesOfTasks) throws IOException {
        for (T task : allTypesOfTasks) {
            writer.write(CSVTaskFormat.toString(task) + "\n");
        }
    }

    @Override
    public Integer createTask(Task task) {
        Integer id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public Integer createEpic(Epic epic) {
        Integer id = super.createEpic(epic);
        save();
        return id;
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
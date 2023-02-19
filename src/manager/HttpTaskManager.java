package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.clients.KVClient;
import manager.utils.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTaskManager {

    private final Gson gson;
    private final KVClient client;

    public HttpTaskManager(int port) {
        this(port, false);
    }

    public HttpTaskManager(int port, boolean load) {
        super(null);
        gson = Managers.getGson();
        client = new KVClient(port);
        if (load) {
            load();
        }
    }

    private <T extends Task> void addTasks(List<T> tasks) {
        for(Task task : tasks) {
            int id = task.getId();
            if(id > getIdCounter()) {
                setIdCounter(id);
            }
            switch (task.getType()) {
                case TASK:
                    this.tasks.put(id, task);
                    updatePrioritizedTasks(task);
                    break;
                case SUBTASK:
                    this.subtasks.put(id, (Subtask) task);
                    updatePrioritizedTasks(task);
                    break;
                case EPIC:
                    this.epics.put(id, (Epic) task);
                    break;
            }
        }
    }

    @Override
    public void load() {
        List<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {}.getType());
        List<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {}.getType());
        List<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {}.getType());
        addTasks(tasks);
        addTasks(epics);
        addTasks(subtasks);

        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {}.getType());
        if(!history.isEmpty()) {
            tasks.addAll(epics);
            tasks.addAll(subtasks);

            Map<Integer, Task> tasksMap = new HashMap<>();
            for (Task task : tasks) {
                tasksMap.put(task.getId(), task);
            }

            for(Integer taskId: history) {
                historyManager.addTask(tasksMap.get(taskId));
            }
        }
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);

        List<Integer> taskIds= new ArrayList<>();
        historyManager.getHistory().forEach(task -> taskIds.add(task.getId()));
        String jsonHistory = gson.toJson(taskIds);
        client.put("history", jsonHistory);
    }
}
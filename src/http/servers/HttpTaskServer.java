package http.servers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.interfaces.TaskManager;
import manager.utils.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private final int PORT = 8080;

    HttpServer httpServer;

    TaskManager taskManager;

    Gson gson;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TaskManagerHandler());
    }

    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
                return new JsonPrimitive(formatter.format(localDateTime));
            }
        });
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); }
        });
        return gsonBuilder.create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);

    }

    public void stop() {
        System.out.println("Выключаем сервер на порту " + PORT);
        httpServer.stop(0);
    }
    class TaskManagerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                final String path = httpExchange.getRequestURI().getPath().substring(6);
                String response;
                switch (path) {
                    case "":
                    case "/":
                        if (!httpExchange.getRequestMethod().equals("GET")) {
                            System.out.println("/ Ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendSuccessfulResponse(httpExchange, response);
                        break;
                    case "/task":
                        handleTask(httpExchange);
                        break;
                    case "/subtask":
                        handleSubtask(httpExchange);
                        break;
                    case "/epic":
                        handleEpic(httpExchange);
                        break;
                    case "/subtask/epic":
                        if (!httpExchange.getRequestMethod().equals("GET")) {
                            System.out.println("/subtask/epic Ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        String rawQuery = httpExchange.getRequestURI().getRawQuery();
                        if (rawQuery != null && !rawQuery.isEmpty()) {
                            System.out.println("GET-запрос с непустым id вызывает getSubtasksOfEpic().");
                            String epicIdString = rawQuery.substring(3);
                            int epicId = Integer.parseInt(epicIdString);
                            response = gson.toJson(taskManager.getSubtasksOfEpic(epicId));
                            sendSuccessfulResponse(httpExchange, response);
                        }
                        break;
                    case "/history":
                        if (!httpExchange.getRequestMethod().equals("GET")) {
                            System.out.println("/ Ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        response = gson.toJson(taskManager.getHistory());
                        sendSuccessfulResponse(httpExchange, response);
                        break;


                    default:
                        System.out.println("Неизвестный запрос: " + httpExchange.getRequestURI());
                        httpExchange.sendResponseHeaders(404, 0);

                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            httpExchange.close();
        }
    }

    private void handleEpic(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        switch (method) {
            case "GET":
                if (rawQuery != null && !rawQuery.isEmpty()) {
                    System.out.println("GET-запрос с непустым id вызывает getEpicById().");
                    String epicIdString = rawQuery.substring(3);
                    int epicId = Integer.parseInt(epicIdString);
                    final String response = gson.toJson(taskManager.getEpicById(epicId));
                    sendSuccessfulResponse(httpExchange, response);
                } else {
                    System.out.println("GET-запрос с пустым id вызывает getAllEpics().");
                    final String response = gson.toJson(taskManager.getAllEpics());
                    sendSuccessfulResponse(httpExchange, response);
                }
                break;
            case "DELETE":
                if (rawQuery != null && !rawQuery.isEmpty()) {
                    System.out.println("DELETE-запрос с непустым id вызывает deleteEpicById().");
                    String epicIdString = rawQuery.substring(3);
                    int epicId = Integer.parseInt(epicIdString);
                    taskManager.deleteEpicById(epicId);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("DELETE-запрос с пустым id вызывает deleteAllEpics().");
                    taskManager.deleteAllEpics();
                    httpExchange.sendResponseHeaders(200, 0);
                }
                break;
            case "POST":
                String json = new String(httpExchange.getRequestBody().readAllBytes());
                if(json.isEmpty()) {
                    System.out.println("Body с задачей пустой.");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                Epic epic = gson.fromJson(json, Epic.class);
                Integer epicId = epic.getId();
                if(epicId != null) {
                    taskManager.updateEpic(epic);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.createEpic(epic);
                    final String response = gson.toJson(epic);
                    sendSuccessfulResponse(httpExchange, response);
                }
                break;
        }
    }

    private void handleSubtask(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        switch (method) {
            case "GET":
                if (rawQuery != null && !rawQuery.isEmpty()) {
                    System.out.println("GET-запрос с непустым id вызывает getSubtaskById().");
                    String subtaskIdString = rawQuery.substring(3);
                    int subtaskId = Integer.parseInt(subtaskIdString);
                    final String response = gson.toJson(taskManager.getSubtaskById(subtaskId));
                    sendSuccessfulResponse(httpExchange, response);
                } else {
                    System.out.println("GET-запрос с пустым id вызывает getAllSubtasks().");
                    final String response = gson.toJson(taskManager.getAllSubtasks());
                    sendSuccessfulResponse(httpExchange, response);
                }
                break;
            case "DELETE":
                if (rawQuery != null && !rawQuery.isEmpty()) {
                    System.out.println("DELETE-запрос с непустым id вызывает deleteSubtaskById().");
                    String subtaskIdString = rawQuery.substring(3);
                    int subtaskId = Integer.parseInt(subtaskIdString);
                    taskManager.deleteSubtaskById(subtaskId);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("DELETE-запрос с пустым id вызывает deleteAllSubtasks().");
                    taskManager.deleteAllSubtasks();
                    httpExchange.sendResponseHeaders(200, 0);
                }
                break;
            case "POST":
                String json = new String(httpExchange.getRequestBody().readAllBytes());
                if(json.isEmpty()) {
                    System.out.println("Body с задачей пустой.");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                Subtask subtask = gson.fromJson(json, Subtask.class);
                Integer subtaskId = subtask.getId();
                if(subtaskId != null) {
                    taskManager.updateSubtask(subtask);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.createSubtask(subtask);
                    final String response = gson.toJson(subtask);
                    sendSuccessfulResponse(httpExchange, response);
                }
                break;
        }
    }

    private void handleTask(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        switch (method) {
            case "GET":
                if (rawQuery != null && !rawQuery.isEmpty()) {
                    System.out.println("GET-запрос с непустым id вызывает getTaskById().");
                    String taskIdString = rawQuery.substring(3);
                    int taskId = Integer.parseInt(taskIdString);
                    final String response = gson.toJson(taskManager.getTaskById(taskId));
                    sendSuccessfulResponse(httpExchange, response);
                } else {
                    System.out.println("GET-запрос с пустым id вызывает getAllTasks().");
                    final String response = gson.toJson(taskManager.getAllTasks());
                    sendSuccessfulResponse(httpExchange, response);
                }
                break;
            case "DELETE":
                if (rawQuery != null && !rawQuery.isEmpty()) {
                    System.out.println("DELETE-запрос с непустым id вызывает deleteTaskById().");
                    String taskIdString = rawQuery.substring(3);
                    int taskId = Integer.parseInt(taskIdString);
                    taskManager.deleteTaskById(taskId);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("DELETE-запрос с пустым id вызывает deleteAllTasks().");
                    taskManager.deleteAllTasks();
                    httpExchange.sendResponseHeaders(200, 0);
                }
                break;
            case "POST":
                String json = new String(httpExchange.getRequestBody().readAllBytes());
                if(json.isEmpty()) {
                    System.out.println("Body с задачей пустой.");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                Task task = gson.fromJson(json, Task.class);
                Integer taskId = task.getId();
                if(taskId != null) {
                    taskManager.updateTask(task);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.createTask(task);
                    final String response = gson.toJson(task);
                    sendSuccessfulResponse(httpExchange, response);
                }
                break;
        }
    }

    private void sendSuccessfulResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        if (response != null && !response.isEmpty()) {
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
        }
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}

package http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import http.servers.HttpTaskServer;
import http.servers.KVServer;
import manager.utils.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;

    @BeforeEach
    public void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    protected void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void endpointTasksTaskPost() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        Gson gson = Managers.getGson();
        Task task = new Task("Task1", "Description1", LocalDateTime.of(2000, 6, 1, 1, 1), 60);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }
        Task task2 = gson.fromJson(response.body(), Task.class);
        task.setId(task2.getId());
        assertEquals(task, task2, "Таски не равны");
    }


    @Test
    public void endpointTasksTaskIdGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 6, 1, 1, 1), 60));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }
        Task task1 = gson.fromJson(response.body(), Task.class);

        url = URI.create("http://localhost:8080/tasks/task?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }
        Task task2 = gson.fromJson(response.body(), Task.class);
        assertEquals(task1, task2, "Таски не равны");
    }


    @Test
    public void endpointTasksTaskIdDelete() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 6, 1, 1, 1), 60));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/task?id=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }
        assertEquals(response.statusCode(), 200, "Не удалено");
        assertNull(httpTaskServer.getTaskManager().getTaskById(1), "Таска не удалена");
    }

    @Test
    public void endpointTasksTaskGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 6, 1, 1, 1), 60));
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 7, 1, 1, 1), 60));
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());

        assertIterableEquals(httpTaskServer.getTaskManager().getAllTasks(), tasks, "Таски не равны");
        assertEquals(2, tasks.size(), "Кол-во тасок отличается");
    }




    @Test
    public void endpointTasksHistoryGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 6, 1, 1, 1), 60));
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            client.send(request, HttpResponse.BodyHandlers.ofString());
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 7, 1, 1, 1), 60));
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 8, 1, 1, 1), 60));
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }


        url = URI.create("http://localhost:8080/tasks/task?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        url = URI.create("http://localhost:8080/tasks/task?id=3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        url = URI.create("http://localhost:8080/tasks/task?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());
        List<Integer> taskIds = new ArrayList<>();
        taskIds.add(1);
        taskIds.add(3);
        taskIds.add(2);
        for(int i = 0; i < taskIds.size(); i++) {
            assertEquals(taskIds.get(i), tasks.get(i).getId(), "Порядок вызова тасок не верен");
        }
        assertEquals(3, tasks.size(), "Кол-во тасок отличается");
    }

    @Test
    public void endpointTasksGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 9, 1, 1, 1), 60));
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            client.send(request, HttpResponse.BodyHandlers.ofString());
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 10, 1, 1, 1), 60));
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        json = gson.toJson(new Task("Task1", "Description1", LocalDateTime.of(2000, 8, 1, 1, 1), 60));
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());
        List<Integer> taskIds = new ArrayList<>();
        taskIds.add(3);
        taskIds.add(1);
        taskIds.add(2);
        for(int i = 0; i < taskIds.size(); i++) {
            assertEquals(taskIds.get(i), tasks.get(i).getId(), "Порядок вызова тасок не верен");
        }
        assertEquals(3, tasks.size(), "Кол-во тасок отличается");
    }

//    тесты на эпики были реализованы в рамках проверки менедженров,
//    т.к. сами эпики ничего специфического из себя не представляют,
//    весь отличающийся функционал, вроде расчета времени исполнения или статуса, реализован в менеджерах
//    какие именно "тесты на эпик" вы имеете в виду?
    @Test
    public void endpointTasksEpicPost() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();
        Epic task = new Epic("Task1", "Description1");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        Epic task2 = gson.fromJson(response.body(), Epic.class);
        task.setId(task2.getId());

        assertEquals(task, task2, "Таски не равны");
    }

    @Test
    public void endpointTasksEpicIdGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Epic("Task1", "Description1"));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }
        Epic task1 = gson.fromJson(response.body(), Epic.class);

        url = URI.create("http://localhost:8080/tasks/epic?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }
        Epic task2 = gson.fromJson(response.body(), Epic.class);
        assertEquals(task1, task2, "Таски не равны");
    }

    @Test
    public void endpointTasksEpicIdDelete() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Epic("Task1", "Description1"));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/epic?id=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }
        assertEquals(response.statusCode(), 200, "Не удалено");
        assertNull(httpTaskServer.getTaskManager().getEpicById(1), "Таска не удалена");
    }

    @Test
    public void endpointTasksEpicGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        String json = gson.toJson(new Epic("Task1", "Description1"));
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        json = gson.toJson(new Epic("Task1", "Description1"));
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        List<Epic> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {}.getType());

        assertIterableEquals(httpTaskServer.getTaskManager().getAllEpics(), tasks, "Таски не равны");
        assertEquals(2, tasks.size(), "Кол-во тасок отличается");
    }

    @Test
    public void endpointTasksSubtaskPost() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();
        Epic task = new Epic("Task1", "Description1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask");

        Subtask subtask = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 10, 1, 1, 1), 60);
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        Subtask subtask2 = gson.fromJson(response.body(), Subtask.class);
        subtask.setId(subtask2.getId());

        assertEquals(subtask, subtask2, "Таски не равны");
    }

    @Test
    public void endpointTasksSubtaskIdGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        Epic task = new Epic("Task1", "Description1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask");

        Subtask subtask = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 10, 1, 1, 1), 60);
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }
        subtask = gson.fromJson(response.body(), Subtask.class);

        url = URI.create("http://localhost:8080/tasks/subtask?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }
        Subtask subtask2 = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask, subtask2, "Таски не равны");
    }

    @Test
    public void endpointTasksSubtaskIdDelete() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        Epic task = new Epic("Task1", "Description1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask");

        Subtask subtask = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 10, 1, 1, 1), 60);
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask?id=2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }
        assertEquals(response.statusCode(), 200, "Не удалено");
        assertNull(httpTaskServer.getTaskManager().getSubtaskById(2), "Таска не удалена");
    }

    @Test
    public void endpointTasksSubtaskGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        Epic task = new Epic("Task1", "Description1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask");

        Subtask subtask = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 10, 1, 1, 1), 60);
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        Subtask subtask2 = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 11, 1, 1, 1), 60);
        json = gson.toJson(subtask2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        List<Subtask> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {}.getType());

        assertIterableEquals(httpTaskServer.getTaskManager().getAllSubtasks(), tasks, "Таски не равны");
        assertEquals(2, tasks.size(), "Кол-во тасок отличается");
    }

    @Test
    public void endpointTasksSubtaskEpicIdGet() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        Gson gson = Managers.getGson();

        Epic task = new Epic("Task1", "Description1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask");

        Subtask subtask = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 10, 1, 1, 1), 60);
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        Subtask subtask2 = new Subtask("Task1", "Description1", 1, LocalDateTime.of(2000, 11, 1, 1, 1), 60);
        json = gson.toJson(subtask2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос провален");
        }

        url = URI.create("http://localhost:8080/tasks/subtask/epic?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            fail("Запрос endpointTasksTask провален");
        }

        List<Subtask> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {}.getType());

        assertIterableEquals(httpTaskServer.getTaskManager().getAllSubtasks(), tasks, "Таски не равны");
        assertIterableEquals(httpTaskServer.getTaskManager().getSubtasksOfEpic(1), tasks, "Таски не равны");
        assertEquals(2, tasks.size(), "Кол-во тасок отличается");
    }
}
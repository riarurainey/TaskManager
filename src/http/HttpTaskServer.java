package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                                LocalDateTime.parse(json.getAsString())
                )
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, context) ->
                                new JsonPrimitive(src.toString())
                ).create();

        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/subtask", new SubTaskHandler());
        server.createContext("/tasks/subtask/epic", new EpicsSubTaskHandler());
        server.createContext("/tasks/history", new HistoryHandler());
        server.createContext("/tasks/", new PriorityHandler());
    }

    public final class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String rawQuery = exchange.getRequestURI().getRawQuery();

                if (path.endsWith("/task") & rawQuery == null) {
                    switch (method) {
                        case "GET" -> {
                            System.out.println("Получаем все задачи/эпики/подзадачи");
                            if (taskManager.getAllTasks().size() == 0) {
                                sendCode(exchange, 405);
                            } else {
                                send(exchange, taskManager.getAllTasks(), 200);
                            }
                        }
                        case "DELETE" -> {
                            System.out.println("Удаляем все задачи/эпики/подзадачи");
                            if (taskManager.getAllTasks().size() == 0) {
                                sendCode(exchange, 405);
                            } else {
                                taskManager.deleteAllTasks();
                                sendCode(exchange, 200);
                            }
                        }
                        case "POST" -> {
                            System.out.println("Создаем задачу");
                            String createTaskJson = readText(exchange);
                            Task task = gson.fromJson(createTaskJson, Task.class);
                            send(exchange, taskManager.createTask(task), 201);
                        }
                        case "PUT" -> {
                            System.out.println("Обновляем задачу");
                            String updateTaskJson = readText(exchange);
                            Task task1 = gson.fromJson(updateTaskJson, Task.class);
                            send(exchange, taskManager.updateTask(task1), 201);
                        }
                        default -> sendCode(exchange, 404);
                    }
                }

                if (rawQuery != null && rawQuery.startsWith("id=")) {

                    long id = parseIdFromString(rawQuery);

                    switch (method) {
                        case "GET" -> {
                            System.out.println("Ищем задачу по id");
                            if (taskManager.findTaskById(id) != null) {
                                send(exchange, taskManager.findTaskById(id), 200);
                            } else {
                                sendCode(exchange, 405);
                            }
                        }
                        case "DELETE" -> {
                            System.out.println("Удаляем задачу по id");
                            if (taskManager.findTaskById(id) != null) {
                                send(exchange, taskManager.deleteTaskById(id), 200);
                            } else {
                                sendCode(exchange, 405);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String rawQuery = exchange.getRequestURI().getRawQuery();

                if (path.endsWith("/epic") & rawQuery == null) {
                    switch (method) {
                        case "POST" -> {
                            System.out.println("Создаем эпик");
                            String createEpicJson = readText(exchange);
                            Epic epic = gson.fromJson(createEpicJson, Epic.class);
                            send(exchange, taskManager.createEpic(epic), 201);
                        }
                        case "PUT" -> {
                            System.out.println("Обновляем эпик");
                            String updateEpicJson = readText(exchange);
                            Epic epic1 = gson.fromJson(updateEpicJson, Epic.class);
                            send(exchange, taskManager.updateEpic(epic1), 201);
                        }
                        default -> sendCode(exchange, 404);
                    }
                }

                if (rawQuery != null && rawQuery.startsWith("id=")) {

                    long id = parseIdFromString(rawQuery);

                    switch (method) {
                        case "GET" -> {
                            System.out.println("Ищем эпик по id");
                            if (taskManager.findEpicById(id) != null) {
                                send(exchange, taskManager.findEpicById(id), 200);
                            } else {
                                sendCode(exchange, 405);
                            }
                        }
                        case "DELETE" -> {
                            System.out.println("Удаляем эпик по id");
                            if (taskManager.findEpicById(id) != null) {
                                send(exchange, taskManager.deleteEpicById(id), 200);
                            } else {
                                sendCode(exchange, 405);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final class SubTaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String rawQuery = exchange.getRequestURI().getRawQuery();

                if (path.endsWith("/subtask") & rawQuery == null) {
                    switch (method) {
                        case "POST" -> {
                            System.out.println("Создаем подзадачу");
                            String createSubtaskJson = readText(exchange);
                            SubTask subTask = gson.fromJson(createSubtaskJson, SubTask.class);
                            send(exchange, taskManager.createSubTask(subTask), 201);
                        }
                        case "PUT" -> {
                            System.out.println("Обновляем подзадачу");
                            String updateSubTaskJson = readText(exchange);
                            SubTask subTask1 = gson.fromJson(updateSubTaskJson, SubTask.class);
                            send(exchange, taskManager.updateSubTask(subTask1), 201);
                        }
                        default -> sendCode(exchange, 404);
                    }
                }

                if (rawQuery != null && rawQuery.startsWith("id=")) {

                    long id = parseIdFromString(rawQuery);

                    switch (method) {
                        case "GET" -> {
                            System.out.println("Ищем подзадачу по id");
                            if (taskManager.findSubTaskById(id) != null) {
                                send(exchange, taskManager.findSubTaskById(id), 200);
                            } else {
                                sendCode(exchange, 405);
                            }
                        }
                        case "DELETE" -> {
                            System.out.println("Удаляем подзадачу по id");
                            if (taskManager.findSubTaskById(id) != null) {
                                send(exchange, taskManager.deleteSubTaskById(id), 200);
                            } else {
                                sendCode(exchange, 405);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final class EpicsSubTaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String rawQuery = exchange.getRequestURI().getRawQuery();

                if (path.endsWith("tasks/subtask/epic/") & rawQuery.startsWith("id=")) {
                    long id = parseIdFromString(rawQuery);
                    if ("GET".equals(method)) {
                        if (taskManager.getEpicsSubtask(id).size() != 0) {
                            send(exchange, taskManager.getEpicsSubtask(id), 200);
                        } else {
                            sendCode(exchange, 405);
                        }
                    } else {
                        sendCode(exchange, 404);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if (path.endsWith("tasks/history")) {
                    if ("GET".equals(method)) {
                        if (taskManager.history().size() != 0) {
                            send(exchange, taskManager.history(), 200);
                        } else {
                            sendCode(exchange, 405);
                        }
                    } else {
                        sendCode(exchange, 404);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final class PriorityHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if (path.endsWith("/tasks/")) {
                    if ("GET".equals(method)) {
                        if (taskManager.getPrioritizedTasks().size() != 0) {
                            send(exchange, taskManager.getPrioritizedTasks(), 200);
                        } else {
                            sendCode(exchange, 405);
                        }
                    } else {
                        sendCode(exchange, 404);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void send(HttpExchange h, Object object, int code) throws IOException {
        String json = gson.toJson(object);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, json.length());
        h.getResponseBody().write(json.getBytes(UTF_8));
    }

    protected void sendCode(HttpExchange h, int code) throws IOException {
        h.sendResponseHeaders(code, 0);

    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    long parseIdFromString(String parameters) {
        String[] arr = parameters.split("=");

        try {
            return Long.parseLong(arr[1]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Ошибка в передачи корректного id");
        }
    }
}
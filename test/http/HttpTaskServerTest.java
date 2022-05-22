package http;

import com.google.gson.*;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private TaskManager taskManager;
    private HttpTaskServer server;
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<String> response;
    private final static int CODE200_OK = 200;
    private final static int CODE201_CREATED = 201;
    private final static int CODE404_NOT_FOUND = 404;
    private final static int CODE405_METHOD_NOT_ALLOWED = 405;
    private static Gson gson = new Gson();
    public Task task;
    public Epic epic;
    public SubTask subTask1;

    @BeforeEach
    public void beforeEach() throws IOException {
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
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
        client = HttpClient.newHttpClient();
        createTestTasks();

    }

    @AfterEach
    void stop() {
        taskManager.deleteAllTasks();
        server.stop();
    }

    public void createTestTasks() {
        task = new Task("taskTest", "desc", 1L, 15L, LocalDateTime.now());
        taskManager.createTask(task);

        epic = new Epic("epicTest", "desc", 2L, 15L, task.getEndTime());
        taskManager.createEpic(epic);
        long epicId = epic.getId();

        subTask1 = new SubTask("ST1", "desc", 3L, Status.NEW, epicId, 15L, task.getEndTime());
        taskManager.createSubTask(subTask1);
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE404_When_Wrong_Method() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Приходит неверный статус код при неверном запросе");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE405_When_TaskList_Is_Empty() throws IOException, InterruptedException {
        taskManager.deleteAllTasks();
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код при пустом списке задач");
    }


    @Test
    void endPoint_Tasks_Task_Should_Return_CODE200_When_TaskList_Is_Not_Empty()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(response.body(), "Тело метода пустое, список задач не вернулся");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код при пустом списке задач");
    }


    @Test
    void endPoint_Tasks_Task_Should_Return_CODE201_When_Create_New_Task() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        Task task = new Task("CreateTestTask", "desc", 5L, 15L,
                LocalDateTime.of(2015, 10, 5, 10, 0));
        String jsonTask = gson.toJson(task);
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE201_CREATED, response.statusCode(),
                "Приходит неверный статус код, когда создаем корректную задачу");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE200_When_Delete_All_Task() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не удалены");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код удалении всех задач");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE405_When_Delete_Empty_Task_List()
            throws IOException, InterruptedException {
        taskManager.deleteAllTasks();
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().
                header("content-type", "application/json").
                uri(url).
                DELETE().
                build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код при пустом списке задач");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE201_When_Update_Task() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        client = HttpClient.newHttpClient();
        task.setName("NewName");
        task.setDescription("new Desc");
        task.setStatus(Status.IN_PROGRESS);
        Task otherTask = taskManager.findTaskById(task.getId());
        String jsonTask = gson.toJson(taskManager.updateTask(task));
        request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .header("content-type", "application/json")
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(task, otherTask, "Задачи не совпадают");
        assertEquals(CODE201_CREATED, response.statusCode(),
                "Приходит неверный статус код, когда обновляем корректную задачу");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE200_When_Find_Task_By_Id() throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/task/?id=%s", task.getId());
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFind = gson.fromJson(response.body(), Task.class);

        assertEquals(task, taskFind, "Задачи не сходятся");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код, когда получаем корректную задачу по id");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE405_When_Find_Task_By_None_Exist_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/task/?id=%s", 100);
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код, когда получаем корректную задачу по id");
    }


    @Test
    void endPoint_Tasks_Task_Should_Return_CODE200_When_Delete_Task_By_Id() throws IOException, InterruptedException {
        long taskId = task.getId();
        String str = String.format("http://localhost:8080/tasks/task/?id=%s", taskId);
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.findTaskById(taskId), "Задача не удалена");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код, когда удаляем корректную задачу по id");
    }

    @Test
    void endPoint_Tasks_Task_Should_Return_CODE405_When_Delete_Task_By_None_Exist_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/task/?id=%s", 100);
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код, когда удаляем корректную задачу по id");
    }


    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE201_When_Create_New_Epic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        client = HttpClient.newHttpClient();
        Epic epic = new Epic("CreateTestEpic", "desc", 100L, 15L,
                LocalDateTime.of(2016, 10, 5, 10, 0));
        String jsonTask = gson.toJson(epic);
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CODE201_CREATED, response.statusCode(),
                "Приходит неверный статус код, когда создаем корректный эпик");
    }


    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE201_When_Update_Epic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        client = HttpClient.newHttpClient();
        epic.setName("NewName");
        epic.setDescription("new Desc");
        Epic otherEpic = taskManager.findEpicById(epic.getId());
        String jsonTask = gson.toJson(taskManager.updateEpic(epic));
        request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .header("content-type", "application/json")
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epic, otherEpic, "Эпики не совпадают");
        assertEquals(CODE201_CREATED, response.statusCode(),
                "Приходит неверный статус код, когда обновляем корректную эпик");

    }

    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE200_When_Find_Epic_By_Id() throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/epic/?id=%s", epic.getId());
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFind = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic, epicFind, "Задачи не сходятся");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код, когда получаем корректный эпик по id");

    }

    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE405_When_Find_Epic_By_None_Exist_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/epic/?id=%s", 100);
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код, когда получаем корректный эпик по id");

    }

    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE200_When_Delete_Epic_By_Id() throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/epic/?id=%s", epic.getId());
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertNull(taskManager.findEpicById(epic.getId()), "Задача не удалена");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код, когда удаляем корректную задачу по id");

    }

    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE200_When_Delete_Epic_By_None_Exist_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/epic/?id=%s", 100);
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код, когда удаляем корректную задачу по id");

    }

    @Test
    void endPoint_Tasks_Epic_Should_Return_CODE404_When_Wrong_Method() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Приходит неверный статус код при неверном запросе");
    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE201_When_Create_New_SubTask()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        client = HttpClient.newHttpClient();
        SubTask subtask = new SubTask("CreateTestSubTask", "desc", epic.getId(), 15L,
                LocalDateTime.of(2014, 10, 5, 10, 0));
        String jsonTask = gson.toJson(subtask);
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE201_CREATED, response.statusCode(),
                "Приходит неверный статус код, когда создаем корректную подзадачу");
    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE201_When_Update_SubTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        client = HttpClient.newHttpClient();
        subTask1.setName("NewName");
        subTask1.setDescription("new Desc");
        SubTask otherSubTask = taskManager.findSubTaskById(subTask1.getId());
        String jsonTask = gson.toJson(taskManager.updateSubTask(subTask1));
        request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .header("content-type", "application/json")
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(subTask1, otherSubTask, "Эпики не совпадают");
        assertEquals(CODE201_CREATED, response.statusCode(),
                "Приходит неверный статус код, когда обновляем корректную подзадачу");

    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE200_When_Find_SubTask_By_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/?id=%s", subTask1.getId());
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        SubTask subTask = taskManager.findSubTaskById(subTask1.getId());
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFind = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask, subTaskFind, "Задачи не сходятся");

        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код, когда получаем корректную подзадачу по id");

    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE405_When_Find_SubTask_None_Exist_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/?id=%s", 100);
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код, когда получаем корректную подзадачу по id");

    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE200_When_Delete_SubTask_By_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/?id=%s", subTask1.getId());
        URI url = URI.create(str);
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.findSubTaskById(3L), "Задача не удалена");
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный статус код, когда удаляем корректную задачу по id");

    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE405_When_Delete_SubTask_None_Exist_Id()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/?id=%s", 100);
        URI url = URI.create(str);
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный статус код, когда удаляем корректную задачу по id");

    }

    @Test
    void endPoint_Tasks_SubTask_Should_Return_CODE404_When_Wrong_Method() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/subtask");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Приходит неверный статус код при неверном запросе");
    }

    @Test
    void endPoint_Tasks_SubTask_Epic_Should_Return_CODE200_When_Get_Epics_SubTask()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/epic/?id=%s", epic.getId());
        URI url = URI.create(str);
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный код при возвращении подзадач эпика");

    }

    @Test
    void endPoint_Tasks_SubTask_Epic_Should_Return_CODE405_When_Get_Empty_Epics_SubTask()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/epic/?id=%s", epic.getId());
        URI url = URI.create(str);
        taskManager.deleteSubTaskById(subTask1.getId());
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный код при возвращении пустых подзадач эпика");

    }

    @Test
    void endPoint_Tasks_SubTask_Epic_Should_Return_CODE404_When_Wrong_Method()
            throws IOException, InterruptedException {
        String str = String.format("http://localhost:8080/tasks/subtask/epic/?id=%s", epic.getId());
        URI url = URI.create(str);
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Приходит неверный статус код при неверном запросе");
    }

    @Test
    void endPoint_Tasks_History_Should_Return_CODE200_When_Get_Not_Empty_History()
            throws IOException, InterruptedException {
        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findSubTaskById(subTask1.getId());

        URI url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный код при возвращении корректной истории");

    }

    @Test
    void endPoint_Tasks_History_Should_Return_CODE405_When_Get_Empty_History()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный код при возвращении корректной истории");

    }

    @Test
    void endPoint_Tasks_History_Should_Return_CODE404_When_Wrong_Method() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Приходит неверный статус код при неверном запросе");
    }

    @Test
    void endPoint_Tasks_Priority_Should_Return_CODE200_When_Get_Priority_Set()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode(),
                "Приходит неверный код при возвращении множества задач отсортированных по приоритету времени");

    }

    @Test
    void endPoint_Tasks_Priority_Should_Return_CODE405_When_Get_Empty_Priority_Set()
            throws IOException, InterruptedException {
        taskManager.deleteAllTasks();
        URI url = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Приходит неверный код при пустого множества задач отсортированных по приоритету времени");

    }

    @Test
    void endPoint_Tasks_Priority_Should_Return_CODE404_When_Wrong_Method() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Приходит неверный статус код при неверном запросе");
    }
}


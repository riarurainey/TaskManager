package http;

import managers.TaskManagerTest;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;
    private final static String url = "http://localhost:8078";

    @BeforeEach
    void start() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(url, false);
        createTestTasks();
        taskManager = new HttpTaskManager(url, true);
    }

    @AfterEach
    void stop() {
        kvServer.stop();
    }

    @Test
    void should_Return_All_Tasks_When_IsStartLoad_True() {
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "Не возвращаются все задачи при использовании метода load");
        assertNotNull(tasks, "Возвращается пустой список всех задач");

    }

    @Test
    void should_Return_All_History_When_IsStartLoad_True() {
        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findSubTaskById(subTask1.getId());
        assertEquals(3, taskManager.history().size(), "История возвращает неверное количество задач");

    }
}

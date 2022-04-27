package managers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    Path path = Path.of("test.cvs");

    @BeforeEach
    public void beforeEach(){
        taskManager = new FileBackedTasksManager(path);
        createTestTasks();

    }
    @AfterEach
    public void afterEach(){
        taskManager.deleteAllTasks();
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void createInMemory() {

        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findSubTaskById(subTask1.getId());

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(path);

        List<Task> tasks = fileBackedTasksManager.getAllTasks();

        assertNotNull(tasks, "Возвращается пустой список всех задач");

        List<SubTask> subTasks = fileBackedTasksManager.getEpicsSubtask(epic.getId());
        assertNotNull(subTasks, "Возвращается пустой список всех подзадач");

        List<Task> historyList = fileBackedTasksManager.history();
        assertEquals(3, historyList.size(), "Возвращает неверное количество задач в истории");

    }

    @Test
    void save_and_Load_From_File_When_List_Of_All_Tasks_is_Empty() {
        FileBackedTasksManager fileBack1 = new FileBackedTasksManager(path);
        assertEquals(0, fileBack1.getAllTasks().size(), "Список задач не пустой");

        fileBack1.save();
        FileBackedTasksManager fileBack2 = FileBackedTasksManager.loadFromFile(path);
        assertEquals(fileBack1.getAllTasks().size(), fileBack2.getAllTasks().size(), "Количество сохраненных" +
                " и восстановленных задач не соответствуют");
    }

    @Test
    void save_and_Load_From_File_When_Epics_Without_Subtasks() {
        FileBackedTasksManager fileBack1 = new FileBackedTasksManager(path);
        Epic epicN = fileBack1.createEpic(new Epic("EpicName", "desc", 10L, LocalDateTime.now()));


        FileBackedTasksManager fileBack2 = FileBackedTasksManager.loadFromFile(path);
        Epic epic1 = fileBack2.epics.get(epicN.getId());
        assertEquals(epicN, epic1, "Эпики не соответствуют");
    }

    @Test
    void save_and_Load_When_History_is_Empty() {
        FileBackedTasksManager fileBack1 = new FileBackedTasksManager(path);

        FileBackedTasksManager fileBack2 = FileBackedTasksManager.loadFromFile(path);
        List<Task> historyList = fileBack2.history();
        assertEquals(0, historyList.size(), "История просмотров не пустая");

    }
}



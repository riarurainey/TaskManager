package managers;

import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    public InMemoryHistoryManager historyManager;
    public InMemoryTaskManager taskManager;
    public Task task;
    public Epic epic;
    public SubTask subTask;

    @BeforeEach
    void createTestTasks() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();

        task = new Task("taskTest", "desc", 15L, LocalDateTime.now());
        taskManager.createTask(task);
        epic = new Epic("epicTest", "desc", 15L, task.getEndTime());
        taskManager.createEpic(epic);
        long epicId = epic.getId();
        subTask = new SubTask("ST1", "desc", Status.NEW, epicId, 15L, task.getEndTime());
        taskManager.createSubTask(subTask);

    }

    @Test
    void add_Element_In_History() {
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "Элемент не добавлен");
    }

    @Test
    void add_Duplicate_Elements() {
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "Метод добавление в историю сохраняет дубликаты");

    }

    @Test
    void get_History_When_List_Of_Elements_Is_Empty() {
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
    }


    @Test
    void remove_From_History_First_Element() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(task.getId());

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории не уменьшился, элемент не удален");
        assertNotEquals(task, historyManager.getHistory().get(0),
                "Первый элемент не удалился из истории");

    }

    @Test
    void remove_From_History_Middle_Element() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(epic.getId());

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории не уменьшился, элемент не удален");
        assertNotEquals(epic, historyManager.getHistory().get(1),
                "Средний элемент не удалился из истории");

    }

    @Test
    void remove_From_History_Last_Element() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(subTask.getId());

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории не уменьшился, элемент не удален");
        assertNotEquals(subTask, historyManager.getHistory().get(historyManager.getHistory().size() - 1),
                "Последний элемент не удалился из истории");

    }
}
package managers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;
    public Task task;
    public Epic epic;
    public SubTask subTask1;
    public SubTask subTask2;
    public SubTask subTask3;


    public void createTestTasks() {
        task = new Task("taskTest", "desc", 15L, LocalDateTime.now());
        taskManager.createTask(task);
        epic = new Epic("epicTest", "desc", 15L, task.getEndTime());
        taskManager.createEpic(epic);
        long epicId = epic.getId();

        subTask1 = new SubTask("ST1", "desc", Status.NEW, epicId, 15L, task.getEndTime());
        taskManager.createSubTask(subTask1);

        subTask2 =
                new SubTask("ST2", "desc", Status.NEW, epicId, 13L, subTask1.getEndTime());
        taskManager.createSubTask(subTask2);

        subTask3 =
                new SubTask("ST3", "desc", Status.NEW, epicId, 13L, subTask2.getEndTime());
        taskManager.createSubTask(subTask3);
    }


    @Test
    void createTask() {
        Task task = new Task("testTask", "desc");
        taskManager.createTask(task);
        Task otherTask = taskManager.findTaskById(task.getId());

        assertNotNull(otherTask, "Задача не найдена");
        assertEquals(otherTask, task, "Задачи не совпадают");

    }

    @Test
    void createEpic() {
        Epic epic = new Epic("testEpic", "desc");
        taskManager.createEpic(epic);
        Epic otherEpic = taskManager.findEpicById(epic.getId());

        assertNotNull(otherEpic, "Эпик не найден");
        assertEquals(otherEpic, epic, "Эпики не совпадают");

    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("testEpic", "desc");
        taskManager.createEpic(epic);
        long epicId = epic.getId();

        SubTask subTask = new SubTask("SB", "desc", epicId);
        taskManager.createSubTask(subTask);

        SubTask otherSubTask = taskManager.findSubTaskById(subTask.getId());

        assertEquals(1, taskManager.getEpicsSubtask(epicId).size(), "Подзадача не создана");
        assertNotNull(otherSubTask, "Такой подзадачи нет");
        assertEquals(subTask, otherSubTask, "Задачи не совпадают");
        assertEquals(subTask.getEpicId(), epicId, "Эпик не найден");

    }

    @Test
    void findTaskById() {
        assertEquals(taskManager.findTaskById(task.getId()), task, "Задачи не совпадают");
        assertNull(taskManager.findTaskById(100), "Задача с таким id существует");
    }

    @Test
    void findEpicById() {
        assertEquals(taskManager.findEpicById(epic.getId()), epic, "Задачи не совпадают");
        assertNull(taskManager.findEpicById(100), "Эпик с таким id существует");
    }

    @Test
    void findSubTaskById() {
        assertEquals(taskManager.findSubTaskById(subTask1.getId()), subTask1, "Задачи не совпадают");
        assertNull(taskManager.findSubTaskById(100), "Подзадача с таким id существует");
    }


    @Test
    void deleteTaskById() {
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.findSubTaskById(task.getId()), "Задача не удалилась");
        assertNull(taskManager.deleteTaskById(10), "Задача с таким id существует");
    }

    @Test
    void deleteEpicById() {
        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.findEpicById(epic.getId()), "Эпик не удалился");
        assertNull(taskManager.deleteEpicById(100), "Эпик с таким id существует");
    }

    @Test
    void deleteSubTaskById() {
        taskManager.deleteSubTaskById(subTask1.getId());

        assertNull(taskManager.findSubTaskById(subTask1.getId()), "Подзадача не удалила");
        assertNull(taskManager.deleteSubTaskById(100), "Подзадача с таким id существует");
    }


    @Test
    void updateTask() {
        task.setName("NewName");
        task.setDescription("new Desc");
        task.setStatus(Status.IN_PROGRESS);
        task.setDuration(20L);
        task.setStartTime(LocalDateTime.now());
        taskManager.updateTask(task);

        Task otherTask = taskManager.findTaskById(task.getId());

        assertNotNull(otherTask, "Задача не найдена");
        assertEquals(task, otherTask, "Задачи не совпадают");

    }

    @Test
    void updateEpic() {
        epic.setName("NewName");
        epic.setDescription("new Desc");

        taskManager.updateEpic(epic);
        Epic otherEpic = taskManager.findEpicById(epic.getId());

        assertNotNull(otherEpic, "Эпик не найден");
        assertEquals(epic, otherEpic, "Эпики не совпадают");

    }


    @Test
    void updateSubTask() {
        subTask1.setName("NewName");
        subTask1.setDescription("new Desc");
        subTask1.setDuration(20L);
        subTask1.setStartTime(task.getEndTime());
        taskManager.updateSubTask(subTask1);
        SubTask otherSubTask = taskManager.findSubTaskById(subTask1.getId());

        assertNotNull(otherSubTask, "Подзадача не найдена");
        assertEquals(subTask1, otherSubTask, "Подзадачи не совпадают");

    }


    @Test
    void getAllTask() {
        List<Task> allTasks = taskManager.getAllTasks();
        assertNotNull(allTasks, "Не возвращаются задачи");
        assertEquals(2, allTasks.size(), "Количество задач неверное ");
        assertEquals(task, allTasks.get(0), "Задачи не совпадают");
    }


    @Test
    void getEpicsSubtask() {
        List<SubTask> subtasks = taskManager.getEpicsSubtask(epic.getId());
        assertNotNull(subtasks, "Не возвращаются подзадачи");
        assertEquals(3, epic.getSubTaskHashMap().size(), "Количество подзадач неверное ");
        assertEquals(subTask1, subtasks.get(0), "Задачи не совпадают");
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }


    @Test
    void history() {
        assertEquals(0, taskManager.history().size(), "История не пустая");

        taskManager.findTaskById(task.getId());
        assertEquals(1, taskManager.history().size(), "Количество просмотров неверное");

        assertFalse(taskManager.history().contains(subTask1),
                "История содержит элемент, который не существует");
    }

    @Test
    void getPrioritizedTasks() {
        subTask3.setStartTime(LocalDateTime.of(2000, Month.JANUARY, 1, 1, 0));
        taskManager.updateSubTask(subTask3);
        subTask2.setStartTime(LocalDateTime.of(2000, Month.JANUARY, 1, 2, 0));
        taskManager.updateSubTask(subTask2);
        subTask1.setStartTime(LocalDateTime.of(2000, Month.JANUARY, 1, 3, 0));
        taskManager.updateSubTask(subTask3);
        task.setStartTime(LocalDateTime.of(2000, Month.JANUARY, 1, 4, 0));
        taskManager.updateTask(task);
        taskManager.getPrioritizedTasks();

        assertEquals(subTask2, taskManager.getPrioritizedTasks().higher(subTask3),
                "Метод сортирует по приоритету неверно, Второй элемент возвращен неверно");
        assertEquals(subTask3, taskManager.getPrioritizedTasks().first(),
                "Метод сортирует по приоритету неверно, Первый элемент возвращен неверно");
        assertEquals(task, taskManager.getPrioritizedTasks().last(),
                "Метод сортирует по приоритету неверно, Последний элемент возвращен неверно");

    }

}


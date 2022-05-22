package managers;

import model.Epic;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        createTestTasks();
    }


    @Test
    void check_Epic_Of_SubTask() {
        assertEquals(epic.getId(), subTask1.getEpicId(),
                "Не найден эпик у подзадачи");
    }


    @Test
    void epic_Status_When_List_Of_Subtask_Is_Empty_Should_Be_New() {
        Epic epic = new Epic("epicTest", "desc", 10L, LocalDateTime.now());
        taskManager.createEpic(epic);
        assertEquals(0, epic.getSubTaskHashMap().size(), "Список подзадач не пустой");
        assertEquals(Status.NEW, epic.getStatus(), "Статус не соответствует");
    }

    @Test
    void epic_Status_When_All_Subtasks_With_Status_New_Should_Be_NEW() {
        assertEquals(Status.NEW, epic.getStatus(), "Статус не соответствует");

    }

    @Test
    void epic_Status_When_All_Subtasks_With_Status_DONE_Should_Be_DONE() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        assertEquals(Status.DONE, epic.getStatus(), "Статус не соответствует");

    }

    @Test
    void epic_Status_When_All_Subtasks_With_Status_DONE_And_NEW_Should_Be_IN_PROGRESS() {
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус не соответствует");

    }

    @Test
    void epic_Status_When_All_Subtasks_With_Status_IN_PROGRESS_Should_Be_IN_PROGRESS() {
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус не соответствует");

    }
}
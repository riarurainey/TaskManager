package managers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    //Создание задачи
    Task createTask(Task task);

    //Создание Эпика
    Epic createEpic(Epic epic);

    //Создание подзадачи
    SubTask createSubTask(SubTask subTask);

    //Поиск задачи по id
    Task findTaskById(long id);

    //Поиск Эпика по id
    Epic findEpicById(long id);

    //Поиск подзадачи по id
    SubTask findSubTaskById(long id);

    //Удаление задачи по id
    Task deleteTaskById(long id);

    //Удаление эпика по id
    Epic deleteEpicById(long id);

    //Удаление подзадачи по id
    SubTask deleteSubTaskById(long id);

    //Обновление задачи
    Task updateTask(Task task);

    //Обновление эпика
    Epic updateEpic(Epic epic);

    //Обновление подзадачи
    SubTask updateSubTask(SubTask subTask);

    //Вывод всех подзадач Эпика
    List<SubTask> getEpicsSubtask(long id);

    // Показать список всех задач
    List<Task> getAllTasks();

    // Удалить сразу все задачи
    void deleteAllTasks();

    //Получение листа истории
    List<Task> history();

    //Получение задач и подзадач по приоритету старта начала
    TreeSet<Task> getPrioritizedTasks();

}

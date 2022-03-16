package managers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Long, Epic> epics = new HashMap<>();
    HistoryManager history = Managers.getDefaultHistory();

    private static long id = 1;

    //Генерация id
    public static long generateId() {
        return id++;
    }

    //Создание задачи
    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    //Создание Эпика
    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    //Создание подзадачи
    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        epics.get(subTask.getEpicId()).getSubTaskHashMap().put(subTask.getId(), subTask);
        return subTask;
    }

    //Поиск задачи по id
    @Override
    public Task findTaskById(long id) {
        history.add(tasks.get(id));
        return tasks.get(id);
    }

    //Поиск Эпика по id
    @Override
    public Epic findEpicById(long id) {
        history.add(epics.get(id));
        return epics.get(id);
    }

    //Поиск подзадачи по id
    @Override
    public SubTask findSubTaskById(long id) {
        SubTask subTask = null;
        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            subTask = epicEntry.getValue().getSubTaskHashMap().get(id);
            break;
        }

        history.add(subTask);
        return subTask;
    }

    //Удаление задачи по id
    @Override
    public Task deleteTaskById(long id) {
        history.remove(id);
        return tasks.remove(id);
    }

    //Удаление эпика по id
    @Override
    public Epic deleteEpicById(long id) {
        history.remove(id);
        return epics.remove(id);
    }

    //Удаление подзадачи по id
    @Override
    public SubTask deleteSubTaskById(long id) {
        SubTask subTask = findSubTaskById(id);

        if (subTask != null) {
            epics.get(subTask.getEpicId()).getSubTaskHashMap().remove(id);
            changeEpicStatus(epics.get(subTask.getEpicId()));
            return subTask;
        }
        history.remove(id);
        return null;
    }

    //Обновление задачи
    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return task;
        }
        return null;
    }

    //Обновление эпика
    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return epic;
        }
        return null;
    }

    //Обновление подзадачи
    @Override
    public SubTask updateSubTask(SubTask subTask) {
        if (epics.containsValue(epics.get(subTask.getEpicId()))
                && epics.get(subTask.getEpicId()).getSubTaskHashMap().containsKey(subTask.getId())) {

            epics.get(subTask.getEpicId()).getSubTaskHashMap().put(subTask.getId(), subTask);
            changeEpicStatus(epics.get(subTask.getEpicId()));
            return subTask;
        }
        return null;
    }

    //Вывод всех подзадач Эпика
    @Override
    public List<SubTask> printEpicsSubtask(long id) {
        List<SubTask> subTasksList = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Map.Entry<Long, SubTask> epicEntry : epic.getSubTaskHashMap().entrySet()) {
                subTasksList.add(epicEntry.getValue());
            }
            return subTasksList;
        } else {
            return null;
        }
    }

    //Изменение статуса у Эпика
    void changeEpicStatus(Epic epic) {
        Set<Status> statusSet = new HashSet<>();
        for (Map.Entry<Long, SubTask> subtaskEntry : epics.get(epic.getId()).getSubTaskHashMap().entrySet()) {
            statusSet.add(subtaskEntry.getValue().getStatus());
        }
        if (statusSet.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (statusSet.size() == 1) {
            epic.setStatus(statusSet.iterator().next());
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // Показать список всех задач
    @Override
    public List<Task> printAllTasks() {
        List<Task> list = new ArrayList<>();

        for (Map.Entry<Long, Task> taskEntry : tasks.entrySet()) {
            list.add(taskEntry.getValue());
        }

        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            list.add(epicEntry.getValue());
        }
        return list;
    }

    // Удалить сразу все задачи
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
    }

    //Вызов метода получение истории из класса controller.InMemoryHistoryManager
    @Override
    public List<Task> history() {

        return history.getHistory();
    }
}

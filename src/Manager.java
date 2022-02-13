import java.util.*;

public class Manager {
    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Long, Epic> epics = new HashMap<>();

    private static long id = 1;

    //Генерация id
    public static long generateId() {
        return id++;
    }

    //Создание задачи
    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    //Создание Эпика
    public Epic createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }

    //Создание подзадачи
    public SubTask createSubTask(SubTask subTask) {
        epics.get(subTask.getEpicId()).getSubTaskHashMap().put(subTask.getId(), subTask);
        return subTask;
    }

    //Поиск задачи по id
    public Task findTaskById(long id) {
        return tasks.get(id);
    }

    //Поиск Эпика по id
    public Epic findEpicById(long id) {
        return epics.get(id);
    }

    //Поиск подзадачи по id
    public SubTask findSubTaskById(long id) {
        SubTask subTask = null;
        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            subTask = epicEntry.getValue().getSubTaskHashMap().get(id);
        }
        return subTask;
    }

    //Удаление задачи по id
    public Task deleteTaskById(long id) {
        return tasks.remove(id);
    }

    //Удаление эпика по id
    public Epic deleteEpicById(long id) {
        return epics.remove(id);
    }

    //Удаление подзадачи по id
    public SubTask deleteSubTaskById(long id) {
        SubTask subTask = findSubTaskById(id);
        if (subTask != null) {
            epics.get(subTask.getEpicId()).getSubTaskHashMap().remove(id);
            changeEpicStatus(epics.get(subTask.getEpicId()));
            return subTask;
        }
        return null;
    }

    //Обновление задачи
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return task;
        }
        return null;
    }

    //Обновление эпика
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return epic;
        }
        return null;
    }

    //Обновление подзадачи
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
    public ArrayList<SubTask> printEpicsSubtask(long id) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
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
    private void changeEpicStatus(Epic epic) {
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
    public ArrayList<Task> printAllTasks() {
        ArrayList<Task> list = new ArrayList<>();

        for (Map.Entry<Long, Task> taskEntry : tasks.entrySet()) {
            list.add(taskEntry.getValue());
        }

        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            list.add(epicEntry.getValue());
        }
        return list;
    }

    // Удалить сразу все задачи
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
    }
}

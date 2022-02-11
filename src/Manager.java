import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Manager {
    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Long, Epic> epics = new HashMap<>();

    private static long id = 1;

    //Генерация id
    public static long generateId() {
        return id++;
    }

    //Создание задачи
    public Task createTask(String name, String description) {
        Task task = new Task(name, description, generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    //Создание Эпика
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    //Создание подзадачи
    public SubTask createSubTask(String name, String description, long epicID) {
        SubTask subTask = new SubTask(name, description, generateId(), epicID);
        epics.get(epicID).getSubTaskHashMap().put(subTask.getId(), subTask);
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
            return epics.get(subTask.getEpicId()).getSubTaskHashMap().remove(id);
        }
        return null;
    }

    //Обновление задачи
    public Task updateTask(Task task, long id) {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            return task;
        }
        return null;
    }

    //Обновление эпика
    public Epic updateEpic(Epic epic, long id) {
        if (epics.containsKey(id)) {
            changeEpicStatus(epic);
            epics.put(id, epic);
            return epic;
        }
        return null;
    }

    //Обновление подзадачи
    public SubTask updateSubTask(SubTask subTask, long id) {
        if (epics.containsValue(epics.get(subTask.getEpicId()))
                && epics.get(subTask.getEpicId()).getSubTaskHashMap().containsKey(id)) {

            epics.get(subTask.getEpicId()).getSubTaskHashMap().put(id, subTask);
            changeEpicStatus(epics.get(subTask.getEpicId()));
            return subTask;
        }
        return null;
    }

    //Вывод всех подзадач Эпика
    public void printEpicsSubtask(long id) {
        StringBuilder subTaskToString = new StringBuilder();
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Map.Entry<Long, SubTask> epicEntry : epic.getSubTaskHashMap().entrySet()) {
                subTaskToString.append(epicEntry.getValue().toString()).append("\n");
            }
            System.out.println("Список подзадач Эпика с ID " + id + ":\n\n" + subTaskToString);
        } else {
            System.out.println("Эпика с таким ID не существует");
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
    public void printAllTasks() {
        for (Map.Entry<Long, Task> taskEntry : tasks.entrySet()) {
            System.out.println(taskEntry.getValue());
        }
        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            System.out.println(epicEntry.getValue());
        }
    }

    // Удалить сразу все задачи
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
    }
}

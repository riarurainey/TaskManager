package managers;

import exceptions.InvalidTimeException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Long, Task> tasks = new HashMap<>();
    protected HashMap<Long, Epic> epics = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();


    private static long id = 1;

    //Генерация id
    public static long generateId() {
        return id++;
    }

    //Создание задачи
    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        checkIntersection(task);
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
        checkIntersection(subTask);
        if (epics.get(subTask.getEpicId()) != null) {
            epics.get(subTask.getEpicId()).getSubTaskHashMap().put(subTask.getId(), subTask);
            changeEpicTimeFields(epics.get(subTask.getEpicId()));
            return subTask;
        } else {
            return null;
        }
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
            if (subTask != null) {
                break;
            }
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
            changeEpicTimeFields(epics.get(subTask.getEpicId()));
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
            if (!task.getStartTime().isEqual(tasks.get(task.getId()).getStartTime())) {
                checkIntersection(task);
            }
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

            if (!subTask.getStartTime().isEqual(epics.get(subTask.getEpicId()).getSubTaskHashMap().get(subTask.getId()).getStartTime())) {
                checkIntersection(subTask);
            }

            epics.get(subTask.getEpicId()).getSubTaskHashMap().put(subTask.getId(), subTask);
            changeEpicTimeFields(epics.get(subTask.getEpicId()));
            changeEpicStatus(epics.get(subTask.getEpicId()));
            return subTask;
        }
        return null;
    }

    //Вывод всех подзадач Эпика
    @Override
    public List<SubTask> getEpicsSubtask(long id) {
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


    //Проверка на пересечение времени
    public void checkIntersection(Task task) {
        TreeSet<Task> sortedTasks = getPrioritizedTasks();

        for (Task sortedTask : sortedTasks) {
            if (task.getStartTime() != null) {
                if (!(checkBefore(sortedTask, task) || checkAfter(sortedTask, task))) {
                    throw new InvalidTimeException("Ошибка в заполнении времени начала задачи и/или в продолжительности. " +
                            "Данное время занято другой задачей.");
                }
            }
        }
    }

    //Получение задач и подзадач по приоритету старта начала
    public TreeSet<Task> getPrioritizedTasks() {

        TreeSet<Task> prioritizedTasks = new TreeSet<>((o1, o2) -> {
            if (o1.getStartTime() == null || o2.getStartTime() == null
                    || o1.getStartTime().isAfter(o2.getStartTime())
                    || o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        });

        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            for (Map.Entry<Long, SubTask> subTaskEntry : epicEntry.getValue().getSubTaskHashMap().entrySet()) {
                prioritizedTasks.add(subTaskEntry.getValue());
            }
        }

        prioritizedTasks.addAll(tasks.values());
        return prioritizedTasks;
    }


    //Изменение времени у эпика
    private void changeEpicTimeFields(Epic epic) {
        Long epicsDuration = null;
        LocalDateTime epicEndTime = null;
        LocalDateTime epicStartTime = null;

        for (Map.Entry<Long, SubTask> subtaskEntry : epics.get(epic.getId()).getSubTaskHashMap().entrySet()) {
            if (epicsDuration == null) {
                epicsDuration = subtaskEntry.getValue().getDuration();
            } else {
                if (subtaskEntry.getValue().getDuration() != null) {
                    epicsDuration += subtaskEntry.getValue().getDuration();
                }
            }

            if (subtaskEntry.getValue().getEndTime() != null) {
                if (epicEndTime == null || epicEndTime.isBefore(subtaskEntry.getValue().getEndTime())) {
                    epicEndTime = subtaskEntry.getValue().getEndTime();
                }
            }

            if (subtaskEntry.getValue().getStartTime() != null) {
                if (epicStartTime == null || epicStartTime.isAfter(subtaskEntry.getValue().getStartTime())) {
                    epicStartTime = subtaskEntry.getValue().getStartTime();
                }
            }
        }

        epic.setStartTime(epicStartTime);
        epic.setDuration(epicsDuration);
        epic.setEndTime(epicEndTime);

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

    //Показать список всех задач
    @Override
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();

        for (Map.Entry<Long, Task> taskEntry : tasks.entrySet()) {
            list.add(taskEntry.getValue());
        }

        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            list.add(epicEntry.getValue());
        }
        return list;
    }

    //Удалить сразу все задачи
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
    }

    //Вызов метода получение истории из класса InMemoryHistoryManager
    @Override
    public List<Task> history() {
        return history.getHistory();
    }

    private boolean checkBefore(Task sorted, Task task) {
        return task.getStartTime().isBefore(sorted.getStartTime())
                && task.getEndTime().isBefore(sorted.getEndTime())
                && (task.getEndTime().isBefore(sorted.getStartTime()) ||
                task.getEndTime().isEqual(sorted.getStartTime()));
    }

    private boolean checkAfter(Task sorted, Task task) {
        return task.getStartTime().isAfter(sorted.getStartTime())
                && task.getEndTime().isAfter(sorted.getEndTime())
                && (task.getStartTime().isAfter(sorted.getEndTime()) ||
                task.getStartTime().isEqual(sorted.getEndTime()));
    }
}

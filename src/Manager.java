import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Manager<T extends Task> {
    // Обновление любой задачи/эпика/подзадачи
    void updateTask(T t, String name, String description, Status status) {
        t.setName(name);
        t.setDescription(description);
        t.setStatus(status);
    }

    //Вывод всех подзадач Эпика
    void printEpicsSubtask(int id, Map<Integer, Task> map) {
        StringBuilder subTaskToString = new StringBuilder();
        Epic epic = getByID(id, map);

        for (Map.Entry<Integer, SubTask> epicEntry : epic.getSubTaskHashMap().entrySet()) {
            subTaskToString.append(epicEntry.getValue().toString()).append("\n");
        }
        System.out.println("Список подзадач Эпика с ID " + id + ":\n\n" + subTaskToString);
    }


    //вспомогательный метод к changeStatus
    //По сету смотрим какие статусы у нас есть
    private Status getTasksStatus(Map<Integer, ? extends Task> map) {
        Set<Status> statusSet = new HashSet<>();
        for (Map.Entry<Integer, ? extends Task> currentMap : map.entrySet()) {
            statusSet.add(currentMap.getValue().getStatus());
        }

        if (statusSet.isEmpty()) {
            return Status.NEW;
        } else if (statusSet.size() == 1) {
            return statusSet.iterator().next();
        } else {
            return Status.IN_PROGRESS;
        }
    }

    //Меняем статус у эпика и/или задачи, если меняется подзадача
    void changeStatus(Map<Integer, Task> map) {
        for (Map.Entry<Integer, Task> mapTask : map.entrySet()) {
            for (Map.Entry<Integer, Epic> epicMap : mapTask.getValue().getEpicHashMap().entrySet()) {
                epicMap.getValue().setStatus(getTasksStatus(epicMap.getValue().getSubTaskHashMap()));
                mapTask.getValue().setStatus(getTasksStatus(mapTask.getValue().getEpicHashMap()));
            }
        }
    }


    void printCurrentTaskInfo(T t) { //Печать конкретной задачи
        if (t != null) {
            System.out.println("Найдена задача c ID: " + t.getId() + "\n" +
                    "Название задачи: " + t.getName() + "\n" +
                    "Описание: " + t.getDescription() + "\n" + "Статус: " + t.getStatus());
        }
    }

    // Удалить задачу по id
    void deleteById(int id, Map<Integer, Task> map) {
        T type = getByID(id, map);
        if (type != null) {
            if (type.getClass().equals(Task.class)) {
                System.out.println("Задача с ID " + id + " и названием "
                        + "\"" + map.get(id).getName() + "\""
                        + " успешно удалена!");
                map.remove(id);
            } else if (type.getClass().equals(Epic.class)) {
                for (Map.Entry<Integer, Task> pair : map.entrySet()) {
                    Map<Integer, Epic> epicMap = pair.getValue().getEpicHashMap();
                    if (epicMap != null && !epicMap.isEmpty()) {
                        if (epicMap.containsKey(id)) {
                            System.out.println("Эпик с ID " + id + " и с названием: "
                                    + "\"" + epicMap.get(id).getName() + "\""
                                    + " успешно удален!");

                            pair.getValue().getEpicHashMap().remove(id);
                            break;
                        }

                    }
                }
            } else {
                for (Map.Entry<Integer, Task> pair : map.entrySet()) {
                    for (Map.Entry<Integer, Epic> epicMap : pair.getValue().getEpicHashMap().entrySet()) {
                        if (epicMap.getValue().getSubTaskHashMap().containsKey(id)) {
                            System.out.println("Подзадача с ID " + id + " и названием "
                                    + "\"" + epicMap.getValue().getSubTaskHashMap().get(id).getName() + "\""
                                    + " успешно удалена!");
                            epicMap.getValue().getSubTaskHashMap().remove(id);
                            break;
                        }
                    }
                }

            }
        }

    }

    // Поиск задачи по id
    @SuppressWarnings("unchecked")
    <T extends Task> T getByID(int id, Map<Integer, Task> map) {
        if (id > Task.getCountId()) {
            System.out.println("Задачи с таким ID " + id + " не существует!");
            return null;
        } else {
            if (map.containsKey(id)) {
                return (T) map.get(id);
            } else if (!map.containsKey(id)) {
                Map<Integer, Epic> epicMap = new HashMap<>();
                for (Map.Entry<Integer, Task> mapTasks : map.entrySet()) {
                    epicMap.putAll(mapTasks.getValue().getEpicHashMap());
                }
                if (epicMap.containsKey(id)) {
                    return (T) epicMap.get(id);
                } else {
                    Map<Integer, SubTask> subTaskMap = new HashMap<>();
                    for (Map.Entry<Integer, Epic> mapEpic : epicMap.entrySet()) {
                        subTaskMap.putAll(mapEpic.getValue().getSubTaskHashMap());
                    }
                    if (subTaskMap.containsKey(id)) {
                        return (T) subTaskMap.get(id);
                    } else {
                        System.out.println("Задачи с таким ID " + id + " не существует");
                        return null;
                    }
                }
            }
        }
        return null;
    }

    //Используется для добавления эпика или подзадачи
    //Если мы добавляем эпик, то уточняем id задачи к которому мы хотим прикрепить Эпик
    //Если по такому id нет задачи, то возвращаем null
    @SuppressWarnings("unchecked")
    <T extends Task> T getSomeTaskByID(int id, Map<Integer, ? extends Task> map) {
        return (T) map.getOrDefault(id, null);
    }

    // Показать список всех задач
    void printAllTasks(Map<Integer, ? extends Task> map) {
        System.out.println("Список всех задач:\n");
        for (Map.Entry<Integer, ? extends Task> pair : map.entrySet()) {
            System.out.print(pair.getValue().toString());
        }
    }

    // Удалить сразу все задачи
    void deleteAllTasks(HashMap<Integer, Task> taskHashMap) {
        taskHashMap.clear();
    }
}

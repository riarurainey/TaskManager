package model;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private final HashMap<Long, SubTask> subTaskHashMap = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
        typeTasks = TypeTasks.EPIC;
    }

    public Epic(String name, String description, Long id, Status status) {
        super(name, description, id, status);
        typeTasks = TypeTasks.EPIC;
    }

    public HashMap<Long, SubTask> getSubTaskHashMap() {
        return subTaskHashMap;
    }

    @Override
    public String toString() {
        StringBuilder epicToString = new StringBuilder();

        for (Map.Entry<Long, SubTask> map : subTaskHashMap.entrySet()) {
            epicToString.append(map.getValue().toString()).append("\n");
        }

        return "ID Эпика: " + getId() + "\n" +
                "Название эпика: " + getName() + '\n' +
                "Описание эпика: " + getDescription() + '\n' +
                "Статус эпика: " + getStatus() + '\n' +
                "Список подзадач Эпика: " + '\n' + epicToString;
    }
}
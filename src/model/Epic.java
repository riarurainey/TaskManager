package model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private LocalDateTime endTime;
    private final HashMap<Long, SubTask> subTaskHashMap = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
        typeTasks = TypeTasks.EPIC;
    }

    public Epic(String name, String description, Long duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        typeTasks = TypeTasks.EPIC;
    }

    public Epic(String name, String description, Long id, Long duration, LocalDateTime startTime) {
        super(name, description, id, duration, startTime);
    }

    public Epic(String name, String description, Long id, Status status, Long duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        typeTasks = TypeTasks.EPIC;

    }

    public HashMap<Long, SubTask> getSubTaskHashMap() {
        return subTaskHashMap;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }



    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

   @Override
   public boolean equals(Object obj) {
        return super.equals(obj);
   }

   @Override
   public int hashCode() {
        return super.hashCode();
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
                "Продолжительность эпика: " + getDuration() + "\n" +
                "Дата и время начала эпика: " + getStartTime() + "\n" +
                "Время завершения эпика: " + getEndTime() + "\n" +
                "Список подзадач Эпика: " + '\n' + epicToString;
    }
}
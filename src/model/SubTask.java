package model;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private final Long epicId;

    public SubTask(String name, String description, Long epicId) {
        super(name, description);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public SubTask(String name, String description, Long epicId, Long duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public SubTask(String name, String description, Long id, Long epicId, Long duration, LocalDateTime startTime) {
        super(name, description, id, duration, startTime);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public SubTask(String name, String description, Status status, Long epicId,
                   Long duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public SubTask(String name, String description, Long id, Status status, Long epicId,
                   Long duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "ID Подзадачи: " + super.id + "\n" +
                "Название подзадачи: " + super.name + "\n" +
                "Описание подзадачи: " + super.description + "\n" +
                "Статус подзадачи: " + super.status + "\n" +
                "Подзадача привязана к Эпику с ID: " + epicId + "\n" +
                "Продолжительность подзадачи (в мин): " + super.duration + "\n" +
                "Дата и время начала подзадачи: " + super.startTime + "\n" +
                "Время завершения подзадачи: " + getEndTime() + "\n";

    }
}
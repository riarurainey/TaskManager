package model;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private final Long epicId;

    public SubTask(String name, String description, Long epicId) {
        super(name, description);
        this.epicId = epicId;
        setTypeTasks(TypeTasks.SUBTASK);

    }

    public SubTask(String name, String description, Long epicId, Long duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
        setTypeTasks(TypeTasks.SUBTASK);

    }

    public SubTask(String name, String description, Long id, Long epicId, Long duration, LocalDateTime startTime) {
        super(name, description, id, duration, startTime);
        this.epicId = epicId;
        setTypeTasks(TypeTasks.SUBTASK);

    }

    public SubTask(String name, String description, Status status, Long epicId,
                   Long duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        setTypeTasks(TypeTasks.SUBTASK);

    }

    public SubTask(String name, String description, Long id, Status status, Long epicId,
                   Long duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
        setTypeTasks(TypeTasks.SUBTASK);

    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "ID Подзадачи: " + getId() + "\n" +
                "Название подзадачи: " + getName() + "\n" +
                "Описание подзадачи: " + getDescription() + "\n" +
                "Статус подзадачи: " + getStatus() + "\n" +
                "Подзадача привязана к Эпику с ID: " + epicId + "\n" +
                "Продолжительность подзадачи (в мин): " + getDuration() + "\n" +
                "Дата и время начала подзадачи: " + getStartTime() + "\n" +
                "Время завершения подзадачи: " + getEndTime() + "\n";

    }
}
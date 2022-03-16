package model;

public class SubTask extends Task {
    private final Long epicId;

    public SubTask(String name, String description, Long epicId) {
        super(name, description);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public SubTask(String name, String description, Long id, Long epicId) {
        super(name, description, id);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

    }

    public SubTask(String name, String description, Long id, Status status, Long epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
        typeTasks = TypeTasks.SUBTASK;

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
                "Подзадача привязана к Эпику с ID: " + epicId;
    }
}
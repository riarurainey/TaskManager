
public class SubTask extends Task {
    private final long epicId;

    public SubTask(String name, String description, long id, long epicId) {
        super(name, description, id);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, long id, long epicId, Status status) {
        super(name, description, id);
        this.epicId = epicId;
        setStatus(status);
    }

    public long getEpicId() {
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
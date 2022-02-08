public class SubTask extends Task {
    private final int id;
    private Status status;


    public SubTask(String name, String description) {
        super(name, description);
        this.id = super.getId();
        setStatus(Status.NEW);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID Подзадачи: " + this.id + "\n" +
                "Название подзадачи: " + this.getName() + "\n" +
                "Описание подзадачи: " + this.getDescription() + "\n" +
                "Статус подзадачи: " + this.status;
    }
}
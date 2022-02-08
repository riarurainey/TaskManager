import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private final int id;
    private Status status;
    private final HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
        this.id = super.getId();
        setStatus(Status.NEW);
    }

    public HashMap<Integer, SubTask> getSubTaskHashMap() {
        return subTaskHashMap;
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
        StringBuilder epicToString = new StringBuilder();

        for (Map.Entry<Integer, SubTask> map : subTaskHashMap.entrySet()) {
            epicToString.append(map.getValue().toString()).append("\n");
        }

        return "ID Эпика: " + this.id + "\n" +
                "Название эпика: " + this.getName() + '\n' +
                "Описание эпика: " + this.getDescription() + '\n' +
                "Статус эпика: " + this.status + '\n' +
                "Список подзадач Эпика: " + '\n' + epicToString;
    }
}
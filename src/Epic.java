import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private final HashMap<Long, SubTask> subTaskHashMap = new HashMap<>();

    public Epic(String name, String description, long id) {
        super(name, description, id);
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
import java.util.HashMap;
import java.util.Map;

//Главный класс от которого наследуются класс Epic и Subtask
//В классе Task есть мапа Эпиков, а у Эпиков есть мапа Сабтасков
public class Task {
    private String name;
    private String description;
    private final int id;
    private static int countId = 0;
    private Status status;
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        id = ++countId;

    }

    public HashMap<Integer, Epic> getEpicHashMap() {
        return epicHashMap;
    }

    public static int getCountId() {
        return countId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder taskToString = new StringBuilder();

        for (Map.Entry<Integer, Epic> map : epicHashMap.entrySet()) {
            taskToString.append(map.getValue().toString()).append("\n");
        }

        return "ID Задачи: " + id + "\n" +
                "Название задачи: " + name + '\n' +
                "Описание задачи: " + description + '\n' +
                "Статус задачи: " + status + '\n' +
                "Список эпиков задачи: " + '\n' + taskToString;
    }
}
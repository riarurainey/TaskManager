import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    final int capacity;
    List<Task> history = new LinkedList<>();

    public InMemoryHistoryManager(int capacity) {
        this.capacity = capacity;
    }

    public InMemoryHistoryManager() {
        this(10);
    }

    //Добавление в историю
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() == capacity) {
            history.remove(0);
        }
        history.add(task);
    }

    //Получение истории
    @Override
    public List<Task> getHistory() {
        return history;
    }
}

import java.util.HashMap;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Task> tasks = new HashMap<>();
        Menu menu = new Menu(tasks);

        Task task1 = new Task("Первая простая задача", "Описание задачи");
        Task task2 = new Task("Вторая простая задача", "Описание задачи");

        Epic epic1 = new Epic("Первый Эпик", "Описание первого эпика");
        Epic epic2 = new Epic("Второй Эпик", "Описание второго эпика");
        SubTask subTask1 = new SubTask("Первая подзадача", "Описание первой подзадачи");
        SubTask subTask2 = new SubTask("Вторая подзадача", "Описание второй подзадачи");

        task1.getEpicHashMap().put(epic1.getId(), epic1);
        epic1.getSubTaskHashMap().put(subTask1.getId(), subTask1);
        tasks.put(task1.getId(), task1);

        task2.getEpicHashMap().put(epic2.getId(), epic2);
        epic2.getSubTaskHashMap().put(subTask2.getId(), subTask2);
        tasks.put(task2.getId(), task2);

        try {
            menu.getMenu();
        } catch (InputMismatchException e) {
            System.out.println("Некорректный ввод, нужно ввести число!");
        }
    }
}


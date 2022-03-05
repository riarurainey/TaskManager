import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Просто Задача", "_")); //id 1
        manager.createEpic(new Epic("Эпик1", "с тремя подзадачами"));//id 2
        manager.createSubTask(new SubTask("Подзадача 1", "Описание", 2L));//id 3
        manager.createSubTask(new SubTask("Подзадача 2", "Описание", 2L));//id 4
        manager.createSubTask(new SubTask("Подзадача 3", "Описание", 2L)); //id 5
        manager.createEpic(new Epic("Эпик2", "без подзадач"));//id 6

        manager.findTaskById(1);
        manager.findEpicById(2);
        manager.findSubTaskById(3);
        manager.findTaskById(1);
        manager.findEpicById(2);
        manager.findSubTaskById(3);

        manager.findEpicById(6);
        manager.findSubTaskById(4);
        manager.findSubTaskById(5);
        System.out.println("_______________________");
        System.out.println(manager.history());

        manager.deleteTaskById(1);
        manager.deleteEpicById(2);
        manager.deleteSubTaskById(3);


        System.out.println("_______________________");
        System.out.println(manager.history());

    }
}

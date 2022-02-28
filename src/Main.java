public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Задача", "Описание"));
        manager.createEpic(new Epic("Эпик1", "Описание"));
        manager.createSubTask(new SubTask("Подзадача 1", "Описание", 2L));
        manager.createSubTask(new SubTask("Подзадача 2", "Описание", 2L));
        manager.createSubTask(new SubTask("Подзадача 3", "Описание", 2L));

        manager.updateSubTask(new SubTask("Смена названия 1", "Смена описания 1", 3L, 2L, Status.IN_PROGRESS));
        manager.updateSubTask(new SubTask("Смена названия 2", "Смена описания 2", 4L, 2L, Status.IN_PROGRESS));
//        System.out.println(manager.printAllTasks());
        manager.findEpicById(2);

        manager.findTaskById(1);
        manager.findSubTaskById(3);
        manager.findTaskById(1);
        manager.findTaskById(1);
        manager.findTaskById(1);
        manager.findTaskById(1);
        manager.findTaskById(1);
        manager.findTaskById(1);
        manager.findSubTaskById(3);
        manager.findSubTaskById(4);
        System.out.println("_________________________________");
        System.out.println(manager.history());
    }
}

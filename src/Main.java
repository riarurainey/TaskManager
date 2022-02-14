public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.createTask(new Task("Задача", "Описание"));
        manager.createEpic(new Epic("Эпик1", "Описание"));
        manager.createSubTask(new SubTask("Подзадача 1", "Описание", 2L));
        manager.createSubTask(new SubTask("Подзадача 2", "Описание", 2L));
        manager.createSubTask(new SubTask("Подзадача 3", "Описание", 2L));

        manager.updateSubTask(new SubTask("Смена названия 1", "Смена описания 1", 3L, 2L, Status.IN_PROGRESS));
        manager.updateSubTask(new SubTask("Смена названия 2", "Смена описания 2", 4L, 2L, Status.IN_PROGRESS));
        System.out.println(manager.printAllTasks());
    }
}

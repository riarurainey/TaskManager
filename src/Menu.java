import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private final HashMap<Integer, Task> mapTask;

    public Menu(HashMap<Integer, Task> mapTask) {
        this.mapTask = mapTask;
    }

    Manager<? extends Task> manager = new Manager<>();

    Scanner scanner = new Scanner(System.in);

    //Основное меню
    public void printMenu() {
        System.out.println("Что Вы хотите сделать?");
        System.out.println("1. Добавить задачу");
        System.out.println("2. Обновить задачу");
        System.out.println("3. Получить список всех задач");
        System.out.println("4. Удалить все задачи");
        System.out.println("5. Получить задачу по идентификатору");
        System.out.println("6. Удалить задачу по идентификатору");
        System.out.println("7. Получение списка всех подзадач определённого эпика.");
        System.out.println("0. Выход из программы");
    }

    //Подменю, которые вызывается, когда нам нужно выбрать конкретную задачу
    public int podMenu() {
        System.out.println("Выберите тип задачи");
        System.out.println("1. Обычная задача. 2. Эпик. 3. Подзадача");
        return scannerInputInt(scanner);

    }

    //Выбор действия+
    public void choiceAction(int action) {
        String name;
        String description;
        int id;
        Task task;
        Epic epic;

        switch (action) {
            case 1:
                switch (podMenu()) {
                    case 1:
                        System.out.println("Введите имя задачи: ");
                        name = scanner.nextLine();
                        System.out.println("Введите описание задачи: ");
                        description = scanner.nextLine();
                        task = new Task(name, description);
                        mapTask.put(task.getId(), task);
                        break;

                    case 2:
                        System.out.println("Введите ID задачи, к которой необходимо добавить Эпик:");
                        id = scannerInputInt(scanner);
                        task = manager.getSomeTaskByID(id, mapTask);
                        if (task != null) {
                            System.out.println("Введите имя Эпика: ");
                            name = scanner.nextLine();
                            System.out.println("Введите описание Эпика: ");
                            description = scanner.nextLine();
                            epic = new Epic(name, description);
                            task.getEpicHashMap().put(epic.getId(), epic);
                        } else {
                            System.out.println("Задачи с ID " + id + " не существует!\n");
                        }
                        manager.changeStatus(mapTask);
                        break;

                    case 3:
                        System.out.println("Введите ID задачи, к которой необходимо добавить подзадачу:");
                        id = scannerInputInt(scanner);
                        task = manager.getSomeTaskByID(id, mapTask);
                        if (task != null) {
                            System.out.println("Введите ID Эпика,к которому необходимо добавить подзадачу:");
                            id = scannerInputInt(scanner);
                            epic = manager.getSomeTaskByID(id, task.getEpicHashMap());
                            if (epic != null) {
                                System.out.println("Введите имя подзадачи: ");
                                name = scanner.nextLine();
                                System.out.println("Введите описание подзадачи: ");
                                description = scanner.nextLine();
                                SubTask subTask = new SubTask(name, description);
                                epic.getSubTaskHashMap().put(subTask.getId(), subTask);
                            } else {
                                System.out.println("Эпика с ID " + id + " не существует в задаче c ID " + task.getId());
                            }
                        } else {
                            System.out.println("Задачи с ID " + id + " не существует!\n");
                        }
                        manager.changeStatus(mapTask);
                        break;
                }
                break;

            case 2:
                System.out.println("Введите ID задачи/эпика/подзадачи, которую хотите изменить?");
                id = scannerInputInt(scanner);
                manager.printCurrentTaskInfo(manager.getByID(id, mapTask));

                if (manager.getByID(id, mapTask) != null) {
                    String currentName = manager.getByID(id, mapTask).getName();
                    String currentDescription = manager.getByID(id, mapTask).getDescription();
                    Status currentStatus = manager.getByID(id, mapTask).getStatus();
                    System.out.println("Хотите поменять название?");
                    System.out.println("1 - Да\n" + "2 - Нет");
                    int selectToChange = scannerInputInt(scanner);
                    if (selectToChange == 1) {
                        System.out.println("Введите новое название: ");
                        currentName = scanner.nextLine();
                    }
                    System.out.println("Хотите поменять описание?");
                    System.out.println("1 - Да\n" + "2 - Нет");
                    selectToChange = scannerInputInt(scanner);
                    if (selectToChange == 1) {
                        System.out.println("Введите новое описание: ");
                        currentDescription = scanner.nextLine();
                    }
                    System.out.println("Сейчас статус задачи: " + currentStatus);
                    System.out.println("Хотите поменять статус задачи?");
                    System.out.println("1 - Да\n" + "2 - Нет");
                    selectToChange = scannerInputInt(scanner);

                    if (selectToChange == 1) {
                        System.out.println("Выберите новый статус: ");
                        System.out.println("1 - NEW\n2 - INPROGRESS\n3 - DONE");
                        selectToChange = scannerInputInt(scanner);
                        if (selectToChange == 1) {
                            currentStatus = Status.NEW;
                        } else if (selectToChange == 2) {
                            currentStatus = Status.IN_PROGRESS;
                        } else if (selectToChange == 3) {
                            currentStatus = Status.DONE;
                        } else {
                            System.out.println("Такого номера не существует");
                        }
                    }
                    manager.updateTask(manager.getByID(id, mapTask), currentName, currentDescription, currentStatus);
                    manager.changeStatus(mapTask);
                }
                break;

            case 3:
                manager.printAllTasks(mapTask);
                break;

            case 4:
                manager.deleteAllTasks(mapTask);
                break;

            case 5:
                System.out.println("Введите ID задачи/эпика/подзадачи, которую хотите получить: ");
                id = scannerInputInt(scanner);
                manager.printCurrentTaskInfo(manager.getByID(id, mapTask));
                break;

            case 6:
                System.out.println("Введите ID задачи/эпика/подзадачи, которую хотите удалить: ");
                id = scannerInputInt(scanner);
                manager.deleteById(id, mapTask);
                manager.changeStatus(mapTask);
                break;

            case 7:
                System.out.println("Введите id Эпика по которому хотите получить список подзадач:");
                id = scannerInputInt(scanner);
                manager.printEpicsSubtask(id, mapTask);
                break;

            case 0:
                System.out.println("Программа завершена!");
                System.exit(0);

            default:
                System.out.println("Такой команды нет");
        }
    }

    //Отдельный метод для вызова меню в Main
    public void getMenu() {
        while (true) {
            printMenu();
            int action = scanner.nextInt();
            choiceAction(action);
        }
    }

    //Метод для корректного считывания сканером. После считывания чисел, остается пустая строка
    //Здесь метод сразу после считывания числа, дальше сканирует, чтобы сбросить пустую строку
    private int scannerInputInt(Scanner scanner) throws InputMismatchException {
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
}

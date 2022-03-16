package managers;


import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager {
    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager1 = loadFromFile(Path.of("history"));
        System.out.println(fileBackedTasksManager1.printAllTasks());
        System.out.println(fileBackedTasksManager1.history());
    }

    static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        List<String> stringList = new ArrayList<>();
        String idNumbers = "";
        String str = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            while ((str = reader.readLine()) != null) {
                if (!str.startsWith("id") && !str.isBlank()) {
                    stringList.add(str);
                } else if (str.isBlank()) {
                    idNumbers = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // возвращаем задачи, подзадачи и эпики из строки
        for (String string : stringList) {
            fileBackedTasksManager.putInMap(fileBackedTasksManager.taskFromString(string));
        }

        //возвращаем историю из строки
        fileBackedTasksManager.restoreHistory(fromStringToListHistoryId(idNumbers));

        return fileBackedTasksManager;
    }


    private final Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    @Override
    public Task deleteTaskById(long id) {
        history.remove(id);
        save();
        return tasks.remove(id);
    }


    @Override
    public Epic deleteEpicById(long id) {
        history.remove(id);
        save();
        return epics.remove(id);
    }


    @Override
    public SubTask deleteSubTaskById(long id) {
        SubTask subTask = findSubTaskById(id);

        if (subTask != null) {
            epics.get(subTask.getEpicId()).getSubTaskHashMap().remove(id);
            changeEpicStatus(epics.get(subTask.getEpicId()));
            return subTask;
        }
        history.remove(id);
        save();
        return null;
    }


    @Override
    public Task findTaskById(long id) {
        history.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Epic findEpicById(long id) {
        history.add(epics.get(id));
        save();
        return epics.get(id);
    }

    @Override
    public SubTask findSubTaskById(long id) {
        SubTask subTask = null;
        for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
            subTask = epicEntry.getValue().getSubTaskHashMap().get(id);
            break;
        }
        history.add(subTask);
        save();
        return subTask;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;

    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubtask = super.createSubTask(subTask);
        save();
        return newSubtask;
    }

    //метод сохраняет задачи и историю в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("id,type,name,status,description,epic");

            for (Task taskEntry : tasks.values()) {
                writer.write("\n");
                writer.write(toSaveToString(taskEntry));

            }
            for (Epic epicEntry : epics.values()) {
                writer.write("\n");
                writer.write(toSaveToString(epicEntry));
            }
            for (Epic epic : epics.values()) {
                for (SubTask subTask : epic.getSubTaskHashMap().values()) {
                    writer.write("\n");
                    writer.write(toSaveToStringSubtask(subTask));
                }
            }

            writer.write("\n\n");
            writer.write(historyToString(history));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в методе сохранения");
        }
    }

    //метод добавляет задачи в мапы
    void putInMap(Task task) {
        if (task.getTypeTasks() == TypeTasks.TASK) {
            tasks.put(task.getId(), task);
        } else if (task.getTypeTasks() == TypeTasks.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getTypeTasks() == TypeTasks.SUBTASK) {
            SubTask subTask = (SubTask) task;
            epics.get(subTask.getEpicId()).getSubTaskHashMap().put(subTask.getId(), subTask);
        }
    }


    //метод возвращает задачу из строки
    Task taskFromString(String value) {

        String[] strings = value.split(",");

        String name = strings[2];
        String description = strings[4];
        Long id = Long.parseLong(strings[0]);
        Status status = Status.valueOf(strings[3]);
        Long epicId = null;
        if (strings.length > 5) {
            epicId = Long.parseLong(strings[5]);
        }

        switch (TypeTasks.valueOf(strings[1])) {
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                return new Epic(name, description, id, status);
            case SUBTASK:
                return new SubTask(name, description, id, status, epicId);
        }
        return null;
    }


    //метод восоздает историю из листа ид
    void restoreHistory(List<Long> historyId) {
        for (Long id : historyId) {
            if (tasks.containsKey(id)) {
                history.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                history.add(epics.get(id));
            } else {
                SubTask subTask = null;
                for (Map.Entry<Long, Epic> epicEntry : epics.entrySet()) {
                    subTask = epicEntry.getValue().getSubTaskHashMap().get(id);
                    break;
                }
                if (subTask != null) {
                    history.add(subTask);
                }
            }
        }
    }


    //метод возращает из строки лист с ид задачами
    static List<Long> fromStringToListHistoryId(String value) {

        String[] strings = value.split(",");
        List<Long> list = new ArrayList<>();
        for (String str : strings) {
            list.add(Long.parseLong(str));
        }
        return list;
    }

    //метод превращения истории в строку
    static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder("");
        for (Task task : manager.getHistory()) {
            builder.append(task.getId());
            builder.append(",");
        }
        return builder.toString();
    }

    @Override
    public List<Task> history() {
        return super.history();
    }


    //Метод превращения обычной задачи и эпика в строку
    public String toSaveToString(Task task) {
        return String.format("%s,%s,%s,%s,%s",
                task.getId(),
                task.getTypeTasks(),
                task.getName(),
                task.getStatus(),
                task.getDescription());

    }

    //Метод превращения подзадачи в строку
    public String toSaveToStringSubtask(SubTask subTask) {
        return String.format("%s,%s,%s,%s,%s,%d",
                subTask.getId(),
                subTask.getTypeTasks(),
                subTask.getName(),
                subTask.getStatus(),
                subTask.getDescription(),
                subTask.getEpicId());

    }

}


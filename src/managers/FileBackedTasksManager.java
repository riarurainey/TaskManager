package managers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TypeTasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    protected static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        List<String> listOfTasks = new ArrayList<>();
        String idNumbers = "";
        String str;

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            while ((str = reader.readLine()) != null) {
                if (!str.isEmpty() && !str.startsWith("id")) {
                    listOfTasks.add(str);
                } else if (str.isBlank()) {
                    idNumbers = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // возвращаем задачи, подзадачи и эпики из строки
        for (String taskString : listOfTasks) {
            fileBackedTasksManager.putInMap(fileBackedTasksManager.taskFromString(taskString));
        }

        //возвращаем историю из строки
        if (idNumbers != null) {
            fileBackedTasksManager.restoreHistory(fromStringToListHistoryId(idNumbers));
        }

        return fileBackedTasksManager;
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
        super.deleteSubTaskById(id);
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
        SubTask subTask = super.findSubTaskById(id);
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
    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("id,type,name,status,description,epic,duration,start_time");

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

    //Метод добавляет задачи в мапы
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


    //Метод возвращает задачу из строки
    Task taskFromString(String value) {
        String[] strings = value.split(",");
        String name = strings[2];
        String description = strings[4];
        Long id = Long.parseLong(strings[0]);
        Status status = Status.valueOf(strings[3]);
        Long epicId = null;
        Long duration = null;
        LocalDateTime startTime = null;

        if (!strings[6].equals("null")) {
            duration = Long.parseLong(strings[6]);
        }
        if (!strings[7].equals("null")) {
            startTime = LocalDateTime.parse(strings[7]);
        }

        if (TypeTasks.valueOf(strings[1]) == TypeTasks.SUBTASK) {
            epicId = Long.parseLong(strings[5]);
        }

        switch (TypeTasks.valueOf(strings[1])) {
            case TASK:
                return new Task(name, description, id, status, duration, startTime);
            case EPIC:
                return new Epic(name, description, id, status, duration, startTime);
            case SUBTASK:
                return new SubTask(name, description, id, status, epicId, duration, startTime);
        }
        return null;
    }


    //Метод восоздает историю из листа ид
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


    //Метод возращает из строки лист с ид задачами
    static List<Long> fromStringToListHistoryId(String value) {

        String[] strings = value.split(",");
        List<Long> list = new ArrayList<>();
        for (String str : strings) {
            list.add(Long.parseLong(str));
        }
        return list;
    }

    //Метод превращения истории в строку
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
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getTypeTasks(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                "-",
                task.getDuration(),
                task.getStartTime());

    }

    //Метод превращения подзадачи в строку
    public String toSaveToStringSubtask(SubTask subTask) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                subTask.getId(),
                subTask.getTypeTasks(),
                subTask.getName(),
                subTask.getStatus(),
                subTask.getDescription(),
                subTask.getEpicId(),
                subTask.getDuration(),
                subTask.getStartTime());
    }

}


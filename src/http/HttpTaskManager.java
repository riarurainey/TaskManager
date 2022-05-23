package http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import managers.FileBackedTasksManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                            LocalDateTime.parse(json.getAsString())
            )
            .registerTypeAdapter(
                    LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, context) ->
                            new JsonPrimitive(src.toString())
            ).create();

    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(String fileName, boolean isStartLoad) throws IOException, InterruptedException {
        super(fileName);
        kvTaskClient = new KVTaskClient(fileName);
        if (isStartLoad) {
            load();
        }
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        ArrayList<SubTask> subTasks = new ArrayList<>();

        for (Epic epic : epics.values()) {
            subTasks.addAll(epic.getSubTaskHashMap().values());
        }

        List<Long> historyIdList = new ArrayList<>();

        for (Task task : history()) {
            historyIdList.add(task.getId());
        }

        String jsonHistory = gson.toJson(historyIdList);
        String jsonSubTasks = gson.toJson(subTasks);

        try {
            kvTaskClient.put("tasks", jsonTasks);
            kvTaskClient.put("epics", jsonEpics);
            kvTaskClient.put("subtasks", jsonSubTasks);
            kvTaskClient.put("history", jsonHistory);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void load() {

        try {
            ArrayList<Task> tasksList = gson.fromJson(kvTaskClient.load("tasks"), new TypeToken<ArrayList<Task>>() {
            }.getType());

            for (Task task : tasksList) {
                putInMap(task);
            }

            ArrayList<Epic> epics = gson.fromJson(kvTaskClient.load("epics"), new TypeToken<ArrayList<Epic>>() {
            }.getType());

            for (Epic epic : epics) {
                putInMap(epic);
            }

            ArrayList<SubTask> subTasks = gson.fromJson(kvTaskClient.load("subtasks"), new TypeToken<ArrayList<SubTask>>() {
            }.getType());

            for (SubTask subTask : subTasks) {
                putInMap(subTask);
            }

            List<Long> history = gson.fromJson(kvTaskClient.load("history"), new TypeToken<ArrayList<Long>>() {
            }.getType());

            restoreHistory(history);

        } catch (NullPointerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

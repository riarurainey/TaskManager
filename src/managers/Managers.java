package managers;

import http.HttpTaskManager;
import java.io.IOException;


public class Managers {
    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078", false);
//        return new FileBackedTasksManager("history");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

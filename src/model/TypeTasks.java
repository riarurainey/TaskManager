package model;

public enum TypeTasks {

    TASK("Task"),
    EPIC("Epic"),
    SUBTASK("SubTask");

    private final String value;

    TypeTasks(String value) {
        this.value = value;

    }

    public String getValue() {
        return value;
    }
}

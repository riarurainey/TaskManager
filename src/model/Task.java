package model;

public class Task {
    private final String name;
    private final String description;
    private Long id;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, Long id) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = id;
    }

    public Task(String name, String description, Long id, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID Задачи: " + id + "\n" +
                "Название задачи: " + name + '\n' +
                "Описание задачи: " + description + '\n' +
                "Статус задачи: " + status + '\n';

    }
}
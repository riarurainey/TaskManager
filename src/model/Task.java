package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private Long id;
    private Status status;
    private Long duration;
    private LocalDateTime startTime;
    private TypeTasks typeTasks;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.typeTasks = TypeTasks.TASK;
    }

    public Task(String name, String description, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
        typeTasks = TypeTasks.TASK;

    }

    public Task(String name, String description, Long id, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        typeTasks = TypeTasks.TASK;
    }

    public Task(String name, String description, Status status, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        typeTasks = TypeTasks.TASK;
    }

    public Task(String name, String description, Long id, Status status, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        typeTasks = TypeTasks.TASK;
    }


    public void setTypeTasks(TypeTasks typeTasks) {
        this.typeTasks = typeTasks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public TypeTasks getTypeTasks() {
        return typeTasks;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plusMinutes(duration);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(id, task.id) && status == task.status
                && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime)
                && typeTasks == task.typeTasks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, duration, startTime, typeTasks);
    }

    @Override
    public String toString() {
        return "ID Задачи: " + id + "\n" +
                "Название задачи: " + name + "\n" +
                "Описание задачи: " + description + "\n" +
                "Статус задачи: " + status + "\n" +
                "Продолжительность задачи (в мин): " + duration + "\n" +
                "Дата и время начала задачи: " + startTime + "\n" +
                "Время завершения задачи: " + getEndTime() + "\n";
    }
}
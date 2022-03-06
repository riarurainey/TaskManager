package controller;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Node first;
    Node last;
    Map<Long, Node> map = new HashMap<>();

    //Добавление в историю
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        linkLast(task);
    }

    //удаление из истории
    @Override
    public void remove(long id) {
        final Node old = map.remove(id);
        removeNode(old);
    }

    //удаление узла
    private void removeNode(Node old) {
        if (old != null) {
            if (old == first) {
                first = old.next;
            } else if (old == last) {
                last = old.prev;
                last.next = null;
            } else {
                old.prev.next = old.next;
            }
        }
    }

    //добавление в конец
    private void linkLast(Task task) {
        remove(task.getId());
        final Node newNode = new Node(task);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }
        last = newNode;
        map.put(task.getId(), newNode);
    }

   // метод собирает задачи в лист
    public List<Task> getTasks() {
        final List<Task> tasks = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }
    //Получение истории
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}

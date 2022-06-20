/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 * @param <T>
 */
public class ModelList<T extends AbstractModel> implements Iterable<T> {
    private final List<ModelRemoveListener<T>> removeListeners
            = new LinkedList<>();
    private final TreeMap<Long, T> map = new TreeMap<>();

    public ModelList() {
    }

    public T get(Long id) {
        return map.get(id);
    }

    public T getByIndex(int index) {
        Map.Entry<Long, T>[] entryArray = map.entrySet().toArray(
                Map.Entry[]::new);
        return entryArray[index].getValue();
    }

    public List<T> asList() {
        return new ArrayList<>(map.values());
    }

    public T getFirst() {
        return map.firstEntry().getValue();
    }

    public T getLast() {
        return map.lastEntry().getValue();
    }

    public void add(T model) {
        map.put(model.getId(), model);
    }

    public void addAll(ModelList<T> modelList) {
        map.putAll(modelList.map);
    }

    public T remove(Long id) {
        T m = map.remove(id);
        if (m != null) {
            fireRemoveModel(m);
        }
        return m;
    }

    public void removeIf(Predicate<? super T> filter) {
        List<T> l = map.values().stream().filter(filter).collect(
                Collectors.toList());
        for (T m : l) {
            remove(m.getId());
        }
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Stream<T> stream() {
        return map.values().stream();
    }

    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    public void addRemoveListener(ModelRemoveListener<T> listener) {
        removeListeners.add(listener);
    }

    public void removeRemoveListener(ModelRemoveListener<T> listener) {
        removeListeners.remove(listener);
    }

    private void fireRemoveModel(T model) {
        for (ModelRemoveListener<T> l : removeListeners) {
            l.removed(model);
        }
    }
}

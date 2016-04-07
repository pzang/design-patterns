package me.pzang.watchers;

/**
 * Created by pzang on 4/6/16.
 */
public interface Watcher<T> {
    void register(Observer<T> observer);

    void notifyMyObservers(T obj);
}

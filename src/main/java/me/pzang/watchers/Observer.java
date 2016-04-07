package me.pzang.watchers;

/**
 * Created by pzang on 4/6/16.
 */
public interface Observer<T> {
    boolean update(T status);
}

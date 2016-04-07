package me.pzang;

import me.pzang.watchers.DirectoryObserver;
import me.pzang.watchers.DirectoryWatcher;
import me.pzang.watchers.Observer;

import java.io.IOException;

public class ApplicationMain {

    public static void main(String[] args) throws IOException {
        System.out.println("design pattern started");
        DirectoryWatcher watcher = new DirectoryWatcher("/home/pzang", true, true);
        Observer ob1 = new DirectoryObserver();
        Observer ob2 = new DirectoryObserver();
        watcher.register(ob1);
        watcher.register(ob2);

        watcher.start();
    }


}
package me.pzang.watchers;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.LinkOption.*;
import static java.nio.file.StandardWatchEventKinds.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created by pzang on 4/6/16.
 */
public class DirectoryWatcher implements Watcher<FileSystemChangeEvent> {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final List<Observer<FileSystemChangeEvent>> observers;
    private boolean recursive = true;
    private boolean trace = false;

    public DirectoryWatcher() throws IOException {
        keys = new HashMap<>();
        observers = new ArrayList<>();
        watcher = FileSystems.getDefault().newWatchService();
    }

    public DirectoryWatcher(String dir, boolean recursive, boolean trace) throws IOException {
        this();
        this.recursive = recursive;
        this.trace = trace;
        Path start = Paths.get(dir);
        if (recursive)
            watchAll(start);
        else
            watch(start);
    }

    void watchAll(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                watch(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    void watch(Path path) throws IOException {
        WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            if (keys.get(key) == null)
                System.out.println("***** adding new watching dir : " + path);
            else
                System.out.println("***** update watched path: " + path);
        }
        keys.put(key, path);
    }

    public void start() {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);


                FileSystemChangeEvent fevent = new FileSystemChangeEvent();
                fevent.setAction(kind.name());
                fevent.setPath(ev.context().toString());
                notifyMyObservers(fevent);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            watchAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }

    }

    @Override
    public void register(Observer<FileSystemChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void notifyMyObservers(FileSystemChangeEvent obj) {
        for (Observer obs : observers) {
            obs.update(obj);
        }
    }
}

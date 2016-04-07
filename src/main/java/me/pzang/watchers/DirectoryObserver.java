package me.pzang.watchers;

/**
 * Created by pzang on 4/7/16.
 */
public class DirectoryObserver implements Observer<FileSystemChangeEvent> {
    private FileSystemChangeEvent currentStatus;

    @Override
    public boolean update(FileSystemChangeEvent status) {
        System.out.println(status);
        this.currentStatus = status;
        return true;
    }
}

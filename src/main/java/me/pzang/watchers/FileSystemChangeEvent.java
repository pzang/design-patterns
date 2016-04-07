package me.pzang.watchers;

/**
 * Created by pzang on 4/7/16.
 */
public class FileSystemChangeEvent {
    private String path;
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileSystemChangeEvent{" +
                "path='" + path + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}

package org.example.demo2;
//实现文件监听功能，备忘录被修改并保存后实时显示更新在ui上
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher extends Thread {
    private final Path dir;
    private final Controller controller;

    public FileWatcher(Path dir, Controller controller) {
        this.dir = dir;
        this.controller = controller;
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            dir.register(watcher, ENTRY_MODIFY);

            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == ENTRY_MODIFY) {
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();

                        if (filename.toString().equals("notepad.txt")) {
                            Platform.runLater(() -> controller.loadNotepadContent());
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error using WatchService");
            e.printStackTrace();
        }
    }
}

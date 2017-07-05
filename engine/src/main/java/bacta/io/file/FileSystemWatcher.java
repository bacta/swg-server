/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.file;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by crush on 6/6/2016.
 */
@Slf4j
public final class FileSystemWatcher {

    private final Path filePath;
    private final Consumer<Path> createCallback;
    private final Consumer<Path> deleteCallback;
    private final Consumer<Path> modifyCallback;
    private volatile boolean watching;

    /**
     * Creates a new FileSystemWatcher which monitors a directory for changes to files. If a file is created, added,
     * or deleted, then the appropriate callback is executed. Call {@link #start()} to start watching. An infinite loop
     * will run until it is told to {@link #stop()} or an exception is thrown.
     *
     * @param filePath       The directory to be watched.
     * @param createCallback The callback to be executed when a file is created in the directory.
     * @param deleteCallback The callback to be executed when a file is deleted in the directory.
     * @param modifyCallback The callback to be executed when a file is modified in the directory.
     */
    public FileSystemWatcher(final Path filePath,
                             final Consumer<Path> createCallback,
                             final Consumer<Path> deleteCallback,
                             final Consumer<Path> modifyCallback) {
        this.filePath = filePath;
        this.createCallback = createCallback;
        this.deleteCallback = deleteCallback;
        this.modifyCallback = modifyCallback;
        this.watching = false;
    }

    /**
     * Starts an infinite loop monitoring the configured directory. Will continue to monitor and call the configured
     * callbacks until either {@link #stop()} is called or an exception is thrown.
     */
    public void start() {
        if (watching) {
            LOGGER.error("This instance is already watching. Create a new instance.");
            return;
        }

        new Thread(() -> {
            try (final WatchService watcher = FileSystems.getDefault().newWatchService()) {
                LOGGER.debug("Watching {} for file changes.", filePath.toString());

                filePath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

                watching = true;

                while (watching) {
                    final WatchKey watchKey = watcher.take();

                    for (final WatchEvent<?> event : watchKey.pollEvents()) {
                        final WatchEvent.Kind<?> kind = event.kind();

                        if (StandardWatchEventKinds.OVERFLOW.equals(kind))
                            continue;

                        final WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;
                        final Path filePath = watchEvent.context();

                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind) && createCallback != null) {
                            createCallback.accept(filePath);
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind) && deleteCallback != null) {
                            deleteCallback.accept(filePath);
                        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind) && modifyCallback != null) {
                            modifyCallback.accept(filePath);
                        }
                    }

                    final boolean valid = watchKey.reset();

                    if (!valid) {
                        watching = false;
                        break;
                    }
                }

                watching = false;
                LOGGER.debug("Not watching for file changes anymore.");
            } catch (final Exception ex) {
                LOGGER.error("Unexpectedly stopped watching: {}", ex.getMessage());
            }
        }).start();
    }

    /**
     * Stops the infinite loop monitoring for file changes.
     */
    public void stop() {
        LOGGER.debug("Requested to stop watching for file changes.");
        watching = false;
    }
}

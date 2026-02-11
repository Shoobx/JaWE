/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses
 */

package org.enhydra.jawe.base.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.enhydra.jawe.JaWEManager;

/**
 * Service that monitors XPDL files for external changes and handles reload logic.
 * Uses Java NIO.2 WatchService to efficiently monitor file system changes.
 */
public class FileWatcherService {

    private WatchService watchService;
    private WatchKey watchKey;
    private Thread watchThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    // Debouncing mechanism to handle multiple rapid file change events
    private ScheduledExecutorService debounceExecutor;
    private ScheduledFuture<?> pendingReloadTask;

    // Configuration constants
    private static final class Config {
        // Timing constants
        static final long DEBOUNCE_DELAY_MS = 1000;           // Wait 1 second after last change
        static final long STABILITY_CHECK_INTERVAL_MS = 100;  // 100ms between stability checks
        static final long UI_SETTLE_DELAY_MS = 100;           // Short delay to let UI settle
        static final long EXECUTOR_SHUTDOWN_TIMEOUT_SEC = 1;   // Executor shutdown timeout

        // Reload constants
        static final int MAX_RELOAD_ATTEMPTS = 3;             // Maximum attempts to reload file

        // Dialog option indices
        static final int DIALOG_LOAD_FROM_DISK = 0;
        static final int DIALOG_SAVE_CHANGES = 1;
        static final int DIALOG_DO_NOTHING = 2;
        static final int DIALOG_DEFAULT_OPTION = DIALOG_DO_NOTHING;
    }

    private String currentFilePath;
    private String currentFileName;
    private JaWEController controller;

    public FileWatcherService(JaWEController controller) {
        this.controller = controller;
        ensureExecutorAvailable();
    }

    // Logging utility methods to reduce verbosity
    private void logInfo(String message) {
        JaWEManager.getInstance().getLoggingManager().info("FileWatcherService -> " + message);
    }

    private void logDebug(String message) {
        JaWEManager.getInstance().getLoggingManager().debug("FileWatcherService -> " + message);
    }

    private void logWarn(String message) {
        JaWEManager.getInstance().getLoggingManager().warn("FileWatcherService -> " + message);
    }

    private void logWarn(String message, Exception e) {
        JaWEManager.getInstance().getLoggingManager().warn("FileWatcherService -> " + message, e);
    }

    private void logError(String message, Exception e) {
        JaWEManager.getInstance().getLoggingManager().error("FileWatcherService -> " + message, e);
    }

    /**
     * Ensure the debounce executor is available and not shutdown
     */
    private synchronized void ensureExecutorAvailable() {
        if (debounceExecutor == null || debounceExecutor.isShutdown()) {
            debounceExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "FileWatcher-Debounce");
                t.setDaemon(true);
                return t;
            });
        }
    }

    /**
     * Start watching the specified file for changes
     */
    public void startWatching(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        // Check if executor is available - if not, service might be shut down
        if (debounceExecutor == null || debounceExecutor.isShutdown()) {
            logWarn("Cannot start watching, service appears to be shut down");
            return;
        }

        // Stop any existing watching
        stopWatching();

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logWarn("Cannot watch non-existent file: " + filePath);
                return;
            }

            // Get the directory to watch (we watch the directory, not the file directly)
            Path dir = file.getParentFile().toPath();
            this.currentFilePath = file.getAbsolutePath();
            this.currentFileName = file.getName();

            // Create watch service
            this.watchService = FileSystems.getDefault().newWatchService();

            // Register the directory for MODIFY events
            this.watchKey = dir.register(watchService,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE);

            // Start the monitoring thread
            this.running.set(true);
            this.watchThread = new Thread(this::watchLoop, "FileWatcher-" + currentFileName);
            this.watchThread.setDaemon(true);
            this.watchThread.start();

            logInfo("Started watching file: " + filePath);

        } catch (IOException e) {
            logError("Failed to start watching file: " + filePath, e);
        }
    }

    /**
     * Stop watching the current file
     */
    public void stopWatching() {
        logInfo("Stopping file watch");
        running.set(false);

        // Cancel any pending debounced reload task
        if (pendingReloadTask != null) {
            pendingReloadTask.cancel(false);
            pendingReloadTask = null;
        }

        if (watchKey != null) {
            watchKey.cancel();
            watchKey = null;
        }

        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                logWarn("Error closing watch service", e);
            }
            watchService = null;
        }

        if (watchThread != null) {
            watchThread.interrupt();
            watchThread = null;
        }

        // Note: We don't shut down the debounceExecutor here since it can be reused
        // for watching other files. It will be shut down when the service is destroyed.

        currentFilePath = null;
        currentFileName = null;

        logInfo("Stopped watching file");
    }

    /**
     * Shutdown the file watcher service completely (called when JaWEController is destroyed)
     */
    public void shutdown() {
        logInfo("Shutting down file watcher service");
        // First stop any current watching
        stopWatching();

        // Then shutdown the debounce executor
        if (debounceExecutor != null && !debounceExecutor.isShutdown()) {
            debounceExecutor.shutdown();
            try {
                if (!debounceExecutor.awaitTermination(Config.EXECUTOR_SHUTDOWN_TIMEOUT_SEC, TimeUnit.SECONDS)) {
                    debounceExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                debounceExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logInfo("Service shut down");
    }

    /**
     * Enable or disable the file watcher notifications
     */
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
        logInfo("File watching " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Check if the watcher is currently enabled
     */
    public boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Check if the service is functional (not shut down)
     */
    public boolean isServiceFunctional() {
        return debounceExecutor != null && !debounceExecutor.isShutdown();
    }

    /**
     * Temporarily disable file watching for a short period to avoid detecting internal operations
     */
    public void temporarilyDisable(long milliseconds) {
        if (!enabled.get()) {
            return; // Already disabled
        }

        setEnabled(false);

        // Re-enable after the specified delay using existing executor
        ensureExecutorAvailable();
        try {
            debounceExecutor.schedule(() -> setEnabled(true), milliseconds, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.RejectedExecutionException e) {
            // Executor unavailable, re-enable immediately as fallback
            logWarn("Cannot schedule re-enable, enabling immediately: " + e.getMessage());
            setEnabled(true);
        }
    }

    /**
     * Main watch loop that monitors for file changes
     */
    private void watchLoop() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                // Check if watch service is still available
                if (watchService == null) {
                    logDebug("Watch service is null, exiting watch loop");
                    break;
                }

                WatchKey key = watchService.take(); // This blocks until events are available

                if (!running.get()) {
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // Skip overflow events
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    // Get the filename that changed
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    // Check if it's the file we're watching
                    if (currentFileName != null && currentFileName.equals(filename.toString())) {
                        // File changed, schedule debounced handling
                        if (enabled.get()) {
                            scheduleFileChangeProcessing();
                        }
                    }
                }

                // Reset the key -- important!
                boolean valid = key.reset();
                if (!valid) {
                    // Directory is no longer accessible
                    break;
                }

            } catch (InterruptedException e) {
                // Thread was interrupted, exit
                Thread.currentThread().interrupt();
                break;
            } catch (java.nio.file.ClosedWatchServiceException e) {
                // Watch service was closed (probably during shutdown or reload), exit gracefully
                logDebug("Watch service closed, exiting watch loop");
                break;
            } catch (Exception e) {
                logError("Error in watch loop", e);
            }
        }
    }

    /**
     * Schedule file change processing with debouncing to avoid race conditions
     * with external editors that may write files in multiple operations
     */
    private void scheduleFileChangeProcessing() {
        synchronized (this) {
            // Check if we're still running
            if (!running.get()) {
                logDebug("Cannot schedule processing, service stopping");
                return;
            }

            // Ensure executor is available
            ensureExecutorAvailable();

            // Cancel any existing pending reload task
            if (pendingReloadTask != null) {
                pendingReloadTask.cancel(false);
            }

            try {
                // Schedule new reload task with delay
                pendingReloadTask = debounceExecutor.schedule(() -> {
                    if (enabled.get() && currentFilePath != null && running.get()) {
                        // Check if we can reload automatically or need to show dialog
                        checkAndScheduleReload(); // Check for unsaved changes first
                    }
                }, Config.DEBOUNCE_DELAY_MS, TimeUnit.MILLISECONDS);

                logDebug("Scheduled debounced file change processing");
            } catch (java.util.concurrent.RejectedExecutionException e) {
                logDebug("Cannot schedule task, executor issue: " + e.getMessage());
                // Try to recreate executor for next time
                ensureExecutorAvailable();
            }
        }
    }

    /**
     * Check for unsaved changes and either auto-reload or show user dialog
     */
    private void checkAndScheduleReload() {
        try {
            // Check if we still have a valid current file
            if (currentFilePath == null || controller.getMainPackage() == null) {
                return;
            }

            // Get the current main package file path
            String currentMainFilePath = JaWEManager.getInstance().getXPDLHandler()
                .getAbsoluteFilePath(controller.getMainPackage());

            // Make sure this is still the file we're watching
            if (!currentFilePath.equals(currentMainFilePath)) {
                return;
            }

            // Check if there are unsaved changes
            boolean hasUnsavedChanges = controller.isPackageModified(controller.getMainPackageId());

            if (!hasUnsavedChanges) {
                // No unsaved changes - reload automatically with retry
                reloadWithRetry(1); // attempt 1
            } else {
                // Has unsaved changes - show dialog on EDT
                SwingUtilities.invokeLater(this::showFileChangedDialog);
            }

        } catch (Exception e) {
            logError("Error in automatic reload check", e);
        }
    }

    /**
     * Coordinate reload with retry logic (schedules UI work on EDT)
     */
    private void reloadWithRetry(int attempt) {
        logInfo("Attempting to reload file (attempt " + attempt + "): " + currentFilePath);

        // Perform the reload on EDT since it involves UI operations
        SwingUtilities.invokeLater(() -> {
            try {
                executeReloadOnEDT();

            } catch (Exception e) {
                logDebug("Reload attempt " + attempt + " failed: " + e.getMessage());

                if (attempt < Config.MAX_RELOAD_ATTEMPTS) {
                    // Schedule retry with exponential backoff
                    long delay = Config.STABILITY_CHECK_INTERVAL_MS * attempt * attempt; // 100, 400, 900ms

                    // Ensure executor is available before scheduling
                    ensureExecutorAvailable();

                    try {
                        debounceExecutor.schedule(() ->
                            reloadWithRetry(attempt + 1),
                            delay, TimeUnit.MILLISECONDS);
                    } catch (java.util.concurrent.RejectedExecutionException ex) {
                        // Executor issues, retry immediately on EDT
                        SwingUtilities.invokeLater(() ->
                            reloadWithRetry(attempt + 1));
                    }
                } else {
                    // All attempts failed - show error
                    showReloadFailedDialog(e);
                }
            }
        });
    }

    /**
     * Execute the actual file reload operations on EDT (closes/opens packages, updates UI)
     */
    private void executeReloadOnEDT() throws Exception {
        // Temporarily disable file watching to avoid recursive notifications
        setEnabled(false);

        try {
            logInfo("Reloading file: '" + currentFilePath + "'");

            // Get the current package ID before closing
            String currentPackageId = controller.getMainPackageId();

            // Preserve file watcher during close/reload cycle
            if (currentPackageId != null) {
                logInfo("Closing package " + currentPackageId + " for reload");
                controller.setPreserveFileWatcherDuringReload(true);
                try {
                    controller.closePackage(currentPackageId, false);
                } finally {
                    controller.setPreserveFileWatcherDuringReload(false);
                }
            }

            // Reload the file - this involves UI operations
            org.enhydra.jxpdl.elements.Package pkg = controller.openPackageFromFile(currentFilePath);

            if (pkg != null) {
                // Success - finalize
                finalizeSuccessfulReload();
            } else {
                throw new IllegalStateException("Package loading returned null");
            }

        } catch (Exception e) {
            // Re-enable file watching on failure
            setEnabled(true);
            throw e; // Rethrow for retry logic
        }
    }

    /**
     * Finalize successful reload on EDT
     */
    private void finalizeSuccessfulReload() {
        logInfo("Restarting file watching after successful reload");

        // Ensure executor is available before scheduling restart
        ensureExecutorAvailable();

        try {
            // Restart file watching with a short delay
            debounceExecutor.schedule(() -> startWatching(currentFilePath),
                                    Config.UI_SETTLE_DELAY_MS, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.RejectedExecutionException e) {
            // Executor was shut down, restart watching directly
            logWarn("Executor unavailable during restart, starting watch directly");
            startWatching(currentFilePath);
        }
    }

    /**
     * Show error dialog when all reload attempts fail
     */
    private void showReloadFailedDialog(Exception e) {
        String message = "Could not reload file from disk after " + Config.MAX_RELOAD_ATTEMPTS + " attempts.\n\n" +
                       "The file may be corrupted, locked by another program, or contain errors.\n\n" +
                       "Error: " + e.getMessage();
        JOptionPane.showMessageDialog(controller.getJaWEFrame(),
            message,
            "File Reload Failed - " + controller.getAppTitle(),
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show dialog when file has changed but there are unsaved changes
     */
    private void showFileChangedDialog() {
        try {
            String message = "The file '" + currentFileName + "' has been modified by another program.\n\n" +
                           "You have unsaved changes in the editor.\n\n" +
                           "What would you like to do?";

            String[] options = {
                "Load new version from disk (discard my changes)",
                "Save my changes to disk",
                "Do nothing (keep editing)"
            };

            int choice = JOptionPane.showOptionDialog(
                controller.getJaWEFrame(),
                message,
                "File Changed - " + controller.getAppTitle(),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[Config.DIALOG_DEFAULT_OPTION]
            );

            switch (choice) {
                case Config.DIALOG_LOAD_FROM_DISK:
                    loadNewVersionFromDisk();
                    break;
                case Config.DIALOG_SAVE_CHANGES:
                    saveChanges();
                    break;
                case Config.DIALOG_DO_NOTHING:
                default:
                    // User chose to do nothing, just continue editing
                    break;
            }

        } catch (Exception e) {
            logError("Error showing file changed dialog", e);
        }
    }

    /**
     * Load new version from disk, discarding current changes
     */
    private void loadNewVersionFromDisk() {
        // User explicitly chose to reload - bypass unsaved changes check
        reloadWithRetry(1);
    }

    /**
     * Save current changes to disk
     */
    private void saveChanges() {
        try {
            logInfo("Saving current changes: " + currentFilePath);

            // Save the current package (savePackage method now handles file watching disable automatically)
            String mainPackageId = controller.getMainPackageId();
            if (mainPackageId != null) {
                controller.savePackage(mainPackageId, currentFilePath);
            }

        } catch (Exception e) {
            logError("Failed to save changes: " + currentFilePath, e);
        }
    }
}

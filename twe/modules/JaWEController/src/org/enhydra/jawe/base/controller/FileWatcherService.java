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
    private static final long DEBOUNCE_DELAY_MS = 1000; // Wait 1 second after last change
    private static final int MAX_STABILITY_CHECKS = 10; // Maximum attempts to verify file stability

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
                if (!debounceExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
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

        // Re-enable after the specified delay
        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                setEnabled(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "FileWatcher-ReEnable");
        delayThread.setDaemon(true);
        delayThread.start();
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
     * Check if a file is stable (not being written to) by verifying size, timestamp and content validity
     */
    private boolean isFileStable(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logDebug("File does not exist: " + filePath);
                return false;
            }

            // Perform multiple stability checks over a longer period
            for (int attempt = 1; attempt <= MAX_STABILITY_CHECKS; attempt++) {
                // Take initial measurements
                long initialSize = file.length();
                long initialModified = file.lastModified();

                // File must have content (not be empty)
                if (initialSize <= 0) {
                    logDebug("File is empty, attempt " + attempt + "/" + MAX_STABILITY_CHECKS + ": " + filePath);
                    if (attempt < MAX_STABILITY_CHECKS) {
                        Thread.sleep(100); // Wait 100ms and try again
                        continue;
                    }
                    return false;
                }

                // Wait and check for changes
                Thread.sleep(100); // 100ms between checks

                // Check if file changed during the wait
                long finalSize = file.length();
                long finalModified = file.lastModified();

                boolean stable = (initialSize == finalSize && initialModified == finalModified && finalSize > 0);

                if (stable) {
                    // File appears stable, now validate it's valid XPDL content
                    if (isValidXpdlContent(filePath)) {
                        logDebug("File stable and valid after " + attempt + " attempts: " + filePath);
                        return true;
                    } else {
                        logDebug("File stable but invalid XPDL content, attempt " + attempt + "/" + MAX_STABILITY_CHECKS + ": " + filePath);
                        if (attempt < MAX_STABILITY_CHECKS) {
                            continue; // Try again
                        }
                        return false;
                    }
                } else {
                    logDebug("File changed during check, attempt " + attempt + "/" + MAX_STABILITY_CHECKS + ": " + filePath +
                               " (size: " + initialSize + "->" + finalSize + ", modified: " + initialModified + "->" + finalModified + ")");
                }
            }

            // All attempts failed
            logWarn("File failed stability check after " + MAX_STABILITY_CHECKS + " attempts: " + filePath);
            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            logWarn("Error checking file stability: " + filePath, e);
            return false;
        }
    }

    /**
     * Check if file content appears to be valid XPDL (basic validation)
     */
    private boolean isValidXpdlContent(String filePath) {
        try {
            // Read first few hundred bytes to check if it looks like XML/XPDL
            byte[] buffer = new byte[512];
            try (java.io.FileInputStream fis = new java.io.FileInputStream(filePath)) {
                int bytesRead = fis.read(buffer);
                if (bytesRead <= 0) {
                    return false;
                }

                String content = new String(buffer, 0, bytesRead, "UTF-8").trim();

                // Basic checks for XPDL/XML structure
                return content.startsWith("<?xml") &&
                       (content.contains("<Package") || content.contains("<xpdl:Package")) &&
                       content.length() > 50; // Must have substantial content
            }
        } catch (Exception e) {
            logDebug("Error validating XPDL content: " + filePath + " - " + e.getMessage());
            return false;
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
                        // Check file stability before processing (not on EDT)
                        if (isFileStable(currentFilePath)) {
                            SwingUtilities.invokeLater(this::handleFileChanged);
                        } else {
                            logDebug("File not stable, rescheduling: " + currentFilePath);
                            // Reschedule for later if file is still being written
                            scheduleFileChangeProcessing();
                        }
                    }
                }, DEBOUNCE_DELAY_MS, TimeUnit.MILLISECONDS);

                logDebug("Scheduled debounced file change processing");
            } catch (java.util.concurrent.RejectedExecutionException e) {
                logDebug("Cannot schedule task, executor issue: " + e.getMessage());
                // Try to recreate executor for next time
                ensureExecutorAvailable();
            }
        }
    }

    /**
     * Handle file change event on the EDT thread
     * (File stability is verified before this method is called)
     */
    private void handleFileChanged() {
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
                // No unsaved changes - reload automatically
                reloadFile();
            } else {
                // Has unsaved changes - show dialog
                showFileChangedDialog();
            }

        } catch (Exception e) {
            logError("Error handling file change", e);
        }
    }

    /**
     * Reload the current file automatically
     */
    private void reloadFile() {
        performFileReload(false, true); // automatic reload, show success message
    }

    /**
     * Perform file reload with configurable behavior for different scenarios
     *
     * @param isUserInitiated true if user explicitly requested reload, false for automatic reload
     * @param showSuccessMessage true to show success notification to user
     */
    private void performFileReload(boolean isUserInitiated, boolean showSuccessMessage) {
        try {
            logInfo("Reloading file: " + currentFilePath);

            // Temporarily disable file watching to avoid recursive notifications
            setEnabled(false);

            // Final validation before attempting reload
            if (!isValidXpdlContent(currentFilePath)) {
                logWarn("File content invalid, aborting reload: " + currentFilePath);
                setEnabled(true);

                if (isUserInitiated) {
                    // Show error message to user for manual reload attempts
                    String message = "Cannot load file from disk. The file appears to be empty or contains invalid XPDL content.";
                    JOptionPane.showMessageDialog(controller.getJaWEFrame(),
                        message,
                        "File Load Error - " + controller.getAppTitle(),
                        JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            // Get the current package ID before closing
            String currentPackageId = controller.getMainPackageId();

            // Preserve file watcher during close/reload cycle to maintain proper UI state
            if (currentPackageId != null) {
                logInfo("Closing package " + currentPackageId + " for reload");
                controller.setPreserveFileWatcherDuringReload(true);
                try {
                    controller.closePackage(currentPackageId, false);
                } finally {
                    controller.setPreserveFileWatcherDuringReload(false);
                }
            }

            // Reload the file
            logInfo("Opening file: '" + currentFilePath + "' (length=" +
                      (currentFilePath != null ? currentFilePath.length() : "null") + ")");

            if (currentFilePath == null || currentFilePath.trim().isEmpty()) {
                logError("Current file path is null or empty, aborting reload", new IllegalStateException("Null file path"));
                setEnabled(true);
                return;
            }

            org.enhydra.jxpdl.elements.Package pkg = controller.openPackageFromFile(currentFilePath);

            // Restart file watching for the reloaded file
            if (pkg != null && currentFilePath != null) {
                logInfo("Restarting file watching after reload");

                // Use a delayed restart to avoid race conditions
                Thread restartThread = new Thread(() -> {
                    try {
                        Thread.sleep(100); // Short delay to let UI settle
                        logInfo("Executing delayed restart");
                        startWatching(currentFilePath);
                    } catch (Exception e) {
                        logError("Error during delayed restart", e);
                        setEnabled(true);
                    }
                }, "FileWatcher-Restart");
                restartThread.setDaemon(true);
                restartThread.start();

                // Show success notification if requested
                if (showSuccessMessage) {
                    String message = "File has been automatically reloaded from disk.";
                    JOptionPane.showMessageDialog(controller.getJaWEFrame(),
                        message,
                        controller.getAppTitle(),
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                logWarn("Reload returned null package: " + currentFilePath);
                // Re-enable file watching even if reload failed
                setEnabled(true);
            }

        } catch (Exception e) {
            // Re-enable file watching even if reload failed
            setEnabled(true);
            logError("Failed to reload file: " + currentFilePath, e);

            if (isUserInitiated) {
                // Show error message to user for manual reload attempts
                try {
                    String message = "Failed to load new version from disk. The file may have been corrupted or contains errors.\n\n" +
                                   "Error: " + e.getMessage();
                    JOptionPane.showMessageDialog(controller.getJaWEFrame(),
                        message,
                        "File Load Error - " + controller.getAppTitle(),
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception dialogException) {
                    logError("Error showing error dialog", dialogException);
                }
            }
        }
    }

    /**
     * Show dialog when file has changed but there are unsaved changes
     */
    private void showFileChangedDialog() {
        try {
            ControllerSettings settings = (ControllerSettings) controller.getSettings();

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
                options[2] // Default to "Do nothing"
            );

            switch (choice) {
                case 0: // Load new version from disk
                    loadNewVersionFromDisk();
                    break;
                case 1: // Save my changes
                    saveChanges();
                    break;
                case 2: // Do nothing
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
        performFileReload(true, false); // user-initiated reload, no success message
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

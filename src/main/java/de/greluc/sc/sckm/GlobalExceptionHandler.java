/**************************************************************************************************
 * SC Kill Monitor                                                                                *
 * Copyright (C) 2025-2025 SC Kill Monitor Team                                                   *
 *                                                                                                *
 * This file is part of SC Kill Monitor.                                                          *
 *                                                                                                *
 * SC Kill Monitor is free software: you can redistribute it and/or modify                        *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 *                                                                                                *
 * SC Kill Monitor is distributed in the hope that it will be useful,                             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 *                                                                                                *
 * You should have received a copy of the GNU General Public License                              *
 * along with SC Kill Monitor. If not, see https://www.gnu.org/licenses/                          *
 **************************************************************************************************/

package de.greluc.sc.sckm;

import de.greluc.sc.sckm.exceptions.ConnectionException;
import de.greluc.sc.sckm.exceptions.DownloadException;
import de.greluc.sc.sckm.exceptions.IntegrityException;
import de.greluc.sc.sckm.exceptions.ParseException;
import de.greluc.sc.sckm.exceptions.ScKillMonitorException;
import de.greluc.sc.sckm.exceptions.UpdateException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import org.jetbrains.annotations.NotNull;

/**
 * Global exception handler for the SC Kill Monitor application.
 * This class provides centralized exception handling for the entire application,
 * ensuring consistent error reporting and user feedback.
 *
 * <p>The handler categorizes exceptions into different types and provides
 * appropriate error messages and logging for each type. It also determines
 * whether the application should continue running or exit after an exception.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Initializes the global exception handler for the application.
     * This method should be called during application startup.
     */
    public static void initialize() {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        
        // Set handler for JavaFX thread
        Platform.runLater(() -> {
            Thread.currentThread().setUncaughtExceptionHandler(new GlobalExceptionHandler());
        });
        
        log.info("Global exception handler initialized");
    }

    /**
     * Handles an uncaught exception from any thread in the application.
     *
     * @param thread the thread where the exception occurred
     * @param throwable the uncaught exception
     */
    @Override
    public void uncaughtException(@NotNull Thread thread, @NotNull Throwable throwable) {
        log.error("Uncaught exception in thread: {}", thread.getName(), throwable);
        
        // Handle the exception based on its type
        Platform.runLater(() -> handleException(throwable, true));
    }
    
    /**
     * Handles an exception that was caught in the application code.
     * This method can be called directly from catch blocks to ensure
     * consistent exception handling throughout the application.
     *
     * @param throwable the exception to handle
     * @param isCritical whether the exception is critical and should potentially terminate the application
     * @return true if the application should continue, false if it should exit
     */
    public static boolean handleException(Throwable throwable, boolean isCritical) {
        // Log the exception
        if (isCritical) {
            log.error("Critical exception occurred", throwable);
        } else {
            log.warn("Non-critical exception occurred", throwable);
        }
        
        // Determine the appropriate alert type and message based on the exception type
        String title = "Error";
        String header = "An error occurred";
        String content = "An unexpected error occurred in the application.";
        boolean shouldExit = isCritical;
        
        if (throwable instanceof ScKillMonitorException) {
            // Handle application-specific exceptions
          switch (throwable) {
            case ConnectionException connectionException -> {
              title = "Connection Error";
              header = "Connection Error";
              content = "Failed to connect to the update server. Please check your internet connection and try again.";
              shouldExit = false;
            }
            case DownloadException downloadException -> {
              title = "Download Error";
              header = "Download Error";
              content = "Failed to download the update file. " + throwable.getMessage();
              shouldExit = false;
            }
            case IntegrityException integrityException -> {
              title = "Security Warning";
              header = "Security Warning";
              content = "The downloaded update file failed integrity verification. This could indicate tampering or corruption. " +
                  "Please try again later or download the update manually from the official website.";
              shouldExit = false;
            }
            case ParseException parseException -> {
              title = "Parse Error";
              header = "Parse Error";
              content = "Failed to parse the response from the update server. " + throwable.getMessage();
              shouldExit = false;
            }
            case UpdateException updateException -> {
              title = "Update Error";
              header = "Update Error";
              content = "An error occurred during the update process. " + throwable.getMessage();
              shouldExit = false;
            }
            default ->
              // Generic application exception
                content = throwable.getMessage();
          }
        } else if (throwable instanceof IOException) {
            // Handle common IO exceptions
            title = "I/O Error";
            header = "I/O Error";
            
            if (throwable instanceof ConnectException || 
                throwable instanceof SocketTimeoutException || 
                throwable instanceof UnknownHostException) {
                content = "Failed to connect to the server. Please check your internet connection and try again.";
                shouldExit = false;
            } else if (throwable instanceof AccessDeniedException) {
                content = "Access denied. You don't have permission to access the requested file or directory.";
                shouldExit = false;
            } else if (throwable instanceof NoSuchFileException) {
                content = "The requested file or directory does not exist.";
                shouldExit = false;
            } else {
                content = "An I/O error occurred: " + throwable.getMessage();
            }
        } else if (throwable instanceof NullPointerException) {
            header = "Application Error";
            content = "The application encountered a null reference. This is likely a bug in the application.";
        } else if (throwable instanceof IllegalArgumentException) {
            header = "Invalid Input";
            content = "Invalid input provided: " + throwable.getMessage();
            shouldExit = false;
        } else if (throwable instanceof IllegalStateException) {
            header = "Invalid State";
            content = "The application is in an invalid state: " + throwable.getMessage();
        }
        
        // Show the alert to the user
        final boolean finalShouldExit = shouldExit;
        final String finalTitle = title;
        final String finalHeader = header;
        final String finalContent = content;
        
        Platform.runLater(() -> {
            Alert.AlertType alertType = finalShouldExit ? Alert.AlertType.ERROR : Alert.AlertType.WARNING;
            AlertHandler.showAlert(alertType, finalHeader, finalContent, true);
            
            // Exit the application if the exception is critical
            if (finalShouldExit) {
                log.error("Exiting application due to critical error");
                System.exit(1);
            }
        });
        
        return !shouldExit;
    }
}
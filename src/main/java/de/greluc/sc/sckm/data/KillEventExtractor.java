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

package de.greluc.sc.sckm.data;

import static de.greluc.sc.sckm.FileHandler.writeKillEventToFile;

import de.greluc.sc.sckm.AlertHandler;
import de.greluc.sc.sckm.settings.SettingsData;
import de.greluc.sc.sckm.util.PathSanitizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * The KillEventExtractor class is responsible for extracting and processing kill events from game
 * log files. These events are represented by {@link KillEvent} objects containing detailed
 * information about each kill event such as timestamp, killingPlayer, killed player, weapon used, and
 * location.
 *
 * <p>This implementation includes optimizations for handling very large log files efficiently:
 * <ul>
 *   <li>Chunked processing for large files</li>
 *   <li>Incremental parsing to avoid re-processing the entire file on each scan</li>
 *   <li>Caching mechanisms for frequently accessed data</li>
 *   <li>Parallel processing for large files</li>
 * </ul>
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.7.0
 * @since 1.2.1
 */
@Log4j2
public class KillEventExtractor {
  // File position tracking for incremental parsing
  private static final Map<String, Long> lastProcessedPositions = new ConcurrentHashMap<>();

  // Cache for parsed kill events to avoid re-parsing
  private static final Map<String, Map<String, KillEvent>> killEventCache = new ConcurrentHashMap<>();

  // Constants for chunked processing
  private static final int CHUNK_SIZE = 10 * 1024 * 1024; // 10MB chunks
  private static final int LARGE_FILE_THRESHOLD = 50 * 1024 * 1024; // 50MB
  private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

  /**
   * Extracts kill events from a log file using optimized techniques for improved performance.
   * This method implements several optimizations:
   * <ul>
   *   <li>Incremental parsing - only processes new content since the last scan</li>
   *   <li>Chunked processing - processes large files in manageable chunks</li>
   *   <li>Parallel processing - uses multiple threads for large files</li>
   *   <li>Caching - stores parsed events to avoid redundant processing</li>
   *   <li>Memory-mapped file access - for efficient file reading</li>
   * </ul>
   *
   * <p>Only new kill events involving the user (as determined by the {@code SettingsData}) are added to the
   * list. The extracted kill events are sorted in descending order by their timestamp.
   *
   * <p>If enabled in the {@code SettingsData}, new kill events are also written to an output file.
   *
   * <p>If the specified log file cannot be read, an error alert is displayed to the user and the
   * method returns false.
   *
   * @param killEvents A list of {@link KillEvent} to which detected kill events will be added.
   * @param inputFilePath The file path to the log file to be read for extracting kill events.
   * @param scanStartTime The start time of the scanning process, used for file naming when writing
   *     kill events.
   * @return true if the extraction was successful, false otherwise
   */
  public static boolean extractKillEvents(
      @NotNull List<KillEvent> killEvents,
      @NotNull String inputFilePath,
      @NotNull ZonedDateTime scanStartTime) {
    AtomicBoolean isWriteSuccessful = new AtomicBoolean(true);

    // Sanitize the input file path to prevent path traversal attacks
    String sanitizedPath = PathSanitizer.sanitizePath(inputFilePath);
    if (sanitizedPath.isEmpty()) {
      Platform.runLater(
          () ->
              AlertHandler.showAlert(
                  Alert.AlertType.ERROR,
                  "Invalid log file path",
                  "The specified log file path is invalid or contains illegal characters.", false));
      log.error("Invalid log file path: {}", inputFilePath);
      return false;
    }

    Path path = Paths.get(sanitizedPath);
    if (!Files.exists(path)) {
      Platform.runLater(
          () ->
              AlertHandler.showAlert(
                  Alert.AlertType.ERROR,
                  "Failed to read log file",
                  "Please check if the file exists and the path is set correctly.", false));
      log.error("Failed to find the specified log file: {}", sanitizedPath);
      return false;
    }

    try {
      // Get file size
      long fileSize = Files.size(path);
      if (fileSize == 0) {
        log.info("Log file is empty: {}", sanitizedPath);
        return true;
      }

      // Get the last processed position for this file
      long lastPosition = lastProcessedPositions.getOrDefault(sanitizedPath, 0L);

      // If the file size is smaller than the last position, the file might have been truncated or replaced
      if (fileSize < lastPosition) {
        log.info("File size ({}) is smaller than last processed position ({}). Resetting position.", 
            fileSize, lastPosition);
        lastPosition = 0;
        // Clear the cache for this file
        killEventCache.remove(sanitizedPath);
      }

      // If we've already processed the entire file and it hasn't changed, return early
      if (lastPosition == fileSize) {
        log.debug("No new content in file since last scan: {}", sanitizedPath);
        return true;
      }

      // Initialize or get the cache for this file
      Map<String, KillEvent> fileCache = killEventCache.computeIfAbsent(sanitizedPath, k -> new ConcurrentHashMap<>());

      // Determine if we should use parallel processing based on file size and amount of new data
      boolean useParallel = fileSize > LARGE_FILE_THRESHOLD && (fileSize - lastPosition) > LARGE_FILE_THRESHOLD;

      log.debug("Processing file: {}, size: {}, last position: {}, using parallel: {}", 
          sanitizedPath, fileSize, lastPosition, useParallel);

      // Process the file
      if (useParallel) {
        // For large files, process in parallel chunks
        processLargeFileInParallel(path, lastPosition, fileSize, killEvents, fileCache, isWriteSuccessful, scanStartTime);
      } else {
        // For smaller files, process sequentially
        processFileSequentially(path, lastPosition, fileSize, killEvents, fileCache, isWriteSuccessful, scanStartTime);
      }

      // Update the last processed position
      lastProcessedPositions.put(sanitizedPath, fileSize);

      // Sort the kill events by timestamp in descending order
      killEvents.sort(Comparator.comparing(KillEvent::timestamp, Comparator.reverseOrder()));

      return isWriteSuccessful.get();
    } catch (IOException ioException) {
      Platform.runLater(
          () ->
              AlertHandler.showAlert(
                  Alert.AlertType.ERROR,
                  "Failed to read log file",
                  "An error occurred while reading the log file.", false));
      log.error("Failed to read the specified log file: {}", sanitizedPath);
      log.trace("Stacktrace:", ioException);
      return false;
    }
  }

  /**
   * Processes a large file in parallel chunks for improved performance.
   * 
   * @param path The path to the file
   * @param startPosition The position to start reading from
   * @param fileSize The total size of the file
   * @param killEvents The list to add kill events to
   * @param fileCache The cache for this file
   * @param isWriteSuccessful Flag to track write success
   * @param scanStartTime The scan start time for file naming
   * @throws IOException If an I/O error occurs
   */
  private static void processLargeFileInParallel(
      Path path, 
      long startPosition, 
      long fileSize, 
      List<KillEvent> killEvents,
      Map<String, KillEvent> fileCache,
      AtomicBoolean isWriteSuccessful,
      ZonedDateTime scanStartTime) throws IOException {

    log.debug("Processing large file in parallel: {}", path);

    // Calculate the number of chunks
    int numChunks = (int) Math.ceil((double) (fileSize - startPosition) / CHUNK_SIZE);
    int numThreads = Math.min(numChunks, MAX_THREADS);

    // Create a thread pool
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<List<KillEvent>>> futures = new ArrayList<>();

    try {
      // Submit tasks for each chunk
      for (int i = 0; i < numChunks; i++) {
        long chunkStart = startPosition + (i * (long) CHUNK_SIZE);
        long chunkEnd = Math.min(chunkStart + CHUNK_SIZE, fileSize);

        // Submit the task
        futures.add(executor.submit(() -> 
            processFileChunk(path, chunkStart, chunkEnd, fileCache, isWriteSuccessful, scanStartTime)));
      }

      // Collect results
      for (Future<List<KillEvent>> future : futures) {
        try {
          List<KillEvent> chunkEvents = future.get();
          for (KillEvent event : chunkEvents) {
            if (!killEvents.contains(event)) {
              killEvents.add(event);
            }
          }
        } catch (Exception e) {
          log.error("Error processing file chunk", e);
          isWriteSuccessful.set(false);
        }
      }
    } finally {
      executor.shutdown();
    }
  }

  /**
   * Processes a file sequentially from the given start position.
   * 
   * @param path The path to the file
   * @param startPosition The position to start reading from
   * @param fileSize The total size of the file
   * @param killEvents The list to add kill events to
   * @param fileCache The cache for this file
   * @param isWriteSuccessful Flag to track write success
   * @param scanStartTime The scan start time for file naming
   * @throws IOException If an I/O error occurs
   */
  private static void processFileSequentially(
      Path path, 
      long startPosition, 
      long fileSize, 
      List<KillEvent> killEvents,
      Map<String, KillEvent> fileCache,
      AtomicBoolean isWriteSuccessful,
      ZonedDateTime scanStartTime) throws IOException {

    log.debug("Processing file sequentially: {}", path);

    try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
      // Map the file into memory starting from the last processed position
      long sizeToMap = fileSize - startPosition;
      MappedByteBuffer buffer = fileChannel.map(
          FileChannel.MapMode.READ_ONLY, startPosition, sizeToMap);

      // Process the file line by line
      StringBuilder lineBuilder = new StringBuilder();
      byte b;
      while (buffer.hasRemaining()) {
        b = buffer.get();
        if (b == '\n') {
          String line = lineBuilder.toString();
          processLine(line, killEvents, fileCache, isWriteSuccessful, scanStartTime);
          lineBuilder.setLength(0); // Clear the buffer for the next line
        } else if (b == '\r') {
          // Skip carriage return
          continue;
        } else {
          lineBuilder.append((char) b);
        }
      }

      // Process the last line if it doesn't end with a newline
      if (lineBuilder.length() > 0) {
        String line = lineBuilder.toString();
        processLine(line, killEvents, fileCache, isWriteSuccessful, scanStartTime);
      }
    }
  }

  /**
   * Processes a chunk of a file and returns the kill events found.
   * 
   * @param path The path to the file
   * @param startPosition The start position of the chunk
   * @param endPosition The end position of the chunk
   * @param fileCache The cache for this file
   * @param isWriteSuccessful Flag to track write success
   * @param scanStartTime The scan start time for file naming
   * @return A list of kill events found in this chunk
   * @throws IOException If an I/O error occurs
   */
  private static List<KillEvent> processFileChunk(
      Path path, 
      long startPosition, 
      long endPosition, 
      Map<String, KillEvent> fileCache,
      AtomicBoolean isWriteSuccessful,
      ZonedDateTime scanStartTime) throws IOException {

    List<KillEvent> chunkEvents = new ArrayList<>();

    try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
      // Map the chunk into memory
      long sizeToMap = endPosition - startPosition;
      MappedByteBuffer buffer = fileChannel.map(
          FileChannel.MapMode.READ_ONLY, startPosition, sizeToMap);

      // Process the chunk line by line
      StringBuilder lineBuilder = new StringBuilder();
      byte b;
      while (buffer.hasRemaining()) {
        b = buffer.get();
        if (b == '\n') {
          String line = lineBuilder.toString();
          processLine(line, chunkEvents, fileCache, isWriteSuccessful, scanStartTime);
          lineBuilder.setLength(0); // Clear the buffer for the next line
        } else if (b == '\r') {
          // Skip carriage return
          continue;
        } else {
          lineBuilder.append((char) b);
        }
      }

      // Process the last line if it doesn't end with a newline
      if (lineBuilder.length() > 0) {
        String line = lineBuilder.toString();
        processLine(line, chunkEvents, fileCache, isWriteSuccessful, scanStartTime);
      }
    }

    return chunkEvents;
  }

  /**
   * Processes a single line from the log file.
   * 
   * @param line The line to process
   * @param killEvents The list to add kill events to
   * @param fileCache The cache for this file
   * @param isWriteSuccessful Flag to track write success
   * @param scanStartTime The scan start time for file naming
   */
  private static void processLine(
      String line, 
      List<KillEvent> killEvents, 
      Map<String, KillEvent> fileCache,
      AtomicBoolean isWriteSuccessful,
      ZonedDateTime scanStartTime) {

    if (line.contains("<Actor Death>") && isWriteSuccessful.get()) {
      // Check if this line is already in the cache
      if (fileCache.containsKey(line)) {
        KillEvent cachedEvent = fileCache.get(line);
        if ((cachedEvent.killedPlayer().equalsIgnoreCase(SettingsData.getHandle())
                || cachedEvent.killingPlayer().equalsIgnoreCase(SettingsData.getHandle()))
            && !killEvents.contains(cachedEvent)) {
          killEvents.add(cachedEvent);
          log.debug("Retrieved cached kill event: {}", cachedEvent);

          if (SettingsData.isWriteKillEventToFile()) {
            isWriteSuccessful.set(writeKillEventToFile(
                cachedEvent,
                scanStartTime.format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss"))));
          }
        }
      } else {
        // Parse the line and add to cache if it's a valid kill event
        Optional<KillEvent> eventOpt = parseKillEvent(line);
        eventOpt.ifPresent(killEvent -> {
          // Add to cache
          fileCache.put(line, killEvent);

          if ((killEvent.killedPlayer().equalsIgnoreCase(SettingsData.getHandle())
                  || killEvent.killingPlayer().equalsIgnoreCase(SettingsData.getHandle()))
              && !killEvents.contains(killEvent)) {
            killEvents.add(killEvent);
            log.info("New kill event detected");
            log.debug("Kill Event:\n{}", killEvent);

            if (SettingsData.isWriteKillEventToFile()) {
              isWriteSuccessful.set(writeKillEventToFile(
                  killEvent,
                  scanStartTime.format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss"))));
            }
          }
        });
      }
    }
  }

  /**
   * Parses a log line to extract and create a {@link KillEvent} object containing details about a
   * kill event. The log line is expected to follow a specific format with markers indicating
   * relevant information such as timestamps, players involved, weapons, and other details.
   *
   * <p>If the log line cannot be parsed correctly, an empty {@link Optional} is returned, and an
   * error is logged.
   *
   * @param logLine the log line containing details of the kill event; must not be null
   * @return an {@link Optional} containing the parsed {@link KillEvent} if successful, or an empty
   *     {@link Optional} if parsing fails
   */
  private static @NotNull Optional<KillEvent> parseKillEvent(@NotNull String logLine) {
    try {
      String timestamp = logLine.substring(logLine.indexOf('<') + 1, logLine.indexOf('>'));
      String killedPlayer = extractValue(logLine, "CActor::Kill: '", "'");
      String zone = extractValue(logLine, "in zone '", "'");
      String killer = extractValue(logLine, "killed by '", "'");
      String weapon = extractValue(logLine, "using '", "'");
      String weaponClass = extractValue(logLine, "[Class ", "]");
      String damageType = extractValue(logLine, "with damage type '", "'");

      return Optional.of(
          new KillEvent(
              UUID.nameUUIDFromBytes(timestamp.getBytes()),
              ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME),
              killedPlayer,
              killer,
              weapon,
              weaponClass,
              damageType,
              zone));
    } catch (Exception exception) {
      log.error("Failed to parse log line: {}", logLine);
      log.trace("Stacktrace:", exception);
      return Optional.empty();
    }
  }

  /**
   * Extracts a substring between the specified start and end tokens within a given text.
   *
   * @param text The input string from which the value should be extracted. Must not be null.
   * @param startToken The starting delimiter of the substring to extract. Must not be null.
   * @param endToken The ending delimiter of the substring to extract. Must not be null.
   * @return The extracted substring if both tokens are found; otherwise, an empty string.
   */
  @SuppressWarnings("SameParameterValue")
  private static @NotNull String extractValue(
      @NotNull String text, @NotNull String startToken, @NotNull String endToken) {
    int startIndex = text.indexOf(startToken);
    if (startIndex == -1) {
      return "";
    }
    startIndex += startToken.length();
    int endIndex = text.indexOf(endToken, startIndex);
    if (endIndex == -1) {
      return "";
    }
    return text.substring(startIndex, endIndex);
  }

  /**
   * Clears the cache for a specific file or all files if no path is provided.
   * This can be useful when the application needs to force a full re-scan of log files.
   * 
   * @param filePath Optional file path to clear cache for a specific file
   */
  public static void clearCache(String filePath) {
    if (filePath != null && !filePath.isEmpty()) {
      String sanitizedPath = PathSanitizer.sanitizePath(filePath);
      if (!sanitizedPath.isEmpty()) {
        log.debug("Clearing cache for file: {}", sanitizedPath);
        lastProcessedPositions.remove(sanitizedPath);
        killEventCache.remove(sanitizedPath);
      }
    } else {
      log.debug("Clearing all caches");
      lastProcessedPositions.clear();
      killEventCache.clear();
    }
  }

  /**
   * Returns the current cache statistics for debugging and monitoring purposes.
   * 
   * @return A string containing cache statistics
   */
  public static String getCacheStats() {
    int totalCachedFiles = lastProcessedPositions.size();
    int totalCachedEvents = 0;

    for (Map<String, KillEvent> fileCache : killEventCache.values()) {
      totalCachedEvents += fileCache.size();
    }

    return String.format("Files tracked: %d, Total cached events: %d", 
        totalCachedFiles, totalCachedEvents);
  }
}

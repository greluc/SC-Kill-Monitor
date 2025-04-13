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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
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
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.5.0
 * @since 1.2.1
 */
@Log4j2
public class KillEventExtractor {

  /**
   * Extracts kill events from a log file and populates the provided list of {@link KillEvent}s. Only
   * new kill events involving the user (as determined by the {@code SettingsData}) are added to the
   * list. The extracted kill events are sorted in descending order by their timestamp.
   *
   * <p>If enabled in the {@code SettingsData}, new kill events are also written to an output file.
   *
   * <p>If the specified log file cannot be read, an error alert is displayed to the user and the
   * method throws an {@link IOException}.
   *
   * @param killEvents A list of {@link KillEvent} to which detected kill events will be added.
   * @param inputFilePath The file path to the log file to be read for extracting kill events.
   * @param scanStartTime The start time of the scanning process, used for file naming when writing
   *     kill events.
   */
  public static boolean extractKillEvents(
      @NotNull List<KillEvent> killEvents,
      @NotNull String inputFilePath,
      @NotNull ZonedDateTime scanStartTime) {
    AtomicBoolean isWriteSuccesfull = new AtomicBoolean(true);
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("<Actor Death>") && isWriteSuccesfull.get()) {
          Optional<KillEvent> event = parseKillEvent(line);
          event.ifPresent(
              killEvent -> {
                if ((killEvent.killedPlayer().equalsIgnoreCase(SettingsData.getHandle())
                        || killEvent.killingPlayer().equalsIgnoreCase(SettingsData.getHandle()))
                    && !killEvents.contains(killEvent)) {
                  killEvents.add(killEvent);
                  log.info("New kill event detected");
                  log.debug("Kill Event:\n{}", killEvent);
                  if (SettingsData.isWriteKillEventToFile()) {
                    isWriteSuccesfull.set(writeKillEventToFile(
                        killEvent,
                        scanStartTime.format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss"))));
                  }
                }
              });
        }
      }
      killEvents.sort(Comparator.comparing(KillEvent::timestamp, Comparator.reverseOrder()));
      return isWriteSuccesfull.get();
    } catch (IOException ioException) {
      Platform.runLater(
          () ->
              AlertHandler.showAlert(
                  Alert.AlertType.ERROR,
                  "Failed to read log file",
                  "Please check if the file exists and the path is set correctly."));
      log.error("Failed to find the specified log file: {}", inputFilePath);
      log.trace("Stacktrace:", ioException);
      return false;
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
              UUID.nameUUIDFromBytes(timestamp. getBytes()),
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
}

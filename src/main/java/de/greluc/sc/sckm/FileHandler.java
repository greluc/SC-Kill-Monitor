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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.greluc.sc.sckm.data.KillEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * This class provides various utilities for handling file operations.
 *
 * <p>The FileHandler class currently includes a method for opening a file chooser dialog that
 * allows the user to select a specific file, filtered by a predefined file type. It utilizes JavaFX
 * FileChooser for the user interface and logs relevant information for debugging purposes.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.0.0
 * @version 1.3.0
 */
@Log4j2
public class FileHandler {

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private FileHandler() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Displays a file chooser dialog that filters files based on a predefined extension.
   * Specifically, it allows the user to select files with the ".log" extension.
   *
   * <p>The chosen file is wrapped in an {@link Optional}. If no file is selected, the returned
   * {@link Optional} will be empty.
   *
   * @return an {@link Optional} containing the selected {@link File}, or an empty {@link Optional}
   *     if no file is chosen.
   */
  public static @NotNull Optional<File> openFileChooser() {
    log.debug("Trying to choose a file!");
    final var fileType = "*.log";
    final var filter = new FileChooser.ExtensionFilter("log File", fileType);
    final var chooser = new FileChooser();
    chooser.getExtensionFilters().add(filter);
    return Optional.ofNullable(chooser.showOpenDialog(null));
  }

  /**
   * Writes information about a KillEvent to a log file in JSON format. The log file name is
   * determined by appending the provided file suffix to a predefined file name pattern.
   *
   * @param killEvent The KillEvent object containing details about the kill event to be logged.
   * @param fileSuffix The suffix to append to the log file name, typically used to differentiate
   *     between different log files or contexts.
   */
  public static void writeKillEventToFile(
      @NotNull KillEvent killEvent, @NotNull String fileSuffix) {
    log.debug("Appending KillEvent to file in JSON format.");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    File file = new File(String.format("logs/kill-events_%s.log", fileSuffix));
    try (FileWriter writer = new FileWriter(file, true)) {
      String json = objectMapper.writeValueAsString(killEvent);
      if (file.length() > 0) {
        writer.write("," + System.lineSeparator());
      }
      writer.write(json);
      log.info("KillEvent successfully written to file: {}", file.getAbsolutePath());
    } catch (IOException e) {
      log.error("Error while writing KillEvent to file", e);
    }
  }
}

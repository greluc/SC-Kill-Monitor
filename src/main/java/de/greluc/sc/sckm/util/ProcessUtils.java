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

package de.greluc.sc.sckm.util;

import de.greluc.sc.sckm.Constants;
import java.io.IOException;
import java.nio.file.Path;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for process execution operations.
 * 
 * <p>This class provides methods for launching external processes
 * with proper error handling and logging.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class ProcessUtils {

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private ProcessUtils() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Launches a Windows MSI installer.
   *
   * @param msiPath The path to the MSI file
   * @return The Process object representing the launched process
   * @throws IOException If an I/O error occurs
   */
  public static @NotNull Process launchMsiInstaller(@NotNull Path msiPath) throws IOException {
    // Get the absolute path to the MSI file
    String msiPathStr = msiPath.toAbsolutePath().toString();
    
    // Create ProcessBuilder with command and arguments as separate elements
    ProcessBuilder processBuilder = new ProcessBuilder("msiexec", "/i", msiPathStr);
    
    log.info("Launching MSI installer: {}", msiPathStr);
    
    // Start the process
    return processBuilder.start();
  }

  /**
   * Launches an external process with the specified command and arguments.
   *
   * @param command The command to execute
   * @param args The arguments to pass to the command
   * @return The Process object representing the launched process
   * @throws IOException If an I/O error occurs
   */
  public static @NotNull Process launchProcess(@NotNull String command, String... args) throws IOException {
    // Create ProcessBuilder with command and arguments
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command().add(command);
    for (String arg : args) {
      processBuilder.command().add(arg);
    }
    
    log.info("Launching process: {} {}", command, String.join(" ", args));
    
    // Start the process
    return processBuilder.start();
  }
}
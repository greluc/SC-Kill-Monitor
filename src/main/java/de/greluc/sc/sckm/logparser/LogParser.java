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

package de.greluc.sc.sckm.logparser;

import de.greluc.sc.sckm.data.KillEvent;
import java.time.ZonedDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Interface defining the contract for log parsers that extract kill events from game log files.
 * 
 * <p>Implementations of this interface are responsible for parsing log files and extracting
 * kill events according to their specific format and rules. This allows for different log
 * parsing strategies to be implemented and swapped as needed.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.7.0
 * @since 1.7.0
 */
public interface LogParser {
  
  /**
   * Extracts kill events from a log file.
   *
   * @param killEvents A list of {@link KillEvent} to which detected kill events will be added.
   * @param inputFilePath The file path to the log file to be read for extracting kill events.
   * @param scanStartTime The start time of the scanning process, used for file naming when writing
   *     kill events.
   * @return true if the extraction was successful, false otherwise
   */
  boolean extractKillEvents(
      @NotNull List<KillEvent> killEvents,
      @NotNull String inputFilePath,
      @NotNull ZonedDateTime scanStartTime);
  
  /**
   * Clears the cache for a specific file or all files if no path is provided.
   * This can be useful when the application needs to force a full re-scan of log files.
   * 
   * @param filePath Optional file path to clear cache for a specific file
   */
  void clearCache(String filePath);
  
  /**
   * Returns the current cache statistics for debugging and monitoring purposes.
   * 
   * @return A string containing cache statistics
   */
  String getCacheStats();
}
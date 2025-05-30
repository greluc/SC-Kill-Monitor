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

package de.greluc.sc.sckm.constants;

import de.greluc.sc.sckm.Constants;
import lombok.Generated;

/**
 * This class contains message constants used throughout the application.
 *
 * <p>It includes constants for log messages and user-facing messages.
 * The class is designed to prevent instantiation as it serves only as a holder for constant values.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.6.0
 * @version 1.6.0
 */
public class MessageConstants {
  // Log messages
  public static final String LOG_GLOBAL_EXCEPTION_HANDLER_INITIALIZED = 
      "Global exception handler initialized";
  public static final String LOG_UNCAUGHT_EXCEPTION = "Uncaught exception in thread: {}";
  public static final String LOG_CRITICAL_EXCEPTION = "Critical exception occurred";
  public static final String LOG_NON_CRITICAL_EXCEPTION = "Non-critical exception occurred";
  public static final String LOG_EXITING_APPLICATION = "Exiting application due to critical error";
  public static final String LOG_STARTING_SCAN = "Starting scan for kill events...";
  public static final String LOG_USING_HANDLE = "Using the selected handle: {}";
  public static final String LOG_USING_INTERVAL = "Using the selected interval: {}";
  public static final String LOG_USING_CHANNEL = "Using the selected channel: {}";
  public static final String LOG_USING_PATH = "Using the selected log file path: {}";
  public static final String LOG_FINISHED_EXTRACTING = "Finished extracting kill events";
  public static final String LOG_FINISHED_UPDATING_GUI = 
      "Finished updating the GUI with kill events";
  public static final String LOG_SCAN_THREAD_INTERRUPTED = 
      "Scan thread was interrupted. Terminating...";
  public static final String LOG_COULD_NOT_LOAD_MAIN_VIEW = "Could not load main view";

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private MessageConstants() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }
}

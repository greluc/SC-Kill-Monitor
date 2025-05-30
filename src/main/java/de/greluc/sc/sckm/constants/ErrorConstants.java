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
 * This class contains error-related constants used throughout the application.
 *
 * <p>It includes constants for error messages, error titles, and error categories.
 * The class is designed to prevent instantiation as it serves only as a holder for constant values.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.6.0
 * @version 1.6.0
 */
public class ErrorConstants {
  // Generic error messages
  public static final String ERROR_TITLE = "Error";
  public static final String ERROR_HEADER = "An error occurred";
  public static final String ERROR_CONTENT = "An unexpected error occurred in the application.";

  // Connection error messages
  public static final String CONNECTION_ERROR_TITLE = "Connection Error";
  public static final String CONNECTION_ERROR_HEADER = "Connection Error";
  public static final String CONNECTION_ERROR_CONTENT = 
      "Failed to connect to the update server. "
      + "Please check your internet connection and try again.";

  // Download error messages
  public static final String DOWNLOAD_ERROR_TITLE = "Download Error";
  public static final String DOWNLOAD_ERROR_HEADER = "Download Error";
  public static final String DOWNLOAD_ERROR_CONTENT_PREFIX = "Failed to download the update file. ";

  // Security error messages
  public static final String SECURITY_WARNING_TITLE = "Security Warning";
  public static final String SECURITY_WARNING_HEADER = "Security Warning";
  public static final String SECURITY_WARNING_CONTENT = 
      "The downloaded update file failed integrity verification. "
      + "This could indicate tampering or corruption. "
      + "Please try again later or download the update manually from the official website.";

  // Parse error messages
  public static final String PARSE_ERROR_TITLE = "Parse Error";
  public static final String PARSE_ERROR_HEADER = "Parse Error";
  public static final String PARSE_ERROR_CONTENT_PREFIX = 
      "Failed to parse the response from the update server. ";

  // Update error messages
  public static final String UPDATE_ERROR_TITLE = "Update Error";
  public static final String UPDATE_ERROR_HEADER = "Update Error";
  public static final String UPDATE_ERROR_CONTENT_PREFIX = 
      "An error occurred during the update process. ";

  // IO error messages
  public static final String IO_ERROR_TITLE = "I/O Error";
  public static final String IO_ERROR_HEADER = "I/O Error";
  public static final String IO_ERROR_NETWORK = 
      "Failed to connect to the server. Please check your internet connection and try again.";
  public static final String IO_ERROR_ACCESS_DENIED = 
      "Access denied. You don't have permission to access the requested file or directory.";
  public static final String IO_ERROR_FILE_NOT_FOUND = 
      "The requested file or directory does not exist.";
  public static final String IO_ERROR_GENERIC_PREFIX = "An I/O error occurred: ";

  // Application error messages
  public static final String APPLICATION_ERROR_HEADER = "Application Error";
  public static final String APPLICATION_ERROR_NULL_POINTER = 
      "The application encountered a null reference. This is likely a bug in the application.";

  // Input error messages
  public static final String INVALID_INPUT_HEADER = "Invalid Input";
  public static final String INVALID_INPUT_PREFIX = "Invalid input provided: ";

  // State error messages
  public static final String INVALID_STATE_HEADER = "Invalid State";
  public static final String INVALID_STATE_PREFIX = "The application is in an invalid state: ";

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private ErrorConstants() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }
}

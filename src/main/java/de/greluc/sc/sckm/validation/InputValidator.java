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

package de.greluc.sc.sckm.validation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Utility class for validating user inputs in the application.
 *
 * <p>This class provides static methods to validate different types of user inputs such as file
 * paths, player handles, and numeric values. It helps ensure that user inputs meet the required
 * format and constraints before they are processed by the application.
 *
 * @author SC Kill Monitor Team
 * @version 1.6.0
 * @since 1.6.0
 */
public class InputValidator {

  // Regular expression for validating Star Citizen player handles
  private static final Pattern HANDLE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{2,16}$");

  // Minimum and maximum values for scan interval
  private static final int MIN_SCAN_INTERVAL = 1;
  private static final int MAX_SCAN_INTERVAL = 3600; // 1 hour in seconds

  /**
   * Validates a file path to ensure it is a valid path format and the file exists.
   *
   * @param path the file path to validate
   * @return ValidationResult containing the validation status and an error message if invalid
   */
  public static ValidationResult validateFilePath(String path) {
    if (path == null || path.trim().isEmpty()) {
      return ValidationResult.invalid("Path cannot be empty");
    }

    try {
      Path filePath = Paths.get(path);
      File file = filePath.toFile();

      if (!file.exists()) {
        return ValidationResult.invalid("File does not exist: " + path);
      }

      if (!file.isFile()) {
        return ValidationResult.invalid("Path is not a file: " + path);
      }

      if (!Files.isReadable(filePath)) {
        return ValidationResult.invalid("File is not readable: " + path);
      }

      return ValidationResult.valid();
    } catch (InvalidPathException e) {
      return ValidationResult.invalid("Invalid path format: " + e.getMessage());
    }
  }

  /**
   * Validates a directory path to ensure it is a valid path format and the directory exists.
   *
   * @param path the directory path to validate
   * @return ValidationResult containing the validation status and an error message if invalid
   */
  public static ValidationResult validateDirectoryPath(String path) {
    if (path == null || path.trim().isEmpty()) {
      return ValidationResult.invalid("Path cannot be empty");
    }

    try {
      Path dirPath = Paths.get(path);
      File dir = dirPath.toFile();

      if (!dir.exists()) {
        return ValidationResult.invalid("Directory does not exist: " + path);
      }

      if (!dir.isDirectory()) {
        return ValidationResult.invalid("Path is not a directory: " + path);
      }

      if (!Files.isReadable(dirPath)) {
        return ValidationResult.invalid("Directory is not readable: " + path);
      }

      return ValidationResult.valid();
    } catch (InvalidPathException e) {
      return ValidationResult.invalid("Invalid path format: " + e.getMessage());
    }
  }

  /**
   * Validates a path string to ensure it has a valid format, regardless of whether the file or
   * directory exists.
   *
   * @param path the path string to validate
   * @return ValidationResult containing the validation status and an error message if invalid
   */
  public static ValidationResult validatePathFormat(String path) {
    if (path == null || path.trim().isEmpty()) {
      return ValidationResult.valid(); // Empty paths are allowed in some contexts
    }

    try {
      Paths.get(path);
      return ValidationResult.valid();
    } catch (InvalidPathException e) {
      return ValidationResult.invalid("Invalid path format: " + e.getMessage());
    }
  }

  /**
   * Validates a Star Citizen player handle to ensure it meets the required format.
   *
   * @param handle the player handle to validate
   * @return ValidationResult containing the validation status and an error message if invalid
   */
  public static ValidationResult validateHandle(String handle) {
    if (handle == null || handle.trim().isEmpty()) {
      return ValidationResult.invalid("Handle cannot be empty");
    }

    String trimmedHandle = handle.trim();
    if (!HANDLE_PATTERN.matcher(trimmedHandle).matches()) {
      return ValidationResult.invalid(
          "Handle must be 3-16 characters and contain only letters, numbers, underscores, and hyphens");
    }

    return ValidationResult.valid();
  }

  /**
   * Validates a scan interval value to ensure it is a valid integer within the acceptable range.
   *
   * @param intervalStr the interval string to validate
   * @return ValidationResult containing the validation status and an error message if invalid
   */
  public static ValidationResult validateScanInterval(String intervalStr) {
    if (intervalStr == null || intervalStr.trim().isEmpty()) {
      return ValidationResult.invalid("Interval cannot be empty");
    }

    try {
      int interval = Integer.parseInt(intervalStr.trim());

      if (interval < MIN_SCAN_INTERVAL) {
        return ValidationResult.invalid("Interval must be at least " + MIN_SCAN_INTERVAL + " second(s)");
      }

      if (interval > MAX_SCAN_INTERVAL) {
        return ValidationResult.invalid("Interval cannot exceed " + MAX_SCAN_INTERVAL + " seconds");
      }

      return ValidationResult.valid();
    } catch (NumberFormatException e) {
      return ValidationResult.invalid("Interval must be a valid number");
    }
  }
}

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
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for sanitizing file paths to prevent path traversal attacks.
 * 
 * <p>This class provides methods to sanitize file paths by normalizing them and ensuring they
 * don't contain path traversal sequences like "../" that could be used to access files outside
 * the intended directory.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class PathSanitizer {

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private PathSanitizer() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Sanitizes a file path to prevent path traversal attacks.
   *
   * <p>This method normalizes the path and ensures it doesn't contain path traversal sequences
   * that could be used to access files outside the intended directory.
   *
   * @param path The file path to sanitize. Must not be null.
   * @return The sanitized file path as a string.
   */
  public static @NotNull String sanitizePath(@NotNull String path) {
    if (path.isBlank()) {
      log.warn("Attempted to sanitize a blank path");
      return "";
    }

    try {
      // Convert to Path object for normalization
      Path normalizedPath = Paths.get(path).normalize();
      
      // Convert back to string with proper file separators
      return normalizedPath.toString();
    } catch (Exception e) {
      log.error("Error sanitizing path: {}", path, e);
      return "";
    }
  }

  /**
   * Sanitizes a file path and ensures it's within a base directory.
   *
   * <p>This method normalizes the path, ensures it doesn't contain path traversal sequences,
   * and verifies that the resulting path is within the specified base directory.
   *
   * @param basePath The base directory path that the file should be within. Must not be null.
   * @param filePath The file path to sanitize. Must not be null.
   * @return The sanitized file path as a string, or an empty string if the path would escape the base directory.
   */
  public static @NotNull String sanitizePathWithinBase(@NotNull String basePath, @NotNull String filePath) {
    if (basePath.isBlank() || filePath.isBlank()) {
      log.warn("Attempted to sanitize with blank base path or file path");
      return "";
    }

    try {
      Path baseNormalized = Paths.get(basePath).normalize().toAbsolutePath();
      Path fileNormalized = Paths.get(filePath).normalize().toAbsolutePath();
      
      // Check if the file path is within the base directory
      if (fileNormalized.startsWith(baseNormalized)) {
        return fileNormalized.toString();
      } else {
        log.warn("Attempted path traversal detected: {} is not within {}", filePath, basePath);
        return "";
      }
    } catch (Exception e) {
      log.error("Error sanitizing path within base: {} - {}", basePath, filePath, e);
      return "";
    }
  }

  /**
   * Creates a safe file path by combining a base directory with a filename.
   *
   * <p>This method ensures that the resulting path is within the base directory
   * and doesn't contain path traversal sequences.
   *
   * @param baseDir The base directory path. Must not be null.
   * @param fileName The filename to append to the base directory. Must not be null.
   * @return The combined file path as a string.
   */
  public static @NotNull String createSafeFilePath(@NotNull String baseDir, @NotNull String fileName) {
    if (baseDir.isBlank()) {
      log.warn("Attempted to create a safe file path with a blank base directory");
      return "";
    }
    
    if (fileName.isBlank()) {
      log.warn("Attempted to create a safe file path with a blank filename");
      return baseDir;
    }

    try {
      // Sanitize the base directory
      String sanitizedBaseDir = sanitizePath(baseDir);
      
      // Remove any directory traversal characters from the filename
      String sanitizedFileName = new File(fileName).getName();
      
      // Combine the paths
      Path combinedPath = Paths.get(sanitizedBaseDir, sanitizedFileName);
      
      return combinedPath.toString();
    } catch (Exception e) {
      log.error("Error creating safe file path: {} - {}", baseDir, fileName, e);
      return "";
    }
  }
}
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
import de.greluc.sc.sckm.exceptions.IntegrityException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for file integrity operations.
 * 
 * <p>This class provides methods for computing and verifying file checksums
 * to ensure file integrity. It uses SHA-256 as the default checksum algorithm.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class FileIntegrityUtils {

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private FileIntegrityUtils() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Computes the SHA-256 checksum of a file.
   *
   * @param filePath The path to the file
   * @return The SHA-256 checksum as a hexadecimal string
   * @throws IOException If an I/O error occurs
   * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available
   */
  public static @NotNull String computeChecksum(@NotNull Path filePath) 
      throws IOException, NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance(Constants.CHECKSUM_ALGORITHM);
    try (InputStream is = Files.newInputStream(filePath);
         DigestInputStream dis = new DigestInputStream(is, digest)) {
      byte[] buffer = new byte[8192];
      while (dis.read(buffer) != -1) {
        // Read the entire file
      }
    }

    byte[] checksumBytes = digest.digest();
    StringBuilder result = new StringBuilder();
    for (byte b : checksumBytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  /**
   * Verifies the integrity of a file by comparing its checksum with the expected checksum.
   *
   * @param filePath The path to the file
   * @param expectedChecksum The expected checksum
   * @throws IntegrityException If the checksums don't match or if there's an error computing the checksum
   */
  public static void verifyFileIntegrity(@NotNull Path filePath, @NotNull String expectedChecksum) 
      throws IntegrityException {
    try {
      String actualChecksum = computeChecksum(filePath);
      boolean isValid = actualChecksum.equalsIgnoreCase(expectedChecksum);
      if (!isValid) {
        log.error("Checksum verification failed. Expected: {}, Actual: {}", 
            expectedChecksum, actualChecksum);
        throw new IntegrityException(expectedChecksum, actualChecksum);
      } else {
        log.info("Checksum verification successful");
      }
    } catch (IOException e) {
      log.error("Failed to verify file integrity due to I/O error", e);
      throw new IntegrityException("Failed to verify file integrity due to I/O error", e);
    } catch (NoSuchAlgorithmException e) {
      log.error("Failed to verify file integrity: checksum algorithm not available", e);
      throw new IntegrityException("Failed to verify file integrity: checksum algorithm not available", e);
    }
  }

  /**
   * Deletes a file if it exists and logs the result.
   *
   * @param filePath The path to the file to delete
   * @return true if the file was deleted or didn't exist, false if deletion failed
   */
  public static boolean deleteFileIfExists(@NotNull Path filePath) {
    try {
      if (Files.exists(filePath)) {
        Files.delete(filePath);
        log.debug("Deleted file: {}", filePath);
        return true;
      }
      return true; // File didn't exist, so consider it a success
    } catch (IOException e) {
      log.warn("Failed to delete file: {}", filePath, e);
      return false;
    }
  }
}
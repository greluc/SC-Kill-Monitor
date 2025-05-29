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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 * Test class for the PathSanitizer utility.
 *
 * <p>This class contains tests to verify that the PathSanitizer correctly sanitizes file paths
 * and prevents path traversal attacks.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
class PathSanitizerTest {

  /**
   * Tests that the sanitizePath method correctly normalizes paths.
   */
  @Test
  void sanitizePath_normalizesPath() {
    // Arrange
    String path = "C:\\Users\\user\\..\\user\\Documents";
    
    // Act
    String sanitizedPath = PathSanitizer.sanitizePath(path);
    
    // Assert
    assertEquals("C:\\Users\\user\\Documents", sanitizedPath);
  }

  /**
   * Tests that the sanitizePath method handles blank paths correctly.
   */
  @Test
  void sanitizePath_handlesBlankPath() {
    // Arrange
    String path = "";
    
    // Act
    String sanitizedPath = PathSanitizer.sanitizePath(path);
    
    // Assert
    assertEquals("", sanitizedPath);
  }

  /**
   * Tests that the sanitizePath method handles paths with traversal attempts correctly.
   */
  @Test
  void sanitizePath_handlesPathTraversalAttempt() {
    // Arrange
    String path = "C:\\Program Files\\..\\..\\Windows\\System32\\config";
    
    // Act
    String sanitizedPath = PathSanitizer.sanitizePath(path);
    
    // Assert
    assertEquals("C:\\Windows\\System32\\config", sanitizedPath);
  }

  /**
   * Tests that the sanitizePathWithinBase method correctly validates paths within a base directory.
   */
  @Test
  void sanitizePathWithinBase_acceptsValidPath() {
    // Arrange
    String basePath = "C:\\Program Files\\MyApp";
    String filePath = "C:\\Program Files\\MyApp\\data\\file.txt";
    
    // Act
    String sanitizedPath = PathSanitizer.sanitizePathWithinBase(basePath, filePath);
    
    // Assert
    assertFalse(sanitizedPath.isEmpty());
    assertTrue(sanitizedPath.startsWith(Paths.get(basePath).normalize().toAbsolutePath().toString()));
  }

  /**
   * Tests that the sanitizePathWithinBase method rejects paths outside the base directory.
   */
  @Test
  void sanitizePathWithinBase_rejectsPathOutsideBase() {
    // Arrange
    String basePath = "C:\\Program Files\\MyApp";
    String filePath = "C:\\Program Files\\MyApp\\..\\..\\Windows\\System32\\config";
    
    // Act
    String sanitizedPath = PathSanitizer.sanitizePathWithinBase(basePath, filePath);
    
    // Assert
    assertEquals("", sanitizedPath);
  }

  /**
   * Tests that the createSafeFilePath method correctly combines a base directory with a filename.
   */
  @Test
  void createSafeFilePath_combinesPathsCorrectly() {
    // Arrange
    String baseDir = "C:\\Program Files\\MyApp";
    String fileName = "data.txt";
    
    // Act
    String safePath = PathSanitizer.createSafeFilePath(baseDir, fileName);
    
    // Assert
    assertEquals(Paths.get(baseDir, fileName).toString(), safePath);
  }

  /**
   * Tests that the createSafeFilePath method handles filenames with path traversal attempts correctly.
   */
  @Test
  void createSafeFilePath_handlesFileNameWithTraversalAttempt() {
    // Arrange
    String baseDir = "C:\\Program Files\\MyApp";
    String fileName = "..\\..\\Windows\\System32\\config\\file.txt";
    
    // Act
    String safePath = PathSanitizer.createSafeFilePath(baseDir, fileName);
    
    // Assert
    assertEquals(Paths.get(baseDir, "file.txt").toString(), safePath);
  }

  /**
   * Tests that the createSafeFilePath method handles blank base directory correctly.
   */
  @Test
  void createSafeFilePath_handlesBlankBaseDir() {
    // Arrange
    String baseDir = "";
    String fileName = "data.txt";
    
    // Act
    String safePath = PathSanitizer.createSafeFilePath(baseDir, fileName);
    
    // Assert
    assertEquals("", safePath);
  }

  /**
   * Tests that the createSafeFilePath method handles blank filename correctly.
   */
  @Test
  void createSafeFilePath_handlesBlankFileName() {
    // Arrange
    String baseDir = "C:\\Program Files\\MyApp";
    String fileName = "";
    
    // Act
    String safePath = PathSanitizer.createSafeFilePath(baseDir, fileName);
    
    // Assert
    assertEquals(baseDir, safePath);
  }
}
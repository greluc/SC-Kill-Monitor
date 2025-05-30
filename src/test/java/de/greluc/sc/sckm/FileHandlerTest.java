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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;

import de.greluc.sc.sckm.data.KillEvent;
import de.greluc.sc.sckm.settings.SettingsData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.UUID;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

/**
 * Test class for the FileHandler utility.
 * 
 * <p>This class verifies that the FileHandler correctly handles file operations,
 * particularly writing KillEvent objects to files.
 *
 * @author SC Kill Monitor Team
 * @version 1.7.0
 * @since 1.7.0
 */
class FileHandlerTest {

  @TempDir
  Path tempDir;

  private KillEvent testKillEvent;

  @BeforeEach
  void setUp() {
    // Create a test KillEvent
    testKillEvent = new KillEvent(
        UUID.randomUUID(),
        ZonedDateTime.now(),
        "TestPlayer",
        "EnemyPlayer",
        "Laser Cannon",
        "Ship",
        "Energy",
        "PU"
    );

    // Set up a test path for kill event files
    SettingsData.setPathKillEvent(tempDir.toString());
  }

  /**
   * Tests that writeKillEventToFile returns false when the kill event path is blank.
   */
  @Test
  void writeKillEventToFile_returnsFalse_whenPathIsBlank() {
    // Arrange
    SettingsData.setPathKillEvent("");

    // Mock Platform.runLater to avoid JavaFX toolkit initialization issues
    try (MockedStatic<Platform> platformMock = mockStatic(Platform.class);
         MockedStatic<AlertHandler> alertHandlerMock = mockStatic(AlertHandler.class)) {

      platformMock.when(() -> Platform.runLater(any(Runnable.class)))
          .thenAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
          });

      alertHandlerMock.when(() -> AlertHandler.showAlert(any(Alert.AlertType.class), anyString(), anyString(), anyBoolean()))
          .thenAnswer(invocation -> null);

      // Act
      boolean result = FileHandler.writeKillEventToFile(testKillEvent, "test");

      // Assert
      assertFalse(result, "Should return false when path is blank");
    }
  }

  /**
   * Tests that writeKillEventToFile successfully writes a kill event to a file.
   */
  @Test
  void writeKillEventToFile_writesKillEventToFile_successfully() throws IOException {
    // Arrange
    String fileSuffix = "test";
    String expectedFileName = String.format("kill-events_%s.log", fileSuffix);
    File expectedFile = tempDir.resolve(expectedFileName).toFile();

    // Mock Platform.runLater to avoid JavaFX toolkit initialization issues
    try (MockedStatic<Platform> platformMock = mockStatic(Platform.class)) {

      platformMock.when(() -> Platform.runLater(any(Runnable.class)))
          .thenAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
          });

      // Act
      boolean result = FileHandler.writeKillEventToFile(testKillEvent, fileSuffix);

      // Assert
      assertTrue(result, "Should return true when file is written successfully");
      assertTrue(expectedFile.exists(), "File should exist");
      assertTrue(expectedFile.length() > 0, "File should not be empty");

      // Verify file content contains expected data
      String fileContent = Files.readString(expectedFile.toPath());
      assertTrue(fileContent.contains(testKillEvent.killedPlayer()), "File should contain killed player name");
      assertTrue(fileContent.contains(testKillEvent.killingPlayer()), "File should contain killing player name");
      assertTrue(fileContent.contains(testKillEvent.weapon()), "File should contain weapon name");
    }
  }

  /**
   * Tests that writeKillEventToFile appends to an existing file.
   */
  @Test
  void writeKillEventToFile_appendsToExistingFile() throws IOException {
    // Arrange
    String fileSuffix = "test";
    String expectedFileName = String.format("kill-events_%s.log", fileSuffix);
    File expectedFile = tempDir.resolve(expectedFileName).toFile();

    // Create a second kill event with different data
    KillEvent secondKillEvent = new KillEvent(
        UUID.randomUUID(),
        ZonedDateTime.now().plusHours(1),
        "OtherPlayer",
        "TestPlayer",
        "Railgun",
        "FPS",
        "Physical",
        "Arena Commander"
    );

    // Mock Platform.runLater to avoid JavaFX toolkit initialization issues
    try (MockedStatic<Platform> platformMock = mockStatic(Platform.class)) {

      platformMock.when(() -> Platform.runLater(any(Runnable.class)))
          .thenAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
          });

      // Act - Write first kill event
      boolean result1 = FileHandler.writeKillEventToFile(testKillEvent, fileSuffix);

      // Get file size after first write
      long fileSizeAfterFirstWrite = expectedFile.length();

      // Act - Write second kill event
      boolean result2 = FileHandler.writeKillEventToFile(secondKillEvent, fileSuffix);

      // Assert
      assertTrue(result1, "First write should be successful");
      assertTrue(result2, "Second write should be successful");
      assertTrue(expectedFile.exists(), "File should exist");
      assertTrue(expectedFile.length() > fileSizeAfterFirstWrite, "File size should increase after second write");

      // Verify file content contains data from both kill events
      String fileContent = Files.readString(expectedFile.toPath());
      assertTrue(fileContent.contains(testKillEvent.killedPlayer()), "File should contain first killed player name");
      assertTrue(fileContent.contains(secondKillEvent.killedPlayer()), "File should contain second killed player name");
      assertTrue(fileContent.contains(","), "File should contain comma separator between JSON objects");
    }
  }
}

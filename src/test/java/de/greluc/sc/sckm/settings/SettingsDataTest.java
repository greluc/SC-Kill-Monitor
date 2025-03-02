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

package de.greluc.sc.sckm.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

public class SettingsDataTest {

  @Test
  public void testSetPathLive_UpdatesPathSuccessfully() {
    // Arrange: Define a new path for testing
    String newPath = "D:\\Games\\StarCitizen\\LIVE\\game.log";

    // Act: Update the pathLive using the setter
    SettingsData.setPathLive(newPath);

    // Assert: Verify the pathLive variable is updated
    assertEquals(newPath, SettingsData.getPathLive());
  }

  @Test
  public void testSetPathLive_NotifiesListeners() {
    // Arrange: Create a mock listener and add it to the SettingsData listeners list
    SettingsListener mockListener = mock(SettingsListener.class);
    SettingsData.addListener(mockListener);

    // Define a new path
    String newPath = "D:\\Games\\StarCitizen\\LIVE\\game.log";

    // Act: Update the pathLive using the setter
    SettingsData.setPathLive(newPath);

    // Assert: Verify that the listener's settingsChanged method was called
    verify(mockListener, times(1)).settingsChanged();

    // Cleanup: Remove the mock listener to prevent interference with other tests
    SettingsData.removeListener(mockListener);
  }

  @Test
  public void testSetPathPtu_UpdatesPathSuccessfully() {
    // Arrange: Define a new path for testing
    String newPath = "D:\\Games\\StarCitizen\\LIVE\\game.log";

    // Act: Update the pathLive using the setter
    SettingsData.setPathPtu(newPath);

    // Assert: Verify the pathLive variable is updated
    assertEquals(newPath, SettingsData.getPathPtu());
  }

  @Test
  public void testSetPathPtu_NotifiesListeners() {
    // Arrange: Create a mock listener and add it to the SettingsData listeners list
    SettingsListener mockListener = mock(SettingsListener.class);
    SettingsData.addListener(mockListener);

    // Define a new path
    String newPath = "D:\\Games\\StarCitizen\\LIVE\\game.log";

    // Act: Update the pathLive using the setter
    SettingsData.setPathPtu(newPath);

    // Assert: Verify that the listener's settingsChanged method was called
    verify(mockListener, times(1)).settingsChanged();

    // Cleanup: Remove the mock listener to prevent interference with other tests
    SettingsData.removeListener(mockListener);
  }
}

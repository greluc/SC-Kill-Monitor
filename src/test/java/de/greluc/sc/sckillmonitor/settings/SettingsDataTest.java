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
 * along with SC Kill Monitor. If not, see <http://www.gnu.org/licenses/>.                        *
 **************************************************************************************************/

package de.greluc.sc.sckillmonitor.settings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.0.0
 * @version 1.0.0
 */
class SettingsDataTest {

  @Test
  void testSetSelectedChannelUpdatesValue() {
    // Arrange
    String newChannel = "PTU";

    // Act
    SettingsData.setSelectedChannel(newChannel);

    // Assert
    assertEquals(newChannel, SettingsData.getSelectedChannel());
  }

  @Test
  void testSetSelectedChannelNotifiesListeners() {
    // Arrange
    String newChannel = "TECH-PREVIEW";
    SettingsListener mockListener = mock(SettingsListener.class);
    SettingsData.addListener(mockListener);

    // Act
    SettingsData.setSelectedChannel(newChannel);

    // Assert
    verify(mockListener, times(1)).settingsChanged();

    // Cleanup
    SettingsData.removeListener(mockListener);
  }

  @Test
  void testSetSelectedChannelDoesNotNotifyRemovedListener() {
    // Arrange
    String newChannel = "HOTFIX";
    SettingsListener mockListener = mock(SettingsListener.class);
    SettingsData.addListener(mockListener);
    SettingsData.removeListener(mockListener);

    // Act
    SettingsData.setSelectedChannel(newChannel);

    // Assert
    verify(mockListener, never()).settingsChanged();
  }
}
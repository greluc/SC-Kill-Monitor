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

import static de.greluc.sc.sckm.Constants.*;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import lombok.extern.log4j.Log4j2;

/**
 * Handles the persistence and retrieval of user-specific settings for the application. Provides
 * methods to save the current settings to a persistent storage and load them back when needed.
 *
 * <p>This class utilizes the {@link java.util.prefs.Preferences} API to save and retrieve
 * application settings. The preferences are associated with the current user and are stored in a
 * persistent storage backend provided by the JVM.
 *
 * <p>The settings managed by this class include various file paths, user handle, and scan interval.
 * These settings are initially retrieved from the {@link SettingsData} class and can also be
 * updated in {@link SettingsData} when loaded from persistent storage.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.4.0
 * @since 1.0.0
 */
@Log4j2
public class SettingsHandler {
  private final Preferences preferences = Preferences.userRoot().node("de").node("greluc").node("sc").node("sckillmonitor");

  /**
   * Saves the application settings to persistent storage.
   *
   * <p>This method writes various settings such as file paths, player handle, scan interval, and
   * boolean flags related to specific application modes into a preferences storage. Once all
   * settings are written, it attempts to flush the preferences, ensuring the changes are
   * persisted. If the flush operation fails due to a {@link BackingStoreException}, an error
   * is logged.
   *
   * <ul>
   *   <li>Settings include paths for different environments (Live, PTU, EPTU, Hotfix,
   *       Tech Preview, Custom).
   *   <li>Player handle.
   *   <li>Scan interval for specific application operations.
   *   <li>Display settings and operational modes flags (e.g., Show All, Killer Mode,
   *       Streamer Mode).
   * </ul>
   */
  public void saveSettings() {
    preferences.put(SETTINGS_PATH_LIVE, SettingsData.getPathLive());
    preferences.put(SETTINGS_PATH_PTU, SettingsData.getPathPtu());
    preferences.put(SETTINGS_PATH_EPTU, SettingsData.getPathEptu());
    preferences.put(SETTINGS_PATH_HOTFIX, SettingsData.getPathHotfix());
    preferences.put(SETTINGS_PATH_TECH_PREVIEW, SettingsData.getPathTechPreview());
    preferences.put(SETTINGS_PATH_CUSTOM, SettingsData.getPathCustom());
    preferences.put(SETTINGS_PLAYER_HANDLE, SettingsData.getHandle());
    preferences.putInt(SETTINGS_SCAN_INTERVAL_SECONDS, SettingsData.getInterval());
    preferences.putBoolean(SETTINGS_SHOW_ALL, SettingsData.isShowAllActive());
    preferences.putBoolean(SETTINGS_WRITE_TO_FILE, SettingsData.isWriteKillEventToFile());
    preferences.putBoolean(SETTINGS_KILLER_MODE_ACTIVE, SettingsData.isKillerModeActive());
    preferences.putBoolean(SETTINGS_STREAMER_MODE_ACTIVE, SettingsData.isStreamerModeActive());
    try {
      preferences.flush();
    } catch (BackingStoreException exception) {
      log.error("Couldn't persist the preferences to the persistent store!", exception);
    }
  }

  /**
   * Loads the application settings from persistent storage into the {@link SettingsData} object.
   *
   * <p>This method attempts to synchronize the preferences store, retrieves the settings,
   * and applies default values if no stored value is found. These settings include paths
   * for various configurations (e.g., LIVE, PTU, EPTU, hotfix, tech preview, or custom paths),
   * user preferences such as player handle, scan interval, and feature toggles (e.g.,
   * show all active, killer mode, streamer mode, etc.).
   *
   * <p>If the preferences cannot be synchronized due to a {@link BackingStoreException},
   * an error is logged, and default values are used for the settings.
   *
   * <p>This method interacts with the underlying {@code preferences} storage mechanism
   * and uses predefined keys to fetch the corresponding values. Any missing keys default
   * to the specified fallback values.
   */
  public void loadSettings() {
    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      log.error("Couldn't load the preferences from the persistent store! Using defaults.", e);
    }
    SettingsData.setPathLive(
        preferences.get(
            SETTINGS_PATH_LIVE,
            "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\LIVE\\game.log"));
    SettingsData.setPathPtu(
        preferences.get(
            SETTINGS_PATH_PTU,
            "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\EPTU\\game.log"));
    SettingsData.setPathEptu(
        preferences.get(
            SETTINGS_PATH_EPTU,
            "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\EPTU\\game.log"));
    SettingsData.setPathHotfix(
        preferences.get(
            SETTINGS_PATH_HOTFIX,
            "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\HOTFIX\\game.log"));
    SettingsData.setPathTechPreview(
        preferences.get(
            SETTINGS_PATH_TECH_PREVIEW,
            "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\TECH-PREVIEW\\game.log"));
    SettingsData.setPathCustom(preferences.get(SETTINGS_PATH_CUSTOM, ""));
    SettingsData.setHandle(preferences.get(SETTINGS_PLAYER_HANDLE, ""));
    SettingsData.setInterval(
        Integer.parseInt(preferences.get(SETTINGS_SCAN_INTERVAL_SECONDS, "60")));
    SettingsData.setShowAllActive(preferences.getBoolean(SETTINGS_SHOW_ALL, false));
    SettingsData.setWriteKillEventToFile(preferences.getBoolean(SETTINGS_WRITE_TO_FILE, false));
    SettingsData.setKillerModeActive(preferences.getBoolean(SETTINGS_KILLER_MODE_ACTIVE, false));
    SettingsData.setStreamerModeActive(preferences.getBoolean(SETTINGS_STREAMER_MODE_ACTIVE, false));
  }
}

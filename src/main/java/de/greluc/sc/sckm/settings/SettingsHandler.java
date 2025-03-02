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
 * @version 1.3.0
 * @since 1.0.0
 */
@Log4j2
public class SettingsHandler {
  private final Preferences preferences = Preferences.userRoot().node("de").node("greluc").node("sc").node("sckillmonitor");

  /**
   * Persists user and application settings to a persistent storage.
   *
   * <p>This method saves various settings parameters, such as file paths, user handle, interval,
   * and boolean flags, into a preference storage. The settings are fetched from the
   * {@link SettingsData} class and written to the persistent store using the provided preferences
   * instance.
   *
   * <p>After all settings have been written, the method attempts to flush the changes to ensure
   * that they are immediately committed to the persistent storage. If the flush operation fails
   * due to a {@link java.util.prefs.BackingStoreException}, an error message is logged.
   *
   * <p>The parameters saved to the storage include:
   * <ul>
   *   <li>Paths for live, PTU (Public Test Universe), EPTU (Experimental Public Test Universe),
   *       hotfix, tech preview, and custom environments.
   *   <li>User handle.
   *   <li>Scan interval in seconds.
   *   <li>Show all flag.
   *   <li>Write kill-event-to-file flag.
   *   <li>Killer-mode-active flag.
   * </ul>
   *
   * <p>Logging is used to capture any exceptions encountered during the flush operation, ensuring
   * that failures are handled and can be reviewed for troubleshooting.
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
    try {
      preferences.flush();
    } catch (BackingStoreException exception) {
      log.error("Couldn't persist the preferences to the persistent store!", exception);
    }
  }

  /**
   * Loads application settings from persistent storage and updates the corresponding
   * values in the {@code SettingsData} class. If the preferences cannot be retrieved,
   * default values are applied.
   *
   * <p>Attempts to sync the preferences from the backing store, and logs an error if
   * the sync fails. Each setting is either fetched from the user preferences or set to
   * a default value if unavailable.
   *
   * <ul>
   *   <li>{@code SETTINGS_PATH_LIVE}: Path to the LIVE game log.
   *   <li>{@code SETTINGS_PATH_PTU}: Path to the PTU game log.
   *   <li>{@code SETTINGS_PATH_EPTU}: Path to the EPTU game log.
   *   <li>{@code SETTINGS_PATH_HOTFIX}: Path to the HOTFIX game log.
   *   <li>{@code SETTINGS_PATH_TECH_PREVIEW}: Path to the TECH-PREVIEW game log.
   *   <li>{@code SETTINGS_PATH_CUSTOM}: Custom log file path.
   *   <li>{@code SETTINGS_PLAYER_HANDLE}: Player's handle or identifier.
   *   <li>{@code SETTINGS_SCAN_INTERVAL_SECONDS}: Interval in seconds for scanning.
   *   <li>{@code SETTINGS_SHOW_ALL}: Boolean flag for showing all events.
   *   <li>{@code SETTINGS_WRITE_KILLEVENT_TO_FILE}: Boolean flag for writing kill events to a file.
   *   <li>{@code SETTINGS_KILLER_MODE_ACTIVE}: Boolean flag for activating the killer mode.
   * </ul>
   *
   * <p>Default paths are system-specific, referencing directories in the "C:\Program Files\Roberts Space Industries\StarCitizen" folder.
   *
   * <p>If the {@code BackingStoreException} occurs during the sync operation, it logs an error message and uses the default values for all preferences.
   * Settings related to paths, handles, intervals, and other options are updated accordingly in {@code SettingsData}.
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
  }
}

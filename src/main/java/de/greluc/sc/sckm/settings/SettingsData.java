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

import de.greluc.sc.sckm.Constants;
import de.greluc.sc.sckm.data.ChannelType;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * The SettingsData class serves as a centralized storage for application settings. It maintains
 * various configuration properties such as file paths, user handle, scanning interval, and the
 * selected channel. These settings are static and accessible globally within the application.
 *
 * <p>This class also provides functionality to manage listeners that are notified whenever a change
 * occurs to any of the settings. Changes to settings trigger the registered listeners by invoking
 * the {@code settingsChanged} method on each of them.
 *
 * <p>Key Responsibilities:
 *
 * <ul>
 *   <li>Store static application configurations like file paths, user handle, and intervals.
 *   <li>Manage and notify listeners of configuration changes.
 *   <li>Ensure thread-safe update and retrieval of settings properties using synchronized
 *       notification to listeners.
 * </ul>
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.5.0
 * @since 1.0.0
 */
public class SettingsData {
  private static final List<SettingsListener> listeners = new ArrayList<>();

  @Getter
  private static String pathLive =
      "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\LIVE\\game.log";

  @Getter
  private static String pathPtu =
      "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\PTU\\game.log";

  @Getter
  private static String pathEptu =
      "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\EPTU\\game.log";

  @Getter
  private static String pathHotfix =
      "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\HOTFIX\\game.log";

  @Getter
  private static String pathTechPreview =
      "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\TECH-PREVIEW\\game.log";

  @Getter private static String pathCustom = "";
  @Getter private static String handle = "";
  @Getter private static int interval = 60;
  @Getter private static ChannelType selectedChannel = ChannelType.LIVE;
  @Getter private static boolean isShowAllActive = false;
  @Getter private static boolean isWriteKillEventToFile = false;
  @Getter private static boolean isKillerModeActive = false;
  @Getter private static boolean isStreamerModeActive = false;

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private SettingsData() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Sets the path for the Live environment.
   *
   * @param pathLive The new path for the Live environment. Must not be null.
   */
  public static void setPathLive(@NotNull String pathLive) {
    SettingsData.pathLive = pathLive;
  }

  /**
   * Sets the path for the PTU (Public Test Universe) environment.
   *
   * @param pathPtu The new path for the PTU environment. Must not be null.
   */
  public static void setPathPtu(@NotNull String pathPtu) {
    SettingsData.pathPtu = pathPtu;
  }

  /**
   * Sets the path for the EPTU (Experimental Public Test Universe) environment.
   *
   * @param pathEptu The new path for the EPTU environment. Must not be null.
   */
  public static void setPathEptu(@NotNull String pathEptu) {
    SettingsData.pathEptu = pathEptu;
  }

  /**
   * Sets the path for the Hotfix environment.
   *
   * @param pathHotfix The new path for the Hotfix environment. Must not be null.
   */
  public static void setPathHotfix(@NotNull String pathHotfix) {
    SettingsData.pathHotfix = pathHotfix;
  }

  /**
   * Sets the path for the Tech Preview environment.
   *
   * @param pathTechPreview The new path for the Tech Preview environment. Must not be null.
   */
  public static void setPathTechPreview(@NotNull String pathTechPreview) {
    SettingsData.pathTechPreview = pathTechPreview;
  }

  /**
   * Sets the custom path configuration for the application.
   *
   * @param pathCustom The new custom path to be set. Must not be null.
   */
  public static void setPathCustom(@NotNull String pathCustom) {
    SettingsData.pathCustom = pathCustom;
  }

  /**
   * Sets the handle in the {@code SettingsData} class. This value is used to uniquely identify
   * or represent a specific aspect of the settings configuration.
   *
   * @param handle The new handle value to set. Must not be {@code null}.
   */
  public static void setHandle(@NotNull String handle) {
    SettingsData.handle = handle;
  }

  /**
   * Sets the interval value for the {@code SettingsData} configuration.
   *
   * @param interval The new interval value to set. Must be a non-negative integer.
   */
  public static void setInterval(int interval) {
    SettingsData.interval = interval;
  }

  /**
   * Sets the selected channel for the application configuration.
   *
   * @param selectedChannel The channel to set as selected. Must not be {@code null}.
   */
  public static void setSelectedChannel(@NotNull ChannelType selectedChannel) {
    SettingsData.selectedChannel = selectedChannel;
  }

  /**
   * Sets the state of the "Show All Events" setting.
   *
   * @param isShowAll A boolean value indicating whether to enable or disable the "Show All Events" setting.
   *                  When {@code true}, all events will be displayed; when {@code false}, only a subset
   *                  of events may be shown depending on other configurations.
   */
  public static void setShowAllActive(boolean isShowAll) {
    SettingsData.isShowAllActive = isShowAll;
  }

  /**
   * Sets the state of whether kill events should be written to a file.
   *
   * @param isWriteKillEventToFile A boolean value indicating whether kill events should
   *                                be written to a file. When {@code true}, kill events
   *                                will be logged to a file; when {@code false}, they
   *                                will not be logged.
   */
  public static void setWriteKillEventToFile(boolean isWriteKillEventToFile) {
    SettingsData.isWriteKillEventToFile = isWriteKillEventToFile;
  }

  /**
   * Sets the state of the "Killer Mode" setting in the application configuration.
   *
   * @param isKillerModeActive A boolean value indicating whether "Killer Mode" should be
   *                           active or not. When {@code true}, "Killer Mode" is enabled;
   *                           when {@code false}, it is disabled.
   */
  public static void setKillerModeActive(boolean isKillerModeActive) {
    SettingsData.isKillerModeActive = isKillerModeActive;
  }

  /**
   * Sets the state of the "Streamer Mode" setting in the application configuration.
   *
   * @param isStreamerModeActive A boolean value indicating whether "Streamer Mode" should be
   *                             active or not. When {@code true}, "Streamer Mode" is enabled;
   *                             when {@code false}, it is disabled.
   */
  public static void setStreamerModeActive(boolean isStreamerModeActive) {
    SettingsData.isStreamerModeActive = isStreamerModeActive;
  }

  /**
   * Adds a new listener to the list of registered {@link SettingsListener} instances.
   *
   * @param listener The listener to be added. This listener will be notified whenever a relevant
   *     change to the settings occurs.
   */
  public static void addListener(@NotNull SettingsListener listener) {
    listeners.add(listener);
  }

  /**
   * Notifies all registered {@link SettingsListener} instances about changes in the settings.
   *
   * <p>This method iterates through the list of registered listeners and invokes their
   * {@link SettingsListener#settingsChanged()} method to inform them of a settings modification.
   *
   * <p>Listeners should implement the {@link SettingsListener} interface and be added to the
   * listener list using {@code addListener(SettingsListener listener)} prior to being notified
   * via this method.
   */
  public static void settingsChanged() {
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Removes a previously registered {@link SettingsListener} instance from the list of listeners.
   * The specified listener will no longer receive notifications about changes in settings.
   *
   * @param listener The {@link SettingsListener} to be removed. If the listener is not currently
   *     registered, no action will be taken.
   */
  public static void removeListener(@NotNull SettingsListener listener) {
    listeners.remove(listener);
  }
}

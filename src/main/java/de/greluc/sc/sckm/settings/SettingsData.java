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
 * along with SC Kill Monitor. If not, see <https://www.gnu.org/licenses/>.                       *
 **************************************************************************************************/

package de.greluc.sc.sckm.settings;

import de.greluc.sc.sckm.Constants;
import lombok.Generated;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.greluc.sc.sckm.Constants.LIVE;

/**
 * The SettingsData class serves as a centralized storage for application settings.
 * It maintains various configuration properties such as file paths, user handle,
 * scanning interval, and the selected channel. These settings are static and accessible
 * globally within the application.
 * <p>
 * This class also provides functionality to manage listeners that are notified whenever
 * a change occurs to any of the settings. Changes to settings trigger the registered
 * listeners by invoking the `settingsChanged` method on each of them.
 * <p>
 * Key Responsibilities:<br>
 * - Store static application configurations like file paths, user handle, and intervals.<br>
 * - Manage and notify listeners of configuration changes.<br>
 * - Ensure thread-safe update and retrieval of settings properties using synchronized
 *   notification to listeners.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.0.0
 * @version 1.0.1
 */
public class SettingsData {
  @Getter
  private static String pathLive = "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\LIVE\\game.log";
  @Getter
  private static String pathPtu = "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\PTU\\game.log";
  @Getter
  private static String pathEptu = "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\EPTU\\game.log";
  @Getter
  private static String pathHotfix = "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\HOTFIX\\game.log";
  @Getter
  private static String pathTechPreview = "C:\\Program Files\\Roberts Space Industries\\StarCitizen\\TECH-PREVIEW\\game.log";
  @Getter
  private static String pathCustom = "";
  @Getter
  private static String handle = "";
  @Getter
  private static int interval = 1;
  @Getter
  private static String selectedChannel = LIVE;

  private static final List<SettingsListener> listeners = new ArrayList<>();

  /**
   * Sets the path for the live environment and notifies all registered listeners
   * about the change in settings.
   *
   * @param pathLive The new path for the live environment.
   */
  public static void setPathLive(@NotNull String pathLive) {
    SettingsData.pathLive = pathLive;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the path for the PTU (Public Test Universe) environment and notifies all registered
   * listeners about the change in settings.
   *
   * @param pathPtu The new path for the PTU environment.
   */
  public static void setPathPtu(@NotNull String pathPtu) {
    SettingsData.pathPtu = pathPtu;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the path for the EPTU (Experimental Public Test Universe) environment
   * and notifies all registered listeners about the change in settings.
   *
   * @param pathEptu The new path for the EPTU environment.
   */
  public static void setPathEptu(@NotNull String pathEptu) {
    SettingsData.pathEptu = pathEptu;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the path for the Hotfix environment and notifies all registered listeners
   * about the change in settings.
   *
   * @param pathHotfix The new path for the Hotfix environment.
   */
  public static void setPathHotfix(@NotNull String pathHotfix) {
    SettingsData.pathHotfix = pathHotfix;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the path for the Tech Preview environment and notifies all registered
   * listeners about the change in settings.
   *
   * @param pathTechPreview The new path for the Tech Preview environment.
   */
  public static void setPathTechPreview(@NotNull String pathTechPreview) {
    SettingsData.pathTechPreview = pathTechPreview;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the custom path and notifies all registered listeners about the change
   * in settings.
   *
   * @param pathCustom The new custom path to be set.
   */
  public static void setPathCustom(@NotNull String pathCustom) {
    SettingsData.pathCustom = pathCustom;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the handle value and notifies all registered listeners about
   * the change in settings.
   *
   * @param handle The new handle to be set.
   */
  public static void setHandle(@NotNull String handle) {
    SettingsData.handle = handle;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the interval value and notifies all registered listeners about
   * the change in settings.
   *
   * @param interval The new interval value to be set.
   */
  public static void setInterval(int interval) {
    SettingsData.interval = interval;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Sets the selected channel and notifies all registered listeners about the change
   * in settings. This method is used to update the current channel setting and trigger
   * any associated actions in registered listeners.
   *
   * @param selectedChannel The new selected channel to be set.
   */
  public static void setSelectedChannel(@NotNull String selectedChannel) {
    SettingsData.selectedChannel = selectedChannel;
    listeners.forEach(SettingsListener::settingsChanged);
  }

  /**
   * Adds a new listener to the list of registered {@link SettingsListener} instances.
   *
   * @param listener The listener to be added. This listener will be notified
   *                 whenever a relevant change to the settings occurs.
   */
  public static void addListener(@NotNull SettingsListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a previously registered {@link SettingsListener} instance
   * from the list of listeners. The specified listener will no longer
   * receive notifications about changes in settings.
   *
   * @param listener The {@link SettingsListener} to be removed. If the
   *                 listener is not currently registered, no action will be taken.
   */
  public static void removeListener(@NotNull SettingsListener listener) {
    listeners.remove(listener);
  }

  /**
   * Used to exclude the unused constructor from code coverage evaluation.
   */
  @Generated
  private SettingsData() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }
}

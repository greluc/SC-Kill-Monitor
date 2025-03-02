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

/**
 * The SettingsListener interface defines a contract for classes that wish to be notified when
 * changes occur to settings in the application. Classes implementing this interface can register
 * themselves to listen for updates in the settings data.
 *
 * <p>Implementers must define the behavior for the {@code settingsChanged} method, which is invoked
 * whenever a relevant change to the settings occurs.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.0.0
 * @version 1.3.0
 */
public interface SettingsListener {
  /**
   * Notifies listeners that settings have been changed.
   *
   * <p>This method is called to inform all registered listeners that a modification has occurred in
   * the application settings. Implementers of the {@link SettingsListener} interface must define
   * the behavior upon receiving this notification.
   */
  void settingsChanged();
}

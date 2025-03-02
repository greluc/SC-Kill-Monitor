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

import lombok.Generated;

/**
 * This class represents a collection of constant values used throughout the application.
 *
 * <p>It includes constants for various environment types, application metadata, and utility
 * class-related string definitions. The class is designed to prevent instantiation as it serves
 * only as a holder for constant values.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.0.0
 * @version 1.3.0
 */
public class Constants {
  public static final String LIVE = "LIVE";
  public static final String PTU = "PTU";
  public static final String EPTU = "EPTU";
  public static final String HOTFIX = "HOTFIX";
  public static final String TECH_PREVIEW = "TECH-PREVIEW";
  public static final String CUSTOM = "Custom";

  public static final String SETTINGS_PATH_LIVE = "path_live";
  public static final String SETTINGS_PATH_PTU = "path_ptu";
  public static final String SETTINGS_PATH_EPTU = "path_eptu";
  public static final String SETTINGS_PATH_HOTFIX = "path_hotfix";
  public static final String SETTINGS_PATH_TECH_PREVIEW = "path_tech_preview";
  public static final String SETTINGS_PATH_CUSTOM = "path_custom";
  public static final String SETTINGS_PLAYER_HANDLE = "player_handle";
  public static final String SETTINGS_SCAN_INTERVAL_SECONDS = "interval_seconds";
  public static final String SETTINGS_SHOW_ALL = "show_all";
  public static final String SETTINGS_WRITE_TO_FILE = "write_to_file";
  public static final String SETTINGS_KILLER_MODE_ACTIVE = "killer_mode_active";

  public static final String APP_TITLE = "SC Kill Monitor";

  public static final String UTILITY_CLASS = "Utility class";

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private Constants() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }
}

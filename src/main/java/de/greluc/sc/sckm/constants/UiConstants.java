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

package de.greluc.sc.sckm.constants;

import de.greluc.sc.sckm.Constants;
import lombok.Generated;

/**
 * This class contains UI-related constants used throughout the application.
 *
 * <p>It includes constants for CSS styles, dimensions, and other UI-related values.
 * The class is designed to prevent instantiation as it serves only as a holder for constant values.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.6.0
 * @version 1.6.0
 */
public class UiConstants {
  // Window dimensions
  public static final int WINDOW_WIDTH = 700;
  public static final int WINDOW_HEIGHT = 500;

  // Text area dimensions
  public static final int TEXT_AREA_MIN_HEIGHT = 160;
  public static final int TEXT_AREA_MAX_HEIGHT = 160;

  // CSS styles
  public static final String FONT_FAMILY_SEGOE_UI = "-fx-font-family: \"Segoe UI\";";

  // Insets (margins)
  public static final int INSET_TOP = 5;
  public static final int INSET_RIGHT = 10;
  public static final int INSET_BOTTOM = 0;
  public static final int INSET_LEFT = 0;

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private UiConstants() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }
}

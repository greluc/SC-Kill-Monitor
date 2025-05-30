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
 * This class contains resource-related constants used throughout the application.
 *
 * <p>It includes constants for file paths, resource locations, and other resource-related values.
 * The class is designed to prevent instantiation as it serves only as a holder for constant values.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @since 1.6.0
 * @version 1.6.0
 */
public class ResourceConstants {
  // FXML paths
  public static final String FXML_MAIN_VIEW = "fxml/MainView.fxml";
  
  // Image paths
  public static final String LOGO_PATH = "logos/sckm.jpg";

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private ResourceConstants() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }
}
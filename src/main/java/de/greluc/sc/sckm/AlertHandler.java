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

import static de.greluc.sc.sckm.Constants.APP_TITLE;

import javafx.scene.control.Alert;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;

/**
 * Provides utility methods to display alerts within the application.
 *
 * <p>The AlertHandler class contains static methods to display different types of alerts using
 * JavaFX's Alert class. It supports alerts with configurable types, headers, and content, as well
 * as displaying general error alerts.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
public class AlertHandler {

  /** Shows a general error that doesn't specify a specific error in its message. */
  @Generated
  public static void showGeneralError() {
    showAlert(
        Alert.AlertType.ERROR, "ERROR", "An error occurred while performing the desired action.");
  }

  /**
   * Shows an alert. Uses the {@link Alert} class.
   *
   * @param alertType {@link Alert.AlertType} that should be used for the alert.
   * @param header String containing the short text with the main information.
   * @param content String containing the description of the alert.
   */
  @Generated
  public static void showAlert(
      @NotNull Alert.AlertType alertType, @NotNull String header, @NotNull String content) {
    var alert = new Alert(alertType);
    alert.titleProperty().set(APP_TITLE);
    alert.headerTextProperty().set(header);
    alert.contentTextProperty().set(content);
    alert.setResizable(true);
    alert.show();
  }
}

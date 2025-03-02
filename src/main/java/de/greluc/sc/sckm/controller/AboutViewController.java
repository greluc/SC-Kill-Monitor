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

package de.greluc.sc.sckm.controller;

import de.greluc.sc.sckm.ScKillMonitorApp;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

/**
 * Controller responsible for managing the About view of the SC Kill Monitor application.
 *
 * <p>The About view provides information about the application, including its purpose, licensing
 * details, and disclaimers. It also displays the application's logos and additional metadata.
 *
 * <p>This controller initializes the rear view elements by populating the text area with the
 * application's description and setting the application's logos into an image view.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.1
 */
public class AboutViewController {

  @FXML private TextArea textAreaAbout;
  @FXML private ImageView imageViewLogoSc;

  /**
   * Initializes the About view of the SC Kill Monitor application.
   *
   * <p>This method populates the text area with detailed information about the application,
   * including its purpose, licensing details, disclaimers, and links to relevant resources.
   * Additionally, it configures an image view to display the application's logos.
   *
   * <p>The displayed content includes:
   *
   * <ul>
   *   <li>A description of the application and its functionality.
   *   <li>Licensing under the GNU General Public License v3.0.
   *   <li>Links to the source code and license text.
   *   <li>A disclaimer regarding the appropriate use of the application.
   *   <li>Acknowledgment that the project is unofficial and not affiliated with the Cloud Imperium
   *       group of companies.
   * </ul>
   *
   * <p>The image view is set up with a specific height, with its aspect ratio preserved, to ensure
   * proper display of the application's logos.
   */
  @FXML
  protected void initialize() {
    textAreaAbout.setText(
        """
            SC Kill Monitor is an application to search the Star Citizen game.log file for the person who killed you.

            The source code is available on GitHub: https://github.com/greluc/SC-Kill-Monitor

            It is licensed under the GNU General Public License v3.0 (GPLv3). You can find the license text under https://github.com/greluc/SC-Kill-Monitor/blob/main/LICENSE.md

            Disclaimer: The SC Kill Monitor app may only be used to report potential griefings or stream snipings. Use of the identified player names for purposes that violate the Star Citizen Terms and Conditions is prohibited. The responsibility for compliant use lies with the user.

            SC Kill Monitor is an unofficial Star Citizen fan project, not affiliated with the Cloud Imperium group of companies. All content in this application not authored by its host or users are property of their respective owners. Star Citizen®, Roberts Space Industries® and Cloud Imperium® are registered trademarks of Cloud Imperium Rights LLC.


            SC Kill Monitor
            Copyright (C) 2025-2025 SC Kill Monitor Team

            SC Kill Monitor is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

            SC Kill Monitor is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

            You should have received a copy of the GNU General Public License along with SC Kill Monitor. If not, see <https://www.gnu.org/licenses/>.""");
    imageViewLogoSc.setImage(
        new javafx.scene.image.Image(
            Objects.requireNonNull(
                ScKillMonitorApp.class.getResourceAsStream("logos/MadeByTheCommunity_Black.png"))));
    imageViewLogoSc.setPreserveRatio(true);
    imageViewLogoSc.setFitHeight(100);
  }
}

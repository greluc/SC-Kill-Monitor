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
import de.greluc.sc.sckm.settings.SettingsHandler;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * MainViewController serves as the primary controller for the application's main view. It
 * initializes and manages interactions between the UI and internal application logic.
 *
 * <p>This controller is responsible for handling transitions between different views such as the
 * {@code StartView}, {@code ScanView}, and {@code SettingsView}. It also provides functionality for
 * basic application actions like closing the application or displaying the "About" dialog.
 *
 * <p>It utilizes JavaFX features such as FXML, scene management, and modal windows to provide a
 * graphical user interface. The controller also integrates with the {@link SettingsHandler} to
 * manage user preferences and settings.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
@Log4j2
public class MainViewController {
  private final SettingsHandler settingsHandler = new SettingsHandler();
  @FXML private GridPane basePane;
  private GridPane startPane;
  private GridPane scanPane;

  /**
   * Initializes the controller and loads the initial application state.
   *
   * <p>This method is responsible for loading and configuring the "StartView.fxml" user interface.
   * It invokes the settings handler to load application settings, initializes the main start pane,
   * retrieves the associated controller for the start view, and establishes its connection to the
   * main view controller. The loaded pane is added to the base UI layout.
   *
   * <p>In the event of a failure to load the FXML file, the method logs the error and terminates
   * the application to prevent further execution with an incomplete UI state.
   */
  @FXML
  protected void initialize() {
    settingsHandler.loadSettings();
    FXMLLoader fxmlLoader =
        new FXMLLoader(ScKillMonitorApp.class.getResource("fxml/StartView.fxml"));
    try {
      startPane = fxmlLoader.load();
    } catch (IOException ioException) {
      log.error("Could not load StartView.fxml", ioException);
      System.exit(-1);
    }
    StartViewController startViewController = fxmlLoader.getController();
    startViewController.setMainViewController(this);
    GridPane.setConstraints(startPane, 0, 1);
    basePane.getChildren().add(startPane);
  }

  /**
   * Handles the event triggered when the settings button is pressed in the user interface. This
   * method initializes and displays the settings window as a modal dialog.
   *
   * <p>The method loads the SettingsView.fxml file to create the settings dialog. It initializes
   * the controller for the settings view and sets required dependencies. The dialog is displayed as
   * a resizable and non-maximized modal window.
   *
   * <p>If the FXML file cannot be loaded, an error is logged.
   *
   * <p>Exceptions:
   *
   * <ul>
   *   <li>IOException: Throws and logs an error if there is an issue loading the FXML file.
   * </ul>
   *
   * <p>Dependencies:
   *
   * <ul>
   *   <li>FXMLLoader to load the FXML file.
   *   <li>Stage for creating the settings window.
   *   <li>SettingsViewController to handle settings logic.
   * </ul>
   */
  @FXML
  protected void onSettingsPressed() {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(ScKillMonitorApp.class.getResource("fxml/SettingsView.fxml"));
      Stage stage = new Stage();
      Scene scene = new Scene(fxmlLoader.load());
      SettingsViewController settingsViewController = fxmlLoader.getController();
      settingsViewController.setSettingsHandler(settingsHandler);
      stage.setScene(scene);
      stage.setMaximized(false);
      stage.setResizable(true);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage
          .getIcons()
          .add(
              new javafx.scene.image.Image(
                  String.valueOf(ScKillMonitorApp.class.getResource("logos/SC-Kill-Monitor.png"))));
      stage.show();
    } catch (IOException ioException) {
      log.error("Couldn't load SettingsView.fxml", ioException);
    }
  }

  /**
   * Handles the action for a close button press event. This method terminates the application by
   * invoking {@link System#exit(int)} with a status code of 0. It is annotated with {@code @FXML}
   * to indicate that it is tied to an associated UI component in an FXML layout.
   */
  @FXML
  protected void onClosePressed() {
    System.exit(0);
  }

  /**
   * Handles the event triggered when the "About" button is pressed in the application UI.
   *
   * <p>This method is responsible for loading the "AboutView.fxml" file, initializing and
   * displaying a new modal Stage containing the "About" view of the application. It sets up the
   * scene, ensures the window's resize behavior is enabled and its modality is configured as
   * application modal.
   *
   * <p>In the case of an IOException, logs an error message indicating the failure to load the FXML
   * file.
   */
  @FXML
  protected void onAboutPressed() {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(ScKillMonitorApp.class.getResource("fxml/AboutView.fxml"));
      Stage stage = new Stage();
      Scene scene = new Scene(fxmlLoader.load());
      stage.setScene(scene);
      stage.setMaximized(false);
      stage.setResizable(true);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage
          .getIcons()
          .add(
              new javafx.scene.image.Image(
                  String.valueOf(ScKillMonitorApp.class.getResource("logos/SC-Kill-Monitor.png"))));
      stage.show();
    } catch (IOException ioException) {
      log.error("Couldn't load AboutView.fxml", ioException);
    }
  }

  /**
   * Handles the action when the start button is pressed. This method transitions the view from the
   * start pane to the scan view pane.
   *
   * <p>The method removes the current startPane from the basePane, loads the ScanView.fxml file to
   * create the scanPane, and initializes the associated controller. If loading the fxml file fails,
   * an error is logged and the application terminates. Finally, the scanPane is positioned in the
   * basePane.
   *
   * <p>This method is designed to manage the transition to the scan view while ensuring proper
   * controller setup and error handling.
   */
  protected void onStartPressed() {
    basePane.getChildren().remove(startPane);
    FXMLLoader fxmlLoader =
        new FXMLLoader(ScKillMonitorApp.class.getResource("fxml/ScanView.fxml"));
    scanPane = null;
    try {
      scanPane = fxmlLoader.load();
    } catch (IOException ioException) {
      log.error("Could not load ScanView.fxml", ioException);
      System.exit(-1);
    }
    ScanViewController scanViewController = fxmlLoader.getController();
    scanViewController.setMainViewController(this);
    GridPane.setConstraints(scanPane, 0, 1);
    basePane.getChildren().add(scanPane);
  }

  /**
   * Handles the event when the stop button is pressed.
   *
   * <p>This method removes the current scan pane from the base pane and loads the StartView FXML
   * file, transitioning back to the start view. It also sets the main view controller for the newly
   * loaded start view controller. If the FXML file cannot be loaded, it logs the error and
   * terminates the application.
   */
  protected void onStopPressed() {
    basePane.getChildren().remove(scanPane);
    FXMLLoader fxmlLoader =
        new FXMLLoader(ScKillMonitorApp.class.getResource("fxml/StartView.fxml"));
    try {
      startPane = fxmlLoader.load();
    } catch (IOException ioException) {
      log.error("Could not load StartView.fxml", ioException);
      System.exit(-1);
    }
    StartViewController startViewController = fxmlLoader.getController();
    startViewController.setMainViewController(this);
    GridPane.setConstraints(startPane, 0, 1);
    basePane.getChildren().add(startPane);
  }
}

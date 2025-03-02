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

import de.greluc.sc.sckm.AlertHandler;
import de.greluc.sc.sckm.Constants;
import de.greluc.sc.sckm.data.ChannelType;
import de.greluc.sc.sckm.settings.SettingsData;
import de.greluc.sc.sckm.settings.SettingsHandler;
import de.greluc.sc.sckm.settings.SettingsListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * The StartViewController class manages the user interface interactions and related logic for the
 * starting view of the application. It handles user inputs, updates UI elements based on
 * application data, and communicates with other controllers.
 *
 * <p>Implements the SettingsListener interface to respond to changes in settings data.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
@Log4j2
public class StartViewController implements SettingsListener {
  @FXML private Label selectedPathValue;
  @FXML private TextField inputHandle;
  @FXML private TextField inputInterval;
  @FXML private ComboBox<ChannelType> channelSelection;
  private MainViewController mainViewController;

  /**
   * Initializes the controller and its associated UI components with predefined settings and data.
   *
   * <p>This method is executed automatically when the FXML file is loaded. It sets initial values
   * for input fields and dropdown menus, and binds the UI components to the application settings.
   *
   * <p>It configures:
   *
   * <ul>
   *   <li>The handle input field with the application's saved handle.
   *   <li>The interval input field using the saved interval value.
   *   <li>The channel selection dropdown with a list of predefined channels.
   * </ul>
   *
   * It also sets the selected path for the application and registers the current instance as a
   * listener to settings changes.
   */
  @FXML
  protected void initialize() {
    inputHandle.setText(SettingsData.getHandle());
    inputInterval.setText(String.valueOf(SettingsData.getInterval()));
    channelSelection.setItems(
        FXCollections.observableArrayList(
            ChannelType.LIVE,
            ChannelType.PTU,
            ChannelType.EPTU,
            ChannelType.HOTFIX,
            ChannelType.TECH_PREVIEW,
            ChannelType.CUSTOM));
    channelSelection.getSelectionModel().select(SettingsData.getSelectedChannel());
    channelSelection.setConverter(
        new StringConverter<>() {
          @Override
          @NotNull
          @Generated
          public String toString(ChannelType type) {
            if (type == null) {
              return "";
            }
            return switch (type) {
              case PTU -> Constants.PTU;
              case EPTU -> Constants.EPTU;
              case HOTFIX -> Constants.HOTFIX;
              case TECH_PREVIEW -> Constants.TECH_PREVIEW;
              case CUSTOM -> Constants.CUSTOM;
              default -> Constants.LIVE;
            };
          }

          @Override
          @Generated
          public ChannelType fromString(String string) {
            return null;
          }
        });

    setSelectedPath();
    SettingsData.addListener(this);
    log.info("Initialized StartViewController");
  }

  /**
   * Handles the event triggered when the "Start" button is clicked in the user interface.
   *
   * <p>This method performs input validation on three fields: handle, interval, and path. If any of
   * the fields are empty, an error alert is displayed and the method returns early. Additionally,
   * the method ensures the interval input is a valid integer, showing an error if the value is
   * invalid.
   *
   * <p>Upon successful validation:
   *
   * <ul>
   *   <li>The handle and interval values are stored using the {@code SettingsData}.
   *   <li>The main controller's start logic is triggered using {@code
   *       mainViewController.onStartPressed()}.
   * </ul>
   *
   * <p>Validation logic includes:
   *
   * <ul>
   *   <li>Checking if the handle input field is empty and logging a warning if so.
   *   <li>Checking if the interval input field is empty and logging a warning if so.
   *   <li>Checking if the selected path field is empty and logging a warning if so.
   *   <li>Parsing the interval input to an integer, handling potential {@code
   *       NumberFormatException} to ensure a valid integer is provided.
   * </ul>
   *
   * <p>Alerts are displayed to the user via {@code AlertHandler} with specific messages indicating
   * the cause of the issue.
   */
  @FXML
  protected void onStartButtonClicked() {
    if (inputHandle.getText().isEmpty()) {
      log.warn("Handle is empty");
      AlertHandler.showAlert(Alert.AlertType.ERROR, "Handle is empty", "Please enter a handle");
      return;
    }
    if (inputInterval.getText().isEmpty()) {
      log.warn("Interval is empty");
      AlertHandler.showAlert(
          Alert.AlertType.ERROR, "Interval is empty", "Please enter an interval");
      return;
    }
    if (selectedPathValue.getText().isEmpty()) {
      log.warn("Path is empty");
      AlertHandler.showAlert(Alert.AlertType.ERROR, "Path is empty", "Please select a path");
      return;
    }

    try {
      SettingsData.setHandle(inputHandle.getText());
      SettingsData.setInterval(Integer.parseInt(inputInterval.getText()));
      SettingsHandler settingsHandler = new SettingsHandler();
      settingsHandler.saveSettings();
      mainViewController.onStartPressed();
    } catch (NumberFormatException numberFormatException) {
      log.warn("Interval is invalid");
      AlertHandler.showAlert(
          Alert.AlertType.ERROR, "Interval is invalid", "Please enter a valid interval");
    }
  }

  /**
   * Handles the selection of a channel in the channel selection dropdown.
   *
   * <p>This method retrieves the currently selected channel from the channel selection dropdown and
   * updates the application's settings to reflect the selected channel. Additionally, it performs
   * necessary updates to ensure that any dependent paths or configurations are aligned with the new
   * channel selection.
   *
   * <p>The method is triggered by a user action on the channel selection UI component.
   */
  @FXML
  protected void onChannelSelection() {
    SettingsData.setSelectedChannel(channelSelection.getSelectionModel().getSelectedItem());
    setSelectedPath();
  }

  /**
   * Sets the appropriate text value for the {@code selectedPathValue} UI element based on the
   * currently selected channel in the application settings.
   *
   * <p>This method evaluates the selected channel retrieved from {@code
   * SettingsData.getSelectedChannel()} and assigns the respective path string obtained from {@code
   * SettingsData} to the text property of the {@code selectedPathValue} label. If the selected
   * channel does not match any predefined channels, it defaults to the "Live" channel path.
   *
   * <p>The supported channels and their corresponding paths include:
   *
   * <ul>
   *   <li>PTU: Path retrieved via {@code SettingsData.getPathPtu()}
   *   <li>EPTU: Path retrieved via {@code SettingsData.getPathEptu()}
   *   <li>HOTFIX: Path retrieved via {@code SettingsData.getPathHotfix()}
   *   <li>TECH_PREVIEW: Path retrieved via {@code SettingsData.getPathTechPreview()}
   *   <li>CUSTOM: Path retrieved via {@code SettingsData.getPathCustom()}
   *   <li>Default: Path retrieved via {@code SettingsData.getPathLive()}
   * </ul>
   */
  private void setSelectedPath() {
    switch (SettingsData.getSelectedChannel()) {
      case PTU:
        selectedPathValue.setText(SettingsData.getPathPtu());
        break;
      case EPTU:
        selectedPathValue.setText(SettingsData.getPathEptu());
        break;
      case HOTFIX:
        selectedPathValue.setText(SettingsData.getPathHotfix());
        break;
      case TECH_PREVIEW:
        selectedPathValue.setText(SettingsData.getPathTechPreview());
        break;
      case CUSTOM:
        selectedPathValue.setText(SettingsData.getPathCustom());
        break;
      default:
        selectedPathValue.setText(SettingsData.getPathLive());
        break;
    }
  }

  /**
   * Sets the main controller for managing the application's primary view.
   *
   * <p>This method is typically used by other controllers to establish a connection with the
   * MainViewController. It allows the passed MainViewController instance to maintain communication
   * and manage state across different application views.
   *
   * @param mainViewController the main controller instance to be set.
   */
  void setMainViewController(MainViewController mainViewController) {
    this.mainViewController = mainViewController;
  }

  /**
   * Handles actions required when the application's settings have been modified.
   *
   * <p>This method is intended to ensure that the current state of the application reflects any
   * changes made to the settings. Specifically, it recalculates or updates the selected path based
   * on the new configuration. It acts as a listener or trigger following settings updates.
   *
   * <p>The method is overridden to provide a concrete implementation for managing settings changes
   * within the respective context of this implementation.
   */
  @Override
  public void settingsChanged() {
    setSelectedPath();
  }
}

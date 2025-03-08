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

import de.greluc.sc.sckm.FileHandler;
import de.greluc.sc.sckm.settings.SettingsData;
import de.greluc.sc.sckm.settings.SettingsHandler;
import java.io.File;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Generated;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * The SettingsViewController class manages the user interface for application settings. It provides
 * functionality for displaying and editing file path configurations, as well as saving the changes
 * through the SettingsHandler.
 *
 * <p>This class is designed for use with JavaFX components, leveraging FXML annotations to bind UI
 * elements and actions.
 *
 * <p>Key Features:
 *
 * <ul>
 *   <li>Display settings for various file paths such as LIVE, PTU, EPTU, HOTFIX, TECH PREVIEW, and
 *       CUSTOM.
 *   <li>Allows users to select or modify file paths via a file chooser dialog.
 *   <li>Saves updated settings by persisting changes in SettingsData and delegating save operations
 *       to the SettingsHandler.
 *   <li>Supports dynamic updates to the settings interface based on user input.
 *   <li>Handles closing the settings window upon saving changes or user interaction.
 * </ul>
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
public class SettingsViewController {
  @FXML private TextField inputPathLive;
  @FXML private TextField inputPathPtu;
  @FXML private TextField inputPathEptu;
  @FXML private TextField inputPathHotfix;
  @FXML private TextField inputPathTechPreview;
  @FXML private TextField inputPathCustom;
  @FXML private CheckBox cbWriteKillEvent;
  @FXML private CheckBox cbKillerMode;
  @Setter private SettingsHandler settingsHandler;

  /**
   * Initializes the view components of the {@link  SettingsViewController}.
   *
   * <p>Sets the text of input fields with the corresponding file path values retrieved from
   * {@link SettingsData}.
   *
   * <p>Specifically, it initializes the following input fields:
   *
   * <ul>
   *   <li>{@code inputPathLive} with the live environment path.
   *   <li>{@code inputPathPtu} with the Public Test Universe (PTU) path.
   *   <li>{@code inputPathEptu} with the Experimental Public Test Universe (EPTU) path.
   *   <li>{@code inputPathHotfix} with the Hotfix environment path.
   *   <li>{@code inputPathTechPreview} with the Tech Preview environment path.
   *   <li>{@code inputPathCustom} with a custom path.
   * </ul>
   *
   * <p>This method ensures that the view reflects the current settings stored in the {@link
   * SettingsData} class.
   */
  @FXML
  protected void initialize() {
    inputPathLive.setText(SettingsData.getPathLive());
    inputPathPtu.setText(SettingsData.getPathPtu());
    inputPathEptu.setText(SettingsData.getPathEptu());
    inputPathHotfix.setText(SettingsData.getPathHotfix());
    inputPathTechPreview.setText(SettingsData.getPathTechPreview());
    inputPathCustom.setText(SettingsData.getPathCustom());
    cbWriteKillEvent.setSelected(SettingsData.isWriteKillEventToFile());
    cbKillerMode.setSelected(SettingsData.isKillerModeActive());
  }

  /**
   * Handles the save action triggered in the settings view. This method captures the current input
   * values from the UI, updates the corresponding paths in the {@link SettingsData} class, and then
   * persists these updated settings using the {@code settingsHandler}. After saving the settings,
   * the associated window is closed.
   *
   * <p>The following paths are updated based on the user input:
   *
   * <ul>
   *   <li>Live environment path.<br>
   *   <li>PTU (Public Test Universe) environment path.<br>
   *   <li>EPTU (Experimental Public Test Universe) environment path.<br>
   *   <li>Hotfix environment path.<br>
   *   <li>Tech Preview environment path.<br>
   *   <li>Custom path.
   * </ul>
   */
  @FXML
  protected void onSave() {
    SettingsData.setPathLive(inputPathLive.getText());
    SettingsData.setPathPtu(inputPathPtu.getText());
    SettingsData.setPathEptu(inputPathEptu.getText());
    SettingsData.setPathHotfix(inputPathHotfix.getText());
    SettingsData.setPathTechPreview(inputPathTechPreview.getText());
    SettingsData.setPathCustom(inputPathCustom.getText());
    SettingsData.setWriteKillEventToFile(cbWriteKillEvent.isSelected());
    SettingsData.setKillerModeActive(cbKillerMode.isSelected());
    settingsHandler.saveSettings();
    closeWindow();
  }

  /** Closes the dedicated settings window. */
  @FXML
  @Generated
  private void closeWindow() {
    var stage = (Stage) inputPathLive.getScene().getWindow();
    stage.close();
  }

  /**
   * Handles the event when the "Live" button is clicked in the settings view. Updates the text
   * content of the {@code inputPathLive} field to reflect a selected file path.
   *
   * <p>This method leverages the {@link #getPath()} method to open a file chooser dialog, allowing
   * the user to select a file or directory. The selected path is then displayed in the {@code
   * inputPathLive} input field.
   */
  @FXML
  private void onLiveClicked() {
    inputPathLive.setText(getPath());
  }

  /**
   * Handles the event when the "PTU" button is clicked in the settings view. Updates the text
   * content of the {@code inputPathPtu} field with a selected file path.
   *
   * <p>This method utilizes the {@link #getPath()} method to display a file chooser dialog,
   * allowing the user to select a file or directory. The selected path is assigned to the {@code
   * inputPathPtu} input field.
   */
  @FXML
  private void onPtuClicked() {
    inputPathPtu.setText(getPath());
  }

  /**
   * Handles the event when the "EPTU" button is clicked in the settings view. Updates the text
   * content of the {@code inputPathEptu} field with a selected file path.
   *
   * <p>This method utilizes the {@link #getPath()} method to display a file chooser dialog,
   * allowing the user to select a file or directory. The selected path is assigned to the {@code
   * inputPathEptu} input field.
   */
  @FXML
  private void onEptuClicked() {
    inputPathEptu.setText(getPath());
  }

  /**
   * Handles the event triggered when the "Hotfix" button is clicked in the settings view. Updates
   * the text content of the {@code inputPathHotfix} field with a selected file path.
   *
   * <p>This method uses the {@link #getPath()} method to open a file chooser dialog, allowing the
   * user to select a file or directory. The chosen path is then displayed in the {@code
   * inputPathHotfix} input field.
   */
  @FXML
  private void onHotfixClicked() {
    inputPathHotfix.setText(getPath());
  }

  /**
   * Handles the event when the "Tech Preview" button is clicked in the settings view. Updates the
   * text content of the {@code inputPathTechPreview} field with a selected file path.
   *
   * <p>This method uses the {@code getPath()} method to open a file chooser dialog, allowing the
   * user to select a file or directory. The selected path is then assigned to the {@code
   * inputPathTechPreview} input field.
   */
  @FXML
  private void onTechPreviewClicked() {
    inputPathTechPreview.setText(getPath());
  }

  /**
   * Handles the event when the "Custom" button is clicked in the settings view. Updates the text
   * content of the {@code inputPathCustom} field with a selected file path.
   *
   * <p>This method utilizes the {@link #getPath()} method to display a file chooser dialog,
   * allowing the user to select a file or directory. The selected path is then set as the value of
   * the {@code inputPathCustom} input field.
   */
  @FXML
  private void onCustomClicked() {
    inputPathCustom.setText(getPath());
  }

  /**
   * Clears the text content of the {@code inputPathLive} field.
   *
   * <p>This method is triggered when the "Clear" action is performed for the live environment path
   * input. It resets the text of the {@code inputPathLive} field to an empty string, ensuring no
   * value is displayed in the corresponding input field.
   */
  @FXML
  private void onLiveClear() {
    inputPathLive.setText("");
  }

  /**
   * Clears the text content of the {@code inputPathPtu} field.
   *
   * <p>This method is triggered when the "Clear" action is performed for the PTU (Public Test
   * Universe) environment path input. It resets the text of the {@code inputPathPtu} field to an
   * empty string, ensuring no value is displayed in the corresponding input field.
   */
  @FXML
  private void onPtuClear() {
    inputPathPtu.setText("");
  }

  /**
   * Clears the text content of the {@code inputPathEptu} field.
   *
   * <p>This method is triggered when the "Clear" action is performed for the Experimental Public
   * Test Universe (EPTU) environment path input. It resets the text of the {@code inputPathEptu}
   * field to an empty string, ensuring no value is displayed in the corresponding input field.
   */
  @FXML
  private void onEptuClear() {
    inputPathEptu.setText("");
  }

  /**
   * Clears the text content of the {@code inputPathHotfix} field.
   *
   * <p>This method is triggered when the "Clear" action is performed for the Hotfix environment
   * path input. It resets the text of the {@code inputPathHotfix} field to an empty string,
   * ensuring no value is displayed in the corresponding input field.
   */
  @FXML
  private void onHotfixClear() {
    inputPathHotfix.setText("");
  }

  /**
   * Clears the text content of the {@code inputPathTechPreview} field.
   *
   * <p>This method is triggered when the "Clear" action is performed for the Tech Preview
   * environment path input. It resets the text of the {@code inputPathTechPreview} field to an
   * empty string, ensuring no value is displayed in the corresponding input field.
   */
  @FXML
  private void onTechPreviewClear() {
    inputPathTechPreview.setText("");
  }

  /**
   * Clears the text content of the {@code inputPathCustom} field.
   *
   * <p>This method is triggered when the "Clear" action is performed for the custom environment
   * path input. It resets the text of the {@code inputPathCustom} field to an empty string,
   * ensuring no value is displayed in the corresponding input field.
   */
  @FXML
  private void onCustomClear() {
    inputPathCustom.setText("");
  }

  /**
   * Opens a file chooser dialog to allow the user to select a file or directory and retrieves the
   * absolute path of the selected item.
   *
   * @return the absolute path of the selected file or directory as a non-null string. If no
   *     selection is made, returns an empty string.
   */
  private @NotNull String getPath() {
    Optional<File> fileContainer = FileHandler.openFileChooser();
    return fileContainer.map(File::getAbsolutePath).orElse("");
  }
}

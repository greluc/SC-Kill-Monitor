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

import static de.greluc.sc.sckm.data.KillEventExtractor.extractKillEvents;

import de.greluc.sc.sckm.data.KillEvent;
import de.greluc.sc.sckm.data.KillEventFormatter;
import de.greluc.sc.sckm.settings.SettingsData;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Controller class for managing the scan view in the application.
 *
 * <p>The {@code ScanViewController} is responsible for scanning log files, processing kill events,
 * and updating the user interface accordingly. It supports features such as:
 *
 * <ul>
 *   <li>Continuous scanning of log files based on user settings.
 *   <li>Interactive controls for starting, stopping, and filtering displayed kill events.
 *   <li>Thread-safe updates to the user interface using JavaFX's {@code Platform.runLater}
 *       mechanism.
 * </ul>
 *
 * <p>This class utilizes a single-threaded ExecutorService for background scanning operations and
 * ensures proper shutdown and resource cleanup when needed. The controller also integrates with the
 * primary application view via the {@link MainViewController}.
 *
 * <p>To properly initialize and use this controller, ensure that it is linked to an FXML layout
 * file and the required dependencies (e.g., JavaFX annotations and Log4j2) are included in the
 * project.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
@Log4j2
public class ScanViewController {
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final List<KillEvent> killEvents = new ArrayList<>();
  private final List<KillEvent> evaluatedKillEvents = new ArrayList<>();
  @FXML private VBox textPane;
  @FXML private ScrollPane scrollPane;
  @FXML private CheckBox cbShowAll;
  @FXML private Label labelKillCount;
  @FXML private Label labelKillCountValue;
  @FXML private Label labelDeathCountValue;
  private int killCount = 0;
  private int deathCount = 0;
  private MainViewController mainViewController;

  /**
   * Initializes the UI components and prepares the application state.
   *
   * <p>This method is automatically called when the associated FXML file is loaded. It performs the
   * following tasks:
   *
   * <ul>
   *   <li>Configures the {@code textPane} to fill the width of its container.
   *   <li>Sets the {@code scrollPane} to adjust its dimensions to fit both height and width.
   *   <li>Submits the {@link #startScan()} method to the {@code executorService} for execution.
   *   <li>Synchronizes the {@code cbShowAll} checkbox with the persisted {@code SettingsData}.
   * </ul>
   */
  @FXML
  protected void initialize() {
    textPane.setFillWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setFitToWidth(true);
    executorService.submit(this::startScan);
    cbShowAll.setSelected(SettingsData.isShowAllActive());
    if (!SettingsData.isKillerModeActive()) {
      labelKillCount.setVisible(false);
      labelKillCountValue.setVisible(false);
    }
    labelKillCountValue.setText(String.valueOf(killCount));
    labelDeathCountValue.setText(String.valueOf(deathCount));
  }

  /**
   * Handles the "Stop" button press event action.
   *
   * <p>This method is responsible for halting all ongoing tasks by immediately terminating the
   * execution of the associated ExecutorService. It also delegates the stop action to the main view
   * controller, ensuring that any associated view state or logic is properly reverted or handled.
   *
   * <p>This method should be invoked when the user decides to interrupt the active process and
   * return the application to a "stopped" state.
   */
  @FXML
  private void onStopPressed() {
    executorService.shutdownNow();
    mainViewController.onStopPressed();
  }

  /**
   * Handles the action event triggered when the "Show All" checkbox or button is clicked.
   *
   * <p>This method updates the "show all" setting based on the selection status
   * of the associated checkbox. It clears the existing list of evaluated kill
   * events, resets the text pane content, and initializes counters for kill
   * and death counts. The respective labels displaying these counts are also updated.
   * Finally, it refreshes and displays the current list of kill events.
   */
  @FXML
  protected void onShowAllClicked() {
    SettingsData.setShowAllActive(cbShowAll.isSelected());
    evaluatedKillEvents.clear();
    textPane.getChildren().clear();
    killCount = 0;
    deathCount = 0;
    labelKillCountValue.setText(String.valueOf(killCount));
    labelDeathCountValue.setText(String.valueOf(deathCount));
    displayKillEvents();
  }

  /**
   * Sets the main view controller. This method establishes the main controller responsible for
   * interacting with and managing the primary application views and their transitions.
   *
   * @param mainViewController the instance of {@code MainViewController} to be set
   */
  void setMainViewController(MainViewController mainViewController) {
    this.mainViewController = mainViewController;
  }

  /**
   * Starts the scanning process for kill events by continuously monitoring a log file based on
   * the selected channel and update interval configured in settings. The method determines the
   * appropriate file path based on the currently selected channel, initializes counters for kill
   * and death events, and repeatedly extracts event data from the log file at the specified
   * interval.
   *
   * <p>The scanning process logs details about the selected configuration and updates the GUI
   * with the processed kill events. If an error occurs during file processing or if the scan
   * thread is interrupted, the method gracefully terminates the scanning process.
   *
   * <p><b>Note:</b> This method continuously runs until an error occurs or the thread is
   * interrupted. Proper thread management is required when invoking this method to ensure
   * efficient system resource usage.
   *
   * <p>Operational flow:
   * <ul>
   *   <li>Determine the correct file path for the selected channel from user settings.</li>
   *   <li>Log configuration details such as handle, interval, channel, and file path.</li>
   *   <li>Initialize scanning by monitoring the log file for kill events.</li>
   *   <li>Extract and process kill event data, then update the GUI.</li>
   *   <li>Sleep for the configured interval before repeating the scan.</li>
   *   <li>Handle errors and interruptions in a robust manner to stop the scan gracefully.</li>
   * </ul>
   */
  public void startScan() {
    String selectedPathValue =
        switch (SettingsData.getSelectedChannel()) {
          case PTU -> SettingsData.getPathPtu();
          case EPTU -> SettingsData.getPathEptu();
          case HOTFIX -> SettingsData.getPathHotfix();
          case TECH_PREVIEW -> SettingsData.getPathTechPreview();
          case CUSTOM -> SettingsData.getPathCustom();
          default -> SettingsData.getPathLive();
        };

    log.info("Starting scan for kill events...");
    log.info("Using the selected handle: {}", SettingsData.getHandle());
    log.info("Using the selected interval: {}", SettingsData.getInterval());
    log.info("Using the selected channel: {}", SettingsData.getSelectedChannel());
    log.info("Using the selected log file path: {}", selectedPathValue);
    ZonedDateTime scanStartTime = ZonedDateTime.now();
    killCount = 0;
    deathCount = 0;

    while (true) {
      try {
        extractKillEvents(killEvents, selectedPathValue, scanStartTime);
      } catch (IOException ioException) {
        Platform.runLater(this::onStopPressed);
        return;
      }
      log.debug("Finished extracting kill events");
      displayKillEvents();
      log.debug("Finished updating the GUI with kill events");

      try {
        TimeUnit.SECONDS.sleep(SettingsData.getInterval());
      } catch (InterruptedException e) {
        log.debug("Scan thread was interrupted. Terminating...");
        Thread.currentThread().interrupt();
        return;
      }
    }
  }

  /**
   * Processes and displays kill events within the application. This method iterates over all kill
   * events, filtering and evaluating each event to update related statistics and UI elements such
   * as kill count, death count, and displaying the kill event in a specified pane. The method ensures
   * no duplicate event evaluations by tracking processed events.
   *
   * <p>The method performs the following tasks:
   * <ul>
   *   <li>Skips evaluation of kill events already processed (stored in {@code evaluatedKillEvents}).
   *   <li>Filters events based on criteria such as player presence and active settings.
   *   <li>Modifies kill or death count based on configuration settings and event details, such as
   *       the killer and killed player.
   *   <li>Updates the UI in a thread-safe manner using the JavaFX {@code Platform.runLater} method
   *       to reflect kill and death statistics in labels and add the kill event pane to the
   *       designated component.
   *   <li>Adds processed kill events to the list of evaluated events.
   * </ul>
   *
   * <p>Conditions and Settings:
   * <ul>
   *   <li>Respects the visibility settings from {@code SettingsData} to determine if all active
   *       events should be shown or only specific ones.
   *   <li>Evaluates the event in "Killer Mode" to categorize events and correctly update the kill or
   *       death count.
   * </ul>
   */
  private void displayKillEvents() {
    killEvents.forEach(
        killEvent -> {
          if (!evaluatedKillEvents.contains(killEvent)) {
            if (checkIfNoPlayer(killEvent) && !SettingsData.isShowAllActive()) {
              return;
            }
            if (killEvent.killer().equals(SettingsData.getHandle())
                && SettingsData.isKillerModeActive()) {
              if (SettingsData.getHandle().equals(killEvent.killedPlayer())) {
                deathCount++;
              } else {
                killCount++;
              }
            } else {
              deathCount++;
            }
            Platform.runLater(
                () -> {
                  textPane.getChildren().add(getKillEventPane(killEvent));
                  labelKillCountValue.setText(String.valueOf(killCount));
                  labelDeathCountValue.setText(String.valueOf(deathCount));
                });
            evaluatedKillEvents.add(killEvent);
          }
        });
  }

  /**
   * Creates and returns a VBox containing a non-editable TextArea, which displays the formatted
   * details of the provided KillEvent object.
   *
   * @param killEvent the KillEvent containing data to be displayed in the TextArea; must not be
   *     null.
   * @return a VBox containing the formatted KillEvent display; never null.
   */
  private @NotNull VBox getKillEventPane(@NotNull KillEvent killEvent) {
    TextArea textArea = new TextArea(KillEventFormatter.format(killEvent));
    textArea.setEditable(false);
    textArea.setMinHeight(160);
    textArea.setMaxHeight(160);

    VBox wrapper = new VBox(textArea);
    wrapper.prefWidthProperty().bind(textPane.widthProperty());
    textArea.prefWidthProperty().bind(wrapper.widthProperty());

    VBox.setMargin(textArea, new Insets(5, 10, 0, 0)); // Top, Right, Bottom, Left
    return wrapper;
  }

  /**
   * Checks if neither the killer nor the killed player in the given kill event represents an actual
   * player. This method inspects the names of the killer and killed player to determine if they are
   * system entities or non-player characters (NPCs).
   *
   * @param killEvent The kill event containing information about the killer and killed player. Must
   *     not be null.
   * @return {@code true} if neither the killer nor the killed player is an actual player, {@code
   *     false} otherwise.
   */
  private boolean checkIfNoPlayer(@NotNull KillEvent killEvent) {
    if (killEvent.killer().toLowerCase().contains("unknown")
        || killEvent.killer().toLowerCase().contains("aimodule")
        || killEvent.killer().toLowerCase().contains("pu_")
        || killEvent.killer().toLowerCase().contains("npc_")
        || killEvent.killer().toLowerCase().contains("kopion_")) {
      return true;
    } else
      return killEvent.killedPlayer().toLowerCase().contains("unknown")
          || killEvent.killedPlayer().toLowerCase().contains("aimodule")
          || killEvent.killedPlayer().toLowerCase().contains("pu_")
          || killEvent.killedPlayer().toLowerCase().contains("npc_")
          || killEvent.killedPlayer().toLowerCase().contains("kopion_");
  }
}

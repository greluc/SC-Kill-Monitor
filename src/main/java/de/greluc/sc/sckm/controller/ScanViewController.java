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
import de.greluc.sc.sckm.settings.SettingsHandler;
import de.greluc.sc.sckm.settings.SettingsListener;
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
 * @version 1.5.0
 * @since 1.0.0
 */
@Log4j2
public class ScanViewController implements SettingsListener {
  private ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final List<KillEvent> killEvents = new ArrayList<>();
  private final List<KillEvent> evaluatedKillEvents = new ArrayList<>();
  @FXML private VBox textPane;
  @FXML private ScrollPane scrollPane;
  @FXML private CheckBox cbShowAll;
  @FXML private CheckBox cbStreamerMode;
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
    cbStreamerMode.setSelected(SettingsData.isStreamerModeActive());
    if (!SettingsData.isKillerModeActive()) {
      labelKillCount.setVisible(false);
      labelKillCountValue.setVisible(false);
    }
    labelKillCountValue.setText(String.valueOf(killCount));
    labelDeathCountValue.setText(String.valueOf(deathCount));
    SettingsData.addListener(this);
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
   * <p>This method updates the "show all" setting based on the selection status of the associated
   * checkbox. It clears the existing list of evaluated kill events, resets the text pane content,
   * and initializes counters for kill and death counts. The respective labels displaying these
   * counts are also updated. Finally, it refreshes and displays the current list of kill events.
   */
  @FXML
  protected void onShowAllClicked() {
    SettingsData.setShowAllActive(cbShowAll.isSelected());
    SettingsData.settingsChanged();
  }

  @FXML
  protected void onStreamerModeClicked() {
    SettingsData.setStreamerModeActive(cbStreamerMode.isSelected());
    SettingsData.settingsChanged();
    SettingsHandler settingsHandler = new SettingsHandler();
    settingsHandler.saveSettings();
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
   * Initiates the scanning process for tracking and logging kill events from a specified data
   * source. This method identifies the relevant log file path based on the selected channel
   * settings and continuously monitors the log file for events. Detected events are processed, and
   * the results are displayed via the user interface.
   *
   * <p>The scanning process will continue indefinitely in a loop until an external condition
   * interrupts it, such as an I/O exception or a manual stop signal. The method also defines
   * specific behaviors for handling exceptions or interruptions during the scan operation.
   *
   * <p>Key actions performed by this method include:
   *
   * <ul>
   *   <li>Retrieving the selected channel and its corresponding log file path
   *   <li>Logging initialization data such as the handle, interval, and channel information
   *   <li>Extracting kill events from the log file
   *   <li>Displaying kill event data by updating the GUI
   *   <li>Handling delays between scan iterations based on configured time intervals
   *   <li>Managing interruptions or input/output exceptions gracefully
   * </ul>
   *
   * <p>The kill event extraction and GUI update logic are delegated to separate methods, ensuring
   * modular and maintainable code. Any detected anomalies, such as I/O errors or interrupted
   * threads, are appropriately logged and managed.
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
      if (!extractKillEvents(killEvents, selectedPathValue, scanStartTime)) {
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
   * Displays kill events by iterating over the list of kill events and determining whether to show
   * them based on certain conditions. Depending on the player's involvement in the kill event,
   * updates the UI components, including the kill count, death count, and a displayed list of kill
   * events.
   *
   * <p>This method evaluates each kill event to ensure it has not been processed already. It
   * applies filters based on player-related settings, such as whether to display events involving
   * the player, all players, or specific roles. If the conditions are met, the method updates the
   * kill and death counts accordingly and modifies the UI components.
   *
   * <p>Each kill event is added to the text pane if it passes the evaluation. The method ensures
   * that the labels for kills and deaths are updated correctly after processing kill events.
   *
   * <p>Important considerations: - Events are only processed if not already evaluated. - The method
   * adheres to user-defined settings from {@code SettingsData}, such as whether to show all events,
   * killer mode, or specific player handles. - UI updates are executed on the JavaFX application
   * thread using {@code Platform.runLater}.
   */
  private void displayKillEvents() {
    killEvents.forEach(
        killEvent -> {
          if (!evaluatedKillEvents.contains(killEvent)) {
            if (checkIfNoPlayer(killEvent) && !SettingsData.isShowAllActive()) {
              return;
            }
            if (killEvent.killingPlayer().equalsIgnoreCase(SettingsData.getHandle())) {
              if (SettingsData.getHandle().equalsIgnoreCase(killEvent.killedPlayer())) {
                if (!SettingsData.isShowAllActive()) {
                  return;
                }
                deathCount++;
              } else {
                if (!SettingsData.isKillerModeActive()) {
                  return;
                }
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
   * Creates a VBox pane containing a non-editable TextArea that displays formatted information
   * about the provided KillEvent. The TextArea dynamically adjusts to match the state of the
   * Streamer Mode setting.
   *
   * @param killEvent The KillEvent object containing information to be displayed in the TextArea.
   *     Must not be null.
   * @return A VBox containing a TextArea with the formatted KillEvent details. Will never return
   *     null.
   */
  private @NotNull VBox getKillEventPane(@NotNull KillEvent killEvent) {
    TextArea textArea =
        new TextArea(KillEventFormatter.format(killEvent, SettingsData.isStreamerModeActive()));
    textArea.setEditable(false);
    textArea.setMinHeight(160);
    textArea.setMaxHeight(160);
    textArea.setStyle("-fx-font-family: \"Segoe UI\";");
    textArea
        .onMouseClickedProperty()
        .set(
            event -> {
              if (SettingsData.isStreamerModeActive()) {
                textArea.setText(KillEventFormatter.format(killEvent, false));
              }
            });

    VBox wrapper = new VBox(textArea);
    wrapper.prefWidthProperty().bind(textPane.widthProperty());
    textArea.prefWidthProperty().bind(wrapper.widthProperty());

    VBox.setMargin(textArea, new Insets(5, 10, 0, 0)); // Top, Right, Bottom, Left
    return wrapper;
  }

  /**
   * Checks if neither the killingPlayer nor the killed player in the given kill event represents an
   * actual player. This method inspects the names of the killingPlayer and killed player to
   * determine if they are system entities or non-player characters (NPCs).
   *
   * @param killEvent The kill event containing information about the killingPlayer and killed
   *     player. Must not be null.
   * @return {@code true} if neither the killingPlayer nor the killed player is an actual player,
   *     {@code false} otherwise.
   */
  private boolean checkIfNoPlayer(@NotNull KillEvent killEvent) {
    if (killEvent.killingPlayer().toLowerCase().contains("unknown")
        || killEvent.killingPlayer().toLowerCase().contains("aimodule")
        || killEvent.killingPlayer().toLowerCase().contains("pu_")
        || killEvent.killingPlayer().toLowerCase().contains("npc_")
        || killEvent.killingPlayer().toLowerCase().contains("kopion_")
        || killEvent.killingPlayer().toLowerCase().contains("missionentitystreamable_")) {
      return true;
    } else
      return killEvent.killedPlayer().toLowerCase().contains("unknown")
          || killEvent.killedPlayer().toLowerCase().contains("aimodule")
          || killEvent.killedPlayer().toLowerCase().contains("pu_")
          || killEvent.killedPlayer().toLowerCase().contains("npc_")
          || killEvent.killedPlayer().toLowerCase().contains("kopion_")
          || killEvent.killedPlayer().toLowerCase().contains("missionentitystreamable_");
  }

  /**
   * Invoked when settings have been changed.
   *
   * <p>This method is called to handle necessary actions after a change in settings. Upon
   * invocation, it resets the display to ensure any updates or modifications are accurately
   * reflected.
   *
   * <p>Subclasses overriding this method should ensure that they call the superclass implementation
   * to maintain proper reset functionality unless explicitly intended otherwise.
   */
  @Override
  public void settingsChanged() {
    resetDisplay();
  }

  /**
   * Resets the display by clearing relevant data structures and UI elements, and reinitializing key
   * values for the kill and death counters. This method also updates the visibility of specific UI
   * components based on the current application settings.
   *
   * <p>Specifically, this method performs the following actions:
   *
   * <ul>
   *   <li>Clears the list of evaluated kill events.
   *   <li>Clears all child nodes from the text pane.
   *   <li>Resets kill and death counters to zero.
   *   <li>Updates the text of kill and death counter labels to reflect the reset values.
   *   <li>Adjusts the visibility of kill count labels based on whether killer mode is active.
   *   <li>Triggers re-display of the kill events.
   * </ul>
   */
  private void resetDisplay() {
    executorService.shutdownNow();
    executorService = Executors.newSingleThreadExecutor();
    Platform.runLater(
        () -> {
          evaluatedKillEvents.clear();
          killCount = 0;
          deathCount = 0;
          textPane.getChildren().clear();
          labelKillCountValue.setText(String.valueOf(killCount));
          labelDeathCountValue.setText(String.valueOf(deathCount));
          if (SettingsData.isKillerModeActive()) {
            labelKillCount.setVisible(true);
            labelKillCountValue.setVisible(true);
          } else {
            labelKillCount.setVisible(false);
            labelKillCountValue.setVisible(false);
          }
          executorService.submit(this::startScan);
        });
  }
}

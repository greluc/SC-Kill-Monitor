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

import static de.greluc.sc.sckm.Constants.CUSTOM;
import static de.greluc.sc.sckm.Constants.EPTU;
import static de.greluc.sc.sckm.Constants.HOTFIX;
import static de.greluc.sc.sckm.Constants.PTU;
import static de.greluc.sc.sckm.Constants.TECH_PREVIEW;
import static de.greluc.sc.sckm.data.KillEventExtractor.extractKillEvents;

import de.greluc.sc.sckm.AlertHandler;
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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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
 * @version 1.2.1
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
    cbShowAll.setSelected(SettingsData.isShowAll());
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
   * Handles the "Show All" button click event in the UI.
   *
   * <p>This method updates the application's settings to reflect the state of the "Show All"
   * checkbox. It clears the collection of evaluated kill events and the content displayed in the
   * text pane. After clearing the text pane, it repopulates it by calling the displayKillEvents
   * method.
   */
  @FXML
  protected void onShowAllClicked() {
    SettingsData.setShowAll(cbShowAll.isSelected());
    evaluatedKillEvents.clear();
    textPane.getChildren().clear();
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
   * Starts a continuous scan process for capturing and processing kill events from log files based
   * on the selected channel configuration.
   *
   * <p>This method determines the appropriate log file path based on the selected channel (e.g.,
   * PTU, EPTU, HOTFIX, TECH_PREVIEW, CUSTOM, or default LIVE channel) and initializes a scanning
   * process to repeatedly extract and display kill events. The interval for each scan iteration is
   * determined using the configured interval value from {@link SettingsData}.
   *
   * <p>The scanning process logs diagnostic and informational messages, including details about the
   * selected handle, interval, channel, and log file path. In case of file read errors or
   * interruptions, the method handles exceptions gracefully, ensuring proper logging and cleanup.
   *
   * <p>The scan runs indefinitely until interrupted (e.g., by an external thread), at which point
   * the thread is terminated cleanly.
   *
   * <p>Details:
   *
   * <ul>
   *   <li>Log messages are recorded at various stages, including scan start, extraction completion,
   *       and GUI update completion.
   *   <li>Kill events are extracted and displayed in each scan iteration.
   *   <li>Handles exceptions for I/O errors during file read and interruptions during sleep.
   * </ul>
   *
   * <p><strong>Note:</strong> Ensure that {@link SettingsData} is properly configured before
   * invoking this method, as it relies on the settings like selected channel, interval, handle, and
   * log file paths.
   *
   * @throws RuntimeException if any unexpected error occurs during scanning.
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

    while (true) {
      try{
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
   * Displays the kill events in the user interface while ensuring that each kill event is displayed
   * only once. The method filters and adds the kill events to the text pane based on specific
   * conditions and user settings.
   *
   * <p>The kill events are processed on the JavaFX application thread using the {@code
   * Platform.runLater} method. This ensures that the UI updates occur on the correct thread. Each
   * kill event is checked against a list of already evaluated events to prevent duplication.
   *
   * <p>Kill events are displayed if:
   *
   * <ul>
   *   <li>The killer field matches the user's handle.
   *   <li>The killer's name includes certain predefined substrings (e.g., "unknown", "aimodule",
   *       "pu_", "npc_", "kopion_").
   *   <li>The user settings permit displaying all kill events.
   * </ul>
   *
   * <p>When a kill event meets the display criteria, it is added to the UI and marked as evaluated
   * by adding it to the collection of processed events.
   */
  private void displayKillEvents() {
    Platform.runLater(
        () ->
            killEvents.forEach(
                killEvent -> {
                  if (!evaluatedKillEvents.contains(killEvent)) {
                    if (killEvent.killer().equals(SettingsData.getHandle())
                        || killEvent.killer().toLowerCase().contains("unknown")
                        || killEvent.killer().toLowerCase().contains("aimodule")
                        || killEvent.killer().toLowerCase().contains("pu_")
                        || killEvent.killer().toLowerCase().contains("npc_")
                        || killEvent.killer().toLowerCase().contains("kopion_")) {
                      if (SettingsData.isShowAll()) {
                        textPane.getChildren().add(getKillEventPane(killEvent));
                        evaluatedKillEvents.add(killEvent);
                      }
                    } else {
                      textPane.getChildren().add(getKillEventPane(killEvent));
                      evaluatedKillEvents.add(killEvent);
                    }
                  }
                }));
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
}

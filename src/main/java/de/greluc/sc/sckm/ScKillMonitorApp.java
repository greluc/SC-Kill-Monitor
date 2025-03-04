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

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Main application class for the SC Kill Monitor.
 *
 * <p>This class is responsible for initializing and launching the JavaFX application. It sets the
 * user interface styles and loads the main view. It also handles application lifecycle events such
 * as closing the application.
 *
 * <p>The application window displays the main graphical user interface with a default width of 600
 * and a height of 500.
 *
 * <p>The application exits completely when the user closes the main window.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
@Log4j2
public class ScKillMonitorApp extends Application {

  /**
   * The main entry point of the application.
   *
   * <p>This method is responsible for launching the JavaFX application. It invokes the `launch`
   * method provided by the `Application` class, which initializes and starts the JavaFX lifecycle.
   *
   * @param args Command-line arguments passed to the application.
   */
  public static void main(String[] args) {
    launch();
  }

  /**
   * The main entry point for the JavaFX application. This method is called after the application
   * has been initialized. It sets up the primary stage, applies user interface styles, loads the
   * main view, and configures the application behavior upon closing.
   *
   * @param stage the primary stage for this application, onto which the application scene can be
   *     set. The primary stage will be shown after the start method completes.
   */
  @Override
  public void start(@NotNull Stage stage) {
    try {
      Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
      Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
      FXMLLoader fxmlLoader =
          new FXMLLoader(ScKillMonitorApp.class.getResource("fxml/MainView.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), 700, 500);
      stage.setScene(scene);
      stage.setMinWidth(700);
      stage.setMinHeight(500);
      stage.setTitle("SC Kill Monitor");
      stage
          .getIcons()
          .add(
              new javafx.scene.image.Image(
                  String.valueOf(ScKillMonitorApp.class.getResource("logos/sckm.jpg"))));
      stage.setOnCloseRequest(
          ignored -> {
            Platform.exit();
            System.exit(0);
          });
      stage.show();
    } catch (IOException ioException) {
      log.error("Could not load main view", ioException);
      System.exit(-1);
    }
  }
}

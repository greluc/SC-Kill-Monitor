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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.greluc.sc.sckm.controller.MainViewController;
import de.greluc.sc.sckm.data.ReleaseData;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;
import javafx.scene.control.Alert;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;

/**
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.5.0
 * @since 1.5.0
 */
@Log4j2
public class UpdateHandler {

  public Optional<ReleaseData> checkUpdate() {
    deleteOldUpdateFile();

    try {
      String releaseJson = fetchReleases("greluc", "SC-Kill-Monitor");
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      ReleaseData release = objectMapper.readValue(releaseJson, ReleaseData.class);
      Semver latestVersion = Semver.parse(release.name.substring(1));
      if (latestVersion != null && latestVersion.isGreaterThan(Constants.APP_VERSION)) {
        log.info("New version available: {}", latestVersion);
        return Optional.of(release);
      } else {
        log.info("No new version available.");
        return Optional.empty();
      }
    } catch (NoSuchElementException | IOException e) {
      log.error("Error while checking for update");
      log.trace("Corresponding error:", e);
      return Optional.empty();
    }
  }

  /**
   * Deletes the existing `update.exe` file from the current directory if it exists.
   *
   * <p>This method checks for the presence of a file named `update.exe` in the working directory and
   * attempts to delete it if found. It logs a debug message upon successful deletion and a warning
   * message if the deletion fails due to an {@link IOException}.
   *
   * <p>The method is used to ensure that any outdated update files are removed before initiating a
   * new update process.
   *
   * <p>Note: This method is private and intended solely for internal use within the {@code UpdateHandler}
   * class.
   */
  private void deleteOldUpdateFile() {
    try {
      java.nio.file.Path updateFilePath = java.nio.file.Paths.get("update.msi");
      if (java.nio.file.Files.exists(updateFilePath)) {
        java.nio.file.Files.delete(updateFilePath);
        log.debug("Deleted existing update.exe file from the current directory.");
      }
    } catch (IOException e) {
      log.warn("Failed to delete update.msi file: {}", e.getMessage(), e);
    }
  }

  public void downloadUpdate(@NotNull ReleaseData release) throws IOException {
    URL url = URI.create(release.releaseAssets.getFirst().browser_download_url).toURL();
    try (BufferedInputStream in = new BufferedInputStream(url.openStream());
         FileOutputStream fileOutputStream = new FileOutputStream("update.msi")) {
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
    } catch (IOException e) {
      // handle exception
    }
  }

  public void startUpdate(@NotNull ReleaseData release, @NotNull MainViewController mainViewController) {
    try {
      downloadUpdate(release);
      new ProcessBuilder("update.msi").start();
      mainViewController.onClosePressed();
    } catch (IOException e) {
      AlertHandler.showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to start the update process. Please try again later or contact the developer for support.", true);
      mainViewController.onClosePressed();
    }
  }

  /**
   * Fetches release data from the GitHub API's releases endpoint.
   *
   * @param owner The repository owner (GitHub username or organization name).
   * @param repo The repository name.
   * @return The JSON response containing the releases' data.
   * @throws IOException if the API call fails.
   */
  public static @NotNull String fetchReleases(@NotNull String owner, @NotNull String repo)
      throws IOException {
    // The URL for the GitHub Releases endpoint
    String apiUrl =
        String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);

    // Open a connection to the API endpoint
    URL url = URI.create(apiUrl).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/vnd.github+json");

    // You can optionally set an API token here for authenticated access
    // connection.setRequestProperty("Authorization", "Bearer YOUR_ACCESS_TOKEN");

    // Check the HTTP response code
    int responseCode = connection.getResponseCode();
    if (responseCode != HttpURLConnection.HTTP_OK) {
      throw new IOException(
          "Failed to fetch data from GitHub API. HTTP Response Code: " + responseCode);
    }

    // Read the response into a StringBuilder
    StringBuilder response = new StringBuilder();
    try (BufferedReader in =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      String line;
      while ((line = in.readLine()) != null) {
        response.append(line);
      }
    }

    // Close the connection
    connection.disconnect();

    return response.toString();
  }
}

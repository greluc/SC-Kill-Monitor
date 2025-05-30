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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.greluc.sc.sckm.controller.MainViewController;
import de.greluc.sc.sckm.data.ReleaseData;
import de.greluc.sc.sckm.exceptions.ConnectionException;
import de.greluc.sc.sckm.exceptions.DownloadException;
import de.greluc.sc.sckm.exceptions.IntegrityException;
import de.greluc.sc.sckm.exceptions.ParseException;
import de.greluc.sc.sckm.exceptions.UpdateException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javafx.scene.control.Alert;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;

/**
 * Handles the update process for the SC Kill Monitor application.
 * 
 * <p>This class is responsible for checking for updates, downloading update files,
 * verifying their integrity, and launching the installer. It uses a secure connection
 * to the GitHub API to fetch release information and implements proper exception handling
 * to ensure a robust update process.
 * 
 * <p>The class uses custom exceptions to handle different error scenarios:
 * <ul>
 *   <li>{@link ConnectionException} - For network-related errors</li>
 *   <li>{@link DownloadException} - For errors during download</li>
 *   <li>{@link IntegrityException} - For errors during integrity verification</li>
 *   <li>{@link ParseException} - For errors during JSON parsing</li>
 * </ul>
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.5.0
 */
@Log4j2
public class UpdateHandler {

  /**
   * Initializes SSL configuration to ensure secure connections.
   * This method sets up proper certificate validation to prevent MITM attacks.
   * 
   * <p>Uses the default system TrustManager for certificate validation,
   * which validates certificate chains against the system's trusted CA certificates.
   *
   * @throws ConnectionException if there is an error initializing the secure connection
   */
  private void initializeSecureConnection() throws ConnectionException {
    try {
      // Use the default SSLContext which uses the system's trusted CA certificates
      SSLContext sc = SSLContext.getDefault();
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Use the default hostname verifier which properly validates that 
      // the hostname matches the certificate
      HttpsURLConnection.setDefaultHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

      log.debug("Secure connection initialized with system default TrustManager and HostnameVerifier");
    } catch (Exception e) {
      log.error("Failed to initialize secure connection", e);
      throw new ConnectionException("Failed to initialize secure connection for update check", e);
    }
  }

  /**
   * Computes the SHA-256 checksum of a file.
   *
   * @param filePath The path to the file
   * @return The SHA-256 checksum as a hexadecimal string
   * @throws IOException If an I/O error occurs
   * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available
   */
  private String computeChecksum(Path filePath) throws IOException, NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance(Constants.CHECKSUM_ALGORITHM);
    try (InputStream is = Files.newInputStream(filePath);
         DigestInputStream dis = new DigestInputStream(is, digest)) {
      byte[] buffer = new byte[8192];
      while (dis.read(buffer) != -1) {
        // Read the entire file
      }
    }

    byte[] checksumBytes = digest.digest();
    StringBuilder result = new StringBuilder();
    for (byte b : checksumBytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  /**
   * Verifies the integrity of a downloaded file by comparing its checksum with the expected checksum.
   *
   * @param filePath The path to the file
   * @param expectedChecksum The expected checksum
   * @throws IntegrityException If the checksums don't match or if there's an error computing the checksum
   */
  private void verifyFileIntegrity(Path filePath, String expectedChecksum) throws IntegrityException {
    try {
      String actualChecksum = computeChecksum(filePath);
      boolean isValid = actualChecksum.equalsIgnoreCase(expectedChecksum);
      if (!isValid) {
        log.error("Checksum verification failed. Expected: {}, Actual: {}", 
            expectedChecksum, actualChecksum);
        throw new IntegrityException(expectedChecksum, actualChecksum);
      } else {
        log.info("Checksum verification successful");
      }
    } catch (IOException e) {
      log.error("Failed to verify file integrity due to I/O error", e);
      throw new IntegrityException("Failed to verify file integrity due to I/O error", e);
    } catch (NoSuchAlgorithmException e) {
      log.error("Failed to verify file integrity: checksum algorithm not available", e);
      throw new IntegrityException("Failed to verify file integrity: checksum algorithm not available", e);
    }
  }

  /**
   * Checks for updates by contacting the GitHub API and comparing the latest version
   * with the current application version.
   *
   * <p>This method uses the global exception handler to handle any exceptions that occur
   * during the update check process, ensuring consistent error reporting to the user.
   *
   * @return An Optional containing the release data if a newer version is available,
   *         or an empty Optional if no update is available or if an error occurred
   */
  public Optional<ReleaseData> checkUpdate() {
    try {
      deleteOldUpdateFile();
      initializeSecureConnection();

      String releaseJson = fetchReleases(Constants.GITHUB_REPO_OWNER, Constants.GITHUB_REPO_NAME);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      ReleaseData release = objectMapper.readValue(releaseJson, ReleaseData.class);

      if (release.name == null || release.name.isEmpty()) {
        log.error("Invalid release data: missing or empty name");
        return Optional.empty();
      }

      Semver latestVersion = Semver.parse(release.name.substring(1));
      if (latestVersion != null && latestVersion.isGreaterThan(Constants.APP_VERSION)) {
        log.info("New version available: {}", latestVersion);
        return Optional.of(release);
      } else {
        log.info("No new version available.");
        return Optional.empty();
      }
    } catch (JsonProcessingException e) {
      log.error("Failed to parse release data", e);
      GlobalExceptionHandler.handleException(new ParseException("Failed to parse release data from GitHub API", e), false);
      return Optional.empty();
    } catch (NoSuchElementException e) {
      log.error("Failed to extract version information from release data", e);
      GlobalExceptionHandler.handleException(new ParseException("Failed to extract version information from release data", e), false);
      return Optional.empty();
    } catch (UpdateException e) {
      log.error("Error during update check", e);
      GlobalExceptionHandler.handleException(e, false);
      return Optional.empty();
    } catch (Exception e) {
      // Catch any unexpected exceptions to ensure the application continues running
      log.error("Unexpected error during update check", e);
      GlobalExceptionHandler.handleException(e, false);
      return Optional.empty();
    }
  }

  /**
   * Deletes the existing `update.msi` file from the current directory if it exists.
   *
   * <p>This method checks for the presence of a file named `update.msi` in the working directory and
   * attempts to delete it if found. It logs a debug message upon successful deletion and a warning
   * message if the deletion fails due to an {@link IOException}.
   *
   * <p>The method is used to ensure that any outdated update files are removed before initiating a
   * new update process.
   *
   * <p>Note: This method is private and intended solely for internal use within the {@code UpdateHandler}
   * class. It handles all exceptions internally and does not throw them to the caller.
   */
  private void deleteOldUpdateFile() {
    try {
      Path updateFilePath = Paths.get("update.msi");
      if (Files.exists(updateFilePath)) {
        Files.delete(updateFilePath);
        log.debug("Deleted existing update.msi file from the current directory.");
      }
    } catch (IOException e) {
      log.warn("Failed to delete update.msi file: {}", e.getMessage(), e);
      // We don't throw an exception here because this is not a critical error
      // The update process can continue even if the old file couldn't be deleted
    }
  }

  /**
   * Downloads an update file from the specified URL and verifies its integrity.
   *
   * @param release The release data containing information about the update
   * @return The path to the downloaded file if successful, empty if download or verification fails
   * @throws DownloadException If there is an error downloading the update file
   * @throws IntegrityException If the downloaded file fails integrity verification
   */
  public Optional<Path> downloadUpdate(@NotNull ReleaseData release) throws DownloadException, IntegrityException {
    if (release.releaseAssets.isEmpty()) {
      log.error("No assets found in the release");
      throw new DownloadException("No assets found in the release");
    }

    var asset = release.releaseAssets.getFirst();
    URL url;
    Path updateFilePath = Paths.get("update.msi");

    try {
      url = URI.create(asset.browser_download_url).toURL();
    } catch (MalformedURLException | IllegalArgumentException e) {
      log.error("Invalid download URL: {}", asset.browser_download_url, e);
      throw new DownloadException("Invalid download URL: " + asset.browser_download_url, e);
    }

    try (BufferedInputStream in = new BufferedInputStream(url.openStream());
         FileOutputStream fileOutputStream = new FileOutputStream(updateFilePath.toFile())) {
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }

      // Verify the integrity of the downloaded file
      if (asset.sha256_checksum != null && !asset.sha256_checksum.isEmpty()) {
        try {
          verifyFileIntegrity(updateFilePath, asset.sha256_checksum);
          log.info("Update file downloaded and verified successfully");
          return Optional.of(updateFilePath);
        } catch (IntegrityException e) {
          log.error("Update file integrity verification failed", e);
          try {
            Files.deleteIfExists(updateFilePath);
          } catch (IOException ioe) {
            log.warn("Failed to delete corrupted update file", ioe);
          }
          throw e;
        }
      } else {
        log.warn("No checksum provided for the update file. Skipping integrity verification.");
        return Optional.of(updateFilePath);
      }
    } catch (IOException e) {
      log.error("Failed to download the update file", e);
      try {
        Files.deleteIfExists(updateFilePath);
      } catch (IOException ioe) {
        log.warn("Failed to delete partially downloaded update file", ioe);
      }
      throw new DownloadException("Failed to download the update file", e);
    }
  }

  /**
   * Starts the update process by downloading the update file, verifying its integrity,
   * and launching the installer.
   *
   * <p>This method uses the global exception handler to handle any exceptions that occur
   * during the update process, ensuring consistent error reporting to the user.
   *
   * @param release The release data containing information about the update
   * @param mainViewController The main view controller to close the application after starting the update
   */
  public void startUpdate(@NotNull ReleaseData release, @NotNull MainViewController mainViewController) {
    try {
      Optional<Path> updateFilePath = downloadUpdate(release);
      if (updateFilePath.isPresent()) {
        // Get the absolute path to the MSI file
        String msiPath = updateFilePath.get().toAbsolutePath().toString();
        // Create ProcessBuilder with command and arguments as separate elements
        ProcessBuilder processBuilder = new ProcessBuilder("msiexec", "/i", msiPath);
        // Start the process
        processBuilder.start();
        mainViewController.onClosePressed();
      } else {
        // This should not happen as downloadUpdate() now throws exceptions instead of returning empty Optional
        log.error("Unexpected empty result from downloadUpdate()");
        throw new UpdateException("Unexpected empty result from downloadUpdate()");
      }
    } catch (IOException | UpdateException e) {
      // Let the global exception handler handle these exceptions
      GlobalExceptionHandler.handleException(e, false);
    } catch (Exception e) {
      // Handle any other unexpected exceptions
      GlobalExceptionHandler.handleException(e, true);
    }
  }

  /**
   * Fetches release data from the GitHub API's releases endpoint.
   * This method uses HTTPS with certificate validation to prevent MITM attacks.
   *
   * @param owner The repository owner (GitHub username or organization name).
   * @param repo The repository name.
   * @return The JSON response containing the releases' data.
   * @throws ConnectionException if there is an error connecting to the GitHub API
   * @throws ParseException if the response from the GitHub API is invalid
   */
  public static @NotNull String fetchReleases(@NotNull String owner, @NotNull String repo)
      throws ConnectionException, ParseException {
    // The URL for the GitHub Releases endpoint
    String apiUrl = String.format("%s/repos/%s/%s/releases/latest", 
        Constants.GITHUB_API_URL, owner, repo);

    URL url;
    try {
      url = URI.create(apiUrl).toURL();
    } catch (MalformedURLException | IllegalArgumentException e) {
      throw new ConnectionException("Invalid GitHub API URL: " + apiUrl, e);
    }

    HttpsURLConnection connection = null;
    try {
      // Open a connection to the API endpoint
      connection = (HttpsURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/vnd.github+json");

      // Set connection timeout
      connection.setConnectTimeout(10000); // 10 seconds
      connection.setReadTimeout(10000); // 10 seconds

      // You can optionally set an API token here for authenticated access
      // connection.setRequestProperty("Authorization", "Bearer YOUR_ACCESS_TOKEN");

      // Use the default hostname verifier which properly validates certificates
      // This is important for preventing MITM attacks
      connection.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

      // Check the HTTP response code
      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new ConnectionException(
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

      String responseStr = response.toString();
      if (responseStr.isEmpty()) {
        throw new ParseException("Empty response received from GitHub API");
      }

      return responseStr;
    } catch (SSLException e) {
      throw new ConnectionException("Secure connection to GitHub API failed. Possible MITM attack or SSL configuration issue", e);
    } catch (IOException e) {
      throw new ConnectionException("Failed to connect to GitHub API", e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}

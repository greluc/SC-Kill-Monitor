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

package de.greluc.sc.sckm.util;

import de.greluc.sc.sckm.Constants;
import de.greluc.sc.sckm.exceptions.ConnectionException;
import de.greluc.sc.sckm.exceptions.ParseException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for HTTP/HTTPS operations.
 * 
 * <p>This class provides methods for making secure HTTP requests, downloading files,
 * and initializing secure connections. It includes proper error handling and security
 * measures to prevent MITM attacks.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class HttpUtils {

  /** Default connection timeout in milliseconds. */
  private static final int DEFAULT_TIMEOUT_MS = 10000; // 10 seconds

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private HttpUtils() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Initializes SSL configuration to ensure secure connections.
   * This method sets up proper certificate validation to prevent MITM attacks.
   * 
   * <p>Uses the default system TrustManager for certificate validation,
   * which validates certificate chains against the system's trusted CA certificates.
   *
   * @throws ConnectionException if there is an error initializing the secure connection
   */
  public static void initializeSecureConnection() throws ConnectionException {
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
      throw new ConnectionException("Failed to initialize secure connection", e);
    }
  }

  /**
   * Makes a secure HTTP GET request to the specified URL and returns the response as a string.
   *
   * @param url the URL to make the request to
   * @param acceptHeader the value for the Accept header (e.g., "application/json")
   * @param timeoutMs the connection and read timeout in milliseconds
   * @return the response body as a string
   * @throws ConnectionException if there is an error connecting to the URL
   * @throws ParseException if the response is empty or cannot be parsed
   */
  public static @NotNull String makeSecureGetRequest(
      @NotNull URL url, @NotNull String acceptHeader, int timeoutMs) 
      throws ConnectionException, ParseException {
    
    HttpsURLConnection connection = null;
    try {
      // Open a connection to the URL
      connection = (HttpsURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", acceptHeader);

      // Set connection timeout
      connection.setConnectTimeout(timeoutMs);
      connection.setReadTimeout(timeoutMs);

      // Use the default hostname verifier which properly validates certificates
      // This is important for preventing MITM attacks
      connection.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

      // Check the HTTP response code
      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new ConnectionException(
            "Failed to fetch data. HTTP Response Code: " + responseCode);
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
        throw new ParseException("Empty response received");
      }

      return responseStr;
    } catch (SSLException e) {
      throw new ConnectionException("Secure connection failed. Possible MITM attack or SSL configuration issue", e);
    } catch (IOException e) {
      throw new ConnectionException("Failed to connect to URL: " + url, e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Makes a secure HTTP GET request to the specified URL with default timeout.
   *
   * @param url the URL to make the request to
   * @param acceptHeader the value for the Accept header (e.g., "application/json")
   * @return the response body as a string
   * @throws ConnectionException if there is an error connecting to the URL
   * @throws ParseException if the response is empty or cannot be parsed
   */
  public static @NotNull String makeSecureGetRequest(
      @NotNull URL url, @NotNull String acceptHeader) 
      throws ConnectionException, ParseException {
    return makeSecureGetRequest(url, acceptHeader, DEFAULT_TIMEOUT_MS);
  }

  /**
   * Makes a secure HTTP GET request to the GitHub API.
   *
   * @param owner the repository owner (GitHub username or organization name)
   * @param repo the repository name
   * @return the response body as a string
   * @throws ConnectionException if there is an error connecting to the GitHub API
   * @throws ParseException if the response is empty or cannot be parsed
   */
  public static @NotNull String fetchGitHubReleases(
      @NotNull String owner, @NotNull String repo)
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

    return makeSecureGetRequest(url, "application/vnd.github+json");
  }

  /**
   * Downloads a file from the specified URL to the specified path.
   *
   * @param url the URL to download the file from
   * @param targetPath the path where the file should be saved
   * @return true if the download was successful, false otherwise
   * @throws IOException if an I/O error occurs during the download
   */
  public static boolean downloadFile(@NotNull URL url, @NotNull Path targetPath) throws IOException {
    try (BufferedInputStream in = new BufferedInputStream(url.openStream());
         FileOutputStream fileOutputStream = new FileOutputStream(targetPath.toFile())) {
      
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
      
      return true;
    } catch (IOException e) {
      log.error("Failed to download file from URL: {}", url, e);
      // Clean up partial download if it exists
      try {
        Files.deleteIfExists(targetPath);
      } catch (IOException ioe) {
        log.warn("Failed to delete partially downloaded file: {}", targetPath, ioe);
      }
      throw e;
    }
  }
}
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

package de.greluc.sc.sckm.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for the ReleaseData and ReleaseAsset classes.
 * 
 * <p>This class verifies that the ReleaseData and ReleaseAsset classes correctly
 * store release information and can be serialized/deserialized from JSON.
 *
 * @author SC Kill Monitor Team
 * @version 1.7.0
 * @since 1.7.0
 */
class ReleaseDataTest {

  /**
   * Tests that a ReleaseData object can be created and its fields accessed.
   */
  @Test
  void releaseData_canBeCreatedAndAccessed() {
    // Arrange
    ReleaseData releaseData = new ReleaseData();
    releaseData.name = "v1.7.0";
    releaseData.releaseAssets = new ArrayList<>();
    
    // Act & Assert
    assertEquals("v1.7.0", releaseData.name);
    assertNotNull(releaseData.releaseAssets);
    assertTrue(releaseData.releaseAssets.isEmpty());
  }
  
  /**
   * Tests that a ReleaseAsset object can be created and its fields accessed.
   */
  @Test
  void releaseAsset_canBeCreatedAndAccessed() {
    // Arrange
    ReleaseAsset releaseAsset = new ReleaseAsset();
    releaseAsset.name = "SC-Kill-Monitor-1.7.0.msi";
    releaseAsset.browser_download_url = "https://example.com/download/SC-Kill-Monitor-1.7.0.msi";
    releaseAsset.sha256_checksum = "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890";
    
    // Act & Assert
    assertEquals("SC-Kill-Monitor-1.7.0.msi", releaseAsset.name);
    assertEquals("https://example.com/download/SC-Kill-Monitor-1.7.0.msi", releaseAsset.browser_download_url);
    assertEquals("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890", releaseAsset.sha256_checksum);
  }
  
  /**
   * Tests that a ReleaseData object with ReleaseAssets can be created and accessed.
   */
  @Test
  void releaseData_withAssets_canBeCreatedAndAccessed() {
    // Arrange
    ReleaseAsset asset1 = new ReleaseAsset();
    asset1.name = "SC-Kill-Monitor-1.7.0.msi";
    asset1.browser_download_url = "https://example.com/download/SC-Kill-Monitor-1.7.0.msi";
    asset1.sha256_checksum = "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890";
    
    ReleaseAsset asset2 = new ReleaseAsset();
    asset2.name = "SC-Kill-Monitor-1.7.0.dmg";
    asset2.browser_download_url = "https://example.com/download/SC-Kill-Monitor-1.7.0.dmg";
    asset2.sha256_checksum = "0987654321fedcba0987654321fedcba0987654321fedcba0987654321fedcba";
    
    ReleaseData releaseData = new ReleaseData();
    releaseData.name = "v1.7.0";
    releaseData.releaseAssets = List.of(asset1, asset2);
    
    // Act & Assert
    assertEquals("v1.7.0", releaseData.name);
    assertEquals(2, releaseData.releaseAssets.size());
    assertEquals("SC-Kill-Monitor-1.7.0.msi", releaseData.releaseAssets.get(0).name);
    assertEquals("SC-Kill-Monitor-1.7.0.dmg", releaseData.releaseAssets.get(1).name);
  }
  
  /**
   * Tests that a ReleaseData object can be serialized to and deserialized from JSON.
   */
  @Test
  void releaseData_canBeSerializedAndDeserialized() throws Exception {
    // Arrange
    ReleaseAsset asset = new ReleaseAsset();
    asset.name = "SC-Kill-Monitor-1.7.0.msi";
    asset.browser_download_url = "https://example.com/download/SC-Kill-Monitor-1.7.0.msi";
    asset.sha256_checksum = "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890";
    
    ReleaseData releaseData = new ReleaseData();
    releaseData.name = "v1.7.0";
    releaseData.releaseAssets = List.of(asset);
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    
    // Act
    String json = mapper.writeValueAsString(releaseData);
    ReleaseData deserializedData = mapper.readValue(json, ReleaseData.class);
    
    // Assert
    assertEquals(releaseData.name, deserializedData.name);
    assertEquals(1, deserializedData.releaseAssets.size());
    assertEquals(releaseData.releaseAssets.get(0).name, deserializedData.releaseAssets.get(0).name);
    assertEquals(releaseData.releaseAssets.get(0).browser_download_url, 
        deserializedData.releaseAssets.get(0).browser_download_url);
    assertEquals(releaseData.releaseAssets.get(0).sha256_checksum, 
        deserializedData.releaseAssets.get(0).sha256_checksum);
  }
  
  /**
   * Tests that a ReleaseData object can be deserialized from a JSON string.
   */
  @Test
  void releaseData_canBeDeserializedFromJson() throws Exception {
    // Arrange
    String json = """
        {
          "name": "v1.7.0",
          "assets": [
            {
              "name": "SC-Kill-Monitor-1.7.0.msi",
              "browser_download_url": "https://example.com/download/SC-Kill-Monitor-1.7.0.msi",
              "sha256_checksum": "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890"
            }
          ]
        }
        """;
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    
    // Act
    ReleaseData releaseData = mapper.readValue(json, ReleaseData.class);
    
    // Assert
    assertEquals("v1.7.0", releaseData.name);
    assertEquals(1, releaseData.releaseAssets.size());
    assertEquals("SC-Kill-Monitor-1.7.0.msi", releaseData.releaseAssets.get(0).name);
    assertEquals("https://example.com/download/SC-Kill-Monitor-1.7.0.msi", 
        releaseData.releaseAssets.get(0).browser_download_url);
    assertEquals("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890", 
        releaseData.releaseAssets.get(0).sha256_checksum);
  }
}
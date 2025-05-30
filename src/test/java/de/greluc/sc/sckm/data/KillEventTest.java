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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Test class for the KillEvent record.
 * 
 * <p>This class verifies that the KillEvent record correctly stores kill event data
 * and that its equals and hashCode methods work as expected.
 *
 * @author SC Kill Monitor Team
 * @version 1.7.0
 * @since 1.7.0
 */
class KillEventTest {

  /**
   * Tests that a KillEvent can be created with valid parameters.
   */
  @Test
  void constructor_createsKillEvent_withValidParameters() {
    // Arrange
    UUID id = UUID.randomUUID();
    ZonedDateTime timestamp = ZonedDateTime.now();
    String killedPlayer = "Player1";
    String killingPlayer = "Player2";
    String weapon = "Laser Cannon";
    String weaponClass = "Ship";
    String damageType = "Energy";
    String zone = "PU";
    
    // Act
    KillEvent killEvent = new KillEvent(
        id,
        timestamp,
        killedPlayer,
        killingPlayer,
        weapon,
        weaponClass,
        damageType,
        zone
    );
    
    // Assert
    assertNotNull(killEvent);
    assertEquals(id, killEvent.id());
    assertEquals(timestamp, killEvent.timestamp());
    assertEquals(killedPlayer, killEvent.killedPlayer());
    assertEquals(killingPlayer, killEvent.killingPlayer());
    assertEquals(weapon, killEvent.weapon());
    assertEquals(weaponClass, killEvent.weaponClass());
    assertEquals(damageType, killEvent.damageType());
    assertEquals(zone, killEvent.zone());
  }
  
  /**
   * Tests that two KillEvents with the same id are considered equal,
   * even if other fields are different.
   */
  @Test
  void equals_returnsTrue_forSameId() {
    // Arrange
    UUID id = UUID.randomUUID();
    
    KillEvent event1 = new KillEvent(
        id,
        ZonedDateTime.now(),
        "Player1",
        "Player2",
        "Laser Cannon",
        "Ship",
        "Energy",
        "PU"
    );
    
    // Create a second event with the same id but different other fields
    KillEvent event2 = new KillEvent(
        id,
        ZonedDateTime.now().plusHours(1),
        "Player3",
        "Player4",
        "Railgun",
        "FPS",
        "Physical",
        "Arena Commander"
    );
    
    // Act & Assert
    assertEquals(event1, event2, "KillEvents with the same id should be equal");
  }
  
  /**
   * Tests that two KillEvents with different ids are not considered equal,
   * even if all other fields are the same.
   */
  @Test
  void equals_returnsFalse_forDifferentId() {
    // Arrange
    ZonedDateTime timestamp = ZonedDateTime.now();
    String killedPlayer = "Player1";
    String killingPlayer = "Player2";
    String weapon = "Laser Cannon";
    String weaponClass = "Ship";
    String damageType = "Energy";
    String zone = "PU";
    
    KillEvent event1 = new KillEvent(
        UUID.randomUUID(),
        timestamp,
        killedPlayer,
        killingPlayer,
        weapon,
        weaponClass,
        damageType,
        zone
    );
    
    // Create a second event with a different id but same other fields
    KillEvent event2 = new KillEvent(
        UUID.randomUUID(),
        timestamp,
        killedPlayer,
        killingPlayer,
        weapon,
        weaponClass,
        damageType,
        zone
    );
    
    // Act & Assert
    assertNotEquals(event1, event2, "KillEvents with different ids should not be equal");
  }
  
  /**
   * Tests that the hashCode method is consistent with equals.
   */
  @Test
  void hashCode_isConsistentWithEquals() {
    // Arrange
    UUID id = UUID.randomUUID();
    
    KillEvent event1 = new KillEvent(
        id,
        ZonedDateTime.now(),
        "Player1",
        "Player2",
        "Laser Cannon",
        "Ship",
        "Energy",
        "PU"
    );
    
    // Create a second event with the same id but different other fields
    KillEvent event2 = new KillEvent(
        id,
        ZonedDateTime.now().plusHours(1),
        "Player3",
        "Player4",
        "Railgun",
        "FPS",
        "Physical",
        "Arena Commander"
    );
    
    // Act & Assert
    assertEquals(event1.hashCode(), event2.hashCode(), 
        "KillEvents that are equal should have the same hash code");
  }
  
  /**
   * Tests that the KillEvent record correctly handles null values.
   */
  @Test
  void constructor_handlesNullValues() {
    // Arrange & Act
    KillEvent killEvent = new KillEvent(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );
    
    // Assert
    assertNotNull(killEvent);
    assertEquals(null, killEvent.id());
    assertEquals(null, killEvent.timestamp());
    assertEquals(null, killEvent.killedPlayer());
    assertEquals(null, killEvent.killingPlayer());
    assertEquals(null, killEvent.weapon());
    assertEquals(null, killEvent.weaponClass());
    assertEquals(null, killEvent.damageType());
    assertEquals(null, killEvent.zone());
  }
}
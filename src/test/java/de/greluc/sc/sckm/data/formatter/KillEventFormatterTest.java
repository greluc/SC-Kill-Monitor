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

package de.greluc.sc.sckm.data.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.greluc.sc.sckm.data.KillEvent;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the KillEventFormatter interface and its implementations.
 * This class verifies that the DefaultKillEventFormatter produces the expected output
 * and that the KillEventFormatterFactory correctly provides formatter instances.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
class KillEventFormatterTest {

  private KillEvent testKillEvent;

  @BeforeEach
  void setUp() {
    // Create a test KillEvent for use in all tests
    testKillEvent = new KillEvent(
        UUID.randomUUID(),
        ZonedDateTime.now(),
        "TestPlayer",
        "TestKiller",
        "TestWeapon",
        "TestWeaponClass",
        "TestDamageType",
        "TestZone"
    );
  }

  /**
   * Tests that the DefaultKillEventFormatter correctly formats a KillEvent
   * with redaction disabled.
   */
  @Test
  void defaultFormatter_formatsKillEventCorrectly_withoutRedaction() {
    // Arrange
    KillEventFormatter formatter = new DefaultKillEventFormatter();

    // Act
    String result = formatter.format(testKillEvent, false);

    // Assert
    assertNotNull(result);
    // Verify that the killer name is included
    assertEquals(true, result.contains("Killer = TestKiller"));
  }

  /**
   * Tests that the DefaultKillEventFormatter correctly formats a KillEvent
   * with redaction enabled.
   */
  @Test
  void defaultFormatter_formatsKillEventCorrectly_withRedaction() {
    // Arrange
    KillEventFormatter formatter = new DefaultKillEventFormatter();

    // Act
    String result = formatter.format(testKillEvent, true);

    // Assert
    assertNotNull(result);
    // Verify that the killer name is redacted
    assertEquals(true, result.contains("Killer = REDACTED"));
    assertEquals(false, result.contains("Killer = TestKiller"));
  }

  /**
   * Tests that the KillEventFormatterFactory correctly provides the default formatter.
   */
  @Test
  void factory_providesDefaultFormatter() {
    // Act
    KillEventFormatter formatter = KillEventFormatterFactory.getFormatter();

    // Assert
    assertNotNull(formatter);
    assertEquals(DefaultKillEventFormatter.class, formatter.getClass());
  }

  /**
   * Tests that the KillEventFormatterFactory correctly allows setting a custom formatter.
   */
  @Test
  void factory_allowsSettingCustomFormatter() {
    // Arrange
    KillEventFormatter customFormatter = new CustomTestFormatter();

    // Act
    KillEventFormatterFactory.setFormatter(customFormatter);
    KillEventFormatter retrievedFormatter = KillEventFormatterFactory.getFormatter();

    // Assert
    assertEquals(customFormatter, retrievedFormatter);

    // Clean up - reset to default for other tests
    KillEventFormatterFactory.resetToDefault();
  }

  /**
   * Tests that the KillEventFormatterFactory correctly resets to the default formatter.
   */
  @Test
  void factory_resetsToDefaultFormatter() {
    // Arrange
    KillEventFormatterFactory.setFormatter(new CustomTestFormatter());

    // Act
    KillEventFormatterFactory.resetToDefault();
    KillEventFormatter formatter = KillEventFormatterFactory.getFormatter();

    // Assert
    assertEquals(DefaultKillEventFormatter.class, formatter.getClass());
  }

  /**
   * Tests that the deprecated static KillEventFormatter.format method
   * delegates to the current formatter from the factory.
   */
  @Test
  @SuppressWarnings("deprecation") // We're intentionally testing deprecated methods
  void deprecatedStaticMethod_delegatesToCurrentFormatter() {
    // Arrange
    KillEventFormatterFactory.resetToDefault();

    // Act
    String resultFromStatic = de.greluc.sc.sckm.data.KillEventFormatter.format(testKillEvent, false);
    String resultFromFactory = KillEventFormatterFactory.getFormatter().format(testKillEvent, false);

    // Assert
    assertEquals(resultFromFactory, resultFromStatic);
  }

  /**
   * A custom formatter implementation for testing the factory's ability to use different formatters.
   */
  private static class CustomTestFormatter implements KillEventFormatter {
    @Override
    public String format(KillEvent killEvent, boolean isRedacted) {
      return "CUSTOM FORMAT";
    }
  }
}

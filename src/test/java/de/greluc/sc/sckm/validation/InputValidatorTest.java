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

package de.greluc.sc.sckm.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link InputValidator} class.
 *
 * <p>This class contains unit tests for the validation methods in the InputValidator class.
 * It tests various input scenarios to ensure that the validation logic works correctly.
 */
class InputValidatorTest {

  @Test
  void validatePathFormat_validPath_returnsValid() {
    ValidationResult result = InputValidator.validatePathFormat("C:\\valid\\path\\format.txt");
    assertTrue(result.isValid());
  }

  @Test
  void validatePathFormat_emptyPath_returnsValid() {
    ValidationResult result = InputValidator.validatePathFormat("");
    assertTrue(result.isValid());
  }

  @Test
  void validatePathFormat_invalidPath_returnsInvalid() {
    ValidationResult result = InputValidator.validatePathFormat("C:\\invalid\\path\\with\\illegal\\char\\*\\file.txt");
    assertFalse(result.isValid());
    assertTrue(result.getErrorMessage().contains("Invalid path format"));
  }

  @Test
  void validateHandle_validHandle_returnsValid() {
    assertTrue(InputValidator.validateHandle("validHandle").isValid());
    assertTrue(InputValidator.validateHandle("valid-handle").isValid());
    assertTrue(InputValidator.validateHandle("valid_handle").isValid());
    assertTrue(InputValidator.validateHandle("valid123").isValid());
    assertTrue(InputValidator.validateHandle("v1").isValid());
    assertTrue(InputValidator.validateHandle("123").isValid());
  }

  @Test
  void validateHandle_invalidHandle_returnsInvalid() {
    assertFalse(InputValidator.validateHandle("").isValid());
    assertFalse(InputValidator.validateHandle("a").isValid()); // Single character is invalid
    assertFalse(InputValidator.validateHandle("too_long_handle_that_exceeds_limit").isValid());
    assertFalse(InputValidator.validateHandle("invalid@handle").isValid());
    assertFalse(InputValidator.validateHandle("invalid handle").isValid());
    assertFalse(InputValidator.validateHandle("invalid#handle").isValid());
  }

  @Test
  void validateScanInterval_validInterval_returnsValid() {
    assertTrue(InputValidator.validateScanInterval("1").isValid());
    assertTrue(InputValidator.validateScanInterval("10").isValid());
    assertTrue(InputValidator.validateScanInterval("60").isValid());
    assertTrue(InputValidator.validateScanInterval("3600").isValid());
  }

  @Test
  void validateScanInterval_invalidInterval_returnsInvalid() {
    assertFalse(InputValidator.validateScanInterval("").isValid());
    assertFalse(InputValidator.validateScanInterval("0").isValid());
    assertFalse(InputValidator.validateScanInterval("-1").isValid());
    assertFalse(InputValidator.validateScanInterval("3601").isValid());
    assertFalse(InputValidator.validateScanInterval("not_a_number").isValid());
  }

  @Test
  void validateScanInterval_validInterval_returnsCorrectErrorMessage() {
    ValidationResult result = InputValidator.validateScanInterval("0");
    assertFalse(result.isValid());
    assertEquals("Interval must be at least 1 second(s)", result.getErrorMessage());

    result = InputValidator.validateScanInterval("3601");
    assertFalse(result.isValid());
    assertEquals("Interval cannot exceed 3600 seconds", result.getErrorMessage());

    result = InputValidator.validateScanInterval("not_a_number");
    assertFalse(result.isValid());
    assertEquals("Interval must be a valid number", result.getErrorMessage());
  }
}

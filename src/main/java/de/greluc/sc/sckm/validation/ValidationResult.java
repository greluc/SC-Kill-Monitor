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

/**
 * Represents the result of a validation operation.
 *
 * <p>This class encapsulates the outcome of validating user input, including whether the validation
 * was successful and any error message if it wasn't. It provides factory methods for creating valid
 * and invalid results.
 *
 * @author SC Kill Monitor Team
 * @version 1.6.0
 * @since 1.6.0
 */
public class ValidationResult {
  private final boolean valid;
  private final String errorMessage;

  /**
   * Private constructor to enforce the use of factory methods.
   *
   * @param valid whether the validation was successful
   * @param errorMessage the error message if validation failed, or null if successful
   */
  private ValidationResult(boolean valid, String errorMessage) {
    this.valid = valid;
    this.errorMessage = errorMessage;
  }

  /**
   * Creates a successful validation result.
   *
   * @return a ValidationResult indicating successful validation
   */
  public static ValidationResult valid() {
    return new ValidationResult(true, null);
  }

  /**
   * Creates a failed validation result with the specified error message.
   *
   * @param errorMessage the error message describing why validation failed
   * @return a ValidationResult indicating failed validation with the error message
   */
  public static ValidationResult invalid(String errorMessage) {
    return new ValidationResult(false, errorMessage);
  }

  /**
   * Checks if the validation was successful.
   *
   * @return true if validation was successful, false otherwise
   */
  public boolean isValid() {
    return valid;
  }

  /**
   * Gets the error message if validation failed.
   *
   * @return the error message, or null if validation was successful
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}

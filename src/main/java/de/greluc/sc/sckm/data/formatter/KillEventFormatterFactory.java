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

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class for creating and managing KillEventFormatter instances.
 * This class provides a centralized way to obtain the current KillEventFormatter implementation
 * and allows for runtime replacement of the formatter with custom implementations.
 *
 * <p>The factory maintains a singleton instance of the current formatter, which defaults to
 * {@link DefaultKillEventFormatter} but can be replaced with any class that implements
 * the {@link KillEventFormatter} interface.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class KillEventFormatterFactory {
  private static KillEventFormatter currentFormatter = new DefaultKillEventFormatter();

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private KillEventFormatterFactory() {
    // Private constructor to prevent instantiation
  }

  /**
   * Gets the current KillEventFormatter implementation.
   *
   * @return The current KillEventFormatter implementation.
   */
  public static @NotNull KillEventFormatter getFormatter() {
    return currentFormatter;
  }

  /**
   * Sets a new KillEventFormatter implementation as the current formatter.
   * This method allows for runtime replacement of the formatter with custom implementations.
   *
   * @param formatter The new KillEventFormatter implementation to use. Must not be null.
   */
  public static void setFormatter(@NotNull KillEventFormatter formatter) {
    log.info("Changing KillEventFormatter implementation to: {}", formatter.getClass().getName());
    currentFormatter = formatter;
  }

  /**
   * Resets the current formatter to the default implementation.
   * This method is useful for returning to the standard formatting behavior after
   * using a custom formatter.
   */
  public static void resetToDefault() {
    log.info("Resetting KillEventFormatter to default implementation");
    currentFormatter = new DefaultKillEventFormatter();
  }
}
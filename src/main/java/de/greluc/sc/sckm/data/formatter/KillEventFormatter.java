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

import de.greluc.sc.sckm.data.KillEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The KillEventFormatter interface defines the contract for formatting kill events into
 * human-readable string representations. Implementations of this interface are responsible
 * for converting {@link KillEvent} objects into formatted strings for display or logging purposes.
 *
 * <p>This interface allows for different formatting strategies to be implemented and swapped
 * at runtime, making the kill event formatting system modular and extensible.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
public interface KillEventFormatter {
  
  /**
   * Formats a {@link KillEvent} instance into a human-readable string representation.
   * The string typically includes details like the kill timestamp, killed player, zone, weapon used,
   * weapon class, and damage type. If the killer's information needs to be redacted,
   * implementations should handle this appropriately.
   *
   * @param killEvent The {@link KillEvent} instance containing the details of the kill event
   *                  to be formatted. Must not be null.
   * @param isRedacted A boolean flag indicating whether the killer's name should be redacted
   *                   in the final output. If true, the killer's name should be replaced with
   *                   an appropriate placeholder; otherwise, the actual name should be included.
   * @return A non-null, human-readable formatted string representing the kill event details
   *         with or without the killer's name redacted, based on the isRedacted flag.
   */
  @NotNull String format(@NotNull KillEvent killEvent, boolean isRedacted);
}
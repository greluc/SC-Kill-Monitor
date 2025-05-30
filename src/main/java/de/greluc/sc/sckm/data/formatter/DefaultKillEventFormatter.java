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
import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The DefaultKillEventFormatter class is the standard implementation of the KillEventFormatter
 * interface. It formats kill events into human-readable string representations with a consistent
 * layout that includes timestamp, player names, zone, weapon details, and damage type.
 *
 * <p>This implementation provides the default formatting behavior for the application, maintaining
 * the same output format as the original static utility class.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
public class DefaultKillEventFormatter implements KillEventFormatter {

  /**
   * Formats a {@link KillEvent} instance into a human-readable string representation.
   * The string includes details like the kill timestamp, killed player, zone, weapon used,
   * weapon class, and damage type. If the killer's information needs to be redacted,
   * the output will replace the killer's name with "REDACTED".
   *
   * @param killEvent The {@link KillEvent} instance containing the details of the kill event
   *                  to be formatted. Must not be null.
   * @param isRedacted A boolean flag indicating whether the killer's name should be redacted
   *                   in the final output. If true, the killer's name will be replaced with
   *                   "REDACTED"; otherwise, the actual name will be included.
   * @return A non-null, human-readable formatted string representing the kill event details
   *         with or without the killer's name redacted, based on the isRedacted flag.
   */
  @Contract(pure = true)
  @Override
  public @NotNull String format(@NotNull KillEvent killEvent, boolean isRedacted) {
    String result;
    if (isRedacted) {
      result =
          "Kill Date = "
              + killEvent.timestamp().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss:SSS"))
              + " UTC"
              + "\n"
              + "Killed Player = "
              + killEvent.killedPlayer()
              + "\n"
              + "Zone = "
              + killEvent.zone()
              + "\n"
              + "Killer = REDACTED"
              + "\n"
              + "Used Method/Weapon = "
              + killEvent.weapon()
              + "\n"
              + "Class = "
              + killEvent.weaponClass()
              + "\n"
              + "Damage Type = "
              + killEvent.damageType();
    } else {
      result =
          "Kill Date = "
              + killEvent.timestamp().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss:SSS"))
              + " UTC"
              + "\n"
              + "Killed Player = "
              + killEvent.killedPlayer()
              + "\n"
              + "Zone = "
              + killEvent.zone()
              + "\n"
              + "Killer = "
              + killEvent.killingPlayer()
              + "\n"
              + "Used Method/Weapon = "
              + killEvent.weapon()
              + "\n"
              + "Class = "
              + killEvent.weaponClass()
              + "\n"
              + "Damage Type = "
              + killEvent.damageType();
    }
    return result;
  }
}
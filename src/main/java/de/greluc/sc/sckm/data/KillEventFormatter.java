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

import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The KillEventFormatter class provides a utility method for formatting kill events into
 * human-readable string representations. This class is primarily used to format the details of a
 * {@link KillEvent} record into a structured string for display or logging purposes.
 *
 * <p>The formatted string includes the following details from the kill event:
 *
 * <ul>
 *   <li>Timestamp of the event in the format "dd.MM.yy HH:mm:ss:SSS UTC".
 *   <li>Name of the killed player.
 *   <li>Zone or location in the game where the event occurred.
 *   <li>Name of the killer (player, NPC, or other entity).
 *   <li>Weapon or method used to perform the kill.
 *   <li>Type of damage inflicted (e.g., explosive, ballistic).
 * </ul>
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.2.1
 */
public class KillEventFormatter {
  /**
   * Formats the details of a KillEvent into a human-readable string representation.
   *
   * @param killEvent the KillEvent object containing information about a specific kill event
   * @return a string representation of the KillEvent with details such as timestamp, killed player,
   *     killer, weapon used, damage type, and zone
   */
  @Contract(pure = true)
  public static @NotNull String format(@NotNull KillEvent killEvent) {
    return "Kill Date = "
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
        + killEvent.killer()
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
}

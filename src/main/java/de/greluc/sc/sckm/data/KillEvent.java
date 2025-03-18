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

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a kill event in the game, encapsulating detailed information such as the players
 * involved, weapon used, damage type, and the location where the event occurred.
 *
 * <p>The {@code KillEvent} record provides immutable data regarding a notable event where one
 * player kills another. It encapsulates all relevant details of the event to facilitate
 * processing, storage, and analysis.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id}: A unique identifier for the kill event.</li>
 *   <li>{@code timestamp}: The date and time when the event occurred.</li>
 *   <li>{@code killedPlayer}: The player who was killed in this event.</li>
 *   <li>{@code killingPlayer}: The player responsible for the kill.</li>
 *   <li>{@code weapon}: The weapon used in the kill.</li>
 *   <li>{@code weaponClass}: The classification of the weapon used.</li>
 *   <li>{@code damageType}: The type of damage inflicted to execute the kill.</li>
 *   <li>{@code zone}: The location or area within the game where the kill occurred.</li>
 * </ul>
 *
 * <p>Equality and hash code implementations are based solely on the {@code id} field, ensuring
 * that two {@code KillEvent} instances with the same identifier are considered equal.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.4.0
 * @since 1.0.0
 */
public record KillEvent(
    UUID id,
    ZonedDateTime timestamp,
    String killedPlayer,
    String killingPlayer,
    String weapon,
    String weaponClass,
    String damageType,
    String zone) {

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof KillEvent killEvent)) return false;
    return Objects.equals(id, killEvent.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

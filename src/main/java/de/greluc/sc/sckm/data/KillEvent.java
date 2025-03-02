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

/**
 * Represents an event in which a player is killed during gameplay.
 *
 * <ul>
 *   <li><strong>timestamp</strong>: The date and time when the kill event occurred.
 *   <li><strong>killedPlayer</strong>: The name of the player who was killed.
 *   <li><strong>killer</strong>: The name of the player, NPC, or entity that performed the kill.
 *   <li><strong>weapon</strong>: The weapon or method used to perform the kill.
 *   <li><strong>weaponClass</strong>: The class of the weapon or method used to perform the kill.
 *   <li><strong>damageType</strong>: The type of damage inflicted (e.g., explosive, ballistic).
 *   <li><strong>zone</strong>: The location or area in the game where the kill occurred.
 * </ul>
 *
 * <p>This record provides a detailed representation of a kill event, storing all relevant details
 * for tracking or monitoring purposes.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.3.0
 * @since 1.0.0
 */
public record KillEvent(
    ZonedDateTime timestamp,
    String killedPlayer,
    String killer,
    String weapon,
    String weaponClass,
    String damageType,
    String zone) {}

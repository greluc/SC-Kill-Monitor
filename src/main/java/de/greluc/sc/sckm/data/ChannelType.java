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

/**
 * Represents the type of a channel. The {@code ChannelType} enum is used to categorize different
 * types of communication channels or deployment channels within the system.
 *
 * <p>Enum Values:
 * <ul>
 *   <li>{@code LIVE}: Represents a live or production channel.</li>
 *   <li>{@code PTU}: Represents a Public Test Universe channel, often used for public testing of
 *       new features or updates.</li>
 *   <li>{@code EPTU}: Represents an Evocati Public Test Universe channel, typically reserved for
 *       early access testing by a select group of testers.</li>
 *   <li>{@code HOTFIX}: Represents a channel used for deploying urgent fixes to the live
 *       environment.</li>
 *   <li>{@code TECH_PREVIEW}: Represents a technical preview channel, often used for showcasing
 *       or experimenting with new technology or features.</li>
 *   <li>{@code CUSTOM}: Represents a customizable or user-defined channel type.</li>
 * </ul>
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.4.0
 * @since 1.0.0
 */
public enum ChannelType {
  LIVE,
  PTU,
  EPTU,
  HOTFIX,
  TECH_PREVIEW,
  CUSTOM
}

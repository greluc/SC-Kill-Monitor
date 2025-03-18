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

package de.greluc.sc.sckm.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UuidTest {

  @Test
  public void testUuidIsEqualForSameInput() {
    String input = "2025-02-12T14:38:24.454Z";
    UUID uuid1 = UUID.nameUUIDFromBytes(input.getBytes());
    UUID uuid2 = UUID.nameUUIDFromBytes(input.getBytes());
    UUID uuid3 = UUID.nameUUIDFromBytes(input.getBytes());

    assertEquals(uuid1, uuid2);
    assertEquals(uuid2, uuid3);
    assertEquals(uuid1, uuid3);
  }
}

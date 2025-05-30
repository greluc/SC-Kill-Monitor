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

package de.greluc.sc.sckm.logparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for the LogParserFactory.
 * 
 * <p>This class verifies that the LogParserFactory correctly provides
 * LogParser instances based on the requested type.
 *
 * @author SC Kill Monitor Team
 * @version 1.7.0
 * @since 1.7.0
 */
class LogParserFactoryTest {

  /**
   * Tests that the getDefaultParser method returns a StarCitizenLogParser.
   */
  @Test
  void getDefaultParser_returnsStarCitizenLogParser() {
    // Act
    LogParser parser = LogParserFactory.getDefaultParser();
    
    // Assert
    assertNotNull(parser);
    assertTrue(parser instanceof StarCitizenLogParser);
  }
  
  /**
   * Tests that the getParser method returns a StarCitizenLogParser when
   * ParserType.STAR_CITIZEN is specified.
   */
  @Test
  void getParser_withStarCitizenType_returnsStarCitizenLogParser() {
    // Act
    LogParser parser = LogParserFactory.getParser(LogParserFactory.ParserType.STAR_CITIZEN);
    
    // Assert
    assertNotNull(parser);
    assertTrue(parser instanceof StarCitizenLogParser);
  }
  
  /**
   * Tests that the getParser method returns the same instance for multiple calls
   * with the same parser type, verifying the singleton pattern.
   */
  @Test
  void getParser_returnsSameInstance_forMultipleCalls() {
    // Act
    LogParser parser1 = LogParserFactory.getParser(LogParserFactory.ParserType.STAR_CITIZEN);
    LogParser parser2 = LogParserFactory.getParser(LogParserFactory.ParserType.STAR_CITIZEN);
    LogParser defaultParser = LogParserFactory.getDefaultParser();
    
    // Assert
    assertNotNull(parser1);
    assertNotNull(parser2);
    assertNotNull(defaultParser);
    assertEquals(parser1, parser2);
    assertEquals(parser1, defaultParser);
  }
}
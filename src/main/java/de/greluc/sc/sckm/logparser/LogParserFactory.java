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

import de.greluc.sc.sckm.Constants;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;

/**
 * Factory class for creating instances of {@link LogParser} implementations.
 * This class follows the factory pattern to provide appropriate log parser implementations
 * based on the requested type.
 *
 * <p>Currently, the factory supports the following parser types:
 * <ul>
 *   <li>STAR_CITIZEN - The default parser for Star Citizen game logs</li>
 * </ul>
 *
 * <p>This factory allows for easy extension with additional parser implementations in the future.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.7.0
 * @since 1.7.0
 */
@Log4j2
public class LogParserFactory {

  /**
   * Enum defining the supported log parser types.
   */
  public enum ParserType {
    /** The default parser for Star Citizen game logs */
    STAR_CITIZEN
  }

  // Singleton instance of the StarCitizenLogParser for better resource management
  private static final LogParser STAR_CITIZEN_PARSER = new StarCitizenLogParser();

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private LogParserFactory() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Creates and returns an instance of a {@link LogParser} based on the specified type.
   * 
   * @param type The type of log parser to create
   * @return An instance of the requested log parser
   */
  public static LogParser getParser(ParserType type) {
    log.debug("Getting log parser of type: {}", type);
    
    return switch (type) {
      case STAR_CITIZEN -> STAR_CITIZEN_PARSER;
      default -> {
        log.warn("Unknown parser type: {}. Using default StarCitizenLogParser.", type);
        yield STAR_CITIZEN_PARSER;
      }
    };
  }

  /**
   * Returns the default log parser (StarCitizenLogParser).
   * 
   * @return The default log parser instance
   */
  public static LogParser getDefaultParser() {
    log.debug("Getting default log parser");
    return STAR_CITIZEN_PARSER;
  }
}
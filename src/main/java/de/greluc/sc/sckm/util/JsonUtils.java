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

package de.greluc.sc.sckm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.greluc.sc.sckm.Constants;
import lombok.Generated;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for JSON operations using Jackson ObjectMapper.
 * 
 * <p>This class provides methods for serializing objects to JSON and deserializing JSON to objects
 * using a pre-configured ObjectMapper instance. The ObjectMapper is configured to handle Java 8
 * date/time types and to produce indented output.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
@Log4j2
public class JsonUtils {

  // Static ObjectMapper for better memory efficiency
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  }

  // Static ObjectMapper configured for lenient deserialization
  private static final ObjectMapper LENIENT_OBJECT_MAPPER;

  static {
    LENIENT_OBJECT_MAPPER = new ObjectMapper();
    LENIENT_OBJECT_MAPPER.registerModule(new JavaTimeModule());
    LENIENT_OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    LENIENT_OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    LENIENT_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /** Used to exclude the unused constructor from code coverage evaluation. */
  @Generated
  private JsonUtils() {
    throw new IllegalStateException(Constants.UTILITY_CLASS);
  }

  /**
   * Returns the pre-configured ObjectMapper instance.
   *
   * @return the pre-configured ObjectMapper instance
   */
  public static @NotNull ObjectMapper getObjectMapper() {
    return OBJECT_MAPPER;
  }

  /**
   * Returns a pre-configured ObjectMapper instance that is lenient during deserialization.
   * This mapper is configured to not fail on unknown properties.
   *
   * @return the pre-configured lenient ObjectMapper instance
   */
  public static @NotNull ObjectMapper getLenientObjectMapper() {
    return LENIENT_OBJECT_MAPPER;
  }

  /**
   * Serializes an object to a JSON string.
   *
   * @param object the object to serialize
   * @return the JSON string representation of the object
   * @throws JsonProcessingException if an error occurs during serialization
   */
  public static @NotNull String toJson(@NotNull Object object) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(object);
  }

  /**
   * Deserializes a JSON string to an object of the specified class.
   *
   * @param <T> the type of the object to deserialize to
   * @param json the JSON string to deserialize
   * @param valueType the class of the object to deserialize to
   * @return the deserialized object
   * @throws JsonProcessingException if an error occurs during deserialization
   */
  public static <T> @NotNull T fromJson(@NotNull String json, @NotNull Class<T> valueType) 
      throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(json, valueType);
  }

  /**
   * Deserializes a JSON string to an object of the specified class using a lenient ObjectMapper.
   * This method is useful when the JSON may contain properties not defined in the target class.
   *
   * @param <T> the type of the object to deserialize to
   * @param json the JSON string to deserialize
   * @param valueType the class of the object to deserialize to
   * @return the deserialized object
   * @throws JsonProcessingException if an error occurs during deserialization
   */
  public static <T> @NotNull T fromJsonLenient(@NotNull String json, @NotNull Class<T> valueType) 
      throws JsonProcessingException {
    return LENIENT_OBJECT_MAPPER.readValue(json, valueType);
  }
}
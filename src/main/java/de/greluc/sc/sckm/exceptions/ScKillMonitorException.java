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

package de.greluc.sc.sckm.exceptions;

/**
 * Base exception class for all application-specific exceptions in the SC Kill Monitor application.
 * This class serves as the parent for all custom exceptions in the application, providing a
 * consistent exception hierarchy and enabling centralized exception handling.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
public class ScKillMonitorException extends Exception {

    /**
     * Constructs a new ScKillMonitorException with the specified detail message.
     *
     * @param message the detail message
     */
    public ScKillMonitorException(String message) {
        super(message);
    }

    /**
     * Constructs a new ScKillMonitorException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ScKillMonitorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ScKillMonitorException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ScKillMonitorException(Throwable cause) {
        super(cause);
    }
}
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
 * Base exception class for update-related errors in the SC Kill Monitor application.
 * This exception is thrown when there are issues with checking for, downloading, or installing updates.
 *
 * @author Lucas Greuloch (greluc, lucas.greuloch@protonmail.com)
 * @version 1.6.0
 * @since 1.6.0
 */
public class UpdateException extends ScKillMonitorException {

    /**
     * Constructs a new UpdateException with the specified detail message.
     *
     * @param message the detail message
     */
    public UpdateException(String message) {
        super(message);
    }

    /**
     * Constructs a new UpdateException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UpdateException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public UpdateException(Throwable cause) {
        super(cause);
    }
}

/*
 * Copyright 2009 Salomo Petrus
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package nl.tranquilizedquality.itest.cargo.exception;

/**
 * Is used when something went wrong during configuration of the container.
 * 
 * @author Salomo Petrus (sape)
 * @since 23 apr 2009
 * 
 */
public class ConfigurationException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2810338479901139278L;

    /**
     * Default constructor.
     */
    public ConfigurationException() {
    }

    /**
     * Constructor where you can specify the error message.
     * 
     * @param msg The error message that will be used.
     */
    public ConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Constructor where you can specify the error message and the cause of this
     * exception.
     * 
     * @param msg The error message that will be used.
     * @param cause The {@link Throwable} that caused this exception.
     */
    public ConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

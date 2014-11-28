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
 * Use this exception when something goes wrong during deploying of your
 * application.
 * 
 * @author Salomo Petrus (sape)
 * @since 1 apr 2009
 * 
 */
public class DeployException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 6530739596346697399L;

    /**
     * Default constructor.
     */
    public DeployException() {
    }

    /**
     * Constructor where you can specify the error message.
     * 
     * @param msg
     *            The error message that will be used.
     */
    public DeployException(String msg) {
        super(msg);
    }

    /**
     * Constructor where you can specify the error message and the cause of this
     * exception.
     * 
     * @param msg
     *            The error message that will be used.
     * @param cause
     *            The {@link Throwable} that caused this exception.
     */
    public DeployException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

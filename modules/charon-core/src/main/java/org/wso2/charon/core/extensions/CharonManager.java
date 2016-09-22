/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.extensions;

import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.exceptions.UnauthorizedException;

import java.util.Map;

/**
 * This interface needs to be implemented by any SCIM service provider implementation.
 * Default implementation is at charon-impl.  This is responsible for reading charon - configurations
 * for reading extensions such as user manager, custom encoders, decoders etc.
 * And the static instance should be initialized at the initialization of the SCIM service provider
 * application or when the first request is received at the service provider.
 */
public interface CharonManager {

    /**
     * Obtain the encoder for the given format.
     *
     * @return
     */
    public JSONEncoder getEncoder(String format) throws FormatNotSupportedException;

    /**
     * Obtain the decoder for the given format.
     *
     * @return
     */
    public JSONDecoder getDecoder(String format) throws FormatNotSupportedException;

    /**
     * Obtain the authentication handler, given the authentication mechanism.
     *
     * @return
     */

    public UserManager getUserManager(String tenantAdminUserName) throws CharonException;

    /**
     * Obtain the the instance of registered tenant manager implementation.
     *
     * @return
     */

}

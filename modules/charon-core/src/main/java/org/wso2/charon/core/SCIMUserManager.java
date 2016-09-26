/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon.core;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.User;

public class SCIMUserManager implements UserManager {

    public static final String USER_NAME_STRING = "userName";
    public static final String SCIM_ENABLED =  "SCIMEnabled";
    public static final String APPLICATION_DOMAIN = "Application";
    public static final String INTERNAL_DOMAIN = "Internal";
    private static Log log = LogFactory.getLog(SCIMUserManager.class);

    private String consumerName;
    private boolean isBulkUserAdd = false;

    public SCIMUserManager( ) {}

    public User createUser(User user) throws CharonException {
        return createUser(user, false);
    }

    public User createUser(User user, boolean isBulkUserAdd) throws CharonException {
        return user;
    }
   }


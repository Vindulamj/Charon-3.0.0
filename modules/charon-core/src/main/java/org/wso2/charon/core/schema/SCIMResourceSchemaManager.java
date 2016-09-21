/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon.core.schema;

/*
This is to check for extension schema for the user and build a custom user schema with it. Unless a extension is defined,
core-user schema need to be returned.
 */

public class SCIMResourceSchemaManager {
	
	private static SCIMResourceSchemaManager manager = new SCIMResourceSchemaManager();
	
	public static SCIMResourceSchemaManager getInstance() {
		return manager;
	}
	
	private SCIMResourceSchemaManager() {
		
	}

	/**
	 * Return the SCIM User Resource Schema
	 * @return
	 */
	public SCIMResourceTypeSchema getUserResourceSchema() {

		//TODO:check for the extension schema in the config file and construct a custom schema with them included
		// returning the core schema
		return SCIMSchemaDefinitions.SCIM_USER_SCHEMA;
	}

}

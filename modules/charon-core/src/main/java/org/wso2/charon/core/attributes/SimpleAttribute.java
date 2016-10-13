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
package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMDefinitions;
/*
 * This class is a blueprint of SimpleAttribute defined in SCIM Core Schema Spec.
 */
public class SimpleAttribute extends AbstractAttribute {

    //In a simple attribute, only one attribute value is present.
    private Object value;

    public SimpleAttribute(String attributeName, Object value) {
        this.name = attributeName;
        this.value = value;
    }

    public Object getValue() { return value; }

    public void setValue(Object value) {
        this.value = value;
    }

    public Attribute getSubAttribute(String attributeName) throws CharonException {
        throw new CharonException("getSubAttribute method not supported by SimpleAttribute.");
    }

    @Override
    public void deleteSubAttributes() throws CharonException {
        throw new CharonException("deleteSubAttributes method not supported by SimpleAttribute.");
    }

    public String getStringValue() throws CharonException {
        if (this.type.equals(SCIMDefinitions.DataType.STRING)) {
            return (String) value;
        } else {
            throw new CharonException("Mismatch in requested data type");
        }
    }
}

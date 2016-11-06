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
package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.schema.SCIMConstants;

/**
 * Represents the listed resource object subjected to pagination
 *
 **/

public class PaginatedListedResource extends ListedResource {
    //number of query results returned in a query response page
    protected int itemsPerPage;
    //The 1-based index of the first result in the
    //current set of query results
    protected int startIndex;

    public int getItemsPerPage() { return itemsPerPage; }

    public void setItemsPerPage(int itemsPerPage) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE, itemsPerPage);
            //No need to let the Default attribute factory to handle the attribute, as this is
            //not officially defined as SCIM attribute, hence have no charactersitics defined
            //TODO: may be we can let the default attribute factory to handle it?
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE))
                    .setValue(itemsPerPage);
        }
    }

    public int getStartIndex() { return startIndex; }

    public void setStartIndex(int startIndex) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.START_INDEX)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourceSchemaConstants.START_INDEX, startIndex);
            //No need to let the Default attribute factory to handle the attribute, as this is
            //not officially defined as SCIM attribute, hence have no charactersitics defined
            //TODO: may be we can let the default attribute factory to handle it?
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.START_INDEX, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.START_INDEX))
                    .setValue(startIndex);
        }
    }
}

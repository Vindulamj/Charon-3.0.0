package org.wso2.charon.core.v2.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the interface for resource schema definition. Default schema definitions included in
 * SCIMResourceTypeSchema.
 * Any SCIM implementation can implement this/extend SCIMSchema to introduce schema extensions.
 */
public interface ResourceTypeSchema {

    public List<String> getSchemasList();

    public void setSchemasList(String schema);

    public ArrayList<AttributeSchema> getAttributesList();

    public void setAttributeList(ArrayList attributeList);

}

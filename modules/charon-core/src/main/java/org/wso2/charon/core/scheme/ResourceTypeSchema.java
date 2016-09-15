package org.wso2.charon.core.scheme;

import java.util.ArrayList;

/**
 * This is the interface for resource schema definition. Default schema definitions included in
 * SCIMResourceTypeSchema.
 * Any SCIM implementation can implement this/extend SCIMSchema to introduce schema extensions.
 */
public interface ResourceTypeSchema {

    public String getSchemas();

    public void setSchemas(String schemas);

    public ArrayList<AttributeSchema> getAttributes();

    public void setAttributes(ArrayList attributes);

}

package org.wso2.charon.core.scheme;

import java.util.ArrayList;
import java.util.List;

/**
 * This declares the SCIM resources schema as specified in SCIM spec.
 */

public class SCIMResourceTypeSchema implements ResourceTypeSchema {

    //The core schema for the resource type is identified using the following schema URI
    //e.g.: for 'User' - urn:ietf:params:scim:schemas:core:2.0:User
    private String schemas;

    private List<AttributeSchema> attributes = new ArrayList<AttributeSchema>();

    private SCIMResourceTypeSchema(String schemas, AttributeSchema[] attributeSchemas) {
        this.schemas=schemas;
        if (attributeSchemas != null) {
            for (AttributeSchema attributeSchema : attributeSchemas) {
                this.attributes.add(attributeSchema);
            }
        }
    }

    public static SCIMResourceTypeSchema createSCIMResourceSchema(String schemas,
                                                              AttributeSchema... attributeSchemas) {
        return new SCIMResourceTypeSchema(schemas,attributeSchemas);

    }


    public String getSchemas() { return null; }

    public void setSchemas(String schemas) { this.schemas=schemas; }

    public ArrayList<AttributeSchema> getAttributes() {
        return null;
    }

    public void setAttributes(ArrayList attributes) { this.attributes=attributes; }
}

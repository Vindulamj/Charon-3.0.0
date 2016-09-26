package org.wso2.charon.core.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * This declares the SCIM resources schema as specified in SCIM core specification 2.0.
 */

public class SCIMResourceTypeSchema implements ResourceTypeSchema {

    //The core schema for the resource type is identified using the following schemas URIs
    //e.g.: for 'User' - urn:ietf:params:scim:schemasList:core:2.0:User
    private List<String> schemasList;
    //set of attributeList in the schema
    private ArrayList<AttributeSchema> attributeList = new ArrayList<AttributeSchema>();

    private SCIMResourceTypeSchema(List<String> schemas, AttributeSchema[] attributeSchemas) {
        this.schemasList =schemas;
        if (attributeSchemas != null) {
            for (AttributeSchema attributeSchema : attributeSchemas) {
                this.attributeList.add(attributeSchema);
            }
        }
    }
    /**
     * Create a SCIMResourceTypeSchema according to the schema id and set of attributeList
     *
     * @param schemas - json encoded string of user info
     * @param attributeSchemas - SCIM defined user schema
     * @return SCIMResourceTypeSchema
     */
    public static SCIMResourceTypeSchema createSCIMResourceSchema(List<String> schemas,
                                                              AttributeSchema... attributeSchemas) {
        return new SCIMResourceTypeSchema(schemas,attributeSchemas);

    }

    public List<String> getSchemasList() { return schemasList; }

    public void setSchemasList(String schema) { this.schemasList.add(schema); }

    public ArrayList<AttributeSchema> getAttributesList() { return attributeList; }

    public void setAttributeList(ArrayList attributeList) { this.attributeList = attributeList; }
}

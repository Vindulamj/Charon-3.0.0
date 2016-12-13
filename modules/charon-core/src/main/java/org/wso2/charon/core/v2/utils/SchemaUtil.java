package org.wso2.charon.core.v2.utils;

import org.wso2.charon.core.v2.schema.*;

import java.util.List;

/**
 * Attribute schema related utils can be found here.
 */
public class SchemaUtil {

    /**
     * return the attribute schema for the asked attribute URI
     * @param attributeFullName
     * @param scimObjectType
     * @return
     */
    public static AttributeSchema getAttributeSchema(String attributeFullName, SCIMResourceTypeSchema scimObjectType) {

        ResourceTypeSchema resourceSchema = scimObjectType;

        if (resourceSchema != null) {
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();
            for (AttributeSchema attributeSchema : attributeSchemas) {
                if (attributeFullName.equals(attributeSchema.getName())) {
                    return attributeSchema;
                }
                if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    if (attributeSchema.getMultiValued()) {
                        List<SCIMAttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (attributeFullName.equals(attributeSchema.getName() + "." + subAttributeSchema.getName())) {
                                return subAttributeSchema;
                            }
                        }
                    } else {
                        List<SCIMAttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (attributeFullName.equals(attributeSchema.getName() + "." + subAttributeSchema.getName())) {
                                return subAttributeSchema;
                            }
                            if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                // this is only valid for extension schema
                                List<SCIMAttributeSchema> subSubAttributeSchemaList = subAttributeSchema.getSubAttributeSchemas();
                                for (AttributeSchema subSubAttributeSchema : subSubAttributeSchemaList) {
                                    if (attributeFullName.equals(attributeSchema.getName() + "." +
                                            subAttributeSchema.getName() + "." + subSubAttributeSchema.getName())) {
                                        return subSubAttributeSchema;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return  null;
    }

}

package org.wso2.charon.core.schema;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.attributes.AbstractAttribute;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.protocol.endpoints.UserResourceManager;

import java.util.List;
import java.util.Map;

public abstract class AbstractValidator {

    private static Log logger= LogFactory.getLog(AbstractValidator.class);

    /**
     * Validate SCIMObject for required attributes given the object and the corresponding schema.
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSCIMObjectForRequiredAttributes(AbstractSCIMObject scimObject,
                                                               ResourceTypeSchema resourceSchema)
            throws BadRequestException, CharonException {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for required attributes.
            if (attributeSchema.getRequired()) {
                if (!attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Required attribute " + attributeSchema.getName() + " is missing in the SCIM Object.";
                    throw new BadRequestException(error);
                }
            }
            //check for required sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            if (attribute != null) {
                List<SCIMAttributeSchema> subAttributesSchemaList =
                        ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

                if (subAttributesSchemaList != null) {
                    for (SCIMAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                        if (subAttributeSchema.getRequired()) {

                            if (attribute instanceof ComplexAttribute) {
                                if (attribute.getSubAttribute(subAttributeSchema.getName()) == null) {
                                    String error = "Required sub attribute: " + subAttributeSchema.getName()
                                            + " is missing in the SCIM Attribute: " + attribute.getName();
                                    throw new BadRequestException(error);
                                }
                            } else if (attribute instanceof MultiValuedAttribute) {
                                List<Attribute> values =
                                        ((MultiValuedAttribute) attribute).getAttributeValues();
                                for (Attribute value : values) {
                                    if (value instanceof ComplexAttribute) {
                                        if (value.getSubAttribute(subAttributeSchema.getName()) == null) {
                                            String error = "Required sub attribute: " + subAttributeSchema.getName()
                                                    + " is missing in the SCIM Attribute: " + attribute.getName();
                                            throw new BadRequestException(error);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
    /**
     * Validate SCIMObject for schema list
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSchemaList(AbstractSCIMObject scimObject,
                                          SCIMResourceTypeSchema resourceSchema) throws CharonException {
        //get resource schema list
        List<String> resourceSchemaList = resourceSchema.getSchemasList();
        //get the scim object schema list
        List<String> objectSchemaList = scimObject.getSchemaList();
        for (String schema : resourceSchemaList) {
            //check for schema.
            if (!objectSchemaList.contains(schema)) {
               throw new CharonException("Not all schemas are set");
            }
        }
    }

    /**
     *Check for readonlyAttributes and remove them if they have been modified.
     *
     * @param scimObject
     * @param resourceSchema
     * @throws CharonException
     */
    public static void removeAnyReadOnlyAttributes(AbstractSCIMObject scimObject,
                                                    SCIMResourceTypeSchema resourceSchema) throws CharonException {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for read-only attributes.
            if (attributeSchema.getMutability()==SCIMDefinitions.Mutability.READ_ONLY) {
                if (attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    scimObject.deleteAttribute(attributeSchema.getName());
                }
            }
            //check for readonly sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            if (attribute != null) {
                List<SCIMAttributeSchema> subAttributesSchemaList =
                        ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

                if (subAttributesSchemaList != null && !subAttributesSchemaList.isEmpty()) {
                    for (SCIMAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                        if (subAttributeSchema.getMutability()==SCIMDefinitions.Mutability.READ_ONLY) {
                            if (attribute instanceof ComplexAttribute) {
                                if (attribute.getSubAttribute(subAttributeSchema.getName()) != null) {
                                    String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                            + " is set in the SCIM Attribute: " + attribute.getName() +
                                            ". Removing it.";
                                    ((ComplexAttribute) attribute).removeSubAttribute(subAttributeSchema.getName());
                                }
                            } else if (attribute instanceof MultiValuedAttribute) {
                                List<Attribute> values =
                                        ((MultiValuedAttribute) attribute).getAttributeValues();
                                for (Attribute value : values) {
                                    if (value instanceof ComplexAttribute) {
                                        if (value.getSubAttribute(subAttributeSchema.getName()) != null) {
                                            String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                    + " is set in the SCIM Attribute: " + attribute.getName() +
                                                    ". Removing it.";
                                            ((ComplexAttribute) value).removeSubAttribute(subAttributeSchema.getName());

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

    }

}
package org.wso2.charon.core.attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.protocol.endpoints.UserResourceEndpoint;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.SCIMAttributeSchema;

public class DefaultAttributeFactory {

    public static Attribute createAttribute(AttributeSchema attributeSchema,
                                            AbstractAttribute attribute) throws CharonException {

        attribute.setMutability(attributeSchema.getMutability());
        attribute.setRequired(attributeSchema.getRequired());
        attribute.setReturned(attributeSchema.getReturned());
        attribute.setCaseExact(attributeSchema.getCaseExact());
        attribute.setMultiValued(attributeSchema.getMultiValued());
        attribute.setDescription(attributeSchema.getDescription());
        attribute.setUniqueness(attributeSchema.getUniqueness());
        attribute.setType(attributeSchema.getType());

        //Default attribute factory knows about SCIMAttribute schema
        try{
            //set data type of the attribute value, if simple attribute
            if (attribute instanceof SimpleAttribute) {
                return createSimpleAttribute(attributeSchema, (SimpleAttribute) attribute);
            }
            if (attribute instanceof MultiValuedAttribute) {
                return createMultiValuedAttribute(attributeSchema, (MultiValuedAttribute) attribute);
            }
            //validate the created attribute against the schema.
            return attribute;
        }
        catch(CharonException e){
            String error = "Unknown attribute schema.";
            throw new CharonException(error);
        }

    }

    /**
     * Once identified that constructing attribute is a simple attribute & related attribute schema is a
     * SCIMAttributeSchema, perform attribute construction operations specific to Simple Attribute.
     *
     * @param attributeSchema
     * @param simpleAttribute
     * @return
     */
    protected static SimpleAttribute createSimpleAttribute(AttributeSchema attributeSchema,
                                                           SimpleAttribute simpleAttribute)
            throws CharonException {
        /*simpleAttribute.setAttributeURI(attributeSchema.getURI());
        if (simpleAttribute.getValue() != null) {
            if (isAttributeDataTypeValid(simpleAttribute.getValue(), attributeSchema.getType())) {

                simpleAttribute.dataType = attributeSchema.getType();
                return simpleAttribute;
            } else {
                String error = "Violation in attribute shcema. DataType doesn't match that of the value.";
                throw new CharonException(error);
            }
        } else {
            return simpleAttribute;
        }*/
        return null;
    }

    /**
     * Once identified that constructing attribute as a multivalued attribute, perform specific operations
     * in creating a multi valued attribute. Such as canonicalization, and validating primary is not
     * repeated etc.
     *
     * @param attributeSchema
     * @param multiValuedAttribute
     * @return
     * @throws CharonException
     */
    protected static MultiValuedAttribute createMultiValuedAttribute(
            AttributeSchema attributeSchema, MultiValuedAttribute multiValuedAttribute)
            throws CharonException {
     /*   multiValuedAttribute.setAttributeURI(attributeSchema.getURI());
        return validateMultiValuedAttribute(attributeSchema, multiValuedAttribute); */
     return null;
    }
}

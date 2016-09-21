package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.SCIMDefinitions;


import java.net.URL;
import java.util.Date;

public class DefaultAttributeFactory {

    public static Attribute createAttribute(AttributeSchema attributeSchema,
                                            AbstractAttribute attribute) throws CharonException, BadRequestException {

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
        catch(BadRequestException e){
            String error = "Violation in attribute schema. DataType doesn't match that of the value.";
            throw new BadRequestException(error);
        }
    }

    /*
     * Once identified that constructing attribute is a simple attribute & related attribute schema is a
     * SCIMAttributeSchema, perform attribute construction operations specific to Simple Attribute.
     *
     * @param attributeSchema
     * @param simpleAttribute
     * @return
     */
    protected static SimpleAttribute createSimpleAttribute(AttributeSchema attributeSchema,
                                                           SimpleAttribute simpleAttribute) throws CharonException, BadRequestException {
        if (simpleAttribute.getValue() != null) {
            if (isAttributeDataTypeValid(simpleAttribute.getValue(), attributeSchema.getType())) {

                simpleAttribute.setType(attributeSchema.getType());
                return simpleAttribute;
            }
        }
        return simpleAttribute;
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
            AttributeSchema attributeSchema, MultiValuedAttribute multiValuedAttribute) throws CharonException {
        return multiValuedAttribute;
    }

    /**
     * When an attribute is created with value and data type provided, we need to validate whether
     * they are matching.
     *
     * @param attributeValue
     * @param attributeDataType
     * @return
     * @throws BadRequestException
     */
    protected static boolean isAttributeDataTypeValid(Object attributeValue,
                                                      SCIMDefinitions.DataType attributeDataType)
            throws BadRequestException {
        switch (attributeDataType) {
            case STRING:
                return attributeValue instanceof String;
            case BOOLEAN:
                return attributeValue instanceof Boolean;
            case DECIMAL:
                return attributeValue instanceof Double;
            case INTEGER:
                return attributeValue instanceof Integer;
            case DATE_TIME:
                return attributeValue instanceof Date;
            case BINARY:
                return attributeValue instanceof Byte[];
            case REFERENCE:
                return attributeValue instanceof URL;
            case COMPLEX:
                return attributeValue instanceof String;

        }
        throw new BadRequestException(ResponseCodeConstants.INVALID_VALUE);
    }

}

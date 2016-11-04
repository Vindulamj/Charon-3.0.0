package org.wso2.charon.core.v2.utils;

import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;
import org.wso2.charon.core.v2.schema.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/*
 * This class acts as an utility class for attributes
 */
public class AttributeUtil {

    /**
     * Convert the raw string to SCIM defined data type accordingly
     *
     * @param attributeValue
     * @param dataType
     * @return Object
     */
    public static Object getAttributeValueFromString(Object attributeValue,
                                                     SCIMDefinitions.DataType dataType)
            throws CharonException, BadRequestException {
        if(attributeValue ==null){
            return attributeValue;
        }
        String attributeStringValue = null;
        if(!(attributeValue instanceof Boolean)){
            if(attributeValue instanceof  Integer){
                attributeStringValue = String.valueOf(attributeValue);
            }
            else if (attributeValue instanceof Double){
                attributeStringValue = String.valueOf(attributeValue);
            }
            else{
                attributeStringValue= (String) attributeValue;
            }
        }
        try {
            switch (dataType) {
                case STRING:
                    return attributeStringValue.trim();
                case BOOLEAN:
                    return parseBoolean(attributeValue);
                case DECIMAL:
                    return Double.parseDouble(attributeStringValue);
                case INTEGER:
                    return Integer.parseInt(attributeStringValue);
                case DATE_TIME:
                    return parseDateTime(attributeStringValue);
                case BINARY:
                    return new Byte(attributeStringValue);
                case REFERENCE:
                    return parseReference(attributeStringValue);
                case COMPLEX:
                    return parseComplex(attributeStringValue);
            }
        }
        catch(Exception e){
            throw new CharonException("Error in converting string value to attribute type: " + dataType);
        }
        return null;
    }

    public static String getStringValueOfAttribute(Object attributeValue,
                                                   SCIMDefinitions.DataType dataType)
            throws CharonException {
        switch (dataType) {
            case STRING:
                return String.valueOf(attributeValue);
            case BOOLEAN:
                return String.valueOf(attributeValue);
            case DECIMAL:
                return String.valueOf(attributeValue);
            case INTEGER:
                return String.valueOf(attributeValue);
            case DATE_TIME:
                return formatDateTime((Date) attributeValue);
            case BINARY:
                return String.valueOf(attributeValue);
            case REFERENCE:
                return String.valueOf(attributeValue);
            case COMPLEX:
                return String.valueOf(attributeValue);
        }
        throw new CharonException("Error in converting attribute value of type: " + dataType + " to string.");
    }
    /**
     * SCIM spec requires date time to be in yyyy-MM-dd'T'HH:mm:ss
     *
     * @param dateTimeString
     */
    public static Date parseDateTime(String dateTimeString) throws CharonException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);
            return sdf.parse(dateTimeString);
        } catch (ParseException e) {
            throw new CharonException("Error in parsing date time. " +
                    "Date time should adhere to the format: "+SCIMConstants.dateTimeFormat);
        }
    }

    public static String parseReference(String referenceString) throws CharonException{
        //TODO: Need a better way for doing this. Think of the way to handle reference types
        return referenceString;
        /*try{
            URI uri =new URI(referenceString);
            uri.normalize();
            return uri;
        } catch (URISyntaxException e) {
            throw new CharonException("Error in normalization of the URI");
        }*/
    }
    //this method is for the consistency purpose only
    public static String parseComplex(String complexString){
        return complexString;
    }

    /**
     * SCIM spec requires date time to be adhered to XML Schema Datatypes Specification
     *
     * @param date
     */
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /**
     * Converts the value to bolean or throw an exception
     *
     * @param booleanValue
     */
    public static Boolean parseBoolean(Object booleanValue) throws BadRequestException {
        if(booleanValue instanceof Boolean){
            return ((Boolean) booleanValue).booleanValue();
        }
        else{
            throw new BadRequestException(ResponseCodeConstants.INVALID_VALUE);
        }
    }

    /**
     * Will iterate through <code>{@code SCIMAttributeSchema}</code> objects
     *
     *
     * @param attributeName
     * @return
     */
    public static String getAttributeURI(String attributeName, SCIMResourceTypeSchema schema) throws BadRequestException {

        Iterator<AttributeSchema> attributeSchemas = schema.getAttributesList().iterator();
        while (attributeSchemas.hasNext()) {
            AttributeSchema attributeSchema = attributeSchemas.next();

            if (attributeSchema.getName().equals(attributeName) || attributeSchema.getURI().equals(attributeName)) {
                return attributeSchema.getURI();
            }
            // check in sub attributes
            String subAttributeURI =
                    checkSCIMSubAttributeURIs(((SCIMAttributeSchema) attributeSchema).getSubAttributeSchemas(),
                            attributeSchema, attributeName);
            if (subAttributeURI != null) {
                return subAttributeURI;
            }
        }
        String error = "Not a valid attribute name/URI";
        throw new BadRequestException(error,ResponseCodeConstants.INVALID_VALUE);
    }

    /**
     * Will iterate through <code>{@code SCIMSubAttributeSchema}</code> objects
     * @param subAttributes
     * @param attributeSchema
     *@param attributeName  @return
     */
    private static String checkSCIMSubAttributeURIs(List<SCIMAttributeSchema> subAttributes,
                                                    AttributeSchema attributeSchema, String attributeName) {
        if (subAttributes != null) {
            Iterator<SCIMAttributeSchema> subsIterator = subAttributes.iterator();

            while(subsIterator.hasNext()) {
                SCIMAttributeSchema subAttributeSchema = subsIterator.next();
                if((attributeSchema.getName()+"."+subAttributeSchema.getName()).equals(attributeName) ||
                        subAttributeSchema.getURI().equals(attributeName)) {
                    return subAttributeSchema.getURI();
                }
                if(subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)){
                    List<SCIMAttributeSchema> subSubAttributeSchemas = subAttributeSchema.getSubAttributeSchemas();
                    if (subSubAttributeSchemas != null) {
                        Iterator<SCIMAttributeSchema> subSubsIterator = subSubAttributeSchemas.iterator();

                        while(subSubsIterator.hasNext()) {
                            SCIMAttributeSchema subSubAttributeSchema = subSubsIterator.next();
                            if((attributeSchema.getName()+"."+subAttributeSchema.getName()+"."+
                                    subSubAttributeSchema.getName()).equals(attributeName) ||
                                    subAttributeSchema.getURI().equals(attributeName)) {
                                return subSubAttributeSchema.getURI();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}

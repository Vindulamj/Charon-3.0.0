package org.wso2.charon.core.utils;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMDefinitions;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AttributeUtil {

    public static Object getAttributeValueFromString(String attributeStringValue,
                                                     SCIMDefinitions.DataType dataType)
            throws CharonException {
        switch (dataType) {
            case STRING:
                return attributeStringValue.trim();
            case BOOLEAN:
                return Boolean.parseBoolean(attributeStringValue);
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
        throw new CharonException("Error in converting string value to attribute type: " + dataType);

    }

    public static Date parseDateTime(String dateTimeString) throws CharonException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return sdf.parse(dateTimeString);
        } catch (ParseException e) {
            throw new CharonException("Error in parsing date time. " +
                    "Date time should adhere to the format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    public static URI parseReference(String referenceString) throws CharonException{
        try{
            URI uri =new URI(referenceString);
            uri.normalize();
            return uri;
        } catch (URISyntaxException e) {
            throw new CharonException("Error in normalization of the URI");
        }
    }

    public static String parseComplex(String complexString){
        //TODO: check for sub complex attribute availability
        return complexString;
    }

    /**
     * SCIM spec requires date time to be adhered to XML Schema Datatypes Specification
     *
     * @param date
     */
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }



}

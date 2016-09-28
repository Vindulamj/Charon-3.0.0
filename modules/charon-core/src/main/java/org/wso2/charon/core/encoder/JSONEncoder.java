package org.wso2.charon.core.encoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMDefinitions;
import org.wso2.charon.core.utils.AttributeUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This encodes the data
 */

public class JSONEncoder {

    private String format;
    private Log logger = LogFactory.getLog(JSONEncoder.class);

    public JSONEncoder() {
        format = SCIMConstants.JSON;
    }

    public String getFormat() {
        return format;
    }


    public String encodeSCIMObject(SCIMObject scimObject) throws CharonException {
        //root json object containing the encoded SCIM Object.
        JSONObject rootObject = new JSONObject();
        rootObject = this.getSCIMObjectAsJSONObject(scimObject);
        return rootObject.toString();
    }


    public String encodeSCIMException(AbstractCharonException exception) {
        //outer most json object
        JSONObject rootErrorObject = new JSONObject();
        //JSON Object containing the error code and error message
        JSONObject errorObject = new JSONObject();

        try {
            //construct error object with details in the exception
            errorObject.put(ResponseCodeConstants.SCHEMAS, exception.getSchemas());
            if(!exception.getScimType().equals(null)){
                errorObject.put(ResponseCodeConstants.SCIM_TYPE, exception.getScimType());
            }
            errorObject.put(ResponseCodeConstants.DETAIL, String.valueOf(exception.getDetail()));
            errorObject.put(ResponseCodeConstants.STATUS, String.valueOf(exception.getStatus()));
            //construct the full json obj.
            rootErrorObject =errorObject;

        } catch (JSONException e) {
            //usually errors occur rarely when encoding exceptions. and no need to pass them to clients.
            //sufficient to log them in server side back end.
            logger.error("SCIMException encoding error");
        }
        return rootErrorObject.toString();
    }
    /**
     * Make JSON object from given SCIM object.
     *
     * @param scimObject
     * @return the resulting string after encoding.
     */
    public JSONObject getSCIMObjectAsJSONObject(SCIMObject scimObject) throws CharonException {
        //root json object containing the encoded SCIM Object.
        JSONObject rootObject = new JSONObject();
        try {
            //encode schemas
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.SCHEMAS,
                    (scimObject.getSchemaList()).toArray(), rootObject);
            //encode attribute list
            Map<String, Attribute> attributes = scimObject.getAttributeList();
            if (attributes != null && !attributes.isEmpty()) {
                for (Attribute attribute : attributes.values()) {
                    //using instanceof instead of polymorphic way, in order to make encoder pluggable.
                    if (attribute instanceof SimpleAttribute) {
                        encodeSimpleAttribute((SimpleAttribute) attribute, rootObject);

                    } else if (attribute instanceof ComplexAttribute) {
                        encodeComplexAttribute((ComplexAttribute) attribute, rootObject);

                    } else if (attribute instanceof MultiValuedAttribute) {
                        encodeMultiValuedAttribute((MultiValuedAttribute) attribute, rootObject);
                    }
                }
            }

        } catch (JSONException e) {
            String errorMessage = "Error in encoding resource..";
            //TODO:log the error
            throw new CharonException(errorMessage);
        }
        return rootObject;
    }


    public void encodeArrayOfValues(String arrayName, Object[] arrayValues,
                                    JSONObject rootObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object arrayValue : arrayValues) {
            jsonArray.put(arrayValue);
        }
        rootObject.put(arrayName, jsonArray);
    }

    /**
     * Encode the simple attribute and include it in root json object to be returned.
     *
     * @param attribute
     * @param rootObject
     */
    public void encodeSimpleAttribute(SimpleAttribute attribute, JSONObject rootObject)
            throws JSONException {
        if (attribute.getValue() != null) {
            //if type is DateTime, convert before encoding.
            if (attribute.getType() != null && attribute.getType() == SCIMDefinitions.DataType.DATE_TIME) {
                rootObject.put(attribute.getName(),
                        AttributeUtil.formatDateTime((Date) attribute.getValue()));
                return;
            }
            rootObject.put(attribute.getName(), attribute.getValue());
        }
    }

    /**
     * Encode the complex attribute and include it in root json object to be returned.
     *
     * @param complexAttribute
     * @param rootObject
     */
    public void encodeComplexAttribute(ComplexAttribute complexAttribute, JSONObject rootObject)
            throws JSONException {
        JSONObject subObject = new JSONObject();
        Map<String, Attribute> attributes = complexAttribute.getSubAttributesList();
        for (Attribute attributeValue : attributes.values()) {
            //using instanceof instead of polymorphic way, in order to make encoder pluggable.
            if (attributeValue instanceof SimpleAttribute) {
                //most of the time, this if condition is hit according to current SCIM spec.
                encodeSimpleAttribute((SimpleAttribute) attributeValue, subObject);

            } else if (attributeValue instanceof MultiValuedAttribute) {
                encodeMultiValuedAttribute((MultiValuedAttribute) attributeValue, subObject);
            }
            rootObject.put(complexAttribute.getName(), subObject);
        }

    }


    /**
     * When an attribute value (of a complex or multivalued attribute) becomes a simple attribute itself,
     * encode it and put it in json array.
     *
     * @param attributeValue
     * @param jsonArray
     * @throws JSONException
     */
    protected void encodeSimpleAttributeValue(SimpleAttribute attributeValue, JSONArray jsonArray)
            throws JSONException {
        if (attributeValue.getValue() != null) {
            JSONObject attributeValueObject = new JSONObject();
            //if type is DateTime, convert before encoding.
            if (attributeValue.getType() != null &&
                    attributeValue.getType() == SCIMDefinitions.DataType.DATE_TIME) {
                attributeValueObject.put(attributeValue.getName(),
                        AttributeUtil.formatDateTime((Date) attributeValue.getValue()));
                return;
            }
            attributeValueObject.put(attributeValue.getName(), attributeValue.getValue());
            jsonArray.put(attributeValueObject);
        }
    }

    /**
     * Encode the simple attribute and include it in root json object to be returned.
     *
     * @param multiValuedAttribute
     * @param jsonObject
     */
    public void encodeMultiValuedAttribute(MultiValuedAttribute multiValuedAttribute,
                                           JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        //TODO:what if values are set as list of string values.For the moment it is ok, since only schemas
        //attribute has such values and we handle it separately in encoding.
        //List<String> stringAttributeValues = multiValuedAttribute.getStringAttributeValues();
        List<Attribute> attributeValues = multiValuedAttribute.getAttributeValues();
        //if values are strings,
        if (attributeValues != null && !attributeValues.isEmpty()) {
            for (Attribute attributeValue : attributeValues) {
                if (attributeValue instanceof SimpleAttribute) {
                    encodeSimpleAttributeValue((SimpleAttribute) attributeValue, jsonArray);

                } else if (attributeValue instanceof ComplexAttribute) {

                    encodeComplexAttributeValue((ComplexAttribute) attributeValue, jsonArray);
                }
            }
        }
        jsonObject.put(multiValuedAttribute.getName(), jsonArray);
    }

    /**
     * When an attribute value (of a multivalued attribute) becomes a complex attribute,
     * use this method to encode it.
     *
     * @param attributeValue
     * @param jsonArray
     */
    protected void encodeComplexAttributeValue(ComplexAttribute attributeValue,
                                               JSONArray jsonArray) throws JSONException {
        JSONObject subObject = new JSONObject();
        Map<String, Attribute> subAttributes = attributeValue.getSubAttributesList();
        for (Attribute value : subAttributes.values()) {
            //using instanceof instead of polymorphic way, in order to make encoder pluggable.
            if (value instanceof SimpleAttribute) {
                encodeSimpleAttribute((SimpleAttribute) value, subObject);

            } else if (value instanceof ComplexAttribute) {
                encodeComplexAttribute((ComplexAttribute) value, subObject);

            } else if (value instanceof MultiValuedAttribute) {
                encodeMultiValuedAttribute((MultiValuedAttribute) value, subObject);
            }
        }
        jsonArray.put(subObject);
    }

}





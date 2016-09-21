package org.wso2.charon.core.encoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.ResourceTypeSchema;
import org.wso2.charon.core.utils.AttributeUtil;

import java.util.List;


public class JSONDecoder{

    private Log logger;

    public JSONDecoder() {
        logger = LogFactory.getLog(JSONDecoder.class);
    }

    /**
     * Decode the resource string sent in the SCIM request/response payload.
     *
     * @param scimResourceString
     * @param resourceSchema
     * @param scimObject
     * @return SCIMObject
     */
    public SCIMObject decodeResource(String scimResourceString,
                                     ResourceTypeSchema resourceSchema, AbstractSCIMObject scimObject)
            throws BadRequestException, CharonException {
        try {
            //decode the string into json representation
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            //get the attribute schemas list from the schema that defines the given resource
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();

            //iterate through the schema and extract the attributes.
            for (AttributeSchema attributeSchema : attributeSchemas) {

                Object attributeValObj = decodedJsonObj.opt(attributeSchema.getName());

                if (attributeValObj instanceof String) {
                    //If an attribute is passed without a value, no need to save it.
                    if(((String) attributeValObj).isEmpty()){
                        continue;
                    }
                    //if the corresponding json value object is String, it is a SimpleAttribute.
                    scimObject.setAttribute(buildSimpleAttribute(attributeSchema, attributeValObj));

                } else if(attributeValObj instanceof Integer) {
                    //if the corresponding json value object is integer, it is a SimpleAttribute.
                    scimObject.setAttribute(buildSimpleAttribute(attributeSchema, Integer.toString((Integer)attributeValObj)));

                } else if (attributeValObj instanceof Boolean) {
                    //if the corresponding json value object is boolean, it is a SimpleAttribute.
                    scimObject.setAttribute(buildSimpleAttribute(attributeSchema,
                            String.valueOf(attributeValObj)));

                } else if (attributeValObj instanceof JSONArray) {
                    //if the corresponding json value object is JSONArray, it is a MultiValuedAttribute.
                    scimObject.setAttribute(
                            buildMultiValuedAttribute(attributeSchema, (JSONArray) attributeValObj));

                } else if (attributeValObj instanceof JSONObject) {
                    //if the corresponding json value object is JSONObject, it is a ComplexAttribute.
                    scimObject.setAttribute(buildComplexAttribute(attributeSchema,
                            (JSONObject) attributeValObj));
                }
            }
            return scimObject;
        }
        catch (JSONException e) {
            logger.error("json error in decoding the resource");
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        } catch (CharonException e) {
            logger.error("Error in building resource from the JSON representation");
            String error = "Error in building resource from the JSON representation";
            throw new CharonException(error);
    }


    }

    private Attribute buildMultiValuedAttribute(AttributeSchema attributeSchema, JSONArray attributeValObj) {
        return null;
    }

    private Attribute buildComplexAttribute(AttributeSchema attributeSchema, JSONObject attributeValObj) {
        return null;
    }


    private SimpleAttribute buildSimpleAttribute(AttributeSchema attributeSchema,
                                                 Object attributeValue) throws CharonException {
        Object attributeValueObject = AttributeUtil.getAttributeValueFromString(
                (String) attributeValue, attributeSchema.getType());
        SimpleAttribute simpleAttribute = new SimpleAttribute(attributeSchema.getName(), attributeValueObject);
        return (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                simpleAttribute);
    }

}


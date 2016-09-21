package org.wso2.charon.core.encoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.charon.core.attributes.*;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.ResourceTypeSchema;
import org.wso2.charon.core.schema.SCIMAttributeSchema;
import org.wso2.charon.core.utils.AttributeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private MultiValuedAttribute buildMultiValuedAttribute(AttributeSchema attributeSchema, JSONArray attributeValues)
            throws CharonException, BadRequestException {
            try {
                MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
                List<String> simpleAttributeValues = new ArrayList<String>();
                List<Attribute> complexAttributeValues = new ArrayList<Attribute>();

                //iterate through JSONArray and create the list of string values.
                for (int i = 0; i < attributeValues.length(); i++) {
                    Object attributeValue = attributeValues.get(i);

                    if (attributeValue instanceof String) {
                        if(((String) attributeValue).isEmpty()){
                            continue;
                        }
                        simpleAttributeValues.add((String) attributeValues.get(i));
                    } else if (attributeValue instanceof JSONObject) {
                        JSONObject complexAttributeValue = (JSONObject) attributeValue;
                        complexAttributeValues.add(buildComplexValue(attributeSchema, complexAttributeValue));
                    } else {
                        String error = "Unknown JSON representation for the MultiValued attribute Value.";
                        throw new CharonException(error);
                    }

                }
                multiValuedAttribute.setStringAttributeValues(simpleAttributeValues);
                multiValuedAttribute.setAttributeValues(complexAttributeValues);

                return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                        multiValuedAttribute);
            } catch (JSONException e) {
                String error = "Error in accessing JSON value of multivalues attribute";
                throw new CharonException(error);
            }
    }

    private Attribute buildComplexAttribute(AttributeSchema complexAttributeSchema, JSONObject jsonObject) throws BadRequestException, CharonException {

        ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeSchema.getName());
        Map<String, Attribute> attributesMap = new HashMap<String, Attribute>();

        // If complex attribute has only sub attributes
        if (((SCIMAttributeSchema) complexAttributeSchema).getSubAttributes() != null) {
            List<SCIMAttributeSchema> subAttributeSchemas =
                    ((SCIMAttributeSchema) complexAttributeSchema).getSubAttributes();

            for (SCIMAttributeSchema subAttributeSchema : subAttributeSchemas) {
                Object subAttributeValue = jsonObject.opt(subAttributeSchema.getName());
                if (subAttributeValue instanceof Integer) {
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(subAttributeSchema, String.valueOf(subAttributeValue));
                    attributesMap.put(subAttributeSchema.getName(), simpleAttribute);
                }
                if (subAttributeValue instanceof Boolean) {
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(subAttributeSchema, String.valueOf(subAttributeValue));
                    attributesMap.put(subAttributeSchema.getName(), simpleAttribute);
                }
                if (subAttributeValue instanceof String) {
                    if(((String) subAttributeValue).isEmpty()){
                        continue;
                    }
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(subAttributeSchema,
                                    subAttributeValue);
                    attributesMap.put(subAttributeSchema.getName(), simpleAttribute);
                }
                else if (subAttributeValue instanceof JSONArray) {
                    // there can be sub attributes which are multivalued: such as: Meta->attributes
                    MultiValuedAttribute multivaluedAttribute =
                            buildMultiValuedAttribute(subAttributeSchema,
                                    (JSONArray) subAttributeValue);

                    attributesMap.put(subAttributeSchema.getName(), multivaluedAttribute);
                }
            }
            complexAttribute.setSubAttributes(attributesMap);

            // if complex attribute has only attributes
        }
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(complexAttributeSchema, complexAttribute);
    }


    private SimpleAttribute buildSimpleAttribute(AttributeSchema attributeSchema,
                                                 Object attributeValue) throws CharonException, BadRequestException {
        Object attributeValueObject = AttributeUtil.getAttributeValueFromString(
                (String) attributeValue, attributeSchema.getType());
        SimpleAttribute simpleAttribute = new SimpleAttribute(attributeSchema.getName(), attributeValueObject);
        return (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                simpleAttribute);
    }

    /**
     * To build a complex type value of a Multi Valued Attribute.
     *
     * @param attributeSchema
     * @param jsonObject
     * @return
     */
    private ComplexAttribute buildComplexValue(AttributeSchema attributeSchema,
                                               JSONObject jsonObject) throws CharonException, BadRequestException {
        ComplexAttribute complexAttribute = new ComplexAttribute(attributeSchema.getName());
        Map<String, Attribute> subAttributesMap = new HashMap<String, Attribute>();
        List<SCIMAttributeSchema> subAttributeSchemas =
                ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

        for (SCIMAttributeSchema subAttributeSchema : subAttributeSchemas) {

            Object subAttributeValue = jsonObject.opt(subAttributeSchema.getName());
            if (subAttributeValue instanceof String) {
                SimpleAttribute simpleAttribute =
                        buildSimpleAttribute(subAttributeSchema, subAttributeValue);
                //let the attribute factory to set the sub attribute of a complex attribute to detect schema violations.
                //DefaultAttributeFactory.setSubAttribute(complexAttribute, simpleAttribute);
                subAttributesMap.put(subAttributeSchema.getName(), simpleAttribute);
            } else if (subAttributeValue instanceof JSONArray) {
                //there can be sub attributes which are multivalued: such as: Meta->attributes
                MultiValuedAttribute multivaluedAttribute =
                        buildMultiValuedAttribute(subAttributeSchema, (JSONArray) subAttributeValue);
                subAttributesMap.put(subAttributeSchema.getName(), multivaluedAttribute);
            }
        }
        complexAttribute.setSubAttributes(subAttributesMap);
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                complexAttribute);
    }

}


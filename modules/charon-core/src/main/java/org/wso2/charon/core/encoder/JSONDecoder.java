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
import org.wso2.charon.core.schema.*;
import org.wso2.charon.core.utils.AttributeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon.core.schema.SCIMDefinitions.DataType.*;

/**
 * This decodes the json encoded resource string and create a SCIM object model accodring to the specification
 * according to the info that the user has sent, and returns SCIMUser object
 */

public class JSONDecoder{

    private Log logger;

    public JSONDecoder() {
        logger = LogFactory.getLog(JSONDecoder.class);
    }

    /**
     * Decode the resource string sent in the SCIM request payload.
     *
     * @param scimResourceString - json encoded string of user info
     * @param resourceSchema - SCIM defined user schema
     * @param scimObject  - a container holding the attributes and schema list
     * @return SCIMObject
     */
    public SCIMObject decodeResource(String scimResourceString, ResourceTypeSchema resourceSchema,
                                     AbstractSCIMObject scimObject) throws BadRequestException, CharonException {
        try {
            //decode the string into json representation
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            //get the attribute schemas list from the schema that defines the given resource
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();
            //get the values for corresponding to schemas key
            JSONArray schemasValues = (JSONArray) decodedJsonObj.opt(SCIMConstants.CommonSchemaConstants.SCHEMAS);
            //set the schemas in scimobject
            for (int i = 0; i < resourceSchema.getSchemasList().size(); i++) {
                scimObject.setSchema(resourceSchema.getSchemasList().get(i));
            }
            //iterate through the schema and extract the attributes.
            for (AttributeSchema attributeSchema : attributeSchemas) {
                //obtain the user defined value for given key- attribute schema name
                Object attributeValObj = decodedJsonObj.opt(attributeSchema.getName());
                SCIMDefinitions.DataType attributeSchemaDataType =attributeSchema.getType();

                if(attributeSchemaDataType.equals(STRING) || attributeSchemaDataType.equals(BINARY) ||
                        attributeSchemaDataType.equals(BOOLEAN)|| attributeSchemaDataType.equals(DATE_TIME)||
                        attributeSchemaDataType.equals(DECIMAL)|| attributeSchemaDataType.equals(INTEGER)||
                        attributeSchemaDataType.equals(REFERENCE)) {
                    if(attributeValObj instanceof String || attributeValObj instanceof Boolean ||
                            attributeValObj instanceof  Integer || attributeValObj==null){
                        //If an attribute is passed without a value, no need to save it.
                        if (attributeValObj==null) {
                            continue;
                        }
                        //if the corresponding schema data type is String/Boolean/Binary/Decimal/Integer/DataTime
                        // or Reference, it is a SimpleAttribute.
                        scimObject.setAttribute(buildSimpleAttribute(attributeSchema, attributeValObj), resourceSchema);

                    }
                    else{
                        logger.error("Error decoding the simple attribute");
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                }

                if (attributeSchemaDataType.equals(COMPLEX)) {
                    if(attributeSchema.getMultiValued()==true) {
                        if (attributeValObj instanceof JSONArray || attributeValObj==null) {
                            if (attributeValObj==null) {
                                continue;
                            }
                            //if the corresponding json value object is JSONArray, it is a MultiValuedAttribute.
                            scimObject.setAttribute(buildMultiValuedAttribute(attributeSchema,
                                            (JSONArray) attributeValObj), resourceSchema);
                        }
                        else{
                            logger.error("Error decoding the multivalued attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                    else if(attributeSchema.getMultiValued()==false){
                        if (attributeValObj instanceof JSONObject || attributeValObj==null) {
                            if (attributeValObj==null) {
                                continue;
                            }
                            //if the corresponding json value object is JSONObject, it is a ComplexAttribute.
                            scimObject.setAttribute(buildComplexAttribute(attributeSchema,
                                    (JSONObject) attributeValObj), resourceSchema);
                        }
                        else{
                            logger.error("Error decoding the complex attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                }
            }
            return scimObject;
        }
        catch (JSONException e) {
            logger.error("json error in decoding the resource");
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    /**
     * Return a simple attribute with the user defined value included and necessary attribute characteristics set
     *
     * @param attributeSchema - Attribute schema
     * @param attributeValue - value for the attribute
     * @return SimpleAttribute
     */
    private SimpleAttribute buildSimpleAttribute(AttributeSchema attributeSchema,
                                                 Object attributeValue) throws CharonException, BadRequestException {
        Object attributeValueObject = AttributeUtil.getAttributeValueFromString(
                 attributeValue, attributeSchema.getType());
        SimpleAttribute simpleAttribute = new SimpleAttribute(attributeSchema.getName(), attributeValueObject);
        return (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                simpleAttribute);
    }

    /**
     * Return a multi valued attribute with the user defined value included and necessary attribute characteristics set
     *
     * @param attributeSchema - Attribute schema
     * @param attributeValues - values for the attribute
     * @return MultiValuedAttribute
     */
    private MultiValuedAttribute buildMultiValuedAttribute(AttributeSchema attributeSchema, JSONArray attributeValues)
            throws CharonException, BadRequestException {
            try {
                MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
                List<Attribute> complexAttributeValues = new ArrayList<Attribute>();

                //iterate through JSONArray and create the list of string values.
                for (int i = 0; i < attributeValues.length(); i++) {
                    Object attributeValue = attributeValues.get(i);

                    if (attributeValue instanceof JSONObject) {
                        JSONObject complexAttributeValue = (JSONObject) attributeValue;
                        complexAttributeValues.add(buildComplexValue(attributeSchema, complexAttributeValue));
                    } else {
                        String error = "Unknown JSON representation for the MultiValued attribute Value.";
                        throw new BadRequestException(error);
                    }

                }
                multiValuedAttribute.setAttributeValues(complexAttributeValues);

                return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                        multiValuedAttribute);
            } catch (JSONException e) {
                String error = "Error in accessing JSON value of multivalued attribute";
                throw new CharonException(error);
            }
    }

    /**
     * Return a complex attribute with the user defined sub values included and necessary attribute characteristics set
     *
     * @param complexAttributeSchema - complex attribute schema
     * @param jsonObject - sub attributes values for the complex attribute
     * @return ComplexAttribute
     */
    private ComplexAttribute buildComplexAttribute(AttributeSchema complexAttributeSchema,
                                            JSONObject jsonObject)
            throws BadRequestException, CharonException {

        ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeSchema.getName());
        Map<String, Attribute> subAttributesMap = new HashMap<String, Attribute>();
        //list of sub attributes of the complex attribute
        List<SCIMAttributeSchema> subAttributeSchemas =
                ((SCIMAttributeSchema) complexAttributeSchema).getSubAttributes();

        //iterate through the complex attribute schema and extract the sub attributes.
        for (AttributeSchema subAttributeSchema : subAttributeSchemas) {

            //obtain the user defined value for given key- attribute schema name
            Object attributeValObj = jsonObject.opt(subAttributeSchema.getName());
            SCIMDefinitions.DataType subAttributeSchemaType =subAttributeSchema.getType();

            if(subAttributeSchemaType.equals(STRING) || subAttributeSchemaType.equals(BINARY) ||
                    subAttributeSchemaType.equals(BOOLEAN)|| subAttributeSchemaType.equals(DATE_TIME)||
                    subAttributeSchemaType.equals(DECIMAL)|| subAttributeSchemaType.equals(INTEGER)||
                    subAttributeSchemaType.equals(REFERENCE)) {
                if(attributeValObj instanceof String || attributeValObj instanceof Boolean ||
                        attributeValObj instanceof  Integer || attributeValObj==null){
                    //If an attribute is passed without a value, no need to save it.
                    if (attributeValObj==null) {
                        continue;
                    }
                    //if the corresponding schema data type is String/Boolean/Binary/Decimal/Integer/DataTime
                    // or Reference, it is a SimpleAttribute.
                    subAttributesMap.put(subAttributeSchema.getName(),
                            buildSimpleAttribute(subAttributeSchema, attributeValObj));
                }
                else{
                    logger.error("Error decoding the simple attribute");
                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                }
            }

            if (subAttributeSchemaType.equals(COMPLEX)) {
                if(subAttributeSchema.getMultiValued()==true) {
                    if (attributeValObj instanceof JSONArray || attributeValObj==null) {
                        if (attributeValObj==null) {
                            continue;
                        }
                        //if the corresponding json value object is JSONArray, it is a MultiValuedAttribute.
                        subAttributesMap.put(subAttributeSchema.getName(),buildMultiValuedAttribute(subAttributeSchema,
                                (JSONArray) attributeValObj));
                    }
                    else{
                        logger.error("Error decoding the multivalued attribute");
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                }
                else if(subAttributeSchema.getMultiValued()==false){
                    if (attributeValObj instanceof JSONObject ) {
                        logger.error("Complex attribute cannot have complex attributes as sub attributes.");
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                    }
                }
            }
            complexAttribute.setSubAttributesList(subAttributesMap);
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(complexAttributeSchema, complexAttribute);
    }


    /**
     * To build a complex type value of a Multi Valued Attribute. (eg. Email with value,type,primary as sub attributes
     *
     * @param attributeSchema
     * @param jsonObject
     * @return ComplexAttribute
     */
    private ComplexAttribute buildComplexValue(AttributeSchema attributeSchema,
                                               JSONObject jsonObject) throws CharonException, BadRequestException {
        ComplexAttribute complexAttribute = new ComplexAttribute(attributeSchema.getName());
        Map<String, Attribute> subAttributesMap = new HashMap<String, Attribute>();
        List<SCIMAttributeSchema> subAttributeSchemas =
                ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

        for (SCIMAttributeSchema subAttributeSchema : subAttributeSchemas) {

            Object subAttributeValue = jsonObject.opt(subAttributeSchema.getName());
                SimpleAttribute simpleAttribute =
                        buildSimpleAttribute(subAttributeSchema, subAttributeValue);
                //let the attribute factory to set the sub attribute of a complex attribute to detect schema violations.
                simpleAttribute=(SimpleAttribute) DefaultAttributeFactory.createAttribute(subAttributeSchema,
                        simpleAttribute);
                subAttributesMap.put(subAttributeSchema.getName(), simpleAttribute);

        }
        complexAttribute.setSubAttributesList(subAttributesMap);
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                complexAttribute);
    }


}


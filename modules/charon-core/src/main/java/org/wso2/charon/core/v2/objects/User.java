package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.ComplexAttribute;
import org.wso2.charon.core.v2.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.v2.attributes.MultiValuedAttribute;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.schema.*;

import java.awt.*;
import java.util.*;

/**
 * Represents the User object which is a collection of attributes defined by SCIM User-schema.
 */
public class User extends AbstractSCIMObject {

    public String getUserName() throws CharonException {
        return this.getSimpleAttributeStringVal(SCIMConstants.UserSchemaConstants.USER_NAME);
    }

    public void setUserName(String userName) throws CharonException, BadRequestException {
        this.setSimpleAttribute(SCIMConstants.UserSchemaConstants.USER_NAME,
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME, userName);
    }

    public String getPassword() throws CharonException {
        return this.getSimpleAttributeStringVal(SCIMConstants.UserSchemaConstants.PASSWORD);
    }

    private void setSimpleAttribute(String attributeName, AttributeSchema attributeSchema, Object value)
            throws CharonException, BadRequestException {
        if (this.isAttributeExist(attributeName)) {
            ((SimpleAttribute) this.attributeList.get(attributeName)).updateValue(value);
        } else {
            SimpleAttribute simpleAttribute = new SimpleAttribute(attributeName, value);
            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema, simpleAttribute);
            this.attributeList.put(attributeName, simpleAttribute);
        }

    }

    private String getSimpleAttributeStringVal(String attributeName) throws CharonException {
        return this.isAttributeExist(attributeName) ?
                ((SimpleAttribute) this.attributeList.get(attributeName)).getStringValue() : null;
    }


    public void setGroup(String type, String value, String display) throws CharonException, BadRequestException {
        SimpleAttribute typeSimpleAttribute = null;
        SimpleAttribute valueSimpleAttribute = null;
        SimpleAttribute displaySimpleAttribute = null;
        ComplexAttribute complexAttribute = new ComplexAttribute();
        if (type != null) {
            typeSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE, type);
            typeSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_TYPE, typeSimpleAttribute);
            complexAttribute.setSubAttribute(typeSimpleAttribute);
        }

        if (value != null) {
            valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, value);
            valueSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (display != null) {
            displaySimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, display);
            displaySimpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_DISPLAY, displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }
        if (complexAttribute.getSubAttributesList().size() != 0) {
            Object typeVal = SCIMConstants.DEFAULT;
            Object valueVal = SCIMConstants.DEFAULT;
            if (typeSimpleAttribute != null && typeSimpleAttribute.getValue() != null) {
                typeVal = typeSimpleAttribute.getValue();
            }
            if (valueSimpleAttribute != null && valueSimpleAttribute.getValue() != null) {
                valueVal = valueSimpleAttribute.getValue();
            }
            String complexAttributeName = SCIMConstants.UserSchemaConstants.GROUPS + "_" + valueVal + "_" + typeVal;
            complexAttribute.setName(complexAttributeName);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, complexAttribute);
            setGroup(complexAttribute);
        }
    }

    private void setGroup(ComplexAttribute groupPropertiesAttribute) throws CharonException, BadRequestException {
        MultiValuedAttribute groupsAttribute;

        if(this.attributeList.containsKey(SCIMConstants.UserSchemaConstants.GROUPS)) {
            groupsAttribute = (MultiValuedAttribute)this.attributeList.get(SCIMConstants.UserSchemaConstants.GROUPS);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
        } else {
            groupsAttribute = new MultiValuedAttribute(SCIMConstants.UserSchemaConstants.GROUPS);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
            groupsAttribute = (MultiValuedAttribute)
                    DefaultAttributeFactory.createAttribute(
                            SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, groupsAttribute);
            this.attributeList.put(SCIMConstants.UserSchemaConstants.GROUPS, groupsAttribute);
        }

    }

    public void setSchemas(){
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        java.util.List<String> schemasList = schema.getSchemasList();
        for(String scheme : schemasList){
            setSchema(scheme);
        }
    }

}

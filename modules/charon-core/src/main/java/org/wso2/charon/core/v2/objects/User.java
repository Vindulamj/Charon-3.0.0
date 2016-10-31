package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.ComplexAttribute;
import org.wso2.charon.core.v2.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.v2.attributes.MultiValuedAttribute;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.schema.AttributeSchema;
import org.wso2.charon.core.v2.schema.SCIMConstants;
import org.wso2.charon.core.v2.schema.SCIMDefinitions;
import org.wso2.charon.core.v2.schema.SCIMSchemaDefinitions;

import java.util.HashMap;
import java.util.Map;

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
        if(this.isAttributeExist(attributeName)) {
            ((SimpleAttribute)this.attributeList.get(attributeName)).updateValue(value);
        } else {
            SimpleAttribute simpleAttribute = new SimpleAttribute(attributeName, value);
            simpleAttribute = (SimpleAttribute)DefaultAttributeFactory.createAttribute(attributeSchema, simpleAttribute);
            this.attributeList.put(attributeName, simpleAttribute);
        }

    }

    private String getSimpleAttributeStringVal(String attributeName) throws CharonException {
        return this.isAttributeExist(attributeName)?
                ((SimpleAttribute)this.attributeList.get(attributeName)).getStringValue():null;
    }

}

package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.ComplexAttribute;
import org.wso2.charon.core.v2.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.schema.SCIMConstants;
import org.wso2.charon.core.v2.schema.SCIMDefinitions;
import org.wso2.charon.core.v2.schema.SCIMSchemaDefinitions;

import java.util.Date;

/**
 * Represents the Group object which is a collection of attributes defined by SCIM Group-schema.
 */
public class Group extends AbstractSCIMObject {

    public String getDisplayName() throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            return ((SimpleAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).getStringValue();
        } else {
            return null;
        }
    }

    public void setDisplayName(String displayName) throws CharonException, BadRequestException {
        if(this.isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            ((SimpleAttribute)this.attributeList.get(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).
                    updateValue(displayName);
        } else {
            SimpleAttribute displayAttribute = new SimpleAttribute(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, displayName);
            displayAttribute = (SimpleAttribute)DefaultAttributeFactory.createAttribute
                    (SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.DISPLAY_NAME, displayAttribute);
            this.attributeList.put(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, displayAttribute);
        }

    }
}

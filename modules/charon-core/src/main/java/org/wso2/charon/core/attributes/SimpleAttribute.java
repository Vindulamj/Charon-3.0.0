package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMDefinitions;
/*
 * This class is a blueprint of SimpleAttribute defined in SCIM Core Schema Spec.
 */
public class SimpleAttribute extends AbstractAttribute {

    //In a simple attribute, only one attribute value is present.
    private Object value;

    public SimpleAttribute(String attributeName, Object value) {
        this.name = attributeName;
        this.value = value;
    }

    public Object getValue() { return value; }

    public void setValue(Object value) {
        this.value = value;
    }

    public Attribute getSubAttribute(String attributeName) throws CharonException {
        throw new CharonException("getSubAttribute method not supported by SimpleAttribute.");
    }

    @Override
    public void deleteSubAttributes() throws CharonException {
        throw new CharonException("deleteSubAttributes method not supported by SimpleAttribute.");
    }

    public String getStringValue() throws CharonException {
        if (this.type.equals(SCIMDefinitions.DataType.STRING)) {
            return (String) value;
        } else {
            throw new CharonException("Mismatch in requested data type");
        }
    }
}

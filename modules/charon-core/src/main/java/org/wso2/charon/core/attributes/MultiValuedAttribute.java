package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;

import java.util.ArrayList;
import java.util.List;
/*
 * This class is a blueprint of MultiValuedAttribute defined in SCIM Core Schema Spec.
 */
public class MultiValuedAttribute extends AbstractAttribute{

    //Multi valued attributes can have VALUES as an array of complex or simple attributes.
    protected List<Attribute> attributeValues = new ArrayList<Attribute>();

    public MultiValuedAttribute(String attributeName, List<Attribute> attributeValues) {
        this.name = attributeName;
        this.attributeValues = attributeValues;
    }

    public MultiValuedAttribute(String attributeName) {
        this.name=attributeName;
    }

    public List<Attribute> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<Attribute> attributeValues) {
        this.attributeValues = attributeValues;
    }

    @Override
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        throw new CharonException("getSubAttribute method not supported by MultiValuedAttribute.");
    }


}

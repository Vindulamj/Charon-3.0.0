package org.wso2.charon.core.attributes;


import java.util.ArrayList;
import java.util.List;

public class MultiValuedAttribute extends AbstractAttribute{

    //array of string values for a multi-valued attribute
    protected List<String> stringAttributeValues;

    //Multi valued attributes can also have VALUES as an array of complex or simple attributes.
    protected List<Attribute> attributeValues = new ArrayList<Attribute>();

    public MultiValuedAttribute(String attributeName, List<Attribute> attributeValues) {
        this.name = attributeName;
        this.attributeValues = attributeValues;
    }

    public MultiValuedAttribute(String attributeName) {
        this.name=attributeName;
    }

    public List<String> getStringAttributeValues() {
        return stringAttributeValues;
    }

    public void setStringAttributeValues(List<String> stringAttributeValues) {
        this.stringAttributeValues = stringAttributeValues;
    }

    public List<Attribute> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<Attribute> attributeValues) {
        this.attributeValues = attributeValues;
    }

}

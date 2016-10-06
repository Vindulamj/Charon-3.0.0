package org.wso2.charon.core.attributes;

import org.w3c.dom.Attr;
import org.wso2.charon.core.exceptions.CharonException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * This class is a blueprint of MultiValuedAttribute defined in SCIM Core Schema Spec.
 */
public class MultiValuedAttribute extends AbstractAttribute{

    //Multi valued attributes can have VALUES as an array of complex or simple attributes.
    protected List<Attribute> attributeValues = new ArrayList<Attribute>();

    //Multi valued attributes can have VALUES as an array of primitive values.
    protected List<Object> attributePrimitiveValues = new ArrayList<Object>();

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

    @Override
    public void deleteSubAttributes() throws CharonException {
        //here we delete the complex type sub attributes which act as sub values
        attributeValues.clear();;
    }

    /**
     * To construct and set a value of a multi-valued attribute, as a complex value containing
     * set of sub attributes.
     */
    public void setComplexValueWithSetOfSubAttributes(Map<String, Attribute> subAttributes) {
        ComplexAttribute complexValue = new ComplexAttribute();
        complexValue.setSubAttributesList(subAttributes);
        this.attributeValues.add(complexValue);
    }

    public List<Object> getAttributePrimitiveValues() {
        return attributePrimitiveValues;
    }

    public void setAttributePrimitiveValues(List<Object> attributePrimitiveValues) {
        this.attributePrimitiveValues = attributePrimitiveValues;
    }

    public void setAttributeValue(Attribute attributeValue){
        attributeValues.add(attributeValue);
    }

}

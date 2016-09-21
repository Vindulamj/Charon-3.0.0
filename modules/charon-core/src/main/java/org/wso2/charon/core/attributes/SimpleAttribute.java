package org.wso2.charon.core.attributes;

public class SimpleAttribute extends AbstractAttribute {

    /*In a simple attribute, only one attribute value is present.*/
    private Object value;

    public SimpleAttribute(String attributeName, Object value) {

        this.name = attributeName;
        this.value = value;
    }

}

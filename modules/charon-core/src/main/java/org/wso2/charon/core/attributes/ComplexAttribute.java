package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vindula on 9/21/16.
 */
public class ComplexAttribute extends AbstractAttribute{

    /*If it is a complex attribute, has a list of sub attributes.*/
    protected Map<String, Attribute> subAttributes = new HashMap<String, Attribute>();

    public ComplexAttribute(String name) {
       this.name=name;
    }

    /**
     * Retrieve the map of sub attributes.
     *
     * @return
     */
    public Map<String, Attribute> getSubAttributes() {
            return subAttributes;
        }

    /**
     * Set the map of sub attributes.
     *
     * @param subAttributes
     */
    public void setSubAttributes(Map<String, Attribute> subAttributes) {
        this.subAttributes = subAttributes;
    }

    /**
     * Retrieve one attribute given the attribute name.
     *
     * @param attributeName
     * @return
     */
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        if (subAttributes.containsKey(attributeName)) {
            return subAttributes.get(attributeName);
        } else {
            return null;
        }
    }

    /**
     * Remove a sub attribute from the complex attribute given the sub attribute name.
     *
     * @param attributeName
     */
    public void removeSubAttribute(String attributeName) {
        if (subAttributes.containsKey(attributeName)) {
            subAttributes.remove(attributeName);
        }
    }

    public boolean isSubAttributeExist(String attributeName) {
        return subAttributes.containsKey(attributeName);
    }

    /**
     * Set a sub attribute on the complex attribute.
     *
     * @param subAttribute
     * @throws CharonException
     */
    public void setSubAttribute(Attribute subAttribute)
            throws CharonException {
        subAttributes.put(subAttribute.getName(), subAttribute);
    }
    }

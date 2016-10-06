package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;

import java.util.HashMap;
import java.util.Map;

/*
 * This class is a blueprint of ComplexAttribute defined in SCIM Core Schema Spec.
 */
public class ComplexAttribute extends AbstractAttribute{

    //If it is a complex attribute, it has a list of sub attributes.
    protected Map<String, Attribute> subAttributesList = new HashMap<String, Attribute>();

    public ComplexAttribute(String name) { this.name=name; }

    public ComplexAttribute() {}

    /**
     * Retrieve the map of sub attributes.
     *
     * @return Map of Attributes
     */
    public Map<String, Attribute> getSubAttributesList() {
            return subAttributesList;
        }

    /**
     * Set the map of sub attributes.
     *
     * @param subAttributesList
     */
    public void setSubAttributesList(Map<String, Attribute> subAttributesList) {
        this.subAttributesList = subAttributesList;
    }

    /**
     * Retrieve one attribute given the attribute name.
     *
     * @param attributeName
     * @return Attribute
     */
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        if (subAttributesList.containsKey(attributeName)) {
            return subAttributesList.get(attributeName);
        } else {
            return null;
        }
    }

    @Override
    public void deleteSubAttributes() throws CharonException {
        subAttributesList.clear();
    }

    /**
     * Remove a sub attribute from the complex attribute given the sub attribute name.
     *
     * @param attributeName
     */
    public void removeSubAttribute(String attributeName) {
        if (subAttributesList.containsKey(attributeName)) {
            subAttributesList.remove(attributeName);
        }
    }

    /**
     * look for the existence of a sub attribute
     *
     * @param attributeName
     */
    public boolean isSubAttributeExist(String attributeName) {
        return subAttributesList.containsKey(attributeName);
    }

    /**
     * Set a sub attribute of the complex attribute's sub attribute list.
     *
     * @param subAttribute
     * @throws CharonException
     */
    public void setSubAttribute(Attribute subAttribute)
            throws CharonException {
        subAttributesList.put(subAttribute.getName(), subAttribute);
    }
    }

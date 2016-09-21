package org.wso2.charon.core.attributes;

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

    }

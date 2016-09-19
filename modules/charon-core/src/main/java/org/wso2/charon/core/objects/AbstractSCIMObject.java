package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

/**
 * This represents the object which is a collection of attributes defined by common-schema.
 * These attributes MUST be included in all other objects which become SCIM resources.
 */

public class AbstractSCIMObject implements SCIMObject{

    public Attribute getAttribute(String attributeName) throws NotFoundException {
        return null;
    }

    public void deleteAttribute(String attributeName) throws NotFoundException {

    }

    public List<String> getSchemaList() {
        return null;
    }

    public Map<String, Attribute> getAttributeList() {
        return null;
    }
}

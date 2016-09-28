package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the listed resource object which is a collection of resources
 **/

public class ListedResource implements SCIMObject {

    /*List of schemas which the resource is associated with*/
    protected List<String> schemaList = new ArrayList<String>();
    //number of items in the scim object List
    protected int totalResults;
    /*Collection of attributes which constitute this resource.*/
    protected Map<String, Attribute> attributeList = new HashMap<String, Attribute>();

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public void setSchemaList(List<String> schemaList) {
        this.schemaList = schemaList;
    }

    @Override
    public Attribute getAttribute(String attributeName) throws NotFoundException {
        return null;
    }

    @Override
    public void deleteAttribute(String attributeName) throws NotFoundException {

    }

    @Override
    public List<String> getSchemaList() {
        return schemaList;
    }

    @Override
    public Map<String, Attribute> getAttributeList() {
       return attributeList;
    }

    public void setSchema(String schema) {
        schemaList.add(schema);
    }

    public void setResources(Map<String, Attribute> valueWithAttributes) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.RESOURCES)) {
            MultiValuedAttribute resourcesAttribute =
                    new MultiValuedAttribute(SCIMConstants.ListedResourceSchemaConstants.RESOURCES);
            resourcesAttribute.setComplexValueWithSetOfSubAttributes(valueWithAttributes);
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.RESOURCES, resourcesAttribute);
        } else {
            ((MultiValuedAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.RESOURCES))
                    .setComplexValueWithSetOfSubAttributes(valueWithAttributes);
        }
    }

    private boolean isAttributeExist(String attributeName) {
        return attributeList.containsKey(attributeName);
    }

}

package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.attributes.MultiValuedAttribute;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.exceptions.NotFoundException;
import org.wso2.charon.core.v2.schema.SCIMConstants;

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
    //number of items in the scim object List  //default is 0
    protected int totalResults =0;
    /*Collection of attributes which constitute this resource.*/
    protected Map<String, Attribute> attributeList = new HashMap<String, Attribute>();

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS, totalResults);
            //No need to let the Default attribute factory to handle the attribute, as this is
            //not officially defined as SCIM attribute, hence have no charactersitics defined
            //TODO: may be we can let the default attribute factory to handle it?
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS))
                    .setValue(totalResults);
        }
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

    protected boolean isAttributeExist(String attributeName) {
        return attributeList.containsKey(attributeName);
    }

}

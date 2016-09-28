package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.*;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.ResourceTypeSchema;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

import java.util.*;

/**
 * This represents the object which is a collection of attributes defined by common-schema.
 * These attributes MUST be included in all other objects which become SCIM resources.
 */

public class AbstractSCIMObject implements SCIMObject{

    /*Collection of attributes which constitute this resource.*/
    protected Map<String, Attribute> attributeList = new HashMap<String, Attribute>();

    /*List of schemas where the attributes of this resource, are defined.*/
    protected List<String> schemaList = new ArrayList<String>();

    /**
     * Set the attributes and corresponding schema in the SCIM Object.
     *
     * @param newAttribute
     * @param resourceSchema
     */
    public void setAttribute(Attribute newAttribute, ResourceTypeSchema resourceSchema) {
        //and update the schemas list if any new schema used in the attribute, and create schemas array.
       // if (!isSchemaExists(resourceSchema.getSchemasList())) {
           // schemaList.add(resourceSchema.getSchemasList());
       // }
        //add the attribute to attribute map
        if (!isAttributeExist(newAttribute.getName())) {
            attributeList.put(newAttribute.getName(), newAttribute);
        }
    }

    /**
     * Set the attributes in the SCIM Object.
     *
     * @param newAttribute
     */
    public void setAttribute(Attribute newAttribute) {
        //add the attribute to attribute map
        if (!isAttributeExist(newAttribute.getName())) {
            attributeList.put(newAttribute.getName(), newAttribute);
        }
    }

    protected boolean isSchemaExists(String schemaName) {
        return schemaList.contains(schemaName);
    }

    public boolean isAttributeExist(String attributeName) {
        return attributeList.containsKey(attributeName);
    }

    public Map<String, Attribute> getAttributeList() { return attributeList; }

    @Override
    public void setSchema(String schema) {
        schemaList.add(schema);
    }

    public List<String> getSchemaList() {
        return schemaList;
    }

    @Override
    public Attribute getAttribute(String attributeName){
        if(attributeList.containsKey(attributeName)){
            return attributeList.get(attributeName);
        }
        return null;
    }

    /**
     * Deleting an attribute is the responsibility of an attribute holder.
     *
     * @param id - name of the attribute
     */
    public void deleteAttribute(String id) {
        if (attributeList.containsKey(id)) {
            attributeList.remove(id);
        }
    }

    /**
     * Deleting a sub attribute of complex attribute is the responsibility of an attribute holder.
     *
     * @param parentAttribute - name of the parent attribute
     * @param childAttribute - name of the sub attribute
     */
    public void deleteSubAttribute(String parentAttribute,String childAttribute) {
        if (attributeList.containsKey(parentAttribute)) {
            ((ComplexAttribute)(attributeList.get(parentAttribute))).removeSubAttribute(childAttribute);
        }
    }

    /**
     * Deleting a sub value's sub attribute of multivalued attribute is the responsibility of an attribute holder.
     *
     */
    public void deleteValuesSubAttribute(String attribute, String subAttribute, String subSimpleAttribute) {
        if (attributeList.containsKey(attribute)) {
            MultiValuedAttribute parentAttribute=((MultiValuedAttribute)attributeList.get(attribute));
            List<Attribute> attributeValues =parentAttribute.getAttributeValues();
            for(Attribute subValue : attributeValues){
                if(subAttribute.equals(subValue.getName())){
                    ((ComplexAttribute)subValue).removeSubAttribute(subSimpleAttribute);
                    break;
                }
            }
        }

    }

    protected boolean isMetaAttributeExist() {
        return attributeList.containsKey(SCIMConstants.CommonSchemaConstants.META);
    }

    /**
     * Set a value for the id attribute. If attribute not already created in the resource,
     * create attribute and set the value.
     * Unique identifier for the SCIM Resource as defined by the Service Provider
     * This is read-only. So can only set once.
     *
     * @param id Unique identifier for the SCIM Resource as defined by the Service Provider.
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setId(String id) throws CharonException, BadRequestException {
        if (isAttributeExist(SCIMConstants.CommonSchemaConstants.ID)) {
            String error ="Read only attribute is trying to be modified";
            throw new CharonException(error);
        } else {
            SimpleAttribute idAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.ID, id);
            idAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.ID, idAttribute);
            this.setAttribute(idAttribute);
        }

    }

    /**
     * set the created date and time of the resource
     *
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) throws CharonException, BadRequestException {
        //create the created date attribute as defined in schema.
        SimpleAttribute createdDateAttribute = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.CREATED,createdDate);
        createdDateAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.CREATED, createdDateAttribute);
        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check created date attribute already exist
            if (metaAttribute.isSubAttributeExist(createdDateAttribute.getName())) {
                //TODO:log info level log that created date already set and can't set again.
                String error ="Read only meta attribute is tried to modify";
                throw new CharonException(error);
            } else {
                metaAttribute.setSubAttribute(createdDateAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute Created Date.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(createdDateAttribute);

        }
    }

    /**
     * set the last modified date and time of the resource
     *
     * @param lastModifiedDate
     */
    public void setLastModified(Date lastModifiedDate) throws CharonException, BadRequestException {
        //create the lastModified date attribute as defined in schema.
        SimpleAttribute lastModifiedAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.LAST_MODIFIED,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.LAST_MODIFIED, lastModifiedDate));

        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check last modified attribute already exist
            if (metaAttribute.isSubAttributeExist(lastModifiedAttribute.getName())) {
                metaAttribute.removeSubAttribute(lastModifiedAttribute.getName());
                metaAttribute.setSubAttribute(lastModifiedAttribute);
            } else {
                metaAttribute.setSubAttribute(lastModifiedAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(lastModifiedAttribute);

        }
    }

    /**
     * crete the meta attribute of the scim object
     *
     */
    protected void createMetaAttribute() throws CharonException, BadRequestException {
        ComplexAttribute metaAttribute =
                (ComplexAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.META,
                        new ComplexAttribute(SCIMConstants.CommonSchemaConstants.META));
        if (isMetaAttributeExist()) {
            String error ="Read only meta attribute is tried to modify";
            throw new CharonException(error);
        } else {
            attributeList.put(SCIMConstants.CommonSchemaConstants.META, metaAttribute);
        }
    }
    /**
     * Return the meta attribute
     *
     * @return ComplexAttribute
     */
    protected ComplexAttribute getMetaAttribute() {
        if (isMetaAttributeExist()) {
            return (ComplexAttribute) attributeList.get(
                    SCIMConstants.CommonSchemaConstants.META);
        } else {
            return null;
        }
    }

    /**
     * Get the value of id attribute.
     * Unique identifier for the SCIM Resource as defined by the Service Provider.
     *
     * @return String
     */
    public String getId() throws CharonException {
        if (isAttributeExist(SCIMConstants.CommonSchemaConstants.ID)) {
            return ((SimpleAttribute) attributeList.get(
                    SCIMConstants.CommonSchemaConstants.ID)).getStringValue();
        } else {
            return null;
        }
    }

    /**
     * set the location of the meta attribute
     *
     * @param location
     */
    public void setLocation(String location) throws CharonException, BadRequestException {
        //create the location attribute as defined in schema.
        SimpleAttribute locationAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.LOCATION,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.LOCATION, location));

        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check version attribute already exist
            if (metaAttribute.isSubAttributeExist(locationAttribute.getName())) {
                String error ="Read only attribute is tried to modify";
                throw new CharonException(error);
            } else {
                metaAttribute.setSubAttribute(locationAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(locationAttribute);

        }
    }

    /**
     * set the resourceType of the meta attribute
     *
     * @param resourceType
     */
    public void setResourceType(String resourceType) throws BadRequestException, CharonException {
        //create the resourceType attribute as defined in schema.
        SimpleAttribute resourceTypeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.RESOURCE_TYPE,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.RESOURCE_TYPE, resourceType));

        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check version attribute already exist
            if (metaAttribute.isSubAttributeExist(resourceTypeAttribute.getName())) {
                String error ="Read only attribute is tried to modify";
                throw new CharonException(error);
            } else {
                metaAttribute.setSubAttribute(resourceTypeAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(resourceTypeAttribute);

        }
    }


}

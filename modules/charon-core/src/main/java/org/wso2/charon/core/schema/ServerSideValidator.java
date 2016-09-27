package org.wso2.charon.core.schema;

import org.w3c.dom.Attr;
import org.wso2.charon.core.attributes.AbstractAttribute;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon.core.utils.AttributeUtil;

import java.util.*;

public class ServerSideValidator extends AbstractValidator{

    /**
     * Validate created SCIMObject according to the spec
     *
     * @param scimObject
     * @param resourceSchema
     * @throw CharonException
     * @throw BadRequestException
     * @throw NotFoundException
     */
    public static void validateCreatedSCIMObject(AbstractSCIMObject scimObject, SCIMResourceTypeSchema resourceSchema)
            throws CharonException, BadRequestException, NotFoundException {

        removeAnyReadOnlyAttributes(scimObject,resourceSchema);

        //add created and last modified dates
        String id = UUID.randomUUID().toString();
        scimObject.setId(id);
        Date date = new Date();
        //set the created date and time
        scimObject.setCreatedDate(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(date)));
        //creates date and the last modified are the same if not updated.
        scimObject.setLastModified(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(date)));
        //set location and resourceType
        if (resourceSchema.isSchemaAvailable(SCIMConstants.USER_CORE_SCHEMA_URI)){
            String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                    SCIMConstants.USER_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
            scimObject.setResourceType(SCIMConstants.USER);
        } else if (resourceSchema.isSchemaAvailable(SCIMConstants.GROUP_CORE_SCHEMA_URI)) {
            String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                    SCIMConstants.GROUP_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
            scimObject.setResourceType(SCIMConstants.GROUP);
        }
        //TODO:Are we supporting version ? (E-tag-resource versioning)
        validateSCIMObjectForRequiredAttributes(scimObject, resourceSchema);
        validateSchemaList(scimObject, resourceSchema);
    }


    private static String createLocationHeader(String location, String resourceID) {
        String locationString = location + "/" + resourceID;
        return locationString;
    }

    public static void removeAttributesOnReturn(User createdUser, ArrayList<String> reuqestedAttributes,
                                                ArrayList<String> requestedExcludingAttributes) {
        Map<String, Attribute> attributeList = createdUser.getAttributeList();
        ArrayList<Attribute> attributeTemporyList= new ArrayList<Attribute>();
        for (Attribute attribute : attributeList.values()) {
            attributeTemporyList.add(attribute);
        }
        for(Attribute attribute : attributeTemporyList){
            //check for never/request attributes.
            if (attribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
                createdUser.deleteAttribute(attribute.getName());
            }
            //if the returned property is request, need to check whether is it specifically requested by the user.
            // If so return it.
            else if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)){
                if(!reuqestedAttributes.contains(attribute.getName()) ){
                    createdUser.deleteAttribute(attribute.getName());
                }
                //if it has been asked to remove, remove it
                if(requestedExcludingAttributes.contains(attribute.getName())){
                    createdUser.deleteAttribute(attribute.getName());
                }
            }

            //check the same for sub attributes
            if(attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)){
                if(attribute.getMultiValued()){
                    List<Attribute> valuesList = ((MultiValuedAttribute)attribute).getAttributeValues();

                    for (Attribute subAttribute : valuesList) {
                        Map<String,Attribute> valuesSubAttributeList=((ComplexAttribute)subAttribute).getSubAttributesList();
                        ArrayList<Attribute> valuesSubAttributeTemporyList= new ArrayList<Attribute>();
                        for (Attribute subSimpleAttribute : valuesSubAttributeList.values()) {
                            valuesSubAttributeTemporyList.add(subSimpleAttribute);
                        }
                        for(Attribute subSimpleAttribute : valuesSubAttributeTemporyList){
                            if(subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)){
                                createdUser.deleteValuesSubAttribute(attribute.getName(),
                                        subAttribute.getName(),subSimpleAttribute.getName());
                            }
                            if(subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)){
                                createdUser.deleteValuesSubAttribute(attribute.getName(),
                                        subAttribute.getName(),subSimpleAttribute.getName());
                            }
                            //TODO: what if the user says he needs sub attribute in the 'attributes' parameter in the request
                        }
                    }
                }
                else{
                    Map<String, Attribute> subAttributeList = ((ComplexAttribute)attribute).getSubAttributesList();
                    ArrayList<Attribute> subAttributeTemporyList= new ArrayList<Attribute>();
                    for (Attribute subAttribute : subAttributeList.values()) {
                        subAttributeTemporyList.add(subAttribute);
                    }
                    for(Attribute subAttribute : subAttributeTemporyList){
                        if(subAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)){
                            createdUser.deleteSubAttribute(attribute.getName(),subAttribute.getName());
                        }
                        if(subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)){
                            createdUser.deleteSubAttribute(attribute.getName(),subAttribute.getName());
                        }
                    //TODO: what if the user says he needs sub attribute in the 'attributes' parameter in the request
                    }
                }
            }
        }
    }
}



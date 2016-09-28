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

    public static void validateRetrievedSCIMObject(AbstractSCIMObject scimObject,
                                                   SCIMResourceTypeSchema resourceSchema,ArrayList<String> reuqestedAttributes,
                                                   ArrayList<String> requestedExcludingAttributes)
            throws BadRequestException, CharonException {
        removeAttributesOnReturn(scimObject,reuqestedAttributes,requestedExcludingAttributes);
        validateSCIMObjectForRequiredAttributes(scimObject, resourceSchema);
        validateSchemaList(scimObject, resourceSchema);
    }
}



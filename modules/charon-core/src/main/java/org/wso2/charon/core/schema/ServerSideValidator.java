package org.wso2.charon.core.schema;

import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon.core.utils.AttributeUtil;

import java.util.Date;
import java.util.UUID;

public class ServerSideValidator extends AbstractValidator{


    public static void validateCreatedSCIMObject(AbstractSCIMObject scimObject, SCIMResourceTypeSchema resourceSchema)
            throws CharonException, BadRequestException, NotFoundException {

        removeAnyReadOnlyAttributes(scimObject,resourceSchema);

        //add created and last modified dates
        String id = UUID.randomUUID().toString();
        scimObject.setId(id);
        Date date = new Date();
        scimObject.setCreatedDate(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(date)));
        //created n last modified are the same if not updated.
        scimObject.setLastModified(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(date)));
        //set location
        if (SCIMConstants.USER_CORE_SCHEMA_URI.equals(resourceSchema.getSchemasList())) {
            String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                    SCIMConstants.USER_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
        } else if (SCIMConstants.GROUP.equals(resourceSchema.getSchemasList())) {
            String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                    SCIMConstants.GROUP_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
        }

        validateSCIMObjectForRequiredAttributes(scimObject, resourceSchema);
        validateSchemaList(scimObject, resourceSchema);
        //TODO: validate for uniqueness
    }


    private static String createLocationHeader(String location, String resourceID) {
        String locationString = location + "/" + resourceID;
        return locationString;
    }

    public static void removePasswordOnReturn(User scimUser) {
        if (scimUser.getAttributeList().containsKey(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PASSWORD.getName())) {
            scimUser.deleteAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PASSWORD.getName());
        }
    }
}


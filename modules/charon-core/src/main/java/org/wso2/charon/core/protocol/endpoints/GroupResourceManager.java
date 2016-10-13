package org.wso2.charon.core.protocol.endpoints;

import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.*;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.ServerSideValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on GroupResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on Group ,
 * based on the HTTP requests received by SCIM Client.
 */

public class GroupResourceManager extends AbstractResourceManager {
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    /**
     * Create group in the service provider given the submitted payload that contains the SCIM group
     * resource, format and the handler to storage.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param userManager
     * @param  attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager,
                               String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        JSONDecoder decoder = null;

        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            decoder = getDecoder();

            //decode the SCIM group object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString,
                    SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());
            //validate decoded group
            ServerSideValidator.validateCreatedSCIMObject(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
            //handover the SCIM User object to the group storage provided by the SP.
            Group createdGroup;
            //need to send back the newly created group in the response payload
            createdGroup = ((UserManager) userManager).createGroup(group);

            //encode the newly created SCIM group object and add id attribute to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (createdGroup != null) {

                encodedGroup = encoder.encodeSCIMObject(createdGroup);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.GROUP_ENDPOINT) + "/" + createdGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String message = "Newly created Group resource is null..";
                throw new InternalErrorException(message);
            }

            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedGroup, httpHeaders);

        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (ConflictException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        return null;
    }

    @Override
    public SCIMResponse listByAttribute(String searchAttribute, UserManager userManager, String format) {
        return null;
    }

    @Override
    public SCIMResponse listByFilter(String filterString, UserManager userManager, String attributes, String excludeAttributes) throws IOException {
        return null;
    }

    @Override
    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse listWithPagination(int startIndex, int count, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse list(UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, String inputFormat, String outputFormat, UserManager userManager) {
        return null;
    }
}

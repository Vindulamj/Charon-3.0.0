package org.wso2.charon.core.protocol.endpoints;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.config.CharonConfiguration;
import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.*;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.PaginatedListedResource;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on GroupResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on Group ,
 * based on the HTTP requests received by SCIM Client.
 */

public class GroupResourceManager extends AbstractResourceManager {

    /**
     * Retrieves a group resource given an unique group id. Mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return SCIM response to be returned.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder();

            //API user should pass a UserManager storage to GroupResourceEndpoint.
            //retrieve the group from the provided storage.
            Group group = ((UserManager) userManager).getGroup(id);

            //if group not found, return an error in relevant format.
            if (group == null) {
                String message = "Group not found in the user store.";
                throw new NotFoundException(message);
            }
            // returns core-group schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

            ServerSideValidator.validateRetrievedSCIMObject(group, schema,attributes,excludeAttributes);
            //convert the group into specific format.
            String encodedGroup = encoder.encodeSCIMObject(group);
            //if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
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
            // returns core-group schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            //decode the SCIM group object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString, schema, new Group());
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

            //put the URI of the Group object in the response header parameter.
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
    /**
     * Method of the ResourceManager that is mapped to HTTP Delete method..
     *
     * @param id - unique resource id
     * @param userManager - userManager instance defined by the external implementor of charon
     * @return
     */
    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        JSONEncoder encoder = null;
        try {
            if (userManager != null) {
            /*handover the SCIM User object to the user storage provided by the SP for the delete operation*/
                userManager.deleteGroup(id);
                //on successful deletion SCIMResponse only has 204 No Content status code.
                return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, null);
            }
            else{
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
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
        //A value less than one shall be interpreted as 1
        if(startIndex<1){
            startIndex=1;
        }
        //If count is not set, server default should be taken
        if(count == 0){
            CharonConfiguration.getInstance().getCountValueForPagination();
        }
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            List<Group> returnedGroups;
            int totalResults=0;
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.listGroupsWithPagination(startIndex,count);

                //TODO: Are we having this method support from user core
                totalResults =userManager.getGroupCount();

                //if user not found, return an error in relevant format.
                if (returnedGroups == null || returnedGroups.isEmpty()) {
                    String error = "Groups not found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                // returns core-group schema
                SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

                for(Group group : returnedGroups){
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObject(group, schema, attributes, excludeAttributes);
                }
                //create a listed resource object out of the returned users list.
                PaginatedListedResource listedResource = createPaginatedListedResource(
                        returnedGroups,startIndex,totalResults);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> ResponseHeaders = new HashMap<String, String>();
                ResponseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, ResponseHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse list(UserManager userManager, String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            List<Group> returnedGroups;
            //API group should pass a UserManager storage to GroupResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.listGroups();

                //if groups not found, return an error in relevant format.
                if (returnedGroups == null || returnedGroups.isEmpty()) {
                    String error = "Groups not found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                for(Group group: returnedGroups){
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObject(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                            attributes, excludeAttributes);
                }
                //create a listed resource object out of the returned groups list.
                ListedResource listedResource = createListedResource(returnedGroups);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> ResponseHeaders = new HashMap<String, String>();
                ResponseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, ResponseHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                //log the error as well.
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    /**
     * Creates the Listed Resource.
     *
     * @param groups
     * @return
     */
    public ListedResource createListedResource(List<Group> groups)
            throws CharonException, NotFoundException {
        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(groups.size());
        for (Group group : groups) {
            Map<String, Attribute> userAttributes = group.getAttributeList();
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }

    /**
     * Creates the Paginated Listed Resource.
     *
     * @param groups
     * @return
     */
    public PaginatedListedResource createPaginatedListedResource(List<Group> groups,int startIndex, int totalResults)
            throws CharonException, NotFoundException {
        PaginatedListedResource paginatedListedResource = new PaginatedListedResource();
        paginatedListedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        paginatedListedResource.setTotalResults(totalResults);
        paginatedListedResource.setItemsPerPage(groups.size());
        paginatedListedResource.setStartIndex(startIndex);

        for (Group group : groups) {
            Map<String, Attribute> userAttributes = group.getAttributeList();
            paginatedListedResource.setResources(userAttributes);
        }
        return paginatedListedResource;
    }


}

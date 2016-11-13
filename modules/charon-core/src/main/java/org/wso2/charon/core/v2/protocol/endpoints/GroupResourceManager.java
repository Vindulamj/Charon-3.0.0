/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon.core.v2.protocol.endpoints;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.config.CharonConfiguration;
import org.wso2.charon.core.v2.encoder.JSONDecoder;
import org.wso2.charon.core.v2.encoder.JSONEncoder;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.exceptions.ConflictException;
import org.wso2.charon.core.v2.exceptions.InternalErrorException;
import org.wso2.charon.core.v2.exceptions.NotFoundException;
import org.wso2.charon.core.v2.exceptions.NotImplementedException;
import org.wso2.charon.core.v2.extensions.UserManager;
import org.wso2.charon.core.v2.objects.Group;
import org.wso2.charon.core.v2.objects.ListedResource;
import org.wso2.charon.core.v2.objects.PaginatedListedResource;
import org.wso2.charon.core.v2.objects.User;
import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;
import org.wso2.charon.core.v2.protocol.SCIMResponse;
import org.wso2.charon.core.v2.schema.SCIMConstants;
import org.wso2.charon.core.v2.schema.SCIMResourceSchemaManager;
import org.wso2.charon.core.v2.schema.SCIMResourceTypeSchema;
import org.wso2.charon.core.v2.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.v2.schema.ServerSideValidator;
import org.wso2.charon.core.v2.utils.AttributeUtil;
import org.wso2.charon.core.v2.utils.CopyUtil;
import org.wso2.charon.core.v2.utils.codeutils.FilterTreeManager;
import org.wso2.charon.core.v2.utils.codeutils.Node;
import org.wso2.charon.core.v2.utils.codeutils.SearchRequest;


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

    /*
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

            ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema, attributes, excludeAttributes);
            //convert the group into specific format.
            String encodedGroup = encoder.encodeSCIMObject(group);
            //if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
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
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (ConflictException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }
    /*
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
            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * Method to filter the groups based on parameters
     * @param filterString
     * @param userManager
     * @param attributes
     * @param  excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse listByFilter(String filterString, UserManager userManager,
                                     String attributes, String excludeAttributes)  {
        JSONEncoder encoder = null;

        FilterTreeManager filterTreeManager = null;
        try {
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

            filterTreeManager = new FilterTreeManager(filterString, schema);
            Node rootNode = filterTreeManager.buildTree();

            //obtain the json encoder
            encoder = getEncoder();

            List<Group> returnedGroups;
            int totalResults = 0;
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.filterGroups(rootNode);

                //if user not found, return an error in relevant format.
                if (returnedGroups == null || returnedGroups.isEmpty()) {
                    String error = "No filter results are found";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                for (Group group: returnedGroups) {
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema, attributes, excludeAttributes);
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(returnedGroups);
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
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return encodeSCIMException(charonException);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * Method to sort the groups
     * @param sortBy
     * @param sortOrder
     * @param usermanager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager,
                                   String attributes, String excludeAttributes) {
        try {
            //check whether provided sortOrder is valid or not
            if (sortOrder != null) {
                if (!(sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING)
                        || sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.DESCENDING))) {
                    String error = " Invalid sortOrder value is specified";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }
            //If a value for "sortBy" is provided and no "sortOrder" is specified,
            // "sortOrder" SHALL default to ascending.
            if (sortOrder == null && sortBy != null) {
                sortOrder = SCIMConstants.OperationalConstants.ASCENDING;
            }
            JSONEncoder encoder = null;
            //obtain the json encoder
            encoder = getEncoder();

            List<Group> returnedGroups;

            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (usermanager != null) {
                // unless configured returns core-user schema or else returns extended user schema)
                SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

                String sortByAttributeURI = null;

                if (sortBy != null) {
                    sortByAttributeURI = AttributeUtil.getAttributeURI(sortBy, schema);
                }
                returnedGroups = usermanager.sortGroups(sortByAttributeURI, sortOrder.toLowerCase());

                //if user not found, return an error in relevant format.
                if (returnedGroups == null || returnedGroups.isEmpty()) {
                    String error = "Users not found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                for (Group group: returnedGroups){
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema, attributes, excludeAttributes);
                }
                //create a listed resource object out of the returned users list.
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
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * method to list the groups with pagination enable
     * @param startIndex
     * @param count
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse listWithPagination(int startIndex, int count,
                                           UserManager userManager, String attributes, String excludeAttributes) {
        //A value less than one shall be interpreted as 1
        if (startIndex < 1) {
            startIndex = 1;
        }
        //If count is not set, server default should be taken
        if (count == 0) {
            CharonConfiguration.getInstance().getCountValueForPagination();
        }
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            List<Group> returnedGroups;
            int totalResults = 0;
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.listGroupsWithPagination(startIndex, count);

                //TODO: Are we having this method support from user core
                totalResults = userManager.getGroupCount();

                //if user not found, return an error in relevant format.
                if (returnedGroups == null || returnedGroups.isEmpty()) {
                    String error = "Groups not found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                // returns core-group schema
                SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

                for (Group group : returnedGroups) {
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema, attributes, excludeAttributes);
                }
                //create a listed resource object out of the returned users list.
                PaginatedListedResource listedResource = createPaginatedListedResource(
                        returnedGroups, startIndex, totalResults);
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
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * Method to list the groups at the /Groups endpoint
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
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

                for (Group group: returnedGroups){
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(
                            group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
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
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * this facilitates the querying using HTTP POST
     * @param resourceString
     * @param userManager
     * @return
     */

    public SCIMResponse listGroupsWithPOST(String resourceString, UserManager userManager)
    {
        JSONEncoder encoder = null;
        JSONDecoder decoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            //obtain the json decoder
            decoder = getDecoder();

            //create the search request object
            SearchRequest searchRequest = decoder.decodeSearchRequestBody(resourceString);

            //A value less than one shall be interpreted as 1
            if(searchRequest.getStartIndex() < 1){
                searchRequest.setStartIndex(1);
            }
            //If count is not set, server default should be taken
            if(searchRequest.getCount() == 0){
                searchRequest.setCount(CharonConfiguration.getInstance().getCountValueForPagination());
            }

            List<Group> returnedGroups;
            int totalResults = 0;
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.listGroupsWithPost(searchRequest);

                //TODO: Are we having this method support from user core
                totalResults = userManager.getGroupCount();

                //if user not found, return an error in relevant format.
                if (returnedGroups == null || returnedGroups.isEmpty()) {
                    String error = "Groups not found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                // unless configured returns core-user schema or else returns extended user schema)
                SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

                for(Group group:returnedGroups){
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema,
                            searchRequest.getAttributesAsString(), searchRequest.getExcludedAttributesAsString());
                }
                //create a listed resource object out of the returned users list.
                PaginatedListedResource listedResource = createPaginatedListedResource(
                        returnedGroups, searchRequest.getStartIndex(), totalResults);
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
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /*
     * method which corresponds to HTTP PUT - delete the group
     * @param existingId
     * @param scimObjectString
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString,
                                      UserManager userManager, String attributes, String excludeAttributes) {
        //needs to validate the incoming object. eg: id can not be set by the consumer.

        JSONEncoder encoder = null;
        JSONDecoder decoder = null;

        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder.
            decoder = getDecoder();

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

            //decode the SCIM User object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString, schema, new Group());
            Group updatedGroup = null;
            if (userManager != null) {
                //retrieve the old object
                Group oldGroup = userManager.getGroup(existingId);
                if (oldGroup != null) {
                    Group newGroup = (Group) ServerSideValidator.validateUpdatedSCIMObject(oldGroup, group, schema);
                    updatedGroup = userManager.updateGroup(oldGroup, newGroup);

                } else {
                    String error = "No user exists with the given id: " + existingId;
                    throw new NotFoundException(error);
                }

            } else {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (updatedGroup != null) {
                //create a deep copy of the user object since we are going to change it.
                Group copiedGroup = (Group) CopyUtil.deepCopy(updatedGroup);
                //need to remove password before returning
                ServerSideValidator.removeAttributesOnReturn(copiedGroup, attributes, excludeAttributes);
                encodedGroup = encoder.encodeSCIMObject(copiedGroup);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.GROUP_ENDPOINT) + "/" + updatedGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated Group resource is null.";
                throw new InternalErrorException(error);
            }

            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);

        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse updateWithPATCH(
            String existingId, String scimObjectString, UserManager userManager,
            String attributes, String excludeAttributes) {
        return null;
    }

    /*
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

    /*
     * Creates the Paginated Listed Resource.
     *
     * @param groups
     * @return
     */
    public PaginatedListedResource createPaginatedListedResource(List<Group> groups, int startIndex, int totalResults)
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

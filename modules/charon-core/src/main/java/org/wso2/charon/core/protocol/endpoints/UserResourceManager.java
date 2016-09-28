package org.wso2.charon.core.protocol.endpoints;


import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.*;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.*;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.wso2.charon.core.utils.CopyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on UserResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on USER ,
 * based on the HTTP requests received by SCIM Client.
 */

public class UserResourceManager extends AbstractResourceManager {

    private Log logger;

    public UserResourceManager() {
        logger = LogFactory.getLog(UserResourceManager.class);
    }

    /**
     * Retrieves a user resource given an unique user id. Mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param userManager - userManager instance defined by the external implementor of charon
     * @return SCIM response to be returned.
     */
    public SCIMResponse get(String id, UserManager userManager) {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            /*API user should pass a UserManager impl to UserResourceEndpoint.
            retrieve the user from the provided UM handler.*/
            User user = ((UserManager) userManager).getUser(id);

            //if user not found, return an error in relevant format.
            if (user == null) {
                String error = "User not found in the user store.";
                throw new NotFoundException(error);
            }

            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            //perform service provider side validation.
            ServerSideValidator.validateRetrievedSCIMObject(user, schema,new ArrayList<String>(),new ArrayList<String>());
            //convert the user into requested format.
            String encodedUser = encoder.encodeSCIMObject(user);
            //if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);

        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /**
     * Returns SCIMResponse based on the sucess or failure of the create user operation
     *
     * @param scimObjectString -raw string containing user info
     * @return userManager - userManager instance defined by the external implementor of charon
     */
    public SCIMResponse create(String scimObjectString, UserManager userManager)  {

        JSONEncoder encoder =null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            //obtain the json decoder
            JSONDecoder decoder = getDecoder();

            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());

            //validate the created user
            ServerSideValidator.validateCreatedSCIMObject(user, schema);

            /*handover the SCIM User object to the user storage provided by the SP.
            need to send back the newly created user in the response payload*/
            User createdUser = userManager.createUser(user);

            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> ResponseHeaders = new HashMap<String, String>();

            if (createdUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(createdUser);
                //need to remove password before returning
                ServerSideValidator.removeAttributesOnReturn(copiedUser,new ArrayList<String>(),new ArrayList<String>());
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                ResponseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + createdUser.getId());
                ResponseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }

            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED,
                    encodedUser, ResponseHeaders);

        } catch (CharonException e) {
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getStatus() == -1) {
                e.setStatus(ResponseCodeConstants.CODE_INTERNAL_ERROR);
            }
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (ConflictException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    public SCIMResponse delete(String id, String outputFormat) {
        return null;
    }

    public SCIMResponse listByAttribute(String searchAttribute, UserManager userManager, String format) {
        return null;
    }

    public SCIMResponse listByFilter(String filterString, UserManager userManager, String format) {
        return null;
    }

    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager, String format) {
        return null;
    }

    public SCIMResponse listWithPagination(int startIndex, int count, UserManager userManager, String format) {
        return null;
    }

    public SCIMResponse list(UserManager userManager, String format) {
        return null;
    }

    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, String inputFormat, String outputFormat, UserManager userManager) {
        return null;
    }

    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, String inputFormat, String outputFormat, UserManager userManager) {
        return null;
    }
}

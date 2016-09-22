package org.wso2.charon.core.protocol.endpoints;


import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.*;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.wso2.charon.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon.core.schema.ServerSideValidator;
import org.wso2.charon.core.utils.CopyUtil;

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

    public SCIMResponse get(String id, String format, UserManager userManager) {
        return null;
    }


    public SCIMResponse create(String scimObjectString, UserManager userManager)  {

        JSONEncoder encoder =null;
        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder();

            //obtain the decoder matching the submitted format.
            JSONDecoder decoder = getDecoder();

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
                ServerSideValidator.removePasswordOnReturn(copiedUser);
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
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedUser, ResponseHeaders);

        } catch (FormatNotSupportedException e) {
            //if the submitted format not supported, encode exception and set it in the response.
            return AbstractResourceManager.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getStatus() == -1) {
                e.setStatus(ResponseCodeConstants.CODE_INTERNAL_ERROR);
            }
            return AbstractResourceManager.encodeSCIMException(encoder, e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(encoder, e);
        } catch (ConflictException e) {
            return AbstractResourceManager.encodeSCIMException(encoder, e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(encoder, e);
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

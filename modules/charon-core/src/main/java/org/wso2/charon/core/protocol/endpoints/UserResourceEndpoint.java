package org.wso2.charon.core.protocol.endpoints;


import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.ConflictException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
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
public class UserResourceEndpoint extends AbstractResourceEndpoint {

    private Log logger;

    public UserResourceEndpoint() {
        logger = LogFactory.getLog(UserResourceEndpoint.class);
    }

    public SCIMResponse get(String id, String format, UserManager userManager) {
        return null;
    }


    public SCIMResponse create(String scimObjectString, String inputFormat, String outputFormat, UserManager userManager, boolean isBulkUserAdd)  {

        JSONEncoder encoder =null;
        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));

            //obtain the decoder matching the submitted format.
            JSONDecoder decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());

            //validate the created user
            ServerSideValidator.validateCreatedSCIMObject(user, schema);

            /*handover the SCIM User object to the user storage provided by the SP.
            need to send back the newly created user in the response payload*/
            User createdUser = userManager.createUser(user, isBulkUserAdd);

            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();

            if (createdUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = userManager.createUser(user, isBulkUserAdd);
                //need to remove password before returning
                ServerSideValidator.removePasswordOnReturn(copiedUser);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + createdUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, outputFormat);

            } else {
                String error = "Newly created User resource is null.";
                logger.error(error);
                throw new InternalException(error);
            }

            //put the URI of the User object in the response header parameter.
            SCIMResponse res=new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedUser, httpHeaders);
            System.out.println(res.getResponseMessage());

        } catch (FormatNotSupportedException e) {
            logger.error("Format not found exception.", e);
            //if the submitted format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            logger.error("Internal server error while creating new resource.", e);
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getStatus() == -1) {
                e.setStatus(ResponseCodeConstants.CODE_INTERNAL_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (BadRequestException e) {
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (ConflictException e) {
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
        //TODO:Return
        return new SCIMResponse(-1,null,null);
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

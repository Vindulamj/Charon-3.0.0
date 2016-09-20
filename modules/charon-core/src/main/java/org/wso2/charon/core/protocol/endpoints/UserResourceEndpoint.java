package org.wso2.charon.core.protocol.endpoints;


import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.extensions.Storage;
import org.wso2.charon.core.extensions.UserManager;

import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.scheme.SCIMConstants;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;



/**
 * REST API exposed by Charon-Core to perform operations on UserResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on USER ,
 * based on the HTTP requests received by SCIM Client.
 */
public class UserResourceEndpoint extends AbstractResourceEndpoint {

    private Log logger = LogFactory.getLog(UserResourceEndpoint.class);

    public SCIMResponse get(String id, String format, UserManager userManager) {
        return null;
    }

    public SCIMResponse create(String scimObjectString, String inputFormat, String outputFormat, UserManager userManager) {

        JSONEncoder encoder =null;
        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));

            //obtain the decoder matching the submitted format.
            JSONDecoder decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));

        } catch (FormatNotSupportedException e) {
            logger.error("Format not found exception.", e);
            //if the submitted format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            logger.error("Internal server error while creating new resource.", e);
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getStatus() == -1) {
                //e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            //return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
        return null;
    }

    public SCIMResponse delete(String id, Storage storage, String outputFormat) {
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

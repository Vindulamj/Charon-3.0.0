package org.wso2.charon.core.protocol.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.config.CharonConfiguration;
import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.InternalErrorException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon.core.utils.CopyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The service provider configuration resource enables a service
 * provider to discover SCIM specification features in a standardized
 * form as well as provide additional implementation details to clients.
 */
public class ServiceProviderConfigResourceManager extends AbstractResourceManager {

    private Log logger;

    public ServiceProviderConfigResourceManager() {
        logger = LogFactory.getLog(ServiceProviderConfigResourceManager.class);
    }

    /**
     * Retrieves a service provider config
     *
     * @return SCIM response to be returned.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        return getServiceProviderConfig();
    }

    private SCIMResponse getServiceProviderConfig(){
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            JSONDecoder decoder = getDecoder();

            // get the service provider config schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getServiceProviderConfigResourceSchema();
            //create a string in json format with relevant values
            String scimObjectString = encoder.buildServiceProviderConfigJsonBody(CharonConfiguration.getInstance().getConfig());
            //decode the SCIM service provider config object, encoded in the submitted payload.
            AbstractSCIMObject ServiceProviderConfigObject = (AbstractSCIMObject) decoder.decodeResource(
                    scimObjectString, schema, new AbstractSCIMObject());

            //encode the newly created SCIM service provider config object and add id attribute to Location header.
            String encodedObject;
            Map<String, String> ResponseHeaders = new HashMap<String, String>();

            if (ServiceProviderConfigObject != null) {
                //create a deep copy of the service provider config object since we are going to change it.
                AbstractSCIMObject copiedObject = (AbstractSCIMObject) CopyUtil.deepCopy(ServiceProviderConfigObject);
                encodedObject = encoder.encodeSCIMObject(copiedObject);
                //add location header
                ResponseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT));
                ResponseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }
            //put the URI of the service provider config object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK,
                    encodedObject, ResponseHeaders);
        }
        catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
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
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }
}

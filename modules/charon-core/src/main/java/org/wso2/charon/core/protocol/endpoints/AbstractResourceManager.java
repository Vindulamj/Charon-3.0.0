package org.wso2.charon.core.protocol.endpoints;

import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an abstract layer for all the resource endpoints to abstract out common
 * operations. And an entry point for initiating the charon from the outside.
 */
public abstract class AbstractResourceManager implements ResourceEndpoint {

    private static JSONEncoder encoder;

    private static JSONDecoder decoder;

    //Keeps  a map of endpoint urls of the exposed resources.
    private static Map<String, String> endpointURLMap;


    /**
     * Returns the encoder for json.
     *
     * @return JSONEncoder - An json encoder for encoding data
     * @throws CharonException
     */
    public JSONEncoder getEncoder() throws CharonException {
        if(encoder == null) {
            //if the encoder is not set, throw a charon exception
            String error="Encoder is not set";
            throw new CharonException(error);
        }
        return encoder;
    }

    /**
     * Returns the decoder for json.
     *
     *
     * @return JSONDecoder - An json decoder for decoding data
     * @throws CharonException
     */
    public JSONDecoder getDecoder() throws CharonException {

        if(decoder == null) {
            //if the decoder is not set, throw a charon exception
            String error="Decoder is not set";
            throw new CharonException(error);
        }
        return decoder;

    }

    /**
     * Returns the endpoint according to the resource.
     *
     * @param resource -Resource type
     * @return endpoint URL
     * @throws NotFoundException
     */
    public static String getResourceEndpointURL(String resource) throws NotFoundException{
        if (endpointURLMap != null && endpointURLMap.size() != 0) {
            return endpointURLMap.get(resource);
        } else {
            throw new NotFoundException();
        }
    }

    public void setEncoder(JSONEncoder encoder) { this.encoder = encoder; }

    public void setDecoder(JSONDecoder decoder) { this.decoder = decoder; }

    public void setEndpointURLMap(Map<String, String> endpointURLMap) {
        AbstractResourceManager.endpointURLMap = endpointURLMap;
    }

    /**
     * Returns SCIM Response object after json encoding the exception
     *
     * @param exception - exception message
     * @return SCIMResponse
     */
    public static SCIMResponse encodeSCIMException(AbstractCharonException exception) {
        Map<String, String> ResponseHeaders = new HashMap<String, String>();
        ResponseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER,SCIMConstants.APPLICATION_JSON);
        return new SCIMResponse(exception.getStatus(), encoder.encodeSCIMException(exception), ResponseHeaders);
    }
}

package org.wso2.charon.core.protocol.endpoints;

import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an abstract layer for all the resource endpoints to abstract out common
 * operations. And and entry point for CharonManager implementations to pass handlers to the
 * implementations of extension points.
 */
public abstract class AbstractResourceManager implements ResourceEndpoint {

    private JSONEncoder encoder;

    private JSONDecoder decoder;

    //Keeps  a map of endpoint urls of the exposed resources.
    private static Map<String, String> endpointURLMap;

    /**
     * Returns the encoder given the encoding format.
     *
     * @return
     * @throws FormatNotSupportedException
     */
    public JSONEncoder getEncoder() throws FormatNotSupportedException, CharonException {
        if(encoder == null) {
            //if the encoder is not set, throw a charon exception
            String error="Encoder is not set";
            throw new CharonException(error);
        }
        return encoder;
    }

    /**
     * Returns the decoder given the decoding format.
     *
     *
     * @return
     * @throws FormatNotSupportedException
     */
    public JSONDecoder getDecoder() throws FormatNotSupportedException, CharonException {

        if(decoder == null) {
            //if the decoder is not set, throw a charon exception
            String error="Decoder is not set";
            throw new CharonException(error);
        }
        return decoder;

    }

    public static SCIMResponse encodeSCIMException(JSONEncoder encoder,
                                                   AbstractCharonException exception) {
        Map<String, String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.identifyContentType(encoder.getFormat()));
        return new SCIMResponse(exception.getStatus(), encoder.encodeSCIMException(exception), httpHeaders);
    }

    public void setEncoder(JSONEncoder encoder) { this.encoder = encoder; }

    public void setDecoder(JSONDecoder decoder) { this.decoder = decoder; }

    public static String getResourceEndpointURL(String resource) {
        if (endpointURLMap != null && endpointURLMap.size() != 0) {
            return endpointURLMap.get(resource);
        } else {
            return null;
        }
    }
}

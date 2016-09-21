package org.wso2.charon.core.encoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.SCIMConstants;

public class JSONEncoder {

    private String format;
    private Log logger = LogFactory.getLog(JSONEncoder.class);

    public JSONEncoder() {
        format = SCIMConstants.JSON;
    }

    public String getFormat() {
        return format;
    }

    public String encodeSCIMException(AbstractCharonException exception) {
        //outer most json object
        JSONObject rootErrorObject = new JSONObject();
        //if multiple errors present, we send them in an array.
        JSONArray arrayOfErrors = new JSONArray();
        //JSON Object containing the error code and error message
        JSONObject errorObject = new JSONObject();

        try {
            //construct error object with details in the exception
            errorObject.put(ResponseCodeConstants.DESCRIPTION, exception.getDetail());
            errorObject.put(ResponseCodeConstants.STATUS, String.valueOf(exception.getStatus()));
            arrayOfErrors.put(errorObject);
            //construct the full json obj.
            rootErrorObject.put(ResponseCodeConstants.ERRORS, arrayOfErrors);

        } catch (JSONException e) {
            //usually errors occur rarely when encoding exceptions. and no need to pass them to clients.
            //sufficient to log them in server side back end.
            logger.error("SCIMException encoding error");
        }
        return rootErrorObject.toString();
    }
}





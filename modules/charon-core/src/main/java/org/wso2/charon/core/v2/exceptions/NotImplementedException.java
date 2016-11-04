package org.wso2.charon.core.v2.exceptions;

import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * Created by vindula on 9/19/16.
 */
public class NotImplementedException extends AbstractCharonException  {
    public NotImplementedException(String msg) {
        status = ResponseCodeConstants.CODE_NOT_IMPLEMENTED;
        detail = msg;
    }
}

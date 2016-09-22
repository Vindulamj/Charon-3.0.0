package org.wso2.charon.core.extensions;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.ConflictException;
import org.wso2.charon.core.objects.User;

/**
 * Created by vindula on 9/19/16.
 */
public interface UserManager {

    public User createUser(User user) throws CharonException, ConflictException;

}

package org.wso2.charon.core.extensions;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.ConflictException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.User;

/**
 * Created by vindula on 9/19/16.
 */
public interface UserManager {
        /***************User Manipulation operations*******************/
    /**
     * Create user with the given user object.
     *
     * @param user User resource to be created in the user store of service provider.
     * @return newly created SCIM User resource sent back to the client in the response.
     */
    public User createUser(User user) throws CharonException, ConflictException;

    /**
     * Obtains the user given the id.
     *
     * @param id
     * @return
     */
    public User getUser(String id) throws CharonException;

    /**
     * Delete the user given the user id.
     *
     * @param userId
     */
    public void deleteUser(String userId) throws NotFoundException, CharonException;
}

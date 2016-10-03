package org.wso2.charon.core.extensions;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.ConflictException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.User;

import java.util.List;

/**
 * This is the interface for UserManager extension.
 * An implementation can plugin their own user manager-(either LDAP based, DB based etc)
 * by implementing this interface and mentioning it in configuration.
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

    public List<User> listUsers() throws CharonException;

    public List<User> listWithPagination(int startIndex, int count);

    public int getUserCount();

    public User updateUser(User validatedUser);
}

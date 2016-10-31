package org.wso2.charon.core.v2.extensions;

import org.wso2.charon.core.v2.utils.codeutils.Node;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.exceptions.ConflictException;
import org.wso2.charon.core.v2.exceptions.NotFoundException;
import org.wso2.charon.core.v2.objects.Group;
import org.wso2.charon.core.v2.objects.User;

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
    public User createUser(User user) throws CharonException, ConflictException, BadRequestException;

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

    public List<User> listUsersWithPagination(int startIndex, int count);

    public int getUserCount();

    public User updateUser(User validatedUser);

    public List<User> filterUsers(Node rootNode);

    public List<User> sortUsers(String sortBy, String sortOrder);

   /* ****************Group manipulation operations********************/

    public Group createGroup(Group group) throws CharonException, ConflictException;

    public Group getGroup(String id);

    public void deleteGroup(String id) throws NotFoundException, CharonException;

    public List<Group> listGroups() throws CharonException;

    public int getGroupCount();

    public List<Group> listGroupsWithPagination(int startIndex, int count);

    public List<Group> filterGroups(Node rootNode);

    public List<Group> sortGroups(String sortByAttributeURI, String sortOrder);

    public Group updateGroup(Group validatedGroup);
}

package org.wso2.charon.core.v2.extensions;

import org.wso2.charon.core.v2.exceptions.*;
import org.wso2.charon.core.v2.utils.codeutils.Node;
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
    public void deleteUser(String userId) throws NotFoundException, CharonException, NotImplementedException;

    public List<User> listUsers() throws CharonException, NotImplementedException;

    public List<User> listUsersWithPagination(int startIndex, int count) throws NotImplementedException;

    public int getUserCount() throws NotImplementedException;

    public User updateUser(User validatedUser) throws NotImplementedException;

    public List<User> filterUsers(Node rootNode) throws NotImplementedException;

    public List<User> sortUsers(String sortBy, String sortOrder) throws NotImplementedException;

   /* ****************Group manipulation operations********************/

    public Group createGroup(Group group) throws CharonException, ConflictException, NotImplementedException;

    public Group getGroup(String id) throws NotImplementedException;

    public void deleteGroup(String id) throws NotFoundException, CharonException, NotImplementedException;

    public List<Group> listGroups() throws CharonException, NotImplementedException;

    public int getGroupCount() throws NotImplementedException;

    public List<Group> listGroupsWithPagination(int startIndex, int count) throws NotImplementedException;

    public List<Group> filterGroups(Node rootNode) throws NotImplementedException;

    public List<Group> sortGroups(String sortByAttributeURI, String sortOrder) throws NotImplementedException;

    public Group updateGroup(Group validatedGroup) throws NotImplementedException;
}

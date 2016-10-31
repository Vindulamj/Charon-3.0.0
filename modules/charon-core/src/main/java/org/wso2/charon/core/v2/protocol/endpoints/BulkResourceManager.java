package org.wso2.charon.core.v2.protocol.endpoints;

import org.wso2.charon.core.v2.extensions.UserManager;
import org.wso2.charon.core.v2.protocol.SCIMResponse;

import java.io.IOException;

/**
 * REST API exposed by Charon-Core to perform bulk operations.
 * Any SCIM service provider can call this API perform bulk operations,
 * based on the HTTP requests received by SCIM Client.
 */
public class BulkResourceManager extends AbstractResourceManager {
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
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

package org.wso2.charon.core.config;

import java.util.ArrayList;

/**
 * This extension interface defines the defines the methods for configuring the charon
 */
public interface Configuration {

    public void setDocumentationURL(String documentationURL);

    public void setPatchSupport(boolean supported);

    public void setBulkSupport(boolean supported, int maxOperations, int maxPayLoadSize);

    public void setFilterSupport(boolean supported, int maxResults);

    public void setChangePasswordSupport(boolean supported);

    public void setETagSupport(boolean supported);

    public void setSortSupport(boolean supported);

    public void setAuthenticationSchemes(ArrayList<Object[]> authenticationSchemes);
}
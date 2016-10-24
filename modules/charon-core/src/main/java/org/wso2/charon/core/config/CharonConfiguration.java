package org.wso2.charon.core.config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains the charon related configurations
 */
public class CharonConfiguration implements Configuration{

    private static CharonConfiguration charonConfiguration = new CharonConfiguration();

    private boolean patchSupport;
    private boolean filterSupport;
    private boolean bulkSupport;
    private boolean sortSupport;
    private boolean eTagSupport;
    private boolean changePasswordSupport;
    private String documentationURL;
    private int maxOperations;
    private int maxPayLoadSize;
    private int maxResults;
    private ArrayList<Object[]> authenticationSchemes = new ArrayList<Object[]>();

    public void setDocumentationURL(String documentationURL){
        this.documentationURL = documentationURL;
    }

    public void setPatchSupport(boolean supported){
        this.patchSupport = supported;
    }

    public void setBulkSupport(boolean supported, int maxOperations, int maxPayLoadSize){
        this.bulkSupport= supported;
        this.maxOperations = maxOperations;
        this.maxPayLoadSize = maxPayLoadSize;
    }

    public void setFilterSupport(boolean supported, int maxResults){
        this.filterSupport = supported;
        this.maxResults = maxResults;
    }

    public void setChangePasswordSupport(boolean supported){
        this.changePasswordSupport = supported;
    }

    public void setETagSupport(boolean supported){
        this.eTagSupport = supported;
    }

    public void setSortSupport(boolean supported){
        this.sortSupport = supported;
    }

    public void setAuthenticationSchemes(ArrayList<Object[]> authenticationSchemes){
        this.authenticationSchemes = authenticationSchemes;
    }

    public HashMap<String,Object> getConfig(){
        HashMap<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(SCIMConfigConstants.DOCUMENTATION_URL, documentationURL);
        configMap.put(SCIMConfigConstants.BULK, bulkSupport);
        configMap.put(SCIMConfigConstants.SORT, sortSupport);
        configMap.put(SCIMConfigConstants.FILTER, filterSupport);
        configMap.put(SCIMConfigConstants.ETAG, eTagSupport);
        configMap.put(SCIMConfigConstants.CHNAGE_PASSWORD, changePasswordSupport);
        configMap.put(SCIMConfigConstants.MAX_OPERATIONS, maxOperations);
        configMap.put(SCIMConfigConstants.MAX_PAYLOAD_SIZE, maxPayLoadSize);
        configMap.put(SCIMConfigConstants.MAX_RESULTS, maxResults);
        configMap.put(SCIMConfigConstants.PATCH, patchSupport);
        configMap.put(SCIMConfigConstants.AUTHENTICATION_SCHEMES, authenticationSchemes);
        return  configMap;
    }

    public static CharonConfiguration getInstance() {
        return charonConfiguration;
    }
}

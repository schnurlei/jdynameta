package de.jdynameta.jdy.jaxrs.rest;

/**
 * Split the path info of the request into namespace, classinfo, filter
 * @author rainer
 *
 */
public interface MetadataPathInfo
{
	
    public String getRepoName();

    public String getClassName();

    public String getFilter();
	

}
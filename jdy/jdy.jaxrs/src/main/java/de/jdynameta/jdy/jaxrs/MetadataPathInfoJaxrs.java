/*
 * Copyright 2015 rainer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.jdy.jaxrs;

import de.jdynameta.jdy.jaxrs.rest.MetadataPathInfo;
import java.util.List;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author rainer
 */
public class MetadataPathInfoJaxrs implements MetadataPathInfo {

    private final String repoName;
    private final String className;
    private final String filter;

    private MetadataPathInfoJaxrs(String repoName, String className, String filter) {
        this.repoName = repoName;
        this.className = className;
        this.filter = filter;
    }

    
    @Override
    public String getRepoName()
    {
        return repoName;
    }

    @Override
    public String getClassName()
    {
        return className;
    }

    @Override
    public String getFilter()
    {
        return filter;
    }
    
    public static MetadataPathInfoJaxrs createFromPath(List<PathSegment> segments, UriInfo uriInfo) {
        
        String repoName;
        String className; 
        String filter = uriInfo.getRequestUri().getQuery();
 
        repoName = segments.get(0).getPath();
        className = segments.get(1).getPath();
        
        return new MetadataPathInfoJaxrs(repoName, className, filter);
    } 
}

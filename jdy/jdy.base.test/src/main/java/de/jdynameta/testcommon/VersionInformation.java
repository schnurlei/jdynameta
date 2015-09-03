/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.testcommon;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionInformation
{
    private static Logger logger = Logger.getLogger(VersionInformation.class.getName());
    private String buildVersion = null;
    private String buildDate = null;

    public VersionInformation() throws Exception
    {
        // get URL to the Main Class
        URL url = VersionInformation.class.getResource("");
        //remove "file:" from path and truncate after "!" 

        String fileName = url.getPath().split("!")[0].replace("file:", "");
        fileName = fileName.replace("%20", " ");

        JarFile jarfile = new JarFile(fileName);
        Manifest manifest = jarfile.getManifest();
        Attributes attrs = (Attributes) manifest.getMainAttributes();

        for (Iterator<Map.Entry<Object, Object>> it = attrs.entrySet().iterator(); it.hasNext();)
        {
            Entry<Object, Object> entry = it.next();

            Object attrKey = entry.getKey();
            Object attrValue = entry.getValue();
        }
        buildVersion = attrs.getValue("Implementation-Version");

        buildDate = attrs.getValue("Build-Date");

    }

    public String getBuildDate()
    {
        return buildDate;
    }

    public String getBuildVersion()
    {
        return buildVersion;
    }

    public static void main(String[] args)
    {
        try
        {
            VersionInformation version = new VersionInformation();
            logger.info("JDynamta Version Information");
            logger.log(Level.INFO, "Version: {0}", version.getBuildVersion());
            logger.log(Level.INFO, "Build: {0}", version.getBuildVersion());
        } catch (Exception e1)
        {
            logger.log(Level.FINE, "No Version available", e1);
            logger.info("No Version available");
        }

    }
}

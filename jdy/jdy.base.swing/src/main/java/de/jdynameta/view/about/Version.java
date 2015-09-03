/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 *	Version.java
 * Created on 12.09.2003
 *
  */
package de.jdynameta.view.about;


/**
 *
 * @author Rainer Schneider
 *
 */
public class Version 
{
	private String projectName;
	private String versionText;
	private String buildText;

	public Version(String aProjectName, String aVersionText, String aBuildText)
	{
		this.projectName = aProjectName;
		this.versionText = aVersionText;
		this.buildText = aBuildText;
	}

	public String getProjectName()
	{
		return projectName;
	}
	
	public String getVersionText()
	{
		return this.versionText;
	}

	public String getBuildText()
	{
		return this.buildText;
	}



}

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
package de.jdynameta.base.metainfo;

/**
 * Describes a 1:n Association from one a master class to a detail class The
 * detail class relation is depicted by an normal ClassInfo. The master class
 * relation is depicted as an {@link ObjectReferenceAttributeInfo} , because the
 * we need the mapping information whether the reference is an key or not null
 * ...
 *
 * @author Rainer
 *
 * @version 04.07.2002
 */
public interface AssociationInfo
{

    /**
     * Class to which the association directs from The Masterclass
     *
     * @return
     */
    public ClassInfo getDetailClass();

    /**
     * Attribute which references to the Master Class in the Detail Class
	 *
     * @return 
     */
    public ObjectReferenceAttributeInfo getMasterClassReference();

    /**
     * descriptive Name of the resource from the view of the Masterclass
	 *
     * @return 
     */
    public String getNameResource();

}

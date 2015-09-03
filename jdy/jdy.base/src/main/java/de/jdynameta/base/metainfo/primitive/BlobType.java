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
package de.jdynameta.base.metainfo.primitive;

import de.jdynameta.base.metainfo.PrimitiveType;

/**
 * Type for binary objects
 *
 * @author Rainer Schneider
 */
public interface BlobType extends PrimitiveType
{

    /**
     * Additional Information about the kind of data the blob holds Normally th
     * type information is provided by the context of the application
     */
    public enum BlobTypeHint
    {

        ANY,
        IMAGE,
        GIF,
        JPEG,
        PNG
    }

    public BlobTypeHint getTypeHint();
}

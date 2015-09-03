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

import java.net.URI;

/**
 * For the primitive types Objects are used. A ByteArray has now default Class
 * like Integer or String
 *
 * @author <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 *
 */
public class BlobByteArrayHolder
{

    private final byte[] bytePayload; // content in base64
    private URI uri; // reference to file which holds the content

    public BlobByteArrayHolder(byte[] aBytePayload)
    {
        super();
        bytePayload = aBytePayload;
    }

    public byte[] getBytes()
    {
        return bytePayload;
    }

    public URI getUri()
    {
        return uri;
    }
}

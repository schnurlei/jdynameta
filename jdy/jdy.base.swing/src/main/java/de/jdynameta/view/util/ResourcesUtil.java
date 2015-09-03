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
package de.jdynameta.view.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ResourcesUtil
{
	private static final int READ_BLOCK = 8192;

	public static File getTempFileForResouce(String resourceName, Class<? extends Object> resourceClass, File tempDir) throws IOException
	{
		File tempFile = new File(tempDir,resourceName);
		tempFile.deleteOnExit();
		InputStream input = resourceClass.getResourceAsStream(resourceName);
		if( input != null) {
			copyInputToOutput(input, tempFile);
		} else {
			throw new IOException("Resource not found: " + resourceName);
		}
		return tempFile;
	}
	
	public static  void copyInputToOutput(InputStream inputStream, File target) throws IOException
	{
		ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
		FileChannel destinationChannel = new FileOutputStream(target).getChannel();

		long transfered = READ_BLOCK;
		long pos = 0;
		while ( transfered > 0) {
			transfered = destinationChannel.transferFrom(inputChannel, pos, READ_BLOCK);
			pos += transfered;
		}
		
		inputChannel.close();
		destinationChannel.close();		
	}
	
}

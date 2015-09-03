/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
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
package de.jdynameta.testcommon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

public class UtilFileLoading 
{
	private static final int READ_BLOCK = 8192;

	private File tempDir;
	
	public UtilFileLoading(File aTempDir)
	{
		assert(aTempDir != null && tempDir.isDirectory());
		this.tempDir = aTempDir;
	}

	public File getTempFile( String relPath, String fileName) throws URISyntaxException, IOException 
	{
		return getTempFile(relPath, fileName, UtilFileLoading.class);
	}
	
	
	/**
	 * Copy resource into a temp file with the same name in the temp directory
	 * @param relPath
	 * @param fileName
	 * @param resourceClass
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public File getTempFile( String relPath, String fileName, Class<? extends Object> resourceClass) throws URISyntaxException, IOException
	{
		InputStream resource = resourceClass.getResourceAsStream( relPath + fileName);
		
		File tempTarget = new File(tempDir, fileName);
		copyInputToOutput(resource, tempTarget);
		
		return tempTarget;
	}
	
	
	public static File getFile( String relPath, String fileName, Class<? extends Object> resourceClass) throws URISyntaxException, FileNotFoundException
	{
		URL resource = resourceClass.getResource( relPath + fileName);
		
		if( resource != null ) {
			return new File(resource.toURI());
		} else {
			throw new FileNotFoundException("File not exits " + relPath + fileName);
		}
		
	}
	

	public static  void copyInputToOutput(InputStream inputStream, File target) throws IOException
	{
            FileChannel destinationChannel;
            try (ReadableByteChannel inputChannel = Channels.newChannel(inputStream))
            {
                destinationChannel = new FileOutputStream(target).getChannel();
                long transfered = READ_BLOCK;
                long pos = 0;
                while ( transfered > 0) {
                    transfered = destinationChannel.transferFrom(inputChannel, pos, READ_BLOCK);
                    pos += transfered;
		}
            }
		destinationChannel.close();		
	}
	
	
	public static long createFileChecksum(byte[] aTextToGetChecksum)
	{
		Checksum checksum = new Adler32();
		checksum.update(aTextToGetChecksum, 0, aTextToGetChecksum.length);
		
		return checksum.getValue();
	}

	
	  private static void doChecksum(String fileName) {

	        try {

	            CheckedInputStream cis = null;
	            long fileSize = 0;
	            try {
	                // Computer Adler-32 checksum
	                cis = new CheckedInputStream(
	                        new FileInputStream(fileName), new Adler32());

	                fileSize = new File(fileName).length();
	               
	            } catch (FileNotFoundException e) {
	                System.err.println("File not found.");
	            }

	            byte[] buf = new byte[128];
	            while(cis.read(buf) >= 0) {
	            }

	            long checksum = cis.getChecksum().getValue();
	            System.out.println(checksum + " " + fileSize + " " + fileName);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }


	    }	
	  
	  
	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.
	public static boolean deleteDir(File dir) 
	{
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
                for (String children1 : children)
                {
                    boolean success = deleteDir(new File(dir, children1));
                    if (!success) {
                        return false;
                    }
                }
	    }
	
	    // The directory is now empty so delete it
	    return dir.delete();
	}	  
}

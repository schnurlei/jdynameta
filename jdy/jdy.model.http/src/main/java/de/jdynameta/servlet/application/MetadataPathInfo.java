package de.jdynameta.servlet.application;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

/**
 * Split the path info of the request into namespace, classinfo, filter
 * @author rainer
 *
 */
public class MetadataPathInfo
{
	private final String repoName;
	private String className;
	private String filter;
	
	public MetadataPathInfo(HttpServletRequest req) throws UnsupportedEncodingException 
	{
		String localName = req.getPathInfo();
		
		String [] callPath = localName.split("/");
		if(  callPath.length < 2 ) {
			this.repoName = null;
		} else {
			this.repoName = callPath[1];
			if ( callPath.length >= 3 ) {
				this.className = callPath[2];
			}
		} 
		
		String queryString = req.getQueryString();

		if( queryString != null) {
			String decoded = URLDecoder.decode(queryString, "UTF-8");
			this.filter = decoded;
		}		
	}
	
	public String getRepoName()
	{
		return repoName;
	}

	public String getClassName()
	{
		return className;
	}

	public String getFilter()
	{
		return filter;
	}


   /**
     * Convert input stream to String
     * @param aInpuStream
     * @return 
     */
    protected static String readFromStream(InputStream aInpuStream) {
        
        BufferedInputStream bufferedInput = null;
        byte[] buffer = new byte[1024];
        
        StringBuilder stringbuffer = new StringBuilder(5000);
        try {
            
            bufferedInput = new BufferedInputStream(aInpuStream);
            
            int bytesRead;
            
            //Keep reading from the file while there is any content
            //when the end of the stream has been reached, -1 is returned
            while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                
                //Process the chunk of bytes read
                //in this case we just construct a String and print it out
                String chunk = new String(buffer, 0, bytesRead);
                stringbuffer.append(chunk);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedInputStream
            try {
                if (bufferedInput != null)
                    bufferedInput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return  stringbuffer.toString();

    }
	
}
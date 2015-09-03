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
package de.jdynameta.json;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

public class JsonTestClient
{
	private String baseUrl;

	public JsonTestClient()
	{

		this.baseUrl = "http://loclhost:8080/servlet";
	}
	
	public JsonTestClient(String aBaseUrl)
	{

		this.baseUrl = aBaseUrl;
	}
	
//	public ObjectList<TypedValueObject> loadValuesFromDb(ClassInfoQuery aFilter) throws PersistentException
//	{
//		String response = sendJsonGetRequest(createUrlForClassInfo(aFilter.getResultInfo()));
//		System.out.println(response);
//		JsonFileReader fileReader = new JsonFileReader();
//		
//		return fileReader.readObjectList( new StringReader(response), aFilter.getResultInfo());
//	}
	
	
	protected String createUrlForClassInfo(ClassInfo aInfo)
	{
		String nameSpacePart = aInfo.getRepoName().equals(MetaModelRepository.META_REPO_NAME) ? "@jdy" : aInfo.getRepoName();
		String className = aInfo.getInternalName();
		return this.baseUrl +"/" + nameSpacePart + "/" + className;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws PersistentException
	 */
//	protected String sendJsonGetRequest(String url) throws PersistentException
//	{
//		HttpClient clientService = new HttpClient();
//		GetMethod get = new GetMethod();
//		try
//		{
//			get.setURI(new URI(url, false));
//		} catch (URIException e)
//		{
//			throw new PersistentException(e.getLocalizedMessage(), e);
//		} catch (NullPointerException e)
//		{
//			throw new PersistentException(e.getLocalizedMessage(), e);
//		}		
//
//		get.setRequestHeader("Content-Type", "application/json");
//		get.setRequestHeader("Accept", "application/json");
//
//		int status;
//		try
//		{
//			status = clientService.executeMethod(get);
//		} catch (HttpException e)
//		{
//			throw new PersistentException(e.getLocalizedMessage(), e);
//		} catch (IOException e)
//		{
//			throw new PersistentException(e.getLocalizedMessage(), e);
//		}
//
//		String responseString;
//		try
//		{
//			InputStream response = get.getResponseBodyAsStream();
//			responseString = readFromStream(response);
//		} catch (IOException e)
//		{
//			throw new PersistentException(e.getLocalizedMessage(), e);
//		}
//
//		// Check response code
//		if (status != HttpStatus.SC_OK) {
//			throw new PersistentException("Received error status " + status);
//		}
//
//		return responseString;
//	}
	
	
	
	
	
//	public static String sendJsonTestRequest(String url) throws NullPointerException, HttpException, IOException
//	{
//		HttpClient clientService = new HttpClient();
//		PostMethod post = new PostMethod();
//		post.setURI(new URI(url, false));		
//		post.setRequestHeader("Content-Type", "application/json");
//		post.setRequestHeader("Accept", "application/json");
//		
//		
////		post.setRequestEntity(new StringRequestEntity(jsonObj.toString(), "application/json", null));
//
//		// execute the POST
//		int status = clientService.executeMethod(post);
//		
//		InputStream response = post.getResponseBodyAsStream();
//		Header[] headers = post.getResponseHeaders();
//		String responseString = readFromStream(response);
//		
//		// Check response code
//		if (status != HttpStatus.SC_OK) {
//			throw new RuntimeException("Received error status " + status);
//		}
//		
//		post.releaseConnection();
//		
//		
//		return responseString;
//		
//	}
	
    /**
     * This method reads contents of a file and print it out
     */
    public static String readFromStream(InputStream aInpuStream) {
        
        BufferedInputStream bufferedInput = null;
        byte[] buffer = new byte[1024];
        
        StringBuffer stringbuffer = new StringBuffer(5000);
        try {
            
            bufferedInput = new BufferedInputStream(aInpuStream);
            
            int bytesRead = 0;
            
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

//	public static void sendYahooTestRequest()
//	{
//		String url = "http://search.yahooapis.com/ImageSearchService/V1/imageSearch?appid=YahooDemo&query=Madonna&output=json&callback=ws_results.obj.array%5B4%5D";
//		
//		try
//		{
//			String body = sendJsonTestRequest(url);
//			// remove Java skript 
//			String jsonBody = body.substring( body.indexOf('(')+1, body.lastIndexOf(')') );
//			System.out.println(jsonBody);
//			
//			JSONParser lParser = new JSONParser (new StringReader(jsonBody));
//			
//			JSONValue value = lParser.nextValue();
//			
//			System.out.println(value.render(true));
//			
//		} catch (NullPointerException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (HttpException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TokenStreamException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RecognitionException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.exit(0);
//	}
	
//	public static void sendJdyTestRequest()
//	{
//		String url = "http://localhost:9090/restserver/JdyServlet/@jdy/NameSpaceInfoModel";
//		
//		try
//		{
//			String body = new JsonTestClient().sendJsonGetRequest(url);
//			System.out.println(body);
//			
//			JSONParser lParser = new JSONParser (new StringReader(body));
//			
//			JSONValue value = lParser.nextValue();
//			
//			System.out.println(value.render(true));
//			
//		} catch (NullPointerException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TokenStreamException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RecognitionException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (PersistentException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//    
	public static void main(String[] args)
	{
//		sendYahooTestRequest();
		String baseUrl = "http://localhost:9090/restserver/JdyServlet";
		
		try
		{
			
			
//			DefaultClassInfoQuery query = new DefaultClassInfoQuery(MetaModelRepository.getSingleton().getNameSpaceModelInfo());
			
//			ObjectList<TypedValueObject> body = new JsonTestClient(baseUrl).loadValuesFromDb(query);
//			System.out.println(body);
			
		
		} catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}	
}

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
package analyse.filter;

import com.sun.jndi.ldap.BerEncoder;
import de.jdynameta.servlet.SearchFilterParser;
import java.io.IOException;
import javax.naming.NamingException;

public class FilterTest 
{
	// 	All contacts with a surname equal to "Smith" or "Johnson".
	private final static String filter1 = "(&(objectCategory=person)(objectClass=contact)(|(sn=Smith)(sn=Johnson)))";
	private final static String filter2 = "(&(objectCategory=person)(objectClass=user)(!cn=andy))";
	
	private final static String filter3 = "(&(objectCategory='person')(objectCategory>='person')(objectCategory<='person'))"; //  exp1 AND exp2 AND exp3"
	private final static String filter4 = "(|(exp1)(exp2)(exp3)) # exp1 OR exp2 OR exp3";
	private final static String filter5 = "(&(!(exp1))(!(exp2))) # NOT exp1 AND NOT exp2";
	
	// examples of search filters
	private final static String filter6 ="(cn=Babs Jensen)";
	private final static String filter7 ="(!(cn=Tim Howes))";
	private final static String filter8 ="(&(objectClass=Person)(|(sn=Jensen)(cn=Babs J*)))";
	private final static String filter9 ="(o=univ*of*mich*)";

	//  escaping mechanism.
	private final static String filter10 ="(o=Parens R Us \\28for all your parenthetical needs\\29)";
	private final static String filter11 = "(cn=*\\2A*)";
	private final static String filter12 ="(filename=C:\5cMyFile)";
	private final static String filter13 ="(bin=\\00\\00\\00\\04)";
	private final static String filter14 ="(sn=Lu\\2a\\28i\\29\\5c)";

	
	
	public static void main(String[] args) 
	{
		String[] hexValues = new String[]{"2a", "28", "29","5c", "00"};
		  
		for (String string : hexValues) {
			int i= Integer.parseInt(string,16);
			System.out.println("Char is:=" + (char)i);
		}
		   
		BerEncoder encoder = new BerEncoder(); 
		
		try {
			SearchFilterParser.encodeFilterString( filter14);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

package de.jdynameta.json;

/** 
 * Helper to build a Json String in the test cases
 * @author rainer
 *
 */
public class JsonStringBuilderHelper
{
	private StringBuilder builder;
	private int tabCounter = 0;
	
	public JsonStringBuilderHelper()
	{
		this.builder = new StringBuilder();
	}


	public void endAssoc()
	{
		tabCounter--;
		addTabs();
		this.builder.append("  } ]").append("\n");
	}


	public void nextAssocObj()
	{
		addTabs();
		this.builder.append("}, {").append("\n");
	}

	public void addAssoc(String key)
	{
		this.builder.append("  \"").append(key).append("\" : [ {").append("\n");
		tabCounter++;
	}

	public void addObject(String key)
	{
		addTabs();
		this.builder.append("  \"").append(key).append("\" : {").append("\n");
		tabCounter++;
	}

	public void endObject()
	{
		tabCounter--;
		addTabs();
		this.builder.append("  ").append("},").append("\n");
	}
	
	public void endObjectLast()
	{
		tabCounter--;
		addTabs();
		this.builder.append("  ").append("}").append("\n");
	}

	
	@Override
	public String toString()
	{
		return builder.toString();
	}
	
	public void addString(String key, String value) 
	{
		addTabs();
		addStringEntry(key, value);
		this.builder.append(",").append("\n");
	}

	private void addTabs()
	{
		for (int i = 0; i < tabCounter; i++)
		{
			this.builder.append("  ");
		}
	}

	public void addLastString(String key, String value) 
	{
		addTabs();
		addStringEntry(key, value);
		this.builder.append("\n");
	}

	private void addStringEntry(String key, String value) 
	{
		this.builder.append("  \"").append(key).append("\"");
		this.builder.append(" ").append(":").append(" ");
		this.builder.append("\"").append(value).append("\"");
	}

	
	public void addValue(String key, String value) 
	{
		this.builder.append("  \"").append(key).append("\"");
		this.builder.append(" ").append(":").append(" ");
		this.builder.append(value);
		this.builder.append(",").append("\n");
	}
	
	public void lineEntry(String key, String value) 
	{
		this.builder.append("\"").append(key).append("\"");
		this.builder.append(" ").append(":").append(" ");
		this.builder.append("\"").append(value).append("\"");
	}
	
	public void listStart()
	{
		this.builder.append("[ {").append("\n");
	}
	public void listEnd()
	{
		this.builder.append("} ]");
	}
}
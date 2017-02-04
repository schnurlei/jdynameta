/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.jdy.model.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 * @author rainer
 */
public class AnalyseJacksonJsonProcessing
{
	final String jsonString = "{\"stringValue\":\"StringTest\",\"booleanValue\":true,\"longValue\":737,\"decimalValue\":554.789,\"textValue\":\"tttzzz\",\"MultipleEntries\":[{\"firstArrayValue\":\"firstArrayEntry\"},{\"secondArrayValue\":\"secondArrayEntry\"}]}";

	@Test
	public void readJsonData() throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		StringReader reader = new StringReader(jsonString);
		JsonNode rootNode = mapper.readValue(reader, JsonNode.class); 
		
		Assert.assertEquals("StringTest", ((ObjectNode)rootNode).findValue("stringValue").asText());

	}
	
	@Test
	public void writeJsonData() throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode rootNode = mapper.createObjectNode(); // will be of type ObjectNode
		((ObjectNode) rootNode).put("stringValue", "StringTest");
		((ObjectNode) rootNode).put("booleanValue", Boolean.TRUE);
		((ObjectNode) rootNode).put("longValue", new Long(737));
		((ObjectNode) rootNode).put("decimalValue", new BigDecimal("554.789"));
		((ObjectNode) rootNode).put("textValue", "tttzzz");
		
		ArrayNode arrayNode = mapper.createArrayNode();
		ObjectNode firstArrayEntry = arrayNode.addObject();
		firstArrayEntry.put("firstArrayValue", "firstArrayEntry");
		ObjectNode secondArrayEntry = arrayNode.addObject();
		secondArrayEntry.put("secondArrayValue", "secondArrayEntry");

		((ObjectNode) rootNode).put("MultipleEntries", arrayNode);

		
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rootNode);
		System.out.println(writer.toString());
		
		Assert.assertEquals(jsonString, writer.toString());
	}

}

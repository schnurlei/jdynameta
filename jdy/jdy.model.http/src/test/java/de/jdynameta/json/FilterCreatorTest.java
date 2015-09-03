package de.jdynameta.json;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Assert;

import org.junit.Test;

import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.QueryCreator;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.json.JsonCompactFileReader.GeneratedValueCreator;
import de.jdynameta.json.client.JsonHttpObjectReader;
import de.jdynameta.metamodel.filter.AppFilterExpr;
import de.jdynameta.metamodel.filter.AppQuery;
import de.jdynameta.metamodel.filter.FilterCreator;
import de.jdynameta.metamodel.filter.FilterRepository;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.ApplicationObjImpl;

public class FilterCreatorTest 
{
	
	@Test
	public void testCreateAppFilter() throws JdyPersistentException, TransformerConfigurationException, ObjectCreationException
	{
		ClassRepository plantShop = PlantShopRepository.createPlantShopRepository();
		ClassInfo plantType = plantShop.getClassForName(PlantShopRepository.Type.Plant.name());
		
		// create filter
		DefaultClassInfoQuery query = QueryCreator.start(plantType)
				.or()
					.equal("BotanicName", "Iris")
					.and()
						.greater("HeigthInCm", new Long(30L))
						.less("HeigthInCm", new Long(100L))
					.end()
				.end().query();
		
		// convert filter to applcation filter
		FilterCreator creator = new FilterCreator();
		AppQuery appQuery = creator.createAppFilter(query);

		// write filter expressionto json string
		StringWriter writerOut = new StringWriter();
		HashMap<String, String> att2AbbrMap = writExpreToJsonString(appQuery, writerOut);
		System.out.println(writerOut.toString());

		GeneratedValueCreator valueGenerator = new GeneratedValueCreator()
		{
			public long nextValue = 0; 
			@Override
			public Object createValue(ClassInfo aClassInfo, AttributeInfo aAttrInfo)
			{
				return new Long(nextValue++);
			}
			
			@Override
			public boolean canGenerateValue(ClassInfo aClassInfo,	AttributeInfo aAttrInfo)
			{
				return (aAttrInfo instanceof PrimitiveAttributeInfo) 
						&& ((PrimitiveAttributeInfo) aAttrInfo).getType() instanceof LongType
						&& ((PrimitiveAttributeInfo) aAttrInfo).isGenerated();
			}
		};
		// convert filter string back to expr
		JsonCompactFileReader reader = new JsonCompactFileReader(att2AbbrMap, FilterRepository.getSingleton().getRepoName(), valueGenerator );
		ObjectList<ApplicationObj> result = reader.readObjectList(new StringReader(writerOut.toString()), FilterRepository.getSingleton().getInfoForType(FilterRepository.TypeName.AppFilterExpr));

		FilterTransformator  transformator = new FilterTransformator(FilterRepository.NAME_CREATOR);
		ObjectList<ReflectionChangeableValueObject> convertedList = JsonHttpObjectReader.convertValObjList(result, transformator);

		AppFilterExpr expr =  (AppFilterExpr) convertedList.get(0);
		AppQuery newAppQuery = new AppQuery();
		newAppQuery.setExpr(expr);
		newAppQuery.setRepoName(plantShop.getRepoName());
		newAppQuery.setClassName(plantType.getInternalName());
		
		ClassInfoQuery resultQuery = creator.createMetaFilter(newAppQuery, plantShop);
		System.out.println(resultQuery);

	}

	private HashMap<String, String> writExpreToJsonString(AppQuery appQuery,
			StringWriter writerOut) throws TransformerConfigurationException,
			JdyPersistentException
	{
		ObjectList<? extends TypedValueObject> queryColl = new ChangeableObjectList<TypedValueObject>(appQuery.getExpr());
		HashMap<String, String> att2AbbrMap = new HashMap<String, String>();
		att2AbbrMap.put("repoName", "rn");
		att2AbbrMap.put("className", "cn");
		att2AbbrMap.put("expr", "ex");
		att2AbbrMap.put("orSubExpr", "ose");
		att2AbbrMap.put("andSubExpr", "ase");
		att2AbbrMap.put("attrName", "an");
		att2AbbrMap.put("operator", "op");
		att2AbbrMap.put("isNotEqual", "ne");
		att2AbbrMap.put("isAlsoEqual", "ae");
		att2AbbrMap.put("longVal", "lv");
		att2AbbrMap.put("textVal", "tv");
		new JsonCompactFileWriter(new JsonFileWriter.WriteAllStrategy(), true, att2AbbrMap)
			.writeObjectList(writerOut, appQuery.getClassInfo(), queryColl, PersistentOperation.Operation.READ);
		return att2AbbrMap;
	}
	
	@Test
	public void readCompactfilterExpression() throws JdyPersistentException
	{
		String aFilterExpr = "[{\"@t\":\"FEA\",\"ase\":[{\"@t\":\"OEX\",\"an\":\"HeigthInCm\",\"op\":{\"@t\":\"FPG\",\"ae\":false},\"lv\":100}]}]";

		GeneratedValueCreator valueGenerator = new GeneratedValueCreator()
		{
			public long nextValue = 0; 
			@Override
			public Object createValue(ClassInfo aClassInfo, AttributeInfo aAttrInfo)
			{
				return new Long(nextValue++);
			}
			
			@Override
			public boolean canGenerateValue(ClassInfo aClassInfo,	AttributeInfo aAttrInfo)
			{
				return (aAttrInfo instanceof PrimitiveAttributeInfo) 
						&& ((PrimitiveAttributeInfo) aAttrInfo).getType() instanceof LongType
						&& ((PrimitiveAttributeInfo) aAttrInfo).isGenerated();
			}
		};		
		
		
		HashMap<String, String> att2AbbrMap = FilterCreator.createAbbreviationMap();
		JsonCompactFileReader reader = new JsonCompactFileReader(att2AbbrMap, FilterRepository.getSingleton().getRepoName(), valueGenerator );
		ObjectList<ApplicationObj> result = reader.readObjectList(new StringReader(aFilterExpr), FilterRepository.getSingleton().getInfoForType(FilterRepository.TypeName.AppFilterExpr));
		ApplicationObj exprObj = result.get(0);
		
		ObjectList<? extends TypedValueObject> exprList = exprObj.getValues("andSubExpr");
		ApplicationObj operatorExpr = (ApplicationObj) exprList.get(0);
		Assert.assertEquals("HeigthInCm", operatorExpr.getValue("attrName"));
		Assert.assertEquals(new Long(100), operatorExpr.getValue("longVal"));
		Assert.assertEquals(Boolean.FALSE, ((ApplicationObj) operatorExpr.getValue("operator")).getValue("isAlsoEqual"));
		
	}
	
	
	
	@SuppressWarnings("serial")
	private class FilterTransformator extends AbstractReflectionCreator<ReflectionChangeableValueObject> 
		implements ObjectTransformator<ValueObject, ReflectionChangeableValueObject> {


		public FilterTransformator(ClassNameCreator aNameCreator)
		{
			super(aNameCreator);
		}

		@Override
		public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,
				ValueObject aObjectToTransform)
		{
			return new TypedWrappedValueObject(aObjectToTransform, aClassinfo);
		}

		@Override
		protected ReflectionChangeableValueObject createProxyObjectFor(
				TypedValueObject aObjToHandle)
		{
			return null;
		}


		@Override
		protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
				ReflectionChangeableValueObject aObjoSetVals,
				TypedValueObject aObjToGetVals) throws ObjectCreationException
		{
			
		}
	}
		
}

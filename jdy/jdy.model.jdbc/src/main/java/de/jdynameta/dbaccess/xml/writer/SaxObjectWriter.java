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
package de.jdynameta.dbaccess.xml.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.transform.TransformerConfigurationException;

import org.xml.sax.SAXException;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.dbaccess.xml.reader.SaxXmlFileReader;

/**
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
@SuppressWarnings("serial")
public class SaxObjectWriter implements ObjectWriter
{
	private File baseDirectory;
	
	
	/**
	 * @param aBaseDirectory
	 */
	public SaxObjectWriter(File aBaseDirectory)
	{
		super();
		this.baseDirectory = aBaseDirectory;
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.dbaccess.base.writer.ObjectWriter#deleteObjectInDb(de.comafra.model.value.ValueObject, de.comafra.model.metainfo.ClassInfo)
	 */
	public void deleteObjectInDb(ValueObject aObjToDelete, ClassInfo aInfo)
			throws JdyPersistentException
	{
		SaxXmlFileReader fileReader = new SaxXmlFileReader();
		
		File modelFile = new File(baseDirectory,aInfo.getExternalName()+".xml");
		

		try
		{
			ObjectList<ValueObject> objectList;
			if( modelFile.exists()) {
				objectList = fileReader.readObjectList( new FileInputStream(modelFile),aInfo);
			} else {
				objectList = new DefaultObjectList<ValueObject>();
			}
			ChangeableObjectList<ValueObject> newList = new ChangeableObjectList<ValueObject>(objectList);
			for( Iterator<ValueObject> valueModelIter = objectList.iterator(); valueModelIter.hasNext(); ) {

				ValueObject curModel = valueModelIter.next();
				if( !areValueObjectsEqual(aInfo, aObjToDelete, curModel)){
					newList.addObject(curModel);
				}
			}

			SaxXmlFileWriter writer = new SaxXmlFileWriter();
			PrintWriter outWriter = new PrintWriter( new FileOutputStream(modelFile));
			writer.writeObjectList(outWriter ,aInfo,newList );
			outWriter.close();

		} catch (FileNotFoundException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (TransformerConfigurationException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (ProxyResolveException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (JdyPersistentException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (SAXException excp)
		{
			throw new JdyPersistentException(excp);
		}
		
	}


	public TypedValueObject insertObjectInDb(ValueObject aObjToInsert, ClassInfo aInfo)
			throws JdyPersistentException
	{
		SaxXmlFileReader fileReader = new SaxXmlFileReader();
		
		File modelFile = new File(baseDirectory,aInfo.getExternalName()+".xml");
		
		try
		{
			
			ObjectList<ValueObject> objectList;
			if( modelFile.exists()) {
				objectList = fileReader.readObjectList( new FileInputStream(modelFile),aInfo);
			} else {
				objectList = new DefaultObjectList<ValueObject>();
			}
			for( Iterator<ValueObject> valueModelIter = objectList.iterator(); valueModelIter.hasNext(); ) {

				ValueObject curModel = valueModelIter.next();
				if( areValueObjectsEqual(aInfo, aObjToInsert, curModel)){
					throw new JdyPersistentException("Object already exist");
				}
			}
			
			ChangeableObjectList<ValueObject> newList = new ChangeableObjectList<ValueObject>(objectList);
			newList.addObject(aObjToInsert);
			
			SaxXmlFileWriter writer = new SaxXmlFileWriter();
			PrintWriter outWriter = new PrintWriter( new FileOutputStream(modelFile));
			writer.writeObjectList(outWriter ,aInfo,newList );
			outWriter.close();
			
		} catch (ProxyResolveException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (FileNotFoundException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (TransformerConfigurationException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (SAXException excp)
		{
			throw new JdyPersistentException(excp);
		}
		return new TypedWrappedValueObject(aObjToInsert, aInfo);	
		
	}
	
	private static boolean areValueObjectsEqual(ClassInfo aInfo, final ValueObject object1, final ValueObject object2) throws JdyPersistentException
	{
		boolean isEqual = true;
		
		if( object1 == null && object2 == null) {
			isEqual = true;
		} else if ( (object1 == null && object2 != null) 
					|| (object1 != null && object2 == null)) {
			isEqual = false;
		} else {
			try
			{
				ObjectCompareAttrHandler compareHandler = new ObjectCompareAttrHandler();
				compareHandler.isEqual = true;
				
				for (AttributeInfo curAttr : aInfo.getAttributeInfoIterator())
				{
					if(curAttr.isKey()) {

						compareHandler.object1 = object1;
						compareHandler.object2 = object2;
						curAttr.handleAttribute(compareHandler, null);
						isEqual = compareHandler.isEqual;
						if(!isEqual) {
							break;
						}
					}
				}
			} catch (JdyPersistentException excp)
			{
				throw excp;
			}
		}
		
		return isEqual;
	}
	
	private static class ObjectCompareAttrHandler implements AttributeHandler
	{
		private boolean isEqual;
		private ValueObject object1;
		private ValueObject object2;
		
		public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
				ValueObject aObjToHandle) throws JdyPersistentException
		{
			isEqual = areValueObjectsEqual( aInfo.getReferencedClass()
											, (ValueObject) object1.getValue(aInfo)
											, (ValueObject) object2.getValue(aInfo));
		}

		public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,
				Object aObjToHandle) throws JdyPersistentException
		{
			Object val1 = object1.getValue(aInfo);
			Object val2 = object2.getValue(aInfo);

			if( val1 == null && val2 == null) {
				isEqual = true;
			} else if ( (val1 == null && val2 != null) 
						|| (val1 != null && val2 == null)) {
				isEqual = false;
			} else {
				isEqual = val1.equals(val2);
			}
		}				
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.dbaccess.base.writer.ObjectWriter#updateObjectToDb(de.comafra.model.value.ValueObject, de.comafra.model.metainfo.ClassInfo)
	 */
	public void updateObjectToDb(ValueObject aObjToModify, ClassInfo aInfo)
			throws JdyPersistentException
	{

		SaxXmlFileReader fileReader = new SaxXmlFileReader();
		
		File modelFile = new File(baseDirectory,aInfo.getExternalName()+".xml");
		
		try
		{
			ObjectList<ValueObject> objectList;
			if( modelFile.exists()) {
				objectList = fileReader.readObjectList( new FileInputStream(modelFile),aInfo);
			} else {
				objectList = new DefaultObjectList<ValueObject>();
			}

			ChangeableObjectList<ValueObject> newList = new ChangeableObjectList<ValueObject>(objectList);
			for( Iterator<ValueObject> valueModelIter = objectList.iterator(); valueModelIter.hasNext(); ) {

				ValueObject curModel = valueModelIter.next();
				if( areValueObjectsEqual(aInfo, aObjToModify, curModel)){
					newList.addObject(aObjToModify);
				} else {
					newList.addObject(curModel);
				}
			}

			SaxXmlFileWriter writer = new SaxXmlFileWriter();
			PrintWriter outWriter = new PrintWriter( new FileOutputStream(modelFile));
			writer.writeObjectList(outWriter ,aInfo,newList );
			outWriter.close();
		} catch (FileNotFoundException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (TransformerConfigurationException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (ProxyResolveException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (JdyPersistentException excp)
		{
			throw new JdyPersistentException(excp);
		} catch (SAXException excp)
		{
			throw new JdyPersistentException(excp);
		}
	}
}

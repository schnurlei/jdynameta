package de.jdynameta.metamodel.application;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.metainfo.impl.JdyAbstractAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyAssociationModel;
import de.jdynameta.base.metainfo.impl.JdyBlobType;
import de.jdynameta.base.metainfo.impl.JdyBooleanType;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyDecimalType;
import de.jdynameta.base.metainfo.impl.JdyFloatType;
import de.jdynameta.base.metainfo.impl.JdyLongType;
import de.jdynameta.base.metainfo.impl.JdyObjectReferenceModel;
import de.jdynameta.base.metainfo.impl.JdyPrimitiveAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
import de.jdynameta.base.metainfo.impl.JdyTextType;
import de.jdynameta.base.metainfo.impl.JdyTimeStampType;
import de.jdynameta.base.metainfo.impl.JdyVarCharType;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TextType.TypeHint;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.EditableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.persistence.objectlist.ObjectListSortWrapper;

public class MetaRepositoryCreator
{
	private DbAccessConnection<ValueObject, GenericValueObjectImpl> metaCon;


	public MetaRepositoryCreator(DbAccessConnection<ValueObject, GenericValueObjectImpl> aMetaCon)
	{
		super();
		this.metaCon = aMetaCon;
	}

	public ClassRepository createMetaRepository(AppRepository appRep)
	{
		JdyRepositoryModel metaRepo = new JdyRepositoryModel(appRep.getApplicationName());
		
		// build base classes
		for (AppClassInfo curClass : appRep.getClassesColl())
		{
			addClassToMetaRepo(metaRepo, curClass);
		} 

		// add Attributes 
		for (AppClassInfo curClass : appRep.getClassesColl())
		{
			buildAttrForMetaRepo(metaRepo, curClass);
		} 

		for (AppClassInfo curClass : appRep.getClassesColl())
		{
			buildAssocsForMetaRepo(metaRepo, curClass);
		} 

		for (AppClassInfo curClass : appRep.getClassesColl())
		{
			buildSubclassesForMetaRepo(metaRepo, curClass);
		} 
		
		return metaRepo;
	}

	private void addClassToMetaRepo(JdyRepositoryModel metaRepo, AppClassInfo curClass)
	{
		JdyClassInfoModel metaClass = metaRepo.addClassInfo(curClass.getInternalName());
		metaClass.setAbstract(curClass.isAbstract());
		metaClass.setExternalName(curClass.getInternalName());
		metaClass.setShortName(curClass.getInternalName());
		metaClass.setNameSpace(curClass.getNameSpace());
	}
	
	private void buildAttrForMetaRepo(ClassRepository metaRepo, AppClassInfo anAppClassInfo)
	{
		JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(anAppClassInfo.getInternalName());
		
		for (AppAttribute appAttr : getAttributesColl(anAppClassInfo))
		{
			JdyAbstractAttributeModel metaAttr = null; 
			if(appAttr instanceof AppPrimitiveAttribute) {
				PrimitiveType metaType = createMetaRepoType((AppPrimitiveAttribute) appAttr); 
				metaAttr = new JdyPrimitiveAttributeModel(metaType
							, appAttr.getInternalName(),appAttr.getInternalName()
							,appAttr.isKey(), appAttr.isNotNull());
			} else {
				AppObjectReference appRef = (AppObjectReference) appAttr;
				JdyClassInfoModel refClass = getMetaRepoRefClass((AppObjectReference) appAttr, metaRepo);

				metaAttr = new JdyObjectReferenceModel(refClass
						, appAttr.getInternalName(),appAttr.getInternalName()
						,appAttr.isKey(), appAttr.isNotNull());
				((JdyObjectReferenceModel)metaAttr).setDependent(appRef.isDependent());
				((JdyObjectReferenceModel)metaAttr).setIsInAssociation(appRef.isInAssociation());
			}

			metaAttr.setGenerated(appAttr.isGenerated());
			metaAttr.setGroup(appAttr.getAttrGroup());
			
			metaClass.addAttributeInfo(metaAttr);
		}
		
	}
	
	private void buildAssocsForMetaRepo(ClassRepository metaRepo, AppClassInfo annAppClass)
	{
		JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(annAppClass.getInternalName());

		for (Object appAssocObj : annAppClass.getAssociationsColl())
		{
			AppAssociation appAssoc = (AppAssociation) appAssocObj;
			
			JdyClassInfoModel masterClass = (JdyClassInfoModel) metaRepo.getClassForName(appAssoc.getMasterClassReference().getMasterclass().getInternalName()); 

			JdyObjectReferenceModel metaMasterClassRef = (JdyObjectReferenceModel) masterClass.getAttributeInfoForExternalName(appAssoc.getMasterClassReference().getInternalName());
			JdyClassInfoModel metaDetailClass = (JdyClassInfoModel) metaRepo.getClassForName(appAssoc.getMasterClassReference().getMasterclass().getInternalName()); 
			String metaAssocName = appAssoc.getNameResource();			
			JdyAssociationModel metaAssoc = new JdyAssociationModel(metaMasterClassRef, metaDetailClass, metaAssocName);
			metaClass.addAssociation(metaAssoc);
		}
	}
	
	private void buildSubclassesForMetaRepo(ClassRepository metaRepo, AppClassInfo anAppClass)
	{
		JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(anAppClass.getInternalName());
		AppClassInfo appSuper = anAppClass.getSuperclass();
		if( appSuper != null) {
			JdyClassInfoModel metaSuper = (JdyClassInfoModel) metaRepo.getClassForName(appSuper.getInternalName());
			metaSuper.addSubclass(metaClass);
		}
		
	}
	
	public AppClassInfo getDetailClass(AppAssociation anAssoc)
	{
		return (anAssoc.getMasterClassReference() == null) ? null : anAssoc.getMasterClassReference().getMasterclass();
	}
	
	
	private JdyClassInfoModel getMetaRepoRefClass(AppObjectReference anAppRef, ClassRepository metaRepo)
	{	
		return (JdyClassInfoModel) metaRepo.getClassForName(anAppRef.getReferencedClass().getInternalName());
	}
	
	private PrimitiveType createMetaRepoType(AppPrimitiveAttribute anAppPrim)
	{	
		PrimitiveType metaType = null; 
		if(anAppPrim instanceof AppBlobType) {
			metaType = new JdyBlobType();
		} else if(anAppPrim instanceof AppBooleanType) {
			metaType = new JdyBooleanType();
		} else if(anAppPrim instanceof AppDecimalType) {
			AppDecimalType decType = (AppDecimalType)anAppPrim;
			JdyDecimalType decimalType = new JdyDecimalType(decType.getMinValue(), decType.getMaxValue(), (int)decType.getScaleValue().longValue());
			ObjectList<AppDecimalDomainModel> domainVals = decType.getDomainValuesColl();
			if (domainVals != null && domainVals.size()>0) {
				for(AppDecimalDomainModel apDomain: domainVals ) {
					decimalType.getDomainValues().add(new DomValue<BigDecimal>(apDomain.getDbValue(), apDomain.getRepresentation()));
				}
			}
			metaType = decimalType;
		} else if(anAppPrim instanceof AppFloatType) {
			metaType = new JdyFloatType();
		} else if(anAppPrim instanceof AppLongType) {
			AppLongType longType = (AppLongType)anAppPrim;
			JdyLongType metaLongType = new JdyLongType(longType.getMinValueValue(), longType.getMaxValueValue());
			ObjectList<AppLongDomainModel> domainVals = ((AppLongType)anAppPrim).getDomainValuesColl();
			if (domainVals != null && domainVals.size()>0) {
				for(AppLongDomainModel apDomain: domainVals ) {
					metaLongType.getDomainValues().add(new DomValue<Long>(apDomain.getDbValue(), apDomain.getRepresentation()));
				}
			}
			metaType = metaLongType;
		} else if(anAppPrim instanceof AppTextType) {
			AppTextType textType = (AppTextType)anAppPrim;
			TypeHint hint = (textType.getTypeHint() != null && !textType.getTypeHint().trim().isEmpty()) ? TypeHint.valueOf(textType.getTypeHint()): null;
			JdyTextType metaStringType = new JdyTextType((int) textType.getLength(), hint);
			ObjectList<AppStringDomainModel> domainVals = ((AppTextType)anAppPrim).getDomainValuesColl();
			if (domainVals != null && domainVals.size()>0) {
				for(AppStringDomainModel apDomain: domainVals ) {
					metaStringType.getDomainValues().add(new DomValue<String>(apDomain.getDbValue(), apDomain.getRepresentation()));
				}
			}
			metaType = metaStringType;
			
		} else if(anAppPrim instanceof AppTimestampType) {
			metaType = new JdyTimeStampType();
		} else if(anAppPrim instanceof AppVarCharType) {
			AppVarCharType textType = (AppVarCharType)anAppPrim;
			VarCharType.TextMimeType mimeType = (textType.getMimeType() != null && !textType.getMimeType().trim().isEmpty()) ? VarCharType.TextMimeType.valueOf(textType.getMimeType()): null;
			metaType = new JdyVarCharType((int) textType.getLength(), textType.isClob(), mimeType);
		}
		return metaType;
	}
	
	private GenericValueObjectImpl insertToDb(GenericValueObjectImpl objToInsert) throws JdyPersistentException 
	{
		return this.metaCon.insertObjectInDb(objToInsert, objToInsert.getClassInfo());
	}

	private void updateToDb(GenericValueObjectImpl objToUpdate) throws JdyPersistentException 
	{
		this.metaCon.updateObjectToDb(objToUpdate, objToUpdate.getClassInfo());
	}

	
	public AppRepository createAppRepository(ClassRepository  metaRep) throws JdyPersistentException
	{
		AppRepository appRepo = new AppRepository();
		appRepo.setApplicationName(metaRep.getRepoName());
		appRepo.setName(metaRep.getRepoName());
		appRepo.setAppVersion(1L);
		appRepo = (AppRepository) insertToDb(appRepo);
		
		ChangeableObjectList<AppClassInfo> classesColl = new ChangeableObjectList<AppClassInfo>();
		appRepo.setClassesColl(classesColl);
		
		// build base classes
		for (ClassInfo curClass : metaRep.getAllClassInfosIter())
		{
			addClassToAppRepo(appRepo, classesColl, curClass);
		} 

		// add Attributes 
		for (ClassInfo curClass : metaRep.getAllClassInfosIter())
		{
			buildAttrForAppRepo(appRepo, curClass);
		} 

		for (ClassInfo curClass : metaRep.getAllClassInfosIter())
		{
			buildAssocsForAppRepo(appRepo, curClass);
		} 
		
		for (ClassInfo curClass : metaRep.getAllClassInfosIter())
		{
			buildSubclassesForAppRepo(appRepo, curClass);
		} 

		
		return appRepo;
	}

	private void addClassToAppRepo(AppRepository anAppRepo, ChangeableObjectList<AppClassInfo> aClassesColl, ClassInfo curClass) throws JdyPersistentException
	{
		AppClassInfo appClass = new AppClassInfo();
		appClass.setIsAbstract(curClass.isAbstract());
		appClass.setName(curClass.getInternalName());
		appClass.setInternalName(curClass.getInternalName());
		appClass.setRepository(anAppRepo);
		appClass.setNameSpace(curClass.getNameSpace());
		ChangeableObjectList<AppClassInfo> subClassColl = new ChangeableObjectList<AppClassInfo>();
		appClass.setSubclassesColl(subClassColl);
		
		appClass = (AppClassInfo) insertToDb(appClass);
		aClassesColl.addObject(appClass);
	}

	private void buildAttrForAppRepo(AppRepository aAppRepo, ClassInfo aMetaClass) throws JdyPersistentException
	{
		AppClassInfo appClass = getClassFromAppRepo(aAppRepo, aMetaClass);
		ChangeableObjectList<AppAttribute> attrColl = new ChangeableObjectList<AppAttribute>();
		appClass.setAttributesColl(attrColl);
		long i = 0;
		
		for (AttributeInfo metaAttr : aMetaClass.getAttributeInfoIterator())
		{
			if( aMetaClass.isSubclassAttribute(metaAttr)) {
				AppAttribute appAttr = null; 
				if(metaAttr instanceof PrimitiveAttributeInfo) {
					appAttr = createAppRepoAttr((PrimitiveAttributeInfo) metaAttr); 
				} else {
					ObjectReferenceAttributeInfo metaRef= (ObjectReferenceAttributeInfo)metaAttr;
					AppClassInfo refClass = getClassFromAppRepo(aAppRepo, metaRef.getReferencedClass());
					appAttr = new AppObjectReference();
					((AppObjectReference)appAttr).setIsDependent(metaRef.isDependent());
					((AppObjectReference)appAttr).setIsInAssociation(metaRef.isInAssociation());
					((AppObjectReference)appAttr).setReferencedClass(refClass);
				}
				appAttr.setName(metaAttr.getInternalName());
				appAttr.setInternalName(metaAttr.getInternalName());
				appAttr.setIsKey(metaAttr.isKey());
				appAttr.setIsNotNull(metaAttr.isNotNull());
				appAttr.setIsGenerated(metaAttr.isGenerated());
				appAttr.setAttrGroup(metaAttr.getAttrGroup());
				appAttr.setPos(i++);
				appAttr.setMasterclass(appClass);
				
				appAttr = (AppAttribute) insertToDb(appAttr);
				attrColl.addObject(appAttr);
				if(metaAttr instanceof PrimitiveAttributeInfo) {
					createDomainValues((PrimitiveAttributeInfo) metaAttr, appAttr);
				}
			}
		}
	}
	
	private AppClassInfo getClassFromAppRepo(AppRepository aAppRepo, ClassInfo aMetaClass)
	{
		AppClassInfo foundedClass = null;
		
		for (AppClassInfo appClass : aAppRepo.getClassesColl())
		{
			if( appClass.getInternalName().equals(aMetaClass.getInternalName())) 
			{
				foundedClass= appClass;
			}
		}
		
		return foundedClass;
	}
	
	private void createDomainValues(PrimitiveAttributeInfo aMetaPrim, AppAttribute appAttr) throws JdyPersistentException
	{
		if( ((PrimitiveAttributeInfo)aMetaPrim).getType() instanceof TextType) {

			if ( ((TextType)aMetaPrim.getType()).getDomainValues() != null 
					&& ((TextType)aMetaPrim.getType()).getDomainValues().size() > 0) 
			{

				AppTextType appTextType = (AppTextType) appAttr;
				ChangeableObjectList<AppStringDomainModel> attrColl = new ChangeableObjectList<AppStringDomainModel>();
				appTextType.setDomainValuesColl(attrColl);
				for( DbDomainValue<String> domainVal : ((TextType)aMetaPrim.getType()).getDomainValues() ) 
				{
					AppStringDomainModel appDomainVal = new AppStringDomainModel();
					appDomainVal.setDbValue(domainVal.getDbValue());
					appDomainVal.setRepresentation(domainVal.getRepresentation());
					appDomainVal.setType(appTextType);
					insertToDb(appDomainVal);
				}
			}
		} else if(aMetaPrim.getType() instanceof LongType) {
			if ( ((LongType)aMetaPrim.getType()).getDomainValues() != null 
					&& ((LongType)aMetaPrim.getType()).getDomainValues().size() > 0) 
			{

				AppLongType appType = (AppLongType) appAttr;
				ChangeableObjectList<AppStringDomainModel> attrColl = new ChangeableObjectList<AppStringDomainModel>();
				appType.setDomainValuesColl(attrColl);
				for( DbDomainValue<Long> domainVal : ((LongType)aMetaPrim.getType()).getDomainValues() ) 
				{
					AppLongDomainModel appDomainVal = new AppLongDomainModel();
					appDomainVal.setDbValue(domainVal.getDbValue());
					appDomainVal.setRepresentation(domainVal.getRepresentation());
					appDomainVal.setType(appType);
					insertToDb(appDomainVal);
				}
			}
		} else if(aMetaPrim.getType() instanceof CurrencyType) {
			if ( ((CurrencyType)aMetaPrim.getType()).getDomainValues() != null 
					&& ((CurrencyType)aMetaPrim.getType()).getDomainValues().size() > 0) 
			{

				AppDecimalType appType = (AppDecimalType) appAttr;
				ChangeableObjectList<AppStringDomainModel> attrColl = new ChangeableObjectList<AppStringDomainModel>();
				appType.setDomainValuesColl(attrColl);
				for( DbDomainValue<BigDecimal> domainVal : ((CurrencyType)aMetaPrim.getType()).getDomainValues() ) 
				{
					AppDecimalDomainModel appDomainVal = new AppDecimalDomainModel();
					appDomainVal.setDbValue(domainVal.getDbValue());
					appDomainVal.setRepresentation(domainVal.getRepresentation());
					appDomainVal.setType(appType);
					insertToDb(appDomainVal);
				}
			}
			
		}
	}
	
	
	private AppPrimitiveAttribute createAppRepoAttr(PrimitiveAttributeInfo aMetaPrim)
	{	
		AppPrimitiveAttribute  appType = null; 
		if(aMetaPrim.getType() instanceof BlobType) {
			appType = new AppBlobType();
//			((AppBlobType)appType).setTypeHintId(((BlobType)aMetaPrim).getTypeHint().name());
		} else if(aMetaPrim.getType() instanceof BooleanType) {
			appType = new AppBooleanType();
		} else if(aMetaPrim.getType() instanceof CurrencyType) {
			appType = new AppDecimalType();
			((AppDecimalType)appType).setMinValue(((CurrencyType)aMetaPrim.getType()).getMinValue());
			((AppDecimalType)appType).setMaxValue(((CurrencyType)aMetaPrim.getType()).getMaxValue());
			((AppDecimalType)appType).setScale(((CurrencyType)aMetaPrim.getType()).getScale());
		} else if(aMetaPrim.getType() instanceof FloatType) {
			appType = new AppFloatType();
		} else if(aMetaPrim.getType() instanceof LongType) {
			appType = new AppLongType();
			((AppLongType)appType).setMinValue(((LongType)aMetaPrim.getType()).getMinValue());
			((AppLongType)appType).setMaxValue(((LongType)aMetaPrim.getType()).getMaxValue());
		} else if(aMetaPrim.getType() instanceof TextType) {
			AppTextType appTextType = new AppTextType();
			appTextType.setLength(((TextType)aMetaPrim.getType()).getLength());
			if(appTextType.getTypeHint() != null) {
				appTextType.setTypeHint(((TextType)aMetaPrim.getType()).getTypeHint().name());
			} else {
				appTextType.setTypeHint(null);
			}
			if ( ((TextType)aMetaPrim.getType()).getDomainValues() != null 
					&& ((TextType)aMetaPrim.getType()).getDomainValues().size() > 0) {
				ChangeableObjectList<AppStringDomainModel> attrColl = new ChangeableObjectList<AppStringDomainModel>();
				appTextType.setDomainValuesColl(attrColl);
				for( DbDomainValue<String> domainVal : ((TextType)aMetaPrim.getType()).getDomainValues() ) {
					AppStringDomainModel appDomainVal = new AppStringDomainModel();
					appDomainVal.setDbValue(domainVal.getDbValue());
					appDomainVal.setRepresentation(domainVal.getRepresentation());
					appDomainVal.setType(appTextType);
				}
				
			}
			
			appType = appTextType;
		} else if(aMetaPrim.getType() instanceof TimeStampType) {
			appType = new AppTimestampType();
			((AppTimestampType)appType).setIsTimePartUsed(((TimeStampType)aMetaPrim.getType()).isTimePartUsed());
			((AppTimestampType)appType).setIsDatePartUsed(((TimeStampType)aMetaPrim.getType()).isDatePartUsed());
		} else if(aMetaPrim.getType() instanceof VarCharType) {
			appType = new AppVarCharType();
			((AppVarCharType)appType).setLength(((VarCharType)aMetaPrim.getType()).getLength());
			((AppVarCharType)appType).setIsClob(((VarCharType)aMetaPrim.getType()).isClob());
			if(((VarCharType)aMetaPrim.getType()).getMimeType() != null) {
				((AppVarCharType)appType).setMimeType(((VarCharType)aMetaPrim.getType()).getMimeType().name());
			} else {
				((AppVarCharType)appType).setMimeType(null);
			}
		} else {
			throw new RuntimeException("Invalid type " + aMetaPrim.getType());
		}
		return appType;
	}

	private void buildAssocsForAppRepo(AppRepository aAppRepo, ClassInfo aMetaClass) throws JdyPersistentException
	{
		AppClassInfo appClass = getClassFromAppRepo(aAppRepo, aMetaClass);
		ChangeableObjectList<AppAssociation> assocColl = new ChangeableObjectList<AppAssociation>();
		appClass.setAssociationsColl(assocColl);

		for (AssociationInfo metaAssocObj : aMetaClass.getAssociationInfoIterator())
		{
			AppClassInfo detailClass = getClassFromAppRepo(aAppRepo, metaAssocObj.getDetailClass()); 
			AppClassInfo masterClass = getClassFromAppRepo(aAppRepo, metaAssocObj.getMasterClassReference().getReferencedClass()); 

			// No superclass associations
			if (masterClass.equals(appClass)) {
				
				AppObjectReference metaMasterClassRef = (AppObjectReference) getAttributeInfoForMetaAttr(detailClass, metaAssocObj.getMasterClassReference());
				// AppClassInfo metaDetailClass = getClassFromAppRepo(aAppRepo,metaAssocObj.getDetailClass()); 
				String metaAssocName = metaAssocObj.getNameResource();			
				AppAssociation appAssoc = new AppAssociation();
				appAssoc.setMasterclass(masterClass);
				appAssoc.setMasterClassReference(metaMasterClassRef);
				appAssoc.setNameResource(metaAssocName);
				appAssoc.setName(metaAssocName);

				insertToDb(appAssoc);
				assocColl.addObject(appAssoc);
			}
		}
	}

	private void buildSubclassesForAppRepo(AppRepository aAppRepo, ClassInfo aMetaClass) throws JdyPersistentException 
	{
		AppClassInfo appClass = getClassFromAppRepo(aAppRepo, aMetaClass);
		ClassInfo metaSuper = aMetaClass.getSuperclass();
		
		if( metaSuper != null) 
		{
			AppClassInfo appSuper = getClassFromAppRepo(aAppRepo, metaSuper);
			appClass.setSuperclass(appSuper);
			updateToDb(appClass);
			if( appSuper.getSubclassesColl() == null ) {
				appSuper.setSubclassesColl( new ChangeableObjectList<AppClassInfo>());
			}
			((EditableObjectList)appSuper.getSubclassesColl()).addObject(appClass);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.ClassInfo#getAttributeInfoForExternalName(java.lang.String)
	 */
	public AppAttribute getAttributeInfoForMetaAttr(AppClassInfo detailClass, AttributeInfo anAttr) throws ProxyResolveException
	{
		AppAttribute result = null;
		for (AppAttribute curAttr : getAllAttributeList(detailClass)) {
			if( curAttr.getInternalName().equals(anAttr.getInternalName())) {
				result = curAttr;
				break;
			}
		}
		return result;	
	}
	
	/**
	 * Get all  AttributeInfoModel
	 *
	 * @return get the Collection ofAttributeInfoModel
	 */
	public static ObjectList<AppAttribute> getAttributesColl(AppClassInfo anAppClass) 
	{
		ObjectListSortWrapper<AppAttribute> sortedAttributes;

		sortedAttributes = new ObjectListSortWrapper<AppAttribute>();
		sortedAttributes.setSortComparator(AttributeComparator.comparator);
		sortedAttributes.setWrappedObjectList((ObjectList<AppAttribute>) anAppClass.getValue(anAppClass.getClassInfo().getAssoc("Attributes")));
		
		return sortedAttributes;
	}	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.impl.DefaultClassInfo#getAllAttributeList()
	 */
	protected ArrayList<AppAttribute> getAllAttributeList(AppClassInfo anAppClass) 
	{
		ArrayList<AppAttribute> tmpAttrList = new ArrayList<AppAttribute>(getAttributesColl(anAppClass).size()); 
		
		if( anAppClass.getSuperclass() != null) {
			for(AppAttribute curAttr : getAllAttributeList(anAppClass.getSuperclass())) {
				tmpAttrList.add((AppAttribute) curAttr);
			}
		}
			
		for(Iterator<AppAttribute> attrIter= getAttributesColl(anAppClass).iterator(); attrIter.hasNext();) {
			tmpAttrList.add(attrIter.next());
		}
		
		Collections.sort(tmpAttrList, AttributeComparator.comparator);
		return 	tmpAttrList;
	}	
	
	
	@SuppressWarnings("serial")
	private static class AttributeComparator implements Comparator<AppAttribute>, Serializable
	{
		private static final AttributeComparator comparator = new AttributeComparator();
		
		@Override
		public int compare(AppAttribute aO1, AppAttribute aO2)
		{
			int comparePos = aO1.getPosValue().compareTo(aO2.getPosValue());
			return (comparePos != 0) ? comparePos : aO1.getInternalName().compareTo(aO2.getInternalName());
		}
	}	
	
	private static class DomValue<Type> implements DbDomainValue<Type>
	{
		private final Type		domValue;
		private final String	representation;

		private DomValue(Type domValue, String representation)
		{
			this.domValue = domValue;
			this.representation = representation;
		}

		@Override
		public Type getDbValue()
		{
			return domValue;
		}

		@Override
		public String getRepresentation()
		{
			return representation;
		}
	}
}

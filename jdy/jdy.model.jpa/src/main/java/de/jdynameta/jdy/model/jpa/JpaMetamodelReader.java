/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.jdy.model.jpa;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.JdyAbstractAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyAssociationModel;
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
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.model.asm.jpa.info.JpaAsmClassInfo;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.BASIC;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_MANY;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_ONE;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_MANY;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_ONE;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

/**
 *
 * @author Rainer_2
 */
public class JpaMetamodelReader
{

     
    public ClassRepository createMetaRepository(final Metamodel metaModel, String anAppName) {
       
        return createMetaRepository(metaModel.getEntities(), anAppName);
    }
    
    
    public ClassRepository createMetaRepository(Set<EntityType<?>> allEntityInfos, String anAppName)
    {
        JdyRepositoryModel metaRepo = new JdyRepositoryModel(anAppName);
        metaRepo.addListener(new DefaultClassRepositoryValidator());
        final Map<String, JdyClassInfoModel> className2InfoMap = new HashMap<>();
       
        // build base classes
        for (EntityType<?> curEntity : allEntityInfos)
        {
            JdyClassInfoModel newModel = addClassToMetaRepo(metaRepo, curEntity);
            className2InfoMap.put(newModel.getInternalName(), newModel);
        }
        
        for (EntityType<?> curEntity : allEntityInfos)
        {
            buildAttrForMetaRepo(metaRepo, curEntity, false);
        }

         for (EntityType<?> curEntity : allEntityInfos)
        {
            buildAssocsForMetaRepo(metaRepo, curEntity);
        } 

         for (EntityType<?> curEntity : allEntityInfos)
        {
            buildSubclassesForMetaRepo(metaRepo, curEntity);
        } 
        
        return metaRepo;
    }

    private JdyClassInfoModel addClassToMetaRepo(JdyRepositoryModel metaRepo, EntityType aEntity)
    {
        JdyClassInfoModel metaClass = metaRepo.addClassInfo(aEntity.getName());

        //metaClass.setAbstract(curClass.isAbstract());
        if (aEntity.getSupertype() != null) {
            metaClass.setExternalName(aEntity.getJavaType().getName());
        } else {
            metaClass.setExternalName(aEntity.getName());
        }
        metaClass.setShortName(aEntity.getName());
        metaClass.setNameSpace(aEntity.getJavaType().getName().replace('.', '_'));
        
        return metaClass;
    }
    
    private void buildAttrForMetaRepo(ClassRepository metaRepo, EntityType<?> anEntity,boolean embeddedId)
    {
        JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(anEntity.getName());

        for (Attribute<?, ?> curAttr : anEntity.getAttributes()) 
        {
            if (!curAttr.isCollection()) {

                if (curAttr.getPersistentAttributeType() == BASIC) 
                {
                    JdyAbstractAttributeModel metaAttr = createPrimitiveField(curAttr, embeddedId);
                    if (metaAttr != null) {
                        metaClass.addAttributeInfo(metaAttr);
                    }
                }else if (curAttr.getPersistentAttributeType() == ONE_TO_ONE || curAttr.getPersistentAttributeType() == MANY_TO_ONE) 
                {
                    JdyObjectReferenceModel metaAttr = createObjectReference(curAttr, embeddedId, metaRepo);
                    if (metaAttr != null) {
                        metaClass.addAttributeInfo(metaAttr);
                    }
                } else 
                {
                    
                }
                                
            } 
        }
    }

    	private void buildAssocsForMetaRepo(ClassRepository metaRepo, EntityType<?> anEntity)
	{
            JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(anEntity.getName());

            for (Attribute<?, ?> curAttr : anEntity.getAttributes()) 
            {
                if (curAttr.isCollection()) {
                    if (curAttr.getPersistentAttributeType() == ELEMENT_COLLECTION) {
//                        JpaCollectionWrapper wrapper = new JpaCollectionWrapper(curAttr);
//                        System.out.println(wrapper.getType());
//                        System.out.println((wrapper.getType()).ordinal());
//                        System.out.println((wrapper.getType()).name());
                    } else if (curAttr.getPersistentAttributeType() == ONE_TO_MANY) {
                        JpaCollectionWrapper wrapper = new JpaCollectionWrapper(curAttr);
                        System.out.println(anEntity.getName());
			JdyClassInfoModel metaDetailClass = (JdyClassInfoModel) metaRepo.getClassForName(wrapper.getReferencedType().getName()); 

                        OneToMany mapping = wrapper.getAnntotationInfo(OneToMany.class);
			JdyObjectReferenceModel metaMasterClassRef = (JdyObjectReferenceModel) metaDetailClass.getAttributeInfoForExternalName(mapping.mappedBy());
			String metaAssocName = curAttr.getName();			
			JdyAssociationModel metaAssoc = new JdyAssociationModel(metaMasterClassRef, metaDetailClass, metaAssocName);
			metaClass.addAssociation(metaAssoc);

                    
                    } else if (curAttr.getPersistentAttributeType() == MANY_TO_MANY) {
                        // not supported at the momment
                    }
                }
            }

	}

    private void buildSubclassesForMetaRepo(ClassRepository metaRepo, EntityType<?> anEntity)
    {
        JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(anEntity.getName());

        EntityType jpaSuper = (EntityType) anEntity.getSupertype();

        while (jpaSuper != null)
        {
            JdyClassInfoModel metaSuper = (JdyClassInfoModel) metaRepo.getClassForName(jpaSuper.getName());
            if (metaSuper != null)
            {
                metaSuper.addSubclass(metaClass);
                break;
            }

        }

    }
    
    
    private JdyObjectReferenceModel createObjectReference(Attribute<?, ?> curAttr, boolean embeddedId, ClassRepository metaRepo) 
    {
        JpaFieldWrapper wrapper = new JpaFieldWrapper(curAttr);
        Type type = wrapper.getType();
        boolean isKey = ((SingularAttribute)curAttr).isId() || embeddedId;
        boolean isNotNull = !((SingularAttribute)curAttr).isOptional()|| !wrapper.isNullable();
        boolean isGenerated = wrapper.getGeneratedInfo() != null;
        String refTypeName = ((EntityType)type).getName();
        ClassInfo referenceType = metaRepo.getClassForName(refTypeName);
        
        JdyObjectReferenceModel  metaAttr = new JdyObjectReferenceModel(referenceType, curAttr.getName(), curAttr.getName(), isKey, isNotNull);
        metaAttr.setGenerated(isGenerated);
        return metaAttr;
    }
    
    private JdyAbstractAttributeModel createPrimitiveField(Attribute<?, ?> curAttr, boolean embeddedId) {

        JpaFieldWrapper wrapper = new JpaFieldWrapper(curAttr);
        PrimitiveType metaType = getPrimiviveType(wrapper);
        if (metaType != null)
        {
            JdyAbstractAttributeModel metaAttr = null;
            boolean isKey = ((SingularAttribute)curAttr).isId() || embeddedId;
            boolean isNotNull = !((SingularAttribute)curAttr).isOptional()|| !wrapper.isNullable();
            boolean isGenerated = wrapper.getGeneratedInfo() != null;
            metaAttr = new JdyPrimitiveAttributeModel(metaType, curAttr.getName(), curAttr.getName(), isKey, isNotNull);
            metaAttr.setGenerated(isGenerated);
            return metaAttr;
        } else {
            return null;
        }
    }
    

    private PrimitiveType getPrimiviveType(JpaFieldWrapper wrapper)
    {
        Class aTypeClass = wrapper.getJavaType();
        
        if (aTypeClass.isAssignableFrom(Integer.class))
        {
            return new JdyLongType((long) Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
        } else if (aTypeClass.isAssignableFrom(Long.class))
        {
            return new JdyLongType(Long.MIN_VALUE, Long.MAX_VALUE);
        } else if (aTypeClass.isAssignableFrom(Short.class))
        {
            return new JdyLongType((long) Short.MIN_VALUE, (long) Short.MAX_VALUE);
        } else if (aTypeClass.isAssignableFrom(Byte.class))
        {
            return new JdyLongType((long) Byte.MIN_VALUE, (long) Byte.MAX_VALUE);
        } else if (aTypeClass.isAssignableFrom(String.class))
        {
            Column column = wrapper.getAnntotationInfo(Column.class);
            int length = (column != null) ? column.length() : 40;

            return new JdyTextType(length);
        
        } else if (aTypeClass.isAssignableFrom(Date.class) || aTypeClass.isAssignableFrom(Timestamp.class))
        {
            Temporal temporal = wrapper.getAnntotationInfo(Temporal.class);
            TemporalType temporalType = (temporal != null ) ? temporal.value() : TemporalType.TIMESTAMP;
            boolean hasDate = (temporalType==TemporalType.TIMESTAMP) || (temporalType==TemporalType.DATE);
            boolean hasTime = (temporalType==TemporalType.TIMESTAMP) || (temporalType==TemporalType.TIME);
            return new JdyTimeStampType(hasDate, hasTime);
        } else if (aTypeClass.isAssignableFrom(Boolean.class))
        {
            return new JdyBooleanType();
        } else if (aTypeClass.isAssignableFrom(Double.class) || aTypeClass.isAssignableFrom(Float.class))
        {
            return new JdyFloatType();
        } else if (aTypeClass.isAssignableFrom(BigDecimal.class))
        {
            Column comlumn = wrapper.getAnntotationInfo(Column.class);
            int scale = comlumn.scale();
            int precision = comlumn.precision();
            
            return new JdyDecimalType();
        } else
        {
            if (aTypeClass.isEnum()) {
                Column column = wrapper.getAnntotationInfo(Column.class);
                int length = (column != null) ? column.length() : 40;

                List<DbDomainValue<String>> domainValues = new ArrayList<>();
                for (Field jpaField : aTypeClass.getDeclaredFields())
                {
                    if(jpaField.isEnumConstant()) 
                    {
                        domainValues.add(new DomValue<>(jpaField.getName(), jpaField.getName()));
                    }
                }

                return new JdyTextType(length, null, domainValues);
                
            }
            return null;
        }
    }
    
    
    private static class DomValue<Type> implements DbDomainValue<Type>
    {
        private final Type domValue;
        private final String representation;

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

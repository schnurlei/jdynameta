package de.jdynameta.model.asm.jpa;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
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
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.model.asm.jpa.info.JpaAsmAnntotationInfo;
import de.jdynameta.model.asm.jpa.info.JpaAsmClassInfo;
import de.jdynameta.model.asm.jpa.info.JpaAsmFieldInfo;

public class JpaAsmRepositoryCreator
{
    private final Map<String, JpaAsmClassInfo> className2InfoMap;

    public JpaAsmRepositoryCreator(DbAccessConnection<ValueObject, GenericValueObjectImpl> aMetaCon)
    {
        super();
        this.className2InfoMap = new HashMap<>();
    }

    public ClassRepository createMetaRepository(List<JpaAsmClassInfo> allJpaInfos, String anAppName)
    {
        JdyRepositoryModel metaRepo = new JdyRepositoryModel(anAppName);
        metaRepo.addListener(new DefaultClassRepositoryValidator());

        // build a map of all Classes
        for (JpaAsmClassInfo curClass : allJpaInfos)
        {
            className2InfoMap.put(curClass.getClassName(), curClass);
        }

        // build base classes
        for (JpaAsmClassInfo curClass : allJpaInfos)
        {
            if (curClass.getEntityInfo() != null)
            {
                addClassToMetaRepo(metaRepo, curClass);
            }
        }

        // add Attributes 
        for (JpaAsmClassInfo curClass : allJpaInfos)
        {
            if (curClass.getEntityInfo() != null)
            {
                buildAttrForMetaRepo(metaRepo, curClass);
            }
        }

        // add associations - objects reference ave to be created 
        for (JpaAsmClassInfo curClass : allJpaInfos)
        {
            if (curClass.getEntityInfo() != null)
            {
                buildAssociationsForMetaRepo(metaRepo, curClass);
            }
        }

        for (JpaAsmClassInfo curClass : allJpaInfos)
        {
            if (curClass.getEntityInfo() != null)
            {
                buildSubclassesForMetaRepo(metaRepo, curClass);
            }
        }

        return metaRepo;
    }

    private void addClassToMetaRepo(JdyRepositoryModel metaRepo, JpaAsmClassInfo curClass)
    {
        JdyClassInfoModel metaClass = metaRepo.addClassInfo(curClass.getSimpleClassName());

        //metaClass.setAbstract(curClass.isAbstract());
        metaClass.setExternalName(curClass.getTableName());
        metaClass.setShortName(curClass.getSimpleClassName());
        metaClass.setNameSpace(curClass.getPackageName().replace('.', '_'));

    }

    private void buildAttrForMetaRepo(ClassRepository metaRepo, JpaAsmClassInfo aJpaClassInfo)
    {
        JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(aJpaClassInfo.getSimpleClassName());

        addFieldsToMetaClass(metaRepo, aJpaClassInfo, metaClass, false);
        JpaAsmClassInfo superClass = this.className2InfoMap.get(aJpaClassInfo.getSuperClassName());

        while (superClass != null)
        {
            if (superClass.getMappedSuperclassInfo() != null)
            {
                addFieldsToMetaClass(metaRepo, superClass, metaClass, false);
                superClass = this.className2InfoMap.get(superClass.getSuperClassName());
            } else
            {
                superClass = null;
            }
        }
    }

    private void buildAssociationsForMetaRepo(ClassRepository metaRepo, JpaAsmClassInfo aJpaClassInfo)
    {
        JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(aJpaClassInfo.getSimpleClassName());
        buildAssocsForMetaClass(metaRepo, aJpaClassInfo, metaClass, false);
        JpaAsmClassInfo superClass = this.className2InfoMap.get(aJpaClassInfo.getSuperClassName());

        while (superClass != null)
        {
            if (superClass.getMappedSuperclassInfo() != null)
            {
                buildAssocsForMetaClass(metaRepo, superClass, metaClass, false);
                superClass = this.className2InfoMap.get(superClass.getSuperClassName());
            } else
            {
                superClass = null;
            }
        }
    }

    private void addFieldsToMetaClass(ClassRepository aMetaRepo, JpaAsmClassInfo anJpaClassInfo, JdyClassInfoModel metaClass, boolean embeddedId)
    {
        for (JpaAsmFieldInfo jpaField : anJpaClassInfo.getAllFields())
        {
            JdyAbstractAttributeModel metaAttr = null;

            if (!jpaField.isStatic() && jpaField.getTransientInfo() == null)
            {
                if (jpaField.getManyToManyInfo() != null)
                {
                } else if (jpaField.getOneToManyInfo() != null)
                {
                } else if (jpaField.getManyToOneInfo() != null || jpaField.getOneToOneInfo() != null)
                {
                    JpaAsmAnntotationInfo manyToOneInfo = jpaField.getManyToOneInfo();
                    JpaAsmAnntotationInfo oneToOneInfo = jpaField.getOneToOneInfo();
                    String internalName = jpaField.getDesc();
                    if (manyToOneInfo != null)
                    {
                        Type type = (Type) manyToOneInfo.getValue("targetEntity");
//						System.out.println(type.getClassName());
//						System.out.println(type.getDescriptor());
                        if (type != null)
                        {
                            internalName = type.getInternalName();
                        } else
                        {
                            internalName = jpaField.getDescSimpleType();
                        }
                    } else
                    {
                        Type type = (Type) oneToOneInfo.getValue("targetEntity");
                        if (type != null)
                        {
                            internalName = type.getInternalName();
                        } else
                        {
                            internalName = jpaField.getDescSimpleType();
                        }
                    }
                    ClassInfo referenceType = aMetaRepo.getClassForName(internalName.substring(internalName.lastIndexOf('/') + 1));
                    boolean isKey = jpaField.getIdInfo() != null || embeddedId;

                    metaAttr = new JdyObjectReferenceModel(referenceType, jpaField.getName(), jpaField.getName(), isKey, !jpaField.isOptional());
                    try
                    {
                        //						metaAttr.setGroup(metaAttr.getAttrGroup());
                        metaClass.addAttributeInfo(metaAttr);
                    } catch (InvalidClassInfoException e)
                    {
                        System.out.println("********Invalid type for field " + e.getLocalizedMessage());
                    }

                } else if (jpaField.getEmbeddedIdInfo() != null || jpaField.getEmbeddedInfo() != null)
                {
                    JpaAsmClassInfo embeddedClass = this.className2InfoMap.get(jpaField.getDescType());
                    addFieldsToMetaClass(aMetaRepo, embeddedClass, metaClass, jpaField.getEmbeddedIdInfo() != null);
                } else if (jpaField.getEnumeratedInfo() != null)
                {
                    boolean isKey = jpaField.getIdInfo() != null || embeddedId;
                    boolean isNotNull = !jpaField.isOptional() || !jpaField.isNullable();
                    String desc = jpaField.getDescType();
                    JpaAsmClassInfo enumClass = className2InfoMap.get(desc);

                    JdyTextType type = createPrimitiveEnumType(jpaField, enumClass);

                    metaAttr = new JdyPrimitiveAttributeModel(type, jpaField.getName(), jpaField.getColumnName(), isKey, isNotNull);

                    try
                    {
                        //						metaAttr.setGroup(metaAttr.getAttrGroup());
                        metaClass.addAttributeInfo(metaAttr);
                    } catch (InvalidClassInfoException e)
                    {
                        System.out.println("********Invalid type for field " + e.getLocalizedMessage());
                    }

                } else
                {
                    PrimitiveType metaType = getPrimiviveType(jpaField);

                    if (metaType != null)
                    {
                        boolean isKey = jpaField.getIdInfo() != null || embeddedId;
                        boolean isNotNull = !jpaField.isOptional() || !jpaField.isNullable();
                        boolean isGenerated = jpaField.getGeneratedInfo() != null;
                        metaAttr = new JdyPrimitiveAttributeModel(metaType, jpaField.getName(), jpaField.getColumnName(), isKey, isNotNull);
                        metaAttr.setGenerated(isGenerated);
                        try
                        {
                            //						metaAttr.setGroup(metaAttr.getAttrGroup());
                            metaClass.addAttributeInfo(metaAttr);
                        } catch (InvalidClassInfoException e)
                        {
                            System.out.println("********Invalid type for field " + e.getLocalizedMessage());
                        }
                    } else
                    {
                        System.out.println("********Invalid type for field " + jpaField.getName());
                    }
                }
            } else
            {
                //System.out.println("static");
            }
        }
    }

    private JdyTextType createPrimitiveEnumType(JpaAsmFieldInfo aJpaField, JpaAsmClassInfo aEnumClass)
    {
        List<DbDomainValue<String>> domainValues = new ArrayList<>();

        for (JpaAsmFieldInfo jpaField : aEnumClass.getAllFields())
        {
            if (jpaField.isEnum())
            {
                domainValues.add(new DomValue<String>(jpaField.getName(), jpaField.getName()));
            }
        }

        return new JdyTextType(100, null, domainValues);
    }

    private PrimitiveType getPrimiviveType(JpaAsmFieldInfo aJpaField)
    {
        if (isTypeOf(Type.INT_TYPE, aJpaField) || isTypeOf(Integer.class, aJpaField))
        {
            return new JdyLongType((long) Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
        } else if (isTypeOf(Type.LONG_TYPE, aJpaField) || isTypeOf(Long.class, aJpaField))
        {
            return new JdyLongType(Long.MIN_VALUE, Long.MAX_VALUE);
        } else if (isTypeOf(Type.SHORT_TYPE, aJpaField) || isTypeOf(Short.class, aJpaField))
        {
            return new JdyLongType((long) Short.MIN_VALUE, (long) Short.MAX_VALUE);
        } else if (isTypeOf(Type.BYTE_TYPE, aJpaField) || isTypeOf(Byte.class, aJpaField))
        {
            return new JdyLongType((long) Byte.MIN_VALUE, (long) Byte.MAX_VALUE);
        } else if (isTypeOf(String.class, aJpaField))
        {
            return new JdyTextType();
        } else if (isTypeOf(Date.class, aJpaField) || isTypeOf(java.sql.Date.class, aJpaField) || isTypeOf(Timestamp.class, aJpaField))
        {
            return new JdyTimeStampType();
        } else if (isTypeOf(Type.BOOLEAN_TYPE, aJpaField) || isTypeOf(Boolean.class, aJpaField))
        {
            return new JdyBooleanType();
        } else if (isTypeOf(Type.DOUBLE_TYPE, aJpaField) || isTypeOf(Double.class, aJpaField))
        {
            return new JdyFloatType();
        } else if (isTypeOf(Type.FLOAT_TYPE, aJpaField) || isTypeOf(Float.class, aJpaField))
        {
            return new JdyFloatType();
        } else if (isTypeOf(BigDecimal.class, aJpaField))
        {
            return new JdyDecimalType();
        } else
        {
            return null;
        }
    }

    private boolean isTypeOf(org.objectweb.asm.Type jpaType, JpaAsmFieldInfo aJpaField)
    {

        return aJpaField.getDesc().equals(jpaType.toString());
    }

    private boolean isTypeOf(Class<?> typeClass, JpaAsmFieldInfo aJpaField)
    {

        return aJpaField.getDesc().length() > 2 && typeClass.getCanonicalName().replace('.', '/').equals(aJpaField.getDescType());
    }

    private void buildAssocsForMetaClass(ClassRepository aMetaRepo, JpaAsmClassInfo anJpaClassInfo, JdyClassInfoModel metaClass, boolean embeddedId)
    {
        for (JpaAsmFieldInfo jpaField : anJpaClassInfo.getAllFields())
        {

            if (!jpaField.isStatic() && jpaField.getTransientInfo() == null)
            {
                if (jpaField.getManyToManyInfo() != null)
                {
                    System.out.println("********many to many not suported");
                } else if (jpaField.getOneToManyInfo() != null)
                {
                    JpaAsmAnntotationInfo one2ManyInfo = jpaField.getOneToManyInfo();
                    Type targetType = (Type) one2ManyInfo.getValue("targetEntity");
                    String mappedBy = (String) one2ManyInfo.getValue("mappedBy");

                    String internalName;
                    if (targetType != null)
                    {
                        internalName = targetType.getInternalName();
                    } else
                    {
                        //@TODO
                        String signature = jpaField.getSignature();
                        String collectionType = signature.substring(signature.indexOf('<') + 1, signature.indexOf('>'));
                        internalName = collectionType.substring(collectionType.lastIndexOf('/') + 1, collectionType.length() - 1);
                    }
                    JdyClassInfoModel detailType = (JdyClassInfoModel) aMetaRepo.getClassForName(internalName.substring(internalName.lastIndexOf('/') + 1));

                    JdyObjectReferenceModel metaMasterClassRef = (JdyObjectReferenceModel) detailType.getAttribute(mappedBy);
                    if (metaMasterClassRef != null)
                    {
                        JdyAssociationModel metaAssoc = new JdyAssociationModel(metaMasterClassRef, detailType, jpaField.getName());
                        metaMasterClassRef.setIsInAssociation(true);
                        metaClass.addAssociation(metaAssoc);
                    } else
                    {
                        System.out.println("******No backreference");
                    }
                }
            }
        }
    }

//	JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(annAppClass.getInternalName());
//
//	for (Object appAssocObj : annAppClass.getAssociationsColl())
//	{
//		AppAssociation appAssoc = (AppAssociation) appAssocObj;
//		
//		JdyClassInfoModel masterClass = (JdyClassInfoModel) metaRepo.getClassForName(appAssoc.getMasterClassReference().getMasterclass().getInternalName()); 
//
//		JdyObjectReferenceModel metaMasterClassRef = (JdyObjectReferenceModel) masterClass.getAttributeInfoForExternalName(appAssoc.getMasterClassReference().getInternalName());
//		JdyClassInfoModel metaDetailClass = (JdyClassInfoModel) metaRepo.getClassForName(appAssoc.getMasterClassReference().getMasterclass().getInternalName()); 
//		String metaAssocName = appAssoc.getNameResource();			
//		JdyAssociationModel metaAssoc = new JdyAssociationModel(metaMasterClassRef, metaDetailClass, metaAssocName);
//		metaClass.addAssociation(metaAssoc);
//	}
    private void buildSubclassesForMetaRepo(ClassRepository metaRepo, JpaAsmClassInfo anJpaClass)
    {
        JdyClassInfoModel metaClass = (JdyClassInfoModel) metaRepo.getClassForName(anJpaClass.getSimpleClassName());

        String jpaSuper = anJpaClass.getSuperSimpleClassName();
        JpaAsmClassInfo jpaSuperClass = className2InfoMap.get(anJpaClass.getSuperClassName());

        while (jpaSuperClass != null)
        {
            JdyClassInfoModel metaSuper = (JdyClassInfoModel) metaRepo.getClassForName(jpaSuper);
            if (metaSuper != null)
            {
                metaSuper.addSubclass(metaClass);
                break;
            }

            jpaSuperClass = className2InfoMap.get(jpaSuperClass.getSuperClassName());
            if (jpaSuperClass != null)
            {
                jpaSuper = jpaSuperClass.getSimpleClassName();
            } else
            {
                jpaSuper = null;
                break;
            }
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

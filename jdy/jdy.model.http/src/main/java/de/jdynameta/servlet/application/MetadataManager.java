package de.jdynameta.servlet.application;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.codehaus.groovy.control.CompilationFailedException;

import de.jdynameta.application.ApplicationHook;
import de.jdynameta.application.ApplicationManagerImpl.WorkflowApplicationHook;
import de.jdynameta.application.WorkflowApplicationStep;
import de.jdynameta.application.WorkflowException;
import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.FilterUtil;
import de.jdynameta.base.metainfo.filter.defaultimpl.QueryCreator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnection;
import de.jdynameta.dbaccess.jdbc.hsqldb.HSqlSchemaHandler;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.groovy.GroovyExecutor;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.ApplicationHookCreator;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.ApplicationRepositoryClassFileGenerator;
import de.jdynameta.metamodel.application.MetaRepositoryCreator;
import de.jdynameta.metamodel.application.RepositoryWorkflowManager;
import de.jdynameta.metamodel.generic.DynamicWorkflowApplicationHook;
import de.jdynameta.metamodel.generic.ScritpingHook;
import de.jdynameta.metamodel.generic.ScritpingHook.ScriptSource;
import de.jdynameta.persistence.cache.CachedObjectTransformator;
import de.jdynameta.persistence.cache.UpdatableObjectCreator;
import de.jdynameta.persistence.impl.proxy.SimpleProxyResolver;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.impl.BasicPersistenceObjectManagerImpl;
import de.jdynameta.persistence.manager.impl.ValueModelObjectCreatorWithProxy;
import de.jdynameta.persistence.manager.impl.ValueModelPersistenceObjectManager;
import de.jdynameta.persistence.state.ApplicationModel;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 *
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class MetadataManager
{
    private final DataSource dataSource;
    private final ApplicationRepository metaRepository;
    private AppModelPersistenceManager metaPersManager;
    private RepositoryWorkflowManager metaMngr;
    private final WorkflowApplicationHook<ApplicationObj> metaHook;
    private final Map<String, RepoInfo> repoName2RepoInfo;

//	String serverName =  "localhost"; 
//    String dbUrl = "jdbc:hsqldb:hsql://"+serverName+"/JDynaMeta";
    public MetadataManager(DataSource aDatasource)
    {
        this.metaHook = new ApplicationHookCreator().createAppHook();
        this.repoName2RepoInfo = new HashMap<>();
        this.metaRepository = ApplicationRepository.getSingleton();
        this.dataSource = aDatasource;

    }

    public WorkflowApplicationHook<ApplicationObj> getHook()
    {
        return this.metaHook;
    }

    public void setupApplicationManager(boolean deleteSchema, boolean createTestSchema) throws InvalidClassInfoException, JdyPersistentException, JdbcSchemaHandler.SchemaValidationException
    {
        initializeApplicationManager("JDynaMeta", deleteSchema, createTestSchema);
    }

    private void initializeApplicationManager(String aSchemaName, boolean deleteSchema, boolean createTestSchema) throws JdyPersistentException, InvalidClassInfoException, JdbcSchemaHandler.SchemaValidationException
    {
        //			HSqlConnectionHolder<ValueObject, TypedReflectionObjectInterface> hsqlConnection = new MetaInfoHSqlConnectionHolder(new File(databasePath,"TestDbRsc"), "JDynaMeta");
        HSqlSchemaHandler hsqlConnection = new HSqlSchemaHandler(dataSource);

        if (deleteSchema && hsqlConnection.existsSchema(aSchemaName))
        {
            hsqlConnection.deleteSchema(aSchemaName);
        }
        if (!hsqlConnection.existsSchema(aSchemaName))
        {
            hsqlConnection.createSchema(aSchemaName, ApplicationRepository.getSingleton());
        } else
        {
            hsqlConnection.validateSchema(aSchemaName, ApplicationRepository.getSingleton());
        }

        this.metaPersManager = new AppModelPersistenceManager(hsqlConnection.createBaseConnection(aSchemaName));
        JdbcSchemaHandler hsqlConnection2 = new HSqlSchemaHandler(dataSource);
        metaMngr = new RepositoryWorkflowManager(hsqlConnection2);

        if (createTestSchema)
        {

            DefaultClassInfoQuery query = QueryCreator.start(ApplicationRepository.getSingleton().getRepositoryModel())
                    .equal("applicationName", PlantShopRepository.createPlantShopRepository().getRepoName())
                    .query();

            JdbcConnection<ValueObject, GenericValueObjectImpl> metaCon;
            metaCon = HsqlUtil.<ValueObject, GenericValueObjectImpl>createBaseConnection(dataSource, aSchemaName);
            metaCon.setObjectTransformator(new ValueModelObjectCreator<>(new ApplicationRepositoryClassFileGenerator.ModelNameCreator()));
            ObjectList<GenericValueObjectImpl> reps = metaCon.loadValuesFromDb(query);

            if (reps.size() == 0)
            {

                ClassRepository plantRepo = PlantShopRepository.createPlantShopRepository();
                if (hsqlConnection.existsSchema(plantRepo.getRepoName()))
                {
                    hsqlConnection.deleteSchema(plantRepo.getRepoName());
                }

                MetaRepositoryCreator creator = new MetaRepositoryCreator(metaCon);
                AppRepository repo = creator.createAppRepository(plantRepo);
                try
                {
                    executeWorkFlowAction("workflow.closeRepository", repo);
                } catch (WorkflowException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

//	public ApplicationManager<AppPersistentGenericObj> createApplicationManagerForNamespace(String aNameSpace) throws JdyPersistentException
//	{
//		DataSource datasource = HsqlUtil.createDatasource("jdbc:hsqldb:hsql://localhost/JDynaMeta","sa","sa");
//	    HSqlSchemaHandler hsqlConnection2 = new HSqlSchemaHandler(datasource);
//	    hsqlConnection2.existsSchema(aNameSpace);
//	    PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persmanager = new ValueModelPersistenceObjectManager(hsqlConnection2.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(aNameSpace));
//	    ApplicationManagerGenericObj genericAppManager = null;
//		    
//	    DefaultClassInfoQuery query = new DefaultClassInfoQuery(metaRepository.getRepositoryModel());
//		query.setFilterExpression(DefaultOperatorEqual.createEqualExpr("applicationName", aNameSpace, metaRepository.getRepositoryModel()));
//
//		ObjectList<GenericValueObjectImpl> readList = metaPersManager.loadObjectsFromDb(query);
//		if( readList.size() > 0) 
//		{
//		    AppRepository appRepository = (AppRepository) readList.get(0);
//		    String notificationServerName = "ws://localhost:9088/websocket/Servlet";
//				
////			    genericAppManager = new ApplicationManagerGroovy(persmanager, appRepository ,  new URI(notificationServerName));
//		    genericAppManager = new ApplicationManagerGenericObj(persmanager );
//		    
//		}
//		
//
//	    return genericAppManager;
//	}
    public ApplicationRepository getMetaRepository()
    {
        return metaRepository;
    }

    public boolean isDbInitialized()
    {
        return getMetaAppManager() != null;
    }

    private AppModelPersistenceManager getMetaAppManager()
    {
        return metaPersManager;
    }

    public ClassInfo getClassInfo(MetadataPathInfo aPath) throws ParseException, JdyPersistentException
    {
        if (aPath.getRepoName() != null)
        {
            if (isMetaDataRepository(aPath.getRepoName()))
            {
                if (aPath.getClassName() == null)
                {
                    throw new ParseException("No valid Classname in namespace: " + aPath.getRepoName(), 0);
                }
                return getClassFromRepository(metaRepository, aPath.getClassName());
            } else
            {

                RepoInfo info = repoName2RepoInfo.get(aPath.getRepoName());
                if (info == null)
                {
                    info = new RepoInfo(aPath.getRepoName(), this.metaPersManager);
                }
                ClassRepository tmpRepo = info.getOrCreateRepo();
                return getClassFromRepository(tmpRepo, aPath.getClassName());
            }
        } else
        {
            throw new ParseException("No valid namespace: " + aPath.getRepoName(), 0);
        }
    }

    private ClassInfo getClassFromRepository(ClassRepository repository, String internalClassName)
    {
        ClassInfo resultClass = null;

        for (ClassInfo type : repository.getAllClassInfosIter())
        {
            if (type.getInternalName().equals(internalClassName))
            {
                resultClass = type;
                break;
            }
        }

        return resultClass;
    }

    private RepoInfo getOrCreateRepoInfo(String aRepoName)
    {
        RepoInfo info = repoName2RepoInfo.get(aRepoName);
        if (info == null)
        {
            info = new RepoInfo(aRepoName, this.metaPersManager);
            repoName2RepoInfo.put(aRepoName, info);
        }
        return info;
    }

    public ClassRepository getRepository(MetadataPathInfo aPath) throws ParseException, JdyPersistentException
    {
        if (aPath.getRepoName() != null)
        {
            if (isMetaDataRepository(aPath.getRepoName()))
            {
                return metaRepository;
            } else
            {
                return getOrCreateRepoInfo(aPath.getRepoName()).getOrCreateRepo();
            }
        } else
        {
            throw new ParseException("No valid repository: " + aPath.getRepoName(), 0);
        }
    }

    public boolean isMetaDataRepository(String aRepoName)
    {
        return aRepoName.equals("@jdy") || aRepoName.equals(this.metaRepository.getRepoName());
    }

    public TypedValueObject insertObject(ApplicationObj objectToInsert)
            throws JdyPersistentException
    {
        String repoName = objectToInsert.getClassInfo().getRepoName();
        if (isMetaDataRepository(repoName))
        {
            TypedValueObject newObject;
            this.getHook().beforeSave(objectToInsert);
            newObject = getMetaAppManager().insertObject(objectToInsert, objectToInsert.getClassInfo());
            this.getHook().afterSave(objectToInsert, true);
            return newObject;
        } else
        {
            RepoInfo info = getOrCreateRepoInfo(repoName);
            GenericValueObjectImpl newObject = info.getPersistentManager(dataSource).insertObject(objectToInsert, objectToInsert.getClassInfo());
            return newObject;
        }
    }

    public TypedValueObject updateObject(ChangeableTypedValueObject objectToUpdate)
            throws JdyPersistentException
    {

        String repoName = objectToUpdate.getClassInfo().getRepoName();
        if (isMetaDataRepository(repoName))
        {
            ApplicationObj wrappedObj = resolveObjectReferences(objectToUpdate);
            this.getHook().beforeSave(wrappedObj);
            getMetaAppManager().updateObject(wrappedObj, wrappedObj.getClassInfo());
            this.getHook().afterSave(wrappedObj, false);
            return wrappedObj;
        } else
        {
            RepoInfo info = getOrCreateRepoInfo(repoName);
            info.getPersistentManager(dataSource).updateObject(objectToUpdate, objectToUpdate.getClassInfo());
            return objectToUpdate;
        }

    }

    public void deleteObject(TypedValueObject objToDelete)
            throws JdyPersistentException
    {
        String repoName = objToDelete.getClassInfo().getRepoName();
        if (isMetaDataRepository(repoName))
        {
            ApplicationObj wrappedObj = resolveObjectReferences(objToDelete);
            this.getHook().beforeDelete(wrappedObj);
            getMetaAppManager().deleteObject(wrappedObj, objToDelete.getClassInfo());
            this.getHook().afterDelete(wrappedObj);
        } else
        {
            RepoInfo info = getOrCreateRepoInfo(repoName);
            ApplicationObj wrappedObj = resolveObjectReferences(objToDelete);
            info.getPersistentManager(dataSource).deleteObject(wrappedObj, objToDelete.getClassInfo());
        }
    }

    public TypedValueObject executeWorkFlowAction(Object stepName, TypedValueObject aObjectToExecuteOn) throws WorkflowException, JdyPersistentException
    {
        TypedValueObject result = null;

        if (isMetaDataRepository(aObjectToExecuteOn.getClassInfo().getRepoName()))
        {

            DefaultClassInfoQuery query = FilterUtil.createSearchEqualObjectFilter(aObjectToExecuteOn.getClassInfo(), aObjectToExecuteOn);
            ObjectList<GenericValueObjectImpl> execObj = this.metaPersManager.loadObjectsFromDb(query);

            if (execObj.size() != 1)
            {
                throw new WorkflowException("could not find object");
            }

            ObjectList<WorkflowApplicationStep<GenericValueObjectImpl>> possibleActs = metaMngr.getWorkflowStepsFor(aObjectToExecuteOn, aObjectToExecuteOn.getClassInfo());
            WorkflowApplicationStep<GenericValueObjectImpl> stepToExecute = null;

            for (WorkflowApplicationStep<GenericValueObjectImpl> curStep : possibleActs)
            {
                if (curStep.getNameResource().equals(stepName))
                {
                    stepToExecute = curStep;
                }
            }

            if (stepToExecute != null)
            {
                stepToExecute.executeOn(execObj.get(0));
                result = updateObject(execObj.get(0));
            } else
            {
                throw new WorkflowException("Invalid step " + stepName);
            }
        } else
        {
            throw new WorkflowException("Invalid step " + stepName);
        }

        return result;
    }

    public ObjectList<GenericValueObjectImpl> loadObjectsFromDb(ClassInfoQuery aFilter) throws JdyPersistentException
    {
        String repoName = aFilter.getResultInfo().getRepoName();
        if (isMetaDataRepository(repoName))
        {
            return this.getMetaAppManager().loadObjectsFromDb(aFilter);
        } else
        {
            return getOrCreateRepoInfo(repoName).getPersistentManager(dataSource).loadObjectsFromDb(aFilter);
        }
    }

    public ApplicationObj resolveObjectReferences(final TypedValueObject typedValueObject) throws JdyPersistentException
    {
        final ApplicationObj wrapper = new ApplicationModel(typedValueObject.getClassInfo(), typedValueObject, false, null);
        for (AttributeInfo attribute : typedValueObject.getClassInfo().getAttributeInfoIterator())
        {
            attribute.handleAttribute(new AttributeHandler()
            {

                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
                {
                    // Ignorem only replace references
                }

                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                        throws JdyPersistentException
                {
                    ValueObject referencedObj = (ValueObject) typedValueObject.getValue(aInfo);
                    if (referencedObj != null)
                    {
                        ObjectList<? extends TypedValueObject> resolvedObj = getMetaAppManager().loadObjectsFromDb(FilterUtil.createSearchEqualObjectFilter(aInfo.getReferencedClass(), referencedObj));
                        final ApplicationObj refObj = new ApplicationModel(resolvedObj.get(0).getClassInfo(), resolvedObj.get(0), false, null);
                        wrapper.setValue(aInfo, refObj);
                    }
                }

            }, typedValueObject);

        }

        return wrapper;
    }

    public static final class AppModelPersistenceManager extends BasicPersistenceObjectManagerImpl<ChangeableValueObject, GenericValueObjectImpl>
    {
        public AppModelPersistenceManager(DbAccessConnection aDbConnection)
        {
            super(aDbConnection);
        }

        @Override
        protected CachedObjectTransformator<ChangeableValueObject, GenericValueObjectImpl> createObjectCreator()
        {
            SimpleProxyResolver<ChangeableValueObject, GenericValueObjectImpl> proxyResolver = new SimpleProxyResolver<>(this.getDbConnect());
            UpdatableObjectCreator<ChangeableValueObject, GenericValueObjectImpl> transformator = new AppModelCreator(proxyResolver, this);

            CachedObjectTransformator<ChangeableValueObject, GenericValueObjectImpl> cachedObjCreator
                    = new CachedObjectTransformator<>();

            cachedObjCreator.setNewObjectCreator(transformator);
            return cachedObjCreator;
        }
    }

    public static class AppModelCreator extends ValueModelObjectCreatorWithProxy
    {
        private final ClassNameCreator nameCreator;

        public AppModelCreator(ProxyResolver aProxyResolver, PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> aObjectManager)
        {
            super(aProxyResolver, aObjectManager);
            this.nameCreator = new ApplicationRepositoryClassFileGenerator.ModelNameCreator();
        }

        @Override
        protected GenericValueObjectImpl createValueObject(ProxyResolver aProxyResolver, ClassInfo aClassInfo, boolean aIsNewFlag) throws ObjectCreationException
        {
            return ApplicationRepository.createValueObject(aProxyResolver, aClassInfo, aIsNewFlag, nameCreator);
        }
    }

    public static GenericValueObjectImpl createValueObject(ProxyResolver aProxyResolver, ClassInfo aClassInfo, boolean aIsNewFlag, ClassNameCreator nameCreator) throws ObjectCreationException
    {
        try
        {
            final Class<? extends Object> metaClass = Class.forName(nameCreator.getAbsolutClassNameFor(aClassInfo));

            Constructor<? extends Object> classConstructor = metaClass.getConstructor(new Class[]
            {
            });
            GenericValueObjectImpl newObject = (GenericValueObjectImpl) classConstructor.newInstance(new Object[]
            {
            });
            newObject.setNew(aIsNewFlag);
            newObject.setProxyResolver(aProxyResolver);

            return newObject;
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException excp)
        {
            throw new ObjectCreationException(excp);
        }
    }

    @SuppressWarnings("serial")
    private class ValueModelObjectCreator<TCreatedObjFromValueObj> extends AbstractReflectionCreator<TCreatedObjFromValueObj>
            implements ObjectTransformator<ValueObject, TCreatedObjFromValueObj>
    {

        public ValueModelObjectCreator(ClassNameCreator aNameCreator)
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
        protected TCreatedObjFromValueObj createProxyObjectFor(
                TypedValueObject aObjToHandle)
        {
            return null;
        }

        @Override
        protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
                TCreatedObjFromValueObj aObjoSetVals,
                TypedValueObject aObjToGetVals) throws ObjectCreationException
        {

        }
    }

    private static class RepoInfo
    {
        private final String repoName;
        private long version;
        private ClassRepository repo;
        private PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persMngr;
        private ApplicationHook<ApplicationObj> hook;
        private final AppModelPersistenceManager metaPersManager;

        public RepoInfo(String aRepoName, AppModelPersistenceManager aMetaPersManager)
        {
            assert (aRepoName != null);
            repoName = aRepoName;
            this.metaPersManager = aMetaPersManager;
        }

        public ClassRepository getOrCreateRepo() throws JdyPersistentException
        {
            DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
            query.setFilterExpression(DefaultOperatorEqual.createEqualExpr("applicationName", repoName, ApplicationRepository.getSingleton().getRepositoryModel()));

            ObjectList<GenericValueObjectImpl> readList = this.metaPersManager.loadObjectsFromDb(query);
            if (readList.size() > 0)
            {
                AppRepository appRepository = (AppRepository) readList.get(0);
                if (appRepository.getClosed() == null || !appRepository.getClosed())
                {
                    throw new JdyPersistentException("Repository not closed for name : " + repoName);
                }
                // create new repository when null or invalid version 
                if (this.repo == null || this.version != appRepository.getAppVersion())
                {
                    MetaRepositoryCreator repCreator = new MetaRepositoryCreator(null);
                    this.repo = repCreator.createMetaRepository(appRepository);
                    this.version = appRepository.getAppVersion();
                }
            } else
            {
                throw new JdyPersistentException("Could not find repo with name : " + repoName);
            }

            return this.repo;
        }

        private PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> getPersistentManager(DataSource dataSource) throws JdyPersistentException
        {
            if (this.persMngr == null)
            {
                HSqlSchemaHandler hsqlConnection2 = new HSqlSchemaHandler(dataSource);
                //hsqlConnection2.existsSchema(repoName);
                this.persMngr = new ValueModelPersistenceObjectManager(hsqlConnection2.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(this.repoName));
            }
            return this.persMngr;
        }

    }

    public static class GroovyAppHook extends DynamicWorkflowApplicationHook
    {

        public GroovyAppHook(AppRepository anAppRepository)
        {
            super(anAppRepository);
        }

        @Override
        protected ScritpingHook createScritpingHook(ScriptSource aClassInfo)
        {
            return new GroovyHook(aClassInfo);
        }

    }

    public static class GroovyHook extends ScritpingHook
    {
        private GroovyExecutor groovyExecutor;

        public GroovyHook(ScriptSource aClassInfo)
        {
            super(aClassInfo);
            try
            {
                this.groovyExecutor = new GroovyExecutor();
            } catch (CompilationFailedException | IOException | InstantiationException | IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void beforeSave(ApplicationObj editedObject) throws WorkflowException
        {
            super.beforeSave(editedObject);
            try
            {

                String groovyScript = getScriptSource().getBeforeSaveScript();
                if (groovyScript != null && !groovyScript.trim().isEmpty())
                {
                    this.groovyExecutor.executeGroovyScript(editedObject.getClassInfo(), editedObject, groovyScript);
                } else
                {
                    this.groovyExecutor.executeGroovyScript(editedObject.getClassInfo(), editedObject, "println 'in Groovy' ");
                }
            } catch (CompilationFailedException | InstantiationException | IllegalAccessException | IOException ex)
            {

                throw new WorkflowException(ex.getLocalizedMessage(), ex);
            }
        }

    }
}

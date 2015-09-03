package de.jdynameta.servlet;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.ApplicationRepositoryClassFileGenerator;
import de.jdynameta.persistence.cache.CachedObjectTransformator;
import de.jdynameta.persistence.cache.UpdatableObjectCreator;
import de.jdynameta.persistence.impl.proxy.SimpleProxyResolver;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.impl.BasicPersistenceObjectManagerImpl;
import de.jdynameta.persistence.manager.impl.ValueModelObjectCreatorWithProxy;

public final class MetaDataPersistenceManager extends BasicPersistenceObjectManagerImpl<ChangeableValueObject, GenericValueObjectImpl>
{
    private static final long serialVersionUID = 1L;

    public MetaDataPersistenceManager(DbAccessConnection<ChangeableValueObject, GenericValueObjectImpl> aDbConnection)
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

    private static class AppModelCreator extends ValueModelObjectCreatorWithProxy
    {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
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
}

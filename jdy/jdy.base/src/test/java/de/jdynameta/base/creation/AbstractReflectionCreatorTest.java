package de.jdynameta.base.creation;

import org.junit.Assert;
import org.junit.Test;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.test.AddressImpl;
import de.jdynameta.base.test.CustomerImpl;
import de.jdynameta.base.test.OrderItemImpl;
import de.jdynameta.base.test.PlantImpl;
import de.jdynameta.base.test.PlantOrderImpl;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.test.PlantShopRepository.Type;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.defaultimpl.ReflectionValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;
import static org.junit.Assert.assertEquals;

public class AbstractReflectionCreatorTest
{
    private final ClassRepository repository = PlantShopRepository.createPlantShopRepository();
    private final ReflectionCreator creator = new ReflectionCreator(PlantShopRepository.createNameCreator());

    @Test
    public void testCreateNewObject() throws ObjectCreationException
    {
        assertEquals("Create empty AddressImpl", AddressImpl.class, creator.createNewObjectFor(repository.getClassForName(Type.Address.name())).getClass());
        assertEquals("Create empty CustomerImpl", CustomerImpl.class, creator.createNewObjectFor(repository.getClassForName(Type.Customer.name())).getClass());
        assertEquals("Create empty OrderItemImpl", OrderItemImpl.class, creator.createNewObjectFor(repository.getClassForName(Type.OrderItem.name())).getClass());
        assertEquals("Create empty PlantImpl", PlantImpl.class, creator.createNewObjectFor(repository.getClassForName(Type.Plant.name())).getClass());
        assertEquals("Create empty PlantOrderImpl", PlantOrderImpl.class, creator.createNewObjectFor(repository.getClassForName(Type.PlantOrder.name())).getClass());

    }

    @Test
    public void testAddress() throws ObjectCreationException
    {
        TypedHashedValueObject address = new TypedHashedValueObject(repository.getClassForName(Type.Address.name()));
        address.setValue("AddressId", "143");
        address.setValue("City", "TestCity");
        address.setValue("Street", "TestStreet");
        address.setValue("ZipCode", "TestZipCode");

        ReflectionCreator creatorTmp = new ReflectionCreator(PlantShopRepository.createNameCreator());
        AddressImpl addressImpl = (AddressImpl) creatorTmp.createObjectFor(address);
        assertEquals("143", addressImpl.getAddressId());
        assertEquals("TestCity", addressImpl.getCity());
        assertEquals("TestStreet", addressImpl.getStreet());
        assertEquals("TestZipCode", addressImpl.getZipCode());

    }

    @SuppressWarnings("serial")
    public static class ReflectionCreator extends AbstractReflectionCreator<ReflectionValueObject>
    {

        public ReflectionCreator(ClassNameCreator aNameCreator)
        {
            super(aNameCreator);
        }

        @Override
        protected ReflectionValueObject createProxyObjectFor(
                TypedValueObject aObjToHandle)
        {
            return null;
        }

        @Override
        protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
                ReflectionValueObject aObjoSetVals,
                TypedValueObject aObjToGetVals) throws ObjectCreationException
        {

        }

    }
}

package de.jdynameta.base.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import de.jdynameta.base.generation.AbstractClassCodeGenerator;
import de.jdynameta.base.generation.ClassInfoCodeGenerator;
import de.jdynameta.base.generation.ClassInfoInterfaceGenerator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.DefaultInterfaceClassNameCreator;
import de.jdynameta.base.view.DbDomainValue;

public class PlantShopRepository
{
    private static final String ROOT_DIR = "src/main/java/";

    public static enum Type
    {
        Plant, OrderItem, Address, Customer, PlantOrder
    }

    public enum PlantFamily implements DbDomainValue<String>
    {
        Iridaceae("Iridaceae", "Iridaceae"), Malvaceae("Malvaceae", "Malvaceae"), Geraniaceae("Geraniaceae", "Geraniaceae"),;

        private final String domValue;
        private final String representation;

        private PlantFamily(String domValue, String representation)
        {
            this.domValue = domValue;
            this.representation = representation;
        }

        @Override
        public String getDbValue()
        {
            return domValue;
        }

        @Override
        public String getRepresentation()
        {
            return representation;
        }
    }

    public static ClassRepository createPlantShopRepository()
    {
        JdyRepositoryModel rep = new JdyRepositoryModel("PlantShop");

        JdyClassInfoModel plantType = rep.addClassInfo(Type.Plant.name());
        plantType.addTextAttr("BotanicName", 200).setIsKey(true);
        plantType.addLongAttr("HeigthInCm", 0, 5000);
        plantType.addTextAttr("PlantFamily", 100, PlantFamily.values());
        plantType.addTextAttr("Color", 100);

        JdyClassInfoModel orderItemType = rep.addClassInfo(Type.OrderItem.name());
        orderItemType.addLongAttr("ItemNr", 0, 1000).setIsKey(true);
        orderItemType.addLongAttr("ItemCount", 0, 1000).setNotNull(true);
        orderItemType.addDecimalAttr("Price", new BigDecimal("0.0000"), new BigDecimal("1000.0000"), 4).setNotNull(true);
        orderItemType.addReference("Plant", plantType).setDependent(false).setNotNull(true);

        JdyClassInfoModel addressType = rep.addClassInfo(Type.Address.name());
        addressType.addTextAttr("AddressId", 30).setIsKey(true);
        addressType.addTextAttr("Street", 30).setNotNull(true);
        addressType.addTextAttr("ZipCode", 30).setNotNull(true);
        addressType.addTextAttr("City", 30).setNotNull(true);

        JdyClassInfoModel customerType = rep.addClassInfo(Type.Customer.name());
        customerType.addTextAttr("CustomerId", 30).setIsKey(true);
        customerType.addTextAttr("FirstName", 30).setNotNull(true);
        customerType.addTextAttr("MiddleName", 30).setNotNull(false);
        customerType.addTextAttr("LastName", 30).setNotNull(true);
        customerType.addReference("PrivateAddress", addressType).setDependent(true).setNotNull(true);
        customerType.addReference("InvoiceAddress", addressType).setDependent(true).setNotNull(false);

        JdyClassInfoModel orderType = rep.addClassInfo(Type.PlantOrder.name());
        orderType.addLongAttr("OrderNr", 0, Integer.MAX_VALUE).setIsKey(true);
        orderType.addTimestampAttr("OrderDate", true, false).setNotNull(true);

        JdyRepositoryModel.addAssociation("Items", orderType, orderItemType, true, true);

        return rep;
    }

    public static DefaultClassNameCreator createNameCreator()
    {
        return new DefaultClassNameCreator()
        {
            @Override
            public String getPackageNameFor(ClassInfo aInfo)
            {
                return "de.jdynameta.base.test";
            }
        };
    }

    public static DefaultInterfaceClassNameCreator createInterfaceNameCreator()
    {
        return new DefaultInterfaceClassNameCreator()
        {
            @Override
            public String getPackageNameFor(ClassInfo aInfo)
            {
                return "de.jdynameta.base.test";
            }
        };
    }

    private static ClassInfoCodeGenerator createConcreteClassGenerator(final ClassNameCreator nameCreator, ClassNameCreator interfaceNameCreator) throws IOException
    {

        ClassInfoCodeGenerator generator = new ClassInfoCodeGenerator(nameCreator, interfaceNameCreator)
        {

            @Override
            protected void appendConstructorDeclaration()
            {
                // appen default Constructor
                this.appendLineWithTabs(1, "/**");
                this.appendLineWithTabs(1, " *Constructor ");
                this.appendLineWithTabs(1, " */");
                this.appendLineWithTabs(1, "public ", getClassName(), " ()");
                this.appendLineWithTabs(1, "{");
                this.appendLineWithTabs(2, "super();");
                this.appendLineWithTabs(1, "}");
            }

            @Override
            public void appendImportDeclarations()
            {
                super.appendImportDeclarations();
            }
        };

        return generator;
    }

    protected void generateInterfaces(ClassRepository aRepository) throws IOException
    {
        ClassNameCreator nameCreator = createInterfaceNameCreator();

        ClassInfoInterfaceGenerator generator = new ClassInfoInterfaceGenerator(nameCreator);

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            System.out.println(curInfo.getInternalName());
            ArrayList<String> superInterfaceColl = new ArrayList<>();
            if (curInfo.getSuperclass() != null)
            {
                superInterfaceColl.add(nameCreator.getAbsolutClassNameFor(curInfo.getSuperclass()));
            } else
            {
                superInterfaceColl.add("de.jdynameta.base.value.ValueObject");
            }

            generator.setInfo(curInfo);
            generator.setAllExtendedInterfaces(superInterfaceColl);
            AbstractClassCodeGenerator.writeToFile(ROOT_DIR, generator);
        }
    }

    protected static void generateSourceFiles(ClassRepository aRepository) throws IOException
    {
        final ClassNameCreator nameCreator = createNameCreator();
        ClassNameCreator interfaceNameCreator = createInterfaceNameCreator();

        ClassInfoCodeGenerator generator = createConcreteClassGenerator(nameCreator, interfaceNameCreator);

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            System.out.println(curInfo.getInternalName());
            String superClassName = (curInfo.getSuperclass() != null)
                    ? nameCreator.getAbsolutClassNameFor(curInfo.getSuperclass())
                    : "de.jdynameta.base.value.defaultimpl.ReflectionValueObject";

            generator.setExtendsClassName(superClassName);
            generator.setInfo(curInfo);
            generator.setImplementedInterfaces(new String[]
            {
                interfaceNameCreator.getAbsolutClassNameFor(curInfo)
            });
            AbstractClassCodeGenerator.writeToFile(ROOT_DIR, generator);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            new PlantShopRepository().generateInterfaces(createPlantShopRepository());
            generateSourceFiles(createPlantShopRepository());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

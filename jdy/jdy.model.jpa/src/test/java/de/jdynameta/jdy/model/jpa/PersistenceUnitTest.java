/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.jdy.model.jpa;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.model.asm.jpa.example.inheritance.CustomerSingle;
import de.jdynameta.model.asm.jpa.example.inheritance.OnlineCustomer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import static junit.framework.TestCase.fail;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * @author rschneider
 */
 public class PersistenceUnitTest {

    private static Logger logger = Logger.getLogger(PersistenceUnitTest.class.getName());

    private EntityManagerFactory emFactory;

    private EntityManager entityManager;

    private Connection connection;

    public PersistenceUnitTest() {
    }

    @Before
    public void setUp() throws Exception {
        try {
            logger.info("Startingemory HSQL database for unit tests");
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:unit-testing-jpa", "sa", "");
        } catch (Exception ex) {
            fail("Exception during HSQL database startup.");
        }
        try {
            logger.info("BuildingEntityManager for unit tests");
            emFactory = Persistence.createEntityManagerFactory("testJPA");
            entityManager = emFactory.createEntityManager();
        } catch (Exception ex) {
            fail("Exception during JPA EntityManager instanciation.");
        }
    }

    @After
    public void tearDown() throws Exception {
        logger.info("Shuting Hibernate JPA layer.");
        if (entityManager != null) {
            entityManager.close();
        }
        if (emFactory != null) {
            emFactory.close();
        }
        logger.info("Stoppingemory HSQL database.");
        try {
            connection.createStatement().execute("SHUTDOWN");
        } catch (Exception ex) {}
    }

    
        @Test
    public void testReadMetaData() {
        JpaMetamodelReader reader = new JpaMetamodelReader();
        ClassRepository repo = reader.createMetaRepository(entityManager.getMetamodel(), "TestApp");

        assertThat("String length", stringType(repo, "Teilnehmer", "name").getLength(), equalTo(60L));
        assertThat("Is Not Generated",primAttr(repo, "Teilnehmer", "name").isGenerated(), equalTo(false));
        assertThat("Is No Key", primAttr(repo, "Teilnehmer", "name").isKey(), equalTo(false));
        assertThat("Is Generated", primAttr(repo, "Teilnehmer", "id").isGenerated(), equalTo(true));
        assertThat("Is Key", primAttr(repo, "Teilnehmer", "id").isKey(), equalTo(true));
        assertThat("Dom Values", stringType(repo, "Teilnehmer", "landkreis").getDomainValues().size(), equalTo(9));
        assertThat("Min Value", longType(repo, "Teilnehmer", "plz").getMinValue(), equalTo((long)Integer.MIN_VALUE));
        assertThat("Date type", timestampType(repo, "Veranstaltung", "datum").isDatePartUsed(), equalTo(true));
        assertThat("OneToOne", refAttr(repo, "Teilnehmer", "veranstaltung").getReferencedClass().getInternalName(), equalTo("Veranstaltung"));
    
        assertThat("ManyToOne", refAttr(repo, "Customer", "invoiceaddressAddressid").getReferencedClass().getInternalName(), equalTo("Address"));

        assertThat("OneToMany", assoc(repo, "Plant", "orderitemCollection").getDetailClass().getInternalName(), equalTo("Orderitem"));

    }

    private TimeStampType timestampType(ClassRepository repo, String aClassName, String anAttributeName) {
        return (TimeStampType) primAttr(repo, aClassName, anAttributeName).getType();
    }

    private LongType longType(ClassRepository repo, String aClassName, String anAttributeName) {
        return (LongType) primAttr(repo, aClassName, anAttributeName).getType();
    }

    
    private TextType stringType(ClassRepository repo, String aClassName, String anAttributeName) {
        return (TextType) primAttr(repo, aClassName, anAttributeName).getType();
    }
    
    private PrimitiveAttributeInfo primAttr(ClassRepository repo, String aClassName, String anAttributeName) {
        return (PrimitiveAttributeInfo)attr(repo, aClassName, anAttributeName);
    }

    private ObjectReferenceAttributeInfo refAttr(ClassRepository repo, String aClassName, String anAttributeName) {
        return (ObjectReferenceAttributeInfo)attr(repo, aClassName, anAttributeName);
    }

    private AssociationInfo assoc(ClassRepository repo, String aClassName, String anAssocName) {
        return repo.getClassForName(aClassName).getAssoc(anAssocName);
    }

    
    private AttributeInfo attr(ClassRepository repo, String aClassName, String anAttributeName) {
        return repo.getClassForName(aClassName).getAttributeInfoForExternalName(anAttributeName);
    }
    
    
    @Test
    public void testMetaData() {
        
        for(EmbeddableType<?> embd: entityManager.getMetamodel().getEmbeddables()) {
            System.out.println(embd.getPersistenceType().name());
            System.out.println(embd.getPersistenceType().ordinal());
            System.out.println(embd.getJavaType().getName());
        }

         for(EntityType<?> type: entityManager.getMetamodel().getEntities()) {
            System.out.println("Persistent type: " + type.getPersistenceType().name());
            System.out.println("Java type: " + type.getJavaType().getName());
            System.out.println("Name: " + type.getName());
            System.out.println("Bindable Type: " + type.getBindableJavaType().getName());
            System.out.println("Super Type: " + ((type.getSupertype() != null) ? type.getSupertype().getJavaType() : ""));
            
            for (Attribute<?, ?> attr: type.getAttributes()) {

                System.out.println("Name: " + attr.getName());
                System.out.println("Java Member: " + attr.getJavaMember());
                System.out.println("Declaring  Type: " + attr.getDeclaringType());
                System.out.println("JavaType: " + attr.getJavaType());
                if (attr.getJavaMember() instanceof Field) {
                    for( Annotation annot: ((Field) attr.getJavaMember()).getAnnotations() ) {
                        System.out.println("Annotation: " + annot.annotationType().getName());
                    }
                }
                System.out.println("PersistentAttributeType: " + attr.getPersistentAttributeType());

                
            }
        }

    }

    @Test
    public void testPersistence() {
        try {

            entityManager.getTransaction().begin();

            //inserting Customer
            CustomerSingle customer = new CustomerSingle();
            customer.setFirstName("Antony");
            customer.setLastName("John");
            customer.setCustType("RETAIL");
            customer.getAddress().setStreet("1 Broad street");
            customer.getAddress().setAppt("111");
            customer.getAddress().setCity("NewYork");
            customer.getAddress().setZipCode("23456");
            entityManager.persist(customer);
//		inserting Online Customer
            OnlineCustomer onlineCust = new OnlineCustomer();
            onlineCust.setFirstName("Henry");
            onlineCust.setLastName("Ho");
            onlineCust.setCustType("ONLINE");
            onlineCust.getAddress().setStreet("1 Mission Street");
            onlineCust.getAddress().setAppt("222");
            onlineCust.getAddress().setCity("Seatle");
            onlineCust.getAddress().setZipCode("33345");
            onlineCust.setWebsite("www.amazon.com");
            entityManager.persist(onlineCust);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            entityManager.getTransaction().rollback();
            fail("Exception during testPersistence");
        }
    }
}
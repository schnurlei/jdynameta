package de.jdynameta.model.asm.jpa;

import de.jdynameta.model.asm.jpa.example.TestEntity;
import java.lang.annotation.Annotation;
import javax.persistence.Entity;

/**
 * Unit test for simple App.
 */
public class JarReaderTest
{

    @org.junit.Test
    public void testApp()
    {

        Class aClass = TestEntity.class;
        Annotation[] annotations = aClass.getAnnotations();

        if (aClass.isAnnotationPresent(Entity.class))
        {

            Entity entity = (Entity) aClass.getAnnotation(Entity.class);
            System.out.println("name: " + entity.name());
            System.out.println("value: " + entity.annotationType());
        }

        for (Annotation annotation : annotations)
        {

            System.out.println("name: " + annotation.toString());
            System.out.println("value: " + annotation.annotationType());
        }

    }
}

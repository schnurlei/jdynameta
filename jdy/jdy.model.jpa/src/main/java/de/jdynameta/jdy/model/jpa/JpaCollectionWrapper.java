/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.jdy.model.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type;

/**
 *
 * @author rainer
 */
public class JpaCollectionWrapper {

    private final Attribute<?, ?> attr;
    private final Field field;
    private final CollectionType type;
    private final EntityType referencedType;

    public JpaCollectionWrapper(Attribute<?, ?> anAttr) {

        
        this.attr = anAttr;
        PluralAttribute listAttr = (PluralAttribute) anAttr;
        this.type = listAttr.getCollectionType();
        
        if (anAttr.getJavaMember() instanceof Field) {
            this.field = ((Field) anAttr.getJavaMember());
        } else {
            this.field = null;
        }
        

        this.referencedType = (EntityType) listAttr.getElementType();
        
    }

    boolean isNullable() {

        Column columnInfo = (Column) getAnntotationInfo(Column.class);
        return (columnInfo != null) ? columnInfo.nullable() : false;
       
    }
 
   public <T extends Annotation> T getAnntotationInfo(Class<T> annotation)
    {

        return this.field.getAnnotation(annotation);
    }    

    Object getGeneratedInfo() {
       return getAnntotationInfo(GeneratedValue.class);
    }

    public Class getJavaType() {
        return this.attr.getJavaType();
    }

    public CollectionType getType() {
        return type;
    }

    public EntityType getReferencedType() {
        return referencedType;
    }
    
    
}

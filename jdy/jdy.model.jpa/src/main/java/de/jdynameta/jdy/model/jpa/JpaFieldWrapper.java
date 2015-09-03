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
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

/**
 *
 * @author rainer
 */
public class JpaFieldWrapper {

    private final Attribute<?, ?> attr;
    private final Field field;
    private final Type type;

    public JpaFieldWrapper(Attribute<?, ?> anAttr) {

        
        this.attr = anAttr;
        SingularAttribute singAttr = (SingularAttribute) anAttr;
        this.type = singAttr.getType();
        
        if (anAttr.getJavaMember() instanceof Field) {
            this.field = ((Field) anAttr.getJavaMember());
        } else {
            this.field = null;
        }

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

    public Type getType() {
        return type;
    }
    
    
}
